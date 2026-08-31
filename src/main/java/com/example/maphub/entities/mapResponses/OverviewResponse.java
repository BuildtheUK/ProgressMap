package com.example.maphub.entities.mapResponses;

import com.example.maphub.entities.buildings.BuildingGridRespItem;
import org.geojson.FeatureCollection;

import java.util.List;

public class OverviewResponse {
    public List<BuildingGridRespItem> buildings;
    public List<HeatmapRespItem> heatmap;
    public FeatureCollection polygons;
}
