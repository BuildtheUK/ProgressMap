package com.example.maphub.entities.mapResponses;

import com.example.maphub.entities.buildings.Building;
import org.geojson.FeatureCollection;

import java.util.List;

public class CloseUpResponse {
    public List<Building> buildings;
    public FeatureCollection polygons;
}
