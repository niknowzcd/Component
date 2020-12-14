package com.architect.component.common.imageload.lifecycle;

import android.annotation.SuppressLint;

import com.architect.component.common.utils.LogUtils;

/**
 * Activity 生命周期管理
 * 如果用的是 androidx的包的话，只需要这一个类就可以了。
 * 如果使用的是support的包，需要同时兼容 support.v4.app.Fragment 和 android.app.Fragment
 */
@SuppressLint("ValidFragment")
public class ActivityLifecycleManager extends android.app.Fragment {

    private static final String TAG = ActivityLifecycleManager.class.getSimpleName();
    public BitmapLifecycleCallback lifecycleCallback;

    @SuppressLint("ValidFragment")
    public ActivityLifecycleManager(BitmapLifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
        LogUtils.d("ActivityLifecycleManager init");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideInit();
            LogUtils.d("ActivityLifecycleManager onStart");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideStop();
            LogUtils.d("ActivityLifecycleManager onStop");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideRecycle();
            LogUtils.d("ActivityLifecycleManager onDestroy");
        }
    }
}
