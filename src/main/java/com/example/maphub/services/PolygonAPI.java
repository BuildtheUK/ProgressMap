package com.example.maphub.services;
import com.example.maphub.entities.mapResponses.ProgressArea;
import org.locationtech.jts.geom.Polygon;

import java.util.List;

public interface PolygonAPI {

    public List<ProgressArea> getPolygons(double minLat, double minLon, double maxLat, double maxLon, String uuid);

}
