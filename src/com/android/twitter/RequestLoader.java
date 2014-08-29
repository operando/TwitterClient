package com.android.twitter;

import twitter4j.auth.RequestToken;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

/**
 * .
 *
 *
 * RequestTask用のLoaderCallbacksを実装したクラス
 *
 */
public class RequestLoader implements LoaderCallbacks<RequestToken> {

	/** . アクティビティへアクセスするために保持 */
	private MyLoaderCallbacks requestcallback;

	/** . コンテキスト. */
	private Context con;

	/**
	 *
	 * コンストラクタ.
	 *
	 * @param callback
	 *            アクティビティにアクセスするオブジェクト
	 * @param context
	 *            コンテキスト
	 */
	public RequestLoader(MyLoaderCallbacks callback, Context context) {
		requestcallback = callback;
		con = context;
	}

	/**
	 * .
	 *
	 * Loaderの開始処理.
	 *
	 * @param id
	 *            ローダID
	 *
	 * @param args
	 *            ローダインスタンスの初期化に必要なパラメーターを格納
	 *
	 * @return requesttask ローダオブジェクト
	 *
	 */
	public Loader<RequestToken> onCreateLoader(int id, Bundle args) {

		RequestTask requesttask = new RequestTask(con);

		// Log.v("tag", "OauthLoderstrat");
		requesttask.forceLoad();
		return requesttask;
	}

	/**
	 * Loaderの処理終了コールバック.
	 *
	 * @param arg0
	 *            ローダオブジェクト
	 *
	 * @param arg1
	 *            取得したリクエストトークン
	 */
	public void onLoadFinished(Loader<RequestToken> arg0, RequestToken arg1) {

		// アクティビティへアクセスする
		requestcallback.requestCallback(arg1);

	}

	/**
	 * Loaderがリセットされた時によびだされる.
	 *
	 * @param arg0
	 *            ローダオブジェクト
	 *
	 */
	public void onLoaderReset(Loader<RequestToken> arg0) {

	}

}
