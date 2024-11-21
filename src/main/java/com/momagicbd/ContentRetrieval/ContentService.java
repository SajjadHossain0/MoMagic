package com.momagicbd.ContentRetrieval;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContentService {

    private final WebClient webClient;
    private final InboxRepository inboxRepository;


    public ContentService(WebClient.Builder webClintBuilder, WebClient webClient, InboxRepository inboxRepository) {
        this.webClient = webClient;
        this.inboxRepository = inboxRepository;
    }

    public void contentRetrive(){

        try{
            ApiResponse apiResponse = webClient.get()
                    .uri("/content").retrieve()
                    .bodyToMono(ApiResponse.class).block();

            if(apiResponse != null && apiResponse.getStatusCode() == 200){
                List<Contents> contents = apiResponse.getContents();

                contents.forEach(content -> {
                    Inbox inbox = new Inbox();

                    inbox.setTransactionId(content.getTransactionId());
                    inbox.setOperator(content.getOperator());
                    inbox.setShortCode(content.getShortCode());
                    inbox.setMsisdn(content.getMsisdn());
                    inbox.setSms(content.getSms());
                    inbox.setStatus("N");

                    String sms = content.getSms();
                    inbox.setKeyword(extractKeyword(sms));
                    inbox.setGameName(extractGameName(sms));

                    LocalDateTime now = LocalDateTime.now();
                    inbox.setCreatedAt(now);
                    inbox.setUpdatedAt(now);

                    inboxRepository.save(inbox);

                });
            }
            else {
                throw new RuntimeException("Error retrieving content");
            }

        }
        catch (Exception e){
            System.out.println("Error fetching content from API : " + e);
            throw new RuntimeException("API call failed", e);
        }

    }

    private String extractKeyword(String sms){
        String[] keywords = sms.split(" ");
        return keywords.length > 0 ? keywords[0] : "";
    }

    private String extractGameName(String sms){
        String[] keywords = sms.split(" ");
        return keywords.length > 1 ? keywords[1] : "";
    }

//    for testing perpose
    /*public static void main(String[] args) {
        String sms = "MICROSOFT ALADDIN 00000000000000180001 29406 W27";
        ContentService service = new ContentService(null, null, null);
        System.out.println("Keyword: " + service.extractKeyword(sms)); // Expected: MICROSOFT
        System.out.println("Game Name: " + service.extractGameName(sms)); // Expected: ALADDIN
    }*/


}