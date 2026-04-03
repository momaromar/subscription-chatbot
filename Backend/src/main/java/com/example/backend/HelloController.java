package com.example.backend;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
// ^ This annotation is useful since it basically helps set CORS

// @CrossOrigin(originPatterns = "http://*:3000") // ALLOWS REQUESTS FROM ANYWHERE! (USE FOR DEMONSTRATIONS!!!!)
@CrossOrigin(origins = "http://localhost:3000") // ts currently only allows requests from local
@RestController
public class HelloController {

    // @PostMapping annotation means this is expecting a post request
    @PostMapping("/hello")
    // I'm gonna be sending in my request in JSON format (Spring is naturally good with handling that).
    // Spring uses a library called Jackson to match the JSON variable sent in the body of my request
    // with the Java variable in PostRequest.java. Then it'll appropriately use the setter to set the
    // Java variable to the request's JSON variable. THAT WAY we can use the getter (as shown here) to
    // check the request's variable value.
    public String hello(@RequestBody PostRequest request)
    {
        if ("secret123".equals(request.getPostBody()))
        {
            return "Hello World";
        }
        else
        {
            return "Unauthorized";
        }
    }
}
