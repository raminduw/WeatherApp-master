package com.ramindu.weeraman.weather.di.modules;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class DisposableModule {
    private CompositeDisposable compositeDisposable;

    @Provides
    public CompositeDisposable provideDisposable(){
        compositeDisposable = new CompositeDisposable();
        return compositeDisposable;
    }
}


