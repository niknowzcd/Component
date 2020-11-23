package com.architect.component.common.http.interceptor;

import com.architect.component.api.core.EmptyUtils;
import com.architect.component.common.http.Request;
import com.architect.component.common.http.Response;
import com.architect.component.common.http.SocketRequestServer;
import com.architect.component.common.http.pool.ConnectionPool;
import com.architect.component.common.http.pool.HttpConnection;

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

    private ConnectionPool connectionPool;

    public ConnectInterceptor(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SocketRequestServer socketRequestServer = new SocketRequestServer();
        Request request = chain.request();

        String protocol = socketRequestServer.getProtocol(request);
        if (EmptyUtils.isEmpty(protocol)) throw new IllegalAccessError("请求url请以http或者https开头");
        String host = socketRequestServer.getHost(request);
        int port = socketRequestServer.getPort(request);

        HttpConnection httpConnection = connectionPool.getConnection(host, port);
        if (httpConnection == null) {
            httpConnection = new HttpConnection(protocol, host, port);
            httpConnection.hastUseTime = System.currentTimeMillis();
            connectionPool.putConnection(httpConnection);
        }
        //todo 复用socket之后 无法正常发送数据，留待解决。
        Socket socket = httpConnection.socket();
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
//                    System.out.println("响应内容 >> " + readLine);
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
