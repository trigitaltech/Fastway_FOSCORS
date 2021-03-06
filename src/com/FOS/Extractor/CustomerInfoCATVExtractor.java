package com.FOS.Extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;

import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.Opcode;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.OpcodeResponse;

public class CustomerInfoCATVExtractor {

	final static Logger logger = LogManager.getLogger(CustomerInfoCATVExtractor.class);

	private String customerInfoResponse;

	public String getCustomerInfoResponse() {
		return customerInfoResponse;
	}

	public void setCustomerInfoResponse(String customerInfoResponse) {
		this.customerInfoResponse = customerInfoResponse;
	}

	public int extractResponseResult(String type, String parameter, String userID)
	{
		int result = 0;
		String ACCOUNT_NO = getCustomerAccountNo(type, parameter, userID);
		logger.info("ACCOUNT_NO: "+ACCOUNT_NO);

		switch (ACCOUNT_NO) {
		case "NC":
			return 204; //NO CONTENT
		case "BAD":
			return 400; //BAD REQUEST
		case "EX":	
		case "NA":
			return 500; //INTERNAL SERVER ERROR
		default: 
			break;
		}

		try {
			String opCodeString = "MSO_OP_CUST_GET_CUSTOMER_INFO";
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
			stringBuffer.append("<MSO_OP_CUST_GET_CUSTOMER_INFO_inputFlist>");
			stringBuffer.append("<ACCOUNT_NO>"+ACCOUNT_NO+"</ACCOUNT_NO>");
			stringBuffer.append("<FLAGS>0</FLAGS>");
			stringBuffer.append("<PROGRAM_NAME>FWFOS</PROGRAM_NAME>");
			stringBuffer.append("<POID>0.0.0.1 / 0 0</POID>");
			//stringBuffer.append("<USERID>0.0.0.1 /account 115000 0</USERID>");
			stringBuffer.append("<USERID>"+userID+"</USERID>");
			stringBuffer.append("</MSO_OP_CUST_GET_CUSTOMER_INFO_inputFlist>");
			String xmlString = stringBuffer.toString();
			logger.info("Input Payload: "+xmlString);
			InfranetWebServiceServiceStub stub = new InfranetWebServiceServiceStub();
			Opcode opcode = new Opcode();
			opcode.setOpcode(opCodeString);
			opcode.setInputXML(xmlString);
			opcode.setM_SchemaFile("?");
			OpcodeResponse response = stub.opcode(opcode);
			String responseString = response.getOpcodeReturn();
			logger.info("Actual XML String :");
			logger.info(responseString);

			responseString = responseString.replace("\"xmlns:brm\": \"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes\",", "");
			responseString = responseString.replaceAll("\n", "").replace("\r", "");
			responseString = responseString.replaceAll("brm:", "");
			responseString = responseString.replaceAll(Pattern.quote("</PLAN>"),"</PLAN><TEST_TAG_1>1</TEST_TAG_1>");
			responseString = responseString.replaceAll(Pattern.quote("</SERVICES>"),"</SERVICES><TEST_TAG_2>2</TEST_TAG_2>");
			logger.info("Modified XML String :");
			logger.info(responseString);

			int INDENT_FACTOR = 0;

			JSONObject xmlJSONObj = XML.toJSONObject(responseString);
			String jsonString = xmlJSONObj.toString(INDENT_FACTOR);
			jsonString = jsonString.replace("\"xmlns:brm\": \"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes\",", "");
			jsonString = jsonString.replaceAll("\n", "").replace("\r", "");
			jsonString = jsonString.replaceAll("brm:", "");
			logger.info("Customer Info Actual Response: ");
			logger.info(jsonString);
			if(jsonString != null && jsonString.toString().contains("ACCOUNT_NO"))
			{
				//1. Devices Block
				jsonString = jsonString.replace("\"DEVICES\": {", "\"DEVICES\": [{");
				jsonString = jsonString.replace("},\"EFFECTIVE_T\":", "}],\"EFFECTIVE_T\":");

				//2. Alias List Block
				jsonString = jsonString.replace("\"ALIAS_LIST\": {", "\"ALIAS_LIST\": [{");
				/*jsonString = jsonString.replace("},\"elem\": 0,\"STATUS_FLAGS\":", "}],\"elem\": 0,\"STATUS_FLAGS\":");
			jsonString = jsonString.replace("},\"elem\": 1,\"STATUS_FLAGS\":", "}],\"elem\": 1,\"STATUS_FLAGS\":");
			jsonString = jsonString.replace("},\"elem\": 2,\"STATUS_FLAGS\": ", "}],\"elem\": 2,\"STATUS_FLAGS\": ");
			jsonString = jsonString.replace("},\"elem\": 3,\"STATUS_FLAGS\": ", "}],\"elem\": 3,\"STATUS_FLAGS\": ");
			jsonString = jsonString.replace("},\"elem\": 4,\"STATUS_FLAGS\": ", "}],\"elem\": 4,\"STATUS_FLAGS\": ");
			jsonString = jsonString.replace("},\"elem\": 5,\"STATUS_FLAGS\": ", "}],\"elem\": 5,\"STATUS_FLAGS\": ");
			jsonString = jsonString.replace("},\"elem\": 6,\"STATUS_FLAGS\": ", "}],\"elem\": 6,\"STATUS_FLAGS\": ");
			jsonString = jsonString.replace("},\"elem\": 7,\"STATUS_FLAGS\": ", "}],\"elem\": 7,\"STATUS_FLAGS\": ");
			jsonString = jsonString.replace("},\"elem\": 8,\"STATUS_FLAGS\": ", "}],\"elem\": 8,\"STATUS_FLAGS\": ");
			jsonString = jsonString.replace("},\"elem\": 9,\"STATUS_FLAGS\": ", "}],\"elem\": 9,\"STATUS_FLAGS\": ");
			jsonString = jsonString.replace("},\"elem\": 10,\"STATUS_FLAGS\": ", "}],\"elem\": 10,\"STATUS_FLAGS\": ");*/

				jsonString = jsonString.replace("},\"NAME\": \"PIN Service Object\",", "}],\"NAME\": \"PIN Service Object\",");

				//3. Services Block
				jsonString = jsonString.replace("\"SERVICES\": {", "\"SERVICES\": [{");
				jsonString = jsonString.replace("}},\"MSO_FLD_CONTACT_PREF\":", "}]},\"MSO_FLD_CONTACT_PREF\":");
				jsonString = jsonString.replace("}},\"DEPOSIT\":", "}]},\"DEPOSIT\":");
				jsonString = jsonString.replace(",\"TEST_TAG_2\": [2,2]","");
				jsonString = jsonString.replace("},\"TEST_TAG_2\": 2","}]");

				//4. Plan Block
				//jsonString = jsonString.replace("\"PLAN\": {", "\"PLAN\": [            {");
				jsonString = jsonString.replaceAll(Pattern.quote("\"elem\": 1,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 1,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\": 2", "}]},{\"elem\": 2"); 	
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 2,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 2,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\":3,", "}]},{\"elem\":3,");
				jsonString = jsonString.replace("}},{\"elem\":","}]},{\"elem\":");

				jsonString = jsonString.replace("\"}},{\"elem\": 3,\"PLAN\":", "\"}]},{\"elem\": 3,\"PLAN\":");
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 3,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 3,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\":4,", "}]},{\"elem\":4,");
				jsonString = jsonString.replace("}},{\"elem\":","}]},{\"elem\":");

				jsonString = jsonString.replace("\"}},{\"elem\": 4,\"PLAN\":", "\"}]},{\"elem\": 4,\"PLAN\":");
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 4,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 4,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\":5,", "}]},{\"elem\":5,");
				jsonString = jsonString.replace("}},{\"elem\":","}]},{\"elem\":");

				jsonString = jsonString.replace("\"}},{\"elem\": 5,\"PLAN\":", "\"}]},{\"elem\": 5,\"PLAN\":");
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 5,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 5,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\":6,", "}]},{\"elem\":6,");
				jsonString = jsonString.replace("}},{\"elem\":","}]},{\"elem\":");

