package com.medizine.dagger.component;

import com.google.android.gms.analytics.AnalyticsService;
import com.medizine.dagger.module.AnalyticsModule;
import com.medizine.dagger.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AnalyticsModule.class, AppModule.class})
public interface AnalyticsComponent {
    void inject(AnalyticsService analyticsService);
}
