package com.architect.component.skin;

import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.core.content.res.ResourcesCompat;

import java.lang.reflect.Method;

/**
 * 皮肤管理器
 * 加载设备内的资源信息，如果是网络资源，先下载再加载
 */
public class SkinManager {

    private static SkinManager instance;
    private Application application;
    private Resources defaultResources;    //默认资源

    private Resources skinResources;       //外部的资源
    private String skinPackageName;        //皮肤包包名

    private boolean loadSkipSuccess;       //皮肤包是否加载成功

    private SkinManager(Application application) {
        this.application = application;
        defaultResources = application.getResources();
    }

    public static SkinManager init(Application application) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }
        return instance;
    }

    public static SkinManager getInstance() {
        return instance;
    }

    public boolean loadSuccess() {
        return loadSkipSuccess;
    }

    public boolean isDefaultSkin() {
        return !loadSkipSuccess;
    }

    public void loaderSkinResource(String skinPath) {
        if (TextUtils.isEmpty(skinPath)) return;

        try {
            //需要通过反射的方式调用对应的方法
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            method.setAccessible(true);
            method.invoke(assetManager, skinPath);

            //创建皮肤包的资源管理器
            skinResources = new Resources(assetManager, defaultResources.getDisplayMetrics(), defaultResources.getConfiguration());
            skinPackageName = application.getPackageManager()
                    .getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES).packageName;
            loadSkipSuccess = !TextUtils.isEmpty(skinPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            loadSkipSuccess = false;
        }
    }

    /**
     * 可以查看 resources.arsc资源映射表
     * 通过ID值获取资源 Name 和 Type
     *
     * @param resourceId
     * @return
     */
    private int getSkinResourceId(int resourceId) {
        //外部皮肤加载失败，展示默认的
        if (!loadSkipSuccess) return resourceId;

        //根据默认的资源id获取资源文件的name和type，再通过这两者获取皮肤包的资源id。
        //前提是默认的和外的皮肤包的资源文件保持命名和类型一致
        String resName = defaultResources.getResourceEntryName(resourceId);
        String resType = defaultResources.getResourceTypeName(resourceId);

        int skinResourceId = skinResources.getIdentifier(resName, resType, skinPackageName);
        loadSkipSuccess = skinResourceId != 0;
        return skinResourceId == 0 ? resourceId : skinResourceId;
    }


    public int getColor(int resourced) {
        int resId = getSkinResourceId(resourced);
        return loadSkipSuccess ? skinResources.getColor(resId) : defaultResources.getColor(resId);
    }

    public ColorStateList getColorStateList(int resourceId) {
        int ids = getSkinResourceId(resourceId);
        return loadSkipSuccess ? ResourcesCompat.getColorStateList(skinResources, ids, null) : ResourcesCompat.getColorStateList(defaultResources, ids, null);
    }

    // mipmap和drawable统一用法（待测）
    public Drawable getDrawableOrMipMap(int resourceId) {
        int ids = getSkinResourceId(resourceId);
        return loadSkipSuccess ? ResourcesCompat.getDrawable(skinResources, ids, null) : ResourcesCompat.getDrawable(defaultResources, ids, null);
    }

    public String getString(int resourceId) {
        int ids = getSkinResourceId(resourceId);
        return loadSkipSuccess ? skinResources.getString(ids) : defaultResources.getString(ids);
    }

    // 返回值特殊情况：可能是color / drawable / mipmap
    public Object getBackgroundOrSrc(int resourceId) {
        // 需要获取当前属性的类型名Resources.getResourceTypeName(resourceId)再判断
        String resourceTypeName = defaultResources.getResourceTypeName(resourceId);

        switch (resourceTypeName) {
            case "color":
                return getColor(resourceId);

            case "mipmap": // drawable / mipmap
            case "drawable":
                return getDrawableOrMipMap(resourceId);
        }
        return null;
    }

    // 获得字体
    public Typeface getTypeface(int resourceId) {
        // 通过资源ID获取资源path，参考：resources.arsc资源映射表
        String skinTypefacePath = getString(resourceId);
        // 路径为空，使用系统默认字体
        if (TextUtils.isEmpty(skinTypefacePath)) return Typeface.DEFAULT;
        return loadSkipSuccess ? Typeface.createFromAsset(skinResources.getAssets(), skinTypefacePath)
                : Typeface.createFromAsset(defaultResources.getAssets(), skinTypefacePath);
    }
}
