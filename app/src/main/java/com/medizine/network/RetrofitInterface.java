package com.medizine.network;


import com.medizine.model.Response;
import com.medizine.model.entity.Appointment;
import com.medizine.model.entity.Doctor;
import com.medizine.model.entity.Slot;
import com.medizine.model.entity.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RetrofitInterface {

    // User APIs
    @GET("/medizine/v1/normalUser/getById")
    Single<Response<User>> getUserById(@Query("id") String userId);

    @GET("/medizine/v1/normalUser/existByPhone")
    Single<Response<User>> getUserByPhoneNumber(@Query("countryCode") String countryCode,
                                                @Query("phoneNumber") String phoneNumber);

    @POST("/medizine/v1/normalUser/create")
    Single<Response<User>> createUser(@Body User user);

    @PATCH("/medizine/v1/normalUser/patchById")
    Single<Response<User>> patchUserById(@Query("id") String userId, @Body User user);

    //Doctor APIs
    @GET("/medizine/v1/doctor/getById")
    Single<Response<Doctor>> getDoctorById(@Query("id") String doctorId);

    @GET("/medizine/v1/doctor/existByPhone")
    Single<Response<Doctor>> getDoctorByPhoneNumber(@Query("countryCode") String countryCode,
                                                    @Query("phoneNumber") String phoneNumber);

    @POST("/medizine/v1/doctor/create")
    Single<Response<Doctor>> createDoctor(@Body Doctor doctor);

    @PATCH("/medizine/v1/doctor/patchById")
    Single<Response<Doctor>> patchDoctorById(@Query("id") String doctorId, @Body Doctor doctor);

    @GET("/medizine/v1/doctor/getMany")
    Single<Response<List<Doctor>>> getAllDoctors();

    //Slot APIs
    @PATCH("/medizine/v1/slot/book")
    Single<Response<Appointment>> book(@Body Slot slot);

    @PUT("/medizine/v1/slot/create")
    Single<Response<Slot>> create(@Body Slot slot);

    @GET("/medizine/v1/slot/getAllByDocId")
    Single<Response<List<Slot>>> getAllSlotsByDoctorId(@Query("doctorId") String doctorId);

    @GET("/medizine/v1/slot/liveSlotStatus")
    Single<Response<List<Slot>>> getLiveSlotStatus(@Query("date") String date,
                                                   @Query("doctorId") String doctorId,
                                                   @Query("userId") String userId);
}
