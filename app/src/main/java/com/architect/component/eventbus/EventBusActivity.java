package com.architect.component.eventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.architect.component.R;
import com.architect.component.annotation.eventbus.Subscribe;
import com.architect.component.annotation.eventbus.ThreadMode;
import com.architect.component.api.core.event.BaseEventBusBeaan;
import com.architect.component.api.core.event.EventBus;
import com.architect.component.common.utils.LogUtils;
import com.architect.component.okhttp.OkHttpTest;


public class EventBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus);
        EventBus.getDefault().register(this);

    }

    // 订阅方法
    @Subscribe
    public void event(BaseEventBusBeaan message) {
        LogUtils.d("EventBusActivity event");
    }

    // 订阅方法
    @Subscribe
    public void event2(UserInfo message) {
        LogUtils.d("EventBusActivity event2");
    }
//
//    // 订阅方法
//    @Subscribe(threadMode = ThreadMode.POSTING, priority = 2)
//    public void event3(BaseEventBusBeaan message) {
//        LogUtils.d("EventBusActivity event3");
//    }

    public void jump(View view) {
        EventBus.getDefault().postSticky(new UserInfo("123", new Bundle()));
        startActivity(new Intent(this, EventBusActivity2.class));
    }

    public void request(View view) {
        OkHttpTest.httpPoolTest();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}