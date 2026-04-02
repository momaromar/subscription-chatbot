package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

// curl -X POST http://localhost:8080/hello -H "Content-Type: application/json" -d "{\"key\": \"secret123\"}"
// ^ Left here for ease of access when demonstrating
}