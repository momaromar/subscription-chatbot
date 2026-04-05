package com.example.backend.stripepay;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes Stripe global API key from configuration.
 */
@Configuration
public class StripePayBootstrap {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @PostConstruct
    void initStripeApiKey() {
        Stripe.apiKey = stripeSecretKey;
    }
}
