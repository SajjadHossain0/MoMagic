package com.momagicbd.Services;

import com.momagicbd.DTO.ChargeRequest;
import com.momagicbd.DTO.ChargeResponse;
import com.momagicbd.DTO.UnlockCodeRequest;
import com.momagicbd.DTO.UnlockCodeResponse;
import com.momagicbd.Entities.ChargeConfig;
import com.momagicbd.Entities.ChargeFailure;
import com.momagicbd.Entities.ChargeSuccess;
import com.momagicbd.Entities.Inbox;
import com.momagicbd.Repositories.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class InboxProcessingService {

    private final WebClient webClient;
    private final InboxRepository inboxRepository;
    private final KeywordDetailsRepository keywordDetailsRepository;
    private final ChargeConfigRepository chargeConfigRepository;
    private final ChargeSuccessRepository chargeSuccessRepository;
    private final ChargeFailureRepository chargeFailureRepository;
    private final ContentService contentService;

    public InboxProcessingService(WebClient.Builder webClintBuilder, InboxRepository inboxRepository, KeywordDetailsRepository keywordDetailsRepository, ChargeConfigRepository chargeConfigRepository, ChargeSuccessRepository chargeSuccessRepository, ChargeFailureRepository chargeFailureRepository, ContentService contentService) {
        this.webClient = webClintBuilder
                .baseUrl("http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services").build();
        this.inboxRepository = inboxRepository;
        this.keywordDetailsRepository = keywordDetailsRepository;
        this.chargeConfigRepository = chargeConfigRepository;
        this.chargeSuccessRepository = chargeSuccessRepository;
        this.chargeFailureRepository = chargeFailureRepository;
        this.contentService = contentService;
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

                // retrive the unlock code
                UnlockCodeResponse unlockCodeResponse = retrieveUnlockCode(inbox);
                if (unlockCodeResponse == null || unlockCodeResponse.getStatusCode() != 200) {
                    inbox.setStatus("F");
                    inboxRepository.save(inbox);
                    System.out.println("Unlock code retrieval failed with status: " +
                            (unlockCodeResponse == null ? "null response" : unlockCodeResponse.getStatusCode()));
                    continue;
                }

                // perform charging
                boolean chargeSuccess = performCharging(inbox, unlockCodeResponse.getUnlockCode());
                if (chargeSuccess) {
                    chargeSuccessLog(inbox);
                    inbox.setStatus("S");
                } else {
                    chargeFailureLog(inbox);
                    inbox.setStatus("F");
                }
                inboxRepository.save(inbox);



            } catch (Exception e) {
                System.out.println("Exception while processing inbox ID: " + inbox.getId());
                e.printStackTrace();
                inbox.setStatus("F");
                inboxRepository.save(inbox);
            }
        }
    }


    private UnlockCodeResponse retrieveUnlockCode(Inbox inbox) {
        UnlockCodeRequest requestPayload = new UnlockCodeRequest(inbox);

        // Validate payload
        if (requestPayload.getTransactionId() == null || requestPayload.getKeyword() == null) {
            throw new IllegalArgumentException("Missing required fields in UnlockCodeRequest");
        }

        // Log the payload details
        System.out.println("Sending UnlockCodeRequest: " + requestPayload);

        try {
            // Make the API call
            return webClient.post()
                    .uri("/unlockCode")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestPayload)
                    .retrieve()
                    .bodyToMono(UnlockCodeResponse.class)
                    .doOnSubscribe(subscription ->
                            System.out.println("Request Headers: Content-Type=application/json"))
                    .doOnSuccess(response ->
                            System.out.println("Received UnlockCodeResponse: " + response))
                    .block();

        } catch (RuntimeException e) {
            // Log the error with full details
            System.err.println("Error during unlock code retrieval for payload: " + requestPayload);
            e.printStackTrace();
            return null;
        }
    }

    private boolean performCharging(Inbox inbox, String unlockCode) {
        ChargeConfig chargeConfig = chargeConfigRepository.findByOperator(inbox.getOperator());

        if (chargeConfig == null) {
            return false;
        }

        try {
            ChargeResponse chargeResponse = webClient.post()
                    .uri("/charge")
                    .bodyValue(new ChargeRequest(inbox, chargeConfig.getChargeCode(), unlockCode))
                    .retrieve()
                    .bodyToMono(ChargeResponse.class).block();

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
