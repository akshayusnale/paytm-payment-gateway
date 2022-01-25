package com.example.paytm.payment.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public abstract interface PaytmMerchantConfigParams {

    public static final String MERCHANTID = "Your-MerchantId";
    public static final String MERCHANTKEY = "Your_Merchant_key";
    public static final String CHANNELID = "WEB";
    public static final String WEBSITE = "WEBSTAGING";
    public static final String INDUSTRYTYPEID = "Retail";
}
