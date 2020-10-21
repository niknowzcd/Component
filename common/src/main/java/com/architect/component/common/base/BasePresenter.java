package com.architect.component.common.base;

/**
 * Created by XiaoZhuoDev on 2017/9/13.
 */

public class BasePresenter<V extends BaseView> {

    private V view;

    public void attachView(V view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public V getView() {
        return this.view;
    }

}
