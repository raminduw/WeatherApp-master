package com.ramindu.weeraman.weather.viewmodels;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ramindu.weeraman.weather.data.WeatherRepository;
import com.ramindu.weeraman.weather.data.photo.ImageResponse;
import com.ramindu.weeraman.weather.data.photo.Photo;
import com.ramindu.weeraman.weather.models.ListItemView;
import com.ramindu.weeraman.weather.models.SelectedCity;
import com.ramindu.weeraman.weather.models.response.CityWeather;
import com.ramindu.weeraman.weather.models.response.WeatherCities;
import com.ramindu.weeraman.weather.models.response.WeatherList;
import com.ramindu.weeraman.weather.utils.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class WeatherViewModelNew extends ViewModel {
    public static final String TAG = WeatherViewModelNew.class.getSimpleName();
    private WeatherRepository weatherRepository;
    private SchedulerProvider schedulerProvider;
    private CompositeDisposable compositeDisposable;

    //status
    private MutableLiveData<LoadingStatus> weatherDataLoadingStatus = new MutableLiveData<>();
    private MutableLiveData<LoadingStatus> deleteCityStatus = new MutableLiveData<>();

    private MutableLiveData<ListItemView> addedCityLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ListItemView>> selectedCitiesLiveData = new MutableLiveData<>();
    private List<ListItemView> selectedCities = new ArrayList<>();

    @Inject
    public WeatherViewModelNew(WeatherRepository weatherRepository, SchedulerProvider schedulerProvider,
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
        retrieveSaveCityList();
    }

    private void retrieveSaveCityList() {
        selectedCities.clear();
        weatherRepository.getSelectedCities().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
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
                .map(new Function<WeatherCities, List<ListItemView>>() {
                    @Override
                    public List<ListItemView> apply(WeatherCities weatherCities) throws Exception {
                        return generateItems(weatherCities);
                    }
                }).doOnNext(new Consumer<List<ListItemView>>() {
                    @Override
                    public void accept(List<ListItemView> listItemViews) throws Exception {
                        weatherRepository.addCityList(cities);
                    }
                });
    }

    public void addCity(String cityName) {
        loadImage(getCityWeatherData(cityName));
    }

    private void loadImage(Observable<List<ListItemView>> observable) {
        observable.flatMap(new Function<List<ListItemView>, ObservableSource<ListItemView>>() {
            @Override
            public ObservableSource<ListItemView> apply(List<ListItemView> listItemViews) throws Exception {
                return Observable.fromIterable(listItemViews);
            }
        }).flatMap(new Function<ListItemView, ObservableSource<Pair<ImageResponse, ListItemView>>>() {

            @Override
            public ObservableSource<Pair<ImageResponse, ListItemView>> apply(ListItemView listItemView) {
                return Observable.zip(getImageObservable(listItemView.getName()),
                        Observable.just(listItemView),
                        new BiFunction<ImageResponse, ListItemView, Pair<ImageResponse, ListItemView>>() {
                            @Override
                            public Pair<ImageResponse, ListItemView> apply(ImageResponse imageResponse, ListItemView listItemView) {
                                return new Pair<>(imageResponse, listItemView);
                            }
                        });
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
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
                        ImageResponse imageResponse = pair.first;
                        ListItemView listItemView = pair.second;
                        ListItemView newListItem = new ListItemView(listItemView.getId(), listItemView.getName(),
                                listItemView.getTemp(), listItemView.getDescription());
                        newListItem.setImageUrl(generateImageUrl(imageResponse));
                        selectedCities.add(newListItem);
                    }
                });
    }


    private Observable<List<ListItemView>> getCityWeatherData(String cityName) {
        return weatherRepository.getWeather(cityName
                )
                .map(new Function<CityWeather, List<ListItemView>>() {
                    @Override
                    public List<ListItemView> apply(CityWeather weatherCities) throws Exception {
                        return generateItems(weatherCities);
                    }
                }).doOnNext(new Consumer<List<ListItemView>>() {
                    @Override
                    public void accept(List<ListItemView> listItemViews) throws Exception {
                        weatherRepository.addCity(new SelectedCity(listItemViews.get(0).getId(),
                                listItemViews.get(0).getName()));
                    }
                });
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

    private List<ListItemView> generateItems(WeatherCities weatherCities) {
        List<ListItemView> list = new ArrayList<>();
        for (WeatherList weatherList : weatherCities.getList()) {
            list.add(new ListItemView(weatherList.getId(), weatherList.getName(), weatherList.getMain().getTemp(),
                    weatherList.getWeather().get(0).getMain()));
        }
        return list;
    }


    private List<ListItemView> generateItems(CityWeather cityWeather) {
        List<ListItemView> list = new ArrayList<>();
        list.add(new ListItemView(cityWeather.getId(), cityWeather.getName(),
                cityWeather.getMain().getTemp(), cityWeather.getWeather().get(0).getMain()));
        return list;
    }

    private String generateImageUrl(ImageResponse imageResponse) {
        String imageUrl = null;
        Photo photo = imageResponse.getPhotos().getPhoto().get(0);
        imageUrl = "https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + "_b.jpg";
        return imageUrl;
    }

    private Observable<ImageResponse> getImageObservable(String city) {
        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + weatherRepository.getFlickerApiKey() +
                "&accuracy=16&per_page=2&page=1&text=" + city +
                "&sort=date-posted-desc&format=json&geo_context=2&nojsoncallback=?";
        return weatherRepository.getImage(url);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }


}