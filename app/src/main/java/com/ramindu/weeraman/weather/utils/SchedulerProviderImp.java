package com.ramindu.weeraman.weather.utils;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SchedulerProviderImp implements SchedulerProvider {

    @Inject
    public SchedulerProviderImp(){
    }

    @Override
    public Scheduler getMainScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public Scheduler getBackgroundScheduler() {
        return Schedulers.io();
    }
}