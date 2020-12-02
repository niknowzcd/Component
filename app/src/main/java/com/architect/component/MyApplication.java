package com.architect.component;

import android.app.Application;

import com.architect.component.api.core.event.EventBus;
import com.architect.component.apt.MyEventBusIndex;
import com.architect.component.skin.SkinManager;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        initEventBus();

        SkinManager.init(this);
    }

    /**
     * 官方使用文档 http://greenrobot.org/eventbus/documentation/subscriber-index/
     * <p>
     * 该方式接入eventBus，通过apt的方式来替换反射，提高性能
     * 注意事项:生成MyEventBusIndex的时候，必须要有类已经实现@Subscribe
     *
     */
    private void initEventBus() {
        EventBus.getDefault().addIndex(new MyEventBusIndex());

//        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }
}
