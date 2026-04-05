package com.example.backend;

import com.example.backend.stripepay.AiPaidAccessGuard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AIController {
    @Value("${AUTHORIZATIONTOKEN}")
    private String authorizationToken;

    private final LLMService llmService;

    // --- Stripe paywall (see com.example.backend.stripepay) ---
    private final AiPaidAccessGuard aiPaidAccessGuard;

    public AIController(LLMService llmService, AiPaidAccessGuard aiPaidAccessGuard) {
        this.llmService = llmService;
        this.aiPaidAccessGuard = aiPaidAccessGuard;
    }
    // ^ This is equivalent to @Autowire, but @Autowire is apparently old and outdated

    @PostMapping("/ai")
    public ResponseEntity<String> ai(
            @RequestBody PostRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) { // Instead of just removing "Bearer" from the header when it's sent,
            // it's apparently best practice to include it and then later remove it in the header checks in backend...
            // SILLY PRACTICE, but it's industry standard.
            authHeader = authHeader.split(" ")[1]; // This line removes it...

            if (authHeader.equals(authorizationToken)) {
                if (!aiPaidAccessGuard.canUseAiForAuthorizedAccount()) {
                    return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(
                            "Payment required. Complete the one-time purchase to use the chatbot.");
                }
                return ResponseEntity.ok(llmService.callLLM(request.getPostBody()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized!");
    }

}
