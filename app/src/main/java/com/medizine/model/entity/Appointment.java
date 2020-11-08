package com.medizine.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.medizine.utils.Utils;

@Entity
public class Appointment {
    @PrimaryKey
    @NonNull
    private String id;
    private String slotId;
    private String userId;
    private String doctorId;
    private String status;
    private String appointmentDate;

    public Appointment() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getFormattedAppointmentDate() {
        if (Utils.isNullOrEmpty(appointmentDate)) {
            return null;
        }
        return Utils.getFormattedAppointmentDate(appointmentDate);
    }
}

