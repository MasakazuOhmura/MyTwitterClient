package com.masakazuohmura.mytwitterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private TwitterTimelineAdapter mAdapter;
    private ArrayList<Tweet> mTweets = new ArrayList<>();

    // API GET search/tweets
    private String q = "iQON";
    private Geocode geocode = null;
    private String lang = "ja";
    private String local = "ja";
    private String resultType = "recent";
    private Integer count = 10;
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

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TwitterTimelineAdapter(mTweets, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.recyclerview_divider));

        final TwitterAuthToken authToken = new TwitterAuthToken(BuildConfig.TWITTER_TOKEN, BuildConfig.TWITTER_SECRET_TOKEN);
        final TwitterSession session = new TwitterSession(authToken, BuildConfig.TWITTER_USER_ID, BuildConfig.TWITTER_USER_NAME);
        final TwitterApiClient twitterApiClient = Twitter.getApiClient(session);
        final SearchService searchService = twitterApiClient.getSearchService();
        requestSearchApi(searchService);

        mRecyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) mRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int current_page) {
                requestSearchApi(searchService);
            }
        });
    }

    private void requestSearchApi(SearchService searchService) {
        searchService.tweets(q,
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
