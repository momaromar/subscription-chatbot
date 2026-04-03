package com.example.backend;

public class PostRequest {

    private String postBody;

    public String getPostBody()
    {
        return postBody;
    }

    // If the request body contains {"postBody":"whateverValue"} (json format) Spring will automatically use
    // the library "Jackson" to call this setter accordingly (since both the JSON and Java variables
    // are named "postBody").
    public void setPostBody(String postBody)
    {
        this.postBody = postBody;
    }
}
