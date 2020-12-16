package com.architect.component.common.imageload.lifecycle;

import android.annotation.SuppressLint;

/**
 * Activity 生命周期管理
 * 如果用的是 androidx的包的话，只需要这一个类就可以了。
 * 如果使用的是support的包，需要同时兼容 support.v4.app.Fragment 和 android.app.Fragment
 */
@SuppressLint("ValidFragment")
public class ActivityLifecycleManager extends android.app.Fragment {

    public BitmapLifecycleCallback lifecycleCallback;

    @SuppressLint("ValidFragment")
    public ActivityLifecycleManager(BitmapLifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideInit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideRecycle(getContext());
        }
    }
}
