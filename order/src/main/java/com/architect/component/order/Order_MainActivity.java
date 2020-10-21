package com.architect.component.order;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.architect.component.annotation.ARouter;

@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main2);

        TextView textView = findViewById(R.id.order_tv);
        String extra = getIntent().getStringExtra("name");

        textView.setText("name = " + extra);
    }
}