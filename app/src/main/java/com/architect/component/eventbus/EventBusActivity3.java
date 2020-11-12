package com.architect.component.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.architect.component.R;
import com.architect.component.annotation.eventbus.Subscribe;
import com.architect.component.annotation.eventbus.ThreadMode;
import com.architect.component.api.core.event.BaseEventBusBeaan;
import com.architect.component.api.core.event.EventBus;
import com.architect.component.common.utils.LogUtils;


public class EventBusActivity3 extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_event_bus3);
        textView = findViewById(R.id.content_tv);
    }

    // 订阅方法
    @Subscribe
    public void event(BaseEventBusBeaan message) {
        LogUtils.d("EventBusActivity3 event ");
    }

    // 订阅方法
    @Subscribe(threadMode = ThreadMode.POSTING, priority = 2, sticky = true)
    public void sticky(UserInfo message) {
        LogUtils.d("EventBusActivity3 sticky3");
    }

//    // 订阅方法
//    @Subscribe(threadMode = ThreadMode.POSTING, priority = 2, sticky = true)
//    public void sticky2(UserInfo message) {
//        LogUtils.d("EventBusActivity2 sticky2");
//    }

    public void sendMessage(View view) {
        EventBus.getDefault().post(new UserInfo("123", new Bundle()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}