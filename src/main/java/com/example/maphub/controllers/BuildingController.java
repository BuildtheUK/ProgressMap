package com.example.maphub.controllers;

import com.example.maphub.entities.*;
import com.example.maphub.services.ProxyAPIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@RequestMapping("/building")
public class BuildingController {

    final private ProxyAPIService proxyAPIService;

    public BuildingController(ProxyAPIService proxyAPIService){
        this.proxyAPIService = proxyAPIService;
    }
    @GetMapping("/total")
    public ResponseEntity<?> getTotalBuildings() {
        int count = proxyAPIService.getBuildingCount(null,null,null,null,null,null,null);
        return ResponseEntity.ok(Map.of("count", count));
    }
    @GetMapping("/playerCount")
    public ResponseEntity<?> getPlayerCount(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }
        String uuid = principal.getName();
        // Fetch buildings filtered specifically by the authenticated player's UUID
        int count = proxyAPIService.getBuildingCount(List.of(uuid), null, null, null, null, null, true);
        return ResponseEntity.ok(Map.of("count", count));
    }

}
