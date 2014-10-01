package com.android.twitter;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

import com.android.twitter.loaders.TwitterOauthLoaderTask;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
//import android.util.Log;

/**
 *
 * OauthTask用のLoaderCallbacksを実装したクラス.
 *
 */
public class OauthLoader implements LoaderCallbacks<AccessToken> {

	/** コンテキスト. */
	private Context con;

	/** アクティビティへアクセスするために保持. */
	private MyLoaderCallbacks mLoadercallback;

	/** リクエストトークン. */
	private RequestToken mReqToken;

	/**
	 *
	 * コンストラクタ.
	 *
	 * @param context
	 *            コンテキスト
	 * @param call
	 *            アクティビティにアクセスするオブジェクト
	 * @param reqToken
	 *            リクエストトークン
	 */
	public OauthLoader(Context context, MyLoaderCallbacks call,
			RequestToken reqToken) {

		con = context;
		mLoadercallback = call;
		mReqToken = reqToken;

	}

	/**
	 *
	 * @param id
	 *            ローダID.
	 *
	 * @param args
	 *            ローダID
	 *
	 * @return oauthtask ローダオブジェクト
	 *
	 */
	public Loader<AccessToken> onCreateLoader(int id, Bundle args) {

		TwitterOauthLoaderTask oauthtask = new TwitterOauthLoaderTask(con, args.getString("pin"),
				mReqToken);
		oauthtask.forceLoad();

		return oauthtask;
	}

	/**
	 *
	 * Loaderの処理終了コールバック.
	 *
	 * @param arg0
	 *            ローダオブジェクト
	 *
	 * @param arg1
	 *            取得したアクセストークン
	 *
	 */
	public void onLoadFinished(Loader<AccessToken> arg0, AccessToken arg1) {
		TwitterOauthLoaderTask exceptionTaks = (TwitterOauthLoaderTask) arg0;
		mLoadercallback.oauthCallback(exceptionTaks, arg1);
	}

	/**
	 * Loaderがリセットされた時によびだされる.
	 *
	 * @param arg0
	 *            ローダオブジェクト
	 *
	 */
	public void onLoaderReset(Loader<AccessToken> arg0) {

	}

}
