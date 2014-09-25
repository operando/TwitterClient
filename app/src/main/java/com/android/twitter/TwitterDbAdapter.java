package com.android.twitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.twitter.databases.TwitterClientHelper;
import com.android.twitter.models.TwitterIconCache;

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

    /**
     * .
     * <p/>
     * テーブルへのINSERT処理
     *
     * @param uri   アイコンへのURI
     * @param bytes アイコンをbyte変換したもの
     * @return long ID
     */
    public long insert(String uri, byte[] bytes) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(TwitterIconCache.TwitterIconCacheColumns.URI, uri);
        initialValues.put(TwitterIconCache.TwitterIconCacheColumns.ICON, bytes);

        return mDb.insert(TwitterIconCache.TABLE_NAME, null, initialValues);

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
        return mDb.delete(TwitterIconCache.TABLE_NAME, TwitterIconCache.TwitterIconCacheColumns.URI + " in(select " + TwitterIconCache.TwitterIconCacheColumns.URI + " from "
                + TwitterIconCache.TABLE_NAME + " limit 0,?)", new String[]{Long.toString(count)}) > 0;
    }

    /**
     * .
     * <p/>
     * テーブルのレコード数を返す
     *
     * @return TwitterIconCacheテーブルのレコード数
     */
    public long getRecordCount() {
        long recordCount = 0;
        Cursor c = mDb.query(TwitterIconCache.TABLE_NAME, new String[]{"COUNT(*)"}, null, null,
                null, null, null);
        if (c.moveToNext()) {
            recordCount = c.getLong(0);
        }
        return recordCount;
    }

}
