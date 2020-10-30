package com.medizine.dagger.module;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.medizine.Constants;
import com.medizine.db.MedizineDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {
    @Provides
    @Singleton
    MedizineDatabase medizineDatabase(@NonNull Application application) {
        return Room.databaseBuilder(application.getApplicationContext(), MedizineDatabase.class, Constants.APP_NAME).build();
    }
}
