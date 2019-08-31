
package com.ramindu.weeraman.weather.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clouds {

    @SerializedName("all")
    @Expose
    private double all;

    public double getAll() {
        return all;
    }

    public void setAll(double all) {
        this.all = all;
    }

}
