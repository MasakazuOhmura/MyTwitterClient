package com.masakazuohmura.mytwitterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.masakazuohmura.mytwitterclient.adapter.TwitterTimelineAdapter;
import com.masakazuohmura.mytwitterclient.listener.EndlessRecyclerViewScrollListener;
import com.masakazuohmura.mytwitterclient.ui.DividerItemDecoration;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MasakazuOhmura on 2016/06/21.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mTwitterTimelineRecyclerView;

    private TwitterTimelineAdapter mAdapter;

    // API GET search/tweets
    private String q = "iQON";
    private Geocode geocode = null;
    private String lang = "ja";
    private String local = "ja";
    private String resultType = "recent";
    private Integer count = null;
    private String until = null;
    private Long sinceId = null;
    private volatile Long maxId = null;
    private Boolean includeEntries = null;
    private Callback<Search> cb = new Callback<Search>() {
        @Override
        public void success(Result<Search> result) {
            for (int i = 0; i < result.data.tweets.size(); i++) {
                Tweet tweet = result.data.tweets.get(i);
                mAdapter.add(tweet);
            }
            maxId = result.data.searchMetadata.maxId;
        }

        @Override
        public void failure(TwitterException exception) {
            Log.e("Twitter Exception", exception.getMessage());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        final TwitterAuthToken authToken = new TwitterAuthToken(BuildConfig.TWITTER_TOKEN, BuildConfig.TWITTER_SECRET_TOKEN);
        final TwitterSession session = new TwitterSession(authToken, BuildConfig.TWITTER_USER_ID, BuildConfig.TWITTER_USER_NAME);
        final TwitterApiClient twitterApiClient = Twitter.getApiClient(session);
        final SearchService searchService = twitterApiClient.getSearchService();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTwitterTimelineRecyclerView.setLayoutManager(layoutManager);
        mTwitterTimelineRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.recyclerview_divider));
        mAdapter = new TwitterTimelineAdapter(this);
        mTwitterTimelineRecyclerView.setAdapter(mAdapter);
        mTwitterTimelineRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getTweets(searchService);
            }
        });

        getTweets(searchService);
    }

    private void getTweets(SearchService searchService) {
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
