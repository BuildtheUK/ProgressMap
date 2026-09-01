package com.example.maphub.controllers;

import com.example.maphub.entities.MapBounds;
import com.example.maphub.entities.buildings.BuildingGridItem;
import com.example.maphub.entities.buildings.BuildingGridRequest;
import com.example.maphub.entities.mapResponses.CloseUpResponse;
import com.example.maphub.entities.mapResponses.OverviewResponse;
import com.example.maphub.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/map")
public class MapController {

    private final BuildingService buildingService;
    private final HeatmapService heatmapService;
    private final ProxyAPIService proxyAPIService;
    private final PolygonAPI polygonAPIService;
    public MapController(BuildingService buildingService, HeatmapService heatmapService, ProxyAPIService proxyAPIService, OldPolygonAPIService polygonAPIService){
        this.buildingService = buildingService;
        this.heatmapService = heatmapService;
        this.proxyAPIService = proxyAPIService;
        this.polygonAPIService = polygonAPIService;
    }

    @PostMapping("/overview")
    public ResponseEntity<?> getOverview(@RequestBody BuildingGridRequest req, Principal principal){
        List<BuildingGridItem> buildings = buildingService.getGridCount(req,principal);
        OverviewResponse resp = new OverviewResponse();
        resp.heatmap = heatmapService.getHeatmap(buildings, buildingService.getTotalCount());
        resp.buildings = buildingService.getGroupedGridCounts(buildings);
        String username = "";
        if (principal != null){
            username = principal.getName();
        }
        resp.progressArea = polygonAPIService.getPolygons(req.minLat(),req.minLon(),req.maxLat(),req.maxLon(),username);
        return ResponseEntity.ok(resp);
    }
    @PostMapping("/closeUp")
    public ResponseEntity<?> getCloseUp(@RequestBody MapBounds req, Principal principal){
        CloseUpResponse resp = new CloseUpResponse();
        resp.buildings = buildingService.getAllBuildings(req,principal);
        String username = "";
        if (principal != null){
            username = principal.getName();
        }
        resp.progressArea = polygonAPIService.getPolygons(req.minLat(),req.minLon(),req.maxLat(),req.maxLon(),username);
        return ResponseEntity.ok(resp);
    }
}
