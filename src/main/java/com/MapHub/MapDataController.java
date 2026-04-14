package com.MapHub;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
public class MapDataController {

    SQL sql = new SQL();

    @GetMapping("/api/getPolygon")
    public ResponseEntity<?> getPolygon(@RequestParam("bounds") double[] mapbounds,@RequestParam("zoom") double zoom) {
        LatLng[] bounds = {new LatLng(70,60), new LatLng(70,70), new LatLng(60,70), new LatLng(60,60),new LatLng(70,60)};
        ProgressArea a = new ProgressArea("#777777",new ArrayList<>(List.of(bounds)),1,"test","i hope this works",new ArrayList<>(List.of(new String[]{"leopardm"})) );
        List<ProgressArea> areas = new ArrayList<>();
        areas.add(a);

        return new ResponseEntity<>(areas, HttpStatus.OK);
    }

    @GetMapping("/api/markers")
    public ResponseEntity<?> getMarkers(@RequestParam("bounds") double[] mapbounds)
    {
        Building b = new Building(new LatLng(51.51380777651,-0.098651795664),"leopardm",false);
        List<Building> buildings = new ArrayList<>();
        buildings.add(b);
        return new ResponseEntity<>(buildings, HttpStatus.OK );
    }


    @PostMapping("/api/save")
    public ResponseEntity<?> saveData(@RequestParam("data") ProgressArea[] data)
    {
        try {
            for (int i = 0; i < data.length; i++) {
                if (data[i].getId() == 0) {
                    sql.addPoly(data[i]);
                } else {
                    sql.updatePoly(data[i]);
                }
            }
            return new ResponseEntity<>( HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
