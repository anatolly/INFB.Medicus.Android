package com.intrafab.medicus.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

/**
 * Created by Artemiy Terekhov on 10.04.2015.
 */
public class Logger {
    private static String mApplicationTag = "";
    private static boolean mRelease = false;

    public static void setApplicationTag(String applicationTag) {
        mApplicationTag = applicationTag;
    }

    public static void setRelease(boolean isRelease) {
        mRelease = isRelease;
    }

    public static void d(String tag, String message) {
        if (!mRelease)
            Log.d(mApplicationTag, formatMessage(tag, message));
    }

    public static void d(String tag, String message, Throwable throwable) {
        if (!mRelease)
            Log.d(mApplicationTag, formatMessage(tag, message), throwable);
    }

    public static void d(String tag, Intent intent) {
        if (mRelease || intent == null)
            return;

        d(tag, "Intent action = " + intent.getAction());

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Set<String> keys = extras.keySet();
            if (keys == null || (keys != null && keys.isEmpty())) {
                d(tag, " extras: none");
            } else {
                d(tag, " extras:");
                for (String key : keys) {
                    d(tag, " " + key + " = " + extras.get(key));
                }
            }
        }
    }

    public static void i(String tag, String message) {
        if (!mRelease)
            Log.i(mApplicationTag, formatMessage(tag, message));
    }

    public static void i(String tag, String message, Throwable throwable) {
        if (!mRelease)
            Log.i(mApplicationTag, formatMessage(tag, message), throwable);
    }

    public static void e(String tag, String error) {
        Log.e(mApplicationTag, formatMessage(tag, error));
    }

    public static void e(String tag, String error, Throwable throwable) {
        Log.e(mApplicationTag, formatMessage(tag, error), throwable);
    }

    public static void w(String tag, String message) {
        Log.w(mApplicationTag, formatMessage(tag, message));
    }

    public static void w(String tag, String message, Throwable throwable) {
        Log.w(mApplicationTag, formatMessage(tag, message), throwable);
    }

    public static void wtf(String tag, String message) {
        if (!mRelease)
            Log.wtf(mApplicationTag, formatMessage(tag, message));
    }

    public static void wtf(String tag, String message, Throwable throwable) {
        if (!mRelease)
            Log.wtf(mApplicationTag, formatMessage(tag, message), throwable);
    }

    /**
     * Formats a log message
     *
     * @param prefix  message prefix, typically the requesting class name
     * @param message message to write
     * @return formatted string of the log message
     */
    private static String formatMessage(String prefix, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("[" + prefix + "] ").append(message);
        return builder.toString();
    }
}
