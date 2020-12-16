package com.architect.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.architect.component.eventbus.EventBusActivity;
import com.architect.component.imageload.GlideMainActivity;
import com.architect.component.router.ARouterMainActivity;
import com.architect.component.skin.SkinTestActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        startActivity(new Intent(this, GlideMainActivity.class));
    }

    public void arouter(View view) {
        startActivity(new Intent(this, ARouterMainActivity.class));
    }

    public void eventBus(View view) {
        startActivity(new Intent(this, EventBusActivity.class));
    }

    public void skin(View view) {
        startActivity(new Intent(this, SkinTestActivity.class));
    }

    public void imageload(View view) {
        startActivity(new Intent(this, GlideMainActivity.class));
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
        }
    }
}
