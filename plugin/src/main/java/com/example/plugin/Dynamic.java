package com.example.plugin;

import android.content.Context;
import android.util.Log;

import com.example.mypluginlibrary.IDynamic;

public class Dynamic implements IDynamic {

    public static final String TAG = "Dynamic";

    @Override
    public String getStringForResId(Context context) {
        Log.e(TAG, "getStringForResId: 到这里了");
        return context.getResources().getString(R.string.myplugin_helloWorld);
    }
}
