package com.example.maphub.services;

import com.example.maphub.entities.*;
import com.example.maphub.entities.buildings.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAmount;
import java.util.*;

@Service
public class BuildingService {
    private final ProxyAPIService proxyAPIService;
    public BuildingService(ProxyAPIService proxyAPIService){
        this.proxyAPIService = proxyAPIService;
    }

    public List<Building> getAllBuildings(@RequestBody MapBounds req, Principal principal){
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
            String colour = "green"; //display colour. all are green if not logged in.
            if (principal!= null){
                if (!b.playerBuilt()){
                    colour = "orange";
                }
                else if (!uuid.equals(b.playerId())){
                    colour = "red";
                }
            }
            String username = b.playerName();
            String uuidDisplay = b.playerId();
            if (!b.playerBuilt()){
                username = "--";
                uuidDisplay = "";
            }
            out.add(new Building(b.buildingId(),uuidDisplay, b.isPublic(), b.playerBuilt(),timeAdded ,b.lat(),b.lon(),colour,username));
        }
        return out;
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

    public List<BuildingGridItem> getGridCount(BuildingGridRequest req, Principal principal) {
        String uuid = "";
        if (principal == null) {
            uuid = null;
        } else {
            uuid = principal.getName();
        }

        return proxyAPIService.getBuildingGridCount(req.minLat(), req.maxLat(), req.minLon(), req.maxLon(), req.stepLat(), req.stepLon(), uuid).cells();
    }
    public List<BuildingGridRespItem> getGroupedGridCounts(List<BuildingGridItem> original){
        Map<xy, BuildingGridItem> gridMap = new HashMap<>();
        for (BuildingGridItem dto : original) {
            if (dto.count() > 0) {
                gridMap.put(new xy(dto.row, dto.col), dto);
            }
        }

        // 2. Sort initial cells ascending by count
        List<BuildingGridItem> sortedCells = new ArrayList<>(gridMap.values());
        sortedCells.sort(Comparator.comparingInt(BuildingGridItem::count));

        // 3. Process merges
        for (BuildingGridItem current : sortedCells) {
            xy currentKey =new xy(current.row,current.col);

            // Skip if this cell was already removed (merged into another cell)
            if (!gridMap.containsKey(currentKey)) {
                continue;
            }
            BuildingGridItem bestNeighbor = null;
            // Check 8-neighbor surrounding grid
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    xy neighborKey = new xy (current.row + dr, current.col + dc);
                    BuildingGridItem neighbor = gridMap.get(neighborKey);

                    // Valid non-merged neighbor
                    if (neighbor != null) {
                        if (bestNeighbor == null) {
                            bestNeighbor = neighbor;
                        } else if (neighbor.count() > bestNeighbor.count()) {
                            bestNeighbor = neighbor;
                        } else if (neighbor.count() == bestNeighbor.count()) {
                            // Tie-breaker: Highest row, then highest col
                            if (neighbor.row > bestNeighbor.row) {
                                bestNeighbor = neighbor;
                            } else if (neighbor.row == bestNeighbor.row && neighbor.col > bestNeighbor.col) {
                                bestNeighbor = neighbor;
                            }
                        }
                    }
                }
            }

            // Merge into best neighbor and remove current from the map
            if (bestNeighbor != null) {
                int combinedCount = bestNeighbor.count() + current.count();

                bestNeighbor.lat = ((bestNeighbor.lat * bestNeighbor.count()) + (current.lat * current.count())) / combinedCount;
                bestNeighbor.lon = ((bestNeighbor.lon * bestNeighbor.count()) + (current.lon * current.count())) / combinedCount;
                bestNeighbor.count = combinedCount;

                gridMap.remove(currentKey);
            }
        }

        // 4. Return remaining cluster cells directly from gridMap
        List<BuildingGridRespItem> out = gridMap.values().stream()
                .map(cell -> new BuildingGridRespItem(cell.lat, cell.lon, cell.count))
                .toList();

        return out;
    }

    private record xy (int x, int y){}

    public int getTotalCount() {
        int count = proxyAPIService.getBuildingCount(null,null,null,null,null,null,null,null,null);
        return count;
    }
    public int getPlayerCount(String uuid) {
        int count = proxyAPIService.getBuildingCount(List.of(uuid),null,null,null,null,null,null,null,null);
        return count;
    }

    public int getRecentBuildingCount(){
        int count = proxyAPIService.getBuildingCount(null,null,null,null,null,null,null, LocalDateTime.now().minusMonths(1), LocalDateTime.now());
        return count;
    }



}
