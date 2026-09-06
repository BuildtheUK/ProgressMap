package com.example.maphub.services;

import com.example.maphub.entities.stats.HomeStatsResp;
import com.example.maphub.entities.stats.PlayerBaseStats;
import com.example.maphub.entities.stats.ProfileStatsResp;
import com.example.maphub.entities.stats.TotalBaseStats;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.example.maphub.Consts.*;

@Service
public class StatsService {
    private final ProxyAPIService proxyAPIService;
    private final BuildingService buildingService;
    public StatsService(ProxyAPIService proxyAPIService, BuildingService buildingService){
        this.proxyAPIService = proxyAPIService;
        this.buildingService = buildingService;
    }


    public HomeStatsResp getHomepageStats()
    {
        TotalBaseStats base = proxyAPIService.getTotalStats();

        if(base == null){
            return new HomeStatsResp();
        }

        double percentage = ((double) base.buildings) / (double) TOTAL_UK_BUILDINGS;

        LocalDateTime expectedFinishTime = null;
        //calculating expected completion time
        if (percentage > 0) {
            long timeSinceStart = Duration.between(SERVER_START_TIME, LocalDateTime.now()).toMillis();
            long expectedTimeToComplete = (long) (timeSinceStart / percentage);
            expectedFinishTime = SERVER_START_TIME.plus(expectedTimeToComplete, ChronoUnit.MILLIS);
        }
        HomeStatsResp resp = new HomeStatsResp();
        resp.buildings = base.buildings;
        resp.buildingsRecent = base.recentBuildings;
        resp.estimatedFinishDate = expectedFinishTime;
        resp.percentage = percentage;
        resp.previousRecentBuildings = base.previousRecentBuildings;
        return resp;

    }
    public ProfileStatsResp getProfileStats(String uuid){

        PlayerBaseStats base = proxyAPIService.getPlayerStats(uuid);

        if (base == null){
            return new ProfileStatsResp();
        }

        ProfileStatsResp resp = new ProfileStatsResp();
        resp.buildings = base.buildings;
        resp.messagesSent = base.messagesSent;
        resp.tplls = base.tplls;
        resp.timeNonAFK = (float)base.timePlayed / SECONDS_IN_DAY;

        resp.reviewsCompleted = base.reviewsCompleted;

        int buildingWeight = 50;
        int tpllWeight = 1;
        if (base.timePlayed <= 0) {
            resp.productivity = "N/A";
            return resp;
        }
        float productivity = (buildingWeight * base.buildings + tpllWeight * base.tplls)/resp.timeNonAFK;
        if(productivity >= 800)
        {
            resp.productivity = "A";
        }
        else if (productivity >= 700 )
        {
            resp.productivity = "B";
        }
        else if (productivity >= 600)
        {
            resp.productivity = "C";
        }
        else if (productivity >= 500)
        {
            resp.productivity = "D";
        }
        else if(productivity >= 400){
            resp.productivity = "E";
        }
        else{
            resp.productivity = "F";
        }
        return  resp;

    }
}
