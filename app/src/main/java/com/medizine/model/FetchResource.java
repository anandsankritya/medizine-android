package com.medizine.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import io.reactivex.Completable;
import io.reactivex.Single;

public abstract class FetchResource<T> {

    /**
     * 30 seconds as default time interval between subsequent fetches
     **/
    public int liveUpdateInterval() {
        return 30;
    }

    @NonNull
    public abstract TYPE fetchType();

    @NonNull
    public abstract LiveData<T> getStorage();

    @Nullable
    public abstract Single<Boolean> exists();

    @NonNull
    public abstract Completable storageCall(@Nullable T t);

    @NonNull
    public abstract Single<Response<T>> networkCall();

    public enum TYPE {
        STORAGE_ONLY,
        NETWORK_ONLY,
        STORAGE_AND_NETWORK,
        NETWORK_IF_NO_STORAGE,
        STORAGE_AND_NETWORK_LIVE
    }
}
