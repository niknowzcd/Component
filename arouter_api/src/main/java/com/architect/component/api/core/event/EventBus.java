package com.architect.component.api.core.event;

import android.os.Handler;
import android.os.Looper;

import com.architect.component.annotation.eventbus.SubscriberInfo;
import com.architect.component.annotation.eventbus.SubscriberInfoIndex;
import com.architect.component.annotation.eventbus.SubscriberMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ArrayList的底层是数组，查询和修改直接根据索引可以很快找到对应的元素（替换）
 * 而增加和删除就涉及到数组元素的移动，所以会比较慢
 * <p>
 * CopyOnWriteArrayList实现了List接口（读写分离）
 * Vector是增删改查方法都加了synchronized，保证同步，但是每个方法执行的时候都要去获得锁，性能就会大大下降
 * 而CopyOnWriteArrayList 只是在增删改上加锁，但是读不加锁，在读方面的性能就好于Vector
 * <p>
 * CopyOnWriteArrayList支持读多写少的并发情况
 */
public class EventBus {

    //apt生成的索引文件
    private SubscriberInfoIndex subscriberInfoIndex;

    private static volatile EventBus instance;
    private Handler handler;
    private ExecutorService executorService;
    private Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;

    //订阅者类型集合，表示订阅者都订阅了那些类型的事件，解除订阅的时候需要把数组清空
    //如果所有订阅者都定义了同一个类型的事件，比如EventBean。
    //这样写起来简单，但是在post的时候，会遍历所有订阅者，效率不高。需要根据业务做拆分
    private Map<Object, List<Class<?>>> typeBySubscriber;

    private final Map<Class<?>, List<SubscriberMethod>> methodBySubscriber;


    public EventBus() {
        methodBySubscriber = new HashMap<>();
        typeBySubscriber = new HashMap<>();
        subscriptionsByEventType = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public void addIndex(SubscriberInfoIndex index) {
        subscriberInfoIndex = index;
    }

    /**
     * @param subscriber
     */
    public void register(Object subscriber) {
        Class<?> subscriberClass = subscriber.getClass();
        List<SubscriberMethod> subscriberMethods = findSubscriberMethods(subscriberClass);

        for (SubscriberMethod method : subscriberMethods) {
            subscribe(subscriber, method);
        }
    }

    public synchronized void unregister(Object subscriber) {
        List<Class<?>> eventTypes = typeBySubscriber.get(subscriber);
        if (eventTypes != null) {
            eventTypes.clear();
            typeBySubscriber.remove(subscriber);
        }
    }

    private List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        List<SubscriberMethod> subscriberMethods = methodBySubscriber.get(subscriberClass);
        if (subscriberMethods != null) return subscriberMethods;

        subscriberMethods = findByAPT(subscriberClass);
        if (subscriberMethods != null) {
            methodBySubscriber.put(subscriberClass, subscriberMethods);
        }

        return subscriberMethods;
    }

    private List<SubscriberMethod> findByAPT(Class<?> subscriberClass) {
        if (subscriberInfoIndex == null) {
            throw new RuntimeException("未添加索引文件");
        }
        SubscriberInfo subscriberInfo = subscriberInfoIndex.getSubscriberInfo(subscriberClass);
        if (subscriberInfo != null) return Arrays.asList(subscriberInfo.getSubscriberMethods());
        return null;
    }


    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        Class<?> eventType = subscriberMethod.getEventType();
        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<>();
            subscriptionsByEventType.put(eventType, subscriptions);
        }

        Subscription subscription = new Subscription(subscriber, subscriberMethod);
        if (subscriptions.contains(subscription)) {
            //粘性事件相关逻辑
        }

        //根据优先级插队
        int size = subscriptions.size();
        for (int i = 0; i <= size; i++) {
            if (i == size || subscriberMethod.getPriority() > subscriptions.get(i).subscriberMethod.getPriority()) {
                if (!subscriptions.contains(subscription)) subscriptions.add(i, subscription);
                break;
            }
        }

        //订阅者类型集合
        List<Class<?>> subscribeEvents = typeBySubscriber.get(subscriber);
        if (subscribeEvents == null) {
            subscribeEvents = new ArrayList<>();
            typeBySubscriber.put(subscriber, subscribeEvents);
        }
        subscribeEvents.add(eventType);
    }

    public void post(Object event) {
        postSingleEventForEventType(event, event.getClass());
    }

    //遍历所有的订阅者，发送对应的事件
    private void postSingleEventForEventType(Object event, Class<?> eventClass) {
        CopyOnWriteArrayList<Subscription> subscriptions;

        synchronized (this) {
            subscriptions = subscriptionsByEventType.get(eventClass);
        }
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                postToSubscription(subscription, event);
            }
        }
    }

    private void postToSubscription(final Subscription subscription, final Object event) {
        switch (subscription.subscriberMethod.getThreadMode()) {
            case POSTING: // 订阅、发布在同一线程
                invokeSubscriber(subscription, event);
                break;
            case MAIN:
                //事件发送方是主线程
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    invokeSubscriber(subscription, event);
                } else {
                    //事件发送方是子线程
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            invokeSubscriber(subscription, event);
                        }
                    });
                }
                break;
            case ASYNC:
                //发送方在主线程
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            invokeSubscriber(subscription, event);
                        }
                    });
                } else {
                    invokeSubscriber(subscription, event);
                }
                break;
        }
    }

    private void invokeSubscriber(Subscription subscription, Object event) {
        try {
            subscription.subscriberMethod.getMethod().invoke(subscription.subscriber, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}















