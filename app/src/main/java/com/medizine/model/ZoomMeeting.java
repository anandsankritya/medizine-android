package com.medizine.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import javax.annotation.Nullable;

public class ZoomMeeting implements Parcelable {
    public static final Creator<ZoomMeeting> CREATOR = new Creator<ZoomMeeting>() {
        @Override
        public ZoomMeeting createFromParcel(Parcel in) {
            return new ZoomMeeting(in);
        }

        @Override
        public ZoomMeeting[] newArray(int size) {
            return new ZoomMeeting[size];
        }
    };
    private String id;
    private MultiLingual meetingTitle;
    private String moduleId;
    private String hostUserId;
    private String moduleType;
    private String meetingNumber;
    private String meetingStatus;
    @Nullable
    private String meetingPassword;
    private String meetingStartTime;
    private String meetingDuration;
    private String meetingUserCount;
    private MediaLink meetingThumbnail;

    public ZoomMeeting() {
    }

    protected ZoomMeeting(Parcel in) {
        id = in.readString();
        meetingTitle = in.readParcelable(MultiLingual.class.getClassLoader());
        moduleId = in.readString();
        hostUserId = in.readString();
        moduleType = in.readString();
        meetingNumber = in.readString();
        meetingStatus = in.readString();
        meetingPassword = in.readString();
        meetingStartTime = in.readString();
        meetingDuration = in.readString();
        meetingUserCount = in.readString();
        meetingThumbnail = in.readParcelable(MediaLink.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitleAsString() {
        return meetingTitle != null ? meetingTitle.getString() : null;
    }

    public MultiLingual getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(MultiLingual meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public String getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(String meetingStatus) {
        this.meetingStatus = meetingStatus;
    }

    @Nullable
    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(@Nullable String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public String getMeetingStartTime() {
        return meetingStartTime;
    }

    public void setMeetingStartTime(String meetingStartTime) {
        this.meetingStartTime = meetingStartTime;
    }

    public String getMeetingDuration() {
        return meetingDuration;
    }

    public void setMeetingDuration(String meetingDuration) {
        this.meetingDuration = meetingDuration;
    }

    public String getMeetingUserCount() {
        return meetingUserCount;
    }

    public void setMeetingUserCount(String meetingUserCount) {
        this.meetingUserCount = meetingUserCount;
    }

    public MediaLink getMeetingThumbnail() {
        return meetingThumbnail;
    }

    public void setMeetingThumbnail(MediaLink meetingThumbnail) {
        this.meetingThumbnail = meetingThumbnail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ZoomMeeting)) return false;
        ZoomMeeting meeting = (ZoomMeeting) o;
        return Objects.equals(getId(), meeting.getId()) &&
                Objects.equals(getMeetingTitle(), meeting.getMeetingTitle()) &&
                Objects.equals(getModuleId(), meeting.getModuleId()) &&
                Objects.equals(getHostUserId(), meeting.getHostUserId()) &&
                Objects.equals(getModuleType(), meeting.getModuleType()) &&
                Objects.equals(getMeetingNumber(), meeting.getMeetingNumber()) &&
                Objects.equals(getMeetingStatus(), meeting.getMeetingStatus()) &&
                Objects.equals(getMeetingPassword(), meeting.getMeetingPassword()) &&
                Objects.equals(getMeetingStartTime(), meeting.getMeetingStartTime()) &&
                Objects.equals(getMeetingDuration(), meeting.getMeetingDuration()) &&
                Objects.equals(getMeetingUserCount(), meeting.getMeetingUserCount()) &&
                Objects.equals(getMeetingThumbnail(), meeting.getMeetingThumbnail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMeetingTitle(), getModuleId(), getHostUserId(), getModuleType(), getMeetingNumber(), getMeetingStatus(),
                getMeetingPassword(), getMeetingStartTime(), getMeetingDuration(), getMeetingUserCount(), getMeetingThumbnail());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(meetingTitle, i);
        parcel.writeString(moduleId);
        parcel.writeString(hostUserId);
        parcel.writeString(moduleType);
        parcel.writeString(meetingNumber);
        parcel.writeString(meetingStatus);
        parcel.writeString(meetingPassword);
        parcel.writeString(meetingStartTime);
        parcel.writeString(meetingDuration);
        parcel.writeString(meetingUserCount);
        parcel.writeParcelable(meetingThumbnail, i);
    }
}
