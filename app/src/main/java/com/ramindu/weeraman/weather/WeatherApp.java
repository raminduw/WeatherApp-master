package com.ramindu.weeraman.weather;

import android.app.Application;

import com.ramindu.weeraman.weather.di.AppComponent;
import com.ramindu.weeraman.weather.di.DaggerAppComponent;
import com.ramindu.weeraman.weather.di.modules.AppModule;
import com.ramindu.weeraman.weather.di.modules.NetModule;


public class WeatherApp extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule("http://api.openweathermap.org/data/2.5/"))
                .build();
    }

    public AppComponent getAppComponent(){
        return mAppComponent;
    }
}
