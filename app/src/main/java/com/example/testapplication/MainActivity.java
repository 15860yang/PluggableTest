package com.example.testapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.mypluginlibrary.IDynamic;
import com.example.testapplication.load_external_res.PluginHelp;
import com.example.testapplication.loading_external_classes.PlugHelp;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {


    private String apkName = "plugin-debug.apk";

    public Resources mResources;
    public Resources.Theme mTheme;

    public static final String TAG = "MainActivity";

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);

        //初始化文件管理类
        BaseFileUtil.getInstance().init(context,apkName,"dex");
        try {
            //将assets文件夹下的指定文件写入硬盘
            //将 apkName 这个文件写入硬盘
            //返回apk文件的路径
            PlugHelp.WriteFilesToHardDisk(context, apkName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将apk文件的dex问价加载到固定文件夹
        String dexPath = BaseFileUtil.getInstance().getDexFilePath();

        String apkPath = BaseFileUtil.getInstance().getApkPath();

        DexClassLoader classLoader = PlugHelp.createClassLoader(this,apkPath,dexPath);

        //加载Res资源
        PluginHelp.loadResources(this);

        try {
            Log.e(TAG, "onCreate:开始创建类");
            Class<?> aClass = classLoader.loadClass("com.example.plugin.Dynamic");
            IDynamic o = (IDynamic) aClass.newInstance();
            String stringForResId = o.getStringForResId(this);
            Log.e(TAG, "onCreate: "+stringForResId );
            Log.e(TAG, "onCreate: 调用成功" );
            Log.e(TAG, "onCreate: 本应用的字符串 = "+ getResources().getString(R.string.app_name) );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        if(mResources == null){
            return super.getResources();
        }
        Log.e(TAG, "getResources: 在这里返回 Resources");
        return mResources;
    }

    public void setResources(Resources resources) {
        mResources = resources;
    }

    @Override
    public Resources.Theme getTheme() {
        if(mTheme == null){
            return super.getTheme();
        }
        Log.e(TAG, "getTheme: 在这里返回 Resources.Theme");
        return mTheme;
    }

    public void setTheme(Resources.Theme theme) {
        mTheme = theme;
    }
}
