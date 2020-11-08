package com.medizine.model;

import javax.annotation.Nullable;

public class ZoomMeetingRequest {
    private String appointmentId;
    private String hostId;
    private String meetingNumber;
    @Nullable
    private String meetingPassword;
    private String meetingStatus;

    public ZoomMeetingRequest() {
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    @Nullable
    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(@Nullable String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public String getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(String meetingStatus) {
        this.meetingStatus = meetingStatus;
    }
}
