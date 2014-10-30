package com.android.twitter;

/**
 * .
 * <p/>
 * Twitterクライアント用定数クラス
 */

public final class TwitterParameter {

    /**
     * . コンストラクタ(privateにしてインスタンス化を防ぐ)
     */
    private TwitterParameter() {
    }

    /**
     * . 認証エラーコード.
     */
    public static final int CLIENT_ERROR = 401;

    /**
     * . HashMapが保持できる上限サイズ
     */
    public static final int MAPMAXSIZE = 15;

    /**
     * . レコード上限
     */
    public static final int RECORDMAX = 100;

    /**
     * プリファレンスのファイル名 .
     */
    public static final String PREFERENCES_NAME = "t4jdata";

    /**
     * . プリファレンスのキー名（アクセストークン）.
     */
    public static final String TOKEN_KEYNAME = "token";

    /**
     * . プリファレンスのキー名（アクセスシークレット）.
     */
    public static final String TOKENSECRET_KYENAME = "tokensecret";

    /**
     * 例外の種類を特定するための列挙型 .
     */
    public enum ERROR {
        /**
         * タイムライン取得エラー.
         */
        TWITTERERR,
        /**
         * ネットワークエラー.
         */
        NETWORKERR,
        /**
         * 認証エラー.
         */
        OAUTHERR
    }
}
