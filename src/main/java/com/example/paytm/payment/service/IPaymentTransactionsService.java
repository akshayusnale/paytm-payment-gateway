package com.example.paytm.payment.service;

import com.example.paytm.payment.dto.PaymentDetailsDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface IPaymentTransactionsService {
    Object getResponseRedirect(HttpServletRequest request) throws Exception;

    Object initiateTransactions(PaymentDetailsDto paymentDetailsDto) throws Exception;
}
