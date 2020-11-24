package com.architect.component.common.jetpack;

import androidx.lifecycle.MutableLiveData;

public class MyEngine {

    // 整个项目都需要用到 引擎，所有只有一个实例 单例模式
    private static MyEngine myEngine;

    public static MyEngine getInstance() {
        if (null == myEngine) {
            synchronized (MyEngine.class) {
                if (null == myEngine) {
                    myEngine = new MyEngine();
                }
            }
        }
        return myEngine;
    }

    // 数据
//     private MutableLiveData<String> data;
//
    private LiveDataBus<String> data;

    // 暴露数据
    public MutableLiveData<String> getData() {
        if (null == data) {
            data =new LiveDataBus<>();
        }
        return data;
    }
}
