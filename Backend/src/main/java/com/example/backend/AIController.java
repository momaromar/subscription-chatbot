package com.example.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AIController {
    @Value("${AUTHORIZATIONTOKEN}")
    private String authorizationToken;

    private final LLMService llmService;
    public AIController(LLMService llmService) { this.llmService = llmService; }
    // ^ This is equivalent to @Autowire, but @Autowire is apparently old and outdated

    @PostMapping("/ai")
    public String ai(@RequestBody PostRequest request, @RequestHeader(value = "Authorization", required = false) String authHeader)
    {
        if (authHeader != null && authHeader.startsWith("Bearer "))
        { // Instead of just removing "Bearer" from the header when it's sent,
          // it's apparently best practice to include it and then later remove it in the header checks in backend...
          // SILLY PRACTICE, but it's industry standard.
            authHeader = authHeader.split(" ")[1]; // This line removes it...
            if (authHeader.equals(authorizationToken))
            {
                return llmService.callLLM(request.getPostBody());
            }
        }
        return ("Unauthorized!");

    }

}
