package com.example.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; // ResponseEntity cameo

import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "http://*:3000")
public class LoginController {

    @Value("${AUTHORIZEDUSERNAME}")
    private String authorizedUsername;
    @Value("${AUTHORIZEDPASSWORD}")
    private String authorizedPassword;
    @Value("${AUTHORIZATIONTOKEN}")
    private String authorizationToken;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body)
    {
        String username = body.get("username");
        String password = body.get("password");

        if (authorizedUsername.equals(username) && authorizedPassword.equals(password)) {
            // JSON body so the frontend can show who is paying / gating (Stripe pay module).
            Map<String, String> json = Map.of(
                    "token", authorizationToken,
                    "username", username);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json);
        }

        return ResponseEntity.status(401).body("Incorrect credentials!!");
    }

}
