package com.architect.component.common.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class SocketRequestServer {

    //空格，请求头拼接的时候需要用到
    private final String SPACE = " ";
    private final String HTTP_VERSION = "HTTP/1.1";
    private final String LINE_BREAK = "\r\n";

    public String getHost(Request request) {
        try {
            URL url = new URL(request.getUrl());
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getPort(Request request) {
        try {
            URL url = new URL(request.getUrl());
            int port = url.getPort();
            return port == -1 ? url.getDefaultPort() : port;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取请求头的所有信息
     *
     * @return
     */
    public String getRequestHeaders(Request request) {
        URL url = null;
        try {
            url = new URL(request.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) return null;

        String urlContent = url.getFile();

        //请求行 GET xxxxx HTTP/1.1\r\n
        StringBuffer buffer = new StringBuffer();
        buffer.append(request.getReuqestMethod())
                .append(SPACE)
                .append(urlContent)
                .append(SPACE)
                .append(HTTP_VERSION)
                .append(LINE_BREAK);

        /**
         *请求集
         *  Content-Length: 48\r\n
         *  Host: restapi.amap.com\r\n
         *  Content-Type: application/x-www-form-urlencoded
         */

        if (!request.getHeaders().isEmpty()) {
            Map<String, String> headers = request.getHeaders();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                buffer.append(entry.getKey())
                        .append(":").append(SPACE)
                        .append(entry.getValue())
                        .append(LINE_BREAK);
            }
            // 拼接空行，代表下面的POST，请求体了
            buffer.append(LINE_BREAK);
        }

        if ("POST".equalsIgnoreCase(request.getReuqestMethod())) {
            buffer.append(request.getRequestBody().getBody());
        }

        return buffer.toString();
    }
}
