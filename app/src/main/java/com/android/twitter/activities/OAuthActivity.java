package com.android.twitter.activities;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.twitter.MyLoaderCallbacks;
import com.android.twitter.OauthLoader;
import com.android.twitter.OauthTask;
import com.android.twitter.R;
import com.android.twitter.RequestLoader;
import com.android.twitter.TwitterParameter;

/**
 * .
 *
 * 認証画面用のアクティビティ
 *
 */
public class OAuthActivity extends Activity implements MyLoaderCallbacks {

	/**. コンテキスト. */
	private Context con;

	/** リクエストトークン. */
	private RequestToken requesttoken;

	/** . RequestLoaderオブジェクト. */
	private RequestLoader requestloader;

	/**
	 *
	 * ビューの作成、データの準備、初期処理などを行う.
	 *
	 * @param savedInstanceState
	 *            前回のアプリ終了時の情報を保持
	 *
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ネットワークがつながっているかを判定
		if (isConnectNetwork()) {
			setContentView(R.layout.oauth);
			con = this;

			requestloader = new RequestLoader(this, this);
			getLoaderManager().initLoader(0, null, requestloader);
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

	/**
	 * .
	 *
	 * @return ネットワークが接続しているかをBooleanで返す
	 */
	public boolean isConnectNetwork() {

		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = connectivity.getActiveNetworkInfo();
		return network != null;

	}

	/**
	 *
	 * OauthLoaderからアクティビティにアクセスするためのメドッソ.
	 *
	 * @param oauthtask
	 *            oauthtaskオブジェクト
	 *
	 * @param accesstoken
	 *            アクセストークン
	 */
	public void oauthCallback(OauthTask oauthtask, AccessToken accesstoken) {
		// 認証ができていたかを判定
		if (accesstoken != null) {

			// 認証できていたらAccessTokenなどを書き込む
			SharedPreferences pref = getSharedPreferences(
					TwitterParameter.PREFERENCES_NAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString(TwitterParameter.TOKEN_KEYNAME,
					accesstoken.getToken());
			editor.putString(TwitterParameter.TOKENSECRET_KYENAME,
					accesstoken.getTokenSecret());
			editor.commit();

			// タイムライン画面へ遷移する
			Intent intent = new Intent(this, TimeLineActivity.class);
			startActivity(intent);
			con = null;
			finish();
		} else {

			switch (oauthtask.getErr()) {
			case NETWORKERR:
				Toast.makeText(con, R.string.errnet, Toast.LENGTH_LONG).show();
				break;
			case OAUTHERR:
				Toast.makeText(con, R.string.badpin, Toast.LENGTH_LONG).show();
				getLoaderManager().restartLoader(0, null, requestloader);
				break;
			default:
				break;
			}
			// 認証用Loaderリセット
			getLoaderManager().destroyLoader(1);
		}
	}

	/**
	 *
	 * RequestLoaderからアクティビティにアクセスするためのメドッソ.
	 *
	 * @param reqToken
	 *            リクエストトークン
	 *
	 */
	public void requestCallback(RequestToken reqToken) {

		requesttoken = reqToken;

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
					OauthLoader oauthloader = new OauthLoader(con,
							(MyLoaderCallbacks) con, requesttoken);

					// Bundleにpinコードをセットして渡す
					Bundle bundle = new Bundle();
					bundle.putString("pin", pin);
					getLoaderManager().initLoader(1, bundle, oauthloader);
				} else {
					Toast.makeText(getApplicationContext(), R.string.notpin,
							Toast.LENGTH_LONG).show();

				}

			}

		});
	}
}
