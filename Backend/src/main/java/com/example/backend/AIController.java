package com.example.backend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class AIController {

    private final LLMService llmService;
    public AIController(LLMService llmService) { this.llmService = llmService; }
    // ^ This is equivalent to @Autowire, but @Autowire is apparently old and outdated

    @PostMapping("/ai")
    public String ai(@RequestBody PostRequest request)
    {
        return llmService.callLLM(request.getPostBody());
    }
}
