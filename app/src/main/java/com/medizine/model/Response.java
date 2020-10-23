package com.medizine.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Response<T> {
    @Nullable
    private T data;
    private Object error;
    private boolean success;

    @NonNull
    public static Response successResponse(Object data) {
        Response response = new Response<>();
        response.success = true;
        response.data = data;
        return response;
    }

    @NonNull
    public static Response failureResponse() {
        Response response = new Response();
        response.data = null;
        response.success = false;
        return response;
    }

    @Nullable
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

