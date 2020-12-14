package com.architect.component.common.imageload.lifecycle;

import androidx.fragment.app.Fragment;

import com.architect.component.common.utils.LogUtils;

/**
 * Activity 生命周期管理
 * 如果用的是 androidx的包的话，需要同时兼容 androidx.fragment.app.Fragment 和 android.app.Fragment
 * 如果使用的是support的包，需要同时兼容 support.v4.app.Fragment 和 android.app.Fragment
 */
public class FragmentActivityLifecycleManager extends Fragment {

    private static final String TAG = FragmentActivityLifecycleManager.class.getSimpleName();
    public BitmapLifecycleCallback lifecycleCallback;

    public FragmentActivityLifecycleManager(BitmapLifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
        LogUtils.d("FragmentActivityLifecycleManager init");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideInit();
            LogUtils.d("FragmentActivityLifecycleManager onStart");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideStop();
            LogUtils.d("FragmentActivityLifecycleManager onStop");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideRecycle();
            LogUtils.d("FragmentActivityLifecycleManager onDestroy");
        }
    }
}
