package com.android.twitter;

import android.app.Application;

import java.io.IOException;
import java.util.Properties;

public class TwitterClientApplication extends Application {

    public static String consumerkey;
    public static String consumersecret;

    @Override
    public void onCreate() {
        super.onCreate();
        Properties p = new Properties();
        try {
            p.load(getResources().getAssets().open("consumerinfo.properties"));
            consumerkey = p.getProperty("consumerkey");
            consumersecret = p.getProperty("consumersecret");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
