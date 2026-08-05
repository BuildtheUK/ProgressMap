package com.example.maphub.services;

import com.example.maphub.entities.buildings.BuildingGridItem;
import com.example.maphub.entities.mapResponses.HeatmapRespItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HeatmapService {

    public List<HeatmapRespItem> getHeatmap(List<BuildingGridItem> buildings){
        List<HeatmapRespItem> out = new ArrayList<>();
        if (buildings.isEmpty())
        {
            return  out;
        }
        double area = calculateAreaWithLatLon(buildings.getFirst());
        for (BuildingGridItem b : buildings){
            HeatmapRespItem h = new HeatmapRespItem();
            h.maxLat = b.maxLat;
            h.maxLon = b.maxLon;
            h.minLat = b.minLat;
            h.minLon = b.minLon;
            //density of buildings /100m^2
            h.magnitude = (int) Math.round(((double) b.count * 10000.0) / area);
            out.add(h);
        }
        return out;
    }
    public double calculateAreaWithLatLon(BuildingGridItem b) {
        if (b == null) {
            return 0.0;
        }

        final double EARTH_RADIUS_METERS = 6371000.0;

        double minLatRad = Math.toRadians(b.minLat);
        double maxLatRad = Math.toRadians(b.maxLat);
        double minLonRad = Math.toRadians(b.minLon);
        double maxLonRad = Math.toRadians(b.maxLon);

        // Height along latitude (North-South)
        double deltaLat = maxLatRad - minLatRad;
        double height = deltaLat * EARTH_RADIUS_METERS;

        // Mid-latitude cosine adjustment for longitude (East-West)
        double avgLatRad = (minLatRad + maxLatRad) / 2.0;
        double deltaLon = maxLonRad - minLonRad;
        double width = deltaLon * EARTH_RADIUS_METERS * Math.cos(avgLatRad);

        return Math.abs(width * height); // Area in square meters (m²)
    }
}

