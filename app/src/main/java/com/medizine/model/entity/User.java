package com.medizine.model.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.medizine.Constants;
import com.medizine.model.MediaLink;
import com.medizine.utils.ImageUtils;

import java.util.Objects;

@Entity
public class User {
    @NonNull
    @PrimaryKey
    private String id;
    private String countryCode;
    private String mobile;
    private String name;
    private String gcmToken;
    private String userType;
    private String profilePicImageId;
    @Embedded(prefix = "profilePic")
    private MediaLink profilePic;
    private String gender;
    private String dateOfBirth;
    private String email;
    private String idProofImageId;
    @Embedded(prefix = "idProof")
    private MediaLink idProof;
    private Boolean isIdProofVerified;

    public User() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfilePicImageId() {
        return profilePicImageId;
    }

    public void setProfilePicImageId(String profilePicImageId) {
        this.profilePicImageId = profilePicImageId;
    }

    public MediaLink getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(MediaLink profilePic) {
        this.profilePic = profilePic;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdProofImageId() {
        return idProofImageId;
    }

    public void setIdProofImageId(String idProofImageId) {
        this.idProofImageId = idProofImageId;
    }

    public MediaLink getIdProof() {
        return idProof;
    }

    public void setIdProof(MediaLink idProof) {
        this.idProof = idProof;
    }

    public Boolean getIdProofVerified() {
        return isIdProofVerified;
    }

    public void setIdProofVerified(Boolean idProofVerified) {
        isIdProofVerified = idProofVerified;
    }

    @Nullable
    public String getProfilePicAsString() {
        if (profilePic != null) {
            return ImageUtils.getImageUrl(profilePic.getS3Links(), Constants.THUMBNAIL);
        }
        return null;
    }

    @Nullable
    public String getIdProofAsString() {
        if (idProof != null) {
            return idProof.getS3Links().get(0).getLink();
        }
        return null;
    }

    public String getCountryCode() {
        if (countryCode != null && !countryCode.isEmpty()) {
            return countryCode;
        } else {
            return "+91";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                Objects.equals(countryCode, user.countryCode) &&
                Objects.equals(mobile, user.mobile) &&
                Objects.equals(name, user.name) &&
                Objects.equals(gcmToken, user.gcmToken) &&
                Objects.equals(userType, user.userType) &&
                Objects.equals(profilePicImageId, user.profilePicImageId) &&
                Objects.equals(profilePic, user.profilePic) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(dateOfBirth, user.dateOfBirth) &&
                Objects.equals(email, user.email) &&
                Objects.equals(idProofImageId, user.idProofImageId) &&
                Objects.equals(idProof, user.idProof) &&
                Objects.equals(isIdProofVerified, user.isIdProofVerified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, countryCode, mobile, name, gcmToken, userType, profilePicImageId, profilePic, gender, dateOfBirth, email, idProofImageId, idProof, isIdProofVerified);
    }
}

