package com.architect.component.common.imageload.cache;

import android.graphics.Bitmap;

import com.architect.component.common.utils.EmptyUtils;
import com.architect.component.common.utils.LogUtils;

public class BitmapWrap {

    private static volatile BitmapWrap bitmapValue;

    public static BitmapWrap getInstance() {
        if (bitmapValue == null) {
            synchronized (BitmapWrap.class) {
                if (bitmapValue == null) {
                    bitmapValue = new BitmapWrap();
                }
            }
        }
        return bitmapValue;
    }

    private String key;
    private Bitmap mBitmap;
    private int count;

    public void useAction() {
        EmptyUtils.checkNotEmpty(mBitmap);

        if (mBitmap.isRecycled()) {
            LogUtils.d("useAction: 已经被回收了");
            return;
        }


        count++;
    }

    public void nonUseAction() {
        count--;
//        if (count <= 0) {
//
//        }

        LogUtils.d("nonUseAction 被调用");
    }

    public void recycleBitmap() {
        if (count > 0 || mBitmap.isRecycled()) return;

        mBitmap.recycle();
        System.gc();
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
