package com.architect.component.common.imageload.lifecycle;

import android.content.Context;

public interface BitmapLifecycleCallback {

    void glideInit();

    void glideStop();

    void glideRecycle(Context context);

}
