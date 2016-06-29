package com.masakazuohmura.mytwitterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.masakazuohmura.mytwitterclient.adapter.TwitterTimelineAdapter;
import com.masakazuohmura.mytwitterclient.api.TwitterApiHelper;
import com.masakazuohmura.mytwitterclient.api.TwitterSearchApi;
import com.masakazuohmura.mytwitterclient.listener.EndlessRecyclerViewScrollListener;
import com.masakazuohmura.mytwitterclient.ui.DividerItemDecoration;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MasakazuOhmura on 2016/06/21.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView twitterTimelineRecyclerView;

    private TwitterTimelineAdapter adapter;

    private volatile Long maxId = null;
    private Callback<Search> cb = new Callback<Search>() {
        @Override
        public void success(Result<Search> result) {
            for (int i = 0; i < result.data.tweets.size(); i++) {
                Tweet tweet = result.data.tweets.get(i);
                adapter.add(tweet);

                if (i == result.data.tweets.size() - 1) {
                    maxId = tweet.id - 1L;
                }
            }
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
        setSupportActionBar(toolbar);

        final TwitterApiHelper twitterApiHelper = new TwitterApiHelper();
        final TwitterSearchApi twitterSearchApi = new TwitterSearchApi(twitterApiHelper.getSearchService());

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        twitterTimelineRecyclerView.setLayoutManager(layoutManager);
        twitterTimelineRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.recyclerview_divider));
        adapter = new TwitterTimelineAdapter(this);
        twitterTimelineRecyclerView.setAdapter(adapter);
        twitterTimelineRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                twitterSearchApi.loadTweets(cb, maxId);
            }
        });
        twitterSearchApi.loadTweets(cb, maxId);
    }

}
