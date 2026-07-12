package com.example.maphub.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Service
public class ProxyAPIService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ProxyAPIService(ObjectMapper om) {
        this.restClient = RestClient.builder()
                .baseUrl("http://51.195.190.215:61101/api")
                .build();
        this.objectMapper = om;
    }

    public String getUsername(String uuid)
    {
        try{
            String output = restClient.get().uri("/player/username/{uuid}",uuid).retrieve().body(String.class);
            if (output == null || output.isBlank()){
                return "";
            }
            return output;
        }catch (Exception e){
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

    public void sendOTC(String uuid, int token){

        System.out.println(token);

    }


}
