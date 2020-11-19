package com.architect.component.common.http;

public class Response {
    private String body;

    public void setBody(String body) {
        this.body = body;
    }

    public String string() {
        return body;
    }
}
