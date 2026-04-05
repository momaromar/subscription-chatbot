package com.example.backend.stripepay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Central check: configured login username must appear in the JSON "database" after Stripe payment.
 */
@Service
public class AiPaidAccessGuard {

    private final PaidAccountsJsonStore paidAccountsJsonStore;

    @Value("${AUTHORIZEDUSERNAME}")
    private String authorizedUsername;

    public AiPaidAccessGuard(PaidAccountsJsonStore paidAccountsJsonStore) {
        this.paidAccountsJsonStore = paidAccountsJsonStore;
    }

    public boolean canUseAiForAuthorizedAccount() {
        return paidAccountsJsonStore.isPaid(authorizedUsername);
    }
}
