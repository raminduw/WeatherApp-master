package com.ramindu.weeraman.weather.utils;

import io.reactivex.Scheduler;

public interface SchedulerProvider {

    Scheduler getMainScheduler();
    Scheduler getBackgroundScheduler();
}