package com.ramindu.weeraman.weather.viewmodels;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ramindu.weeraman.weather.data.WeatherRepository;
import com.ramindu.weeraman.weather.data.photo.ImageResponse;
import com.ramindu.weeraman.weather.models.ListItemView;
import com.ramindu.weeraman.weather.models.SelectedCity;
import com.ramindu.weeraman.weather.utils.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;



public class WeatherViewModel extends ViewModel {
    public static final String TAG = WeatherViewModel.class.getSimpleName();
    private WeatherRepository weatherRepository;
    private SchedulerProvider schedulerProvider;
    private CompositeDisposable compositeDisposable;

    //status
    private MutableLiveData<LoadingStatus> weatherDataLoadingStatus = new MutableLiveData<>();
    private MutableLiveData<LoadingStatus> deleteCityStatus = new MutableLiveData<>();

    //data
    private MutableLiveData<ListItemView> addedCityLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ListItemView>> selectedCitiesLiveData = new MutableLiveData<>();
    private List<ListItemView> selectedCities = new ArrayList<>();

    @Inject
    public WeatherViewModel(WeatherRepository weatherRepository, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        this.weatherRepository = weatherRepository;
        this.schedulerProvider = schedulerProvider;
        this.compositeDisposable = compositeDisposable;
    }

    public MutableLiveData<LoadingStatus> getWeatherDataLoadingStatus() {
        return weatherDataLoadingStatus;
    }

    public MutableLiveData<LoadingStatus> getDeleteCityStatus() {
        return deleteCityStatus;
    }

    public MutableLiveData<List<ListItemView>> getSelectedCitiesLiveData() {
        return selectedCitiesLiveData;
    }

    public MutableLiveData<ListItemView> getAddedCityLiveData() {
        return addedCityLiveData;
    }

    public boolean isCityAlreadyAvailable(String city) {
        if (selectedCities != null && selectedCities.size() > 0) {
            for (ListItemView localCity : selectedCities) {
                if (localCity.getName().equalsIgnoreCase(city)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void loadInitialWeatherList() {
        retrieveSavedCityList();
    }

    private void retrieveSavedCityList() {
        selectedCities.clear();
        weatherRepository.getSelectedCities().subscribeOn(schedulerProvider.getBackgroundScheduler())
                .observeOn(schedulerProvider.getMainScheduler())
                .subscribe(new SingleObserver<List<SelectedCity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        weatherDataLoadingStatus.setValue(LoadingStatus.LOADING);
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<SelectedCity> selectedCities) {
                        if (selectedCities == null || selectedCities.size() == 0) {
                            selectedCities = weatherRepository.getDefaultCities();
                        }
                        loadImage(invokeWeatherDataList(selectedCities));
                    }

                    @Override
                    public void onError(Throwable e) {
                        weatherDataLoadingStatus.setValue(LoadingStatus.FAIL);
                    }
                });
    }

    private Observable<List<ListItemView>> invokeWeatherDataList(List<SelectedCity> cities) {
        return weatherRepository.getWeather(cities
               )
                .map(weatherCities -> weatherRepository.generateItems(weatherCities)).doOnNext(listItemViews -> weatherRepository.addCityList(cities));
    }

    public void addCity(String cityName) {
        loadImage(getCityWeatherData(cityName));
    }

    private void loadImage(Observable<List<ListItemView>> observable) {
        observable.flatMap(listItemViews -> Observable.fromIterable(listItemViews)).
                flatMap(listItemView -> Observable.zip(weatherRepository.getImageObservable(listItemView.getName()),
                        Observable.just(listItemView), (imageResponse, list) -> new Pair<>(imageResponse, list)
                )).subscribeOn(schedulerProvider.getBackgroundScheduler())
                .observeOn(schedulerProvider.getMainScheduler())
                .subscribe(new Observer<Pair<ImageResponse, ListItemView>>() {
                    @Override
                    public void onComplete() {
                        selectedCitiesLiveData.setValue(selectedCities);
                        weatherDataLoadingStatus.setValue(LoadingStatus.SUCCESS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        weatherDataLoadingStatus.setValue(LoadingStatus.FAIL);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Pair<ImageResponse, ListItemView> pair) {
                        selectedCities.add(weatherRepository.generateListViewItem(pair));
                    }
                });
    }


    private Observable<List<ListItemView>> getCityWeatherData(String cityName) {
        return weatherRepository.getWeather(cityName)
                .map(weatherCities -> weatherRepository.generateItems(weatherCities)).doOnNext(listItemViews ->
                        weatherRepository.addCity(new SelectedCity(listItemViews.get(0).getId(),
                                listItemViews.get(0).getName())));
    }

    public void removeCity(ListItemView item) {
        weatherRepository.deleteCity(item).subscribeOn(schedulerProvider.getBackgroundScheduler())
                .observeOn(schedulerProvider.getMainScheduler()).
                subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        deleteCityStatus.setValue(LoadingStatus.LOADING);
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer localCities) {
                        removeCityFromList(item);
                        deleteCityStatus.setValue(LoadingStatus.SUCCESS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        deleteCityStatus.setValue(LoadingStatus.FAIL);
                    }
                });
    }

    private void removeCityFromList(ListItemView item) {
        for (ListItemView listItemView : selectedCities) {
            if (item.getId() == listItemView.getId()) {
                selectedCities.remove(listItemView);
                break;
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

}