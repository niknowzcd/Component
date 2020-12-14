package com.architect.component.common.imageload.disk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.architect.component.common.imageload.cache.BitmapWrap;
import com.architect.component.common.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存，第三级缓存
 */
public class DiskLruCacheWrap {

    private static final String TAG = DiskLruCacheWrap.class.getSimpleName();

    private static final String DISKLRU_CACHE_DIR = "disk_lru_cache_dir";
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final long MAX_SIZE = 1024 * 1024 * 100;

    private DiskLruCache diskLruCache;


    public DiskLruCacheWrap() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DISKLRU_CACHE_DIR);
        try {
            diskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.d(TAG, "DiskLruCacheWrap 创建文件失败 ,casue >> " + e.getMessage());
        }
    }


    public void put(String key, BitmapWrap wrap) {
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;

        try {
            editor = diskLruCache.edit(key);
            outputStream = editor.newOutputStream(0);
            Bitmap bitmap = wrap.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (editor != null) editor.abort();
            } catch (IOException ex) {
                ex.printStackTrace();
                LogUtils.d(TAG, "put abort error, cause >> " + e.getMessage());
            }
        } finally {
            try {
                if (editor != null) editor.commit();   //不管成功失败，都需要提交
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.d(TAG, "put commit error , cause >> " + e.getMessage());
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.d(TAG, "outputStream.close() error , cause >> " + e.getMessage());
                }
            }
        }
    }

    public BitmapWrap get(String key) {
        InputStream inputStream = null;

        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                BitmapWrap wrap = BitmapWrap.getInstance();
                wrap.setBitmap(bitmap);
                wrap.setKey(key);
                return wrap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


}
