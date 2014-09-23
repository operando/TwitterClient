package com.android.twitter.models;

import android.graphics.Bitmap;

import lombok.Data;

/**
 *
 * タイムラインの情報を保持するクラス. インスタンス１つに対して、タイムライン１件分の情報が保持される
 *
 */
@Data
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

}
