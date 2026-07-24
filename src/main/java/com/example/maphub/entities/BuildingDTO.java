package com.example.maphub.entities;

public record BuildingDTO(
        int buildingId,
        String playerName,
        String playerId,
        boolean isPublic,
        boolean playerBuilt,
        String timeAdded,
        double lat,
        double lon
) {}
