package com.medizine.model.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class User {
    @NonNull
    @PrimaryKey
    private String id;
    private String countryCode;
    private String mobile;
    private String name;
    private String bio;
    private String gcmToken;
    private String userType;
    private String profilePicId;
//    @Embedded(prefix = "userProfilePic")
//    private MediaLink profilePic;
    private String gender;
    private String dateOfBirth;
    private String altMobile;
    private String email;
    private String altCountryCode;
    private String idProofId;
//    @Embedded(prefix = "userIdProof")
//    private MediaLink idProof;
    private Boolean isIdProofVerified;
    private Date subscriptionEndDate;
//    @Embedded(prefix = "settings")
//    private UserSettings settings;
//    @Embedded(prefix = "address")
//    private Address address;
//    @Embedded(prefix = "location")
//    private Point location;
//    @Embedded(prefix = "currentLocation")
//    private Point currentLocation;
//    @Embedded(prefix = "socialLinks")
//    private SocialLinks socialLinks;

    public User() {
    }

//    @Nullable
//    public String getIdProofAsString() {
//        if (idProof != null) {
//            return idProof.getS3Links().get(0).getLink();
//        }
//        return null;
//    }

    public String getAltCountryCode() {
        return altCountryCode;
    }

    public void setAltCountryCode(String altCountryCode) {
        this.altCountryCode = altCountryCode;
    }

    public String getCountryCode() {
        if (countryCode != null && !countryCode.isEmpty()) {
            return countryCode;
        } else {
            return "+91";
        }
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Nullable
    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

//    public MediaLink getProfilePic() {
//        return profilePic;
//    }
//
//    public void setProfilePic(@Nullable MediaLink profilePic) {
//        this.profilePic = profilePic;
//    }
//
//    public MediaLink getIdProof() {
//        return idProof;
//    }
//
//    public void setIdProof(@Nullable MediaLink idProof) {
//        this.idProof = idProof;
//    }
//
//    @Nullable
//    public String getProfilePicAsString() {
//        if (profilePic != null) {
//            return ImageUtils.getImageUrl(profilePic.getS3Links(), Constants.THUMBNAIL);
//        }
//        return null;
//    }

    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    @Nullable
    public String getGender() {
        return gender;
    }

    public void setGender(@Nullable String gender) {
        this.gender = gender;
    }

    @Nullable
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@Nullable String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getAltMobile() {
        return altMobile;
    }

    public void setAltMobile(@Nullable String altMobile) {
        this.altMobile = altMobile;
    }

    public Boolean getIdProofVerified() {
        return isIdProofVerified;
    }

    public void setIdProofVerified(Boolean idProofVerified) {
        isIdProofVerified = idProofVerified;
    }

//    @Nullable
//    public UserSettings getSettings() {
//        return settings;
//    }
//
//    public void setSettings(UserSettings settings) {
//        this.settings = settings;
//    }

    public String getProfilePicId() {
        return profilePicId;
    }

    public void setProfilePicId(String profilePicId) {
        this.profilePicId = profilePicId;
    }

    public String getIdProofId() {
        return idProofId;
    }

    public void setIdProofId(String idProofId) {
        this.idProofId = idProofId;
    }

//    public Address getAddress() {
//        return address;
//    }
//
//    public void setAddress(Address address) {
//        this.address = address;
//    }
//
//    public Point getLocation() {
//        return location;
//    }
//
//    public void setLocation(Point location) {
//        this.location = location;
//    }
//
//    public Point getCurrentLocation() {
//        return currentLocation;
//    }
//
//    public void setCurrentLocation(Point currentLocation) {
//        this.currentLocation = currentLocation;
//    }

//    public String getBioAsString() {
//        return bio != null ? bio.getString() : null;
//    }
//
//    public SocialLinks getSocialLinks() {
//        return socialLinks;
//    }
//
//    public void setSocialLinks(SocialLinks socialLinks) {
//        this.socialLinks = socialLinks;
//    }

}

