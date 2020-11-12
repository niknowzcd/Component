package com.architect.component.api.core.event;

import com.architect.component.annotation.eventbus.SubscriberMethod;

public class Subscription {

    final Object subscriber;
    final SubscriberMethod subscriberMethod;

    public Subscription(Object subscriber, SubscriberMethod subscriberMethod) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }

    /**
     * 重写equals,为了确保添加到数据里面的对象不会重复。
     * 如果不重写equals的话，ArrayList()的contains()匹配就不会生效，导致往缓存数组里重复添加数据。
     * <p>
     * Object对象比较的是内存地址，默认情况下只有同一个new出来的对象会相等。
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Subscription) {
            Subscription otherSubscription = (Subscription) o;
            return subscriberMethod.equals(otherSubscription.subscriberMethod);
        }
        return false;
    }
}
