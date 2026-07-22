package com.example.maphub.services;



import com.example.maphub.entities.BuildingDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;





@Service

public class ProxyAPIService {


    private final RestClient restClient;

    private final ObjectMapper objectMapper;


    public ProxyAPIService(@Value("${api.proxy.base-url}") String baseUrl) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory) // Critical fix
                .build();

        this.objectMapper = new ObjectMapper();

    }


    public String getUsername(String uuid) {

        try {

            String output = restClient.get().uri("/player/username/{uuid}", uuid).retrieve().body(String.class);

            if (output == null || output.isBlank()) {

                return "";

            }

            return output;

        } catch (Exception e) {

            return "";

        }

    }


    public String getUuid(String username) {

        try {

            String output = restClient.get()

                    .uri("/player/uuid/{username}", username) // Automatically encodes URL variables

                    .retrieve()

                    .body(String.class);

            if (output == null || output.isBlank()) {

                return "";

            }

            return objectMapper.readValue(output, String.class);

        } catch (Exception e) {

            return "";

        }

    }

    public boolean sendMessage(String uuid, String messageText) {
        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(Map.of("message", messageText));
        }catch (JsonProcessingException e){
            return false;
        }
        uuid = uuid.replace("\"", "").trim();
        System.out.println(jsonPayload);
        byte[] bodyBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
        try {
            restClient.post()
                    .uri("/player/message/{uuid}", uuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(bodyBytes.length) // Explicitly tells Grizzly how many bytes to read
                    .body(bodyBytes)                // Prevents chunked encoding stream hangs
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            System.err.println("Error sending message to " + uuid + ": " + e.getMessage());
            return false;
        }

    }

    /**
     * Formats the OTC token into a message and sends it via Proxy API
     */
    public boolean sendOTC(String uuid, int token) {
        String message = "Your verification code is: " + token;
        boolean success = sendMessage(uuid, message);
        if (success) {
            System.out.println("Successfully sent OTC to " + uuid);
        } else {
            System.err.println("Failed to send OTC to " + uuid);
        }
        return success;
    }

    /**
     * Fetches full building objects inside an area with optional privacy filtering for a player
     */
    public List<BuildingDTO> getBuildingsByArea(double minLat, double maxLat, double minLon, double maxLon, String playerUuid) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/buildings/area")
                                .queryParam("minLat", minLat)
                                .queryParam("maxLat", maxLat)
                                .queryParam("minLon", minLon)
                                .queryParam("maxLon", maxLon);
                        if (playerUuid != null && !playerUuid.isBlank()) {
                            builder.queryParam("playerUuid", playerUuid);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BuildingDTO>>() {});
        } catch (Exception e) {
            System.err.println("Error fetching buildings by area: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Gets building counts with optional filtering by area, players, visibility, or source
     */
    public int getBuildingCount(List<String> playerUuids, Double minLat, Double maxLat, Double minLon, Double maxLon, Boolean isPublic, Boolean playerBuilt) {
        try {
            BuildingCountDTO response = restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/buildings/count");
                        if (minLat != null) builder.queryParam("minLat", minLat);
                        if (maxLat != null) builder.queryParam("maxLat", maxLat);
                        if (minLon != null) builder.queryParam("minLon", minLon);
                        if (maxLon != null) builder.queryParam("maxLon", maxLon);
                        if (isPublic != null) builder.queryParam("isPublic", isPublic);
                        if (playerBuilt != null) builder.queryParam("playerBuilt", playerBuilt);
                        if (playerUuids != null && !playerUuids.isEmpty()) {
                            for (String uuid : playerUuids) {
                                builder.queryParam("playerUuid", uuid);
                            }
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .body(BuildingCountDTO.class);

            return response != null ? response.count() : 0;
        } catch (Exception e) {
            System.err.println("Error fetching building count: " + e.getMessage());
            return 0;
        }
    }

    public record BuildingCountDTO(int count) {}

    /**
     * Gets aggregated building counts mapped over a grid for heatmaps and zoomed-out map views
     */
    public BuildingGridResponseDTO getBuildingGridCount(double minLat, double maxLat, double minLon, double maxLon, double latStep, double lonStep, String playerUuid) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/buildings/grid-count")
                                .queryParam("minLat", minLat)
                                .queryParam("maxLat", maxLat)
                                .queryParam("minLon", minLon)
                                .queryParam("maxLon", maxLon)
                                .queryParam("stepLat", latStep)
                                .queryParam("stepLon", lonStep);
                        if (playerUuid != null && !playerUuid.isBlank()) {
                            builder.queryParam("playerUuid", playerUuid);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .body(BuildingGridResponseDTO.class);
        } catch (Exception e) {
            System.err.println("Error fetching grid building count: " + e.getMessage());
            return null;
        }
    }

    public record GridCellDTO(
            double lat,
            double lon,
            int count
    ) {}

    public record BuildingGridResponseDTO(
            List<GridCellDTO> cells
    ) {}
    
}