package com.android.twitter.views;


import android.content.Context;

import com.android.twitter.R;
import com.android.twitter.TwitterParameter;
import com.android.twitter.utils.ToastUtils;

public class ErrorToast {

    public static void show(Context context, TwitterParameter.ERROR error) {
        int redId = -1;
        switch (error) {
            case NETWORKERR:
                redId = R.string.errnet;
                break;
            case OAUTHERR:
                redId = R.string.badpin;
                break;
            case TWITTERERR:
                redId = R.string.gettlerr;
                break;
            default:
                return;
        }
        ToastUtils.show(context, redId);
    }
}
