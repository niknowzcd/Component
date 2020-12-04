package com.architect.component.skin;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.architect.component.R;

import java.io.File;

/**
 * 换肤测试类(这里主要是测试白天/夜间模式)
 * 注意需要在AndroidManifest下配置configChange = uiMode"
 * 不然模式切换会导致重建activity
 * <p>
 * https://blog.csdn.net/weixin_37011894/article/details/78921805
 *
 * 测试用的皮肤apk在根目录下的skin_2.apk
 * skin_2.apk可以更换名字，不用apk结尾也行，主要看皮肤文件中有没有对应的几个文件，AndroidManifest.xml;res包;resources.arsc资源映射表
 */
public class SkinTestActivity extends SkinActivity {

    private String skinPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skin_activity_main_2);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        skinPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "component_test.skin";
        System.out.println("skinPath >> " + skinPath);

        requestPermission();
    }

    public void switch_skin(View view) {
        switchSkin(skinPath);
    }

////夜间模式测试代码
//    public void dayOrNight(View view) {
//        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        switch (uiMode) {
//            case Configuration.UI_MODE_NIGHT_NO:
//                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                break;
//            case Configuration.UI_MODE_NIGHT_YES:
//                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    protected boolean openChangeSkin() {
        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        System.out.println("白天/夜间模式变化时回调,需要在AndroidManifest下配置configChange = uiMode");
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
    }
}
