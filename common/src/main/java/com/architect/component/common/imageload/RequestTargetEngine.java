package com.architect.component.common.imageload;

import android.content.Context;
import android.widget.ImageView;

import com.architect.component.common.imageload.cache.ActiveCache;
import com.architect.component.common.imageload.cache.BitmapKey;
import com.architect.component.common.imageload.cache.BitmapWrap;
import com.architect.component.common.imageload.cache.BitmapWrapCallback;
import com.architect.component.common.imageload.cache.MemoryCache;
import com.architect.component.common.imageload.disk.DiskLruCacheWrap;
import com.architect.component.common.imageload.lifecycle.BitmapLifecycleCallback;
import com.architect.component.common.imageload.network.LoadResponseListener;
import com.architect.component.common.imageload.network.NetWorkCache;
import com.architect.component.common.imageload.pool.BitmapMemoryPool;
import com.architect.component.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 核心的处理类
 */
public class RequestTargetEngine implements BitmapLifecycleCallback, LoadResponseListener, BitmapWrapCallback {

    private HashMap<String, List<BitmapWrap>> bitmapByContext = new HashMap<>();

    private ActiveCache activeCache;
    private MemoryCache memoryCache;
    private DiskLruCacheWrap diskLruCacheWrap;
    private BitmapMemoryPool bitmapMemoryPool;
    private static final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;

    public RequestTargetEngine() {
        if (activeCache == null) {
            activeCache = new ActiveCache(this);
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
        }

        if (bitmapMemoryPool == null) {
            bitmapMemoryPool = new BitmapMemoryPool(MEMORY_MAX_SIZE);
        }
        diskLruCacheWrap = new DiskLruCacheWrap();
    }

    private String path;
    private Context requestContext;
    private String key;
    private ImageView imageView;

    public void loadBitmap(String path, Context context) {
        this.path = path;
        this.requestContext = context;
        this.key = new BitmapKey(path).getKey();
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;
        BitmapWrap bitmapWrap = getBitmapWrap();
        if (bitmapWrap != null) {
            imageView.setImageBitmap(bitmapWrap.getBitmap());
            cacheBitmapByContext(bitmapWrap);
        }
    }

    private void cacheBitmapByContext(BitmapWrap bitmapWrap) {
        if (bitmapByContext.containsKey(requestContext.getClass().getSimpleName())) {
            List<BitmapWrap> list = bitmapByContext.get(requestContext.getClass().getSimpleName());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(bitmapWrap);
        } else {
            List<BitmapWrap> list = new ArrayList<>();
            list.add(bitmapWrap);
            bitmapByContext.put(requestContext.getClass().getSimpleName(), list);
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
//        if (bitmapWrap != null) {
//            bitmapWrap.acquire();
//            return bitmapWrap;
//        }
//
//        bitmapWrap = memoryCache.get(key);
//        if (bitmapWrap != null) {
//            memoryCache.manualRemove(key);
//            activeCache.put(key, bitmapWrap);
//
//            return bitmapWrap;
//        }

        bitmapWrap = diskLruCacheWrap.get(key, bitmapMemoryPool);
        if (bitmapWrap != null) {
            activeCache.put(key, bitmapWrap);
            return bitmapWrap;
        }

        new NetWorkCache().loadBitmap(path, this, requestContext);
        return null;
    }

    @Override
    public void glideInit() {
//        LogUtils.d("RequestTargetEngine glideOnResume");
    }

    @Override
    public void glideStop() {
//        LogUtils.d("RequestTargetEngine glideStop");
    }

    @Override
    public void glideRecycle(Context context) {
//        LogUtils.d("RequestTargetEngine glideRecycle");

//        if (activeCache != null) {
//            activeCache.closeThread();
//        }

        String activityName = context.getClass().getSimpleName();
        if (bitmapByContext.containsKey(activityName)) {
            List<BitmapWrap> bitmapWraps = bitmapByContext.get(activityName);
            if (bitmapWraps != null) {
                for (BitmapWrap bitmapWrap : bitmapWraps) {
                    bitmapNonUseListener(bitmapWrap.getKey(), bitmapWrap);
                    System.gc();
//                    LogUtils.d("activityCache size >> "+activeCache.getSize());
                }
            }
        }
    }

    @Override
    public void loadSuccess(BitmapWrap bitmapWrap) {
        if (bitmapWrap != null) {
            bitmapWrap.setKey(key);
            diskLruCacheWrap.put(key, bitmapWrap);

            imageView.setImageBitmap(bitmapWrap.getBitmap());
            cacheBitmapByContext(bitmapWrap);
        }
    }

    @Override
    public void loadError(Exception e) {
        LogUtils.d("RequestTargetEngine responseException: 加载外部资源失败 e:" + e.getMessage());
    }

    /**
     * 活动缓存里面的数据不再引用之后，将其放入到memoryCache下。
     */
    @Override
    public void bitmapNonUseListener(String key, BitmapWrap bitmapWrap) {
        memoryCache.put(key, bitmapWrap);
    }
}
