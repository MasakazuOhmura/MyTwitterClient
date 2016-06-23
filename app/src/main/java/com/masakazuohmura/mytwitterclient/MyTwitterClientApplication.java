package com.masakazuohmura.mytwitterclient;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by MasakazuOhmura on 2016/06/24.
 */
public class MyTwitterClientApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.CONSUMER_KEY,
                BuildConfig.CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }
}
