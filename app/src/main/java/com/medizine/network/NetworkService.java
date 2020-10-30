package com.medizine.network;

import com.medizine.Constants;
import com.medizine.MedizineApp;
import com.medizine.db.StorageService;
import com.medizine.model.Feedback;
import com.medizine.model.LoginRequest;
import com.medizine.model.MediaLink;
import com.medizine.model.Response;
import com.medizine.model.entity.AuthUser;
import com.medizine.model.entity.User;
import com.medizine.model.request.SignUpRequest;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class NetworkService {
    public static NetworkService INSTANCE;

    @Inject
    Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    NetworkService() {
        if (MedizineApp.getNetComponent() != null)
            MedizineApp.getNetComponent().inject(this);
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    public static NetworkService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkService();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public Single<Response<Object>> requestOtp(String countryCode, String mobile, boolean isRetry) {
        return retrofitInterface.requestOtp(countryCode, mobile, isRetry);
    }

    public Single<Response<AuthUser>> loginByOtp(LoginRequest loginRequest) {
        return retrofitInterface.loginByOtp(loginRequest);
    }

    public Single<Response<AuthUser>> signUp(SignUpRequest signUpRequest, String referralCode) {
        return retrofitInterface.signUp(signUpRequest, referralCode);
    }

    public Single<Response<Object>> verifyNumber(LoginRequest loginRequest) {
        return retrofitInterface.verifyNumber(loginRequest);
    }

    public Single<Response<User>> getUserProfile(String userId) {
        return retrofitInterface.getUserProfile(userId);
    }

    public Single<Response<Object>> getUserByPhoneNumber(String phoneNumber) {
        return retrofitInterface.getUserByPhoneNumber(Constants.COUNTRY_CODE_IN, phoneNumber);
    }

    public Single<Response<User>> updateUser(User user) {
        return retrofitInterface.updateUser(StorageService.getInstance().getUser().getId(), user);
    }

    public Single<Response<MediaLink>> uploadMedia(String type, MultipartBody.Part requestBodyFile, RequestBody name) {
        return retrofitInterface.uploadMedia(type, false, requestBodyFile, name);
    }

    public Single<Response<Object>> sendFeedback(Feedback feedback) {
        return retrofitInterface.sendFeedbackByNormalUser(feedback);
    }

    //Doctor APIs
    public Single<Response<Object>> getDoctorByPhoneNumber(String phoneNumber) {
        return retrofitInterface.getDoctorByPhoneNumber(Constants.COUNTRY_CODE_IN, phoneNumber);
    }

//    public Single<Response<User>> updateGcmToken(GcmTokenUpdateRequest gcmTokenUpdateRequest) {
//        return retrofitInterface.updateGcmToken(gcmTokenUpdateRequest);
//    }
//
//    public Single<Response<Object>> createZoomMeetingIfNotExists(ZoomMeetingRequest zoomMeetingRequest) {
//        return retrofitInterface.createZoomMeetingIfNotExists(zoomMeetingRequest);
//    }
//
//    public Single<Response<ZoomMeeting>> getLiveMeetingByModuleIdAndType(String moduleType, String moduleId) {
//        return retrofitInterface.getLiveMeetingByModuleIdAndType(moduleType, moduleId);
//    }
//
//    public Single<Response<ZoomMeeting>> updateZoomMeeting(String id, ZoomMeetingRequest zoomMeetingRequest) {
//        return retrofitInterface.updateZoomMeeting(id, zoomMeetingRequest);
//    }

}