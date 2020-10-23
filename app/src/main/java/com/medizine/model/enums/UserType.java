package com.medizine.model.enums;

import android.content.Context;

import com.medizine.R;

import java.lang.reflect.Field;

public enum UserType {
    NORMAL("NORMAL", R.string.normal),
    DOCTOR("DOCTOR", R.string.doctor);

    private final int resourceId;

    UserType(String name, int resourceId) {
        this.resourceId = resourceId;

        try {
            Field fieldName = null;
            if (getClass().getSuperclass() != null)
                fieldName = getClass().getSuperclass().getDeclaredField("name");
            if (fieldName != null) {
                fieldName.setAccessible(true);
                fieldName.set(this, name);
                fieldName.setAccessible(false);
            }
        } catch (Exception e) {
        }
    }

    public String getLocaleString(Context context) {
        return context.getResources().getString(resourceId);
    }
}
