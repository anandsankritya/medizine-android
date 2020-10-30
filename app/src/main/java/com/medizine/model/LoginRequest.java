package com.medizine.model;

public class LoginRequest {
    String countryCode;
    String mobile;
    String otp;


    public LoginRequest(String countryCode, String mobile, String otp) {
        this.countryCode = countryCode;
        this.mobile = mobile;
        this.otp = otp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
