package com.medizine.dagger.module;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AnalyticsModule {

    @NonNull
    @Singleton
    @Provides
    public FirebaseAnalytics firebaseInstanceInstance(@NonNull Application application) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(application.getApplicationContext());
//        if (BuildConfig.DEBUG) {
//            firebaseAnalytics.setAnalyticsCollectionEnabled(false);
//        }
        return firebaseAnalytics;
    }

    @NonNull
    @Singleton
    @Provides
    public Tracker googleAnalyticsInstance(@NonNull Application application) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(application.getApplicationContext());
        /**
         * The SDK provides a dryRun flag that when set, prevents any data from being sent to Google Analytics.
         * The dryRun flag should be set whenever you are testing or debugging an implementation and do not want test
         * data to appear in your Google Analytics reports.
         */
//        if (BuildConfig.FLAVOR.equals("dev")) {
//            analytics.setDryRun(true);
//        }
        return analytics.newTracker("UA-99100075-1");
    }
}
