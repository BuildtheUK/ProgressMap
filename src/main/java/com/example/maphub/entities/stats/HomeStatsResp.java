package com.example.maphub.entities.stats;

import java.time.LocalDateTime;

public class HomeStatsResp {
    public int buildings;
    public double percentage;
    public int buildingsRecent; //buildings in the last month?
    public LocalDateTime estimatedFinishDate;
    public int previousRecentBuildings;
}
