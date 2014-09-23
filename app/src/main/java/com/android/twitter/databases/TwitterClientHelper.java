package com.android.twitter.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.twitter.models.TwitterIconCache;

public class TwitterClientHelper extends SQLiteOpenHelper {

    /**
     * . データベース名
     */
    private static final String DATABASE_NAME = "datacache.db";

    /**
     * . データベースのバージョン
     */
    private static final int DATABASE_VERSION = 1;


    /**
     * . テーブル作成用のSQL
     */
    private static final String DATABASE_CREATE = "create table " + TwitterIconCache.TABLE_NAME
            + "( " + TwitterIconCache.TwitterIconCacheColumns.URI + " text primary key, " + TwitterIconCache.TwitterIconCacheColumns.ICON + " blob)";

    /**
     * .
     * <p/>
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public TwitterClientHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS " + TwitterIconCache.TABLE_NAME);
        onCreate(db);
    }
}
