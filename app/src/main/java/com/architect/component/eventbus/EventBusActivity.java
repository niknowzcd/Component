package com.architect.component.eventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.architect.component.R;
import com.architect.component.annotation.eventbus.Subscribe;
import com.architect.component.common.utils.LogUtils;


public class EventBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus);
    }

    // 订阅方法
    @Subscribe
    public void event(int string) {
        LogUtils.d("event >>> ");
    }


}