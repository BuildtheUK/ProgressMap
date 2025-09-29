package com.MapHub;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public ProgressArea(String colour, List<LatLng> bounds, int id, String name, String description, List<String> builders) {
        this.colour = colour;
        this.bounds = bounds;
        this.id = id;
        this.name = name;
        this.description = description;
        this.builders = builders;

    }

}
