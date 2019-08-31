package com.ramindu.weeraman.weather.data.remote;


import com.ramindu.weeraman.weather.data.photo.ImageResponse;
import com.ramindu.weeraman.weather.models.response.CityWeather;
import com.ramindu.weeraman.weather.models.response.WeatherCities;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface WeatherApiService {


    @GET("weather")
    Observable<CityWeather> getWeatherForCity(@Query("q") String city,
                                              @Query("APPID") String key, @Query("units") String units);
    @GET("group")
    Observable<WeatherCities> getWeatherForCities(@Query("id") String cities,
                                                  @Query("APPID") String key, @Query("units") String units);
    @GET
    Observable<ImageResponse> getImage(@Url String url);
}
