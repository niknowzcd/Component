package com.architect.component.common.imageload;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.architect.component.common.imageload.lifecycle.ActivityLifecycleManager;
import com.architect.component.common.imageload.lifecycle.FragmentActivityLifecycleManager;

/**
 * 管理生命周期的方式，就是通过传入的 context 或者 activity 生成一个fragment
 * 然后通过fragment的生命周期来管理对应 glide的生命周期
 */
public class RequestManager {

    private static final String FRAGMENT_ACTIVITY_TAG = "fragment_activity_tag";
    private static final String ACTIVIEY_TAG = "activity_tag";

    private Context requestContext;
    private RequestTargetEngine requestTargetEngine;

    {
        requestTargetEngine = new RequestTargetEngine();
    }

    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestContext = fragmentActivity;

        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_TAG);
        if (fragment == null) {
            fragment = new FragmentActivityLifecycleManager(requestTargetEngine);
            fragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_TAG).commitAllowingStateLoss();
        }
    }

    public RequestManager(Activity activity) {
        this.requestContext = activity;

        android.app.FragmentManager manager = activity.getFragmentManager();
        android.app.Fragment fragment = manager.findFragmentByTag(ACTIVIEY_TAG);
        if (fragment == null) {
            fragment = new ActivityLifecycleManager(requestTargetEngine);
            manager.beginTransaction().add(fragment, ACTIVIEY_TAG).commit();
        }
    }


    public RequestManager(Context context) {
        this.requestContext = context;
    }

    public RequestTargetEngine load(String path) {
        requestTargetEngine.loadBitmap(path, requestContext);
        return requestTargetEngine;
    }


}
