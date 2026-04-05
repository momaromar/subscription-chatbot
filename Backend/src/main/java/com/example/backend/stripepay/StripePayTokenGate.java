package com.example.backend.stripepay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Reuses the same bearer token contract as the original {@code /login} flow.
 */
@Component
public class StripePayTokenGate {

    @Value("${AUTHORIZATIONTOKEN}")
    private String authorizationToken;

    public boolean isAuthorizedBearer(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        return authorizationToken.equals(token);
    }
}
