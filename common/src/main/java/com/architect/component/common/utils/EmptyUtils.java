package com.architect.component.common.utils;


import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;

public class EmptyUtils {



    public static void checkArgument(boolean expression, @NonNull String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    @NonNull
    public static <T> T checkNotNull(@Nullable T arg) {
        return checkNotNull(arg, "Argument must not be null");
    }

    @NonNull
    public static <T> T checkNotNull(@Nullable T arg, @NonNull String message) {
        if (arg == null) {
            throw new NullPointerException(message);
        }
        return arg;
    }

    @NonNull
    public static String checkNotEmpty(@Nullable String string) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException("Must not be null or empty");
        }
        return string;
    }

    @NonNull
    public static <T extends Collection<Y>, Y> T checkNotEmpty(@NonNull T collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("Must not be empty. 传递进来的值:"+collection+"是null");
        }
        return collection;
    }

    public static void checkNotEmpty(Bitmap bitmap) {
        if (null == bitmap) {
            throw new IllegalArgumentException("Must not be empty. 传递进来的值bitmap:"+bitmap+"是null");
        }
    }



}
