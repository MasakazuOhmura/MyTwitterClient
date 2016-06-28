package com.masakazuohmura.mytwitterclient.twkit;

/**
 * Created by MasakazuOhmura on 2016/06/27.
 */

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterException;

import io.fabric.sdk.android.Logger;

/**
 * LoggingCallback logs error messages to the logger and passes TwitterExceptions through to the
 * given Callback. Concrete subclasses must implement success(Result<T> result) and optionally call
 * cb's success with the appropriate unpacked result data.
 * @param <T> expected response type
 */
abstract class LoggingCallback<T> extends Callback<T> {
    // Wrapped cb generic type is unknown, concrete subclass responsible for implementing
    // success(Result<T> result) and unpacking result to call cb with proper type checking
    private final Callback cb;
    private final Logger logger;

    /**
     * Constructs a LoggingCallback.
     * @param cb Wrapped Callback of any type
     * @param logger a Logger.
     */
    LoggingCallback(Callback cb, Logger logger) {
        this.cb = cb;
        this.logger = logger;
    }

    @Override
    public void failure(TwitterException exception) {
        //logger.e(TweetUi.LOGTAG, exception.getMessage(), exception);
        if (cb != null) {
            cb.failure(exception);
        }
    }
}