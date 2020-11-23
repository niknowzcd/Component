package com.architect.component.common.http.interceptor;

import com.architect.component.common.http.Request;
import com.architect.component.common.http.RequestBody;
import com.architect.component.common.http.Response;
import com.architect.component.common.http.SocketRequestServer;

import java.io.IOException;
import java.util.Map;

/**
 * 请求头相关的拦截器
 */
public class RequestHeaderInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Map<String, String> headers = request.getHeaders();

        headers.put("Host", new SocketRequestServer().getHost(request));

        if ("POST".equalsIgnoreCase(request.getReuqestMethod())) {
            headers.put("Content-Length", request.getRequestBody().getBody().length() + "");
            headers.put("Content-Type", RequestBody.TYPE);
        }
        headers.put("Connection", "Keep-Alive");

        return chain.process(request);
    }
}
