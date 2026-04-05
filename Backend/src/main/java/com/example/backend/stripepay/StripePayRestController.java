package com.example.backend.stripepay;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Stripe Checkout + webhook + helpers for the one-time paywall.
 * <p>
 * Local webhook testing: {@code stripe listen --forward-to localhost:8080/api/stripepay/webhook}
 */
@RestController
@CrossOrigin(originPatterns = "http://*:3000")
@RequestMapping("/api/stripepay")
public class StripePayRestController {

    private final PaidAccountsJsonStore paidAccountsJsonStore;
    private final StripePayTokenGate tokenGate;

    @Value("${AUTHORIZEDUSERNAME}")
    private String authorizedUsername;

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String stripeWebhookSecret;

    @Value("${stripe.checkout.success-url}")
    private String checkoutSuccessUrl;

    @Value("${stripe.checkout.cancel-url}")
    private String checkoutCancelUrl;

    @Value("${stripe.product.display-name}")
    private String productDisplayName;

    @Value("${stripe.product.amount-cents}")
    private long productAmountCents;

    @Value("${stripe.product.currency}")
    private String productCurrency;

    public StripePayRestController(PaidAccountsJsonStore paidAccountsJsonStore, StripePayTokenGate tokenGate) {
        this.paidAccountsJsonStore = paidAccountsJsonStore;
        this.tokenGate = tokenGate;
    }

    @GetMapping("/access-status")
    public ResponseEntity<Map<String, Object>> accessStatus(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!tokenGate.isAuthorizedBearer(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean paid = paidAccountsJsonStore.isPaid(authorizedUsername);
        Map<String, Object> body = new HashMap<>();
        body.put("paid", paid);
        body.put("username", authorizedUsername);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/checkout-session")
    public ResponseEntity<?> createCheckoutSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!tokenGate.isAuthorizedBearer(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(checkoutSuccessUrl)
                    .setCancelUrl(checkoutCancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(productCurrency.toLowerCase())
                                                    .setUnitAmount(productAmountCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(productDisplayName)
                                                                    .build())
                                                    .build())
                                    .build())
                    .putMetadata("username", authorizedUsername)
                    .build();

            Session session = Session.create(params);
            Map<String, String> body = new HashMap<>();
            body.put("url", session.getUrl());
            return ResponseEntity.ok(body);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Stripe error: " + e.getMessage());
        }
    }

    /**
     * Stripe sends raw JSON; signature verification requires the exact request body bytes.
     */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        if (sigHeader == null || sigHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing Stripe-Signature");
        }
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if (!"checkout.session.completed".equals(event.getType())) {
            return ResponseEntity.ok("ignored");
        }

        Optional<StripeObject> obj = event.getDataObjectDeserializer().getObject();
        if (obj.isEmpty() || !(obj.get() instanceof Session session)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unexpected event payload");
        }

        try {
            maybeRecordCheckoutSession(session);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Persist failed");
        }

        return ResponseEntity.ok("ok");
    }

    /**
     * Browser return-path helper when webhooks are not forwarded (common in local dev).
     */
    @PostMapping("/verify-session")
    public ResponseEntity<?> verifySession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> body) {
        if (!tokenGate.isAuthorizedBearer(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        String sessionId = body == null ? null : body.get("sessionId");
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity.badRequest().body("sessionId required");
        }

        try {
            Session session = Session.retrieve(sessionId);
            if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Checkout not paid yet");
            }
            maybeRecordCheckoutSession(session);
            Map<String, Object> ok = new HashMap<>();
            ok.put("paid", true);
            ok.put("username", authorizedUsername);
            return ResponseEntity.ok(ok);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Stripe error: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Persist failed");
        }
    }

    private void maybeRecordCheckoutSession(Session session) throws IOException {
        Map<String, String> meta = session.getMetadata();
        String user = meta != null ? meta.get("username") : null;
        if (user == null || !user.equals(authorizedUsername)) {
            return;
        }
        paidAccountsJsonStore.recordPaidUsername(user, session.getId());
    }
}
