package com.architect.component.api.core.event;

import com.architect.component.annotation.eventbus.SubscriberMethod;

public class Subscription {

    final Object subscriber;
    final SubscriberMethod subscriberMethod;

    public Subscription(Object subscriber, SubscriberMethod subscriberMethod) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }

}
