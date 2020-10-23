package com.medizine.utils;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private static RxBus mInstance;

    @NonNull
    private Subject publisher = PublishSubject.create();

    public static RxBus getInstance() {
        if (mInstance == null) {
            mInstance = new RxBus();
        }

        return mInstance;
    }

    public void sendEvent(@NonNull Object object) {
        if (publisher.hasObservers()) {
            publisher.onNext(object);
        }
    }

    @NonNull
    public Observable<Object> toObservable() {
        return publisher;
    }
}

