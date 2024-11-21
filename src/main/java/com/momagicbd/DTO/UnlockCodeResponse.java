package com.momagicbd.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnlockCodeResponse {
    private int statusCode;
    private String unlockCode;
}
