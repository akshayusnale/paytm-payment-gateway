package com.example.paytm.payment.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomResponseMessage {
    private int status;
    private String message;
}
