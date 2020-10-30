package com.medizine.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class SocialLinks implements Parcelable {
    public static final Creator<SocialLinks> CREATOR = new Creator<SocialLinks>() {
        @Override
        public SocialLinks createFromParcel(Parcel in) {
            return new SocialLinks(in);
        }

        @Override
        public SocialLinks[] newArray(int size) {
            return new SocialLinks[size];
        }
    };
    private String youtube;
    private String website;
    private String android;
    private String email;
    private String whatsApp;
    private String telegram;

    protected SocialLinks(Parcel in) {
        youtube = in.readString();
        website = in.readString();
        android = in.readString();
        email = in.readString();
        whatsApp = in.readString();
        telegram = in.readString();
    }

    public SocialLinks() {
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SocialLinks that = (SocialLinks) o;
        return Objects.equals(youtube, that.youtube) &&
                Objects.equals(website, that.website) &&
                Objects.equals(android, that.android) &&
                Objects.equals(email, that.email) &&
                Objects.equals(whatsApp, that.whatsApp) &&
                Objects.equals(telegram, that.telegram);
    }

    @Override
    public int hashCode() {
        return Objects.hash(youtube, website, android, email, whatsApp, telegram);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(youtube);
        dest.writeString(website);
        dest.writeString(android);
        dest.writeString(email);
        dest.writeString(whatsApp);
        dest.writeString(telegram);
    }
}
