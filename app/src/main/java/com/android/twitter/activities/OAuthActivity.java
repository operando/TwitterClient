package com.android.twitter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.android.twitter.MyLoaderCallbacks;
import com.android.twitter.OauthLoader;
import com.android.twitter.R;
import com.android.twitter.loaders.TwitterRequestTokenLoader;
import com.android.twitter.TwitterParameter;
import com.android.twitter.loaders.TwitterOauthLoaderTask;
import com.android.twitter.utils.PreferenceUtils;
import com.android.twitter.utils.ToastUtils;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * .
 * <p/>
 * 認証画面用のアクティビティ
 */
public class OAuthActivity extends Activity implements MyLoaderCallbacks {

    /**
     * ビューの作成、データの準備、初期処理などを行う.
     *
     * @param savedInstanceState 前回のアプリ終了時の情報を保持
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ネットワークがつながっているかを判定
        if (isConnectNetwork()) {
            setContentView(R.layout.oauth);
            requestToken();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(R.string.mainneterr);
            dialog.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
    }

    private void requestToken() {
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<RequestToken>() {
            @Override
            public Loader<RequestToken> onCreateLoader(int i, Bundle bundle) {
                TwitterRequestTokenLoader requesttask = new TwitterRequestTokenLoader(getApplicationContext());
                requesttask.forceLoad();
                return requesttask;
            }

            @Override
            public void onLoadFinished(Loader<RequestToken> loader, RequestToken requestToken) {
                requestCallback(requestToken);
            }

            @Override
            public void onLoaderReset(Loader<RequestToken> loader) {
            }
        });
    }


    /**
     * .
     *
     * @return ネットワークが接続しているかをBooleanで返す
     */
    private boolean isConnectNetwork() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivity.getActiveNetworkInfo();
        return network != null;
    }

    /**
     * OauthLoaderからアクティビティにアクセスするためのメドッソ.
     *
     * @param oauthtask   oauthtaskオブジェクト
     * @param accesstoken アクセストークン
     */
    public void oauthCallback(TwitterOauthLoaderTask oauthtask, AccessToken accesstoken) {
        // 認証ができていたかを判定
        if (accesstoken != null) {
            // 認証できていたらAccessTokenなどを書き込む
            PreferenceUtils.saveString(this, TwitterParameter.PREFERENCES_NAME, TwitterParameter.TOKEN_KEYNAME,
                    accesstoken.getToken());
            PreferenceUtils.saveString(this, TwitterParameter.PREFERENCES_NAME, TwitterParameter.TOKENSECRET_KYENAME,
                    accesstoken.getTokenSecret());

            // タイムライン画面へ遷移する
            Intent intent = new Intent(this, TimeLineActivity.class);
            startActivity(intent);
            finish();
        } else {

            switch (oauthtask.getErr()) {
                case NETWORKERR:
                    ToastUtils.show(this, R.string.errnet);
                    break;
                case OAUTHERR:
                    ToastUtils.show(this, R.string.badpin);
                    requestToken();
                    break;
                default:
                    break;
            }
            // 認証用Loaderリセット
            getLoaderManager().destroyLoader(1);
        }
    }

    /**
     * RequestLoaderからアクティビティにアクセスするためのメドッソ.
     *
     * @param reqToken リクエストトークン
     */
    public void requestCallback(final RequestToken reqToken) {
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(reqToken.getAuthorizationURL());

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                EditText editPin = (EditText) findViewById(R.id.pin);
                String pin = editPin.getText().toString();

                // Pinコードが入力されているかを判定
                if (!(pin.equals(""))) {
                    // pinコードが入力されたいたら認証用のLoaderを起動する
                    OauthLoader oauthloader = new OauthLoader(OAuthActivity.this,
                            (MyLoaderCallbacks) OAuthActivity.this, reqToken);

                    // Bundleにpinコードをセットして渡す
                    Bundle bundle = new Bundle();
                    bundle.putString("pin", pin);
                    getLoaderManager().initLoader(1, bundle, oauthloader);
                } else {
                    ToastUtils.show(OAuthActivity.this, R.string.notpin);
                }
            }
        });
    }
}
