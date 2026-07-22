package com.example.maphub.entities;

public record BuildingDTO(
        int buildingId,
        int coordinateId,
        String playerId,
        boolean isPublic,
        boolean playerBuilt,
        String timeAdded,
        double lat,
        double lon
) {}
