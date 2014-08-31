package com.android.twitter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.twitter.R;
import com.android.twitter.model.TwitterStatus;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Viewにセットするデータ項目へのアクセスを提供するクラス.
 */
public class TwitterAdapter extends BindableAdapter<TwitterStatus> {

    /**
     * レイアウトXMLからViewを動的に生成するために使用.
     */
    private LayoutInflater inflater;
    /**
     * タイムラインの情報が入ったList.
     */
    private List<TwitterStatus> items;

    static class ViewHolder {
        @InjectView(R.id.icon)
        ImageView mIcon;
        @InjectView(R.id.text_id)
        TextView mTextId;
        @InjectView(R.id.text_name)
        TextView mTextName;
        @InjectView(R.id.text_tweet)
        TextView mTextTweet;
        @InjectView(R.id.text_date)
        TextView mTextDate;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * コンストラクタ.
     *
     * @param context            コンテキスト
     * @param textViewResourceId ビューをインスタンス化するときに使用するTextViewを含むレイアウトファイルのリソースID
     * @param item               タイムラインの情報が入ったList
     */
    public TwitterAdapter(Context context, int textViewResourceId,
                          List<TwitterStatus> item) {
        super(context, item);
        this.items = item;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View v = inflater.inflate(R.layout.tl, container, false);
        ViewHolder vh = new ViewHolder(v);
        v.setTag(vh);

        return v;
    }

    @Override
    public void bindView(TwitterStatus item, int position, View view) {
        ViewHolder vh = (ViewHolder) view.getTag();

        vh.mTextId.setText(item.getId());
        vh.mTextTweet.setText(item.getTl());
        vh.mIcon.setImageBitmap(item.getIcon());
        vh.mTextName.setText(item.getName());
        vh.mTextDate.setText(item.getDate());
    }

}
