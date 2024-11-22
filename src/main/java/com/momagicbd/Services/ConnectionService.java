package com.momagicbd.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ConnectionService {

    private final WebClient webClient;

    public ConnectionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://demo.webmanza.com/a55dbz923ace647v/api/v1.0")
                .build();
    }

    public void checkConnection() {
        this.webClient.get()
                .uri("/ping").retrieve()
                .bodyToMono(String.class).block();

    }
}
