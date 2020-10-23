package com.medizine.dagger.module;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.medizine.Constants;
import com.medizine.db.MedizineDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.medizine.db.DBMigration.MIGRATION_1_2;

@Module
public class DbModule {

    @Provides
    @Singleton
    MedizineDatabase medizineDatabase(@NonNull Application application) {
        MedizineDatabase medizineDatabase = Room.databaseBuilder(application.getApplicationContext(), MedizineDatabase.class, Constants.APP_NAME)
                .addMigrations(MIGRATION_1_2)
                .build();

//        if (BuildConfig.FLAVOR.equals("dev")) {
//            Stetho.initialize(
//                    Stetho.newInitializerBuilder(application.getApplicationContext())
//                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(application.getApplicationContext()))
//                            .build());
//        }

        return medizineDatabase;
    }
}
