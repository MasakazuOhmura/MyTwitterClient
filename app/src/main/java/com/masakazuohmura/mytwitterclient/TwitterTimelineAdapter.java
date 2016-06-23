package com.masakazuohmura.mytwitterclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MasakazuOhmura on 2016/06/23.
 */

public class TwitterTimelineAdapter extends RecyclerView.Adapter<TwitterTimelineAdapter.ViewHolder> {

    private ArrayList<Tweet> mTweets;
    private static Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tweet_text)
        TextView tweetText;
        @BindView(R.id.user_name)
        TextView userNameText;
        @BindView(R.id.user_screen_name)
        TextView userScreenNameText;
        @BindView(R.id.user_icon)
        ImageView userIconImage;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "未実装...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public TwitterTimelineAdapter(ArrayList<Tweet> dataSet, Context context) {
        mTweets = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.timeline_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Picasso.with(mContext).load(mTweets.get(position).user.profileImageUrl).into(viewHolder.userIconImage);
        viewHolder.userScreenNameText.setText(UserUtils.formatScreenName(mTweets.get(position).user.screenName));
        viewHolder.userNameText.setText(mTweets.get(position).user.name);
        viewHolder.tweetText.setText(mTweets.get(position).text);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
