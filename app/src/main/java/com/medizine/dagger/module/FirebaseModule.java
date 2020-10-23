package com.medizine.dagger.module;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    @Provides
    @Singleton
    FirebaseRemoteConfig provideRemoteConfig(@NonNull Application application) {
        FirebaseApp.initializeApp(application.getApplicationContext());
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        return mFirebaseRemoteConfig;
    }
}