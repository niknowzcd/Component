package com.architect.component.okhttp;

import com.architect.component.common.http.Call;
import com.architect.component.common.http.Callback;
import com.architect.component.common.http.MyOkHttpClient;
import com.architect.component.common.http.Request;
import com.architect.component.common.http.Response;

import java.io.IOException;

/**
 * httpTest
 */
public class OkHttpTest {


    public static void main(String[] args) {
        String PATH = "http://www.baidu.com";

        MyOkHttpClient client = new MyOkHttpClient.Builder().build();
        Request request = new Request.Builder().url(PATH).build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("OkHttpTest 失败 error >> " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("OkHttpTest 成功 >> " + response.string());
            }
        });
    }
}
