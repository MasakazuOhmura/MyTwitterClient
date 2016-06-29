package com.masakazuohmura.mytwitterclient.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.masakazuohmura.mytwitterclient.R;
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

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private Context context;
    private final Object lock = new Object();

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
        }
    }

    public void add(@NonNull Tweet tweet) {
        int position = tweets.size();
        synchronized (lock) {
            tweets.add(tweet);
        }
        notifyItemInserted(position);
    }

    public TwitterTimelineAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.timeline_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Picasso.with(context).load(tweets.get(position).user.profileImageUrl).into(viewHolder.userIconImage);
        viewHolder.userScreenNameText.setText(UserUtils.formatScreenName(tweets.get(position).user.screenName));
        viewHolder.userNameText.setText(tweets.get(position).user.name);
        viewHolder.tweetText.setText(tweets.get(position).text);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }
}
