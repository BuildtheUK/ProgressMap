package com.example.maphub.services;

import org.geojson.FeatureCollection;

public interface PolygonAPI {

    public FeatureCollection getPolygons(double minLat, double minLon, double maxLat, double maxLon, String uuid);

}
