package com.momagicbd.DTO;

import com.momagicbd.Entities.Inbox;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {
    private String transactionId;
    private String operator;
    private String shortCode;
    private String msisdn;
    private String chargeCode;

    public ChargeRequest(Inbox inbox, String chargeCode, String unlockCode) {
        this.transactionId = inbox.getTransactionId();
        this.operator = inbox.getOperator();
        this.shortCode = inbox.getShortCode();
        this.msisdn = inbox.getMsisdn();
        this.chargeCode = chargeCode;
    }

}
