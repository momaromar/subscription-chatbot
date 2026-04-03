package com.example.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;


@Service
public class LLMService {

    // This pulls API_KEY from app-secrets.properties.
    // If you are cloning the repo, make sure you check
    // the readme and download the secrets file!
    @Value("${API_KEY}")
    private String apiKey;


    // callLLM() is a prewritten template function for calling openrouter API Endpoints
    public String callLLM(String userInput) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://openrouter.ai/api/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", "meta-llama/llama-3-8b-instruct",
                "messages", new Object[] {
                        Map.of("role", "user", "content", userInput)
                }
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        var choices = (java.util.List<Map>) response.getBody().get("choices");
        var message = (Map) choices.get(0).get("message");

        return message.get("content").toString();
    }
}
