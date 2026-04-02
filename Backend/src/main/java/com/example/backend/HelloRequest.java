package com.example.backend;

public class HelloRequest {

    private String key;

    public String getKey()
    {
        return key;
    }

    // If the request body contains {"key":"whateverValue"} (json format) Spring will automatically use
    // the library "Jackson" to call this setter accordingly (since both the JSON and Java variables
    // are named "key").
    public void setKey(String key)
    {
        this.key = key;
    }
}
