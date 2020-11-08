package com.medizine.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.medizine.ConnectivityListener;
import com.medizine.network.Connectivity;

public class NetworkReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkReceiver.class.getSimpleName();
    private ConnectivityListener mListener;

    public NetworkReceiver(ConnectivityListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        boolean status = Connectivity.create().isAvailable();
        Log.d(TAG, status ? "connected" : "disconnected");
        mListener.onChanged(status);
    }
}
