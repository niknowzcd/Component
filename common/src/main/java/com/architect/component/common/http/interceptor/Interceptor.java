package com.architect.component.common.http.interceptor;

import com.architect.component.common.http.Request;
import com.architect.component.common.http.Response;

import java.io.IOException;

public interface Interceptor {

    Response intercept(Chain chain) throws IOException;

    interface Chain {
        Request request();

        Response process(Request request) throws IOException;
    }
}
