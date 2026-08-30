package com.example.maphub.entities.buildings;

public class BuildingGridItem {
    public double lat;
    public double lon;
    public int count;
    public int row;
    public int col;
    public double minLat;
    public double minLon;
    public double maxLat;
    public double maxLon;

    public int count() {
        return count;
    }
}
