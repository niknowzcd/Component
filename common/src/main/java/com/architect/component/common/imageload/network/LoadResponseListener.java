package com.architect.component.common.imageload.network;

import com.architect.component.common.imageload.cache.BitmapWrap;

public interface LoadResponseListener {

    void loadSuccess(BitmapWrap bitmapWrap);

    void loadError(Exception e);

}
