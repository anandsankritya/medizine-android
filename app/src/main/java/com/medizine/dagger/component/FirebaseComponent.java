package com.medizine.dagger.component;

import com.medizine.FirebaseRemoteConfigUpdate;
import com.medizine.MedizineApp;
import com.medizine.dagger.module.AppModule;
import com.medizine.dagger.module.FirebaseModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, FirebaseModule.class})
public interface FirebaseComponent {

    void inject(FirebaseRemoteConfigUpdate firebaseRemoteConfigUpdate);

    void inject(MedizineApp medizineApp);
}