package com.medizine.network;

import com.medizine.Constants;
import com.medizine.MedizineApp;
import com.medizine.db.StorageService;
import com.medizine.model.Response;
import com.medizine.model.entity.Appointment;
import com.medizine.model.entity.Doctor;
import com.medizine.model.entity.Slot;
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
    public Single<Response<User>> getUserById(String userId) {
        return retrofitInterface.getUserById(userId);
    }

    public Single<Response<User>> getUserByPhoneNumber(String phoneNumber) {
        return retrofitInterface.getUserByPhoneNumber(Constants.COUNTRY_CODE_IN, phoneNumber);
    }

    public Single<Response<User>> createUser(User user) {
        return retrofitInterface.createUser(user);
    }

    public Single<Response<User>> patchUserById(User user) {
        return retrofitInterface.patchUserById(StorageService.getInstance().getUser().getId(), user);
    }

    //Doctor APIs
    public Single<Response<Doctor>> getDoctorById(String doctorId) {
        return retrofitInterface.getDoctorById(doctorId);
    }

    public Single<Response<Doctor>> getDoctorByPhoneNumber(String phoneNumber) {
        return retrofitInterface.getDoctorByPhoneNumber(Constants.COUNTRY_CODE_IN, phoneNumber);
    }

    public Single<Response<Doctor>> createDoctor(Doctor doctor) {
        return retrofitInterface.createDoctor(doctor);
    }

    public Single<Response<Doctor>> patchDoctorById(Doctor doctor) {
        return retrofitInterface.patchDoctorById(StorageService.getInstance().getDoctor().getId(), doctor);
    }

    public Single<Response<List<Doctor>>> getAllDoctors() {
        return retrofitInterface.getAllDoctors();
    }

    //Slot APIs
    public Single<Response<Appointment>> bookAppointment(Slot slot) {
        return retrofitInterface.book(slot);
    }

    public Single<Response<Slot>> createSlot(Slot slot) {
        return retrofitInterface.create(slot);
    }

    public Single<Response<List<Slot>>> getAllSlotsByDoctorId(String doctorId) {
        return retrofitInterface.getAllSlotsByDoctorId(doctorId);
    }

    public Single<Response<List<Slot>>> getLiveSlotStatus(String date, String doctorId, String userId) {
        return retrofitInterface.getLiveSlotStatus(date, doctorId, userId);
    }

}