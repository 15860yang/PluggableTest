package com.example.testapplication;

import android.content.Context;

import java.io.File;

public class BaseFileUtil {

    private BaseFileUtil(){}

    private static volatile BaseFileUtil instance = null;

    public static BaseFileUtil getInstance() {
        if(instance == null){
            synchronized (BaseFileUtil.class){
                if(instance == null){
                    instance = new BaseFileUtil();
                }
            }
        }
        return instance;
    }

    private Context mContext = null;
    private String mApkName = null;
    private String mDexFileName = null;

    public void init(Context context,String apkFileName,String dexFileName){
        mApkName = apkFileName;
        mContext = context;
        mDexFileName = dexFileName;
    }

    public String getApkPath() {
        return getFileStreamPath(mApkName);
    }

    public File getOutPutApkFile(){
        return mContext.getFileStreamPath(mApkName);
    }

    public String getDexFilePath() {
        return getDir(mDexFileName);
    }

    /**
     * @param fileName 文件名
     * @return  /data/data/应用程序名/app_${fileName}
     */
    public String getDir(String fileName){
        return mContext.getDir(fileName,Context.MODE_PRIVATE).getAbsolutePath();
    }
    /**
     *
     * @return /data/data/com.lenovo/files/download
     */
    public String getFileStreamPath(String fileName){
        return mContext.getFileStreamPath(fileName).getPath();
    }

    public File createOutPutFile(String fileName) {
        return mContext.getFileStreamPath(fileName);
    }
}
