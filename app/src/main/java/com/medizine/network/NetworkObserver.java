package com.medizine.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import com.medizine.MedizineApp;
import com.medizine.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class NetworkObserver {
    @NonNull
    private static String TAG = NetworkObserver.class.getSimpleName();
    private ConnectivityManager.NetworkCallback networkCallback;

    /**
     * Observes network connectivity
     *
     * @return Observable representing stream of the network connectivity.
     */
    public Observable<Connectivity> observeNetworkConnectivity() {
        final Context context = MedizineApp.getAppContext();
        final String service = Context.CONNECTIVITY_SERVICE;
        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(service);
        final NetworkInfo netInfo = manager.getActiveNetworkInfo();
        return Observable.create((ObservableOnSubscribe<Connectivity>) subscriber -> {
            if (netInfo != null && netInfo.isConnected()) {
                subscriber.onNext(Connectivity.create());
            } else {
                networkCallback = createNetworkCallback(subscriber, context);
                final NetworkRequest networkRequest = new NetworkRequest.Builder().build();
                manager.registerNetworkCallback(networkRequest, networkCallback);
            }
        }).doOnDispose(() -> {
            if (netInfo == null || !netInfo.isConnected()) {
                unregisterCallback(manager);
            }
        }).startWith(Connectivity.create()).distinctUntilChanged();
    }

    private ConnectivityManager.NetworkCallback createNetworkCallback(@NonNull final ObservableEmitter<Connectivity> subscriber,
                                                                      @NonNull final Context context) {
        return new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                subscriber.onNext(Connectivity.create());
            }

            @Override
            public void onLost(Network network) {
                subscriber.onNext(Connectivity.create());
            }
        };
    }

    private void unregisterCallback(@NonNull final ConnectivityManager manager) {
        try {
            manager.unregisterNetworkCallback(networkCallback);
        } catch (Exception exception) {
            Utils.logException(TAG, exception);
        }
    }
}
