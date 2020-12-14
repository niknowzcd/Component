package com.architect.component.common.imageload.cache;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

/**
 * 内存缓存，第二级缓存
 */
public class MemoryCache extends LruCache<String, BitmapWrap> {

    private boolean manual;

    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, BitmapWrap value) {
        Bitmap bitmap = value.getBitmap();

        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }

        return bitmap.getByteCount();
    }


    /**
     * 手动删除
     */
    public void manualRemove(String key) {
        manual = true;
        remove(key);
        manual = false;
    }


    /**
     * 自动获取，根据Lru算法得到删除的数据 ，跟{@code getVaule()相反}
     * 最少使用的元素会被移除  oldValue
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, BitmapWrap oldValue, BitmapWrap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);

        if (!manual) {

        }
    }
}
