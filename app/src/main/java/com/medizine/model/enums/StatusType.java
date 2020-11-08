package com.medizine.model.enums;

import com.medizine.utils.Utils;

import java.lang.reflect.Field;

public enum StatusType {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    StatusType(String name) {
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
            Utils.logException("StatusType", e);
        }
    }
}
