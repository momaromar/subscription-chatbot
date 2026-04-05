package com.example.backend.stripepay;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * One row in {@code paid-accounts.json} — who paid and when (Stripe checkout id for audit).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaidAccountEntry {

    private String username;
    private String stripeCheckoutSessionId;
    private String paidAt;

    public PaidAccountEntry() {
    }

    public PaidAccountEntry(String username, String stripeCheckoutSessionId, String paidAt) {
        this.username = username;
        this.stripeCheckoutSessionId = stripeCheckoutSessionId;
        this.paidAt = paidAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStripeCheckoutSessionId() {
        return stripeCheckoutSessionId;
    }

    public void setStripeCheckoutSessionId(String stripeCheckoutSessionId) {
        this.stripeCheckoutSessionId = stripeCheckoutSessionId;
    }

    public String getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(String paidAt) {
        this.paidAt = paidAt;
    }
}
