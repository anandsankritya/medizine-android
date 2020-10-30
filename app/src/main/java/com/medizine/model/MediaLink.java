package com.medizine.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.TypeConverters;

import com.medizine.model.converters.MediaObjectListConverter;
import com.medizine.utils.Utils;

import java.util.List;
import java.util.Objects;

@TypeConverters({MediaObjectListConverter.class})
public class MediaLink implements Parcelable {
    public static final Creator<MediaLink> CREATOR = new Creator<MediaLink>() {
        @Override
        public MediaLink createFromParcel(Parcel in) {
            return new MediaLink(in);
        }

        @Override
        public MediaLink[] newArray(int size) {
            return new MediaLink[size];
        }
    };
    private String id;
    private String mediaType;
    private List<MediaObject> s3Links;
    private String uri;

    // Empty constructor
    public MediaLink() {
    }

    // Parcel Constructor
    public MediaLink(Parcel in) {
        id = in.readString();
        mediaType = in.readString();
        s3Links = in.createTypedArrayList(MediaObject.CREATOR);
        uri = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public List<MediaObject> getS3Links() {
        return s3Links;
    }

    public void setS3Links(List<MediaObject> s3Links) {
        this.s3Links = s3Links;
    }

    @Nullable
    public String getUri() {
        if (uri != null) {
            return uri;
        } else if (s3Links != null && s3Links.size() > 0) {
            return s3Links.get(0).getLink();
        } else {
            return null;
        }
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(mediaType);
        dest.writeTypedList(s3Links);
        dest.writeString(uri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MediaLink mediaLink = (MediaLink) o;
        return Objects.equals(id, mediaLink.id) &&
                Objects.equals(mediaType, mediaLink.mediaType) &&
                Objects.equals(s3Links, mediaLink.s3Links) &&
                Objects.equals(uri, mediaLink.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mediaType, s3Links, uri);
    }

    public boolean contains(@NonNull String link) {
        if (!Utils.isListEmpty(s3Links)) {
            for (MediaObject mediaObject : s3Links) {
                if (link.equals(mediaObject.getLink())) {
                    return true;
                }
            }
        }
        return false;
    }
}