package com.ramindu.weeraman.weather.di.modules;


import com.ramindu.weeraman.weather.data.remote.WeatherApiService;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



@Module(includes = {AppModule.class})
public class NetModule {

    private String baseUrl;
    public NetModule(String baseUrl){
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    WeatherApiService provideRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(WeatherApiService.class);

    }

}