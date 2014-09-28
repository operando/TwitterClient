package com.android.twitter.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class PreferenceUtils {

    private PreferenceUtils() {
    }

    private static SharedPreferences getSharedPreferences(Context context, String preferenceName) {
        return context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context, String preferenceName) {
        return getSharedPreferences(context, preferenceName).edit();
    }

    public static void saveString(Context context, String preferenceName, String key, String value) {
        SharedPreferences.Editor editor = getEditor(context, preferenceName);
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String preferenceName, String key) {
        return getString(context, preferenceName, key, null);
    }

    public static String getString(Context context, String preferenceName, String key, String defaultValue) {
        return getSharedPreferences(context, preferenceName).getString(key, defaultValue);
    }

    public static void clear(Context context, String preferenceName) {
        getEditor(context, preferenceName).clear().apply();
    }

}
