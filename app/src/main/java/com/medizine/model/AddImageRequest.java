package com.medizine.model;

public class AddImageRequest {
    private String photoId;

    public AddImageRequest(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }
}
