package com.android.twitter;

import com.android.twitter.loaders.OauthTask;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 *
 * LoaderCallbacksを実装したクラスからActivityへアクセスするためのインターフェース.
 *
 */
public interface MyLoaderCallbacks {

	/**
	 *
	 * @param arg0
	 *            OauthTaskオブジェクト.
	 *
	 * @param arg1
	 *            アクセストークン
	 */
	void oauthCallback(OauthTask arg0, AccessToken arg1);

	/**
	 *
	 * @param arg1
	 *            リクエストトークン
	 *
	 */
	void requestCallback(RequestToken arg1);

}
