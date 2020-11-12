package com.architect.component.eventbus;

import android.os.Bundle;

import com.architect.component.api.core.event.BaseEventBusBeaan;

/**
 * 定义事件
 */
public class UserInfo extends BaseEventBusBeaan {


    public UserInfo(String msg, Bundle data) {
        super(msg, data);
    }
}
