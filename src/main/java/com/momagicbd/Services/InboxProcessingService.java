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


    public InboxProcessingService(WebClient.Builder webClintBuilder, InboxRepository inboxRepository, KeywordDetailsRepository keywordDetailsRepository, ChargeConfigRepository chargeConfigRepository, ChargeSuccessRepository chargeSuccessRepository, ChargeFailureRepository chargeFailureRepository) {
        this.webClient = webClintBuilder
                .baseUrl("http://demo.webmanza.com").build();
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

                // retrive the unlock code
                UnlockCodeResponse unlockCodeResponse = retrieveUnlockCode(inbox);
                if (unlockCodeResponse == null || unlockCodeResponse.getStatusCode() != 200) {
                    System.out.println("UnlockCodeResponse is null");

                    inbox.setStatus("F");
                    inboxRepository.save(inbox);
                    System.out.println("unlock code : " + unlockCodeResponse.getStatusCode());
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
        try{

             return webClient.post()
                    .uri("/a55dbz923ace647v/api/v1.0/services/unlockCode")
                    .bodyValue(new UnlockCodeRequest(inbox)).retrieve()
                    .bodyToMono(UnlockCodeResponse.class).block();

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }


        private boolean performCharging(Inbox inbox, String unlockCode) {
            ChargeConfig chargeConfig = chargeConfigRepository.findByOperator(inbox.getOperator());

            if (chargeConfig == null) {
                return false;
            }

            try{
                ChargeResponse chargeResponse = webClient.post()
                        .uri("/a55dbz923ace647v/api/v1.0/services/charge")
                        .bodyValue(new ChargeRequest(inbox, chargeConfig.getChargeCode(), unlockCode))
                        .retrieve()
                        .bodyToMono(ChargeResponse.class).block();

                return chargeResponse != null && chargeResponse.getStatusCode() == 200;

            }catch (RuntimeException e){
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

}
