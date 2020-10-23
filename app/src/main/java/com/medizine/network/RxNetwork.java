package com.medizine.network;

import android.Manifest;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import io.reactivex.Observable;

public class RxNetwork {
    /**
     * Observes network connectivity. Information about network state, type and typeName are contained in
     * observed Connectivity object.
     *
     * @param context Context of the activity or an application
     * @return RxJava Observable with Connectivity class containing information about network state,
     * type and typeName
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static Observable<Connectivity> observeNetworkConnectivity(@Nullable final Context context) {
        return new NetworkObserver().observeNetworkConnectivity();
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static Observable<Connectivity> observeNetworkConnectivity() {
        return new NetworkObserver().observeNetworkConnectivity();
    }
}
