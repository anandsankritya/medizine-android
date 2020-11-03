package com.medizine.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medizine.MedizineApp;
import com.medizine.model.entity.Doctor;
import com.medizine.model.entity.User;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

import static com.medizine.Constants.AUTH_PRIVATE_KEY;

public enum StorageService {
    INSTANCE;

    private static final String TAG = StorageService.class.getSimpleName();

    @Inject
    MedizineDatabase mMedizineDatabase;
    private User mUser;
    private Doctor mDoctor;
    private String privateKey;

    StorageService() {
        MedizineApp.getStorageComponent().inject(this);
    }

    @NonNull
    public static StorageService getInstance() {
        return INSTANCE;
    }

    public MedizineDatabase getMedizineDatabase() {
        return mMedizineDatabase;
    }

    public void clearCache() {
        mUser = null;
        mDoctor = null;
    }

    public User getUser() {
        if (mUser == null) {
            mUser = getMedizineDatabase()
                    .userDao()
                    .get()
                    .subscribeOn(Schedulers.io())
                    .blockingGet();
        }
        return mUser;
    }

    public Doctor getDoctor() {
        if (mDoctor == null) {
            mDoctor = getMedizineDatabase()
                    .doctorDao()
                    .get()
                    .subscribeOn(Schedulers.io())
                    .blockingGet();
        }
        return mDoctor;
    }

    public void updateUser(@Nullable User user) {
        if (user != null) {
            mUser = user;
            getMedizineDatabase().userDao().insertOrUpdate(user).subscribeOn(Schedulers.io()).blockingAwait();
        }
    }

    public void updateDoctor(@Nullable Doctor doctor) {
        if (doctor != null) {
            mDoctor = doctor;
            getMedizineDatabase().doctorDao().insertOrUpdate(doctor).subscribeOn(Schedulers.io()).blockingAwait();
        }
    }

    public String getPrivateKey() {
        if (privateKey == null) {
            privateKey = PrefService.getInstance().getString(AUTH_PRIVATE_KEY, "");
        }
        return privateKey;
    }

    public void storePrivateKey(String key) {
        PrefService.getInstance().saveData(AUTH_PRIVATE_KEY, key);
        privateKey = key;
    }

}
