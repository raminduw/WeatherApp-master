package com.ramindu.weeraman.weather;

import com.ramindu.weeraman.weather.data.WeatherRepository;
import com.ramindu.weeraman.weather.data.local.WeatherDataDao;
import com.ramindu.weeraman.weather.data.photo.ImageResponse;
import com.ramindu.weeraman.weather.data.photo.Photo;
import com.ramindu.weeraman.weather.data.photo.Photos;
import com.ramindu.weeraman.weather.data.remote.WeatherApiService;
import com.ramindu.weeraman.weather.models.response.CityWeather;
import com.ramindu.weeraman.weather.models.response.Main;
import com.ramindu.weeraman.weather.models.response.Weather;
import com.ramindu.weeraman.weather.models.response.WeatherCities;
import com.ramindu.weeraman.weather.models.response.WeatherList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeatherRepositoryTest {

    private WeatherDataDao weatherDataDao;
    private WeatherApiService weatherApiService;
    private WeatherRepository repository;

    @Before
    public void before() throws Exception {
        weatherDataDao = mock(WeatherDataDao.class);
        weatherApiService = mock(WeatherApiService.class);
        repository = new WeatherRepository(weatherDataDao, weatherApiService);
    }

    @Test
    public void testRepositoryKey() {
        assertNotNull(repository.getApiKey());
    }


    @Test
    public void testFlickerKey() {
        assertNotNull(repository.getFlickerApiKey());
    }

    @Test
    public void testUnits() {
        assertNotNull(repository.getUnit());
    }

    @Test
    public void testGetDefaultCityList() {
        assertEquals(4, repository.getDefaultCities().size());
    }

    @Test
    public void testGenerateImageUrl() {

        ImageResponse imageResponse = Mockito.mock(ImageResponse.class);
        Photo photo = Mockito.mock(Photo.class);
        Photos photos = Mockito.mock(Photos.class);
        List<Photo> list = new ArrayList<>();
        list.add(photo);

        when(imageResponse.getPhotos()).thenReturn(photos);
        when(photos.getPhoto()).thenReturn(list);

        when(photo.getFarm()).thenReturn(1);
        when(photo.getServer()).thenReturn("server");
        when(photo.getId()).thenReturn("xxxxxx");
        when(photo.getSecret()).thenReturn("yyyyy");

        assertEquals("https://farm1.staticflickr.com/server/xxxxxx_yyyyy_b.jpg", repository.generateImageUrl(imageResponse));

    }

    @Test
    public void testGenerateListViewItemForCityList() {

        WeatherCities weatherCities = Mockito.mock(WeatherCities.class);
        WeatherList weatherList = Mockito.mock(WeatherList.class);
        Main main = Mockito.mock(Main.class);
        Weather weather = Mockito.mock(Weather.class);
        List<WeatherList> list = new ArrayList<>();
        list.add(weatherList);
        List<Weather> list2 = new ArrayList<>();
        list2.add(weather);
        when(weatherList.getId()).thenReturn(1L);
        when(weatherList.getName()).thenReturn("Colombo");
        when(weatherList.getMain()).thenReturn(main);
        when(main.getTemp()).thenReturn(20.0);
        when(weatherList.getWeather()).thenReturn(list2);
        when(list2.get(0).getMain()).thenReturn("Clean");

        when(weatherCities.getList()).thenReturn(list);
        assertEquals(1, repository.generateItems(weatherCities).size());
    }


    @Test
    public void testGenerateListViewItemForCity() {

        CityWeather cityWeather = Mockito.mock(CityWeather.class);
        Main main = Mockito.mock(Main.class);
        when(cityWeather.getId()).thenReturn(1L);
        when(cityWeather.getName()).thenReturn("Colombo");
        when(cityWeather.getMain()).thenReturn(main);
        when(main.getTemp()).thenReturn(20.0);

        Weather weather = Mockito.mock(Weather.class);
        List<Weather> list = new ArrayList<>();
        list.add(weather);

        when(cityWeather.getWeather()).thenReturn(list);
        assertEquals(1, repository.generateItems(cityWeather).size());
    }


}
