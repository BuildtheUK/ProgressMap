package com.MapHub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgressArea {
    @JsonProperty
    private String colour;
    @JsonProperty
    private List<LatLng> bounds;
    @JsonProperty
    private int id;
    @JsonProperty
    private List<String> builders;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private double area = 0;
    @JsonProperty
    private LatLng[] squarebounds = {};

    public ProgressArea(String colour, List<LatLng> bounds, int id, String name, String description, List<String> builders) {
        this.colour = colour;
        this.bounds = bounds;
        this.id = id;
        this.name = name;
        this.description = description;
        this.builders = builders;

    }

    public double getArea()
    {
        return 0;

    }

    public List<LatLng> getBounds(){
        return bounds;
    }

    public int getId()
    {
        return id;
    }


}
