package com.architect.component.common.imageload.pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.architect.component.common.utils.LogUtils;

import java.util.TreeMap;

/**
 * 复用bitmap的内存空间。
 * 有很多bitmap不再被使用之后，并不会马上被回收，这个时候如果一个新的bitmap需要空间就会去开辟新的内存空间。
 * 如果这个时候内存不够分配，还得先把旧的内存空间回收。不断的回收开辟，容易导致内存抖动。
 * <p>
 * 这个类的作用就是将那些不再使用的，并且还没有被回收的内存空间用来给新的bitmap使用。
 * <p>
 * {@code 核心代码
 * <p>
 * BitmapFactory.Options options2 = new BitmapFactory.Options();
 * Bitmap bitmapPoolResult = bitmapPool.get(w, h, Bitmap.Config.RGB_565);
 * options2.inBitmap = bitmapPoolResult;
 * options2.inMutable = true;
 * options2.inPreferredConfig = Bitmap.Config.RGB_565;
 * options2.inJustDecodeBounds = false;
 * Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options2)
 * <p>
 * }
 * <p>
 * BitmapFactory.Options的一些参数参考
 * https://www.jianshu.com/p/b16256a91427
 */
public class BitmapMemoryPool extends LruCache<Integer, Bitmap> implements BitmapPoolImlpl {

    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    public BitmapMemoryPool(int maxSize) {
        super(maxSize);
    }

    /**
     * 内存复用的条件
     * 1.bitmap.isMutable=true  表示位图信息可以变更
     * 2.bitmap的内存大小必须小于maxSize()
     */
    @Override
    public void put(Bitmap bitmap) {
        if (!bitmap.isMutable()) {
            return;
        }
        int bitmapSize = getBitmapSize(bitmap);
        if (bitmapSize > maxSize()) {
            return;
        }

        put(bitmapSize, bitmap);
        //treeMap的作用是为了快速查找，在有序的map情况下，TreeMap的效率会比HashMap高
        treeMap.put(bitmapSize, null);
    }

    private int getBitmapSize(Bitmap bitmap) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }


    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        int bitmapSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        Integer key = treeMap.ceilingKey(bitmapSize);
        if (key == null) return null;
        if (bitmapSize < key) {
            LogUtils.d("BitmapMemoryPool 复用返回");
            return remove(key);
        }

        return null;
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return getBitmapSize(value);
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        treeMap.remove(key);
    }
}
