package com.medizine.model;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class Resource<T> {
    public enum Status {
        UPLOADING,
        DELETING,
        EDITING,
        FETCHING,
        SUCCESS_FETCH,
        SUCCESS_FETCH_LIVE,
        SUCCESS_UPLOAD,
        SUCCESS_EDIT,
        SUCCESS_DELETE,
        ERROR_NETWORK,
        ERROR_ATTACHMENT,
        ERROR_UNKNOWN
    }

    private LiveData<T> liveData;
    private MutableLiveData<Status> statusLiveData;

    public Resource() {
        statusLiveData = new MutableLiveData<>();
        statusLiveData.setValue(Status.FETCHING);
    }

    public void observeData(LifecycleOwner lifecycleOwner, Observer<T> observer) {
        if (liveData != null) {
            liveData.observe(lifecycleOwner, observer);
        }
    }

    public void removeDataObserver(Observer<T> observer) {
        if (liveData != null) {
            liveData.removeObserver(observer);
        }
    }

    public void setLiveData(LiveData<T> liveData) {
        this.liveData = liveData;
    }

    public void observeStatus(LifecycleOwner lifecycleOwner, Observer<Status> observer) {
        statusLiveData.observe(lifecycleOwner, observer);
    }

    public void removeStatusObserver(Observer<Status> observer) {
        statusLiveData.removeObserver(observer);
    }

    public void setStatus(Status status) {
        this.statusLiveData.postValue(status);
    }

    public Status getStatus() {
        return statusLiveData.getValue();
    }
}
