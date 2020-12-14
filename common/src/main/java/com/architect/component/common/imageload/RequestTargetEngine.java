package com.architect.component.common.imageload;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.architect.component.common.imageload.cache.ActiveCache;
import com.architect.component.common.imageload.cache.BitmapKey;
import com.architect.component.common.imageload.cache.BitmapWrap;
import com.architect.component.common.imageload.cache.MemoryCache;
import com.architect.component.common.imageload.disk.DiskLruCacheWrap;
import com.architect.component.common.imageload.lifecycle.BitmapLifecycleCallback;
import com.architect.component.common.imageload.network.LoadResponseListener;
import com.architect.component.common.imageload.network.NetWorkCache;
import com.architect.component.common.utils.LogUtils;

/**
 * 核心的处理类
 */
public class RequestTargetEngine implements BitmapLifecycleCallback, LoadResponseListener {

    private ActiveCache activeCache;
    private MemoryCache memoryCache;
    private DiskLruCacheWrap diskLruCacheWrap;
    private static final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;

    public RequestTargetEngine() {
        if (activeCache == null) {
            activeCache = new ActiveCache();
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
        }
        diskLruCacheWrap = new DiskLruCacheWrap();
    }

    private String path;
    private Context context;
    private String key;
    private ImageView imageView;

    public void loadBitmap(String path, Context context) {
        this.path = path;
        this.context = context;
        this.key = new BitmapKey(path).getKey();
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;
        BitmapWrap bitmapWrap = getBitmapWrap();
        if (bitmapWrap != null) {
            imageView.setImageBitmap(bitmapWrap.getBitmap());
        }
    }

    /**
     * 1.先从activeCache中找，找到的话直接返回
     * 2.接着从memoryCache,找到的话，将其赋值给activeCache，然后返回
     * 3.从diskLruCacheWrap，找到的话，将其赋值给activeCache，然后返回
     * 4.网络加载
     */
    public BitmapWrap getBitmapWrap() {
        BitmapWrap bitmapWrap = activeCache.get(key);
        if (bitmapWrap != null) {
            return bitmapWrap;
        }

        bitmapWrap = memoryCache.get(key);
        if (bitmapWrap != null) {
            memoryCache.manualRemove(key);
            activeCache.put(key, bitmapWrap);

            return bitmapWrap;
        }

        bitmapWrap = diskLruCacheWrap.get(key);
        if (bitmapWrap != null) {
            activeCache.put(key, bitmapWrap);
            return bitmapWrap;
        }

        new NetWorkCache().loadBitmap(path, this, context);
        return null;
    }

    @Override
    public void glideInit() {
        LogUtils.d("RequestTargetEngine glideInit");
    }

    @Override
    public void glideStop() {
        LogUtils.d("RequestTargetEngine glideStop");
    }

    @Override
    public void glideRecycle() {
        LogUtils.d("RequestTargetEngine glideRecycle");
    }

    @Override
    public void loadSuccess(BitmapWrap bitmapWrap) {
        if (bitmapWrap != null) {
            bitmapWrap.setKey(key);
            diskLruCacheWrap.put(key, bitmapWrap);

            imageView.setImageBitmap(bitmapWrap.getBitmap());
        }
    }

    @Override
    public void loadError(Exception e) {
        LogUtils.d("RequestTargetEngine responseException: 加载外部资源失败 e:" + e.getMessage());
    }
}
