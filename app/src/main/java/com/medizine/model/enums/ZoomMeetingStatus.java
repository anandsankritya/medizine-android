package com.medizine.model.enums;

import android.content.Context;

import com.medizine.R;

import java.lang.reflect.Field;

public enum ZoomMeetingStatus {
    COMPLETED("COMPLETED", R.string.zoom_status_completed),
    LIVE("LIVE", R.string.zoom_status_live),
    UPCOMING("UPCOMING", R.string.upcoming),
    UNKNOWN("UNKNOWN", R.string.unknown);

    private final int resourceId;

    ZoomMeetingStatus(String name, int resourceId) {

        this.resourceId = resourceId;

        try {
            Field fieldName = null;
            if (getClass().getSuperclass() != null) {
                fieldName = getClass().getSuperclass().getDeclaredField("name");
            }
            if (fieldName != null) {
                fieldName.setAccessible(true);
                fieldName.set(this, name);
                fieldName.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getLocaleString(Context context) {
        return context.getResources().getString(resourceId);
    }

}