				jsonString = jsonString.replace("\"}},{\"elem\": 6,\"PLAN\":", "\"}]},{\"elem\": 6,\"PLAN\":");
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 6,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 6,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\":7,", "}]},{\"elem\":7,");
				jsonString = jsonString.replace("}},{\"elem\":","}]},{\"elem\":");

				jsonString = jsonString.replace("\"}},{\"elem\": 7,\"PLAN\":", "\"}]},{\"elem\": 7,\"PLAN\":");
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 7,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 7,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\":8,", "}]},{\"elem\":8,");
				jsonString = jsonString.replace("}},{\"elem\":","}]},{\"elem\":");

				jsonString = jsonString.replace("\"}},{\"elem\": 8,\"PLAN\":", "\"}]},{\"elem\": 8,\"PLAN\":");
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 8,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 8,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\":9,", "}]},{\"elem\":9,");
				jsonString = jsonString.replace("}},{\"elem\":","}]},{\"elem\":");

				jsonString = jsonString.replace("\"}},{\"elem\": 9,\"PLAN\":", "\"}]},{\"elem\": 9,\"PLAN\":");
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 9,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 9,\"PLAN\": [{"));

				jsonString = jsonString.replace("}},{\"elem\":10,", "}]},{\"elem\":10,");
				jsonString = jsonString.replace("}},{\"elem\":","}]},{\"elem\":");

				jsonString = jsonString.replace("\"}},{\"elem\": 10,\"PLAN\":", "\"}]},{\"elem\": 10,\"PLAN\":");
				jsonString = jsonString.replace(Pattern.quote("\"elem\": 10,\"PLAN\": {"), 
						Matcher.quoteReplacement("\"elem\": 10,\"PLAN\": [{"));

				//jsonString = jsonString.replace("}}],\"AAC_VENDOR\":","}]}],\"AAC_VENDOR\":");
				jsonString = jsonString.replace("}}],\"BAL_GRP_OBJ\":","}]}],\"BAL_GRP_OBJ\":");

				jsonString = jsonString.replace("\"PLAN\": {", "\"PLAN\": [{");
				jsonString = jsonString.replace(",\"TEST_TAG_1\": [1,1]","");
				jsonString = jsonString.replace("},\"TEST_TAG_1\": 1","}]");

				/*jsonString = jsonString.replace("\"DESCR\": \"\"}},{\"elem\": 3}],\"AAC_VENDOR\": \"\",",
					"\"DESCR\": \"\"}]},{\"elem\": 3}],\"AAC_VENDOR\": \"\",");*/

				//5. Products Block
				jsonString = jsonString.replaceAll(Pattern.quote("\"PRODUCTS\": {"), Matcher.quoteReplacement("\"PRODUCTS\": [{"));
				jsonString = jsonString.replaceAll(Pattern.quote("\"},\"PLAN_OBJ\": \""), Matcher.quoteReplacement("\"}],\"PLAN_OBJ\": \""));
				//jsonString = jsonString.replace("},\"MSO_FLD_DISC_AMOUNT\": ","}],\"MSO_FLD_DISC_AMOUNT\": ");

				//4. Members Block },"PARENT":
				jsonString = jsonString.replace("\"MEMBERS\": {","\"MEMBERS\": [{");
				jsonString = jsonString.replace("},\"CONN_TYPE\":","}],\"CONN_TYPE\":");
				jsonString = jsonString.replace("},\"PARENT\":","}],\"PARENT\":");
				//5. Credit Profile Block
				jsonString = jsonString.replace("\"MSO_FLD_CREDIT_PROFILE\": {","\"MSO_FLD_CREDIT_PROFILE\": [{");
				if(jsonString.contains("GROUP_INFO"))
				{
					if(jsonString.contains("},\"GROUP_INFO\": {"))
						jsonString = jsonString.replace("},\"GROUP_INFO\": {","}],\"GROUP_INFO\": {");
				}  
				else
					jsonString = jsonString.replace("},\"MSO_FLD_DATA_ACCESS\":","}],\"MSO_FLD_DATA_ACCESS\":");

				//6. NameInfo Block
				jsonString = jsonString.replace("\"LASTSTAT_CMNT\": \"en_US\",\"PARENT_FLAGS\": 1,\"NAMEINFO\": {", "\"LASTSTAT_CMNT\": \"en_US\",\"PARENT_FLAGS\": 1,\"NAMEINFO\": [{");
				jsonString = jsonString.replace("},\"CREATED_T\"", "}],\"CREATED_T\"");

				//7. New NameInfo Block
				jsonString = jsonString.replace("\"LASTSTAT_CMNT\": \"\",\"PARENT_FLAGS\": 1,\"NAMEINFO\": {", "\"LASTSTAT_CMNT\": \"\",\"PARENT_FLAGS\": 1,\"NAMEINFO\": [{");
				jsonString = jsonString.replace("},\"CREATED_T\"", "}],\"CREATED_T\"");

				//8. Phones		"PHONES": {
				jsonString = jsonString.replace("\"PHONES\": {", "\"PHONES\": [{");
				jsonString = jsonString.replace("},\"SERVICE_OBJ\": ","}],\"SERVICE_OBJ\": ");
				logger.info("Customer Info Modified Response: ");
				logger.info(jsonString);
				setCustomerInfoResponse(jsonString);

				result = 200;
			}
			else if(jsonString.contains("0.0.0.1 /search -1 0"))
				result = 204;
			else if(jsonString.contains("ERROR_CODE") && jsonString.contains("ERROR_DESCR"))
			{
				JSONObject obj = new JSONObject(jsonString);
				String ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_GET_CUSTOMER_INFO_outputFlist").optString("ERROR_CODE","404");
				String ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_GET_CUSTOMER_INFO_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
				setCustomerInfoResponse(ERROR_CODE+":"+ERROR_DESCR);
				result = 403; //OBRM REFUSED
			}
		} catch (Exception e) {
			logger.error("Error occurred while sending SOAP Request to Server: ", e);
			return result = 500;
		}
		return result;
	}

	private String getCustomerAccountNo(String type, String parameter, String userID)
	{
		String ACCOUNT_NO = null;
		String opCodeString = "MSO_OP_SEARCH";
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		stringBuffer.append("<MSO_OP_SEARCH_inputFlist>");
		//stringBuffer.append("<NAME>02077929393</NAME>");
		//stringBuffer.append("<MSO_FLD_RMN>7837135933</MSO_FLD_RMN>");
		switch (type) {
		case "ACC":
			return parameter;
		case "VC":
		case "STB":
			stringBuffer.append("<NAME>"+parameter.trim()+"</NAME>");
			stringBuffer.append("<FLAGS>6</FLAGS>");
			break;
			//New Version added on 1/1/17
		case "SERIAL":
			stringBuffer.append("<MSO_FLD_SERIAL_NO>"+parameter.trim()+"</MSO_FLD_SERIAL_NO>");
			stringBuffer.append("<FLAGS>122</FLAGS>");
			break;	
		case "RMN":
			stringBuffer.append("<MSO_FLD_RMN>"+parameter.trim()+"</MSO_FLD_RMN>");
			stringBuffer.append("<FLAGS>2</FLAGS>");
			break;	

		default: return "BAD"; // Bad Request
		}
		stringBuffer.append("<PROGRAM_NAME>FWFOS</PROGRAM_NAME>");
		stringBuffer.append("<POID>0.0.0.1 /search -1 0</POID>");
		//stringBuffer.append("<USERID>0.0.0.1 /account 69206 7</USERID>");
		stringBuffer.append("<USERID>"+userID+"</USERID>");
		stringBuffer.append("</MSO_OP_SEARCH_inputFlist>");
		String xmlString = stringBuffer.toString();
		logger.info("Input Payload: "+xmlString);
		try {

			InfranetWebServiceServiceStub stub = new InfranetWebServiceServiceStub();
			Opcode opcode = new Opcode();
			opcode.setOpcode(opCodeString);
			opcode.setInputXML(xmlString);
			opcode.setM_SchemaFile("?");
			OpcodeResponse response = stub.opcode(opcode);
			String responseString = response.getOpcodeReturn();
			logger.info("Actual XML String :");
			logger.info(responseString);

			responseString = responseString.replace("\"xmlns:brm\": \"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes\",", "");
			responseString = responseString.replaceAll("\n", "").replace("\r", "");
			responseString = responseString.replaceAll("brm:", "");

			logger.info("Modified XML String :");
			logger.info(responseString);

			int INDENT_FACTOR = 0;

			JSONObject xmlJSONObj = XML.toJSONObject(responseString);
			String jsonString = xmlJSONObj.toString(INDENT_FACTOR);
			jsonString = jsonString.replace("\"xmlns:brm\": \"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes\",", "");
			jsonString = jsonString.replaceAll("\n", "").replace("\r", "");
			jsonString = jsonString.replaceAll("brm:", "");
			logger.info("Customer Info Actual Response: ");
			logger.info(jsonString);

			xmlJSONObj = new JSONObject(jsonString);

			if(jsonString != null && jsonString.toString().contains("ACCOUNT_NO"))
			{
				if(jsonString.contains("\"RESULTS\": {"))
					ACCOUNT_NO = xmlJSONObj.getJSONObject("MSO_OP_SEARCH_outputFlist").getJSONObject("RESULTS").optString("ACCOUNT_NO","NA");
				else if(jsonString.contains("\"RESULTS\": [{"))
					ACCOUNT_NO = xmlJSONObj.getJSONObject("MSO_OP_SEARCH_outputFlist").getJSONArray("RESULTS").getJSONObject(0).optString("ACCOUNT_NO","NA");
			}
			else if(jsonString.contains("0.0.0.1 /search -1 0"))
				ACCOUNT_NO = "NC";
			else
				ACCOUNT_NO = "NA";
		}
		catch(Exception e)
		{
			logger.error(e);
			ACCOUNT_NO = "EX";
		}
		return ACCOUNT_NO;
	}
}
