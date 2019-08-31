package com.ramindu.weeraman.weather.data;


import android.util.Pair;

import com.ramindu.weeraman.weather.data.local.WeatherDataDao;
import com.ramindu.weeraman.weather.data.photo.ImageResponse;
import com.ramindu.weeraman.weather.data.photo.Photo;
import com.ramindu.weeraman.weather.data.remote.WeatherApiService;
import com.ramindu.weeraman.weather.models.response.CityWeather;
import com.ramindu.weeraman.weather.models.ListItemView;
import com.ramindu.weeraman.weather.models.SelectedCity;
import com.ramindu.weeraman.weather.models.response.WeatherCities;
import com.ramindu.weeraman.weather.models.response.WeatherList;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;


public class WeatherRepository {
    private WeatherDataDao weatherDataDao;
    private WeatherApiService weatherApiService;

    public WeatherRepository(WeatherDataDao weatherDataDao, WeatherApiService weatherApiService) {
        this.weatherApiService = weatherApiService;
        this.weatherDataDao = weatherDataDao;
    }

    public Observable<CityWeather> getWeather(String city) {
        return weatherApiService.getWeatherForCity(city,getApiKey(), getUnit());
    }

    public Observable<WeatherCities> getWeather(List<SelectedCity> list) {
        String cities = "";
        for (SelectedCity selectedCity : list) {
            cities = cities.concat(selectedCity.getId() + ",");
        }
        cities = cities.substring(0, cities.length() - 1);
        return weatherApiService.getWeatherForCities(cities, getApiKey(), getUnit());
    }

    public Single<List<SelectedCity>> getSelectedCities() {
        return weatherDataDao.getAllCities();
    }

    public void addCityList(List<SelectedCity> localCities) {
        if (localCities!=null) {
            weatherDataDao.addCityList(localCities);
        }
    }

    public void addCity(SelectedCity selectedCity) {
        weatherDataDao.addCity(selectedCity);
    }

    public Single<Integer> deleteCity(ListItemView item) {
        return weatherDataDao.deleteCity(item.getId());
    }

    public Observable<ImageResponse> getImage(String url) {
        return weatherApiService.getImage(url);
    }

    public String getApiKey() {
        return "51750fd19c5b1be5baf82bd9932e9155";
    }

    public String getFlickerApiKey() {
        return "17e0c1cc12ca750ce2d270bee70539bd";
    }

    public String getUnit() {
        return "metric";
    }

    public List<SelectedCity> getDefaultCities() {
        List<SelectedCity> cities = new ArrayList<>();
        cities.add(new SelectedCity(2964574L, "Dublin"));
        cities.add(new SelectedCity(2643743L, "London"));
        cities.add(new SelectedCity(1816670L, "Beijing"));
        cities.add(new SelectedCity(2147714L, "Sydney"));
        return cities;
    }

    public List<ListItemView> generateItems(WeatherCities weatherCities) {
        List<ListItemView> list = new ArrayList<>();
        for (WeatherList weatherList : weatherCities.getList()) {
            list.add(new ListItemView(weatherList.getId(), weatherList.getName(), weatherList.getMain().getTemp(),
                    weatherList.getWeather().get(0).getMain()));
        }
        return list;
    }


    public List<ListItemView> generateItems(CityWeather cityWeather) {
        List<ListItemView> list = new ArrayList<>();
        list.add(new ListItemView(cityWeather.getId(), cityWeather.getName(),
                cityWeather.getMain().getTemp(), cityWeather.getWeather().get(0).getMain()));
        return list;
    }

    public String generateImageUrl(ImageResponse imageResponse) {
        String imageUrl = null;
        Photo photo = imageResponse.getPhotos().getPhoto().get(0);
        imageUrl = "https://farm" + photo.getFarm() + ".staticflickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + "_b.jpg";
        return imageUrl;
    }

    public Observable<ImageResponse> getImageObservable(String city) {
        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + getFlickerApiKey() +
                "&accuracy=16&per_page=2&page=1&text=" + city +
                "&sort=date-posted-desc&format=json&geo_context=2&nojsoncallback=?";
        return getImage(url);
    }

    public ListItemView generateListViewItem(Pair<ImageResponse, ListItemView> pair){
        ImageResponse imageResponse = pair.first;
        ListItemView listItemView = pair.second;
        ListItemView newListItem = new ListItemView(listItemView.getId(), listItemView.getName(),
                listItemView.getTemp(), listItemView.getDescription());
        newListItem.setImageUrl(generateImageUrl(imageResponse));
        return newListItem;
    }

}
