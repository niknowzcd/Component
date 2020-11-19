package com.architect.component.common.http;

public class MyOkHttpClient {

    private Dispatcher dispatcher;
    private boolean isCanceled;

    public boolean isCanceled() {
        return isCanceled;
    }

    public MyOkHttpClient(Builder builder) {
        dispatcher = builder.dispatcher;
        isCanceled = builder.isCanceled;
    }

    public final static class Builder {
        Dispatcher dispatcher;
        boolean isCanceled;

        public Builder() {
            dispatcher = new Dispatcher();
        }

        public Builder dispatcher(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        // 用户取消请求
        public Builder canceled() {
            isCanceled = true;
            return this;
        }

        public MyOkHttpClient build() {
            return new MyOkHttpClient(this);
        }
    }

    public Call newCall(Request request) {
        return new RealCall(this, request);
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }
}
