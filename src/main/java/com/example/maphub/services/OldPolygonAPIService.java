package com.example.maphub.services;

import com.example.maphub.entities.mapResponses.ProgressArea;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.System;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OldPolygonAPIService implements PolygonAPI {
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final String directory = System.getProperty("user.dir");
    private final List<Polygon> progressAreaPolygons = new ArrayList<>();
    private final List<ProgressArea> progressAreaData = new ArrayList<>();
    public OldPolygonAPIService(){
        System.out.println(directory);
        loadFile();
    }
    public List<ProgressArea> getPolygons(double minLat, double minLon, double maxLat, double maxLon, String uuid) {

            List<ProgressArea> polygons = new ArrayList<>();

            Coordinate[] boundedCoords = new Coordinate[]{new Coordinate(minLon,minLat), new Coordinate(minLon,maxLat),new Coordinate(maxLon,maxLat), new Coordinate(maxLon,minLat), new Coordinate(minLon,minLat)};

            Polygon boundedArea = geometryFactory.createPolygon(boundedCoords);
            double totalArea = boundedArea.getArea();
            double areaBound = totalArea/500000;
            for (int i =0; i < progressAreaPolygons.size(); i++){
                if (progressAreaPolygons.get(i).getArea() >= areaBound && boundedArea.intersects(progressAreaPolygons.get(i))){
                    polygons.add(progressAreaData.get(i));
                }
            }

            return polygons;
        }

        private void loadFile(){
        File myObj = new File(directory , "ProgressAreas.geojson");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(myObj);
            JsonNode features = root.get("features");
            for (JsonNode feature: features){

                JsonNode geometry = feature.get("geometry");
                if (geometry == null || !geometry.has("type") || !geometry.get("type").asString().equalsIgnoreCase("polygon") ) {
                    continue;
                }
                ProgressArea area = new ProgressArea();
                JsonNode properties = feature.get("properties");
                if (properties != null) {



                    area.colour = properties.hasNonNull("fill")
                            ? properties.get("fill").asString("blue")
                            : "blue";

                    area.description = properties.hasNonNull("description")
                            ? properties.get("description").asString("unknown")
                            : "unknown";

                    area.title = properties.hasNonNull("title")
                            ? properties.get("title").asString("Area")
                            : "Area";
                }

                if(geometry.has("coordinates")){
                    JsonNode rings = geometry.get("coordinates");
                    JsonNode outerring = rings.get(0);
                    area.coords = new ArrayList<>();
                    List<Coordinate> coordinates = new ArrayList<>();
                    for (JsonNode point : outerring) {
                        double lon = point.get(0).asDouble();
                        double lat = point.get(1).asDouble();
                        area.coords.add(new double[]{lat, lon});
                        coordinates.add(new Coordinate(lon, lat));
                    }
                    Polygon poly = geometryFactory.createPolygon(coordinates.toArray(new Coordinate[0]));
                    area.area = calculateSphericalAreaInSquareMeters(coordinates);
                    progressAreaPolygons.add(poly);
                    progressAreaData.add(area);
                }


            }
            System.out.println("Loaded progress areas");


        }catch (JacksonException e) {
            System.out.println("Progress Area file not loaded");
        }
    }

    public double calculateSphericalAreaInSquareMeters(List<Coordinate> coordinates) {
        if (coordinates.size() < 3) return 0.0;

        double earthRadius = 6371008.8; // Earth's mean radius in meters
        double totalArea = 0.0;

        int size = coordinates.size();
        for (int i = 0; i < size; i++) {
            Coordinate p1 = coordinates.get(i);
            Coordinate p2 = coordinates.get((i + 1) % size);

            double tanLat1 = Math.tan(Math.toRadians(p1.y) / 2.0);
            double tanLat2 = Math.tan(Math.toRadians(p2.y) / 2.0);

            double deltaLon = Math.toRadians(p2.x - p1.x);

            totalArea += deltaLon * (2.0 + tanLat1 * tanLat2);
        }

        totalArea = Math.abs(totalArea * earthRadius * earthRadius / 2.0);
        return totalArea;
    }
    }
