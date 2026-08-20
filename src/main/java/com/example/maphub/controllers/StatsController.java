package com.example.maphub.controllers;


import com.example.maphub.entities.stats.HomeStatsResp;
import com.example.maphub.entities.stats.StatsFilters;
import com.example.maphub.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;
    public StatsController(StatsService statsService){
        this.statsService = statsService;
    }
    @GetMapping("/homePage")
    public ResponseEntity<?> getHomePageStats(){
        HomeStatsResp resp =  statsService.getHomepageStats();
        return ResponseEntity.ok(resp);
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getProfileStats(Principal principal){
        if (principal == null){
            return ResponseEntity.status(401).body("User not logged in");
        }
        return ResponseEntity.ok(statsService.getProfileStats(principal.getName()));
    }
}
