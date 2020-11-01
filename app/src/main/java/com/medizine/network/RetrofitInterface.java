package com.medizine.network;


import com.medizine.model.Response;
import com.medizine.model.entity.Doctor;
import com.medizine.model.entity.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitInterface {

    // User APIs
    @GET("/medizine/v1/normalUser/existByPhone")
    Single<Response<User>> getUserByPhoneNumber(@Query("countryCode") String countryCode,
                                                @Query("phoneNumber") String phoneNumber);

    @POST("/medizine/v1/normalUser/create")
    Single<Response<User>> createUser(@Body User user);

    @PATCH("/medizine/v1/normalUser/patchById")
    Single<Response<User>> patchUserById(@Query("id") String userId, @Body User user);

//    @Multipart
//    @POST("/api/v2/media/{type}/upload")
//    Single<Response<MediaLink>> uploadMedia(@Path("type") String type,
//                                            @Query("override") Boolean override,
//                                            @Part MultipartBody.Part file,
//                                            @Part("name") RequestBody name);

    //Doctor APIs
    @GET("/medizine/v1/doctor/existByPhone")
    Single<Response<Object>> getDoctorByPhoneNumber(@Query("countryCode") String countryCode,
                                                    @Query("phoneNumber") String phoneNumber);

    @GET("/medizine/v1/doctor/getMany")
    Single<Response<List<Doctor>>> getAllDoctors();
}
