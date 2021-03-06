package com.architect.component.skin;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import com.architect.component.skin.core.CustomAppCompatViewInflater;
import com.architect.component.skin.core.ViewsMatch;
import com.architect.component.skin.utils.ActionBarUtils;
import com.architect.component.skin.utils.NavigationUtils;
import com.architect.component.skin.utils.StatusBarUtils;


/**
 * 重点看
 * 1.onCreate
 * 2.onCreateView
 * 3.applySkinForView
 */
public class SkinActivity extends AppCompatActivity {

    private CustomAppCompatViewInflater viewInflater;

    /**
     * 查看源代码可以看到,这么一段逻辑。
     *
     * if (mFactory2 != null) {
     *     view = mFactory2.onCreateView(parent, name, context, attrs);
     * } else if (mFactory != null) {
     *     view = mFactory.onCreateView(name, context, attrs);
     * } else {
     *     view = null;
     * }
     *
     * 即我们所看到的view都是通过mFactory2来生成出来的，也就是说我们可以通过给mFactory2赋值来控制view的生成
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LayoutInflaterCompat.setFactory2(layoutInflater, this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        if (openChangeSkin()) {
            if (viewInflater == null) {
                viewInflater = new CustomAppCompatViewInflater(context);
            }

            viewInflater.setName(name);
            viewInflater.setAttrs(attrs);
            View view = viewInflater.autoMatch();
            if (view != null) {
                return view;
            }
        }
        return super.onCreateView(name, context, attrs);
    }

    /**
     * @return 是否开启换肤，增加此开关是为了避免开发者误继承此父类，导致未知bug
     */
    protected boolean openChangeSkin() {
        return false;
    }


    protected void switchSkin(String skinPath){
        SkinManager.getInstance().loaderSkinResource(skinPath);

        applySkinForView(getWindow().getDecorView());
    }


    //夜间模式
    protected void setDayNightMode(@AppCompatDelegate.NightMode int nightMode) {

        final boolean isPost21 = Build.VERSION.SDK_INT >= 21;

        getDelegate().setLocalNightMode(nightMode);

        if (isPost21) {
            // 换状态栏
            StatusBarUtils.forStatusBar(this);
            // 换标题栏
            ActionBarUtils.forActionBar(this);
            // 换底部导航栏
            NavigationUtils.forNavigation(this);
        }

        View decorView = getWindow().getDecorView();
        applySkinForView(decorView);
    }

    /**
     * 回调接口 给具体控件换肤操作
     */
    protected void applySkinForView(View view) {
        if (view instanceof ViewsMatch) {
            ViewsMatch viewsMatch = (ViewsMatch) view;
            viewsMatch.skinnableView();
        }

        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                applySkinForView(parent.getChildAt(i));
            }
        }
    }
}
