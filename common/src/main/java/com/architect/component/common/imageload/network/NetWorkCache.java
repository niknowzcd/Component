package com.architect.component.common.imageload.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.architect.component.common.imageload.cache.BitmapWrap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 网络缓存，第四级缓存
 */
public class NetWorkCache implements Runnable {

    private LoadResponseListener listener;
    private String path;

    public BitmapWrap loadBitmap(String path, LoadResponseListener listener, Context context) {
        this.path = path;
        this.listener = listener;

        Uri uri = Uri.parse(path);
        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
            new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>()).execute(this);
        }

        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(path);
            URLConnection connection = url.openConnection();
            httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setConnectTimeout(5000);
            final int responseCode = httpURLConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inputStream = httpURLConnection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        BitmapWrap bitmapWrap = BitmapWrap.getInstance();
                        bitmapWrap.setBitmap(bitmap);

                        listener.loadSuccess(bitmapWrap);
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listener.loadError(new IllegalStateException("请求失败 请求码:" + responseCode));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (httpURLConnection != null) httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
