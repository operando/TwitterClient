package com.android.twitter.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.android.twitter.TwitterParameter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * リクエストトークンを取得するAsyncTaskLoaderを継承したクラス.
 */
public class TwitterRequestTokenLoader extends AsyncTaskLoader<RequestToken> {

    /**
     * コンストラクタ.
     *
     * @param context コンテキスト
     */
    public TwitterRequestTokenLoader(Context context) {
        super(context);
    }

    /**
     * リクエストトークンの取得を行う.
     *
     * @return reqToken 取得したリクエストトークン
     */
    @Override
    public RequestToken loadInBackground() {
        ConfigurationBuilder confbuilder = new ConfigurationBuilder()
                .setOAuthConsumerKey(TwitterParameter.CONSUMERKEY)
                .setOAuthConsumerSecret(TwitterParameter.CONSUMERSECRET);
        try {
            Twitter twitter = new TwitterFactory(confbuilder.build())
                    .getInstance();

            // リクエストトークン取得
            RequestToken reqToken = twitter.getOAuthRequestToken("");

            return reqToken;
        } catch (TwitterException e) {
            return null;
        }
    }
}
