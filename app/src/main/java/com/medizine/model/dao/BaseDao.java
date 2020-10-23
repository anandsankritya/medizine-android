package com.medizine.model.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;

public interface BaseDao<T extends Object> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(T... entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdate(T... entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertList(List<T> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdateList(List<T> entities);

    @Update
    Completable update(T entity);

    @Delete
    Completable delete(T... entity);
}
