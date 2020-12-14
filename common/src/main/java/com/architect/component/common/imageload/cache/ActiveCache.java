package com.architect.component.common.imageload.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动缓存也是第一级缓存
 * <p>
 * WeakReference 参考
 * https://blog.csdn.net/gdutxiaoxu/article/details/80738581
 * https://www.jianshu.com/p/f86d3a43eec5
 */
public class ActiveCache {

    private Map<String, WeakReference<BitmapWrap>> map = new HashMap<>();
    private ReferenceQueue<BitmapWrap> queue = new ReferenceQueue<>();
    private boolean isCloseThread;


    public void put(String key, BitmapWrap bitmapWrap) {
        map.put(key, new MyWeakReference(bitmapWrap, getQueue(), key));
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

        map.clear();
        System.gc();
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
    public static class MyWeakReference extends WeakReference<BitmapWrap> {
        private String key;

        public MyWeakReference(BitmapWrap referent, ReferenceQueue<? super BitmapWrap> q, String key) {
            super(referent, q);
            this.key = key;
        }
    }

    private ReferenceQueue<BitmapWrap> getQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (!isCloseThread) {
                        remove();
                    }
                }
            };
            thread.start();
        }

        return queue;
    }

    /**
     * queue.remove() 内部是一个阻塞式方法,所以正常流程下，这个线程可以一直开着
     */
    private void remove() {
        try {
            Reference<? extends BitmapWrap> reference = queue.remove();
            MyWeakReference weakReference = (MyWeakReference) reference;
            if (map != null && !map.isEmpty()) {
                map.remove(weakReference.key);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
