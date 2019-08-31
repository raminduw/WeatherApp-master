package com.ramindu.weeraman.weather.di.modules;

import android.app.Application;

import androidx.room.Room;

import com.ramindu.weeraman.weather.R;
import com.ramindu.weeraman.weather.data.local.WeatherDataDao;
import com.ramindu.weeraman.weather.data.local.WeatherDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {AppModule.class})
public class DatabaseModule {

    @Provides
    @Singleton
    WeatherDatabase provideWeatherDatabase(Application application){
        return Room.databaseBuilder(
                application.getApplicationContext(),
                WeatherDatabase.class,
                application.getApplicationContext().getString(R.string.app_name))
                .build();
    }

    @Provides
    @Singleton
    WeatherDataDao provideWeatherDao(WeatherDatabase coinTrackDatabase){
        return coinTrackDatabase.weatherDataDao();
    }
}
