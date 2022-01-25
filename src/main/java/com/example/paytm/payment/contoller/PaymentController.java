package com.example.paytm.payment.contoller;


import com.example.paytm.payment.dto.PaymentDetailsDto;
import com.example.paytm.payment.response.CustomResponseMessage;
import com.example.paytm.payment.response.EntityResponse;
import com.example.paytm.payment.service.IPaymentTransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/paymentGateway")
public class PaymentController {
    @Autowired
    IPaymentTransactionsService paymentTransactionsService;

    @PostMapping(value = "/initiateTransactions")
    public ResponseEntity<?> initiateTransactions(@RequestBody PaymentDetailsDto paymentDetailsDto){
        try {
            return new ResponseEntity<>(new EntityResponse(0, "success" ,paymentTransactionsService.initiateTransactions(paymentDetailsDto)), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CustomResponseMessage(-1, "Data Error"), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/validateChecksum")
    public ResponseEntity<?> getResponseRedirect(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(new EntityResponse(0, "success" ,paymentTransactionsService.getResponseRedirect(request)), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CustomResponseMessage(-1, "Data Error"), HttpStatus.OK);
        }
    }
}
