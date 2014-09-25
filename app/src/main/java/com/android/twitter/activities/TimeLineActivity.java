package com.android.twitter.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.twitter.R;
import com.android.twitter.TwitterDbAdapter;
import com.android.twitter.TwitterParameter;
import com.android.twitter.TwitterTask;
import com.android.twitter.adapters.TwitterAdapter;
import com.android.twitter.models.TwitterStatus;

import java.util.HashMap;
import java.util.List;

import twitter4j.Status;
import twitter4j.URLEntity;

/**
 * タイムライン画面を表示するアクティビティ.
 */
public class TimeLineActivity extends ListActivity implements
        LoaderCallbacks<List<TwitterStatus>> {

    /**
     * 更新用のアイテムID.
     */
    public static final int UPDATE_ID = Menu.FIRST;

    /**
     * TwitterTaskオブジェクトを保持.
     */
    private TwitterTask twitterTask;

    /**
     * . TwitterDbAdapterオブジェクト
     */
    private TwitterDbAdapter mTwitterDb;

    /**
     * . 取得したアイコンの差分データを保持する
     */
    private HashMap<String, byte[]> map;

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
            Intent intent = new Intent(this, OAuthActivity.class);
            startActivity(intent);
            finish();
        } else {

            mTwitterDb = new TwitterDbAdapter(this);
            mTwitterDb.open();

            map = new HashMap<String, byte[]>();

            getLoaderManager().initLoader(0, bundle, this);
        }
    }

    public Bundle createBundle() {

        SharedPreferences pref = getSharedPreferences(
                TwitterParameter.PREFERENCES_NAME, MODE_PRIVATE);
        String token = pref.getString(TwitterParameter.TOKEN_KEYNAME, "");
        String tokensecret = pref.getString(
                TwitterParameter.TOKENSECRET_KYENAME, "");

        // Loaderに渡すBundleを作成
        Bundle bundle = new Bundle();
        bundle.putString(TwitterParameter.TOKEN_KEYNAME, token);
        bundle.putString(TwitterParameter.TOKENSECRET_KYENAME, tokensecret);

        return bundle;

    }

    public void resetPreferences() {

        SharedPreferences pref = getSharedPreferences(
                TwitterParameter.PREFERENCES_NAME, MODE_PRIVATE);
        pref.edit().clear().commit();

    }

    /**
     * Loaderの開始処理.
     *
     * @param id   ローダを識別する ID
     * @param args ローダインスタンスの初期化に必要なパラメーターを格納
     * @return twitterTask ローダオブジェクト
     */
    public Loader<List<TwitterStatus>> onCreateLoader(int id, Bundle args) {

        twitterTask = new TwitterTask(this,
                args.getString(TwitterParameter.TOKEN_KEYNAME),
                args.getString(TwitterParameter.TOKENSECRET_KYENAME), id,
                mTwitterDb, map);

        twitterTask.forceLoad();
        return twitterTask;

    }

    /**
     * Loaderの処理終了コールバック.
     *
     * @param arg0 ローダオブジェクト
     * @param arg1 取得したタイムラインの情報
     */
    public void onLoadFinished(Loader<List<TwitterStatus>> arg0,
                               List<TwitterStatus> arg1) {

        if (arg1 == null) {
            // エラー処理.
            TwitterTask exceptionTask = (TwitterTask) arg0;
            switch (exceptionTask.getErr()) {
                case NETWORKERR:
                    Toast.makeText(this, R.string.errnet, Toast.LENGTH_LONG).show();
                    break;
                case TWITTERERR:
                    Toast.makeText(this, R.string.gettlerr, Toast.LENGTH_LONG)
                            .show();
                    break;
                case OAUTHERR:
                    if (exceptionTask.getId() == 0) {
                        // 初期起動時の認証エラー
                        Intent intent = new Intent(this, OAuthActivity.class);
                        startActivity(intent);
                        finish();
                        getLoaderManager().destroyLoader(0);
                    } else {
                        // 更新時の認証エラー
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setMessage(R.string.apperr);
                        dialog.setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent(
                                                getApplicationContext(),
                                                OAuthActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();
                    }
                    // Preferencesの内容を削除する
                    resetPreferences();

                    break;
                default:
                    break;
            }
        } else {
            // タイムライン表示処理
            getLoaderManager().destroyLoader(1);
            TwitterAdapter array = new TwitterAdapter(getApplicationContext(),
                    R.layout.tl, arg1);
            setListAdapter(array);
            setContentView(R.layout.main);

            // HashMapに保持すつ上限サイズを15に設定
            // テストでは上限を10に設定。
            if (map.size() >= TwitterParameter.MAPMAXSIZE) {
                Log.v("MapSize(INSERTMAE)", Integer.toString(map.size()));
                insertmap();
            } else {
                Log.v("MapSize(NOINSERT)", Integer.toString(map.size()));
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
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, UPDATE_ID, 0, R.string.updete);
        return result;
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
            case UPDATE_ID:
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

        List<Status> user = twitterTask.getUser();
        Status status = user.get(position);
        URLEntity[] urlArray = status.getURLEntities();

        int size = urlArray.length;
        if (size > 0) {
            if (size <= 1) {
                Uri uri = Uri.parse(urlArray[0].getExpandedURL().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {

                final String[] url = new String[size];

                for (int i = 0; i < size; i++) {
                    url[i] = urlArray[i].getExpandedURL().toString();
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.jumpurl);

                alert.setItems(url, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialoginterface, int i) {
                        Uri uri = Uri.parse(url[i]);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).create().show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("MapSize(INSERTMAE)", Integer.toString(map.size()));
        if (mTwitterDb != null) {
            if (map.size() != 0) {
                insertmap();
            } else {
                mTwitterDb.close();
            }
        }

    }

    /**
     * HashMapの中身をINSERTする処理
     */
    public void insertmap() {
        // レコード数 = テーブルのレコード数 + HashMapが保持しているURIの数 - テーブルのレコード上限数
        // テストでは上限を20件に設定。
        long result = mTwitterDb.getRecordCount() + map.size() - TwitterParameter.RECORDMAX;

        // レコード数が0より大きかったら、テーブルのレコードを削除
        if (result > 0) {
            mTwitterDb.delete(result);
        }

        // HashMapのキー値を取得
        String[] key = (String[]) map.keySet().toArray(new String[0]);

        for (String uri : key) {
            // HashMapの中身をテーブルにINSET
            mTwitterDb.insert(uri, map.get(uri));
        }
        // HashMapをクリア
        map.clear();
        Log.v("MapSize(Clear)", Integer.toString(map.size()));
    }

    /**
     * Loaderがリセットされた時によびだされる.
     *
     * @param arg0 ローダオブジェクト
     */
    public void onLoaderReset(Loader<List<TwitterStatus>> arg0) {

    }
}
