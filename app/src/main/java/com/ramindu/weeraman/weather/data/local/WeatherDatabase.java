package com.ramindu.weeraman.weather.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ramindu.weeraman.weather.models.SelectedCity;

@Database(entities = {SelectedCity.class}, version = 1, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDataDao weatherDataDao();
}
