package com.momagicbd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MomagicbdApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomagicbdApplication.class, args);
    }

}

/*
http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/ping

Content Retrieval
Request Method: GET
Request URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/content


Unlock Code Retrieval
Request Method: POST
Request URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/unlockCode

Charging
Request Method: POST
Request URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/charge





1. Connect to the MoMagic Server
First, check if the server is accessible using the ping API.
URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/ping
Implementation:
Add a service to call the API and log the response.
2. Retrieve Content
Retrieve SMS content from the content provider service.
URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/content
Method: GET
Implementation Steps:
Create a service class to call the content API.
Parse the response.
Save the retrieved content to the inbox table with an initial status N.
3. Extract Parts of SMS
Parse the retrieved SMS content using the provided format.
Extract: Keyword, Game Name, IMEI, and Device Model.
Implementation Steps:
Write a utility function to split the SMS into parts.
Validate the format and handle errors gracefully.
4. Validate Keyword
Check if the keyword exists in the keyword_details table.
Keywords are dynamic, so ensure runtime adaptability.
Implementation Steps:
Create a method in the service layer to query the keyword_details repository.
If valid, proceed to the next step; otherwise, skip.
5. Retrieve Unlock Code
If the keyword is valid, retrieve the unlock code via an API call.
URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/unlockCode
Method: POST
Implementation Steps:
Create a service method to call the unlock code API.
Pass the necessary details from the parsed SMS.
Handle API responses (success and failure).
6. Charge the User
Perform charging after successfully retrieving the unlock code.
URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/charge
Method: POST
Implementation Steps:
Create a service method to call the charge API.
Log the results:
On success, save the record in charge_success_log and update the inbox status to S.
On failure, save the record in charge_failure_log and update the inbox status to F.
7. Follow Best Practices
Use Java 21 features (e.g., virtual threads for high performance).
Write clean, modular, and well-documented code.
Use meaningful names for classes, methods, and variables.
8. Tools & Technologies
Backend: Spring Boot
Database: MySQL
API Testing: Postman
Version Control: GitHub repository
Implementation
Let’s start with step 1: checking the connection to the MoMagic server.

Ping Service
Create a PingService to call the ping API.
java
Copy code
package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PingService {

    private final WebClient webClient;

    public PingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://demo.webmanza.com/a55dbz923ace647v/api/v1.0").build();
    }

    public String checkConnection() {
        return this.webClient.get()
                .uri("/ping")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
Controller to Test Connection
Create a REST controller to test the connection.
java
Copy code
package com.example.demo.controller;

import com.example.demo.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @Autowired
    private PingService pingService;

    @GetMapping("/check-connection")
    public String checkConnection() {
        return pingService.checkConnection();
    }
}
Test the Ping
Run the Spring Boot application and hit http://localhost:8080/check-connection.

Expected output: Response from the ping API.
Next Steps
Once the connection is confirmed, we will:

Create the content retrieval service.
Store the retrieved content in the database.
Would you like to proceed with the next step?










*/
