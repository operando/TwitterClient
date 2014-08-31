package com.android.twitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * .
 *
 * DBキャッシュ用クラス
 *
 */
public class TwitterDbAdapter {

    /** . データベース名 */
    private static final String DATABASE_NAME = "datacache";

    /** . データベースのバージョン */
    private static final int DATABASE_VERSION = 1;

    /** . テーブル名 */
    private static final String TABLE_NAME = "image_cache";

    /** . カラム名 */
    private static final String COLUMN_URI = "uri";

    /** . カラム名 */
    private static final String COKUMN_ICON = "icon";

    /** . テーブル作成用のSQL */
    private static final String DATABASE_CREATE = "create table " + TABLE_NAME
            + "( " + COLUMN_URI + " text primary key, icon blob)";

    /** . コンテキスト */
    private final Context con;

    /** . DatabaseHelperオブジェクト */
    private DatabaseHelper mDbHelper;

    /** . SQLiteDatabaseオブジェクト */
    private SQLiteDatabase mDb;

    /**
     * .
     *
     * SQLiteOpenHelperを継承したクラス<br >
     * DB、テーブル作成時に使用
     *
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * .
         *
         * コンストラクタ
         *
         * @param context
         *            コンテキスト
         *
         */
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

    }

    /**
     * .
     *
     * コンストラクタ
     *
     * @param context
     *            コンテキスト
     */
    public TwitterDbAdapter(Context context) {
        con = context;
    }

    /**
     * .
     *
     * データベースのオープン処理
     *
     */
    public void open() {

        mDbHelper = new DatabaseHelper(con);
        mDb = mDbHelper.getWritableDatabase();
    }

    /**
     * .
     *
     * データベースのクローズ処理
     *
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * .
     *
     * テーブルへのINSERT処理
     *
     * @param uri
     *            アイコンへのURI
     * @param bytes
     *            アイコンをbyte変換したもの
     *
     * @return long ID
     */
    public long insert(String uri, byte[] bytes) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_URI, uri);
        initialValues.put(COKUMN_ICON, bytes);

        return mDb.insert(TABLE_NAME, null, initialValues);

    }

    /**
     * .
     *
     * URIが存在しているかをチェックする
     *
     * @param uri
     *            アイコンへのURI
     *
     * @return select文の結果
     */
    public Cursor selectall(String uri) {
        return mDb.query(TABLE_NAME, new String[] { COKUMN_ICON }, COLUMN_URI
                + " = '" + uri + "'", null, null, null, null);
    }

    /**
     * .
     *
     * テーブルのレコード削除処理
     *
     * @param count
     *            削除するレコード数
     * @return 処理結果
     */
    public boolean delete(int count) {

        return mDb.delete(TABLE_NAME, COLUMN_URI + " in(select uri from "
                + TABLE_NAME + " limit 0," + count + ")", null) > 0;
    }

    /**
     * .
     *
     * テーブルのレコード数を返す
     *
     * @return select文の結果
     */
    public Cursor countrecord() {
        return mDb.query(TABLE_NAME, new String[] { "count(*)" }, null, null,
                null, null, null);
    }

}
