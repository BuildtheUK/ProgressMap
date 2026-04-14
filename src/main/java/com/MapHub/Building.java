package com.MapHub;
import com.fasterxml.jackson.annotation.JsonProperty;
public class Building {
    @JsonProperty
    private LatLng location;
    @JsonProperty
    private String addedBy;
    @JsonProperty
    private boolean isVisible;
    public Building(LatLng location, String addedBy, boolean isVisible) {
        this.location = location;
        this.addedBy = addedBy;
        this.isVisible = isVisible;
    }
}
