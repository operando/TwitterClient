package com.android.twitter.utils;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtils {

    private ToastUtils() {
    }

    public static void show(Context context, int resId) {
        show(context, context.getString(resId));
    }

    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
