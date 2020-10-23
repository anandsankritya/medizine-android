package com.medizine.dagger.component;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import com.medizine.dagger.module.AppModule;
import com.medizine.dagger.module.DbModule;
import com.medizine.dagger.module.PrefModule;
import com.medizine.db.MedizineDatabase;
import com.medizine.db.PrefService;
import com.medizine.db.StorageService;

@Singleton
@Component(modules = {AppModule.class, DbModule.class, PrefModule.class})
public interface StorageComponent {
    SharedPreferences sharedPreferences();

    MedizineDatabase medizineDatabase();

    void inject(StorageService storageService);

    void inject(PrefService prefService);
}
