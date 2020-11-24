package com.architect.component.common.jetpack;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.architect.component.common.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jetpack_activity_login);

        final TextView textView = findViewById(R.id.textView);

        // 监听数据的变化 【我们已经Hook，已经自定义了，是无法收到 改变的数据，我们的目标达到了】
        MyEngine.getInstance().getData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
    }

    // 李元霸案例 == 正常功能：
    /*@Override
    protected void onResume() {
        super.onResume();
        // 先观察
        MyEngine.getInstance().getData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                TextView textView = findViewById(R.id.textView);
                textView.setText(s);
            }
        });

        // 后改变  5秒钟后 改变
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyEngine.getInstance().getData().setValue("李元霸");
            }
        }, 5000);
    }*/
}
