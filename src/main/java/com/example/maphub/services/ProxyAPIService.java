package com.example.maphub.services;



import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;



import java.net.URI;

import java.net.http.HttpClient;

import java.net.http.HttpRequest;

import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
    
}