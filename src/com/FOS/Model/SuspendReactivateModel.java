package com.FOS.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.Opcode;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.OpcodeResponse;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;
import com.FOS.Extractor.SuspendReactivateExtractor;
import com.FOS.Validator.LoginValidator;
import com.google.gson.Gson;

public class SuspendReactivateModel {

	final static Logger logger = LogManager.getLogger(SuspendReactivateModel.class);
	private String suspendResponse;
	private String reactivateResponse;
	private String obrmResponseString;
	
	public String getSuspendResponse() {
		return suspendResponse;
	}

	public void setSuspendResponse(String suspendResponse) {
		this.suspendResponse = suspendResponse;
	}

	public String getReactivateResponse() {
		return reactivateResponse;
	}

	public void setReactivateResponse(String reactivateResponse) {
		this.reactivateResponse = reactivateResponse;
	}

	public String getObrmResponseString() {
		return obrmResponseString;
	}

	public void setObrmResponseString(String obrmResponseString) {
		this.obrmResponseString = obrmResponseString;
	}

	public int suspendResult(SuspendReactivateExtractor extractor,
			ISPDBConnector ispDBConnector, LoginValidator validator,
			String agentID, String userID) {
		int result = 200;
		String suspendServiceOpCodeString = "MSO_OP_CUST_SUSPEND_SERVICE";
		String suspendServiceXMlRequest = extractor.getSuspendXMlRequest(validator, userID);
		int brmResponseResult = processBRMRequest(suspendServiceOpCodeString, suspendServiceXMlRequest);
		String responseString = getObrmResponseString();
		String REFERENCE_ID = "SUSPEND-"+new Date().getTime(), ERROR_CODE = "NA", ERROR_DESCR = "NA", JOB_RESULT = null;
		Map<String, String> suspendServiceResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		String insertQuery = "insert into FOS_SUSPEND_SERVICE_MASTER (FOS_AGENT_ID, ACCOUNT_NO, "
				+ "ACCOUNT_POID, SERVICE_OBJ, RESULT, REFERENCE_ID, ERROR_CODE, ERROR_DESCR, "
				+ "REQ_PAYLOAD_BLOB, RES_PAYLOAD_BLOB, CUSTOMER_NAME) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(insertQuery);

			Blob REQ_PAYLOAD_BLOB = connection.createBlob();
			byte[] requestPayloadByteContent = suspendServiceXMlRequest.getBytes();
			REQ_PAYLOAD_BLOB.setBytes(1, requestPayloadByteContent);

			Blob RES_PAYLOAD_BLOB = connection.createBlob();
			byte[] responsePayloadByteContent = responseString.getBytes();
			RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

			if(brmResponseResult == 200)
			{
				logger.info("Suspend service Response: ");
				logger.info(responseString);
				if(responseString.contains("ACCOUNT - Service suspend status completed successfully"))
				{
					JOB_RESULT = "SUSPEND SERVICE SUCCESS";
					suspendServiceResponseMap.put("RESULT", JOB_RESULT);
					suspendServiceResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}	

				else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
				{
					JSONObject obj = new JSONObject(responseString);
					ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_SUSPEND_SERVICE_outputFlist").optString("ERROR_CODE","404");
					ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_SUSPEND_SERVICE_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
					JOB_RESULT = "ERROR";
					suspendServiceResponseMap.put("RESULT", JOB_RESULT);
					suspendServiceResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					suspendServiceResponseMap.put("ERROR_CODE", ERROR_CODE);
					suspendServiceResponseMap.put("ERROR_DESCR", ERROR_DESCR);
					result = 403; //OBRM REFUSED
				}
			}
			else if(brmResponseResult == -100)
			{
				JOB_RESULT = "FAILED";
				suspendServiceResponseMap.put("RESULT", JOB_RESULT);
				suspendServiceResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				suspendServiceResponseMap.put("ERROR_CODE", "413");
				suspendServiceResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
				result = 500; 
			}

			int counter = 0;  			
			preparedStatement.setLong(++counter, Long.valueOf(agentID));
			preparedStatement.setString(++counter, extractor.getAccount_no());
			preparedStatement.setString(++counter, extractor.getAccount_poid());
			preparedStatement.setString(++counter, extractor.getService_obj());
			preparedStatement.setString(++counter, JOB_RESULT);
			preparedStatement.setString(++counter, REFERENCE_ID);
			preparedStatement.setString(++counter, ERROR_CODE);
			preparedStatement.setString(++counter, ERROR_DESCR);
			preparedStatement.setBlob(++counter, REQ_PAYLOAD_BLOB);
			preparedStatement.setBlob(++counter, RES_PAYLOAD_BLOB);
			preparedStatement.setString(++counter, extractor.getCustomer_name());
			preparedStatement.executeUpdate();

			String suspendServiceResponse = gson.toJson(suspendServiceResponseMap);
			setSuspendResponse(suspendServiceResponse);
			logger.info("Suspend Service Result: "+result);
			logger.info("Suspend Service Response:");
			logger.info(suspendServiceResponse);

		} catch (SQLException | NullPointerException | JSONException | ClassNotFoundException | IOException e) {
			logger.error("Suspend Service Exception: ", e);
			JOB_RESULT = "FAILED";
			suspendServiceResponseMap.put("RESULT", JOB_RESULT);
			suspendServiceResponseMap.put("REFERENCE_ID", REFERENCE_ID);
			suspendServiceResponseMap.put("ERROR_CODE", "500");
			suspendServiceResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
			String suspendServiceResponse = gson.toJson(suspendServiceResponseMap);
			setSuspendResponse(suspendServiceResponse);
			return -100;
		}
		finally {

			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int reactivateResult(SuspendReactivateExtractor extractor,
			ISPDBConnector ispDBConnector, LoginValidator validator,
			String agentID, String userID) {
		int result = 200;
		String reactivateServiceOpCodeString = "MSO_OP_CUST_REACTIVATE_SERVICE";
		String reactivateServiceXMlRequest = extractor.getReactivateXMlRequest(validator, userID);
		int brmResponseResult = processBRMRequest(reactivateServiceOpCodeString, reactivateServiceXMlRequest);
		String responseString = getObrmResponseString();
		String REFERENCE_ID = "REACTIVATE-"+new Date().getTime(), ERROR_CODE = "NA", ERROR_DESCR = "NA", JOB_RESULT = null;
		Map<String, String> reactivateServiceResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		String insertQuery = "insert into FOS_REACTIVATE_SERVICE_MASTER (FOS_AGENT_ID, ACCOUNT_NO, "
				+ "ACCOUNT_POID, SERVICE_OBJ, RESULT, REFERENCE_ID, ERROR_CODE, ERROR_DESCR, "
				+ "REQ_PAYLOAD_BLOB, RES_PAYLOAD_BLOB, CUSTOMER_NAME) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(insertQuery);

			Blob REQ_PAYLOAD_BLOB = connection.createBlob();
			byte[] requestPayloadByteContent = reactivateServiceXMlRequest.getBytes();
			REQ_PAYLOAD_BLOB.setBytes(1, requestPayloadByteContent);

			Blob RES_PAYLOAD_BLOB = connection.createBlob();
			byte[] responsePayloadByteContent = responseString.getBytes();
			RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

			if(brmResponseResult == 200)
			{
				logger.info("Reactivate Service Response: ");
				logger.info(responseString);
				if(responseString.contains("ACCOUNT - Service reactivate status completed successfully"))
				{
					JOB_RESULT = "REACTIVATE SERVICE SUCCESS";
					reactivateServiceResponseMap.put("RESULT", JOB_RESULT);
					reactivateServiceResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}	

				else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
				{
					JSONObject obj = new JSONObject(responseString);
					ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_REACTIVATE_SERVICE_outputFlist").optString("ERROR_CODE","404");
					ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_REACTIVATE_SERVICE_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
					JOB_RESULT = "ERROR";
					reactivateServiceResponseMap.put("RESULT", JOB_RESULT);
					reactivateServiceResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					reactivateServiceResponseMap.put("ERROR_CODE", ERROR_CODE);
					reactivateServiceResponseMap.put("ERROR_DESCR", ERROR_DESCR);
					result = 403; //OBRM REFUSED
				}
			}
			else if(brmResponseResult == -100)
			{
				JOB_RESULT = "FAILED";
				reactivateServiceResponseMap.put("RESULT", JOB_RESULT);
				reactivateServiceResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				reactivateServiceResponseMap.put("ERROR_CODE", "413");
				reactivateServiceResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
				result = 500; 
			}

			int counter = 0;  			
			preparedStatement.setLong(++counter, Long.valueOf(agentID));
			preparedStatement.setString(++counter, extractor.getAccount_no());
			preparedStatement.setString(++counter, extractor.getAccount_poid());
			preparedStatement.setString(++counter, extractor.getService_obj());
			preparedStatement.setString(++counter, JOB_RESULT);
			preparedStatement.setString(++counter, REFERENCE_ID);
			preparedStatement.setString(++counter, ERROR_CODE);
			preparedStatement.setString(++counter, ERROR_DESCR);
			preparedStatement.setBlob(++counter, REQ_PAYLOAD_BLOB);
			preparedStatement.setBlob(++counter, RES_PAYLOAD_BLOB);
			preparedStatement.setString(++counter, extractor.getCustomer_name());
			preparedStatement.executeUpdate();

			String reactivateServiceResponse = gson.toJson(reactivateServiceResponseMap);
			setReactivateResponse(reactivateServiceResponse);
			logger.info("Reactivate Service Result: "+result);
			logger.info("Reactivate Service Response:");
			logger.info(reactivateServiceResponse);

		} catch (SQLException | NullPointerException | JSONException | ClassNotFoundException | IOException e) {
			logger.error("Reactivate Service Exception: ", e);
			JOB_RESULT = "FAILED";
			reactivateServiceResponseMap.put("RESULT", JOB_RESULT);
			reactivateServiceResponseMap.put("REFERENCE_ID", REFERENCE_ID);
			reactivateServiceResponseMap.put("ERROR_CODE", "500");
			reactivateServiceResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
			String reactivateServiceResponse = gson.toJson(reactivateServiceResponseMap);
			setReactivateResponse(reactivateServiceResponse);
			return -100;
		}
		finally {

			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	private JSONObject convertToJSONObject(ResultSet resultSet)
			throws Exception {
		JSONObject obj = new JSONObject();
		while (resultSet.next()) {
			int total_rows = resultSet.getMetaData().getColumnCount();

			for (int i = 0; i < total_rows; i++) {
				String key = resultSet.getMetaData().getColumnLabel(i + 1).toUpperCase();
				String value = resultSet.getString(i + 1);
				if(key.equalsIgnoreCase("PLAN_POID"))
					value = "0.0.0.1 /plan "+value+" 0";
				obj.put(key, value);
			}
		}
		return obj;
	}

	private int processBRMRequest(String opCodeString, String xmlRequest)
	{
		int result = 200;
		String jsonString = null;
		try {
			InfranetWebServiceServiceStub stub = new InfranetWebServiceServiceStub();
			Opcode opcode = new Opcode();
			opcode.setOpcode(opCodeString);
			opcode.setInputXML(xmlRequest);
			opcode.setM_SchemaFile("?");
			OpcodeResponse opcodeResponse = stub.opcode(opcode);
			String responseString = opcodeResponse.getOpcodeReturn();

			//logger.info(responseString);
			int INDENT_FACTOR = 4;
			JSONObject xmlJSONObj = XML.toJSONObject(responseString);
			jsonString = xmlJSONObj.toString(INDENT_FACTOR);
			jsonString = jsonString.replaceAll("\"xmlns:brm\": \"http://xmlns.oracle.com/BRM/schemas/BusinessOpcodes\",", "");
			jsonString = jsonString.replaceAll("\n", "").replace("\r", "");
			jsonString = jsonString.replaceAll("brm:", "");
			//logger.info(jsonString);

		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			jsonString = e.getMessage();
			result = -100;
		}
		setObrmResponseString(jsonString);
		return result;
	}

	@SuppressWarnings("unused")
	private String blobToStringUtil(Blob blob)
	{
		StringBuffer strOut = new StringBuffer();
		String aux;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(blob.getBinaryStream()));
			while ((aux=br.readLine())!=null)
				strOut.append(aux);
		} catch (SQLException | IOException e) {
			logger.error("Blob to String: ", e);
		}
		return strOut.toString();
	}
	
}
