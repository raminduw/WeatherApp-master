package com.ramindu.weeraman.weather.di.modules;


import androidx.lifecycle.ViewModel;

import com.ramindu.weeraman.weather.viewmodels.WeatherViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;


@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel.class)
    abstract ViewModel weatherViewModel(WeatherViewModel weatherViewModel);


}
