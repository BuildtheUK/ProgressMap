package com.example.maphub.services;

import com.example.maphub.entities.buildings.BuildingGridItem;
import com.example.maphub.entities.mapResponses.HeatmapRespItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HeatmapService {

    // Maximum density at high zoom:
    // 10 buildings / 10,000 m²
    private static final double MAX_DENSITY = 10.0 / 10_000.0;

    // At large scales, 20% of all buildings = maximum
    private static final double MAX_BUILDING_SHARE = 0.20;

    // Controls where the transition from density -> total progress occurs.
    // 5 km² is a reasonable starting point.
    private static final double TRANSITION_AREA = 20_000_000.0;

    public List<HeatmapRespItem> getHeatmap(List<BuildingGridItem> buildings, int totalBuildings) {
        List<HeatmapRespItem> out = new ArrayList<>();

        if (buildings.isEmpty() || totalBuildings <= 0) {
            return out;
        }
        for (BuildingGridItem b : buildings) {
            double area = calculateAreaWithLatLon(b);
            if (area <= 0) {
                continue;
            }
            double buildingCount = b.count;
            double density = buildingCount / area;
            double densityWeight = Math.exp(-area / TRANSITION_AREA);
            double densityScore = density / MAX_DENSITY;
            double buildingShare = buildingCount / (double) totalBuildings;
            double progressScore = buildingShare / MAX_BUILDING_SHARE;
            double score = densityWeight * densityScore + (1.0 - densityWeight) * progressScore;

            //score between 0 and 1
            score = Math.min(score, 1.0);
            int magnitude = (int) (Math.sqrt(score) * 8.99);
            HeatmapRespItem h = new HeatmapRespItem();
            h.maxLat = b.maxLat;
            h.maxLon = b.maxLon;
            h.minLat = b.minLat;
            h.minLon = b.minLon;
            h.magnitude = magnitude;
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

