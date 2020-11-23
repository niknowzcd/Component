package com.architect.component.common.http.interceptor;

import com.architect.component.common.http.Request;
import com.architect.component.common.http.Response;

import java.io.IOException;

/**
 * 重试与重定向的拦截器
 */
public class RetryAndFollowUpInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//        System.out.println("我是重试与重定向的拦截器");

        return chain.process(chain.request());
    }
}
