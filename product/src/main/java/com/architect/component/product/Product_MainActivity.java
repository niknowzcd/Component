package com.architect.component.product;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.architect.component.annotation.ARouter;

@ARouter(path = "/product/product_MainActivity")
public class Product_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_main);

        TextView textView = findViewById(R.id.product_tv);
        String extra = getIntent().getStringExtra("name2");
        textView.setText("name2 = " + extra);
    }
}