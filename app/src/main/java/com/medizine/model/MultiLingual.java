package com.medizine.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.medizine.utils.Utils;

import java.util.Objects;


public class MultiLingual implements Parcelable {
    public static final Creator<MultiLingual> CREATOR = new Creator<MultiLingual>() {
        @Override
        public MultiLingual createFromParcel(Parcel in) {
            return new MultiLingual(in);
        }

        @Override
        public MultiLingual[] newArray(int size) {
            return new MultiLingual[size];
        }
    };
    public String en;
    public String hi;

    public MultiLingual() {
    }

    protected MultiLingual(Parcel in) {
        en = in.readString();
        hi = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(en);
        dest.writeString(hi);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getEnAbsolute() {
        return en;
    }

    public String getHiAbsolute() {
        return hi;
    }

    private String getEn() {
        if (en != null && !en.isEmpty()) {
            return en;
        } else {
            return hi;
        }
    }

    public void setEn(String en) {
        this.en = en;
    }

    private String getHi() {
        if (hi != null && !hi.isEmpty()) {
            return hi;
        } else {
            return en;
        }
    }

    public void setHi(String hi) {
        this.hi = hi;
    }

    public String getString() {
        switch (Utils.getLocale()) {
            case "hi":
                return getHi();
            case "en":
                return getEn();
        }
        return getHi();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof MultiLingual) {
            MultiLingual m1 = (MultiLingual) obj;
            boolean enEqual = m1.en == null || m1.en.equals(en);
            boolean hiEqual = m1.hi == null || m1.hi.equals(hi);
            return enEqual && hiEqual;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(en, hi);
    }
}
