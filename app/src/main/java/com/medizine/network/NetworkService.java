package com.medizine.network;

import com.medizine.Constants;
import com.medizine.MedizineApp;
import com.medizine.db.StorageService;
import com.medizine.model.Response;
import com.medizine.model.entity.Doctor;
import com.medizine.model.entity.User;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
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

    //User APIs
    public Single<Response<User>> getUserByPhoneNumber(String phoneNumber) {
        return retrofitInterface.getUserByPhoneNumber(Constants.COUNTRY_CODE_IN, phoneNumber);
    }

    public Single<Response<User>> createUser(User user) {
        return retrofitInterface.createUser(user);
    }

    public Single<Response<User>> patchUserById(User user) {
        return retrofitInterface.patchUserById(StorageService.getInstance().getUser().getId(), user);
    }

//    public Single<Response<MediaLink>> uploadMedia(String type, MultipartBody.Part requestBodyFile, RequestBody name) {
//        return retrofitInterface.uploadMedia(type, false, requestBodyFile, name);
//    }

    //Doctor APIs
    public Single<Response<Object>> getDoctorByPhoneNumber(String phoneNumber) {
        return retrofitInterface.getDoctorByPhoneNumber(Constants.COUNTRY_CODE_IN, phoneNumber);
    }

    public Single<Response<List<Doctor>>> getAllDoctors() {
        return retrofitInterface.getAllDoctors();
    }

}