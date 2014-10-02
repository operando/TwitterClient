package com.android.twitter.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class IntentUtils {
    private IntentUtils() {
    }

    public static void openBrowser(Context context, String uri) {
        openBrowser(context, Uri.parse(uri));
    }

    public static void openBrowser(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
