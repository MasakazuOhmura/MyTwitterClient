package com.masakazuohmura.mytwitterclient;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.util.ArrayList;

/**
 * Created by MasakazuOhmura on 2016/06/24.
 */
public class TwitterTimelineRecyclerView extends RecyclerView {

    private SearchService mSearchService;

    private TwitterTimelineAdapter mAdapter;
    private ArrayList<Tweet> mTweets = new ArrayList<>();

    // API GET search/tweets
    private String q = "iQON";
    private Geocode geocode = null;
    private String lang = "ja";
    private String local = "ja";
    private String resultType = "recent";
    private Integer count = null;
    private String until = null;
    private Long sinceId = null;
    private Long maxId = null;
    private Boolean includeEntries = null;
    private Callback<Search> cb = new Callback<Search>() {
        @Override
        public void success(Result<Search> result) {
            for (int i = 0; i < result.data.tweets.size(); i++) {
                Tweet tweet = result.data.tweets.get(i);
                mTweets.add(tweet);

                if (i == result.data.tweets.size() - 1) {
                    maxId = tweet.id - 1L;
                }
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void failure(TwitterException exception) {
            Log.e("Twitter Exception", exception.getMessage());
        }
    };

    public TwitterTimelineRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public TwitterTimelineRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TwitterTimelineRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        twitterInit();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        this.setLayoutManager(layoutManager);
        this.addItemDecoration(new DividerItemDecoration(context, R.drawable.recyclerview_divider));

        mAdapter = new TwitterTimelineAdapter(mTweets, context);
        this.setAdapter(mAdapter);
    }

    private void twitterInit() {
        final TwitterAuthToken authToken = new TwitterAuthToken(BuildConfig.TWITTER_TOKEN, BuildConfig.TWITTER_SECRET_TOKEN);
        final TwitterSession session = new TwitterSession(authToken, BuildConfig.TWITTER_USER_ID, BuildConfig.TWITTER_USER_NAME);
        final TwitterApiClient twitterApiClient = Twitter.getApiClient(session);
        mSearchService = twitterApiClient.getSearchService();
    }

    public void getTweets() {
        Log.e("getTweet", "getTweet : " + maxId);
        mSearchService.tweets(q,
                geocode,
                lang,
                local,
                resultType,
                count,
                until,
                sinceId,
                maxId,
                includeEntries,
                cb);
    }
}
