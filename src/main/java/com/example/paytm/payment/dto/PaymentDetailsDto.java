package com.example.paytm.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDetailsDto {
    String customerId;
    String firstName;
    String lastName;
    String email;
    String mobile;
    String orderId;
    String amount;
}
