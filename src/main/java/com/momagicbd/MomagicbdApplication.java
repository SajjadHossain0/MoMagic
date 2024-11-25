package com.momagicbd;

import com.momagicbd.Services.ConnectionService;
import com.momagicbd.Services.ContentService;
import com.momagicbd.Services.InboxProcessingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MomagicbdApplication {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext contex = SpringApplication.run(MomagicbdApplication.class, args);

        boolean success = false;
        while(!success){
            try{
                ConnectionService connectionService = contex.getBean(ConnectionService.class);
                ContentService contentService = contex.getBean(ContentService.class);
                InboxProcessingService inboxProcessingService = contex.getBean(InboxProcessingService.class);

                System.out.println("Checking Connection...");
                connectionService.checkConnection();

                System.out.println("Retrieving Content...");
                contentService.contentRetrieve();

                System.out.println("Inbox Processing...");
                inboxProcessingService.processInbox();

                System.out.println("Successfully processed  data!");

                success = true;

            } catch (Exception e){
                System.err.println("Error while processing data : " + e.getMessage());
                e.printStackTrace();
                System.out.println("Retrying in 3 seconds...");

                Thread.sleep(3000);

            }
        }

    }
}
