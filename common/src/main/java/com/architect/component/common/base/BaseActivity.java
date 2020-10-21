package com.architect.component.common.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.architect.component.common.utils.ActivityUtils;
import com.architect.component.common.utils.LogUtils;


/**
 * Created by XiaoZhuoDev on 2017/9/13.
 */

public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView {

    private P presenter;
    private V view;


    public P getPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        if (AndroidBug54971Workaround.checkDeviceHasNavigationBar(this)) {
            AndroidBug54971Workaround.assistActivity(findViewById(android.R.id.content));
        }
        ActivityUtils.addActivity(this);
        if (presenter == null) {
            presenter = createPresenter();
        }
        if (view == null) {
            view = createView();
        }

        if (presenter == null) {
            throw new NullPointerException("Presenter 不能为空");
        }
        if (view == null) {
            throw new NullPointerException("View 不能为空");
        }
        presenter.attachView(view);
    }

    /**
     * 获取布局id
     *
     * @return
     */
    protected abstract int getContentView();

    /**
     * 绑定presenter
     *
     * @return
     */
    protected abstract P createPresenter();

    /**
     * 绑定view
     *
     * @return
     */
    protected abstract V createView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        ActivityUtils.finishActivity(this);
    }

    @Override
    public void getDataFail(String failMag, int code) {

    }

    @Override
    public void getDataFail(String failMsg) {

    }

    public DialogFragment mFragment;

    public void showDialogFragment(DialogFragment fragment) {
        try {
            if (fragment == null) return;
            if (mFragment != null) {
                mFragment.dismissAllowingStateLoss();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment.show(transaction, fragment.getClass().getSimpleName());
            mFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("showDialog error = " + e.getMessage());
        }
    }

    protected boolean hasDialogFragmentIsVisible() {
        return mFragment != null && mFragment.isVisible();
    }

    public void hideDialogFragment() {
        if (mFragment != null) {
            mFragment.dismissAllowingStateLoss();
        }
    }
//
//
//    private LoadingDialog2 loadingDialog2;
//    private LoadingDialog2 loadingDialogbyText;
//
//    public void showLoadingDialog2() {
//        if (isFinishing()) return;
//        if (loadingDialog2 == null) {
//            loadingDialog2 = new LoadingDialog2(this, getResources().getString(R.string.loading));
//        }
//        loadingDialog2.show();
//    }
//
//    protected void showLoadingDialog2(String text) {
//        if (loadingDialog2 == null) {
//            loadingDialog2 = new LoadingDialog2(this, text);
//        }
//        loadingDialog2.show();
//    }
//
//    protected void showLoadingDialogByText(String text) {
//        if (loadingDialogbyText == null) {
//            loadingDialogbyText = new LoadingDialog2(this, text);
//        }
//        loadingDialogbyText.show();
//    }
//
//    protected void hideLoadingDialogByText() {
//        if (loadingDialogbyText != null) {
//            loadingDialogbyText.close();
//        }
//    }
//
//    public void hideLoadingDialog2() {
//        if (loadingDialog2 != null) {
//            loadingDialog2.close();
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
