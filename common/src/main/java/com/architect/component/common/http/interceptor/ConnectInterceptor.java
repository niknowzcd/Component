package com.architect.component.common.http.interceptor;

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

/**
 * 网络连接的拦截器
 */
public class ConnectInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        SocketRequestServer socketRequestServer = new SocketRequestServer();
        Request request = chain.request();

        Socket socket = new Socket(socketRequestServer.getHost(request), socketRequestServer.getPort(request));

        //请求 output
        OutputStream os = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os));
        String requestHeaders = socketRequestServer.getRequestHeaders(request);
        System.out.println("请求头信息 >> " + requestHeaders);
        bufferedWriter.write(requestHeaders);
        bufferedWriter.flush();


        //返回 input
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                String readerLine = null;
//                while (true) {
//                    try {
//                        if ((readerLine = bufferedReader.readLine()) != null) {
//                            // Log.d(TAG, "服务器响应的:" + readerLine);
//                            System.out.println("服务器响应的:" + readerLine);
//                        } else {
//                            return;
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }.start();

        Response response = new Response();
        //取出第一行的返回信息，其中包含响应码 HTTP/1.1 200 OK
        String firstReadLine = bufferedReader.readLine();
        String[] strings = firstReadLine.split(" ");
        response.setCode(Integer.parseInt(strings[1]));

        //响应的内容体是在空行之下的
        String readLine;
        try {
            while ((readLine = bufferedReader.readLine()) != null) {
                if ("".equals(readLine)) {
                    //读取到空行了，下一行就是我们需要的内容主体
                    response.setBody(bufferedReader.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
