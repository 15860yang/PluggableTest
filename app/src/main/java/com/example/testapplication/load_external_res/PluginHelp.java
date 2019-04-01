package com.example.testapplication.load_external_res;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.example.testapplication.BaseFileUtil;
import com.example.testapplication.MainActivity;

import java.lang.reflect.Method;

public class PluginHelp {



    public static void loadResources(Context context) {
        MainActivity activity = (MainActivity) context;

        String dexPath = BaseFileUtil.getInstance().getApkPath();

        try {
            //反射调用 addAssetPath 方法 将Res资源路径添加进去
            AssetManager assetManager = context.getAssets();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            //反射调用系统的 addAssetPath 方法来传入我们外部的资源路径
            addAssetPath.invoke(assetManager, dexPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Resources mResources = new Resources(activity.getAssets(), activity.getResources().getDisplayMetrics(),
                activity.getResources().getConfiguration());
        activity.setResources(mResources);

        Resources.Theme mTheme = mResources.newTheme();

        activity.setTheme(mTheme);
        mTheme.setTo(activity.getTheme());
    }
}
