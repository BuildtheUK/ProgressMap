package com.example.maphub.services;

import com.example.maphub.entities.mapResponses.ProgressArea;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OldPolygonAPIService implements PolygonAPI {
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    public List<ProgressArea> getPolygons(double minLat, double minLon, double maxLat, double maxLon, String uuid) {
            Coordinate[] coordinates = new Coordinate[] {
                    new Coordinate(-2.395048498111797, 51.355557375227946),
                    new Coordinate(-2.3886775308294825, 51.40741506296923),
                    new Coordinate(-2.3026080613045874, 51.382421036288996),
                    new Coordinate(-2.395048498111797, 51.355557375227946)
            };

            List<ProgressArea> polygons = new ArrayList<>();
            ProgressArea a = new ProgressArea();
            Polygon poly = geometryFactory.createPolygon(coordinates);
        a.coords = new ArrayList<>(); // Prevent NullPointerException

        for (Coordinate c : poly.getCoordinates()) {
            a.coords.add(new double[]{c.y, c.x}); // [lat, lng] ready for Leaflet L.polygon
        }
            a.builders = new ArrayList<String>();
            a.builders.add("leopardm");
            a.colour="#555555";
            a.percentageComplete=45;
            polygons.add(a);
            return polygons;
        }
    }
