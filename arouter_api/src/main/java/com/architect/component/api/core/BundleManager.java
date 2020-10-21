package com.architect.component.api.core;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class BundleManager {

    private Bundle bundle = new Bundle();

    public Bundle getBundle() {
        return bundle;
    }

    public BundleManager withString(@Nullable String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withBoolean(@Nullable String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(@Nullable String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(@Nullable Bundle extraBundle) {
        bundle.putAll(extraBundle);
        return this;
    }


    public void navigation(Context context) {
        RouterManager.getInstance().navigation(context, this);
    }


}
