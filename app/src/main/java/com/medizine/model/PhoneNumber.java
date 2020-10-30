package com.medizine.model;

import android.widget.EditText;

public class PhoneNumber {
    private EditText countryCode;
    private EditText mobileNumber;

    public PhoneNumber(EditText countryCode, EditText mobileNumber) {
        this.countryCode = countryCode;
        this.mobileNumber = mobileNumber;
    }

    public EditText getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(EditText countryCode) {
        this.countryCode = countryCode;
    }

    public EditText getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(EditText mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
