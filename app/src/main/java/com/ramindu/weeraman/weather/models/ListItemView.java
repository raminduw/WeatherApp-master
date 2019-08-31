package com.ramindu.weeraman.weather.models;

public class ListItemView {

    private Long id;
    private String name;
    private double temp;
    private String description;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;

    public ListItemView(Long id, String name, double temp, String description) {
        this.id = id;
        this.name = name;
        this.temp = temp;
        this.description = description;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
