package com.medizine.utils;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.BehaviorSubject;

public class RxOtpBus {
    private static RxOtpBus mInstance;
    private final BehaviorSubject<Object> subject = BehaviorSubject.create();

    public static RxOtpBus getInstance() {
        if (mInstance == null) {
            mInstance = new RxOtpBus();
        }
        return mInstance;
    }

    public void sendEvent(@androidx.annotation.NonNull @NonNull Object object) {
        subject.onNext(object);
    }

    @androidx.annotation.NonNull
    public Observable<Object> toObservable() {
        return subject;
    }
}
