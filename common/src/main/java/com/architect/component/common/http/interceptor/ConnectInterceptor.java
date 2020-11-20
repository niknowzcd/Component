package com.architect.component.common.http.interceptor;

import com.architect.component.api.core.EmptyUtils;
import com.architect.component.common.http.Request;
import com.architect.component.common.http.Response;
import com.architect.component.common.http.SocketRequestServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

/**
 * 网络连接的拦截器
 */
public class ConnectInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        SocketRequestServer socketRequestServer = new SocketRequestServer();
        Request request = chain.request();

        String protocol = socketRequestServer.getProtocol(request);
        if (EmptyUtils.isEmpty(protocol)) throw new IllegalAccessError("请求url请以http或者https开头");

        Socket socket = new Socket(socketRequestServer.getHost(request), socketRequestServer.getPort(request));
        if ("Https".equalsIgnoreCase(protocol)) {
            socket = SSLSocketFactory.getDefault().createSocket(socketRequestServer.getHost(request), socketRequestServer.getPort(request));
        }

        //请求 output
        OutputStream os = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
        String requestHeaders = socketRequestServer.getRequestHeaders(request);
        System.out.println("请求头信息 >> " + requestHeaders);
        bufferedWriter.write(requestHeaders);
        bufferedWriter.flush();

        //返回 input
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Response response = new Response();
        //取出第一行的返回信息，其中包含响应码 HTTP/1.1 200 OK
        String firstReadLine = bufferedReader.readLine();
        String[] strings = firstReadLine.split(" ");
        response.setCode(Integer.parseInt(strings[1]));
        response.setBody(getResponseBody(bufferedReader));

        return response;
    }

    /**
     * 读取到空行了，下一行就是我们需要的内容主体
     * todo 返回数据过多时, 用StringBuilder存不下来
     *
     * @return
     */
    private String getResponseBody(BufferedReader bufferedReader) {
        StringBuilder bodyBuffer = new StringBuilder();
        boolean startRead = false;
        String readLine;
        try {
            while ((readLine = bufferedReader.readLine()) != null) {
                if (startRead) {
                    System.out.println("响应内容 >> " + readLine);
                    bodyBuffer.append(readLine);
                }

                if ("".equals(readLine)) {
                    startRead = true;
                }
            }
            return bodyBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
