package com.example.maphub.services;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OldPolygonAPIService implements PolygonAPI {
    public FeatureCollection getPolygons( double minLat, double minLon, double maxLat, double maxLon, String uuid) {

        // Define coordinates for a simple triangle/polygon (first and last coordinate must match)
        List<LngLatAlt> exteriorRing = new ArrayList<>();
        exteriorRing.add(new LngLatAlt(-105.0, 40.0));
        exteriorRing.add(new LngLatAlt(-105.0, 41.0));
        exteriorRing.add(new LngLatAlt(-104.0, 40.0));
        exteriorRing.add(new LngLatAlt(-105.0, 40.0));

        Polygon polygon = new Polygon(exteriorRing);

        // Create Feature and assign geometry
        Feature feature = new Feature();
        feature.setGeometry(polygon);

        // Custom metadata properties
        feature.setProperty("name", "test");
        feature.setProperty("builders", "leopardm");
        feature.setProperty("date", "20/05/2006");

        // Simplestyle properties for green color and 50% opacity
        feature.setProperty("stroke", "#00FF00");
        feature.setProperty("stroke-opacity", 1);
        feature.setProperty("fill", "#00FF00");
        feature.setProperty("fill-opacity", 0.5);

        // Add feature to a FeatureCollection
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.add(feature);
        return featureCollection;
    }
}
