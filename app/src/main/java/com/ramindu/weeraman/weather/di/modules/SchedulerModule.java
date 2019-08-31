package com.ramindu.weeraman.weather.di.modules;


import com.ramindu.weeraman.weather.utils.SchedulerProvider;
import com.ramindu.weeraman.weather.utils.SchedulerProviderImp;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class SchedulerModule {

    @Binds
    @Singleton
    abstract SchedulerProvider providerScheduler(SchedulerProviderImp schedulerProvider) ;
}