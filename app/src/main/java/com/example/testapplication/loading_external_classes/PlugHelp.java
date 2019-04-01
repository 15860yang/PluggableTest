package com.example.testapplication.loading_external_classes;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.example.mypluginlibrary.IBean;
import com.example.mypluginlibrary.ICallBack;
import com.example.testapplication.BaseFileUtil;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PlugHelp {

    /**
     * 将指定目录下的文件写入到硬盘
     * @param context
     * @param apkName
     */
    public static String WriteFilesToHardDisk(Context context,String apkName){
        String path = null;

        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            //从资源管理器打开这个文件流
            is = assetManager.open(apkName);
            //获取改文件的文件句柄
            File outPutFile = BaseFileUtil.getInstance().createOutPutFile(apkName);
            fos = new FileOutputStream(outPutFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0){
                fos.write(buffer,0,count);
            }
            fos.flush();
            Log.e(TAG, "WriteFilesToHardDisk: 成功写入硬盘 文件夹目录 = "+ outPutFile.getPath());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closeSilently(is);
            closeSilently(fos);
        }
        return null;
    }

    /**
     * 为硬盘中指定的apk文件创建类加载器
     * @param context
     * @param apkPath
     */
    public static DexClassLoader createClassLoader(Context context, String apkPath,String dexPath){
        /**
         *  参数表
         *  @param dexPath 指目标类所在的APK或jar文件的路径.类装载器将从该路径中寻找指定的目标类,
         *                  该类必须是APK或jar的全路径.如果要包含多个路径,路径之间必须使用特定的分割符分隔,
         *                 特定的分割符可以使用System.getProperty(“path.separtor”)获得.
         *  @param dexOutputDir 由于dex文件被包含在APK或者Jar文件中,因此在装载目标类之前需要先从APK或Jar
         *                     文件中解压出dex文件,该参数就是制定解压出的dex文件存放的路径.在Android系统中,
         *                     一个应用程序一般对应一个Linux用户id,应用程序仅对属于自己的数据目录路径有写的权限,
         *                     因此,该参数可以使用该程序的数据路径.
         *  @param libPath 指目标类中所使用的C/C++库存放的路径
         *  @param parent  父加载器，遵守双亲委派模式
         *
         */
        Log.e(TAG, "createClassLoader: apk文件路径 = " + apkPath + ",,输出dex文件路径 = " + dexPath);

        return new DexClassLoader(apkPath, dexPath, null, context.getClassLoader());
    }

    private static String TAG = "test";
    public static void getClassInstanceByRefect(Context application,DexClassLoader dexClassLoader,String classFullPath) {
        //然后加载完成就可以通过反射获取这个类的实例了
        Class mLoadClassBean;
        try {
            mLoadClassBean = dexClassLoader.loadClass(classFullPath);
            IBean beanObject = (IBean) mLoadClassBean.newInstance();

            Toast.makeText(application, beanObject.getName(new ICallBack() {
                @Override
                public void doThing(String s) {
                    Log.e(TAG, "doThing: " + s);
                }
            }), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable e) {
            // ignore
        }
    }

}
