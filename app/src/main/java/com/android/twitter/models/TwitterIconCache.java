package com.android.twitter.models;

import android.provider.BaseColumns;

public class TwitterIconCache {

    /**
     * . テーブル名
     */
    public static final String TABLE_NAME = "image_cache";

    public static class TwitterIconCacheColumns implements BaseColumns {

        /**
         * . カラム名
         */
        public static final String URI = "uri";

        /**
         * . カラム名
         */
        public static final String ICON = "icon";
    }
}
