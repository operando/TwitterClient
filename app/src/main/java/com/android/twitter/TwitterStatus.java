package com.android.twitter;

import android.graphics.Bitmap;

/**
 *
 * タイムラインの情報を保持するクラス. インスタンス１つに対して、タイムライン１件分の情報が保持される
 *
 */
public class TwitterStatus {

    /** .　アカウントIDを保持するために使用.　 */
    private String id;
    /** ツイート日時を保持するために使用. */
    private String date;
    /** つぶやきを保持するために使用. */
    private String tl;
    /** アイコンを保持するために使用. */
    private Bitmap icon;
    /** . ユーザー名を保持するために使用.　 */
    private String name;

    /**
     * 　 　アカウントIDをフィールドにセットする.
     *
     * @param argumentId
     *            アカウントID
     */
    public void setId(String argumentId) {
        this.id = argumentId;
    }

    /**
     *
     * ツイート日時をフィールドにセットする.
     *
     * @param argumentDate
     *            ツイート日時
     */
    public void setDate(String argumentDate) {
        this.date = argumentDate;
    }

    /**
     *
     * つぶやきをフィールドにセットする.
     *
     * @param argumentTl
     *            つぶやき
     */
    public void setTl(String argumentTl) {
        this.tl = argumentTl;
    }

    /**
     *
     * アイコンをフィールドにセットする.
     *
     * @param argumentIcon
     *            アイコン
     */
    public void setIcon(Bitmap argumentIcon) {
        this.icon = argumentIcon;
    }

    /**
     *
     * ユーザー名をフィールドにセットする.
     *
     * @param argumentName
     *            ユーザー名
     */
    public void setName(String argumentName) {
        this.name = argumentName;
    }

    /**
     *
     * アカウントIDを返す.
     *
     * @return id アカウントID
     */
    public String getId() {
        return id;
    }

    /**
     *
     * ツイート日時を返す.
     *
     * @return date ツイート日時
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * つぶやきを返す.
     *
     * @return id つぶやき
     */
    public String getTl() {
        return tl;
    }

    /**
     *
     * アイコンを返す.
     *
     * @return icon アイコン
     */
    public Bitmap getIcon() {
        return icon;
    }

    /**
     *
     * ユーザー名を返す.
     *
     * @return name ユーザー名
     */
    public String getName() {
        return name;
    }

}
