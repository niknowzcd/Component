package com.architect.component.router;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.architect.component.R;
import com.architect.component.annotation.ARouter;

@ARouter(path = "/app/MainActivity3")
public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }
}