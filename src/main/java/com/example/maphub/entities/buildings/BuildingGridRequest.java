package com.example.maphub.entities.buildings;

public record BuildingGridRequest (
    double minLat,
    double minLon,
    double maxLat,
    double maxLon,
    int zoom,
    double centreLat
) {}
