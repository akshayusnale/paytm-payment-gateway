package com.example.paytm.payment.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultInfoResponse {
    private String resultStatus;
    private String resultCode;
    private String resultMsg;
}
