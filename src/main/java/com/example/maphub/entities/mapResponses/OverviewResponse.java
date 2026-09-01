package com.example.maphub.entities.mapResponses;

import com.example.maphub.entities.buildings.BuildingGridRespItem;

import java.util.List;

public class OverviewResponse {
    public List<BuildingGridRespItem> buildings;
    public List<HeatmapRespItem> heatmap;
    public List<ProgressArea> progressArea;
}
