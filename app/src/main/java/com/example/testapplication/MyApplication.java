package com.example.testapplication;

import android.app.Application;

/**
 * Copyright (C) 2019, Xiaomi Inc. All rights reserved.
 * <p>
 * Description:
 *
 * @author yanghao6
 * @date 2019-02-25.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
//            Hook.hookInstrumentation(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
