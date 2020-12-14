package com.architect.component.common.imageload;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

public class Glide {

    private RequestManagerRetriver retriver;

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
        RequestManagerRetriver retriver = new RequestManagerRetriver();
        Glide glide = new Glide(retriver);
        return glide.retriver;
    }


}
