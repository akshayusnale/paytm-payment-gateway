package com.example.paytm.payment.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PaytmTransactionDetailsRespomse {
    private String bankName;
    private String bankTxnId;
    private String currency;
    private String gatewayName;
    private String orderId;
    private String paymentMode;
    private String responseCode;
    private String responseMsg;
    private String status;
    private Double txnAmount;
    private Timestamp txnDate;
    private String txnId;
}
