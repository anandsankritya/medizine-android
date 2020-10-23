package com.medizine.network;


import com.medizine.model.Feedback;
import com.medizine.model.Response;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitInterface {

    // User APIs
    @GET("/api/v2/otp/request")
    Single<Response<Object>> requestOtp(@Query("countryCode") String countryCode, @Query("mobile") String mobile, @Query("retry") boolean retry);

//    @POST("/api/v2/otp/loginByOtp")
//    Single<Response<AuthUser>> loginByOtp(@Body LoginRequest loginRequest);
//
//    @POST("/api/v2/otp/verifyNumber")
//    Single<Response<Object>> verifyNumber(@Body LoginRequest loginRequest);
//
//    @POST("/api/v2/user/signup")
//    Single<Response<AuthUser>> signUp(@Body SignUpRequest signUpRequest,
//                                      @Query("invite_code") String referralCode);
//
//    @POST("/api/v2/user/updateGcmToken")
//    Single<Response<User>> updateGcmToken(@Body GcmTokenUpdateRequest gcmTokenUpdateRequest);
//
    @POST("/api/v2/user/feedback")
    Single<Response<Object>> sendFeedbackByNormalUser(@Body Feedback feedback);
//
//    @POST("/user/updateAnalytics")
//    Single<Response<UserAnalytics>> updateUserAnalytics(@Body UserAnalytics userAnalytics);
//
//    @PUT("/api/v2/user/{id}/profile")
//    Single<Response<User>> updateUser(@Path("id") String userId, @Body User user);
//
//    @GET("/api/v2/user/{id}/profile")
//    Single<Response<User>> getUserProfile(@Path("id") String userId);
//
//    @GET("/api/v2/user/getUserByCountryCodeAndMobile")
//    Single<Response<String>> getUserByCountryCodeAndMobile(@Query("countryCode") String countryCode,
//                                                           @Query("mobile") String mobile);
//
//    @POST("/api/v2/user/contacts")
//    Single<Response<Object>> syncContacts(@Body UserContactsRequest users);

    //Zoom Meeting APIs
//    @POST("/api/v2/zoomMeetings/createZoomMeetingIfNotExists")
//    Single<Response<Object>> createZoomMeetingIfNotExists(@Body ZoomMeetingRequest zoomMeetingRequest);
//
//    @GET("/api/v2/zoomMeetings/getLiveMeeting/{module}/{moduleId}")
//    Single<Response<ZoomMeeting>> getLiveMeetingByModuleIdAndType(@Path("module") String module, @Path("moduleId") String moduleId);
//
//    @PATCH("/api/v2/zoomMeetings/patchById/{id}")
//    Single<Response<ZoomMeeting>> updateZoomMeeting(@Path("id") String id, @Body ZoomMeetingRequest zoomMeetingRequest);
//
//    @POST("https://gitlab.com/api/v4/projects/{projectId}/issues")
//    Single<Response<Object>> createGitlabIssue(@Path("projectId") String projectId,
//                                               @Query("access_token") String accessToken,
//                                               @Query("title") String gitlabIssueTitle,
//                                               @Query("description") String gitlabIssueDescription,
//                                               @Query("labels") String gitlabIssueLabel);

}
