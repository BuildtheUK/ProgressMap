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

    @GetMapping("/api/getPolygon")
    public ResponseEntity<?> getPolygon(@RequestParam("bounds") double[] mapbounds,@RequestParam("zoom") double zoom) {
        System.out.println(mapbounds);
        System.out.println(zoom);
        LatLng[] bounds = {new LatLng(70,60), new LatLng(70,70), new LatLng(60,70), new LatLng(60,60),new LatLng(70,60)};
        ProgressArea a = new ProgressArea("#777777",new ArrayList<>(List.of(bounds)),1,"test","i hope this works",new ArrayList<>(List.of(new String[]{"leopardm"})) );

        List<ProgressArea> areas = new ArrayList<>();
        areas.add(a);

        return new ResponseEntity<>(areas, HttpStatus.OK);
    }
}
