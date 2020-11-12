package com.architect.component.eventbus;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.architect.component.R;
import com.architect.component.annotation.eventbus.Subscribe;
import com.architect.component.api.core.event.EventBus;
import com.architect.component.common.utils.LogUtils;


public class EventBusActivity2 extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);


        setContentView(R.layout.activity_event_bus2);
        textView = findViewById(R.id.content_tv);
    }

    // 订阅方法
    @Subscribe
    public void event(UserInfo message) {
        LogUtils.d("EventBusActivity2 event ");
    }

    public void sendMessage(View view) {
        EventBus.getDefault().post(new UserInfo("123", 12));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}