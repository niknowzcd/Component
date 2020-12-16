package com.architect.component.common.imageload.cache;

import com.architect.component.common.utils.LogUtils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 活动缓存也是第一级缓存
 * <p>
 * WeakReference 参考
 * https://blog.csdn.net/gdutxiaoxu/article/details/80738581
 * https://www.jianshu.com/p/f86d3a43eec5
 * <p>
 * Q:MemoryCache底层用的是LinkedHashMap.为什么还要多一层ActiveCache呢?
 * A:ActiveCache可以理解为MemoryCache的备用空间.
 * 当需要用到某个图片的时候，将图片移动到ActiveCache，而将MemoryCache对应的图片删除。这样能延迟MemoryCache内存过大，进行的清理工作。
 * <p>
 * Q:那为什么有了ActiveCache，又要MemoryCache呢？
 * A:因为ActiveCache采用的是WeakReference虚引用，在很多情况下都容易被清理掉，这个时候如果没有MemoryCache的话，就只能去磁盘或者网络获取了。
 * <p>
 * Q:MemoryCache不是LRU算法的吗？应该会自动清理内存才对？
 * A:MemoryCache内部实现是一个LinkedHashMap,初始化大小是固定的，随着存储的内容增加自动扩容，增加ActiveCache也是为了延迟扩容的时机
 * <p>
 * 举个例子说明，假设MemoryCache初始化大小为8，最大容量为16。这个时候界面上已经加载了8个数据了，需要新增加一个数据，如果没有ActiveCache的话
 * MemoryCache就需要去扩容，而有了ActiveCache。8个数据的引用都在ActiveCache上，而MemoryCache只有新增加的一个数据。
 */
public class ActiveCache {

    private Map<String, WeakReference<BitmapWrap>> map = new HashMap<>();
    private Map<String, String> map2 = new HashMap<>();
    private ReferenceQueue<BitmapWrap> queue = new ReferenceQueue<>();
    private boolean isCloseThread;
    private BitmapWrapCallback callback;
    private Thread cleanThread;

    public ActiveCache(BitmapWrapCallback callback) {
        this.callback = callback;
    }

    public void put(String key, BitmapWrap bitmapWrap) {
        map.put(key, new MyWeakReference(bitmapWrap, getQueue(), key));
        bitmapWrap.setCallback(callback);
    }

    public BitmapWrap get(String key) {
        WeakReference<BitmapWrap> reference = map.get(key);
        if (reference != null) {
            return reference.get();
        }

        return null;
    }

    public void closeThread() {
        isCloseThread = true;
        if (cleanThread != null) {
            cleanThread.interrupt();
            try {
                cleanThread.join(TimeUnit.SECONDS.toMillis(5));
                if (cleanThread.isAlive()) {
                    throw new RuntimeException("Failed to join in time");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * WeakReference可以接受两个参数，第二个参数是一个ReferenceQueue队列。
     * 当referent被回收的时候，会把这个对象放到ReferenceQueue中 ，可以通过System.gc()来手动回收
     *
     * <pre>{@code
     *
     *   put("123", new BitmapWrap());
     *   System.gc();
     *   System.out.println("bitmap >> " + map.get("123").get());
     *   System.out.println("queue >> " + queue.poll());
     *
     * }</pre>
     */
    public static final class MyWeakReference extends WeakReference<BitmapWrap> {
        private final String key;

        public MyWeakReference(BitmapWrap referent, ReferenceQueue<? super BitmapWrap> q, String key) {
            super(referent, q);
            this.key = key;
        }
    }

    private ReferenceQueue<BitmapWrap> getQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();

            cleanThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (!isCloseThread) {
                        remove();
                    }
                }
            };
            cleanThread.start();
        }

        return queue;
    }

    /**
     * queue.remove() 内部是一个阻塞式方法,所以正常流程下，这个线程可以一直开着
     */
    private void remove() {
        try {
            LogUtils.d("activitycache remove");
            Reference<? extends BitmapWrap> reference = queue.remove();
            MyWeakReference weakReference = (MyWeakReference) reference;
            if (map != null && !map.isEmpty()) {
                map.remove(weakReference.key);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getSize() {
        return map.size();
    }

}
