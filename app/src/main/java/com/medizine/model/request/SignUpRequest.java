package com.medizine.model.request;

public class SignUpRequest {
    String countryCode;
    String mobile;
    String otp;
    String name;


    public SignUpRequest(String countryCode, String mobile, String otp, String name) {
        this.countryCode = countryCode;
        this.mobile = mobile;
        this.otp = otp;
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
