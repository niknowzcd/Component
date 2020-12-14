package com.architect.component.common.imageload.lifecycle;

public interface BitmapLifecycleCallback {

    void glideInit();

    void glideStop();

    void glideRecycle();

}
