package com.architect.component.common.http;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Dispatcher {

    private final int maxRequests = 30;  // 同时访问任务，最大限制64个
    private final int maxRequestHost = 5;  //同时访问同一个服务器域名，最大限制5个

    private Deque<RealCall.AsyncCall> runningCalls = new ArrayDeque<>();
    private Deque<RealCall.AsyncCall> readyCalls = new ArrayDeque<>();

    public void enqueue(RealCall.AsyncCall call) {
        if (runningCalls.size() < maxRequests && runningCallHost(call) < maxRequestHost) {
            runningCalls.add(call);
            executorService().execute(call);
        } else {
            readyCalls.add(call);
        }

//        System.out.println("runningCalls >> "+runningCalls.size());
//        System.out.println("readyCalls >> "+readyCalls.size());
    }


    private ExecutorService executorService() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("自定义的线程....");
                thread.setDaemon(false); // 不是守护线程
                return thread;
            }
        });
    }


    private int runningCallHost(RealCall.AsyncCall call) {
        int count = 0;
        if (runningCalls.isEmpty()) return 0;

        return count;

//        SocketRequestServer server = new SocketRequestServer();

    }

    /**
     * 移除运行完成的任务，将readyCalls下等待的任务取出来运行
     *
     */
    public void finish(RealCall.AsyncCall call) {
        runningCalls.remove(call);

        if (readyCalls.isEmpty()) return;

        for (RealCall.AsyncCall nextCall : readyCalls) {
            readyCalls.remove(nextCall);
            runningCalls.add(nextCall);

            executorService().execute(nextCall);
        }
    }
}
