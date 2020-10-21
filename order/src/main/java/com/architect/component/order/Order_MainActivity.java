package com.architect.component.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.architect.component.annotation.ARouter;
import com.architect.component.order.R;

@ARouter(path = "/order/MainActivity")
public class Order_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main2);
    }
}