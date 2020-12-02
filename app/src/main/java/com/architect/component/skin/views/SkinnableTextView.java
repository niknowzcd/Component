package com.architect.component.skin.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.architect.component.R;
import com.architect.component.skin.SkinManager;
import com.architect.component.skin.core.ViewsMatch;
import com.architect.component.skin.model.AttrsBean;


/**
 * 自定义的属性中 custom_typeface 至关重要，需要先定义在theme下。
 * 不然TypedArray无法通过getResourceId()来获取资源id，从而导致后续换肤的失败
 */
public class SkinnableTextView extends AppCompatTextView implements ViewsMatch {

    private AttrsBean attrsBean;

    public SkinnableTextView(Context context) {
        this(context, null);
    }

    public SkinnableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SkinnableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();

        // 根据自定义属性，匹配控件属性的类型集合，如：background + textColor
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableTextView,
                defStyleAttr, 0);
        // 存储到临时JavaBean对象
        attrsBean.saveViewResource(typedArray, R.styleable.SkinnableTextView);
        typedArray.recycle();
    }

    @Override
    public void skinnableView() {
        // 根据自定义属性，获取styleable中的background属性
        int key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_background];
        // 根据styleable获取控件某属性的resourceId
        int backgroundResourceId = attrsBean.getViewResource(key);
        if (backgroundResourceId > 0) {
            if (SkinManager.getInstance().loadSuccess()) {
                Object skinResourceId = SkinManager.getInstance().getBackgroundOrSrc(backgroundResourceId);
                if (skinResourceId instanceof Integer) {
                    int color = (int) skinResourceId;
                    setBackgroundColor(color);
                } else {
                    Drawable drawable = (Drawable) skinResourceId;
                    setBackgroundDrawable(drawable);
                }
            } else {
                Drawable drawable = ContextCompat.getDrawable(getContext(), backgroundResourceId);
                setBackgroundDrawable(drawable);
            }
        }

        // 根据自定义属性，获取styleable中的textColor属性
        key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_textColor];
        int textColorResourceId = attrsBean.getViewResource(key);
        if (textColorResourceId > 0) {
            if (SkinManager.getInstance().loadSuccess()) {
                ColorStateList color = SkinManager.getInstance().getColorStateList(textColorResourceId);
                setTextColor(color);
            } else {
                ColorStateList color = ContextCompat.getColorStateList(getContext(), textColorResourceId);
                setTextColor(color);
            }
        }

        // 根据自定义属性，获取styleable中的字体 custom_typeface 属性
        key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_custom_typeface];
        int textTypefaceResourceId = attrsBean.getViewResource(key);
        if (textTypefaceResourceId > 0) {
            if (SkinManager.getInstance().loadSuccess()) {
                setTypeface(SkinManager.getInstance().getTypeface(textTypefaceResourceId));
            } else {
                setTypeface(Typeface.DEFAULT);
            }
        }
    }
}
