package com.architect.component.api.core.event;

import android.os.Bundle;

/**
 * EventBus的基础传输对象
 * 由于EventBus是根据事件类型对象来查找订阅者，如果所有订阅者都是用的同一个事件类型订阅的话。
 * 当post一个事件的时候，所有的订阅者都会接收到事件的分发，白白消耗一部分性能。
 *
 * 另外 EventBus订阅的时候，@Subscribe 只允许接受一个参数，为了规范，所以定义一个事件的基础类。
 *
 * 如果一个事件是公共的，可以直接使用基础类，而如果只是单独某个界面用到，可以继承自基础类
 */
public class BaseEventBusBeaan {

    public String msg;
    public Bundle data;

    public BaseEventBusBeaan(String msg, Bundle data) {
        this.msg = msg;
        this.data = data;
    }
}
