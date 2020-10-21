package com.architect.component.common.utils;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.widget.Toast;

import com.architect.component.common.base.MyApplication;

import java.lang.reflect.Field;

import static android.widget.Toast.makeText;

public class ToastUtils {

    private static Toast toast;
    private static Toast animationToast;
    private static Toast mCenterToast;

    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {
            toast = makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            if (Build.VERSION.SDK_INT >= 27) {
                toast.cancel();
                toast = makeText(context, content, Toast.LENGTH_SHORT);
            } else {
                toast.setText(content);
            }
        }
        toast.show();
    }

    public static void showToast(String content) {
        com.hjq.toast.ToastUtils.setGravity(Gravity.BOTTOM, 0, 150);
        com.hjq.toast.ToastUtils.show(content);
    }

    public static void showCenterToast(String content) {
        if (mCenterToast == null) {
            mCenterToast = makeText(MyApplication.getContext(), content, Toast.LENGTH_LONG);
            mCenterToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            if (Build.VERSION.SDK_INT >= 27) {
                mCenterToast.cancel();
                mCenterToast = makeText(MyApplication.getContext(), content, Toast.LENGTH_LONG);
                mCenterToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mCenterToast.setText(content);
            }
        }
        mCenterToast.show();
    }


//    /**
//     * 调用有动画的Toast
//     */
//    public static Toast makeTextAnim(Context context, CharSequence text, int duration) {
//        if (animationToast == null) {
//            animationToast = makeText(context, text, duration);
//        }
//        View inflate = LayoutInflater.from(context).inflate(R.layout.toast_layout, null, false);
//        TextView textView = inflate.findViewById(R.id.toast_text);
//        textView.setText(text);
//        animationToast.setView(inflate);
//        animationToast.setDuration(duration);
//        try {
//            Object mTN;
//            mTN = getField(animationToast, "mTN");
//            if (mTN != null) {
//                Object mParams = getField(mTN, "mParams");
//                if (mParams != null
//                        && mParams instanceof WindowManager.LayoutParams) {
//                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
//                    params.windowAnimations = R.style.anim_view;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return animationToast;
//    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    private static Object getField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }
}
