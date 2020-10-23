package com.medizine.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.medizine.model.entity.User;

import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface UserDao extends BaseDao<User> {
    @Query("SELECT * FROM User")
    Maybe<User> get();

    @Query("SELECT * FROM User")
    LiveData<User> getLiveData();

    @Query("SELECT * FROM User WHERE id = :id")
    LiveData<User> getUser(String id);

    @Query("SELECT EXISTS (SELECT 1 FROM User WHERE id = :id)")
    Single<Boolean> exists(String id);
}
