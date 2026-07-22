package com.example.maphub.entities;

import java.time.LocalDateTime;

public record Building(int buildingId, String playerId, boolean isPublic, boolean playerBuilt, LocalDateTime timeAdded, double lat, double lon) {
}
