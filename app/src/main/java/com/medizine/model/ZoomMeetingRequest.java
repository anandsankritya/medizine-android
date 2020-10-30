package com.medizine.model;

import java.util.LinkedHashMap;

public class ZoomMeetingRequest {
    private MultiLingual meetingTitle;

    private String moduleId;
    private String moduleType;

    private String hostUserId;

    private String meetingNumber;
    private String meetingPassword;
    private String meetingStatus;
    private String meetingStartTime;
    private String meetingDuration;
    private String meetingUserCount;

    private String mediaLinkId;

    private LinkedHashMap<String, String> meetingMetaData;

    public ZoomMeetingRequest() {
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

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public String getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(String meetingStatus) {
        this.meetingStatus = meetingStatus;
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

    public String getMediaLinkId() {
        return mediaLinkId;
    }

    public void setMediaLinkId(String mediaLinkId) {
        this.mediaLinkId = mediaLinkId;
    }

    public LinkedHashMap<String, String> getMeetingMetaData() {
        return meetingMetaData;
    }

    public void setMeetingMetaData(LinkedHashMap<String, String> meetingMetaData) {
        this.meetingMetaData = meetingMetaData;
    }
}
