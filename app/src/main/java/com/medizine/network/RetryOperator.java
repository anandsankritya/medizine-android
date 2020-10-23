package com.medizine.network;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import retrofit2.HttpException;

public class RetryOperator {
    public static final int RETRY_COUNT = 3;
    public static final int DELAY = 2; // In seconds

    public static <T> Observable<T> jainamRetryWhen(Observable<T> source) {
        return source.retryWhen(errors -> errors.zipWith(Observable.range(1, RETRY_COUNT), (n, i) -> (n))
                .flatMap(error -> {
                    if (error instanceof HttpException) {
                        int code = ((HttpException) error).code();

                        // Retry if server error or Too many request error
                        if (code >= 500 || code == 429) {
                            return Observable.timer(DELAY, TimeUnit.SECONDS);
                        }
                    }
                    return Observable.error(error);
                }));
    }
}
