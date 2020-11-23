package com.architect.component.okhttp;

import com.architect.component.common.http.Call;
import com.architect.component.common.http.Callback;
import com.architect.component.common.http.MyOkHttpClient;
import com.architect.component.common.http.Request;
import com.architect.component.common.http.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.OkHttpClient;

/**
 * httpTest
 */
public class OkHttpTest {
    private static String PATH_GET = "http://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=13cb58f5884f9749287abbead9c658f2";
    //    private static String PATH_GET = "https://www.baidu.com";
    private static String PATG_POST = "http://restapi.amap.com/v3/weather/weatherInfo";

    public static void main(String[] args) throws Exception {
//        singleRequest();

        httpPoolTest();

        OkHttpClient client=new OkHttpClient.Builder().build();
        okhttp3.Request request=new okhttp3.Request.Builder().build();

        okhttp3.Call call=client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

            }
        });
    }

    public static MyOkHttpClient client = new MyOkHttpClient.Builder().build();
    public static Request request = new Request.Builder().url(PATH_GET).build();

    public static void httpPoolTest() {
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


    private static void singleRequest() throws Exception {
        System.out.println("请输入网址，然后回车..."); // www.baidu.com
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String inputPath = br.readLine();


        MyOkHttpClient client = new MyOkHttpClient.Builder().build();
//        RequestBody body = new RequestBody();
//        body.addBody("city", "110101");
//        body.addBody("key", "13cb58f5884f9749287abbead9c658f2");

        Request request = new Request.Builder().url(PATH_GET).build();
//        Request request = new Request.Builder().url(inputPath).build();
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
