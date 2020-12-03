package com.architect.component.skin.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatViewInflater;

import com.architect.component.skin.views.SkinnableButton;
import com.architect.component.skin.views.SkinnableImageView;
import com.architect.component.skin.views.SkinnableLinearLayout;
import com.architect.component.skin.views.SkinnableTextView;


/**
 * 自定义控件加载器（可以考虑该类不被继承）
 */
public final class CustomAppCompatViewInflater extends AppCompatViewInflater {

    private String name; // 控件名
    private Context context; // 上下文
    private AttributeSet attrs; // 某控件对应所有属性

    public CustomAppCompatViewInflater(@NonNull Context context) {
        this.context = context;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttrs(AttributeSet attrs) {
        this.attrs = attrs;
    }

    /**
     * @return 自动匹配控件名，并初始化控件对象
     */
    public View autoMatch() {
        View view = null;
        switch (name) {
            case "LinearLayout":
                view = new SkinnableLinearLayout(context, attrs);
                break;
            case "TextView":
                view = new SkinnableTextView(context, attrs);
                break;
            case "ImageView":
                view = new SkinnableImageView(context, attrs);
                break;
            case "Button":
                view = new SkinnableButton(context, attrs);
                break;
        }
        return view;
    }
}
