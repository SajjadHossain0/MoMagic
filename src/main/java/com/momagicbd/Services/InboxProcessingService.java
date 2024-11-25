package com.momagicbd.Services;

import com.momagicbd.DTO.*;
import com.momagicbd.Entities.*;
import com.momagicbd.Repositories.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
public class InboxProcessingService {

    private final WebClient webClient;
    private final InboxRepository inboxRepository;
    private final KeywordDetailsRepository keywordDetailsRepository;
    private final ChargeConfigRepository chargeConfigRepository;
    private final ChargeSuccessRepository chargeSuccessRepository;
    private final ChargeFailureRepository chargeFailureRepository;

    public InboxProcessingService(WebClient.Builder webClintBuilder, InboxRepository inboxRepository, KeywordDetailsRepository keywordDetailsRepository, ChargeConfigRepository chargeConfigRepository, ChargeSuccessRepository chargeSuccessRepository, ChargeFailureRepository chargeFailureRepository, ContentService contentService) {
        this.webClient = webClintBuilder
                .baseUrl("http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services").build();
        this.inboxRepository = inboxRepository;
        this.keywordDetailsRepository = keywordDetailsRepository;
        this.chargeConfigRepository = chargeConfigRepository;
        this.chargeSuccessRepository = chargeSuccessRepository;
        this.chargeFailureRepository = chargeFailureRepository;
    }

    public void processInbox() {
        List<Inbox> inboxList = inboxRepository.findByStatus("N");

        for (Inbox inbox : inboxList) {
            try {

                // check keyword is valid or not
                if (!keywordDetailsRepository.existsByKeyword(inbox.getKeyword())) {
                    inbox.setStatus("F");
                    inboxRepository.save(inbox);
                    System.out.println("keyword : " + inbox.getKeyword());
                    continue;
                }

                // retrieve the unlock code
                UnlockCodeResponse unlockCodeResponse = retrieveUnlockCode(inbox);
                if (unlockCodeResponse == null || unlockCodeResponse.getStatusCode() != 200) {
                    inbox.setStatus("F");
                    inboxRepository.save(inbox);
                    System.out.println("Unlock code retrieval failed with status: " +
                            (unlockCodeResponse == null ? "null response" : unlockCodeResponse.getStatusCode()));
                    continue;
                }

                // perform charging
                boolean chargeSuccess = performCharging(inbox);
                if (chargeSuccess) {
                    chargeSuccessLog(inbox);
                    inbox.setStatus("S");
                } else {
                    chargeFailureLog(inbox);
                    inbox.setStatus("F");
                }
                inboxRepository.save(inbox);

                System.out.println("==========================================");
                System.out.println("Charging complete!");
                System.out.println("==========================================");

            } catch (Exception e) {
                System.out.println("Exception while processing inbox ID: " + inbox.getId());
                e.printStackTrace();
                inbox.setStatus("F");
                inboxRepository.save(inbox);
            }
        }
    }

    private UnlockCodeResponse retrieveUnlockCode(Inbox inbox) {
        UnlockCodeRequest unlockCodeRequest = new UnlockCodeRequest(inbox);

        // Validate unlockCodeRequest
        if (unlockCodeRequest.getTransactionId() == null || unlockCodeRequest.getKeyword() == null) {
            throw new IllegalArgumentException("Missing required fields in UnlockCodeRequest");
        }

        // Log the unlockCodeRequest details
        System.out.println("Sending UnlockCodeRequest: " + unlockCodeRequest);

        try {
            return webClient.post()
                    .uri("/unlockCode")
                    .header("Content-Type", "application/json")
                    .bodyValue(unlockCodeRequest)
                    .retrieve()
                    .bodyToMono(UnlockCodeResponse.class)
                    .doOnSubscribe(subscription ->
                            System.out.println("Request Headers: Content-Type=application/json"))
                    .doOnSuccess(response ->
                            System.out.println("Received UnlockCodeResponse: " + response))
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5)))
                    .block();

        } catch (RuntimeException e) {
            System.err.println("Error during unlock code retrieval for payload: " + unlockCodeRequest);
            e.printStackTrace();
            return null;
        }
    }

    private boolean performCharging(Inbox inbox) {
        ChargeConfig chargeConfig = chargeConfigRepository.findByOperator(inbox.getOperator());

        if (chargeConfig == null) {
            return false;
        }

        try {
            ChargeResponse chargeResponse = webClient.post()
                    .uri("/charge")
                    .bodyValue(new ChargeRequest(inbox, chargeConfig.getChargeCode()))
                    .retrieve()
                    .bodyToMono(ChargeResponse.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5)))
                    .block();

            return chargeResponse != null && chargeResponse.getStatusCode() == 200;

        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void chargeSuccessLog(Inbox inbox) {
        ChargeSuccess chargeSuccess = new ChargeSuccess(inbox);

        chargeSuccessRepository.save(chargeSuccess);
    }

    private void chargeFailureLog(Inbox inbox) {
        ChargeFailure chargeFailure = new ChargeFailure(inbox);

        chargeFailureRepository.save(chargeFailure);
    }

    @Bean
    public WebClient webClientWithLogging(WebClient.Builder webClientBuilder) {
        return webClientBuilder.filter((request, next) -> {
            System.out.println("Outgoing Request: " + request.method() + " " + request.url());
            request.headers().forEach((name, values) ->
                    values.forEach(value -> System.out.println(name + ": " + value))
            );

            return next.exchange(request).doOnSuccess(response -> {
                System.out.println("Response Status Code: " + response.statusCode());
                response.headers().asHttpHeaders().forEach((name, values) ->
                        values.forEach(value -> System.out.println(name + ": " + value))
                );
            });
        }).build();
    }
    
}
