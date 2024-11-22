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

check connection
http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/ping

Content Retrieval
Request Method: GET
Request URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/content

Unlock Code Retrieval
If the keyword of the content (SMS) is valid, your application shall retrieve game
unlock code from the unlock code provider service.
Request Method: POST
Request URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/unlockCode
 Status Codes: 200, 400, 500, 503, 504

 Sample Request:

{
            "transactionId": "00144a9f-01a2-4a9d-b434-a3bae9976d41",
            "operator": "ROBI",
            "shortCode": "16957",
            "msisdn": "8801824341321",
            "keyword": "HHRPDZ",
            "gamename": "SUPERMARIO"
}

Sample Response:

{
"statusCode": 200,
"message": "unlock code retrive successfully",
"unlockCode": "114111",
    "transactionId": "00144a9f-01a2-4a9d-b434-a3bae9976d41",
            "operator": "ROBI",
            "shortCode": "16957",
            "msisdn": "8801824341321",
            "keyword": "HHRPDZ",
            "gamename": "SUPERMARIO"
}

Charging
Once the unlock code has successfully been retrieved, your application shall perform
charging.
Request Method: POST
Request URL: http://demo.webmanza.com/a55dbz923ace647v/api/v1.0/services/charge
Status Codes: 200, 400, 500, 503, 504

Sample Request:

{
            "transactionId": "00144a9f-01a2-4a9d-b434-a3bae9976d41",
            "operator": "ROBI",
            "shortCode": "16957",
            "msisdn": "8801824341321",
            "chargeCode": "RO874556"
}

Sample Response:

{
            "statusCode": 200,
            "message": "Successfully performed charging",
            "transactionId": "00144a9f-01a2-4a9d-b434-a3bae9976d41",
            "operator": "ROBI",
            "shortCode": "16957",
            "msisdn": "8801824341321",
            "chargeCode": "RO874556"
}
Detailed Explanation
First of all, content shall be retrieved from the content provider service. After receiving the
content, it shall be inserted into the database’s inbox
 table. Each SMS shall be inserted into
the database with an initial status N.
Then your application needs to extract parts of the SMS from the content. Here is a sample
SMS:
IBM NEEDFORSPEED 00000000000000014303 84486 L34
The SMS format is given below:
Keyword
Game name
IMEI
IBM
Device Model
NEEDFORSPEED 00000000000000014303 84486
L34
Now the application needs to check if the keyword is valid by checking if the keyword exists
in the keyword_details
 table. Remember, new keywords might get added to the database
while your application is still running. So, the application should be able to adapt to the
change.
If the keyword is not valid, the system shall proceed to the next content. Otherwise, your
system shall try to retrieve unlock code by calling the appropriate API.
If unlock code retrieval succeeds, the system shall attempt to charge. If charging succeeds,
an you must make an entry to the charge_success_log
 table and update the status of the
content on the inbox
 charge_failure_log
 table to S. But if charging fails, you shall make an entry to the
table and update the status of the content on the inbox table to F.




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
