package com.architect.component.common.http.pool;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

public class HttpConnection {

    private Socket socket;
    public long hastUseTime;    //最后的连接时间

    //连接对象最后的使用时间
    private long hasUserTime;

    public HttpConnection(String protocol, String host, int port) {
        try {
            if ("https".equalsIgnoreCase(protocol)) {
                socket = SSLSocketFactory.getDefault().createSocket(host, port);
            } else {
                socket = new Socket(host, port);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("HttpConnection 初始化失败 ....");
        }
    }

    public boolean isConnection(String host, int port) {
        if (socket == null) {
            return false;
        }

        return socket.getPort() == port && socket.getInetAddress().getHostName().equalsIgnoreCase(host);
    }


    public void closeSocket() {
        System.out.println("closeSocket");
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("closeSocket error >> " + e.getMessage());
            }
        }
    }

    public Socket socket() {
        return socket;
    }
}
