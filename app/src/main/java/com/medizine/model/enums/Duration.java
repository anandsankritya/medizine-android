package com.medizine.model.enums;

import android.content.Context;

import com.medizine.R;

import java.lang.reflect.Field;

public enum Duration {
    UNKNOWN("UNKNOWN", R.string.unknown),
    FIFTEEN_MINUTES("FIFTEEN_MINUTES", R.string.duration_15_mins),
    THIRTY_MINUTES("THIRTY_MINUTES", R.string.duration_30_mins),
    FORTY_FIVE_MINUTES("FORTY_FIVE_MINUTES", R.string.duration_45_mins),
    ONE_HR("ONE_HR", R.string.duration_one_hr),
    ONE_AND_HALF_HRS("ONE_AND_HALF_HRS", R.string.duration_one_and_half_hrs),
    TWO_HRS("TWO_HRS", R.string.duration_two_hrs),
    TWO_AND_HALF_HRS("TWO_AND_HALF_HRS", R.string.duration_two_and_half_hrs),
    THREE_HRS("THREE_HRS", R.string.duration_three_hrs);

    private final int resourceId;

    Duration(String name, int resourceId) {
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
        }
    }

    public String getLocaleString(Context context) {
        return context.getResources().getString(resourceId);
    }

}
