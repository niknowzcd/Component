package com.architect.component.skin.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.architect.component.R;
import com.architect.component.skin.SkinManager;
import com.architect.component.skin.core.ViewsMatch;
import com.architect.component.skin.model.AttrsBean;


public class SkinnableImageView extends AppCompatImageView implements ViewsMatch {

    private AttrsBean attrsBean;

    public SkinnableImageView(Context context) {
        this(context, null);
    }

    public SkinnableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();

        // 根据自定义属性，匹配控件属性的类型集合，如：src
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableImageView,
                defStyleAttr, 0);
        // 存储到临时JavaBean对象
        attrsBean.saveViewResource(typedArray, R.styleable.SkinnableImageView);
        typedArray.recycle();
    }

    @Override
    public void skinnableView() {
        // 根据自定义属性，获取styleable中的src属性
        int key = R.styleable.SkinnableImageView[R.styleable.SkinnableImageView_android_src];
        // 根据styleable获取控件某属性的resourceId
        int backgroundResourceId = attrsBean.getViewResource(key);
        if (backgroundResourceId > 0) {
            // 是否默认皮肤
            if (SkinManager.getInstance().isDefaultSkin()) {
                // 兼容包转换
                Drawable drawable = ContextCompat.getDrawable(getContext(), backgroundResourceId);
                setImageDrawable(drawable);
            } else {
                // 获取皮肤包资源
                Object skinResourceId = SkinManager.getInstance().getBackgroundOrSrc(backgroundResourceId);
                // 兼容包转换
                if (skinResourceId instanceof Integer) {
                    int color = (int) skinResourceId;
                    setImageResource(color);
                } else {
                    Drawable drawable = (Drawable) skinResourceId;
                    setImageDrawable(drawable);
                }
            }
        }
    }
}
