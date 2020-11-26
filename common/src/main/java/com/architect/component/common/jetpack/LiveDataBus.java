package com.architect.component.common.jetpack;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 自定义的LiveData  解决 数据粘性问题 / 数据倒灌问题
 * 出现粘性数据的原因是因为 LiveData中dispatchingValue分发的函数不仅仅在setValue触发，在observer的时候也会触发，这时就会导致数据刷新的后果
 *
 * https://blog.csdn.net/waterIsTooDeep/article/details/105932334
 *
 * 以后开发过程中，会使用我们的 LiveDataBus，而不是LiveData原装的
 *
 */
public class LiveDataBus<T> extends MutableLiveData<T> {

    private static final String TAG = LiveDataBus.class.getSimpleName();

    // 观察 的 行为， 做事情 TODO HOOk

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, observer);

        Class<LiveData> liveDataClass = LiveData.class;
        try {
            Field mObservers = liveDataClass.getDeclaredField("mObservers");
            mObservers.setAccessible(true);

            // 获取集合 SafeIterableMap
            Object observers = mObservers.get(this);
            Class<?> observersClass = observers.getClass();

            // 获取SafeIterableMap的get(Object obj)方法
            Method methodGet = observersClass.getDeclaredMethod("get", Object.class);
            methodGet.setAccessible(true);

            // 执行get函数
            Object objectWrapperEntry =  methodGet.invoke(observers, observer);

            Object objectWrapper = null;

            if (objectWrapperEntry instanceof Map.Entry) {
                objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
            }

            if (objectWrapper == null) {
                throw new NullPointerException("ObserverWrapper can not be null");
            }

            // 获取ObserverWrapper的Class对象  LifecycleBoundObserver extends ObserverWrapper
            Class<?> wrapperClass = objectWrapper.getClass().getSuperclass();

            // 获取ObserverWrapper的field mLastVersion
            Field mLastVersion = wrapperClass.getDeclaredField("mLastVersion");
            mLastVersion.setAccessible(true);

            // 获取liveData的field mVersion
            Field mVersion = liveDataClass.getDeclaredField("mVersion");
            mVersion.setAccessible(true);
            Object mV = mVersion.get(this);

            // TODO 最关键的一句话  让我们的条件相等
            // 把当前ListData的mVersion赋值给 ObserverWrapper的field mLastVersion
            mLastVersion.set(objectWrapper, mV);

            mObservers.setAccessible(false);
            methodGet.setAccessible(false);
            mLastVersion.setAccessible(false);
            mVersion.setAccessible(false);

        } catch (Exception e) {
            Log.e(TAG, "observe: HOOK 发生了异常:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
