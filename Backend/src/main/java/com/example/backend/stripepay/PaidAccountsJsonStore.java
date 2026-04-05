package com.example.backend.stripepay;

import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

/**
 * Simple JSON file persistence for paid usernames (no SQL DB in this project).
 */
@Component
public class PaidAccountsJsonStore {

    private final ObjectMapper objectMapper;
    private final Path filePath;
    private final Object fileLock = new Object();

    public PaidAccountsJsonStore(
            ObjectMapper objectMapper,
            @Value("${paid.accounts.storage-path}") String storagePath) {
        this.objectMapper = objectMapper;
        Path p = Paths.get(storagePath.trim());
        this.filePath = p.isAbsolute() ? p : Paths.get(System.getProperty("user.dir")).resolve(p);
    }

    public boolean isPaid(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        synchronized (fileLock) {
            PaidAccountsDocument doc = readDocument();
            return doc.getAccounts().stream().anyMatch(a -> username.equals(a.getUsername()));
        }
    }

    /**
     * Idempotent: if username already recorded, no-op.
     */
    public void recordPaidUsername(String username, String stripeCheckoutSessionId) throws IOException {
        if (username == null || username.isBlank()) {
            return;
        }
        synchronized (fileLock) {
            PaidAccountsDocument doc = readDocument();
            List<PaidAccountEntry> list = doc.getAccounts();
            boolean exists = list.stream().anyMatch(a -> username.equals(a.getUsername()));
            if (exists) {
                return;
            }
            list.add(new PaidAccountEntry(
                    username,
                    stripeCheckoutSessionId,
                    Instant.now().toString()));
            writeDocument(doc);
        }
    }

    private PaidAccountsDocument readDocument() {
        try {
            if (!Files.exists(filePath)) {
                Path parent = filePath.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                PaidAccountsDocument empty = new PaidAccountsDocument();
                writeDocument(empty);
                return empty;
            }
            return objectMapper.readValue(filePath.toFile(), PaidAccountsDocument.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read paid accounts file: " + filePath, e);
        }
    }

    private void writeDocument(PaidAccountsDocument doc) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), doc);
    }
}
