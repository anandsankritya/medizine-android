package com.medizine.model.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.medizine.utils.Utils;

@Entity
public class Slot {
    @PrimaryKey
    @NonNull
    private String id;
    private Boolean booked;
    private Boolean bookedBySameUser;
    private String doctorId;
    private String endTime;
    private String startTime;

    public Slot() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public Boolean getBooked() {
        return booked;
    }

    public void setBooked(Boolean booked) {
        this.booked = booked;
    }

    public Boolean getBookedBySameUser() {
        return bookedBySameUser;
    }

    public void setBookedBySameUser(Boolean bookedBySameUser) {
        this.bookedBySameUser = bookedBySameUser;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Nullable
    public String getFormattedStartTime() {
        if (Utils.isNullOrEmpty(startTime)) {
            return null;
        }
        return Utils.getFormattedTimeFromIsoDateString(startTime);
    }

    @Nullable
    public String getFormattedEndTime() {
        if (Utils.isNullOrEmpty(endTime)) {
            return null;
        }
        return Utils.getFormattedTimeFromIsoDateString(endTime);
    }
}
