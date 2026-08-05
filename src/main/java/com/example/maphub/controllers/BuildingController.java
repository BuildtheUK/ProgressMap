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
    public int getTotalBuildings() {
        int count = proxyAPIService.getBuildingCount(null,null,null,null,null,null,null);
        return  count;
    }
    @GetMapping("/playerCount")
    public int getPlayerCount(Principal principal) {
        if (principal == null) {
            return 0;
        }
        String uuid = principal.getName();
        // Fetch buildings filtered specifically by the authenticated player's UUID
        int count = proxyAPIService.getBuildingCount(List.of(uuid), null, null, null, null, null, true);
        return count;
    }

}
