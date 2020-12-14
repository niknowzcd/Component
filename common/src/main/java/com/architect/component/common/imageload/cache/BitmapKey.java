package com.architect.component.common.imageload.cache;

import com.architect.component.common.utils.MD5Utils;

public class BitmapKey {

    private String key;

    public BitmapKey(String key) {
        this.key = MD5Utils.md5(key);
    }

    public String getKey() {
        return key;
    }
}
