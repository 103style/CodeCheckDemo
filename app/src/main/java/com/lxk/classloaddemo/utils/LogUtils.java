package com.lxk.classloaddemo.utils;

import android.util.Log;

import com.lxk.classloaddemo.BuildConfig;

/**
 * @author https://github.com/103style
 * @date 2019/9/18 10:54
 */
public class LogUtils {

    public static void d(String tag, String msg) {
        print(Log.DEBUG, tag, msg);
    }

    public static void e(String tag, String msg) {
        print(Log.ERROR, tag, msg);
    }

    public static void v(String tag, String msg) {
        print(Log.VERBOSE, tag, msg);
    }

    public static void w(String tag, String msg) {
        print(Log.WARN, tag, msg);
    }

    public static void i(String tag, String msg) {
        print(Log.INFO, tag, msg);
    }


    private static void print(int priority, String tag, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        switch (priority) {
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.INFO:
            default:
                Log.i(tag, msg);
                break;
        }
    }
}
