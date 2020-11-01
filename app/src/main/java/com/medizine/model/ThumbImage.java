package com.medizine.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.TypeConverters;

import com.medizine.model.converters.MediaObjectListConverter;

import java.util.List;
import java.util.Objects;

@TypeConverters({MediaObjectListConverter.class})
public class ThumbImage implements Parcelable {
    public static final Creator<ThumbImage> CREATOR = new Creator<ThumbImage>() {
        @Override
        public ThumbImage createFromParcel(Parcel in) {
            return new ThumbImage(in);
        }

        @Override
        public ThumbImage[] newArray(int size) {
            return new ThumbImage[size];
        }
    };
    private String id;
    private List<MediaObject> s3Links;

    public ThumbImage() {
    }

    protected ThumbImage(Parcel in) {
        id = in.readString();
        s3Links = in.createTypedArrayList(MediaObject.CREATOR);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MediaObject> getS3Links() {
        return s3Links;
    }

    public void setS3Links(List<MediaObject> s3Links) {
        this.s3Links = s3Links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThumbImage)) return false;
        ThumbImage that = (ThumbImage) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getS3Links(), that.getS3Links());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getS3Links());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(s3Links);
    }
}
