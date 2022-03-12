package com.FOS.Extractor;

import java.util.Calendar;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;

import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.Opcode;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.OpcodeResponse;

import com.FOS.Validator.LoginValidator;

public class CustomerSearchBillExtractor 
{
final static Logger logger = LogManager.getLogger(CustomerSearchBillExtractor.class);
	
	private String customerSearchBillResponse;
	
	public String getCustomerSearchBillResponse() {
		return customerSearchBillResponse;
	}

	public void setCustomerSearchBillResponse(String customerSearchBillResponse) {
		this.customerSearchBillResponse = customerSearchBillResponse;
	}

/*	public int extractCustomerSearchBillResult(String accountPOID, String accountNo, LoginValidator validator)*/
	public int extractCustomerSearchBillResult(String accountNo, LoginValidator validator)
	{
		int result = 0;
		try {
			
			/*MSO_OP_CUST_SEARCH_BILL
			<MSO_OP_CUST_SEARCH_BILL_inputFlist>
			<ACCOUNT_NO>CR-1043702792</ACCOUNT_NO>
			<COUNT>5</COUNT>
			<FLAGS>0</FLAGS>
			<POID>0.0.0.1 /account -1 0</POID>
			<MSO_FLD_SERVICE_TYPE>2</MSO_FLD_SERVICE_TYPE>
			<START_T>1548482455128</START_T>
			<PROGRAM_NAME>OAP|cableadmin</PROGRAM_NAME>
			</MSO_OP_CUST_SEARCH_BILL_inputFlist>*/
			Calendar calendar = Calendar.getInstance();
			long endTime = calendar.getTimeInMillis();
			long startTime = subtractDateMonthsMillis(calendar, 6);
			String opCodeString = "MSO_OP_CUST_SEARCH_BILL";
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			stringBuffer.append("<MSO_OP_CUST_SEARCH_BILL_inputFlist>");
			stringBuffer.append("<ACCOUNT_NO>"+accountNo+"</ACCOUNT_NO>");
			stringBuffer.append("<COUNT>5</COUNT>");
			stringBuffer.append("<FLAGS>0</FLAGS>");
			/*stringBuffer.append("<POID>"+accountPOID+"</POID>");*/
			stringBuffer.append("<POID>0.0.0.1 /account -1 0</POID>");
			stringBuffer.append("<MSO_FLD_SERVICE_TYPE>2</MSO_FLD_SERVICE_TYPE>");
			stringBuffer.append("<START_T>"+startTime+"</START_T>");
			stringBuffer.append("<END_T>"+endTime+"</END_T>");
			stringBuffer.append("<PROGRAM_NAME>FOS_"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
			stringBuffer.append("</MSO_OP_CUST_SEARCH_BILL_inputFlist>");
			String xmlString = stringBuffer.toString();
			logger.info("Input XML: "+xmlString);
			
			InfranetWebServiceServiceStub stub = new InfranetWebServiceServiceStub();
			Opcode opcode = new Opcode();
			opcode.setOpcode(opCodeString);
			opcode.setInputXML(xmlString);
			opcode.setM_SchemaFile("?");
			OpcodeResponse response = stub.opcode(opcode);
			String responseString = response.getOpcodeReturn();
			
			responseString = responseString.replace("\"xmlns:brm\": \"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes\",", "");
			responseString = responseString.replaceAll("\n", "").replace("\r", "");
			responseString = responseString.replaceAll("brm:", "");
			/*//System.out.println("XML String :");
			//System.out.println(responseString);
			setCustomerInfoXMLResponse(responseString);*/
			
			int INDENT_FACTOR = 0;
			JSONObject xmlJSONObj = XML.toJSONObject(responseString);
			String customerSearchBillResponseJson = xmlJSONObj.toString(INDENT_FACTOR);
			customerSearchBillResponseJson = customerSearchBillResponseJson.replace("\"xmlns:brm\": \"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes\",", "");
			customerSearchBillResponseJson = customerSearchBillResponseJson.replaceAll("\n", "").replace("\r", "");
			customerSearchBillResponseJson = customerSearchBillResponseJson.replaceAll("brm:", "");
			logger.info("Receipts Before:");
			logger.info(customerSearchBillResponseJson);
			logger.info("Receipts After:");
			customerSearchBillResponseJson = customerSearchBillResponseJson.replace("\"BILLS\": {", "\"BILLS\": [{");
			customerSearchBillResponseJson = customerSearchBillResponseJson.replace("\"},\"POID\":", "\"}],\"POID\":");
			logger.info(customerSearchBillResponseJson);
			setCustomerSearchBillResponse(customerSearchBillResponseJson);
			result = 200;
		} catch (Exception e) {
			logger.error("Error occurred while sending SOAP Request to Server: ", e);
			return result = 500;
		}
		return result;
	}
	
	private long subtractDateMonthsMillis(Calendar calendar, int monthsToSubtract)
	{
		calendar.add(Calendar.MONTH, -monthsToSubtract);
		return calendar.getTimeInMillis();
	}
}
