package com.architect.component.common.imageload.pool;

import android.graphics.Bitmap;

interface BitmapPoolImlpl {

    void put(Bitmap bitmap);

    Bitmap get(int width, int height, Bitmap.Config config);
}
