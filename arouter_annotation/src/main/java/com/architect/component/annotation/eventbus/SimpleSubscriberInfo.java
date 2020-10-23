package com.architect.component.annotation.eventbus;

public class SimpleSubscriberInfo implements SubscriberInfo {

    private final Class<?> subscriberClass;

    private final SubscriberMethod[] methodInfos;

    public SimpleSubscriberInfo(Class<?> subscriberClass, SubscriberMethod[] methodInfos) {
        this.subscriberClass = subscriberClass;
        this.methodInfos = methodInfos;
    }

    @Override
    public Class<?> getSubscriberClass() {
        return subscriberClass;
    }

    @Override
    public SubscriberMethod[] getSubscriberMethods() {
        return methodInfos;
    }

}
