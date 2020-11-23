package com.architect.component.common.http.pool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 参考 https://www.jianshu.com/p/32de43ce0252
 *
 * socker连接的连接池
 * 目的:复用已经存在的socket，避免频繁进行网络连接产生的消耗 (三次握手，四次挥手)
 * <p>
 * 核心部分在与如何维护好线程池的大小，或者说如何来处理部分长时间不再使用的连接
 * 开启一个线程专门来执行清理工作。具体看clean()函数
 */
public class ConnectionPool {

    private static final Executor executor = new ThreadPoolExecutor(0,
            Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), ThreadUtils.threadFactory("MyOkhttp ConnectionPool", true));

    private static Deque<HttpConnection> httpConnections = new ArrayDeque<>();

    private boolean cleanRunnableFlag;
    private long keepAlive;    //闲置时间

    public ConnectionPool() {
        this(60, TimeUnit.MILLISECONDS);
    }

    public ConnectionPool(long keepAlive, TimeUnit timeUnit) {
        this.keepAlive = timeUnit.toMicros(keepAlive);
    }

    /**
     * 清理线程池中的连接对象 (核心逻辑)
     */
    private Runnable cleanRunnable = new Runnable() {
        @Override
        public void run() {
            long nextCheckCleanTime = clean(System.currentTimeMillis());

            if (nextCheckCleanTime == -1) {
                cleanRunnableFlag = false;
                return;
            }

            if (nextCheckCleanTime > 0) {
                synchronized (ConnectionPool.this) {
                    try {
                        ConnectionPool.this.wait(nextCheckCleanTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    /**
     * 遍历数组,计算每一个连接的闲置时间,将所有闲置时间大于keepAlive的连接移除。
     * 计算所有还未移除的连接的最大的闲置时间，将这个时间返回，用于清理线程的休眠
     * 如果没有连接存在，返回-1，并将清理线程回收，注意修改cleanRunnableFlag的值。
     * 等到下一次添加新的线程的时候，再次开启清理线程
     */
    private long clean(long currentTimeMills) {
        long idleRecordSave = -1;

        synchronized (this) {
            Iterator<HttpConnection> iterator = httpConnections.iterator();
            while (iterator.hasNext()) {
                HttpConnection connection = iterator.next();

                long idleTime = currentTimeMills - connection.hastUseTime;
                if (idleTime > keepAlive) {
                    iterator.remove();
                    connection.closeSocket();
                    continue;
                }

                //计算所有链接中 最长的闲置时间
                if (idleRecordSave < idleTime) {
                    idleRecordSave = idleTime;
                }
            }

            //说明还有连接存在
            if (idleRecordSave != -1) {
                return (keepAlive - idleRecordSave);
            }

        }
        return idleRecordSave;
    }


    public synchronized void putConnection(HttpConnection httpConnection) {
//        if (!cleanRunnableFlag) {
//            cleanRunnableFlag = true;
//            executor.execute(cleanRunnable);
//        }
        httpConnections.add(httpConnection);

        System.out.println("ConnectionPool size >> " + httpConnections.size());
    }

    public HttpConnection getConnection(String host, int port) {
        Iterator<HttpConnection> iterator = httpConnections.iterator();

        while (iterator.hasNext()) {
            HttpConnection connection = iterator.next();
            if (connection.isConnection(host, port)) {
//                iterator.remove();
                return connection;
            }
        }
        return null;
    }

}
