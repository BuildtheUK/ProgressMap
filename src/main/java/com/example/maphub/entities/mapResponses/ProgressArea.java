package com.example.maphub.entities.mapResponses;

import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;
import java.util.List;

public class ProgressArea {
    public List<String> builders;
    public String colour;
    public double percentageComplete;
    public LocalDateTime timeFinished;
    public List<double[]> coords;
    public String title;
    public String description;
    public double area;
}
