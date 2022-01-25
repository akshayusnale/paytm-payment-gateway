package com.example.paytm.payment.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaytmResponse {
    private String mid;
    private String orderId;
    private String token;
    private String callBackUrl;
}
