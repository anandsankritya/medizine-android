package com.medizine.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;
import java.util.Objects;

public class MediaObject implements Parcelable {

    public static final Creator<MediaObject> CREATOR = new Creator<MediaObject>() {
        @Override
        public MediaObject createFromParcel(Parcel in) {
            return new MediaObject(in);
        }

        @Override
        public MediaObject[] newArray(int size) {
            return new MediaObject[size];
        }
    };
    private String link;
    private Map<String, String> objectMetadata;

    // Empty constructor
    public MediaObject() {
    }

    // Parcel Constructor
    public MediaObject(Parcel in) {
        link = in.readString();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Map<String, String> getObjectMetadata() {
        return objectMetadata;
    }

    public void setObjectMetadata(Map<String, String> objectMetadata) {
        this.objectMetadata = objectMetadata;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MediaObject)) return false;
        MediaObject that = (MediaObject) o;
        return link.equals(that.link) && objectMetadata.equals(that.objectMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, objectMetadata);
    }
}