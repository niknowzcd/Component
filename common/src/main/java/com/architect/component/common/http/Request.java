package com.architect.component.common.http;

import java.util.HashMap;
import java.util.Map;

public class Request {

    public static final String GET = "GET";
    public static final String POST = "POST";

    private String url;
    private String reuqestMethod = GET;
    private Map<String, String> headers = new HashMap<>();
    private RequestBody requestBody;

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Request(Builder builder) {
        this.url = builder.url;
        this.reuqestMethod = builder.requestMethod;
        this.headers.putAll(builder.headers);
        this.requestBody = builder.requestBody;
    }

    public String getUrl() {
        return url;
    }

    public String getReuqestMethod() {
        return reuqestMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public final static class Builder {
        private String url;
        private String requestMethod = GET;
        private Map<String, String> headers = new HashMap<>();
        private RequestBody requestBody;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder get() {
            requestMethod = GET;
            return this;
        }

        public Builder post(RequestBody requestBody) {
            requestMethod = POST;
            this.requestBody = requestBody;
            return this;
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }

}
