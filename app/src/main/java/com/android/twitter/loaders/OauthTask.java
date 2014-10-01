package com.android.twitter.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.android.twitter.TwitterParameter;

import java.io.IOException;
import java.net.ConnectException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * アクセストークンを取得するAsyncTaskLoaderを継承したクラス.
 *
 */

public class OauthTask extends AsyncTaskLoader<AccessToken> {

	/** . 入力されたPINコードを保持 */
	private String mPin;

	/** リクエストトークン. */
	private RequestToken mReqToken;

	/**. 例外の種類を格納. */
	private TwitterParameter.ERROR exception;

	/**
	 *
	 * コンストラクタ.
	 *
	 * @param context
	 *            コンテキスト
	 * @param pin
	 *            pinコード
	 * @param reqToken
	 *            リクエストトークン
	 */
	public OauthTask(Context context, String pin, RequestToken reqToken) {
		super(context);
		mPin = pin;
		mReqToken = reqToken;
	}

	/**
	 *
	 * アクセストークンの取得を行う.
	 *
	 * @return accsessToken アクセストークン
	 *
	 */
	@Override
	public AccessToken loadInBackground() {
		try {
			ConfigurationBuilder confbuilder = new ConfigurationBuilder()
					.setOAuthConsumerKey(TwitterParameter.CONSUMERKEY)
					.setOAuthConsumerSecret(TwitterParameter.CONSUMERSECRET);
			Twitter twitter = new TwitterFactory(confbuilder.build())
					.getInstance();

			//アクセストークン取得
			AccessToken accsessToken = twitter.getOAuthAccessToken(mReqToken,
					mPin);

			return accsessToken;
		} catch (TwitterException e) {
			e.printStackTrace();
			if (TwitterParameter.CLIENT_ERROR == e.getStatusCode()) {
				exception = TwitterParameter.ERROR.OAUTHERR;
			} else {
				if (e.getCause() instanceof IOException
						&& e.getCause() instanceof ConnectException) {
					exception = TwitterParameter.ERROR.NETWORKERR;
				} else {
					exception = TwitterParameter.ERROR.OAUTHERR;
				}
			}
			return null;
		}
	}

	/**
	 *
	 * 例外を識別する列挙型を返す.
	 *
	 * @return err 例外の種類を識別するための列挙型
	 */
	public TwitterParameter.ERROR getErr() {
		return exception;
	}

}
