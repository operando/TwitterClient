package com.android.twitter.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.twitter.R;
import com.android.twitter.TwitterClientApplication;
import com.android.twitter.TwitterDbAdapter;
import com.android.twitter.TwitterParameter;
import com.android.twitter.models.AsyncResult;
import com.android.twitter.models.TwitterStatus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * バックグラウンド処理用のクラス. メインスレッドでは行えないネットワーク接続処理をする
 */
public class TwitterTimeLineLoaderTask extends AsyncTaskLoader<AsyncResult<List<TwitterStatus>>> {

    /**
     * . OAuth認証設定用変数.
     */
    private ConfigurationBuilder confbuilder;

    /**
     * . 取得したアイコンの差分データを保持する
     */
    private HashMap<String, byte[]> map;

    /**
     * コンストラクタ.
     *
     * @param context     コンテキスト
     * @param token       アクセストークン
     * @param tokensecret トークンシークレット
     * @param hashmap     差分取得アイコン保持用
     */
    public TwitterTimeLineLoaderTask(Context context, String token, String tokensecret,
                                      HashMap<String, byte[]> hashmap) {
        super(context);
        confbuilder = new ConfigurationBuilder().setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokensecret)
                .setOAuthConsumerKey(TwitterClientApplication.consumerkey)
                .setOAuthConsumerSecret(TwitterClientApplication.consumersecret);
        map = hashmap;
    }

    @Override
    public AsyncResult<List<TwitterStatus>> loadInBackground() {
        AsyncResult<List<TwitterStatus>> asyncResult = new AsyncResult<List<TwitterStatus>>();
        Twitter twitter = new TwitterFactory(confbuilder.build()).getInstance();
        List<TwitterStatus> list = new ArrayList<TwitterStatus>();
        try {

            List<Status> user = twitter.getHomeTimeline();

            SimpleDateFormat format = new SimpleDateFormat(getContext().getText(
                    R.string.date).toString());
            TwitterDbAdapter dbHelper = new TwitterDbAdapter(getContext());
            dbHelper.open();

            for (Status status : user) {

                // Lodaerがリセットされていないかチェック
                if (isReset()) {
                    return null;
                }

                Date date = status.getCreatedAt();

                TwitterStatus twitterstatus = new TwitterStatus();

                // アイコン取得処理
                URL iconURL = new URL(status.getUser().getProfileImageURL());

                Bitmap icon;
                String uri = iconURL.toString();

                Cursor c = dbHelper.selectall(uri);

                byte[] b;

                if (!c.moveToFirst() && !map.containsKey(uri)) {
                    // コネクションを開く
                    HttpURLConnection httpURL = (HttpURLConnection) iconURL
                            .openConnection();
                    // 接続先のデータを取得
                    InputStream inputstream = new BufferedInputStream(
                            httpURL.getInputStream());
                    // bitmapにデコードする
                    icon = BitmapFactory.decodeStream(inputstream);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    icon.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    b = stream.toByteArray();

                    httpURL.disconnect();
                    inputstream.close();

                    map.put(uri, b);
                } else {
                    if (map.containsKey(uri)) {
                        b = map.get(uri);
                    } else {
                        b = c.getBlob(0);
                    }
                    icon = BitmapFactory.decodeByteArray(b, 0, b.length);
                }

                twitterstatus.setId(status.getUser().getName());
                twitterstatus.setDate(format.format(date));
                twitterstatus.setTl(status.getText());
                twitterstatus.setIcon(icon);
                twitterstatus.setName(status.getUser().getScreenName());
                twitterstatus.setTimeLineStatus(status);
                list.add(twitterstatus);
                c.close();
            }
            asyncResult.setData(list);
        } catch (TwitterException e) {
            asyncResult.setException(e);
            e.printStackTrace();
            if (e.isCausedByNetworkIssue()) {
                asyncResult.setError(TwitterParameter.ERROR.NETWORKERR);
            } else {
                // 認証エラーかどうかを判定
                if (TwitterParameter.CLIENT_ERROR == e.getStatusCode()) {
                    asyncResult.setError(TwitterParameter.ERROR.OAUTHERR);
                } else {
                    asyncResult.setError(TwitterParameter.ERROR.TWITTERERR);
                }
            }
        } catch (MalformedURLException e) {
            asyncResult.setException(e);
            e.printStackTrace();
        } catch (IOException e) {
            asyncResult.setException(e);
            e.printStackTrace();
        }
        return asyncResult;
    }

    @Override
    protected void onReset() {
        super.onReset();
        cancelLoad();
    }
}