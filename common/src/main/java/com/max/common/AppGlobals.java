package com.max.common;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: maker
 * @date: 2020/12/1 10:39
 * @description:
 */
public class AppGlobals {
    private static Application sApplication;

    public static Application getApplication(){
        if(sApplication == null){
            try {
                Method method = Class.forName("android.app.ActivityThread").getMethod("currentApplication");
                sApplication = (Application) method.invoke(null,null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return sApplication;
    }
} 