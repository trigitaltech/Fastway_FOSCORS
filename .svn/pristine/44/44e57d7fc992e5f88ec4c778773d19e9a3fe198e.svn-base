package com.FOS.Utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SMSUtils {

	final static Logger logger = LogManager.getLogger(SMSUtils.class);
	public int sendSMS(String accNo, String paidAmt, String paymentMode, String msisdn)
	{
		String date = DateUtils.getCurDateString();
		try {
			String recipient = "FASTWY";
			String message   = "Dear Customer, INR."+paidAmt+" paid by "+paymentMode+", Dated: "+date+" against your A/c."+accNo;
			String username  = "FWAYD";
			String password  = "fastway1234";
			//String receiver  = "917013870575";
			String receiver  = "91"+msisdn;
			//http://193.105.74.58/api/v3/sendsms/plain?user=FWAYD&password=Sid2014!&sender=NPLS&SMSText=Hello World&GSM=7013870575
			//http://sms.bsmart.in:8080/smart/SmartSendSMS.jsp?usr=abc&pass=xyz&msisdn=919833151862&sid=SMS&msg=test message&mt=0
			String requestUrl  = "http://193.105.74.58/api/v3/sendsms/plain?&" +
					"user=" + URLEncoder.encode(username, "UTF-8") +
					"&password=" + URLEncoder.encode(password, "UTF-8") +
					"&sender=" + URLEncoder.encode(recipient, "UTF-8") +
					"&SMSText=" + URLEncoder.encode(message, "UTF-8") +
					"&GSM=" + receiver ;

			logger.info(requestUrl.toString());
			URL url = new URL(requestUrl);
			HttpURLConnection uc = (HttpURLConnection)url.openConnection();
			logger.info(uc.getResponseMessage());
			uc.disconnect();
		} catch(Exception e) {
			logger.error(e);
			return -100;
		}
		return 200;
	}
}
