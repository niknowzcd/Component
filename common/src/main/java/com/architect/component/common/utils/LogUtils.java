package com.architect.component.common.utils;

import android.util.Log;

import com.architect.component.common.BuildConfig;

public class LogUtils {

    private static final String TAG = "logger";

    public static void d(String msg) {
//        if (!BuildConfig.isRelease) {
            Log.d(TAG, msg);
//        }
    }
}
