package com.momagicbd.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class ConnectionService {

    private final WebClient webClient;

    public ConnectionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://demo.webmanza.com/a55dbz923ace647v/api/v1.0")
                .build();
    }

    public void checkConnection() {
        try {
            this.webClient.get()
                    .uri("/ping").retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5)))
                    .block();
            System.out.println("==========================================");
            System.out.println("Connection created successfully!");
            System.out.println("==========================================");
        }
        catch (Exception e) {
            System.out.println("Failed to connect : " + e.getMessage());
            throw new RuntimeException("Connection failed!",e);
        }

    }
}
