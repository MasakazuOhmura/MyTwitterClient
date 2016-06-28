package com.masakazuohmura.mytwitterclient.twkit;

/**
 * Created by MasakazuOhmura on 2016/06/27.
 */


import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.GuestCallback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * SearchTimeline provides a timeline of tweets from the search/tweets API source.
 */
public class MySearchTimeline implements MyTimeline<Tweet> {
    private TwitterApiClient twitterApiClient;

    /**
     * Returns a decremented maxId if the given id is non-null. Otherwise returns the given maxId.
     * Suitable for REST Timeline endpoints which return inclusive previous results when exclusive
     * is desired.
     */
    static Long decrementMaxId(Long maxId) {
        return maxId == null ? null : maxId - 1;
    }

    /**
     * Adds the request to the guest AuthRequestQueue where guest auth will be setup.
     */
    void addRequest(final Callback<TwitterApiClient> cb) {
        //tweetUi.getGuestAuthQueue().addClientRequest(cb);
    }

    /**
     * Wrapper callback which unpacks a list of Tweets into a MyTimelineResult (cursor and items).
     */
    static class TweetsCallback extends Callback<List<Tweet>> {
        protected final Callback<MyTimelineResult<Tweet>> cb;

        /**
         * Constructs a TweetsCallback
         *
         * @param cb A callback which expects a MyTimelineResult
         */
        TweetsCallback(Callback<MyTimelineResult<Tweet>> cb) {
            this.cb = cb;
        }

        @Override
        public void success(Result<List<Tweet>> result) {
            final List<Tweet> tweets = result.data;
            final MyTimelineResult<Tweet> timelineResult
                    = new MyTimelineResult<>(new MyTimelineCursor(tweets), tweets);
            if (cb != null) {
                cb.success(timelineResult, result.response);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (cb != null) {
                cb.failure(exception);
            }
        }
    }


    static final String FILTER_RETWEETS = " -filter:retweets";   // leading whitespace intentional
//    static final String RESULT_TYPE = "recent";

    String query;
    String languageCode;
    private String local;
    private String resultType;

    MySearchTimeline(TwitterApiClient twitterApiClient) {
        this.twitterApiClient = twitterApiClient;
    }

    MySearchTimeline(TwitterApiClient twitterApiClient, String query, String languageCode, String local, String resultType) {
        this(twitterApiClient);
        this.languageCode = languageCode;
        this.local = local;
        this.resultType = resultType;
        // if the query is non-null append the filter Retweets modifier
        this.query = query == null ? null : query + FILTER_RETWEETS;
    }

    /**
     * Loads Tweets with id greater than (newer than) sinceId. If sinceId is null, loads the newest
     * Tweets.
     *
     * @param sinceId minimum id of the Tweets to load (exclusive).
     * @param cb      callback.
     */
    public void next(Long sinceId, Callback<MyTimelineResult<Tweet>> cb) {
        addRequest(createSearchRequest(sinceId, null, cb));
    }

    /**
     * Loads Tweets with id less than (older than) maxId.
     *
     * @param maxId maximum id of the Tweets to load (exclusive).
     * @param cb    callback.
     */
    public void previous(Long maxId, Callback<MyTimelineResult<Tweet>> cb) {
        // api quirk: search api provides results that are inclusive of the maxId iff
        // FILTER_RETWEETS is added to the query (which we currently always add), decrement the
        // maxId to get exclusive results
        addRequest(createSearchRequest(null, decrementMaxId(maxId), cb));
    }

    Callback<TwitterApiClient> createSearchRequest(final Long sinceId, final Long maxId,
                                                   final Callback<MyTimelineResult<Tweet>> cb) {
        return new LoggingCallback<TwitterApiClient>(cb, Fabric.getLogger()) {
            @Override
            public void success(Result<TwitterApiClient> result) {
                result.data.getSearchService().tweets(query, null, languageCode, local, resultType,
                        null, null, sinceId, maxId, true,
                        new GuestCallback<>(new MySearchTimeline.SearchCallback(cb)));
            }
        };
    }

    /**
     * Wrapper callback which unpacks a Search API result into a MyTimelineResult (cursor and items).
     */
    class SearchCallback extends Callback<Search> {
        protected final Callback<MyTimelineResult<Tweet>> cb;

        /**
         * Constructs a SearchCallback
         *
         * @param cb A Callback which expects a MyTimelineResult
         */
        SearchCallback(Callback<MyTimelineResult<Tweet>> cb) {
            this.cb = cb;
        }

        @Override
        public void success(Result<Search> result) {
            final List<Tweet> tweets = result.data.tweets;
            final MyTimelineResult<Tweet> timelineResult
                    = new MyTimelineResult<>(new MyTimelineCursor(tweets), tweets);
            if (cb != null) {
                cb.success(timelineResult, result.response);
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
     * SearchTimeline Builder
     */
    public static class Builder {
        private TwitterApiClient twitterApiClient;
        private String query;
        private String lang;
        private String local;
        private String resultType;

//        /**
//         * Constructs a Builder.
//         */
//        public Builder() {
//            this(TweetUi.getInstance());
//        }


        public Builder(TwitterApiClient twitterApiClient) {
            if (twitterApiClient == null) {
                throw new IllegalArgumentException("TweetUi instance must not be null");
            }
            this.twitterApiClient = twitterApiClient;
        }

        public MySearchTimeline.Builder query(String query) {
            this.query = query;
            return this;
        }

        public MySearchTimeline.Builder local(String local) {
            this.local = local;
            return this;
        }

        public MySearchTimeline.Builder languageCode(String languageCode) {
            this.lang = languageCode;
            return this;
        }

        public MySearchTimeline.Builder resultType(String resultType) {
            this.resultType = resultType;
            return this;
        }

        /**
         * Builds a SearchTimeline from the Builder parameters.
         *
         * @return a SearchTimeline.
         * @throws IllegalStateException if query is not set (is null).
         */
        public MySearchTimeline build() {
            if (query == null) {
                throw new IllegalStateException("query must not be null");
            }
            return new MySearchTimeline(twitterApiClient, query, lang, local, resultType);
        }
    }

}
