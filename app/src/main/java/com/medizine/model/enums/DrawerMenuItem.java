package com.medizine.model.enums;

import android.content.Context;

import com.medizine.R;

public enum DrawerMenuItem {
    APPOINTMENTS(R.string.appointments, R.drawable.ic_calender),
    CONTACT_US(R.string.contact_us, R.drawable.ic_phone_grey),
    SHARE_APP(R.string.share_app, R.drawable.ic_share_grey),
    LOG_OUT(R.string.log_out, R.drawable.ic_baseline_exit_to_app_24);

    private final int resourceId;
    private final int resDrawable;

    DrawerMenuItem(int resourceId, int resDrawable) {
        this.resourceId = resourceId;
        this.resDrawable = resDrawable;
    }

    public static DrawerMenuItem fromValue(int resourceId) {
        for (DrawerMenuItem item : values()) {
            if (item.resourceId == resourceId)
                return item;
        }
        return SHARE_APP;
    }

    public String getLocaleString(Context context) {
        return context.getResources().getString(resourceId);
    }

    public int getImageResource() {
        return resDrawable;
    }
}