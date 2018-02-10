package com.example.luegg.oa.base;

import android.util.Log;

public class Logger {

    private static final int LOG_VERB = 0;
    private static final int LOG_DEBUG = 1;
    private static final int LOG_INFO = 10;
    private static final int LOG_WARNING = 20;
    private static final int LOG_ERROR = 30;
    private static final int LOG_CLOSE = 100;
    private static int LOG_LEVEL = LOG_DEBUG;

    public static void setLevel(int levle) {
        LOG_LEVEL = levle;
    }

    public static void i(String tag, String msg){
        if (LOG_LEVEL <= LOG_INFO){
            Log.i(tag, msg);
        }
    }
    public static void v(String tag, String msg){
        if (LOG_LEVEL <= LOG_VERB){
            Log.v(tag, msg);
        }
    }
    public static void d(String tag, String msg){
        if (LOG_LEVEL <= LOG_DEBUG){
            Log.d(tag, msg);
        }
    }
    public static void w(String tag, String msg){
        if (LOG_LEVEL <= LOG_WARNING){
            Log.w(tag, msg);
        }
    }
    public static void e(String tag, String msg){
        if (LOG_LEVEL <= LOG_ERROR){
            if (Constant.SHOW_TOAST_WHILE_ERROR) {
                CommonUtil.showToast(msg);
            }
            Log.e(tag, msg);
        }
    }
}
