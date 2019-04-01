package com.example.testapplication.baseHook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * Copyright (C) 2019, Xiaomi Inc. All rights reserved.
 * <p>
 * Description:
 *
 * @author yanghao6
 * @date 2019-02-26.
 */
public class ReflectUtil {

    public static Field getFieldByClassNameAndFieldName(String className,String fieldName)throws Exception{
        final Class<?> aClass = Class.forName(className);
        final Field declaredField = aClass.getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        return declaredField;
    }

    public static Object getObjectByClassNameAndFieldName(String className,String fieldName,Object obj)throws Exception{
        Field field = getFieldByClassNameAndFieldName(className,fieldName);
        return field.get(obj);
    }

    public static Method getMethodByClassNameAndMethodName(String className,String methodName,Class<?> ...args)throws Exception{
        final Class<?> aClass = Class.forName(className);
        final Method declaredMethod = aClass.getMethod(methodName,args);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }


}
