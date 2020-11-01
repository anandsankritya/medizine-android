package com.medizine.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.medizine.model.entity.Doctor;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface DoctorDao extends BaseDao<Doctor> {
    @Query("SELECT * FROM Doctor")
    Maybe<Doctor> get();

    @Query("SELECT * FROM Doctor")
    LiveData<Doctor> getLiveData();

    @Query("SELECT * FROM Doctor WHERE id = :id")
    LiveData<Doctor> getDoctor(String id);

    @Query("SELECT EXISTS (SELECT 1 FROM Doctor WHERE id = :id)")
    Single<Boolean> exists(String id);
}
