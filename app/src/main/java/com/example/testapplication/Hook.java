package com.example.testapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
/**
 * Copyright (C) 2019, Xiaomi Inc. All rights reserved.
 * <p>
 * Description:
 *
 * @author yanghao6
 * @date 2019-02-25.
 */
public class Hook {

    public static final String TAG = Hook.class.getSimpleName();
    private static Context mContext;

    @SuppressLint("PrivateApi")
    public static void hookAMS(Context context) throws Exception {
        mContext = context;
        //获取ActivityManager的IActivityManagerSingleton(Singleton<IActivityManager>类型)字段
        Object iActivityManagerSingletonObject = ReflectUtil.
                getObjectByClassNameAndFieldName("android.app.ActivityManager",
                        "IActivityManagerSingleton", null);


        //获取IActivityManagerSingleton字段对象的mInstance字段，这个字段也就是ActivityManager.getService得到的对象
        final Field mInstance = ReflectUtil.getFieldByClassNameAndFieldName("android.util.Singleton","mInstance");

        //这里就是我们拿到的Ams,我们的StartActivity就是通过这个Ams调用的，我们要拦截的也是这个方法
        final Object mAms = mInstance.get(iActivityManagerSingletonObject);
        //这个构造出我们的代理对象
        AmsProxy proxy = new AmsProxy(mAms);
        //这里拿到接口
        final Class<?> aClass = Class.forName("android.app.IActivityManager");
        //使用Proxy生成代理对象
        final Object o = Proxy
                .newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{aClass}, proxy);
        //将我们构造出来的代理对象扔给系统
        mInstance.set(iActivityManagerSingletonObject, o);


        //获取到ActivityThread的单例对象
        final Class<?> activityThread = Class.forName("android.app.ActivityThread");
        final Field sCurrentActivityThread = activityThread.getDeclaredField("sCurrentActivityThread");
        sCurrentActivityThread.setAccessible(true);
        final Object aT = sCurrentActivityThread.get(null);


        //获取ActivityThread内部的mH字段
        final Field mH = activityThread.getDeclaredField("mH");
        mH.setAccessible(true);
        final Object o2 = mH.get(aT);
        final Field mCallback = Handler.class.getDeclaredField("mCallback");
        mCallback.setAccessible(true);

        AthCallbackProxy callbackProxy = new AthCallbackProxy((Handler) o2);
        mCallback.set(o2, callbackProxy);

    }

    static class AthCallbackProxy implements Handler.Callback {

        private Handler mHandler;

        public AthCallbackProxy(Handler handler) {
            mHandler = handler;
        }


        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100) {
                Object obj = msg.obj;
                try {
                    final Field intent = obj.getClass().getDeclaredField("intent");
                    intent.setAccessible(true);
                    final Intent realIntent = (Intent) intent.get(obj);
                    ComponentName componentName = realIntent.getParcelableExtra("realComponentName");
                    if (componentName != null) {
                        realIntent.setComponent(componentName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    static class AmsProxy implements InvocationHandler {

        private Object mObject;

        public AmsProxy(Object object) {
            mObject = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (args != null) {
                int index = -1;
                for (int i = 0; i < args.length - 1; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                    }
                }
                if (index != -1) {
                    Log.d(TAG, "invoke:");
                    final Intent intent = (Intent) args[index];
                    final ComponentName intentComponent = intent.getComponent();
                    ComponentName componentProxy = new ComponentName(mContext, ProxyActivity.class);
                    intent.setComponent(componentProxy);
                    intent.putExtra("realComponentName", intentComponent);
                }
            }
            return method.invoke(mObject, args);
        }
    }

    public static void hookInstrumentation(Context context)throws Exception{
        mContext = context;
        //先拿到系统的ActivityThread
        final Object sCurrentActivityThread = ReflectUtil
                .getObjectByClassNameAndFieldName("android.app.ActivityThread", "sCurrentActivityThread",null);
        //再拿到系统的mInstrumentation

        final Object mInstrumentation = ReflectUtil.getObjectByClassNameAndFieldName
                ("android.app.ActivityThread", "mInstrumentation", sCurrentActivityThread);
        final Method execStartActivity = ReflectUtil
                .getMethodByClassNameAndMethodName("android.app.Instrumentation", "execStartActivity"
                        , Context.class, IBinder.class,IBinder.class, Activity.class,Intent.class,int.class, Bundle.class);
        final Method newActivity = ReflectUtil.getMethodByClassNameAndMethodName("android.app.Instrumentation",
                "newActivity",ClassLoader.class,String.class,Intent.class);
        InstrumentationProxy proxy = new InstrumentationProxy(execStartActivity,newActivity,mInstrumentation);
        final Field mInstrumentation1 = ReflectUtil
                .getFieldByClassNameAndFieldName("android.app.ActivityThread", "mInstrumentation");
        mInstrumentation1.set(sCurrentActivityThread,proxy);
    }
    static class InstrumentationProxy extends Instrumentation{
        private Method mexecStartActivityMethod;
        private Method mNewActivityMethod;
        private Object mObject;

        public InstrumentationProxy(Method execStartActivityMethod,Method newActivityMethod, Object object) {
            mexecStartActivityMethod = execStartActivityMethod;
            mNewActivityMethod = newActivityMethod;
            Log.e(TAG, "InstrumentationProxy:mexecStartActivityMethod =  " + mexecStartActivityMethod.getName()+
                    ",mNewActivityMethod = " + mNewActivityMethod.getName());


            mObject = object;
        }

        public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                Intent intent, int requestCode, Bundle options)throws Exception{
            //在这里，就可以做我们的替换工作了
            return (ActivityResult) mexecStartActivityMethod.invoke(mObject,who, contextThread, token, target,
                    intent,  requestCode, options);
        }

        @Override
        public Activity newActivity(ClassLoader cl, String className, Intent intent)throws IllegalAccessException, IllegalArgumentException{
            //在这里，需要把目标Activity恢复过来
            try {
                return (Activity) mNewActivityMethod.invoke(mObject,cl,className,intent);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
