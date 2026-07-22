package com.example.maphub;

import com.example.maphub.entities.*;
import com.example.maphub.services.ProxyAPIService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/building")
public class BuildingController {

    final private ProxyAPIService proxyAPIService;

    public BuildingController(ProxyAPIService proxyAPIService){
        this.proxyAPIService = proxyAPIService;
    }

    @PostMapping("/allBuildings")
    public ResponseEntity<?> getAllBuildings(@RequestBody MapBounds req, Principal principal){
        String uuid = "";
        if (principal == null){
            uuid = null;
        }
        else{
            uuid = principal.getName();
        }
        List<BuildingDTO> resp = proxyAPIService.getBuildingsByArea(req.minLat(), req.maxLat(), req.minLon(), req.maxLon(), uuid);
        List<Building> out = new ArrayList<>();
        for (BuildingDTO b : resp){
            LocalDateTime timeAdded = parseTimestamp(b.timeAdded());
            out.add(new Building(b.buildingId(),b.playerId(), b.isPublic(), b.playerBuilt(),timeAdded ,b.lat(),b.lon()));
        }
        return ResponseEntity.ok(out);
    }

    private LocalDateTime parseTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.isBlank()) {
            return null;
        }
        try {
            long epochMillis = Long.parseLong(timestampStr);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @PostMapping("/gridCount")
    public ResponseEntity<?> getGridCount(@RequestBody BuildingGridRequest req, Principal principal) {
        String uuid = "";
        if (principal == null){
            uuid = null;
        }
        else{
            uuid = principal.getName();
        }

        ProxyAPIService.BuildingGridResponseDTO resp = proxyAPIService.getBuildingGridCount(req.minLat(), req.maxLat(), req.minLon(), req.maxLon(), req.stepLat(), req.stepLon(),uuid);
        List<BuildingGridItem> out = new ArrayList<>();
        for (ProxyAPIService.GridCellDTO cell : resp.cells()){
            out.add(new BuildingGridItem(cell.lat(),cell.lon(), cell.count()));
        }
        return ResponseEntity.ok(out);
    }
}
