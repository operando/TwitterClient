package com.android.twitter.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.android.twitter.R;
import com.android.twitter.TwitterDbAdapter;
import com.android.twitter.TwitterParameter;
import com.android.twitter.adapters.TwitterAdapter;
import com.android.twitter.loaders.TwitterTimeLineLoaderTask;
import com.android.twitter.models.AsyncResult;
import com.android.twitter.models.TwitterStatus;
import com.android.twitter.utils.IntentUtils;
import com.android.twitter.utils.PreferenceUtils;
import com.android.twitter.views.ErrorToast;

import java.util.HashMap;
import java.util.List;

import twitter4j.Status;
import twitter4j.URLEntity;

/**
 * タイムライン画面を表示するアクティビティ.
 */
public class TimeLineActivity extends ListActivity implements
        LoaderCallbacks<AsyncResult<List<TwitterStatus>>> {

    /**
     * TwitterTaskオブジェクトを保持.
     */
    private TwitterTimeLineLoaderTask twitterTask;

    /**
     * . TwitterDbAdapterオブジェクト
     */
    private TwitterDbAdapter mTwitterDb;

    /**
     * . 取得したアイコンの差分データを保持する
     */
    private HashMap<String, byte[]> map;

    /** タイムライン取得中に表示するプログレスバー. */
    private ProgressDialog mProgress;

    private boolean isTimeLineUpdate;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, TimeLineActivity.class);
        return intent;
    }

    /**
     * ビューの作成、データの準備、初期処理などを行う.
     *
     * @param savedInstanceState 前回のアプリ終了時の情報を保持
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = createBundle();

        String token = bundle.getString(TwitterParameter.TOKEN_KEYNAME);

        // 認証済みかどうかを判定
        if (token.length() == 0) {
            // 　認証画面へ遷移する
            startActivity(OAuthActivity.createIntent(this));
            finish();
        } else {
            mTwitterDb = new TwitterDbAdapter(this);
            mTwitterDb.open();
            map = new HashMap<String, byte[]>();
            getLoaderManager().initLoader(0, bundle, this);
        }
    }

    public Bundle createBundle() {
        // Loaderに渡すBundleを作成
        Bundle bundle = new Bundle();
        bundle.putString(TwitterParameter.TOKEN_KEYNAME, PreferenceUtils.getString(this,
                TwitterParameter.PREFERENCES_NAME, TwitterParameter.TOKEN_KEYNAME, ""));
        bundle.putString(TwitterParameter.TOKENSECRET_KYENAME, PreferenceUtils.getString(this,
                TwitterParameter.PREFERENCES_NAME, TwitterParameter.TOKENSECRET_KYENAME, ""));
        return bundle;
    }

    /**
     * Loaderの開始処理.
     *
     * @param id   ローダを識別する ID
     * @param args ローダインスタンスの初期化に必要なパラメーターを格納
     * @return twitterTask ローダオブジェクト
     */
    public Loader<AsyncResult<List<TwitterStatus>>> onCreateLoader(int id, Bundle args) {
        twitterTask = new TwitterTimeLineLoaderTask(this,
                args.getString(TwitterParameter.TOKEN_KEYNAME),
                args.getString(TwitterParameter.TOKENSECRET_KYENAME), map);
        twitterTask.forceLoad();

        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getText(R.string.load).toString());
        mProgress.setIndeterminate(true);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.show();

        return twitterTask;
    }

    /**
     * Loaderの処理終了コールバック.
     *
     * @param loader ローダオブジェクト
     * @param asyncResult 取得したタイムラインの情報
     */
    public void onLoadFinished(Loader<AsyncResult<List<TwitterStatus>>> loader,
                               AsyncResult<List<TwitterStatus>> asyncResult) {
        mProgress.dismiss();

        if (asyncResult.getException() != null) {
            TwitterParameter.ERROR error = asyncResult.getError();
            ErrorToast.show(this, error);
            if (error == TwitterParameter.ERROR.OAUTHERR) {
                if (isTimeLineUpdate) {
                    // 更新時の認証エラー
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage(R.string.apperr);
                    dialog.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    startActivity(OAuthActivity.createIntent(TimeLineActivity.this));
                                    finish();
                                }
                            }).show();
                } else {
                    // 初期起動時の認証エラー
                    startActivity(OAuthActivity.createIntent(this));
                    getLoaderManager().destroyLoader(0);
                    finish();
                }
                PreferenceUtils.clear(this, TwitterParameter.PREFERENCES_NAME);
                return;
            }

            // タイムライン表示処理
            getLoaderManager().destroyLoader(1);
            TwitterAdapter adapter = new TwitterAdapter(getApplicationContext(), asyncResult.getData());
            setListAdapter(adapter);
            setContentView(R.layout.main);

            // HashMapに保持すつ上限サイズを15に設定
            // テストでは上限を10に設定。
            if (map.size() >= TwitterParameter.MAPMAXSIZE) {
                insertmap();
            }
        }
    }

    /**
     * メニューオプション作成.
     *
     * @param menu Menuオブジェクト
     * @return result メニュー表示の有無
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.time_line, menu);
        return true;
    }

    /**
     * メニューオプション押下処理.
     *
     * @param item タップされたメニュー
     * @return 選択処理の完了
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.time_line_update:
                isTimeLineUpdate = true;
                Bundle bundle = createBundle();
                getLoaderManager().initLoader(1, bundle, this);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Status status = ((AsyncResult<List<TwitterStatus>>) l.getAdapter().getItem(position)).getData().get(position).getTimeLineStatus();
        URLEntity[] urlArray = status.getURLEntities();

        int size = urlArray.length;
        if (size > 0) {
            if (size <= 1) {
                IntentUtils.openBrowser(this, urlArray[0].getExpandedURL().toString());
            } else {
                final String[] url = new String[size];
                for (int i = 0; i < size; i++) {
                    url[i] = urlArray[i].getExpandedURL().toString();
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.jumpurl);
                alert.setItems(url, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        IntentUtils.openBrowser(TimeLineActivity.this, url[i]);
                    }
                }).create().show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTwitterDb != null) {
            if (map.size() != 0) {
                // レコード数 = テーブルのレコード数 + HashMapが保持しているURIの数 - テーブルのレコード上限数
                // テストでは上限を20件に設定。
                long result = mTwitterDb.getRecordCount() + map.size() - TwitterParameter.RECORDMAX;
                // レコード数が0より大きかったら、テーブルのレコードを削除
                if (result > 0) {
                    mTwitterDb.delete(result);
                }
                insertmap();
                // HashMapをクリア
                map.clear();
            } else {
                mTwitterDb.close();
            }
        }
    }

    /**
     * HashMapの中身をINSERTする処理
     */
    public void insertmap() {
        mTwitterDb.insertsIcon(map);
    }

    /**
     * Loaderがリセットされた時によびだされる.
     *
     * @param loader ローダオブジェクト
     */
    public void onLoaderReset(Loader<AsyncResult<List<TwitterStatus>>> loader) {
    }
}
