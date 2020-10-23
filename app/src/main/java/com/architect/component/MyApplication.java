package com.architect.component;

import android.app.Application;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        initEventBus();
    }

    /**
     * 官方使用文档 http://greenrobot.org/eventbus/documentation/subscriber-index/
     * <p>
     * 该方式接入eventBus，通过apt的方式来替换反射，提高性能
     * 注意事项:生成MyEventBusIndex的时候，必须要有类已经实现@Subscribe
     *
     */
    private void initEventBus() {
//        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }
}
