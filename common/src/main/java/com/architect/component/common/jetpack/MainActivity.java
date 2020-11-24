package com.architect.component.common.jetpack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.architect.component.common.R;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jetpack_activity_main);

        textView =  findViewById(R.id.textView);

        // 张三案例：先观察
        // 观察数据的变化
        MyEngine.getInstance().getData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 张三案例：后改变 5秒钟之后 改变
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyEngine.getInstance().getData().setValue("张三");
            }
        }, 5000);
    }

    // 启动LoginActivity
    public void startLoginActivity(View view) {
        MyEngine.getInstance().getData().setValue("刘德利");

        startActivity(new Intent(this, LoginActivity.class));
    }
}
