package com.medizine;

import androidx.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface StateLayoutViewMode {

    int PROGRESS = 1;
    int CONTENT = 2;
    int EMPTY = 3;
    int ERROR = 4;

    @Documented
    @IntDef({PROGRESS, CONTENT, EMPTY, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {
    }
}
