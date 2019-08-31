
package com.ramindu.weeraman.weather.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherCities {

    @SerializedName("list")
    @Expose
    private java.util.List<WeatherList> list = null;

    public java.util.List<WeatherList> getList() {
        return list;
    }

    public void setList(java.util.List<WeatherList> list) {
        this.list = list;
    }

}
