package com.ramindu.weeraman.weather.data.local;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ramindu.weeraman.weather.models.SelectedCity;

import java.util.List;

import io.reactivex.Single;


@Dao
public interface WeatherDataDao {

    @Query("SELECT * FROM SelectedCity")
    Single<List<SelectedCity>> getAllCities();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCityList(List<SelectedCity> cities);

    @Query("Delete FROM SelectedCity WHERE id = :id")
    Single<Integer> deleteCity(Long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addCity(SelectedCity selectedCity);

}
