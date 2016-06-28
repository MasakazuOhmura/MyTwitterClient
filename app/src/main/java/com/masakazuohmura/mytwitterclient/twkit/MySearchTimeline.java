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
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * SearchTimeline provides a timeline of tweets from the search/tweets API source.
 */
public class MySearchTimeline implements MyTimeline<Tweet> {
    protected final TweetUi tweetUi;

//    private void scribeImpression() {
//        tweetUi.scribe(
//                ScribeConstants.getSyndicatedSdkTimelineNamespace(getTimelineType()),
//                ScribeConstants.getTfwClientTimelineNamespace(getTimelineType())
//        );
//    }

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
    static final String RESULT_TYPE = "filtered";
    private static final String SCRIBE_SECTION = "search";

//    final String query;
//    final String languageCode;
//    final Integer maxItemsPerRequest;

    String query;
    String languageCode;
    Integer maxItemsPerRequest;

    MySearchTimeline(TweetUi tweetUi) {
        if (tweetUi == null) {
            throw new IllegalArgumentException("TweetUi instance must not be null");
        }
        this.tweetUi = tweetUi;
        //scribeImpression();
    }

    MySearchTimeline(TweetUi tweetUi, String query, String languageCode, Integer maxItemsPerRequest) {
        this(tweetUi);
        this.languageCode = languageCode;
        this.maxItemsPerRequest = maxItemsPerRequest;
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
    @Override
    public void next(Long sinceId, Callback<MyTimelineResult<Tweet>> cb) {
        addRequest(createSearchRequest(sinceId, null, cb));
    }

    /**
     * Loads Tweets with id less than (older than) maxId.
     *
     * @param maxId maximum id of the Tweets to load (exclusive).
     * @param cb    callback.
     */
    @Override
    public void previous(Long maxId, Callback<MyTimelineResult<Tweet>> cb) {
        // api quirk: search api provides results that are inclusive of the maxId iff
        // FILTER_RETWEETS is added to the query (which we currently always add), decrement the
        // maxId to get exclusive results
        addRequest(createSearchRequest(null, decrementMaxId(maxId), cb));
    }

//    @Override
//    String getTimelineType() {
//        return SCRIBE_SECTION;
//    }

    Callback<TwitterApiClient> createSearchRequest(final Long sinceId, final Long maxId,
                                                   final Callback<MyTimelineResult<Tweet>> cb) {
        return new LoggingCallback<TwitterApiClient>(cb, Fabric.getLogger()) {
            @Override
            public void success(Result<TwitterApiClient> result) {
                result.data.getSearchService().tweets(query, null, languageCode, null, RESULT_TYPE,
                        maxItemsPerRequest, null, sinceId, maxId, true,
                        new GuestCallback<>(new SearchCallback(cb)));
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
        private TweetUi tweetUi;
        private String query;
        private String lang;
        private Integer maxItemsPerRequest = 30;

        /**
         * Constructs a Builder.
         */
        public Builder() {
            this(TweetUi.getInstance());
        }

        /**
         * Constructs a Builder.
         *
         * @param tweetUi A TweetUi instance.
         */
        public Builder(TweetUi tweetUi) {
            if (tweetUi == null) {
                throw new IllegalArgumentException("TweetUi instance must not be null");
            }
            this.tweetUi = tweetUi;
        }

        /**
         * Sets the query for the SearchTimeline.
         *
         * @param query A UTF-8, URL-encoded search query of 500 characters maximum, including
         *              operators. Queries may additionally be limited by complexity.
         */
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        /**
         * Sets the languageCode for the SearchTimeline.
         *
         * @param languageCode Restricts tweets to the given language, given by an ISO 639-1 code.
         *                     Language detection is best-effort.
         */
        public Builder languageCode(String languageCode) {
            this.lang = languageCode;
            return this;
        }

        /**
         * Sets the number of Tweets returned per request for the SearchTimeline.
         *
         * @param maxItemsPerRequest The number of tweets to return per request, up to a maximum of
         *                           100.
         */
        public Builder maxItemsPerRequest(Integer maxItemsPerRequest) {
            this.maxItemsPerRequest = maxItemsPerRequest;
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
            return new MySearchTimeline(tweetUi, query, lang, maxItemsPerRequest);
        }
    }

}
