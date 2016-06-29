package com.masakazuohmura.mytwitterclient.api;

import com.masakazuohmura.mytwitterclient.BuildConfig;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.SearchService;

/**
 * Created by MasakazuOhmura on 2016/06/29.
 */

public class TwitterApiHelper {
    final SearchService searchService;

    public TwitterApiHelper() {
        final TwitterAuthToken authToken = new TwitterAuthToken(BuildConfig.TWITTER_TOKEN, BuildConfig.TWITTER_SECRET_TOKEN);
        final TwitterSession session = new TwitterSession(authToken, BuildConfig.TWITTER_USER_ID, BuildConfig.TWITTER_USER_NAME);
        final TwitterApiClient twitterApiClient = Twitter.getApiClient(session);
        searchService = twitterApiClient.getSearchService();
    }

    public SearchService getSearchService(){
        return searchService;
    }
}
