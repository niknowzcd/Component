package com.architect.component.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.architect.component.common.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

/**
 * Created by XiaoZhuoDev on 2017/9/15.
 */

public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements BaseView {
    private P mPresenter;
    private V mView;

    public P getPresenter() {
        return mPresenter;
    }

    protected Activity mAttachActivity;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mAttachActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        if (mView == null) {
            mView = createView();
        }

        if (mPresenter == null) {
            throw new NullPointerException("Presenter 不能为空");
        }
        if (mView == null) {
            throw new NullPointerException("View 不能为空");
        }
        mPresenter.attachView(mView);
    }

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
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();

    }


    @Override
    public void getDataFail(String failMag, int code) {
        ToastUtils.showToast(failMag);
    }

    @Override
    public void getDataFail(String failMsg) {
        ToastUtils.showToast(failMsg);
    }

//
//    private LoadingDialog2 loadingDialog2;
//    private LoadingDialog2 loadingDialogbyText;
//
//    protected void showLoadingDialog2() {
//        if (mAttachActivity.isFinishing()) return;
//        if (hasDialogFragmentIsVisible()) return;
//        if (loadingDialog2 == null) {
//            loadingDialog2 = new LoadingDialog2(getActivity(), getResources().getString(R.string.loading));
//        }
//        loadingDialog2.show();
//    }
//
//    protected void showLoadingDialogByText(String text) {
//        if (loadingDialogbyText == null) {
//            loadingDialogbyText = new LoadingDialog2(getActivity(), text);
//        }
//        loadingDialogbyText.show();
//    }
//
//    protected void hideLoadingDialog2() {
//        if (loadingDialog2 != null) {
//            loadingDialog2.close();
//        }
//    }
//
//    protected void hideLoadingDialogByText() {
//        if (loadingDialogbyText != null) {
//            loadingDialogbyText.close();
//        }
//    }

    /**
     * 统一的dialog显示框
     * 加try catch的原因是因为  showDialogFragment的场景很多  尤其实在异步线程中容易报错
     *
     * @param fragment
     */
    protected void showDialogFragment(DialogFragment fragment) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity == null) return;

        activity.showDialogFragment(fragment);
    }

    protected void hideDialogFragment() {
        BaseActivity activity = (BaseActivity) getActivity();
        if (activity == null) return;
        activity.hideDialogFragment();
    }

    protected boolean hasDialogFragmentIsVisible() {
        if (getActivity() == null) return false;
        return ((BaseActivity) getActivity()).mFragment != null && ((BaseActivity) getActivity()).mFragment.isVisible();
    }
}
