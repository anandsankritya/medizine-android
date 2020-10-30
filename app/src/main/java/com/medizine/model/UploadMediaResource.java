package com.medizine.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.io.IOException;

import io.reactivex.Single;

public abstract class UploadMediaResource<T> {

    @NonNull
    public Single<Response<MediaLink>> attachmentCall() throws IOException {
        return Single.just(new Response<>());
    }

    public void attachmentResponse(MediaLink response) {
    }

    @NonNull
    public abstract LiveData<T> getStorage();

}

