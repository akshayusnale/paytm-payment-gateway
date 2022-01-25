package com.example.paytm.payment.service.impl;

import com.example.paytm.payment.config.PaytmMerchantConfigParams;
import com.example.paytm.payment.dto.PaymentDetailsDto;
import com.example.paytm.payment.response.PaytmTransactionDetailsRespomse;
import com.example.paytm.payment.response.ResultInfoResponse;
import com.example.paytm.payment.service.IPaymentTransactionsService;
import com.paytm.pg.merchant.PaytmChecksum;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PaymentTransactionsService implements IPaymentTransactionsService {

    @Override
    public Object initiateTransactions(PaymentDetailsDto paymentDetailsDto) throws Exception{

        JSONObject paytmParams = new JSONObject();

        JSONObject body = new JSONObject();
        body.put("requestType", "Payment");
        body.put("mid", PaytmMerchantConfigParams.MERCHANTID);
        body.put("websiteName", PaytmMerchantConfigParams.WEBSITE);
        body.put("orderId", paymentDetailsDto.getOrderId());
        body.put("channelId", PaytmMerchantConfigParams.CHANNELID);
        body.put("industryTypeId", PaytmMerchantConfigParams.INDUSTRYTYPEID);
        body.put("callbackUrl", "http://localhost:8080/api/v1/paymentGateway/validateChecksum");

        JSONObject txnAmount = new JSONObject();
        txnAmount.put("value", paymentDetailsDto.getAmount());
        txnAmount.put("currency", "INR");

        JSONObject userInfo = new JSONObject();
        userInfo.put("custId", paymentDetailsDto.getCustomerId());
        userInfo.put("email", paymentDetailsDto.getEmail());
        userInfo.put("mobile", paymentDetailsDto.getMobile());
        userInfo.put("firstName", paymentDetailsDto.getFirstName());
        userInfo.put("lastName", paymentDetailsDto.getLastName());

        body.put("txnAmount", txnAmount);
        body.put("userInfo", userInfo);

        /*
         * Generate checksum by parameters we have in body
         */
        String checksum = null;
        try {
            checksum = PaytmChecksum.generateSignature(body.toString(), PaytmMerchantConfigParams.MERCHANTKEY);
            System.out.println("generateSignature Returns: " + checksum);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject head = new JSONObject();
        head.put("signature", checksum);

        paytmParams.put("body", body);
        paytmParams.put("head", head);

        String post_data = paytmParams.toString();

        /* for Staging */
        URL url = null;
        try {
            url = new URL("https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid="+ PaytmMerchantConfigParams.MERCHANTID +"&orderId="+paymentDetailsDto.getOrderId());
        } catch (MalformedURLException e) {
            throw new Exception("Message: " + e.getMessage());
        }

        /* for Production */
    /*        URL url = null;
        try {
            url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid="+ PaytmMerchantConfigParams.MERCHANTID +"&orderId="+orderId);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    */

        String responseData = "";
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
            requestWriter.writeBytes(post_data);
            requestWriter.close();
            InputStream is = connection.getInputStream();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
            if ((responseData = responseReader.readLine()) != null) {
                System.out.append("Response: " + responseData);
            }
            responseReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        JSONObject object = new JSONObject(responseData);
        JSONObject body1 = (JSONObject) object.get("body");
        JSONObject resultInfo = (JSONObject) body1.get("resultInfo");
        String resultStatus = String.valueOf(resultInfo.get("resultStatus"));

        // if payment fails send response
        if (resultStatus.equalsIgnoreCase("F")){
            ResultInfoResponse resultInfoResponse = new ResultInfoResponse();
            resultInfoResponse.setResultStatus(String.valueOf(resultInfo.get("resultStatus")));
            resultInfoResponse.setResultCode(String.valueOf(resultInfo.get("resultCode")));
            resultInfoResponse.setResultMsg(String.valueOf(resultInfo.get("resultMsg")));
            return resultInfoResponse;
        }

        String token = null;
        try {
            token = (String) body1.get("txnToken");
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

        if (token == null)
            throw new Exception("Duplicate orderId!!");

        String action = "https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage?mid="+PaytmMerchantConfigParams.MERCHANTID+"&orderId=" + paymentDetailsDto.getOrderId() + "&txnToken=" + token;
        return action;

    }

    @Override
    public Object getResponseRedirect(HttpServletRequest request) throws Exception {

        Map<String, String[]> mapData = request.getParameterMap();
        PaytmTransactionDetailsRespomse paymentGatewayTransaction = new PaytmTransactionDetailsRespomse();

        TreeMap<String, String> parameters = new TreeMap<String, String>();
        String paytmChecksum = "";
        for (Map.Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
            if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())){
                paytmChecksum = requestParamsEntry.getValue()[0];
            } else {
                parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
            }
        }
        String result;

        boolean isValideChecksum = false;
        System.out.println("RESULT : "+parameters.toString());
        try {
            isValideChecksum = validateCheckSum(parameters, paytmChecksum);
            if (isValideChecksum && parameters.containsKey("RESPCODE")) {
                if (parameters.get("RESPCODE").equals("01")) {
                    result = "Payment Successful";
                } else {
                    result = "Payment Failed";
                }
            } else {
                result = "Checksum mismatched";
            }

            // save callback details
            paymentGatewayTransaction.setBankName(parameters.get("BANKNAME"));
            paymentGatewayTransaction.setBankTxnId(parameters.get("BANKTXNID"));
            paymentGatewayTransaction.setCurrency(parameters.get("CURRENCY"));
            paymentGatewayTransaction.setGatewayName(parameters.get("GATEWAYNAME"));
            paymentGatewayTransaction.setOrderId(parameters.get("ORDERID"));
            paymentGatewayTransaction.setPaymentMode(parameters.get("PAYMENTMODE"));
            paymentGatewayTransaction.setResponseCode(parameters.get("RESPCODE"));
            paymentGatewayTransaction.setResponseMsg(parameters.get("RESPMSG"));
            paymentGatewayTransaction.setStatus(parameters.get("STATUS"));
            paymentGatewayTransaction.setTxnAmount(Double.valueOf(parameters.get("TXNAMOUNT")));
            paymentGatewayTransaction.setTxnDate(Timestamp.valueOf(parameters.get("TXNDATE")));
            paymentGatewayTransaction.setTxnId(parameters.get("TXNID"));
        } catch (Exception e) {
            result = e.toString();
        }

        return paymentGatewayTransaction;
    }

    private boolean validateCheckSum(TreeMap<String, String> parameters, String paytmChecksum) throws Exception {
        return PaytmChecksum.verifySignature(parameters,
                PaytmMerchantConfigParams.MERCHANTKEY, paytmChecksum);
    }

}
