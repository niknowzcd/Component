package com.architect.component.common.http;

public class Response {
    private String body;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String string() {
        return body;
    }
}
