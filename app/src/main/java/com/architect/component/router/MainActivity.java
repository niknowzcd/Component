package com.architect.component.router;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.architect.component.R;
import com.architect.component.annotation.ARouter;
import com.architect.component.api.core.RouterManager;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void jumpOrder(View view) {
        RouterManager.getInstance()
                .build("/order/Order_MainActivity")
                .withString("name", "张三")
                .navigation(this);
    }


    public void jumpProduct(View view) {
        RouterManager.getInstance()
                .build("/product/product_MainActivity")
                .withString("name2", "李四")
                .navigation(this);
    }
}