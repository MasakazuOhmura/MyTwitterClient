package com.masakazuohmura.mytwitterclient.twkit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.masakazuohmura.mytwitterclient.R;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Identifiable;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MasakazuOhmura on 2016/06/23.
 */
public class TwitterTimelineAdapter2<T extends Identifiable> extends RecyclerView.Adapter<TwitterTimelineAdapter2.ViewHolder> {

    private ArrayList<Tweet> mTweets;
    private Context context;
    protected  MyTimelineDelegate<Tweet> delegate;
    protected Callback<Tweet> actionCallback;

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
                }
            });
        }
    }

    /**
     * Constructs a MyTimelineListAdapter for the given MyTimeline.
     *
     * @param context  the context for row views.
     * @param timeline a MyTimeline providing access to timeline data items.
     * @throws IllegalArgumentException if context or timeline is null
     */
    public TwitterTimelineAdapter2(Context context, MyTimeline<Tweet> timeline) {
        this(context, new MyTimelineDelegate<Tweet>(timeline));
    }

    TwitterTimelineAdapter2(Context context, MyTimelineDelegate<Tweet> delegate) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        this.context = context;
        this.delegate = delegate;
        delegate.refresh(null);
    }


    TwitterTimelineAdapter2(Context context, MyTimeline<Tweet> timeline,
                            Callback<Tweet> cb) {
        this(context, new MyTimelineDelegate<>(timeline), cb);
    }

    TwitterTimelineAdapter2(Context context, MyTimelineDelegate<Tweet> delegate,
                            Callback<Tweet> cb) {
        this.context = context;
        this.delegate = delegate;
        this.actionCallback = new TwitterTimelineAdapter2.ReplaceTweetCallback(delegate, cb);
    }


    /**
     * Clears the items and loads the latest MyTimeline items.
     */
    public void refresh(Callback<MyTimelineResult<Tweet>> cb) {
        delegate.refresh(cb);
    }

//    @Override
//    public Tweet getItem(int position) {
//        return delegate.getItem(position);
//    }

    @Override
    public long getItemId(int position) {
        return delegate.getItemId(position);
    }

//    @Override
//    public void registerDataSetObserver(DataSetObserver observer) {
//        delegate.registerDataSetObserver(observer);
//    }

//    @Override
//    public void unregisterDataSetObserver(DataSetObserver observer) {
//        delegate.unregisterDataSetObserver(observer);
//    }

//    @Override
//    public void notifyDataSetChanged() {
//        delegate.notifyDataSetChanged();
//    }
//
//    @Override
//    public void notifyDataSetInvalidated() {
//        delegate.notifyDataSetInvalidated();
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.timeline_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Picasso.with(context).load(mTweets.get(position).user.profileImageUrl).into(viewHolder.userIconImage);
        viewHolder.userScreenNameText.setText(UserUtils.formatScreenName(mTweets.get(position).user.screenName));
        viewHolder.userNameText.setText(mTweets.get(position).user.name);
        viewHolder.tweetText.setText(mTweets.get(position).text);
    }

    @Override
    public int getItemCount() {
        return delegate.getCount();
    }

//    @Override
//    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
//        delegate.registerDataSetObserver(observer);
//    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
    }

    /*
    * On success, sets the updated Tweet in the MyTimelineDelegate to replace any old copies
    * of the same Tweet by id.
    */
    static class ReplaceTweetCallback extends Callback<Tweet> {
        MyTimelineDelegate<Tweet> delegate;
        Callback<Tweet> cb;

        ReplaceTweetCallback(MyTimelineDelegate<Tweet> delegate, Callback<Tweet> cb) {
            this.delegate = delegate;
            this.cb = cb;
        }

        @Override
        public void success(Result<Tweet> result) {
            delegate.setItemById(result.data);
            if (cb != null) {
                cb.success(result);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (cb != null) {
                cb.failure(exception);
            }
        }
    }

    /**
     * TweetMyTimelineListAdapter Builder
     */
    public static class Builder {
        private Context context;
        private MyTimeline<Tweet> timeline;
        private Callback<Tweet> actionCallback;

        /**
         * Constructs a Builder.
         *
         * @param context Context for Tweet views.
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Sets the Tweet timeline data source.
         *
         * @param timeline MyTimeline of Tweets
         */
        public TwitterTimelineAdapter2.Builder setTimeline(MyTimeline<Tweet> timeline) {
            this.timeline = timeline;
            return this;
        }

        /**
         * Sets the callback to call when a Tweet action is performed on a Tweet view.
         *
         * @param actionCallback called when a Tweet action is performed.
         */
        public TwitterTimelineAdapter2.Builder setOnActionCallback(Callback<Tweet> actionCallback) {
            this.actionCallback = actionCallback;
            return this;
        }

        /**
         * Builds a TweetMyTimelineListAdapter from Builder parameters.
         *
         * @return a TweetMyTimelineListAdpater
         */
        public TwitterTimelineAdapter2 build() {
            return new TwitterTimelineAdapter2(context, timeline, actionCallback);
        }
    }
}
