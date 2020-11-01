package com.medizine;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.medizine.event.Event;
import com.medizine.utils.RxBus;
import com.medizine.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

public enum FirebaseRemoteConfigUpdate {
    INSTANCE;

    public static final String LATEST_APP = "LATEST_APP";
    public static final String FORCE_UPDATE_APP = "FORCE_UPDATE_APP";
    public static final String UPDATE_APP = "UPDATE_APP";

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    FirebaseRemoteConfigUpdate() {
        MedizineApp.getFirebaseComponent().inject(this);
    }

    @NonNull
    public static FirebaseRemoteConfigUpdate getInstance() {
        return INSTANCE;
    }

    public void syncWithServer() {
        mFirebaseRemoteConfig.fetch();
    }

    public Event.UpdateCheckerEvent checkForUpdate() {
        @APP_UPDATE
        String appStatus = LATEST_APP;
        String currentVersionName = BuildConfig.VERSION_NAME;
        String forceUpdateVersionName = mFirebaseRemoteConfig.getString("force_update");
        String normalUpdateVersionName = mFirebaseRemoteConfig.getString("normal_update");
        try {
            if (Utils.compareVersions(currentVersionName, forceUpdateVersionName) == -1) {
                appStatus = FORCE_UPDATE_APP;
            } else if (Utils.compareVersions(currentVersionName, normalUpdateVersionName) == -1) {
                appStatus = UPDATE_APP;
            }
        } catch (NumberFormatException e) {
            RxBus.getInstance().sendEvent(new Event.UpdateCheckerEvent("", ""));
        }
        switch (appStatus) {
//            case FORCE_UPDATE_APP:
//                return new Event.UpdateCheckerEvent(Constants.FirebaseConfigProperties.FORCE_UPDATE_TYPE,
//                        getRemoteConfigStringValue(Constants
//                                .FirebaseConfigProperties.FORCE_UPDATE_BODY));
//            case UPDATE_APP:
//                return new Event.UpdateCheckerEvent(Constants.FirebaseConfigProperties.NORMAL_UPDATE_TYPE,
//                        getRemoteConfigStringValue(Constants
//                                .FirebaseConfigProperties.NORMAL_UPDATE_BODY));

            default:
                return new Event.UpdateCheckerEvent("", "");
        }
    }

    public boolean isForceUpdateEnabled() {
        return Constants.FirebaseConfigProperties.FORCE_UPDATE_TYPE.equals(checkForUpdate().getUpdateType());
    }

    public boolean showOnboarding() {
        return false;
    }

    public String getForceShareSequence() {
        return mFirebaseRemoteConfig.getString(Constants.FirebaseConfigProperties.FORCE_SHARE_SEQUENCE);
    }

    public String getContactNumber() {
        return mFirebaseRemoteConfig.getString(Constants.FirebaseConfigProperties.CONTACT_NUMBER);
    }

    public boolean showForceShare() {
        return mFirebaseRemoteConfig.getBoolean(Constants.FirebaseConfigProperties.SHOW_FORCE_SHARE);
    }


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({LATEST_APP, FORCE_UPDATE_APP, UPDATE_APP})
    @interface APP_UPDATE {

    }

}

