package com.architect.component.common.imageload;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * https://blog.csdn.net/qq_24345643/article/details/87564862
 *
 *
 */
public class Glide {

    private static volatile Glide glide;
    private RequestManagerRetriver retriver;

    public static Glide get() {
        if (glide == null) {
            synchronized (Glide.class) {
                if (glide == null) {
                    initGlide();
                }
            }
        }
        return glide;
    }

    private static void initGlide() {
        Glide.glide = new GlideBuilder().build();
    }

    public Glide(RequestManagerRetriver retriver) {
        this.retriver = retriver;
    }

    public static RequestManager with(FragmentActivity fragmentActivity) {
        return getRetriver().get(fragmentActivity);
    }

    public static RequestManager with(Activity activity) {
        return getRetriver().get(activity);
    }

    public static RequestManager with(Context context) {
        return getRetriver().get(context);
    }

    public static RequestManagerRetriver getRetriver() {
        return Glide.get().retriver;
    }


}
