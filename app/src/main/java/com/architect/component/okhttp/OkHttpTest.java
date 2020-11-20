package com.architect.component.okhttp;

import com.architect.component.common.http.Call;
import com.architect.component.common.http.Callback;
import com.architect.component.common.http.MyOkHttpClient;
import com.architect.component.common.http.Request;
import com.architect.component.common.http.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * httpTest
 */
public class OkHttpTest {
    //        private static String PATH_GET = "http://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2";
    private static String PATH_GET = "https://www.baidu.com";
    private static String PATG_POST = "http://restapi.amap.com/v3/weather/weatherInfo";


    public static void main(String[] args) throws Exception {
        System.out.println("请输入网址，然后回车..."); // www.baidu.com
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String inputPath = br.readLine();


        MyOkHttpClient client = new MyOkHttpClient.Builder().build();
//        RequestBody body = new RequestBody();
//        body.addBody("city", "110101");
//        body.addBody("key", "13cb58f5884f9749287abbead9c658f2");

        Request request = new Request.Builder().url(inputPath).build();
//        Request request = new Request.Builder().post(body).url(PATG_POST).build();

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
