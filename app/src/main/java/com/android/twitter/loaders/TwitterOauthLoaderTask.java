package com.android.twitter.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.android.twitter.TwitterClientApplication;
import com.android.twitter.TwitterParameter;
import com.android.twitter.models.AsyncResult;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * アクセストークンを取得するAsyncTaskLoaderを継承したクラス.
 */

public class TwitterOauthLoaderTask extends AsyncTaskLoader<AsyncResult<AccessToken>> {

    /**
     * . 入力されたPINコードを保持
     */
    private String mPin;

    /**
     * リクエストトークン.
     */
    private RequestToken mReqToken;

    public TwitterOauthLoaderTask(Context context, String pin, RequestToken reqToken) {
        super(context);
        mPin = pin;
        mReqToken = reqToken;
    }

    /**
     * アクセストークンの取得を行う.
     *
     * @return accsessToken アクセストークン
     */
    @Override
    public AsyncResult<AccessToken> loadInBackground() {
        AsyncResult<AccessToken> asyncResult = new AsyncResult<AccessToken>();
        try {
            ConfigurationBuilder confbuilder = new ConfigurationBuilder()
                    .setOAuthConsumerKey(TwitterClientApplication.consumerkey)
                    .setOAuthConsumerSecret(TwitterClientApplication.consumersecret);
            Twitter twitter = new TwitterFactory(confbuilder.build())
                    .getInstance();
            AccessToken accsessToken = twitter.getOAuthAccessToken(mReqToken,
                    mPin);
            asyncResult.setData(accsessToken);
        } catch (TwitterException e) {
            asyncResult.setException(e);
            if (e.isCausedByNetworkIssue()) {
                asyncResult.setError(TwitterParameter.ERROR.NETWORKERR);
            } else if (TwitterParameter.CLIENT_ERROR == e.getStatusCode()) {
                asyncResult.setError(TwitterParameter.ERROR.OAUTHERR);
            } else {
                asyncResult.setError(TwitterParameter.ERROR.OAUTHERR);
            }
        }
        return asyncResult;
    }
}