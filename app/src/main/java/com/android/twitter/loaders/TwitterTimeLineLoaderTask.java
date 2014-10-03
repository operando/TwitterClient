package com.android.twitter.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.android.twitter.R;
import com.android.twitter.TwitterDbAdapter;
import com.android.twitter.TwitterParameter;
import com.android.twitter.models.TwitterStatus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
 *
 * バックグラウンド処理用のクラス. メインスレッドでは行えないネットワーク接続処理をする
 *
 */
public class TwitterTimeLineLoaderTask extends AsyncTaskLoader<List<TwitterStatus>> {

    /** . OAuth認証設定用変数. */
    private ConfigurationBuilder confbuilder;

    /** . タイムラインのステータス */
    private List<Status> user;

    /** . 例外の種類を格納. */
    private TwitterParameter.ERROR exception;

    /** . 取得したアイコンの差分データを保持する */
    private HashMap<String, byte[]> map;

    /**
     *
     * コンストラクタ.
     *
     * @param context
     *            コンテキスト
     *
     * @param token
     *            アクセストークン
     *
     * @param tokensecret
     *            トークンシークレット
     *
     *
     * @param hashmap
     *            差分取得アイコン保持用
     *
     */
    public TwitterTimeLineLoaderTask(Context context, String token, String tokensecret,
                                     HashMap<String, byte[]> hashmap) {
        super(context);
        confbuilder = new ConfigurationBuilder().setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokensecret)
                .setOAuthConsumerKey(TwitterParameter.CONSUMERKEY)
                .setOAuthConsumerSecret(TwitterParameter.CONSUMERSECRET);
        map = hashmap;
    }

    /**
     *
     * タイムラインの取得を行う.
     *
     * @return list 取得したタイムラインの情報
     *
     */
    @Override
    public List<TwitterStatus> loadInBackground() {
        Twitter twitter = new TwitterFactory(confbuilder.build()).getInstance();
        List<TwitterStatus> list = new ArrayList<TwitterStatus>();
        try {

            user = twitter.getHomeTimeline();

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

                    icon.compress(CompressFormat.PNG, 100, stream);

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

                // ユーザ名セット
                twitterstatus.setId(status.getUser().getName());
                // ツイート時間セット
                twitterstatus.setDate(format.format(date));
                // タイムラインセット
                twitterstatus.setTl(status.getText());
                // アイコンセット
                twitterstatus.setIcon(icon);
                // スクリーンネーム
                twitterstatus.setName(status.getUser().getScreenName());
                list.add(twitterstatus);
                c.close();
            }
        } catch (TwitterException e) {
            e.printStackTrace();
            if (e.isCausedByNetworkIssue()) {
                exception = TwitterParameter.ERROR.NETWORKERR;
            } else {
                // 認証エラーかどうかを判定
                if (TwitterParameter.CLIENT_ERROR == e.getStatusCode()) {
                    exception = TwitterParameter.ERROR.OAUTHERR;
                } else {
                    exception = TwitterParameter.ERROR.TWITTERERR;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return list;
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

    /**
     * .
     *
     * 取得したタイムラインのステータスを返す
     *
     * @return user 取得したタイムラインのステータス
     */
    public List<Status> getUser() {
        return user;
    }

    @Override
    protected void onReset() {
        super.onReset();
        // onStopLoading();
        cancelLoad();
    }
}