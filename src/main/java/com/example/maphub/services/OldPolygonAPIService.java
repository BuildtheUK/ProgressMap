package com.example.maphub.services;

import com.example.maphub.entities.mapResponses.ProgressArea;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.System;

import java.util.ArrayList;
import java.util.List;

@Service
public class OldPolygonAPIService implements PolygonAPI {
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final String directory = System.getProperty("user.dir");
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

        //load a list of all progress areas into an in memory array
        //this array is then used to find the areas that overlap with the map view
    //hopefully efficiently although this is not a permenant feature as progress manager will
    //replace it
    //I need to actually change a lot here but this should be rough structure
        private void loadFile(){
            File myObj = new File("filename.txt");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(myObj);
            String name = jsonNode.get("name").asText();
            int age = jsonNode.get("age").asInt();
            String city = jsonNode.get("city").asText();
            String state = jsonNode.get("state").asText();
            String country = jsonNode.get("country").asText();
            System.out.println("Name: " + name);
            System.out.println("Age: " + age);
            System.out.println("City: " + city);
            System.out.println("State: " + state);
            System.out.println("Country: " + country);


        }catch (JacksonException e) {
            System.out.println("Progress Area file not loaded");
        }

            // try-with-resources: Scanner will be closed automatically



    }
    }
