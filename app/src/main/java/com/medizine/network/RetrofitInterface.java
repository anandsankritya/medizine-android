package com.medizine.network;


import com.medizine.model.Feedback;
import com.medizine.model.LoginRequest;
import com.medizine.model.MediaLink;
import com.medizine.model.Response;
import com.medizine.model.entity.AuthUser;
import com.medizine.model.entity.User;
import com.medizine.model.request.SignUpRequest;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    // User APIs
    @GET("/api/v2/otp/request")
    Single<Response<Object>> requestOtp(@Query("countryCode") String countryCode, @Query("mobile") String mobile, @Query("retry") boolean retry);

    @POST("/api/v2/otp/loginByOtp")
    Single<Response<AuthUser>> loginByOtp(@Body LoginRequest loginRequest);

    @POST("/api/v2/otp/verifyNumber")
    Single<Response<Object>> verifyNumber(@Body LoginRequest loginRequest);

    @POST("/api/v2/user/signup")
    Single<Response<AuthUser>> signUp(@Body SignUpRequest signUpRequest, @Query("invite_code") String referralCode);
//
//    @POST("/api/v2/user/updateGcmToken")
//    Single<Response<User>> updateGcmToken(@Body GcmTokenUpdateRequest gcmTokenUpdateRequest);
//
//    @POST("/user/updateAnalytics")
//    Single<Response<UserAnalytics>> updateUserAnalytics(@Body UserAnalytics userAnalytics);
//
    @PUT("/api/v2/user/{id}/profile")
    Single<Response<User>> updateUser(@Path("id") String userId, @Body User user);

    @GET("/api/v2/user/{id}/profile")
    Single<Response<User>> getUserProfile(@Path("id") String userId);

    @GET("/medizine/v1/normalUser/existByPhone")
    Single<Response<Object>> getUserByPhoneNumber(@Query("countryCode") String countryCode,
                                                  @Query("phoneNumber") String phoneNumber);

    @Multipart
    @POST("/api/v2/media/{type}/upload")
    Single<Response<MediaLink>> uploadMedia(@Path("type") String type,
                                            @Query("override") Boolean override,
                                            @Part MultipartBody.Part file,
                                            @Part("name") RequestBody name);

    @POST("/api/v2/user/feedback")
    Single<Response<Object>> sendFeedbackByNormalUser(@Body Feedback feedback);

    //Doctor APIs
    @GET("/medizine/v1/doctor/existByPhone")
    Single<Response<Object>> getDoctorByPhoneNumber(@Query("countryCode") String countryCode,
                                                    @Query("phoneNumber") String phoneNumber);

}
