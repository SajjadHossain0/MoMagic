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
    private String gameName;

    public UnlockCodeRequest(Inbox inbox) {
        this.transactionId = inbox.getTransactionId();
        this.operator = inbox.getOperator();
        this.shortCode = inbox.getShortCode();
        this.msisdn = inbox.getMsisdn();
        this.keyword = inbox.getKeyword();
        this.gameName = inbox.getGameName();
    }

    @Override
    public String toString() {
        return "UnlockCodeRequest : \n" +
                "{\n" +
                "'transactionId': '"+transactionId+"',\n"+
                "'operator': '"+operator+"',\n"+
                "'shortCode': '"+shortCode+"',\n"+
                "'msisdn': '"+msisdn+"',\n"+
                "'keyword': '"+keyword+"',\n"+
                "'gameName': '"+gameName+"',\n" +
                "}";
    }

}
