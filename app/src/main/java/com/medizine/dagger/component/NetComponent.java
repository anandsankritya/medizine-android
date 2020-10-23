package com.medizine.dagger.component;

import com.medizine.dagger.module.NetModule;
import com.medizine.network.NetworkService;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {NetModule.class})
public interface NetComponent {
    Retrofit retrofit();

    void inject(NetworkService networkService);
}
