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

import com.BRM.CATV.CustomerSearchBill.CustomerSearchBillMaster;
import com.google.gson.Gson;

public class GetCustomerBillModel {

	final static Logger logger = LogManager.getLogger(GetCustomerBillModel.class);
	private String customerBillInfo;

	public String getCustomerBillInfo() {
		return customerBillInfo;
	}

	public void setCustomerBillInfo(String customerBillInfo) {
		this.customerBillInfo = customerBillInfo;
	}

	public int getCustomerSearchBillResult(
			CustomerSearchBillMaster customerSearchBillMaster) {
		SimpleDateFormat dummyDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
		com.BRM.CATV.CustomerSearchBill.MSO_OP_CUST_SEARCH_BILL_outputFlist mso_op_cust_search_bill_outputflist = 
				customerSearchBillMaster.getMso_op_cust_search_bill_outputflist();
		List<com.BRM.CATV.CustomerSearchBill.BILLS> bills = mso_op_cust_search_bill_outputflist.getBills();
		String ACCOUNT_OBJ = "-", BILL_NO = "-", DUE = "-", END_T = "-", POID = "-", ELEM = "-";
		
		List<Map<String, String>> customerBillInfoList = new ArrayList<Map<String, String>>();
		
		for(com.BRM.CATV.CustomerSearchBill.BILLS bill: bills)
		{			
			Map<String, String> customerBillInfoMap = new LinkedHashMap<String, String>();
			ELEM = bill.get_elem();
			ACCOUNT_OBJ = bill.getAccount_obj();
			BILL_NO = bill.getBill_no();
			DUE = bill.getDue();
			END_T = bill.getEnd_t();
			POID = bill.getPoid();
			
			Date date = null;
			try 
			{
				if(END_T != null && !END_T.isEmpty())
				{
					//END_T = END_T.replace("T"," ").replace("Z","");
					dummyDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
					date = dummyDateFormat.parse(END_T);
					END_T = dateFormat.format(date);
				}
				else
					END_T = "-";
			} catch (ParseException | NullPointerException e) {
				// TODO Auto-generated catch block
				logger.error("Get Customer Bill Info: ",e);
				return -100;
			}
			customerBillInfoMap.put("ELEM", ELEM);
			customerBillInfoMap.put("ACCOUNT_OBJ", ACCOUNT_OBJ);
			customerBillInfoMap.put("BILL_NO", BILL_NO);
			customerBillInfoMap.put("DUE", DUE);
			customerBillInfoMap.put("END_T", END_T);
			customerBillInfoMap.put("POID", POID );
			customerBillInfoList.add(customerBillInfoMap);
		}
		Gson gson = new Gson();
		String customerBillInfo = gson.toJson(customerBillInfoList);
		setCustomerBillInfo(customerBillInfo);
		return 200;
	}
}
