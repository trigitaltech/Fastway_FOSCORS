package com.FOS.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class GetReceiptsModel {

	final static Logger logger = LogManager.getLogger(GetReceiptsModel.class);
	private String paymentReceiptInfo;
	//private SimpleDateFormat dummyDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

	public String getPaymentReceiptInfo() {
		return paymentReceiptInfo;
	}

	public void setPaymentReceiptInfo(String paymentReceiptInfo) {
		this.paymentReceiptInfo = paymentReceiptInfo;
	}

	public int getReceiptInfoResult(com.BRM.CATV.GetReceipts.CATVReceiptsMaster bbReceiptsMaster) 
	{
		SimpleDateFormat dummyDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
		com.BRM.CATV.GetReceipts.MSO_OP_PYMT_GET_RECEIPTS_outputFlist mso_OP_PYMT_GET_RECEIPTS_outputFlist = bbReceiptsMaster.getMso_op_pymt_get_receipts_outputflist(); 
		com.BRM.CATV.GetReceipts.RESULTElement[] resultElements = mso_OP_PYMT_GET_RECEIPTS_outputFlist.getResults();
		String ELEM = "-", ACCOUNT_NO = "-", ADDRESS = "-", AMOUNT = "-", BANK_ACCOUNT_NO = "-", BANK_CODE = "-", BANK_NAME = "-", BRANCH_NO = "-", CHEQUE_NO = "-",
				CREATED_T = "-", DESCR = "-", NAME = "-", PAY_TYPE = "-", RECEIPT_NO = "-", REFERENCE_ID = "-";
		int resultElementsLength = resultElements.length;
		if(resultElementsLength > 3)
			resultElementsLength = 3;
		logger.info("Result Element Length: "+resultElementsLength);
		
		List<Map<String, String>> paymentReceiptInfoList = new ArrayList<Map<String, String>>();
		
		for(int i = 0; i< resultElementsLength; i++)
		{			
			Map<String, String> paymentReceiptInfoMap = new LinkedHashMap<String, String>();
			ELEM = ""+i;
			ACCOUNT_NO = resultElements[i].getAccount_no();
			ADDRESS = resultElements[i].getAddress();
			AMOUNT = resultElements[i].getAmount();
			BANK_ACCOUNT_NO = resultElements[i].getBank_account_no();
			BANK_CODE = resultElements[i].getBank_code();
			BANK_NAME = resultElements[i].getBank_name();
			BRANCH_NO = resultElements[i].getBranch_no();
			CHEQUE_NO = resultElements[i].getCheck_no();
			CREATED_T = resultElements[i].getCreated_t();
			DESCR = resultElements[i].getDescr();
			NAME = resultElements[i].getName();
			PAY_TYPE = resultElements[i].getPay_type();
			RECEIPT_NO = resultElements[i].getReceipt_no();
			REFERENCE_ID = resultElements[i].getReference_id();

			if(PAY_TYPE != null)
			{
				switch (PAY_TYPE) {
				case "10011":	
					PAY_TYPE = "CASH";
					break;
				case "10012":	
					PAY_TYPE = "CHEQUE";
					break;
				case "10013":	
					PAY_TYPE = "ONLINE";
					break;
				default: PAY_TYPE = "NO STATUS";
				break;
				}
			}

			Date date = null;
			try 
			{
				if(CREATED_T != null && !CREATED_T.isEmpty())
				{
					//CREATED_T = CREATED_T.replace("T"," ").replace("Z","");
					dummyDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
					date = dummyDateFormat.parse(CREATED_T);
					CREATED_T = dateFormat.format(date);
				}
				else
					CREATED_T = "-";
			} catch (ParseException | NullPointerException e) {
				// TODO Auto-generated catch block
				logger.error("Get Customer Info: ",e);
				return -100;
			}
			paymentReceiptInfoMap.put("ELEM", ELEM);
			paymentReceiptInfoMap.put("ACCOUNT_NO", ACCOUNT_NO);
			paymentReceiptInfoMap.put("ADDRESS", ADDRESS);
			paymentReceiptInfoMap.put("AMOUNT", AMOUNT);
			paymentReceiptInfoMap.put("BANK_ACCOUNT_NO", BANK_ACCOUNT_NO);
			paymentReceiptInfoMap.put("BANK_CODE", BANK_CODE );
			paymentReceiptInfoMap.put("BANK_NAME", BANK_NAME );
			paymentReceiptInfoMap.put("BRANCH_NO", BRANCH_NO);
			paymentReceiptInfoMap.put("CHEQUE_NO", CHEQUE_NO);
			paymentReceiptInfoMap.put("CREATED_T", CREATED_T);
			paymentReceiptInfoMap.put("DESCR", DESCR); // --Imp Field
			paymentReceiptInfoMap.put("NAME", NAME); // --Imp Field
			paymentReceiptInfoMap.put("PAY_TYPE", PAY_TYPE); //Imp Field
			paymentReceiptInfoMap.put("RECEIPT_NO", RECEIPT_NO);
			paymentReceiptInfoMap.put("REFERENCE_ID", REFERENCE_ID);
			paymentReceiptInfoList.add(paymentReceiptInfoMap);
		}

		Gson gson = new Gson();
		String paymentReceiptInfo = gson.toJson(paymentReceiptInfoList);
		setPaymentReceiptInfo(paymentReceiptInfo);
		//paymentReceiptInfoMap.
		return 200;
	}
}
