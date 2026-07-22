package com.example.maphub.entities;

public record BuildingGridRequest (
    double minLat,
    double minLon,
    double maxLat,
    double maxLon,
    double stepLat,
    double stepLon
) {}
