package com.medizine.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.medizine.model.converters.DateConverter;
import com.medizine.model.converters.StringListConverter;
import com.medizine.model.dao.UserDao;
import com.medizine.model.entity.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters({StringListConverter.class, DateConverter.class})
public abstract class MedizineDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}