package com.ramindu.weeraman.weather.di.modules;


import com.ramindu.weeraman.weather.data.WeatherRepository;
import com.ramindu.weeraman.weather.data.local.WeatherDataDao;
import com.ramindu.weeraman.weather.data.remote.WeatherApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(includes = {AppModule.class, NetModule.class, DatabaseModule.class})
public class RepositoryModule {
    @Provides
    @Singleton
    WeatherRepository provideCoinTrackRepository(WeatherDataDao weatherDataDao,
                                                 WeatherApiService weatherApiService){
        return new WeatherRepository(weatherDataDao, weatherApiService);
    }
}
