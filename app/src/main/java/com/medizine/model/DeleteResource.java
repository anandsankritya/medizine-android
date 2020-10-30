package com.medizine.model;

import androidx.annotation.NonNull;

import io.reactivex.Single;

public abstract class DeleteResource<T> {
    public abstract boolean shouldDeleteFromNetwork();

    public abstract Single<Response<String>> storageCall();

    @NonNull
    public abstract Single<Response<String>> networkCall();
}
