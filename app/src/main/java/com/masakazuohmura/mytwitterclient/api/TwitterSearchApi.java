package com.masakazuohmura.mytwitterclient.api;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

/**
 * Created by MasakazuOhmura on 2016/06/29.
 */

public class TwitterSearchApi {

    private TwitterSearchApi() {
    }

    private static TwitterSearchApi twitterSearchApi = new TwitterSearchApi();

    public static TwitterSearchApi getInstance() {
        return twitterSearchApi;
    }

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
    private Callback<Search> cb = null;

    public void setCb(Callback<Search> cb) {
        this.cb = cb;
    }

//    public TwitterSearchApi(Callback cb) {
//        this.cb = cb;
//    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public void getTweets(SearchService searchService) {
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