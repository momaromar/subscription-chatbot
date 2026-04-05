package com.example.backend.stripepay;

import java.util.ArrayList;
import java.util.List;

/**
 * Root JSON shape for the on-disk "database" file.
 */
public class PaidAccountsDocument {

    private List<PaidAccountEntry> accounts = new ArrayList<>();

    public List<PaidAccountEntry> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<PaidAccountEntry> accounts) {
        this.accounts = accounts != null ? accounts : new ArrayList<>();
    }
}
