package com.medizine.dagger.component;

import android.app.Application;

import com.medizine.dagger.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    Application application();
}
