package com.architect.component.common.http;

import com.architect.component.common.http.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class MyOkHttpClient {

    private Dispatcher dispatcher;
    private boolean isCanceled;
    private List<Interceptor> interceptors;

    public List<Interceptor> interceptors() {
        return interceptors;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public MyOkHttpClient(Builder builder) {
        dispatcher = builder.dispatcher;
        isCanceled = builder.isCanceled;
        interceptors = builder.interceptors;
    }

    public final static class Builder {
        Dispatcher dispatcher;
        boolean isCanceled;
        List<Interceptor> interceptors;

        public Builder() {
            dispatcher = new Dispatcher();
            interceptors = new ArrayList<>();
        }

        public Builder addIntercepor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
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
