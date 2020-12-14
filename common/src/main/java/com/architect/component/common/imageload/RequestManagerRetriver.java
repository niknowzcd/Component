package com.architect.component.common.imageload;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

public class RequestManagerRetriver {

    public RequestManager get(FragmentActivity fragmentActivity) {
        return new RequestManager(fragmentActivity);
    }

    public RequestManager get(Activity activity) {
        return new RequestManager(activity);
    }

    public RequestManager get(Context context) {
        return new RequestManager(context);
    }

}
