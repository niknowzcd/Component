package com.architect.component.common.imageload.cache;

import android.graphics.Bitmap;

import com.architect.component.common.utils.EmptyUtils;
import com.architect.component.common.utils.LogUtils;

public class BitmapWrap {

//    private static volatile BitmapWrap bitmapValue;
    private BitmapWrapCallback callback;
    private String key;
    private Bitmap mBitmap;
    private int count;

//    public static BitmapWrap getInstance() {
//        if (bitmapValue == null) {
//            synchronized (BitmapWrap.class) {
//                if (bitmapValue == null) {
//                    bitmapValue = new BitmapWrap();
//                }
//            }
//        }
//        return bitmapValue;
//    }

    public void setCallback(BitmapWrapCallback callback) {
        this.callback = callback;
    }

    public void acquire() {
        EmptyUtils.checkNotEmpty(mBitmap);

        if (mBitmap.isRecycled()) {
            LogUtils.d("useAction: 已经被回收了");
            return;
        }
        count++;
    }

    public void release() {
        if (--count <= 0 && callback != null) {
//            callback.bitmapNonUseListener(key, bitmapValue);
        }

        System.gc();
//        LogUtils.d("release 被调用  key >> " + key + " bitmap >> " + mBitmap.toString() + " count >> " + count);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

}
