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

public class GetReceiptsExtractor 
{
final static Logger logger = LogManager.getLogger(GetReceiptsExtractor.class);
	
	private String receiptsResponse;
	
	public String getReceiptsResponse() {
		return receiptsResponse;
	}

	public void setReceiptsResponse(String receiptsResponse) {
		this.receiptsResponse = receiptsResponse;
	}

	public int extractReceiptsResult(String accountPOID, LoginValidator validator)
	{
		int result = 0;
		try {
			
			/*<MSO_OP_PYMT_GET_RECEIPTS_inputFlist>
			<MSO_FLD_SERVICE_TYPE>0</MSO_FLD_SERVICE_TYPE>
			<FLAGS>1</FLAGS>
			<POID>0.0.0.1 /account 108947289 20</POID>
			<START_T>1532737250</START_T>
			<END_T>1545980406</END_T>
			<PROGRAM_NAME>OAP|cableadmin</PROGRAM_NAME>
			</MSO_OP_PYMT_GET_RECEIPTS_inputFlist>*/
			
			Calendar calendar = Calendar.getInstance();
			long endTime = calendar.getTimeInMillis() / 1000;
			long startTime = subtractDateMonthsMillis(calendar, 6) / 1000;
			String opCodeString = "MSO_OP_PYMT_GET_RECEIPTS";
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			stringBuffer.append("<MSO_OP_PYMT_GET_RECEIPTS_inputFlist>");
			stringBuffer.append("<MSO_FLD_SERVICE_TYPE>0</MSO_FLD_SERVICE_TYPE>");
			stringBuffer.append("<FLAGS>1</FLAGS>");
			stringBuffer.append("<POID>"+accountPOID+"</POID>");
			stringBuffer.append("<START_T>"+startTime+"</START_T>");
			stringBuffer.append("<END_T>"+endTime+"</END_T>");
			stringBuffer.append("<PROGRAM_NAME>FOS_"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
			stringBuffer.append("</MSO_OP_PYMT_GET_RECEIPTS_inputFlist>");
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
			String receiptsResponseJson = xmlJSONObj.toString(INDENT_FACTOR);
			receiptsResponseJson = receiptsResponseJson.replace("\"xmlns:brm\": \"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes\",", "");
			receiptsResponseJson = receiptsResponseJson.replaceAll("\n", "").replace("\r", "");
			receiptsResponseJson = receiptsResponseJson.replaceAll("brm:", "");
			logger.info("Receipts Before:");
			logger.info(receiptsResponseJson);
			logger.info("Receipts After:");
			receiptsResponseJson = receiptsResponseJson.replace("\"RESULTS\": {", "\"RESULTS\": [{");
			receiptsResponseJson = receiptsResponseJson.replace("\"},\"POID\":", "\"}],\"POID\":");
			logger.info(receiptsResponseJson);
			setReceiptsResponse(receiptsResponseJson);
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
