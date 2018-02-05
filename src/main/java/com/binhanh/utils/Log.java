package com.binhanh.utils;

/**
 * Created by Nguyen Duc Luc on 11/18/2016.
 * Hiện thị log với TAG = Binh Anh
 */

public class Log {
    public static final String TAG = "Binh Anh";

    public static void e(String log){
        android.util.Log.e(TAG, log);
    }

    public static void e(String log, Exception e){
        android.util.Log.e(TAG, log, e);
    }

    public static void d(String log){
        android.util.Log.d(TAG, log);
    }

    public static void d(String log, Exception e){
        android.util.Log.d(TAG, log, e);
    }

    public static void d(Exception e){
        android.util.Log.d(TAG, "Exception", e);
    }

    public static void w(String log){
        android.util.Log.w(TAG, log);
    }

    public static void w(String log, Exception e){
        android.util.Log.w(TAG, log, e);
    }

    public static void w(Exception e){
        android.util.Log.w(TAG, "com.binhanh.utils - Exception: ", e);
    }
}
