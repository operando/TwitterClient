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

import com.android.twitter.R;
import com.android.twitter.TwitterParameter;
import com.android.twitter.loaders.TwitterOauthLoaderTask;
import com.android.twitter.loaders.TwitterRequestTokenLoader;
import com.android.twitter.models.AsyncResult;
import com.android.twitter.utils.PreferenceUtils;
import com.android.twitter.utils.ToastUtils;
import com.android.twitter.views.ErrorToast;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * .
 * <p/>
 * 認証画面用のアクティビティ
 */
public class OAuthActivity extends Activity {

    public static Intent createIntent(Context context){
        Intent intent = new Intent(context, OAuthActivity.class);
        return intent;
    }

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
            setContentView(R.layout.activity_oauth);
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
                    // Bundleにpinコードをセットして渡す
                    Bundle bundle = new Bundle();
                    bundle.putString("pin", pin);
                    requestTwitterOaut(reqToken, bundle);
                } else {
                    ToastUtils.show(OAuthActivity.this, R.string.notpin);
                }
            }
        });
    }

    private void requestTwitterOaut(final RequestToken reqToken, Bundle bundle) {
        getLoaderManager().initLoader(1, bundle, new LoaderManager.LoaderCallbacks<AsyncResult<AccessToken>>() {
            @Override
            public Loader<AsyncResult<AccessToken>> onCreateLoader(int i, Bundle bundle) {
                TwitterOauthLoaderTask oauthtask = new TwitterOauthLoaderTask(OAuthActivity.this, bundle.getString("pin"),
                        reqToken);
                oauthtask.forceLoad();

                return oauthtask;
            }

            @Override
            public void onLoadFinished(Loader<AsyncResult<AccessToken>> loader, AsyncResult<AccessToken> asyncResult) {
                // 認証ができていたかを判定
                AccessToken accesstoken = asyncResult.getData();
                if (accesstoken != null) {
                    // 認証できていたらAccessTokenなどを書き込む
                    PreferenceUtils.saveString(OAuthActivity.this, TwitterParameter.PREFERENCES_NAME, TwitterParameter.TOKEN_KEYNAME,
                            accesstoken.getToken());
                    PreferenceUtils.saveString(OAuthActivity.this, TwitterParameter.PREFERENCES_NAME, TwitterParameter.TOKENSECRET_KYENAME,
                            accesstoken.getTokenSecret());

                    // タイムライン画面へ遷移する
                    startActivity(TimeLineActivity.createIntent(OAuthActivity.this));
                    finish();
                } else {
                    TwitterParameter.ERROR error = asyncResult.getError();
                    ErrorToast.show(OAuthActivity.this, error);
                    if (error == TwitterParameter.ERROR.OAUTHERR) {
                        requestToken();
                    }
                    // 認証用Loaderリセット
                    getLoaderManager().destroyLoader(1);
                }
            }

            @Override
            public void onLoaderReset(Loader<AsyncResult<AccessToken>> loader) {
            }
        });
    }
}
