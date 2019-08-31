package com.ramindu.weeraman.weather.di;
import com.ramindu.weeraman.weather.di.modules.AppModule;
import com.ramindu.weeraman.weather.di.modules.DatabaseModule;
import com.ramindu.weeraman.weather.di.modules.DisposableModule;
import com.ramindu.weeraman.weather.di.modules.NetModule;
import com.ramindu.weeraman.weather.di.modules.RepositoryModule;
import com.ramindu.weeraman.weather.di.modules.SchedulerModule;
import com.ramindu.weeraman.weather.di.modules.ViewModelModule;
import com.ramindu.weeraman.weather.views.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        NetModule.class,
        ViewModelModule.class,
        RepositoryModule.class,
        SchedulerModule.class,
        DisposableModule.class,
        DatabaseModule.class,
})

public interface AppComponent {
    void inject(MainActivity mainActivity);
}
