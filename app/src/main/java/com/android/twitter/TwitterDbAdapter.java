package com.android.twitter;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.android.twitter.databases.TwitterClientHelper;
import com.android.twitter.models.TwitterIconCache;

import java.util.Map;

/**
 * .
 * <p/>
 * DBキャッシュ用クラス
 */
public class TwitterDbAdapter {

    /**
     * . コンテキスト
     */
    private final Context con;

    /**
     * . DatabaseHelperオブジェクト
     */
    private TwitterClientHelper mDbHelper;

    /**
     * . SQLiteDatabaseオブジェクト
     */
    private SQLiteDatabase mDb;

    /**
     * .
     * <p/>
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public TwitterDbAdapter(Context context) {
        con = context;
    }

    /**
     * .
     * <p/>
     * データベースのオープン処理
     */
    public void open() {
        mDbHelper = new TwitterClientHelper(con);
        mDb = mDbHelper.getWritableDatabase();
    }

    /**
     * .
     * <p/>
     * データベースのクローズ処理
     */
    public void close() {
        mDbHelper.close();
    }

    public void insertsIcon(Map<String, byte[]> icons) {
        SQLiteStatement stat = mDb.compileStatement("INSERT INTO " + TwitterIconCache.TABLE_NAME + "(" +
                TwitterIconCache.TwitterIconCacheColumns.URI + "," +
                TwitterIconCache.TwitterIconCacheColumns.ICON + ") VALUES(?,?)");
        try {
            mDb.beginTransaction();
            for (Map.Entry<String, byte[]> icon : icons.entrySet()) {
                stat.bindString(1, icon.getKey());
                stat.bindBlob(2, icon.getValue());
                stat.executeInsert();
            }
        } finally {
            mDb.setTransactionSuccessful();
        }
    }

    /**
     * .
     * <p/>
     * URIが存在しているかをチェックする
     *
     * @param uri アイコンへのURI
     * @return select文の結果
     */
    public Cursor selectall(String uri) {
        return mDb.query(TwitterIconCache.TABLE_NAME, new String[]{TwitterIconCache.TwitterIconCacheColumns.ICON}, TwitterIconCache.TwitterIconCacheColumns.URI
                + " = '?'", new String[]{uri}, null, null, null);
    }

    /**
     * .
     * <p/>
     * テーブルのレコード削除処理
     *
     * @param count 削除するレコード数
     * @return 処理結果
     */
    public boolean delete(long count) {
        boolean isDelete = false;
        try {
            mDb.beginTransaction();
            isDelete = mDb.delete(TwitterIconCache.TABLE_NAME, TwitterIconCache.TwitterIconCacheColumns.URI + " in(select " + TwitterIconCache.TwitterIconCacheColumns.URI + " from "
                    + TwitterIconCache.TABLE_NAME + " limit 0,?)", new String[]{Long.toString(count)}) > 0;
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        return isDelete;
    }

    /**
     * .
     * <p/>
     * テーブルのレコード数を返す
     *
     * @return TwitterIconCacheテーブルのレコード数
     */
    public long getRecordCount() {
        return DatabaseUtils.queryNumEntries(mDb, TwitterIconCache.TABLE_NAME);
    }

}
