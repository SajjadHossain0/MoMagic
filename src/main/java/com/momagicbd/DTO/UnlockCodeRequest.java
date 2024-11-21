package com.momagicbd.DTO;

import com.momagicbd.Entities.Inbox;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnlockCodeRequest {
    private String transactionId;
    private String operator;
    private String shortCode;
    private String msisdn;
    private String keyword;
    private String gamename;

    // Constructor using Inbox object
    public UnlockCodeRequest(Inbox inbox) {
        this.transactionId = java.util.UUID.randomUUID().toString(); // Generate a random transaction ID
        this.operator = inbox.getOperator(); // Assume Inbox has this field
        this.shortCode = inbox.getShortCode(); // Assume Inbox has this field
        this.msisdn = inbox.getMsisdn(); // Assume Inbox has this field
        this.keyword = inbox.getKeyword(); // Assume Inbox has this field
        this.gamename = inbox.getGameName(); // Assume Inbox has this field
    }

    @Override
    public String toString() {
        return "UnlockCodeRequest\n{\n" +
                "transactionId='" + transactionId + '\'' +
                ", \noperator='" + operator + '\'' +
                ", \nshortCode='" + shortCode + '\'' +
                ", \nmsisdn='" + msisdn + '\'' +
                ", \nkeyword='" + keyword + '\'' +
                ", \ngamename='" + gamename + '\n' +
                '}';
    }

}
