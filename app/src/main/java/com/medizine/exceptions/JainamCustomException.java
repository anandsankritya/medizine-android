package com.medizine.exceptions;

/**
 * Created by vivek on 13/05/17.
 */

public abstract class JainamCustomException extends RuntimeException {
    JainamCustomException(String exception) {
        super(exception);
        // need to log via Crashlytics
    }
}
