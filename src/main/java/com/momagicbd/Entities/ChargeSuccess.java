package com.momagicbd.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "charge_success_log")
public class ChargeSuccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sms_id", nullable = false)
    private Long smsId;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "operator", nullable = false, length = 50)
    private String operator;

    @Column(name = "short_code", nullable = false)
    private String shortCode;

    @Column(name = "msisdn", nullable = false)
    private String msisdn;

    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;

    @Column(name = "game_name", nullable = false, length = 100)
    private String gameName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist //set field whenever it is created
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate //set field whenever it is updated
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public ChargeSuccess(Inbox inbox) {
        this.smsId = inbox.getId();
        this.transactionId = inbox.getTransactionId();
        this.operator = inbox.getOperator();
        this.shortCode = inbox.getShortCode();
        this.msisdn = inbox.getMsisdn();
        this.keyword = inbox.getKeyword();
        this.gameName = inbox.getGameName();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }



}
