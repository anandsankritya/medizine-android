package com.medizine.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Single;

public abstract class UploadResource<T> {

    public int getAttachmentCount() {
        return 0;
    }

    @NonNull
    public Single<Response<MediaLink>> attachmentCall(int i) throws IOException {
        return Single.just(new Response<>());
    }

    public void attachmentResponse(int i, MediaLink response) {
    }
    
    @NonNull
    public abstract Completable storageCall(@Nullable T t);

    @NonNull
    public abstract Single<Response<T>> networkCall();
}
