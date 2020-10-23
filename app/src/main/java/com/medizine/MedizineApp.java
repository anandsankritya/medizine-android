package com.medizine;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.medizine.dagger.component.AnalyticsComponent;
import com.medizine.dagger.component.AppComponent;
import com.medizine.dagger.component.DaggerAnalyticsComponent;
import com.medizine.dagger.component.DaggerAppComponent;
import com.medizine.dagger.component.DaggerFirebaseComponent;
import com.medizine.dagger.component.DaggerNetComponent;
import com.medizine.dagger.component.DaggerStorageComponent;
import com.medizine.dagger.component.FirebaseComponent;
import com.medizine.dagger.component.NetComponent;
import com.medizine.dagger.component.StorageComponent;
import com.medizine.dagger.module.AppModule;
import com.medizine.dagger.module.DbModule;
import com.medizine.dagger.module.FirebaseModule;
import com.medizine.dagger.module.NetModule;
import com.medizine.dagger.module.PrefModule;

import javax.inject.Inject;

public class MedizineApp extends MultiDexApplication {
    public static boolean sharePopupShown = false;
    private static AppComponent appComponent;
    @Nullable
    private static NetComponent netComponent;
    private static StorageComponent storageComponent;
    private static AnalyticsComponent analyticsComponent;
    private static FirebaseComponent firebaseComponent;
    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;

//    NotificationBroadcastReceiver mReceiver;
//    NotificationChannelLanguageReceiver mNotificationChannelLanguageReceiver;

    public static FirebaseComponent getFirebaseComponent() {
        return firebaseComponent;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static Context getAppContext() {
        return appComponent.application().getApplicationContext();
    }

    public static StorageComponent getStorageComponent() {
        return storageComponent;
    }

    @Nullable
    public static NetComponent getNetComponent() {

        return netComponent;
    }

    public static void destroyNetComponent() {
        netComponent = null;
    }

    @NonNull
    public static NetComponent buildAndReturnNetComponent() {
        netComponent = DaggerNetComponent.builder().netModule(new NetModule(Configuration.BASE_URL)).build();
        return netComponent;
    }

    public static AnalyticsComponent getAnalyticsComponent() {
        return analyticsComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        netComponent = netComponent == null ? buildAndReturnNetComponent() : netComponent;
        storageComponent = DaggerStorageComponent.builder().appModule(new AppModule(this)).dbModule(new DbModule()).prefModule(new PrefModule()).build();
        analyticsComponent = DaggerAnalyticsComponent.builder().appModule(new AppModule(this)).build();
        firebaseComponent = DaggerFirebaseComponent.builder().appModule(new AppModule(this)).firebaseModule(new FirebaseModule()).build();

        //firebaseComponent.inject(this);
        //mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        FirebaseRemoteConfig.getInstance().fetchAndActivate();
        Stetho.initializeWithDefaults(this);
        MapsInitializer.initialize(this);
        //Places.initialize(this, getString(R.string.google_maps_key));
        //Send app opened event whenever user opens application
//        AnalyticsUtils.Events.sendAppOpenedEvent();
//        mReceiver = new NotificationBroadcastReceiver();
//        mNotificationChannelLanguageReceiver = new NotificationChannelLanguageReceiver();
//        registerReceiver(mReceiver, new IntentFilter("android.intent.action.NOTIFICATION_DISMISS"));
//        registerReceiver(mNotificationChannelLanguageReceiver, new IntentFilter("android.intent.action.LOCALE_CHANGED"));
//        Utils.deleteLogFile();
    }
}

