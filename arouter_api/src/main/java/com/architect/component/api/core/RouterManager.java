package com.architect.component.api.core;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.LruCache;

import com.architect.component.annotation.RouterBean;

import java.util.Map;

/**
 * 路由管理的核心类
 */
public final class RouterManager {

    private static RouterManager instance;
    private String path;
    private String group;

    private static final String PACKAGE_PATH_FOR_APT = "com.architect.component.apt.ARouter$$Group$$";

    //缓存类，提高效率
    private LruCache<String, ARouterLoadGroup> groupCache = new LruCache<>(163);
    private LruCache<String, ARouterLoadPath> pathCache = new LruCache<>(163);

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    /**
     * 对外暴露的入口
     *
     * @param path /app/MainActivity 结构的路径
     */
    public BundleManager build(String path) {
        //@ARouter注解中的path值，必须要以 / 开头（模仿阿里Arouter规范）
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("@ARouter注解中的path值，必须要以 / 开头");
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("@ARouter注解未按规范配置，如：/app/MainActivity");
        }

        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("@ARouter注解未按规范配置，如：/app/MainActivity");
        }

        this.group = finalGroup;
        this.path = path;

        return new BundleManager();
    }

    /**
     * 跳转的核心逻辑，由BundleManager触发
     * 通过包名找到apt生成的映射文件，再通过这些映射文件跳转
     */
    protected void navigation(Context context, BundleManager bundleManager) {
        String groupClassName = PACKAGE_PATH_FOR_APT + group;

        try {
            ARouterLoadGroup mLoadGroup = groupCache.get(this.group);
            if (mLoadGroup == null) {
                Class<?> clazz = Class.forName(groupClassName);
                mLoadGroup = (ARouterLoadGroup) clazz.newInstance();
                groupCache.put(group, mLoadGroup);
            }

            if (mLoadGroup.loadGroup().isEmpty()) {
                throw new RuntimeException("路由Group读取失败");
            }

            ARouterLoadPath mLoadPath = pathCache.get(path);
            if (mLoadPath == null) {
                Map<String, Class<? extends ARouterLoadPath>> map = mLoadGroup.loadGroup();
                Class<? extends ARouterLoadPath> clazz = map.get(group);
                if (clazz != null) mLoadPath = clazz.newInstance();
                pathCache.put(path, mLoadPath);
            }

            if (mLoadPath == null || mLoadPath.loadPath().isEmpty()) {
                throw new RuntimeException("路由Paths读取失败");
            }

            RouterBean bean = mLoadPath.loadPath().get(path);
            if (bean == null) {
                throw new RuntimeException("路由RouterBean读取失败");
            }

            //最终的跳转逻辑
            jump(context, bean, bundleManager);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jump(Context context, RouterBean bean, BundleManager bundleManager) {
        switch (bean.getType()) {
            case Activity:
                Intent intent = new Intent(context, bean.getClazz());
                intent.putExtras(bundleManager.getBundle());
                context.startActivity(intent);
                break;

        }
    }
}
