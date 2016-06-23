package com.masakazuohmura.mytwitterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MasakazuOhmura on 2016/06/21.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    TwitterTimelineRecyclerView mTwitterTimelineRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mTwitterTimelineRecyclerView.getTweets();
        mTwitterTimelineRecyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager) mTwitterTimelineRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int current_page) {
                mTwitterTimelineRecyclerView.getTweets();
            }
        });
    }

}
