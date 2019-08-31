package com.ramindu.weeraman.weather;

import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.ramindu.weeraman.weather.data.WeatherRepository;
import com.ramindu.weeraman.weather.data.photo.ImageResponse;
import com.ramindu.weeraman.weather.models.ListItemView;
import com.ramindu.weeraman.weather.models.SelectedCity;
import com.ramindu.weeraman.weather.models.response.WeatherCities;
import com.ramindu.weeraman.weather.utils.SchedulerProvider;
import com.ramindu.weeraman.weather.viewmodels.LoadingStatus;
import com.ramindu.weeraman.weather.viewmodels.WeatherViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeatherDataViewModelTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private WeatherViewModel viewModel;
    private WeatherRepository repository;
    private CompositeDisposable compositeDisposable;
    private SchedulerProvider schedulerProvider;


    @Before
    public void before() throws Exception {
        repository = mock(WeatherRepository.class);
        schedulerProvider = mock(SchedulerProvider.class);
        compositeDisposable = mock(CompositeDisposable.class);
        viewModel = new WeatherViewModel(repository,schedulerProvider, compositeDisposable);

    }
     @Test
    public void testSelectedCityAvailablity() {
         boolean  isAvailable = viewModel.isCityAlreadyAvailable("Colombo");
         assertFalse(isAvailable);
     }


    @Test
    public void testRemoveCityWithError() {
        ListItemView item = Mockito.mock(ListItemView.class);

        Exception exception = new Exception();
        when(repository.deleteCity(item))
                .thenReturn(Single.<Integer>error(exception));

        when(schedulerProvider.getBackgroundScheduler())
                .thenReturn(Schedulers.trampoline());

        when(schedulerProvider.getMainScheduler())
                .thenReturn(Schedulers.trampoline());


        viewModel.removeCity(item);
        assertEquals(LoadingStatus.FAIL, viewModel.getDeleteCityStatus().getValue());
    }

    @Test
    public void testRemoveCitySuccess() {
        ListItemView item = Mockito.mock(ListItemView.class);

        when(repository.deleteCity(item))
                .thenReturn(Single.just(1));

        when(schedulerProvider.getBackgroundScheduler())
                .thenReturn(Schedulers.trampoline());

        when(schedulerProvider.getMainScheduler())
                .thenReturn(Schedulers.trampoline());


        viewModel.removeCity(item);
        assertEquals(LoadingStatus.SUCCESS, viewModel.getDeleteCityStatus().getValue());
    }


    @Test
    public void testRetrieveWeatherListFail() {

        Exception exception = new Exception();
        when(repository.getSelectedCities())
                .thenReturn(Single.<List<SelectedCity>>error(exception));

        when(schedulerProvider.getBackgroundScheduler())
                .thenReturn(Schedulers.trampoline());

        when(schedulerProvider.getMainScheduler())
                .thenReturn(Schedulers.trampoline());

        viewModel.loadInitialWeatherList();

        assertEquals(LoadingStatus.FAIL, viewModel.getWeatherDataLoadingStatus().getValue());
    }

    @Test
    public void testRetrieveWeatherListSuccess() {
        List<SelectedCity> list = new ArrayList<>();
        SelectedCity selectedCity = Mockito.mock(SelectedCity.class);
        list.add(selectedCity);

        WeatherCities weatherCities =Mockito.mock(WeatherCities.class);
        ImageResponse imageResponse =Mockito.mock(ImageResponse.class);

        ListItemView item = Mockito.mock(ListItemView.class);
        List<ListItemView> list1 = new ArrayList<>();
        list1.add(item);

        when(schedulerProvider.getBackgroundScheduler())
                .thenReturn(Schedulers.trampoline());

        when(schedulerProvider.getMainScheduler())
                .thenReturn(Schedulers.trampoline());

        when(repository.getUnit())
                .thenReturn("key");
        when(repository.getUnit())
                .thenReturn("unit");

        when(repository.generateItems(weatherCities)).thenReturn(list1);

        when(selectedCity.getName())
                .thenReturn("colombo");

        when(item.getName())
                .thenReturn("colombo");

        when(item.getTemp())
                .thenReturn(20.0);

        when(item.getId())
                .thenReturn(2L);

        when(item.getDescription())
                .thenReturn("clear");


        when(repository.getSelectedCities())
                .thenReturn(Single.just(list));

        when(repository.getWeather(list)).thenReturn(Observable.just(weatherCities));

        when(repository.getImageObservable(selectedCity.getName())).thenReturn(Observable.just(imageResponse));

        when(repository.generateImageUrl(imageResponse)).thenReturn("image_url");

        viewModel.loadInitialWeatherList();

        assertEquals(LoadingStatus.SUCCESS, viewModel.getWeatherDataLoadingStatus().getValue());
    }

}
