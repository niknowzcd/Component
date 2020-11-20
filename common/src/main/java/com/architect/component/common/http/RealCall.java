package com.architect.component.common.http;

import com.architect.component.common.http.interceptor.ConnectInterceptor;
import com.architect.component.common.http.interceptor.Interceptor;
import com.architect.component.common.http.interceptor.RealInterceptor;
import com.architect.component.common.http.interceptor.RequestHeaderInterceptor;
import com.architect.component.common.http.interceptor.RetryAndFollowUpInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络请求的具体执行类
 */
public class RealCall implements Call {

    private MyOkHttpClient client;
    private Request request;
    private boolean executed;

    public RealCall(MyOkHttpClient client, Request request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public void enqueue(Callback callback) {
        synchronized (this) {
            if (executed) {
                executed = true;
                throw new IllegalStateException("enqueue Already Executed");
            }
        }

        client.dispatcher().enqueue(new AsyncCall(callback));
    }

    /**
     * 这里用final修饰，表示不希望类被继承和修改
     * 至于为什么不用private? 因为这个内部类需要被其他类调用
     */
    final class AsyncCall implements Runnable {
        private Callback callback;

        public AsyncCall(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            boolean signalledCallback = false;
            try {
                Response response = getResponseWithInterceptorChain();
                if (client.isCanceled()) {
                    signalledCallback = true;
                    callback.onFailure(RealCall.this, new IOException("用户取消了 Canceled"));
                } else {
                    signalledCallback = true;
                    callback.onResponse(RealCall.this, response);
                }
            } catch (Exception e) {
                if (signalledCallback) {
                    System.out.println("执行到这段逻辑，说明代码已经回调给用户，是用户操作的时候报的错");
                } else {
                    callback.onFailure(RealCall.this, new IOException("netWork error >> " + e.toString()));
                }
            } finally {
                client.dispatcher().finish(this);
            }
        }


        private Response getResponseWithInterceptorChain() throws IOException {
            //这一行是自定义的interceptor
            List<Interceptor> interceptors = new ArrayList<>(client.interceptors());
            interceptors.add(new RetryAndFollowUpInterceptor());
            interceptors.add(new RequestHeaderInterceptor());
            interceptors.add(new ConnectInterceptor());

            RealInterceptor chain = new RealInterceptor(interceptors, 0, request, RealCall.this);
            return chain.process(request);
        }
    }
}
