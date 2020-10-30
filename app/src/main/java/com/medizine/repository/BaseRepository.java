package com.medizine.repository;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.medizine.exceptions.NetworkUnavailableException;
import com.medizine.model.DeleteResource;
import com.medizine.model.FetchResource;
import com.medizine.model.MediaLink;
import com.medizine.model.Resource;
import com.medizine.model.Response;
import com.medizine.model.UploadMediaResource;
import com.medizine.model.UploadResource;
import com.medizine.network.Connectivity;
import com.medizine.network.RetryOperator;
import com.medizine.network.RxNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseRepository<T> {

    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @CallSuper
    @NonNull
    public Resource<T> upload(UploadResource<T> t, boolean isEdit) {
        Resource<T> resource = new Resource<>();
        Observable<Response<T>> connectivityObservable = RxNetwork.observeNetworkConnectivity()
                .doOnSubscribe(__ -> {
                    resource.setStatus(isEdit ? Resource.Status.EDITING : Resource.Status.UPLOADING);
                })
                .subscribeOn(Schedulers.io())
                .flatMapSingle((Function<Connectivity, SingleSource<Response<T>>>) connectivity -> {
                    if (!connectivity.isAvailable()) {
                        throw new NetworkUnavailableException();
                    } else {
                        return Single.just(new Response<>());
                    }
                });

        List<Observable<Response<MediaLink>>> attachmentObservables = new ArrayList<>();
        if (t.getAttachmentCount() > 0) {
            attachmentObservables.add(connectivityObservable.flatMapSingle(response -> t.attachmentCall(0)));
        }
        for (int i = 1; i < t.getAttachmentCount(); i++) {
            int finalI = i;
            attachmentObservables.add(attachmentObservables.get(i - 1).flatMapSingle(response -> {
                t.attachmentResponse(finalI - 1, response.getData());
                return t.attachmentCall(finalI);
            }));
        }

        Observable<Response<T>> networkObservable;
        if (attachmentObservables.size() == 0) {
            networkObservable = connectivityObservable.flatMapSingle(response -> t.networkCall());
        } else {
            networkObservable = attachmentObservables.get(attachmentObservables.size() - 1).flatMapSingle(response -> {
                t.attachmentResponse(attachmentObservables.size() - 1, response.getData());
                return t.networkCall();
            });
        }

        Observable<Response<T>> finalObservable = networkObservable.flatMapSingle(response -> t.storageCall(response.getData()).toSingleDefault(new Response<>()));

        Disposable disposable = finalObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            resource.setStatus(isEdit ? Resource.Status.SUCCESS_EDIT : Resource.Status.SUCCESS_UPLOAD);
                        }, throwable -> {
                            if (throwable instanceof NetworkUnavailableException) {
                                resource.setStatus(Resource.Status.ERROR_NETWORK);
                            } else if (throwable instanceof IOException) {
                                resource.setStatus(Resource.Status.ERROR_ATTACHMENT);
                            } else {
                                resource.setStatus(Resource.Status.ERROR_UNKNOWN);
                            }
                        }
                );
        new CompositeDisposable().add(disposable);
        return resource;
    }

    @CallSuper
    @NonNull
    public Resource<Object> uploadObject(UploadResource<Object> t, boolean isEdit) {
        Resource<Object> resource = new Resource<>();
        Observable<Response<Object>> connectivityObservable = RxNetwork.observeNetworkConnectivity()
                .doOnSubscribe(__ -> {
                    resource.setStatus(isEdit ? Resource.Status.EDITING : Resource.Status.UPLOADING);
                })
                .subscribeOn(Schedulers.io())
                .flatMapSingle((Function<Connectivity, SingleSource<Response<Object>>>) connectivity -> {
                    if (!connectivity.isAvailable()) {
                        throw new NetworkUnavailableException();
                    } else {
                        return Single.just(new Response<>());
                    }
                });

        List<Observable<Response<MediaLink>>> attachmentObservables = new ArrayList<>();
        if (t.getAttachmentCount() > 0) {
            attachmentObservables.add(connectivityObservable.flatMapSingle(response -> t.attachmentCall(0)));
        }
        for (int i = 1; i < t.getAttachmentCount(); i++) {
            int finalI = i;
            attachmentObservables.add(attachmentObservables.get(i - 1).flatMapSingle(response -> {
                t.attachmentResponse(finalI - 1, response.getData());
                return t.attachmentCall(finalI);
            }));
        }

        Observable<Response<Object>> networkObservable;
        if (attachmentObservables.size() == 0) {
            networkObservable = connectivityObservable.flatMapSingle(response -> t.networkCall());
        } else {
            networkObservable = attachmentObservables.get(attachmentObservables.size() - 1).flatMapSingle(response -> {
                t.attachmentResponse(attachmentObservables.size() - 1, response.getData());
                return t.networkCall();
            });
        }

        Observable<Response<Object>> finalObservable = networkObservable.flatMapSingle(response -> t.storageCall(response.getData()).toSingleDefault(new Response<>()));

        Disposable disposable = finalObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            resource.setStatus(isEdit ? Resource.Status.SUCCESS_EDIT : Resource.Status.SUCCESS_UPLOAD);
                        }, throwable -> {
                            if (throwable instanceof NetworkUnavailableException) {
                                resource.setStatus(Resource.Status.ERROR_NETWORK);
                            } else if (throwable instanceof IOException) {
                                resource.setStatus(Resource.Status.ERROR_ATTACHMENT);
                            } else {
                                resource.setStatus(Resource.Status.ERROR_UNKNOWN);
                            }
                        }
                );
        new CompositeDisposable().add(disposable);
        return resource;
    }

    @CallSuper
    @NonNull
    public Resource<MediaLink> uploadMediaLink(UploadMediaResource<MediaLink> t) {
        Resource<MediaLink> resource = new Resource<>();
        resource.setLiveData(t.getStorage());

        Observable<Response<MediaLink>> connectivityObservable = RxNetwork.observeNetworkConnectivity()
                .doOnSubscribe(__ -> resource.setStatus(Resource.Status.UPLOADING))
                .subscribeOn(Schedulers.io())
                .flatMapSingle((Function<Connectivity, SingleSource<Response<MediaLink>>>) connectivity -> {
                    if (!connectivity.isAvailable()) {
                        throw new NetworkUnavailableException();
                    } else {
                        return Single.just(new Response<>());
                    }
                });

        Observable<Response<MediaLink>> networkObservable = connectivityObservable.flatMapSingle(response -> t.attachmentCall()).flatMapSingle(response -> {
            t.attachmentResponse(response.getData());
            return t.attachmentCall();
        });

        Disposable disposable = networkObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            resource.setStatus(Resource.Status.SUCCESS_UPLOAD);
                        }, throwable -> {
                            if (throwable instanceof NetworkUnavailableException) {
                                resource.setStatus(Resource.Status.ERROR_NETWORK);
                            } else if (throwable instanceof IOException) {
                                resource.setStatus(Resource.Status.ERROR_ATTACHMENT);
                            } else {
                                resource.setStatus(Resource.Status.ERROR_UNKNOWN);
                            }
                        }
                );
        new CompositeDisposable().add(disposable);
        return resource;
    }

    @NonNull
    public Resource<T> fetch(FetchResource<T> t) {
        Resource<T> resource = new Resource<>();
        resource.setStatus(Resource.Status.FETCHING);
        resource.setLiveData(t.getStorage());
        if (t.fetchType() == FetchResource.TYPE.STORAGE_ONLY) {
            resource.setStatus(Resource.Status.SUCCESS_FETCH);
            return resource;
        }

        if (t.fetchType() == FetchResource.TYPE.NETWORK_IF_NO_STORAGE) {
            Single<Boolean> exists = t.exists();
            if (exists == null)
                throw new IllegalStateException("Give exists response if fetch Type is NETWORK_IF_NO_STORAGE");
            else {
                Disposable disposable = exists.toObservable()
                        .subscribeOn(Schedulers.io())
                        .subscribe(response -> {
                                    if (!response) {
                                        fetchFromNetwork(t, resource);
                                    }
                                }, throwable -> {
                                    fetchFromNetwork(t, resource);
                                }
                        );
                mCompositeDisposable.add(disposable);
            }
        } else {
            fetchFromNetwork(t, resource);
        }
        return resource;
    }

    private void fetchFromNetwork(FetchResource<T> t, Resource<T> resource) {
        Observable<Response<T>> connectivityObservable = RxNetwork.observeNetworkConnectivity()
                .subscribeOn(Schedulers.io())
                .flatMapSingle((Function<Connectivity, SingleSource<Response<T>>>) connectivity -> {
                    if (!connectivity.isAvailable()) {
                        throw new NetworkUnavailableException();
                    } else {
                        return Single.just(new Response<>());
                    }
                });

        Observable<Response<T>> networkObservable = connectivityObservable.flatMapSingle(response -> t.networkCall());

        Observable<Response<T>> finalObservable = networkObservable.flatMapSingle(response -> t.storageCall(response.getData()).toSingleDefault(new Response<>()));

        Disposable disposable = finalObservable.retryWhen(RetryOperator::jainamRetryWhen)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            resource.setStatus(Resource.Status.SUCCESS_FETCH);
                        }, throwable -> {
                            if (throwable instanceof NetworkUnavailableException) {
                                resource.setStatus(Resource.Status.ERROR_NETWORK);
                            } else {
                                resource.setStatus(Resource.Status.ERROR_UNKNOWN);
                            }
                        }
                );
        mCompositeDisposable.add(disposable);
    }

    @NonNull
    public Resource<List<T>> fetchList(FetchResource<List<T>> t) {
        Resource<List<T>> resource = new Resource<>();
        resource.setStatus(Resource.Status.FETCHING);
        resource.setLiveData(t.getStorage());
        if (t.fetchType() == FetchResource.TYPE.STORAGE_ONLY) {
            resource.setStatus(Resource.Status.SUCCESS_FETCH);
            return resource;
        }

        if (t.fetchType() == FetchResource.TYPE.NETWORK_IF_NO_STORAGE) {
            Single<Boolean> exists = t.exists();
            if (exists == null)
                throw new IllegalStateException("Give exists response if fetch Type is NETWORK_IF_NO_STORAGE");
            else {
                Disposable disposable = exists.toObservable()
                        .subscribeOn(Schedulers.io())
                        .subscribe(response -> {
                                    if (!response) {
                                        fetchListFromNetwork(t, resource);
                                    }
                                }, throwable -> {
                                    fetchListFromNetwork(t, resource);
                                }
                        );
                mCompositeDisposable.add(disposable);
            }
        } else {
            fetchListFromNetwork(t, resource);
        }
        return resource;
    }

    private void fetchListFromNetwork(FetchResource<List<T>> t, Resource<List<T>> resource) {
        Observable<Response<List<T>>> connectivityObservable = RxNetwork.observeNetworkConnectivity()
                .subscribeOn(Schedulers.io())
                .flatMapSingle((Function<Connectivity, SingleSource<Response<List<T>>>>) connectivity -> {
                    if (!connectivity.isAvailable()) {
                        throw new NetworkUnavailableException();
                    } else {
                        return Single.just(new Response<>());
                    }
                });

        Observable<Response<List<T>>> networkObservable = connectivityObservable.flatMapSingle(response -> t.networkCall());

        Observable<Response<List<T>>> finalObservable = networkObservable.flatMapSingle(response -> t.storageCall(response.getData()).toSingleDefault(new Response<>()));

        Disposable disposable = finalObservable.compose(RetryOperator::jainamRetryWhen)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            resource.setStatus(Resource.Status.SUCCESS_FETCH);
                        }, throwable -> {
                            if (throwable instanceof NetworkUnavailableException) {
                                resource.setStatus(Resource.Status.ERROR_NETWORK);
                            } else {
                                resource.setStatus(Resource.Status.ERROR_UNKNOWN);
                            }
                        }
                );
        mCompositeDisposable.add(disposable);
    }

    @CallSuper
    @NonNull
    public Resource<T> delete(DeleteResource<T> t) {
        Resource<T> resource = new Resource<>();

        Observable<Response<String>> finalObservable;
        if (t.shouldDeleteFromNetwork()) {
            Observable<Response<T>> connectivityObservable = RxNetwork.observeNetworkConnectivity()
                    .doOnSubscribe(__ -> {
                        resource.setStatus(Resource.Status.DELETING);
                    })
                    .subscribeOn(Schedulers.io())
                    .flatMapSingle((Function<Connectivity, SingleSource<Response<T>>>) connectivity -> {
                        if (!connectivity.isAvailable()) {
                            throw new NetworkUnavailableException();
                        } else {
                            return Single.just(new Response<>());
                        }
                    });

            Observable<Response<String>> networkObservable = connectivityObservable.flatMapSingle(response -> t.networkCall());

            finalObservable = networkObservable.flatMapSingle(response -> t.storageCall());
        } else {
            finalObservable = t.storageCall().subscribeOn(Schedulers.io()).toObservable();
        }

        Disposable disposable = finalObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            resource.setStatus(Resource.Status.SUCCESS_DELETE);
                        }, throwable -> {
                            if (throwable instanceof NetworkUnavailableException) {
                                resource.setStatus(Resource.Status.ERROR_NETWORK);
                            } else {
                                resource.setStatus(Resource.Status.ERROR_UNKNOWN);
                            }
                        }
                );
        mCompositeDisposable.add(disposable);
        return resource;
    }

    @NonNull
    public Resource<Object> fetchObject(FetchResource<Object> t) {
        Resource<Object> resource = new Resource<>();
        resource.setStatus(Resource.Status.FETCHING);
        resource.setLiveData(t.getStorage());
        if (t.fetchType() == FetchResource.TYPE.STORAGE_ONLY) {
            resource.setStatus(Resource.Status.SUCCESS_FETCH);
            return resource;
        }

        if (t.fetchType() == FetchResource.TYPE.NETWORK_IF_NO_STORAGE) {
            Single<Boolean> exists = t.exists();
            if (exists == null)
                throw new IllegalStateException("Give exists response if fetch Type is NETWORK_IF_NO_STORAGE");
            else {
                Disposable disposable = exists.toObservable()
                        .subscribeOn(Schedulers.io())
                        .subscribe(response -> {
                                    if (!response) {
                                        fetchObjectFromNetwork(t, resource);
                                    }
                                }, throwable -> {
                            fetchObjectFromNetwork(t, resource);
                                }
                        );
                mCompositeDisposable.add(disposable);
            }
        } else {
            fetchObjectFromNetwork(t, resource);
            if (t.fetchType() == FetchResource.TYPE.STORAGE_AND_NETWORK_LIVE) {
                Disposable disposable = Observable.interval(t.liveUpdateInterval(), TimeUnit.SECONDS)
                        .flatMap((Function<Long, ObservableSource<?>>) aLong -> Observable.just(new Response<>()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> fetchObjectFromNetwork(t, resource));
                mCompositeDisposable.add(disposable);
            }
        }
        return resource;
    }

    private void fetchObjectFromNetwork(FetchResource<Object> t, Resource<Object> resource) {
        Observable<Response<Object>> connectivityObservable = RxNetwork.observeNetworkConnectivity()
                .subscribeOn(Schedulers.io())
                .flatMapSingle((Function<Connectivity, SingleSource<Response<Object>>>) connectivity -> {
                    if (!connectivity.isAvailable()) {
                        throw new NetworkUnavailableException();
                    } else {
                        return Single.just(new Response<>());
                    }
                });

        Observable<Response<Object>> networkObservable = connectivityObservable.flatMapSingle(response -> t.networkCall());

        Observable<Response<Object>> finalObservable = networkObservable.flatMapSingle(response -> t.storageCall(response.getData()).toSingleDefault(new Response<>()));

        Disposable disposable = finalObservable.compose(RetryOperator::jainamRetryWhen)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            resource.setStatus(Resource.Status.SUCCESS_FETCH);
                        }, throwable -> {
                            if (throwable instanceof NetworkUnavailableException) {
                                resource.setStatus(Resource.Status.ERROR_NETWORK);
                            } else {
                                resource.setStatus(Resource.Status.ERROR_UNKNOWN);
                            }
                        }
                );
        mCompositeDisposable.add(disposable);
    }

    public abstract void destroy();
}
