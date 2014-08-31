package com.android.twitter.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.twitter.R;
import com.android.twitter.TwitterStatus;

/**
*
* Viewにセットするデータ項目へのアクセスを提供するクラス.
*
*/
public class TwitterAdapter extends ArrayAdapter<TwitterStatus> {

	/** レイアウトXMLからViewを動的に生成するために使用. */
	private LayoutInflater inflater;
	/** タイムラインの情報が入ったList. */
	private List<TwitterStatus> items;

	/**
	 *
	 * コンストラクタ.
	 *
	 * @param context
	 *            コンテキスト
	 * @param textViewResourceId
	 *            ビューをインスタンス化するときに使用するTextViewを含むレイアウトファイルのリソースID
	 * @param item
	 *            タイムラインの情報が入ったList
	 */
	public TwitterAdapter(Context context, int textViewResourceId,
			List<TwitterStatus> item) {
		super(context, textViewResourceId, item);
		this.items = item;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 *
	 * 表示する内容をViewにセットする.
	 *
	 * @param position Viewの位置
	 * @param convertView 前回使用したViewオブジェクト
	 * @param parent 複数のView含む親View
	 * @return View Viewオブジェクト
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.tl, null);
		}

		TwitterStatus item = items.get(position);

		if (item != null) {

			TextView idView = (TextView) view.findViewById(R.id.text_id);
			TextView dateView = (TextView) view.findViewById(R.id.text_date);
			TextView tweetView = (TextView) view.findViewById(R.id.text_tweet);
			ImageView iconView = (ImageView) view.findViewById(R.id.icon);
			TextView nameView = (TextView) view.findViewById(R.id.text_name);

			idView.setText(item.getId());
			tweetView.setText(item.getTl());
			iconView.setImageBitmap(item.getIcon());
			nameView.setText(item.getName());
			dateView.setText(item.getDate());
		}
		return view;
	}
}
