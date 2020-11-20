package com.architect.component.common.http.interceptor;

import com.architect.component.common.http.RealCall;
import com.architect.component.common.http.Request;
import com.architect.component.common.http.Response;

import java.io.IOException;
import java.util.List;

public class RealInterceptor implements Interceptor.Chain {

    private final List<Interceptor> interceptors;
    private int index;
    private final Request request;
    private final RealCall realCall;

    public RealInterceptor(List<Interceptor> interceptors, int index, Request request, RealCall realCall) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
        this.realCall = realCall;
    }

    @Override
    public Response process(Request request) throws IOException {
        if (index >= interceptors.size()) throw new IllegalArgumentException("interceptor 取值有误");

        Interceptor interceptor = interceptors.get(index);
        RealInterceptor next = new RealInterceptor(interceptors, index + 1, request, realCall);


        return interceptor.intercept(next);
    }

    public Request request() {
        return request;
    }

}
