package com.FOS.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.Opcode;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.OpcodeResponse;

import com.BRM.CATV.AddPlan.PLANElement;
import com.BRM.CATV.AddPlan.PLAN_LISTElement;
import com.BRM.CATV.CancelPlan.CANCEL_PLAN_LISTElement;
import com.BRM.CATV.ChangePlan.CUR_PLAN_LISTElement;
import com.BRM.CATV.ChangePlan.NEW_PLAN_LISTElement;
import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;
import com.FOS.DBConnector.PinDBConnector;
import com.FOS.Extractor.AddPlanExtractor;
import com.FOS.Extractor.CancelPlanExtractor;
import com.FOS.Extractor.ChangePlanExtractor;
import com.FOS.Extractor.PlanCodesExtractor;
import com.FOS.Validator.LoginValidator;
import com.google.gson.Gson;

public class PlanModel {

	final static Logger logger = LogManager.getLogger(PlanModel.class);
	private String addPlanResponse;
	private String retryAddPlanResponse;

	private String planChangeResponse;
	private String retryPlanChangeResponse;

	private String cancelPlanResponse;
	private String retryCancelPlanResponse;

	private String planInfo;
	private String planList;
	private String planByIDList;
	private String obrmResponseString;
	private String planPriceDetail;

	
	public String getPlanByIDList() {
		return planByIDList;
	}

	public void setPlanByIDList(String planByIDList) {
		this.planByIDList = planByIDList;
	}

	
	public String getPlanPriceDetail() {
		return planPriceDetail;
	}

	public void setPlanPriceDetail(String planPriceDetail) {
		this.planPriceDetail = planPriceDetail;
	}

	public String getCancelPlanResponse() {
		return cancelPlanResponse;
	}

	public void setCancelPlanResponse(String cancelPlanResponse) {
		this.cancelPlanResponse = cancelPlanResponse;
	}

	public String getRetryCancelPlanResponse() {
		return retryCancelPlanResponse;
	}

	public void setRetryCancelPlanResponse(String retryCancelPlanResponse) {
		this.retryCancelPlanResponse = retryCancelPlanResponse;
	}

	public String getPlanChangeResponse() {
		return planChangeResponse;
	}

	public void setPlanChangeResponse(String planChangeResponse) {
		this.planChangeResponse = planChangeResponse;
	}

	public String getRetryPlanChangeResponse() {
		return retryPlanChangeResponse;
	}

	public void setRetryPlanChangeResponse(String retryPlanChangeResponse) {
		this.retryPlanChangeResponse = retryPlanChangeResponse;
	}

	public String getPlanList() {
		return planList;
	}

	public void setPlanList(String planList) {
		this.planList = planList;
	}


	public String getPlanInfo() {
		return planInfo;
	}

	public void setPlanInfo(String planInfo) {
		this.planInfo = planInfo;
	}

	public String getAddPlanResponse() {
		return addPlanResponse;
	}

	public void setAddPlanResponse(String addPlanResponse) {
		this.addPlanResponse = addPlanResponse;
	}

	public String getRetryAddPlanResponse() {
		return retryAddPlanResponse;
	}

	public void setRetryAddPlanResponse(String retryAddPlanResponse) {
		this.retryAddPlanResponse = retryAddPlanResponse;
	}

	public String getObrmResponseString() {
		return obrmResponseString;
	}

	public void setObrmResponseString(String obrmResponseString) {
		this.obrmResponseString = obrmResponseString;
	}

	public int addPlanResult(AddPlanExtractor extractor,
			ISPDBConnector ispDBConnector, PinDBConnector pinDBConnector, LoginValidator validator, String agentID, String userID) {
		int result = 200;
		String addPlanOpCodeString = "MSO_OP_CUST_ADD_PLAN";
		String addPlanXMlRequest = extractor.extractAddPlanXMlRequest(validator, userID);
		int brmResponseResult = processBRMRequest(addPlanOpCodeString, addPlanXMlRequest);
		String responseString = getObrmResponseString();
		logger.info("BRM Response Code: "+brmResponseResult);
		logger.info("BRM Response: "+responseString);
		String REFERENCE_ID = "ADD_PLAN-"+new Date().getTime(), ERROR_CODE = "NA", ERROR_DESCR = "NA", JOB_RESULT = null;
		Map<String, String> addPlanResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		String insertQuery = "insert into FOS_ADD_PLAN_MASTER (FOS_AGENT_ID, ACCOUNT_NO, "
				+ "ACCOUNT_POID, SERVICE_OBJ, PLAN_LIST_ID, PLAN_LIST_NAME, SERVICE_TYPE, CITY, "
				+ "RESULT, REFERENCE_ID, ERROR_CODE, ERROR_DESCR, REQ_PAYLOAD_BLOB, RES_PAYLOAD_BLOB) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(insertQuery);

			Blob REQ_PAYLOAD_BLOB = connection.createBlob();
			byte[] requestPayloadByteContent = addPlanXMlRequest.getBytes();
			REQ_PAYLOAD_BLOB.setBytes(1, requestPayloadByteContent);

			Blob RES_PAYLOAD_BLOB = connection.createBlob();
			byte[] responsePayloadByteContent = responseString.getBytes();
			RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

			if(brmResponseResult == 200)
			{
				logger.info("Add plan response: ");
				logger.info(responseString);
				if(responseString.contains("Service add plan completed successfully"))
				{
					JOB_RESULT = "ADD PLAN SUCCESS";
					addPlanResponseMap.put("RESULT", JOB_RESULT);
					addPlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}	

				else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
				{
					JSONObject obj = new JSONObject(responseString);
					ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_ADD_PLAN_outputFlist").optString("ERROR_CODE","404");
					ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_ADD_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
					JOB_RESULT = "ERROR";
					addPlanResponseMap.put("RESULT", JOB_RESULT);
					addPlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					addPlanResponseMap.put("ERROR_CODE", ERROR_CODE);
					addPlanResponseMap.put("ERROR_DESCR", ERROR_DESCR);
					result = 403; //OBRM REFUSED
				}
			}
			else if(brmResponseResult == -100)
			{
				JOB_RESULT = "FAILED";
				addPlanResponseMap.put("RESULT", JOB_RESULT);
				addPlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				addPlanResponseMap.put("ERROR_CODE", "413");
				addPlanResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
				result = 500; 
			}

			int counter = 0;
			PLAN_LISTElement[] plan_list = extractor.getPlan_list();
			for(int i=0; i<plan_list.length; i++)
			{
				preparedStatement.setLong(++counter, Long.valueOf(agentID));
				preparedStatement.setString(++counter, extractor.getACCOUNT_NO());
				preparedStatement.setString(++counter, extractor.getACCOUNT_POID());
				preparedStatement.setString(++counter, extractor.getSERVICE_OBJ());
				preparedStatement.setString(++counter, extractor.getPlan_list()[i].getPlan_list_id());
				preparedStatement.setString(++counter, extractor.getPlan_list()[i].getPlan_list_name());
				preparedStatement.setString(++counter, extractor.getPlan_list()[i].getService_type());
				preparedStatement.setString(++counter, extractor.getPlan_list()[i].getCity());
				preparedStatement.setString(++counter, JOB_RESULT);
				preparedStatement.setString(++counter, REFERENCE_ID);
				preparedStatement.setString(++counter, ERROR_CODE);
				preparedStatement.setString(++counter, ERROR_DESCR);
				preparedStatement.setBlob(++counter, REQ_PAYLOAD_BLOB);
				preparedStatement.setBlob(++counter, RES_PAYLOAD_BLOB);
				preparedStatement.addBatch();
				counter = 0;
			}
			int[] batchResult1 = preparedStatement.executeBatch();
			logger.info("Batch Result - 1: "+batchResult1);

			HouseKeeper.closePreparedStatement(preparedStatement);
			String insertPlansSQL = "insert into FOS_ADD_PLAN_DETAIL (REFERNCE_ID, PLAN_ID, PLAN_TYPE, CODE, POID, "
					+ "SERVICE_TYPE, PAYMENT_TYPE, PLAN_CATEGORY, DEVICE_TYPE) "
					+ " values (?,?,?,?,?,?,?,?,?)";
			counter = 0;
			PreparedStatement plansPrepStmt = connection.prepareStatement(insertPlansSQL);
			PLANElement[] plans = extractor.getPlan_list()[0].getPlans();
			if(plans != null){
				for(int i=0; i<plans.length; i++)
				{
					PLANElement plan = plans[i];
					if(plan != null)
					{
						plansPrepStmt.setString(++counter, REFERENCE_ID);
						plansPrepStmt.setString(++counter, plan.getPlan_id());
						plansPrepStmt.setString(++counter, plan.getPlan_type());
						plansPrepStmt.setString(++counter, plan.getCode());
						plansPrepStmt.setString(++counter, plan.getPoid());
						plansPrepStmt.setString(++counter, plan.getService_type());
						plansPrepStmt.setString(++counter, plan.getPayment_type());
						plansPrepStmt.setString(++counter, plan.getPlan_category());
						plansPrepStmt.setString(++counter, plan.getDevice_type());
						plansPrepStmt.addBatch();
						counter = 0;
					}
				}
				int[] batchResult2 = plansPrepStmt.executeBatch();
				logger.info("Batch Result - 2: "+batchResult2);
			}
			String addPlanResponse = gson.toJson(addPlanResponseMap);
			setAddPlanResponse(addPlanResponse);

			logger.info("Add plan result: "+result);
			logger.info("Add plan response:");
			logger.info(addPlanResponse);

		} catch (SQLException | NullPointerException | JSONException | ClassNotFoundException | IOException e) {
			logger.error("Add plan exception: ",e);
			JOB_RESULT = "FAILED";
			addPlanResponseMap.put("RESULT", JOB_RESULT);
			addPlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
			addPlanResponseMap.put("ERROR_CODE", "500");
			addPlanResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
			String addPlanResponse = gson.toJson(addPlanResponseMap);
			setAddPlanResponse(addPlanResponse);
			return -100;
		}
		finally
		{
			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	/*public int retryFupTopUpResult(String referenceID, ISPDBConnector ispDBConnector, String agentID)
	{
		boolean flag = true;
		int result = 0;
		Connection connection = null; PreparedStatement preparedStatement = null; ResultSet resultSet = null;
		String fupTopUpXMlRequest = null, JOB_RESULT = null, FOS_AGENT_ID = agentID, REFERENCE_ID = referenceID, 
				fupTopUpOpCodeString = "MSO_OP_CUST_ADD_PLAN", responseString = null, ERROR_CODE = "NA", ERROR_DESCR = "NA";
		Blob REQ_PAYLOAD_BLOB = null;
		String requestPayloadQuery = "SELECT * FROM FOS_FUP_TOPUP_MASTER WHERE REFERENCE_ID = ? AND FOS_AGENT_ID = ? AND RESULT != 'SUCCESS'";
		Map<String, String> fupTopUpResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		Timestamp TRANSACTION_TIMESTAMP = new Timestamp(new Date().getTime());
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(requestPayloadQuery);
			preparedStatement.setString(1, referenceID);
			preparedStatement.setString(2, agentID);		
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				REQ_PAYLOAD_BLOB = resultSet.getBlob("REQ_PAYLOAD_BLOB");
				fupTopUpXMlRequest = blobToStringUtil(REQ_PAYLOAD_BLOB);
				//logger.info("Retry TopUp Request XML: ");
				//logger.info(xmlString);
			}	
			else
				flag = false; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			if(flag)
			{
				int brmResponseResult = processBRMRequest(fupTopUpOpCodeString, fupTopUpXMlRequest);
				responseString = getObrmResponseString();
				if(brmResponseResult == 200)
				{
					logger.info("Retry FUP Top Up Response: ");
					logger.info(responseString);
					if(responseString.contains("Service add plan completed successfully"))
					{
						JOB_RESULT = "FUP TOPUP SUCCESS";
						fupTopUpResponseMap.put("RESULT", JOB_RESULT);
						fupTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					}	

					else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
					{
						JSONObject obj = new JSONObject(responseString);
						ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_ADD_PLAN_outputFlist").optString("ERROR_CODE","404");
						ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_ADD_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
						JOB_RESULT = "ERROR";
						fupTopUpResponseMap.put("RESULT", JOB_RESULT);
						fupTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
						fupTopUpResponseMap.put("ERROR_CODE", ERROR_CODE);
						fupTopUpResponseMap.put("ERROR_DESCR", ERROR_DESCR);
						result = 403; //OBRM REFUSED
					}
				}
				else if(brmResponseResult == -100)
				{
					JOB_RESULT = "FAILED";
					fupTopUpResponseMap.put("RESULT", JOB_RESULT);
					fupTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					fupTopUpResponseMap.put("ERROR_CODE", "413");
					fupTopUpResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
					result = 500; 
				}
			}

		} 
		catch (Exception e) {
			logger.error("Retry FUP TopUp Exception:", e);
			responseString = e.getMessage();
			JOB_RESULT = "FAILED";
			fupTopUpResponseMap.put("RESULT", JOB_RESULT);
			fupTopUpResponseMap.put("ERROR_CODE", "413");
			fupTopUpResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
			fupTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
			if(flag)
			{
				try {
					connection = ispDBConnector.getConnection();
					Blob RES_PAYLOAD_BLOB = connection.createBlob();
					byte[] responsePayloadByteContent = responseString.getBytes();
					RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

					String insertQuery = "UPDATE FOS_FUP_TOPUP_MASTER SET "
							+"RESULT = ?, OBRM_RESPONSE_TIMESTAMP = ?, "
							+ "ERROR_CODE = ?, ERROR_DESCR = ?, REQ_PAYLOAD_BLOB = ?, RES_PAYLOAD_BLOB = ? "
							+ "WHERE FOS_AGENT_ID = ? AND REFERENCE_ID = ? ";
					preparedStatement = connection.prepareStatement(insertQuery);
					preparedStatement.setString(1, JOB_RESULT);
					preparedStatement.setTimestamp(2, TRANSACTION_TIMESTAMP);
					preparedStatement.setString(3, ERROR_CODE);
					preparedStatement.setString(4, ERROR_DESCR);
					preparedStatement.setBlob(5, REQ_PAYLOAD_BLOB);
					preparedStatement.setBlob(6, RES_PAYLOAD_BLOB);
					preparedStatement.setString(7, FOS_AGENT_ID);
					preparedStatement.setString(8, REFERENCE_ID);
					preparedStatement.executeUpdate();

					String retryFupTopUpResponse = gson.toJson(fupTopUpResponseMap);
					setRetryFupTopUpResponse(retryFupTopUpResponse);
					logger.info("Retry FUP Top Up Result: "+result);
					logger.info("Retry FUP Top Up Response:");
					logger.info(retryFupTopUpResponse);

				} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
					logger.error("Retry TopUp Exception: ", e);
					JOB_RESULT = "FAILED";
					fupTopUpResponseMap.put("RESULT", JOB_RESULT);
					fupTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					fupTopUpResponseMap.put("ERROR_CODE", "500");
					fupTopUpResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
					String retryFupTopUpResponse = gson.toJson(fupTopUpResponseMap);
					setRetryFupTopUpResponse(retryFupTopUpResponse);
					return -100;
				}
				finally{
					HouseKeeper.closePreparedStatement(preparedStatement);
					ispDBConnector.closeConnection(connection);
				}
			}
			else
			{
				JOB_RESULT = "BAD REQUEST";
				fupTopUpResponseMap.put("RESULT", JOB_RESULT);
				fupTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				fupTopUpResponseMap.put("ERROR_CODE", "404");
				fupTopUpResponseMap.put("ERROR_DESCR", "NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY");
				String retryFupTopUpResponse = gson.toJson(fupTopUpResponseMap);
				setRetryFupTopUpResponse(retryFupTopUpResponse);
				return 404; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			}
		}
		return result;
	}*/

	/*public int topUpResult(AddPlanExtractor extractor,
			ISPDBConnector ispDBConnector, LoginValidator validator, String agentID) {
		int result = 200;
		String topUpOpCodeString = "MSO_OP_CUST_TOP_UP_BB_PLAN";
		String topUpXMlRequest = extractor.extractTopUpXMlRequest(validator, ispDBConnector);
		int brmResponseResult = processBRMRequest(topUpOpCodeString, topUpXMlRequest);
		String responseString = getObrmResponseString();
		String REFERENCE_ID = "TOP_UP-"+new Date().getTime(), ERROR_CODE = "NA", ERROR_DESCR = "NA", JOB_RESULT = null;
		Map<String, String> gbTopUpResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		String insertQuery = "insert into FOS_GB_TOPUP_MASTER (FOS_AGENT_ID, ACCOUNT_NO, ACCOUNT_OBJ, "
				+ "ACCOUNT_POID, PLAN_OBJ, PLAN_NAME, SERVICE_OBJ, "
				+ "RESULT, REFERENCE_ID, ERROR_CODE, ERROR_DESCR, REQ_PAYLOAD_BLOB, RES_PAYLOAD_BLOB) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(insertQuery);

			Blob REQ_PAYLOAD_BLOB = connection.createBlob();
			byte[] requestPayloadByteContent = topUpXMlRequest.getBytes();
			REQ_PAYLOAD_BLOB.setBytes(1, requestPayloadByteContent);

			Blob RES_PAYLOAD_BLOB = connection.createBlob();
			byte[] responsePayloadByteContent = responseString.getBytes();
			RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

			if(brmResponseResult == 200)
			{
				logger.info("Top Up Response: ");
				logger.info(responseString);
				if(responseString.contains("Topup/renewal completed"))
				{
					JOB_RESULT = "TOPUP SUCCESS";
					gbTopUpResponseMap.put("RESULT", JOB_RESULT);
					gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}	

				else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
				{
					JSONObject obj = new JSONObject(responseString);
					ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_TOP_UP_BB_PLAN_outputFlist").optString("ERROR_CODE","404");
					ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_TOP_UP_BB_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
					JOB_RESULT = "ERROR";
					gbTopUpResponseMap.put("RESULT", JOB_RESULT);
					gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					gbTopUpResponseMap.put("ERROR_CODE", ERROR_CODE);
					gbTopUpResponseMap.put("ERROR_DESCR", ERROR_DESCR);
					result = 403; //OBRM REFUSED
				}
			}
			else if(brmResponseResult == -100)
			{
				JOB_RESULT = "FAILED";
				gbTopUpResponseMap.put("RESULT", JOB_RESULT);
				gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				gbTopUpResponseMap.put("ERROR_CODE", "413");
				gbTopUpResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
				result = 500; 
			}

			preparedStatement.setLong(1, Long.valueOf(agentID));
			preparedStatement.setString(2, extractor.getACCOUNT_NO());
			preparedStatement.setString(3, extractor.getACCOUNT_OBJ());
			preparedStatement.setString(4, extractor.getACCOUNT_POID());
			preparedStatement.setString(5, extractor.getPLAN_OBJ());
			preparedStatement.setString(6, extractor.getPLAN_NAME());
			preparedStatement.setString(7, extractor.getSERVICE_OBJ());
			preparedStatement.setString(8, JOB_RESULT);
			preparedStatement.setString(9, REFERENCE_ID);
			preparedStatement.setString(10, ERROR_CODE);
			preparedStatement.setString(11, ERROR_DESCR);
			preparedStatement.setBlob(12, REQ_PAYLOAD_BLOB);
			preparedStatement.setBlob(13, RES_PAYLOAD_BLOB);
			preparedStatement.executeUpdate();

			String topUpResponse = gson.toJson(gbTopUpResponseMap);
			setGbTopUpResponse(topUpResponse);
			logger.info("TopUp Result: "+result);
			logger.info("TopUp Response:");
			logger.info(topUpResponse);

		} catch (ClassNotFoundException | IOException | SQLException | NullPointerException | JSONException e) {

			logger.error("Top Up Exception: ",e);
			JOB_RESULT = "FAILED";
			gbTopUpResponseMap.put("RESULT", JOB_RESULT);
			gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
			gbTopUpResponseMap.put("ERROR_CODE", "500");
			gbTopUpResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
			String topUpResponse = gson.toJson(gbTopUpResponseMap);
			setGbTopUpResponse(topUpResponse);
			return -100;
		}
		finally
		{
			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int retryTopUpResult(String referenceID, ISPDBConnector ispDBConnector, String agentID)
	{
		boolean flag = true;
		int result = 0;
		Connection connection = null; PreparedStatement preparedStatement = null; ResultSet resultSet = null;
		String topUpXMlRequest = null, JOB_RESULT = null, FOS_AGENT_ID = agentID, REFERENCE_ID = referenceID, 
				topUpOpCodeString = "MSO_OP_CUST_TOP_UP_BB_PLAN", responseString = null, ERROR_CODE = "NA", ERROR_DESCR = "NA";
		Blob REQ_PAYLOAD_BLOB = null;
		String requestPayloadQuery = "SELECT * FROM FOS_GB_TOPUP_MASTER WHERE REFERENCE_ID = ? AND FOS_AGENT_ID = ? AND RESULT != 'SUCCESS'";
		Map<String, String> gbTopUpResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		Timestamp TRANSACTION_TIMESTAMP = new Timestamp(new Date().getTime());
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(requestPayloadQuery);
			preparedStatement.setString(1, referenceID);
			preparedStatement.setString(2, agentID);		
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				REQ_PAYLOAD_BLOB = resultSet.getBlob("REQ_PAYLOAD_BLOB");
				topUpXMlRequest = blobToStringUtil(REQ_PAYLOAD_BLOB);
				//logger.info("Retry TopUp Request XML: ");
				//logger.info(xmlString);
			}	
			else
				flag = false; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			if(flag)
			{
				int brmResponseResult = processBRMRequest(topUpOpCodeString, topUpXMlRequest);
				responseString = getObrmResponseString();
				if(brmResponseResult == 200)
				{
					logger.info("Top Up Response: ");
					logger.info(responseString);
					if(responseString.contains("Topup/renewal completed"))
					{
						JOB_RESULT = "TOPUP SUCCESS";
						gbTopUpResponseMap.put("RESULT", JOB_RESULT);
						gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					}	

					else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
					{
						JSONObject obj = new JSONObject(responseString);
						ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_TOP_UP_BB_PLAN_outputFlist").optString("ERROR_CODE","404");
						ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_TOP_UP_BB_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
						JOB_RESULT = "ERROR";
						gbTopUpResponseMap.put("RESULT", JOB_RESULT);
						gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
						gbTopUpResponseMap.put("ERROR_CODE", ERROR_CODE);
						gbTopUpResponseMap.put("ERROR_DESCR", ERROR_DESCR);
						result = 403; //OBRM REFUSED
					}
				}
				else if(brmResponseResult == -100)
				{
					JOB_RESULT = "FAILED";
					gbTopUpResponseMap.put("RESULT", JOB_RESULT);
					gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					gbTopUpResponseMap.put("ERROR_CODE", "413");
					gbTopUpResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
					result = 500; 
				}
			}
		} 
		catch (Exception e) {
			logger.error("Retry TopUp Exception:", e);
			responseString = e.getMessage();
			JOB_RESULT = "FAILED";
			gbTopUpResponseMap.put("RESULT", JOB_RESULT);
			gbTopUpResponseMap.put("ERROR_CODE", "413");
			gbTopUpResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
			gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
			if(flag)
			{
				try {
					connection = ispDBConnector.getConnection();
					Blob RES_PAYLOAD_BLOB = connection.createBlob();
					byte[] responsePayloadByteContent = responseString.getBytes();
					RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

					String insertQuery = "UPDATE FOS_GB_TOPUP_MASTER SET "
							+"RESULT = ?, OBRM_RESPONSE_TIMESTAMP = ?, "
							+ "ERROR_CODE = ?, ERROR_DESCR = ?, REQ_PAYLOAD_BLOB = ?, RES_PAYLOAD_BLOB = ? "
							+ "WHERE FOS_AGENT_ID = ? AND REFERENCE_ID = ? ";
					preparedStatement = connection.prepareStatement(insertQuery);
					preparedStatement.setString(1, JOB_RESULT);
					preparedStatement.setTimestamp(2, TRANSACTION_TIMESTAMP);
					preparedStatement.setString(3, ERROR_CODE);
					preparedStatement.setString(4, ERROR_DESCR);
					preparedStatement.setBlob(5, REQ_PAYLOAD_BLOB);
					preparedStatement.setBlob(6, RES_PAYLOAD_BLOB);
					preparedStatement.setString(7, FOS_AGENT_ID);
					preparedStatement.setString(8, REFERENCE_ID);
					preparedStatement.executeUpdate();

					String retryTopUpResponse = gson.toJson(gbTopUpResponseMap);
					setRetryTopUpResponse(retryTopUpResponse);
					logger.info("Retry Top Up Result: "+result);
					logger.info("Retry Top Up Response:");
					logger.info(retryTopUpResponse);

				} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
					logger.error("Retry TopUp Exception: ", e);
					JOB_RESULT = "FAILED";
					gbTopUpResponseMap.put("RESULT", JOB_RESULT);
					gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					gbTopUpResponseMap.put("ERROR_CODE", "500");
					gbTopUpResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
					String retryTopUpResponse = gson.toJson(gbTopUpResponseMap);
					setRetryTopUpResponse(retryTopUpResponse);
					return -100;
				}
				finally{
					HouseKeeper.closePreparedStatement(preparedStatement);
					ispDBConnector.closeConnection(connection);
				}
			}
			else
			{
				JOB_RESULT = "BAD REQUEST";
				gbTopUpResponseMap.put("RESULT", JOB_RESULT);
				gbTopUpResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				gbTopUpResponseMap.put("ERROR_CODE", "404");
				gbTopUpResponseMap.put("ERROR_DESCR", "NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY");
				String retryTopUpResponse = gson.toJson(gbTopUpResponseMap);
				setRetryTopUpResponse(retryTopUpResponse);
				return 404; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			}
		}
		return result;
	}

	public int planRenewalResult(AddPlanExtractor extractor,
			ISPDBConnector ispDBConnector, LoginValidator validator, String agentID) {
		int result = 200;
		String renewPlanOpCodeString = "MSO_OP_CUST_RENEW_BB_PLAN";
		String renewPlanXMlRequest = extractor.getPlanRenewalXMlRequest(validator, ispDBConnector);
		int brmResponseResult = processBRMRequest(renewPlanOpCodeString, renewPlanXMlRequest);
		String responseString = getObrmResponseString();
		String REFERENCE_ID = "RENEW -"+new Date().getTime(), ERROR_CODE = "NA", ERROR_DESCR = "NA", JOB_RESULT = null;
		Map<String, String> planRenewResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		String insertQuery = "insert into FOS_PLAN_RENEW_MASTER (FOS_AGENT_ID, ACCOUNT_NO, ACCOUNT_OBJ, "
				+ "ACCOUNT_POID, PLAN_OBJ, PLAN_NAME, SERVICE_OBJ, "
				+ "RESULT, REFERENCE_ID, ERROR_CODE, ERROR_DESCR, REQ_PAYLOAD_BLOB, RES_PAYLOAD_BLOB) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(insertQuery);

			Blob REQ_PAYLOAD_BLOB = connection.createBlob();
			byte[] requestPayloadByteContent = renewPlanXMlRequest.getBytes();
			REQ_PAYLOAD_BLOB.setBytes(1, requestPayloadByteContent);

			Blob RES_PAYLOAD_BLOB = connection.createBlob();
			byte[] responsePayloadByteContent = responseString.getBytes();
			RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

			if(brmResponseResult == 200)
			{
				logger.info("Renew Response: ");
				logger.info(responseString);
				if(responseString.contains("Topup/renewal completed"))
				{
					JOB_RESULT = "RENEW SUCCESS";
					planRenewResponseMap.put("RESULT", JOB_RESULT);
					planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}	

				else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
				{
					JSONObject obj = new JSONObject(responseString);
					ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_RENEW_BB_PLAN_outputFlist").optString("ERROR_CODE","404");
					ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_RENEW_BB_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
					JOB_RESULT = "ERROR";
					planRenewResponseMap.put("RESULT", JOB_RESULT);
					planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					planRenewResponseMap.put("ERROR_CODE", ERROR_CODE);
					planRenewResponseMap.put("ERROR_DESCR", ERROR_DESCR);
					result = 403; //OBRM REFUSED
				}
			}
			else if(brmResponseResult == -100)
			{
				JOB_RESULT = "FAILED";
				planRenewResponseMap.put("RESULT", JOB_RESULT);
				planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				planRenewResponseMap.put("ERROR_CODE", "413");
				planRenewResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
				result = 500; 
			}

			preparedStatement.setLong(1, Long.valueOf(agentID));
			preparedStatement.setString(2, extractor.getACCOUNT_NO());
			preparedStatement.setString(3, extractor.getACCOUNT_OBJ());
			preparedStatement.setString(4, extractor.getACCOUNT_POID());
			preparedStatement.setString(5, extractor.getPLAN_OBJ());
			preparedStatement.setString(6, extractor.getPLAN_NAME());
			preparedStatement.setString(7, extractor.getSERVICE_OBJ());
			preparedStatement.setString(8, JOB_RESULT);
			preparedStatement.setString(9, REFERENCE_ID);
			preparedStatement.setString(10, ERROR_CODE);
			preparedStatement.setString(11, ERROR_DESCR);
			preparedStatement.setBlob(12, REQ_PAYLOAD_BLOB);
			preparedStatement.setBlob(13, RES_PAYLOAD_BLOB);
			preparedStatement.executeUpdate();

			String planRenewResponse = gson.toJson(planRenewResponseMap);
			setPlanRenewResponse(planRenewResponse);
			logger.info("Renew Result: "+result);
			logger.info("Renew Response:");
			logger.info(planRenewResponse);

		} catch (ClassNotFoundException | IOException | SQLException | NullPointerException | JSONException e) {
			logger.error("plan Renew Exception: ", e);
			JOB_RESULT = "FAILED";
			planRenewResponseMap.put("RESULT", JOB_RESULT);
			planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
			planRenewResponseMap.put("ERROR_CODE", "500");
			planRenewResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
			String planRenewResponse = gson.toJson(planRenewResponseMap);
			setPlanRenewResponse(planRenewResponse);
			return -100;
		}
		finally {

			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int retryPlanRenewResult(String referenceID, ISPDBConnector ispDBConnector, String agentID)
	{
		boolean flag = true;
		int result = 0;
		Connection connection = null; PreparedStatement preparedStatement = null; ResultSet resultSet = null;
		String planRenewXMlRequest = null, JOB_RESULT = null, FOS_AGENT_ID = agentID, REFERENCE_ID = referenceID, 
				planRenewOpCodeString = "MSO_OP_CUST_RENEW_BB_PLAN", responseString = null, ERROR_CODE = "NA", ERROR_DESCR = "NA";
		Blob REQ_PAYLOAD_BLOB = null;
		String requestPayloadQuery = "SELECT * FROM FOS_PLAN_RENEW_MASTER WHERE REFERENCE_ID = ? AND FOS_AGENT_ID = ? AND RESULT != 'SUCCESS'";
		Map<String, String> planRenewResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		Timestamp TRANSACTION_TIMESTAMP = new Timestamp(new Date().getTime());
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(requestPayloadQuery);
			preparedStatement.setString(1, referenceID);
			preparedStatement.setString(2, agentID);		
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				REQ_PAYLOAD_BLOB = resultSet.getBlob("REQ_PAYLOAD_BLOB");
				planRenewXMlRequest = blobToStringUtil(REQ_PAYLOAD_BLOB);
				//logger.info("Retry TopUp Request XML: ");
				//logger.info(xmlString);
			}	
			else
				flag = false; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			if(flag)
			{
				int brmResponseResult = processBRMRequest(planRenewOpCodeString, planRenewXMlRequest);
				responseString = getObrmResponseString();
				if(brmResponseResult == 200)
				{
					logger.info("Renew Response: ");
					logger.info(responseString);
					if(responseString.contains("Topup/renewal completed"))
					{
						JOB_RESULT = "RENEW SUCCESS";
						planRenewResponseMap.put("RESULT", JOB_RESULT);
						planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					}	

					else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
					{
						JSONObject obj = new JSONObject(responseString);
						ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_RENEW_BB_PLAN_outputFlist").optString("ERROR_CODE","404");
						ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_RENEW_BB_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
						JOB_RESULT = "ERROR";
						planRenewResponseMap.put("RESULT", JOB_RESULT);
						planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
						planRenewResponseMap.put("ERROR_CODE", ERROR_CODE);
						planRenewResponseMap.put("ERROR_DESCR", ERROR_DESCR);
						result = 403; //OBRM REFUSED
					}
				}
				else if(brmResponseResult == -100)
				{
					JOB_RESULT = "FAILED";
					planRenewResponseMap.put("RESULT", JOB_RESULT);
					planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					planRenewResponseMap.put("ERROR_CODE", "413");
					planRenewResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
					result = 500; 
				}
			}
		} 
		catch (Exception e) {
			logger.error("Retry Renew Exception: ", e);
			responseString = e.getMessage();
			JOB_RESULT = "FAILED";
			planRenewResponseMap.put("RESULT", JOB_RESULT);
			planRenewResponseMap.put("ERROR_CODE", "413");
			planRenewResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
			planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closePreparedStatement(preparedStatement);

			if(flag)
			{
				try {

					Blob RES_PAYLOAD_BLOB = connection.createBlob();
					byte[] responsePayloadByteContent = responseString.getBytes();
					RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

					String insertQuery = "UPDATE FOS_PLAN_RENEW_MASTER SET "
							+"RESULT = ?, OBRM_RESPONSE_TIMESTAMP = ?, "
							+ "ERROR_CODE = ?, ERROR_DESCR = ?, REQ_PAYLOAD_BLOB = ?, RES_PAYLOAD_BLOB = ? "
							+ "WHERE FOS_AGENT_ID = ? AND REFERENCE_ID = ? ";
					preparedStatement = connection.prepareStatement(insertQuery);
					preparedStatement.setString(1, JOB_RESULT);
					preparedStatement.setTimestamp(2, TRANSACTION_TIMESTAMP);
					preparedStatement.setString(3, ERROR_CODE);
					preparedStatement.setString(4, ERROR_DESCR);
					preparedStatement.setBlob(5, REQ_PAYLOAD_BLOB);
					preparedStatement.setBlob(6, RES_PAYLOAD_BLOB);
					preparedStatement.setString(7, FOS_AGENT_ID);
					preparedStatement.setString(8, REFERENCE_ID);
					preparedStatement.executeUpdate();

					String retryPlanRenewResponse = gson.toJson(planRenewResponseMap);
					setRetryRenewResponse(retryPlanRenewResponse);
					logger.info("Retry Renew Result: "+result);
					logger.info("Retry Renew Response:");
					logger.info(retryPlanRenewResponse);
				} catch (SQLException | NullPointerException e) {
					logger.error("Retry Renew Exception: ", e);
					JOB_RESULT = "FAILED";
					planRenewResponseMap.put("RESULT", JOB_RESULT);
					planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					planRenewResponseMap.put("ERROR_CODE", "500");
					planRenewResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
					String retryPlanRenewResponse = gson.toJson(planRenewResponseMap);
					setRetryRenewResponse(retryPlanRenewResponse);
					return -100;
				}
				finally
				{
					HouseKeeper.closePreparedStatement(preparedStatement);
					ispDBConnector.closeConnection(connection);
				}
			}
			else
			{
				JOB_RESULT = "BAD REQUEST";
				planRenewResponseMap.put("RESULT", JOB_RESULT);
				planRenewResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				planRenewResponseMap.put("ERROR_CODE", "404");
				planRenewResponseMap.put("ERROR_DESCR", "NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY");
				String retryPlanRenewResponse = gson.toJson(planRenewResponseMap);
				setRetryRenewResponse(retryPlanRenewResponse);
				return 404; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			}
		}
		return result;
	}*/

	public int changePlanResult(ChangePlanExtractor extractor,
			ISPDBConnector ispDBConnector, LoginValidator validator, String agentID, String userID) {
		int result = 200;
		String changePlanOpCodeString = "MSO_OP_CUST_CHANGE_PLAN";
		String changePlanXMlRequest = extractor.getchangePlanXMlRequest(validator, userID);
		int brmResponseResult = processBRMRequest(changePlanOpCodeString, changePlanXMlRequest);
		String responseString = getObrmResponseString();
		logger.info("BRM Response Code: "+brmResponseResult);
		logger.info("BRM Response: "+responseString);
		String REFERENCE_ID = "CHANGE_PLAN-"+new Date().getTime(), ERROR_CODE = "NA", ERROR_DESCR = "NA", JOB_RESULT = "NA";
		Map<String, String> changePlanResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		String insertQuery = "insert into FOS_PLAN_CHANGE_MASTER (FOS_AGENT_ID, ACCOUNT_NO, "
				+ "ACCOUNT_POID, SERVICE_OBJ, RESULT, REFERENCE_ID, ERROR_CODE, ERROR_DESCR, "
				+ "REQ_PAYLOAD_BLOB, RES_PAYLOAD_BLOB, CUSTOMER_NAME) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(insertQuery);

			Blob REQ_PAYLOAD_BLOB = connection.createBlob();
			byte[] requestPayloadByteContent = changePlanXMlRequest.getBytes();
			REQ_PAYLOAD_BLOB.setBytes(1, requestPayloadByteContent);

			Blob RES_PAYLOAD_BLOB = connection.createBlob();
			byte[] responsePayloadByteContent = responseString.getBytes();
			RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

			if(brmResponseResult == 200)
			{
				logger.info("Change Plan Response: ");
				logger.info(responseString);
				if(responseString.contains("ACCOUNT - Service change plan completed successfully"))
				{
					JOB_RESULT = "PLAN CHANGE SUCCESS";
					changePlanResponseMap.put("RESULT", JOB_RESULT);
					changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}	

				else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
				{
					JSONObject obj = new JSONObject(responseString);
					ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_CHANGE_PLAN_outputFlist").optString("ERROR_CODE","404");
					ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_CHANGE_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
					JOB_RESULT = "ERROR";
					changePlanResponseMap.put("RESULT", JOB_RESULT);
					changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					changePlanResponseMap.put("ERROR_CODE", ERROR_CODE);
					changePlanResponseMap.put("ERROR_DESCR", ERROR_DESCR);
					result = 403; //OBRM REFUSED
				}
			}
			else if(brmResponseResult == -100)
			{
				JOB_RESULT = "FAILED";
				changePlanResponseMap.put("RESULT", JOB_RESULT);
				changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				changePlanResponseMap.put("ERROR_CODE", "413");
				changePlanResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
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

			String insertPlansSQL = "insert into FOS_PLAN_CHANGE_DETAIL (REFERNCE_ID, "
					+ "CURR_PLAN_OBJ, CURR_PLAN_FLAGS, CURR_PLAN_DEAL_OBJ, CURR_PLAN_PACKAGE_ID, "
					+ "NEW_PLAN_OBJ, NEW_PLAN_FLAGS, NEW_PLAN_DEAL_OBJ) "
					+ " VALUES (?,?,?,?,?,?,?,?)";
			counter = 0;
			PreparedStatement plansPrepStmt = connection.prepareStatement(insertPlansSQL);
			CUR_PLAN_LISTElement[] cur_PLAN_LISTElements = extractor.getCur_plan_list();
			NEW_PLAN_LISTElement[] new_PLAN_LISTElements = extractor.getNew_plan_list();
			if(cur_PLAN_LISTElements != null){
				for(int i=0; i<cur_PLAN_LISTElements.length; i++)
				{
					CUR_PLAN_LISTElement cur_PLAN_LISTElement = cur_PLAN_LISTElements[i];
					NEW_PLAN_LISTElement new_PLAN_LISTElement = new_PLAN_LISTElements[i];
					com.BRM.CATV.ChangePlan.DEALElement[] curDealElements = cur_PLAN_LISTElement.getDeals();
					//com.BRM.CATV.ChangePlan.DEALElement[] newDealElements = new_PLAN_LISTElement.getDeals();

					if(cur_PLAN_LISTElement != null && new_PLAN_LISTElement != null)
					{
						for(int j=0; j<curDealElements.length; j++)
						{
							com.BRM.CATV.ChangePlan.DEALElement curDealElement = curDealElements[j];
							//com.BRM.CATV.ChangePlan.DEALElement newDealElement = newDealElements[j];
							plansPrepStmt.setString(++counter, REFERENCE_ID);
							plansPrepStmt.setString(++counter, cur_PLAN_LISTElement.getPlan_obj());
							plansPrepStmt.setString(++counter, "0");
							plansPrepStmt.setString(++counter, curDealElement.getDeal_obj());
							plansPrepStmt.setString(++counter, curDealElement.getPackage_id());
							plansPrepStmt.setString(++counter, new_PLAN_LISTElement.getPlan_obj());
							plansPrepStmt.setString(++counter, "1");
							plansPrepStmt.setString(++counter, "NA");
							//plansPrepStmt.setString(++counter, newDealElement.getDeal_obj());
							plansPrepStmt.addBatch();
							counter = 0;
						}
					}
				}
				int[] batchResult = plansPrepStmt.executeBatch();
				logger.info("Batch Result: "+batchResult);
			}

			String planChangeResponse = gson.toJson(changePlanResponseMap);
			setPlanChangeResponse(planChangeResponse);
			logger.info("Plan Change Result: "+result);
			logger.info("Plan Change Response:");
			logger.info(planChangeResponse);

		} catch (SQLException | NullPointerException | JSONException | ClassNotFoundException | IOException e) {
			logger.error("Plan Change Exception: ", e);
			JOB_RESULT = "FAILED";
			changePlanResponseMap.put("RESULT", JOB_RESULT);
			changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
			changePlanResponseMap.put("ERROR_CODE", "500");
			changePlanResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
			String changePlanResponse = gson.toJson(changePlanResponseMap);
			setPlanChangeResponse(changePlanResponse);
			return -100;
		}
		finally {

			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}


	public int cancelPlanResult(CancelPlanExtractor extractor,
			ISPDBConnector ispDBConnector, LoginValidator validator,
			String agentID, String userID) {
		int result = 200;
		String cancelPlanOpCodeString = "MSO_OP_CUST_CANCEL_PLAN";
		String cancelPlanXMlRequest = extractor.getCancelPlanXMlRequest(validator, userID);
		int brmResponseResult = processBRMRequest(cancelPlanOpCodeString, cancelPlanXMlRequest);
		String responseString = getObrmResponseString();
		logger.info("BRM Response Code: "+brmResponseResult);
		logger.info("BRM Response: "+responseString);
		String REFERENCE_ID = "CANCEL_PLAN-"+new Date().getTime(), ERROR_CODE = "NA", ERROR_DESCR = "NA", JOB_RESULT = null;
		Map<String, String> cancelPlanResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		String insertQuery = "insert into FOS_CANCEL_PLAN_MASTER (FOS_AGENT_ID, ACCOUNT_NO, "
				+ "ACCOUNT_POID, SERVICE_OBJ, RESULT, REFERENCE_ID, ERROR_CODE, ERROR_DESCR, "
				+ "REQ_PAYLOAD_BLOB, RES_PAYLOAD_BLOB, CUSTOMER_NAME) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(insertQuery);

			Blob REQ_PAYLOAD_BLOB = connection.createBlob();
			byte[] requestPayloadByteContent = cancelPlanXMlRequest.getBytes();
			REQ_PAYLOAD_BLOB.setBytes(1, requestPayloadByteContent);

			Blob RES_PAYLOAD_BLOB = connection.createBlob();
			byte[] responsePayloadByteContent = responseString.getBytes();
			RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

			if(brmResponseResult == 200)
			{
				logger.info("Cancel Plan Response: ");
				logger.info(responseString);
				if(responseString.contains("ACCOUNT - Service cancel plan completed successfully"))
				{
					JOB_RESULT = "CANCEL PLAN SUCCESS";
					cancelPlanResponseMap.put("RESULT", JOB_RESULT);
					cancelPlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}	

				else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
				{
					JSONObject obj = new JSONObject(responseString);
					ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_CANCEL_PLAN_outputFlist").optString("ERROR_CODE","404");
					ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_CANCEL_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
					JOB_RESULT = "ERROR";
					cancelPlanResponseMap.put("RESULT", JOB_RESULT);
					cancelPlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					cancelPlanResponseMap.put("ERROR_CODE", ERROR_CODE);
					cancelPlanResponseMap.put("ERROR_DESCR", ERROR_DESCR);
					result = 403; //OBRM REFUSED
				}
			}
			else if(brmResponseResult == -100)
			{
				JOB_RESULT = "FAILED";
				cancelPlanResponseMap.put("RESULT", JOB_RESULT);
				cancelPlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				cancelPlanResponseMap.put("ERROR_CODE", "413");
				cancelPlanResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
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

			String insertPlansSQL = "insert into FOS_CANCEL_PLAN_DETAIL (REFERNCE_ID, "
					+ "CANCEL_PLAN_OBJ, CANCEL_PLAN_DEAL_OBJ, CANCEL_PLAN_PACKAGE_ID) "
					+ " VALUES (?,?,?,?)";
			counter = 0;
			PreparedStatement plansPrepStmt = connection.prepareStatement(insertPlansSQL);
			CANCEL_PLAN_LISTElement[] cancel_PLAN_LISTElements = extractor.getCancel_plan_list();
			if(cancel_PLAN_LISTElements != null){
				for(int i=0; i<cancel_PLAN_LISTElements.length; i++)
				{
					CANCEL_PLAN_LISTElement cancel_PLAN_LISTElement = cancel_PLAN_LISTElements[i];
					com.BRM.CATV.CancelPlan.DEALElement[] cancelDealElements = cancel_PLAN_LISTElement.getDeals();
					if(cancel_PLAN_LISTElement != null)
					{
						for(int j=0; j<cancelDealElements.length; j++)
						{
							com.BRM.CATV.CancelPlan.DEALElement curDealElement = cancelDealElements[j];
							plansPrepStmt.setString(++counter, REFERENCE_ID);
							plansPrepStmt.setString(++counter, cancel_PLAN_LISTElement.getPlan_obj());
							plansPrepStmt.setString(++counter, curDealElement.getDeal_obj());
							plansPrepStmt.setString(++counter, curDealElement.getPackage_id());
							plansPrepStmt.addBatch();
							counter = 0;
						}
					}
				}
				int[] batchResult = plansPrepStmt.executeBatch();
				logger.info("Batch Result: "+batchResult);
			}

			String cancelPlanResponse = gson.toJson(cancelPlanResponseMap);
			setCancelPlanResponse(cancelPlanResponse);
			logger.info("Cancel Plan Result: "+result);
			logger.info("Cancel Plan Response:");
			logger.info(cancelPlanResponse);

		} catch (SQLException | NullPointerException | JSONException | ClassNotFoundException | IOException e) {
			logger.error("Cancel Plan Exception: ", e);
			JOB_RESULT = "FAILED";
			cancelPlanResponseMap.put("RESULT", JOB_RESULT);
			cancelPlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
			cancelPlanResponseMap.put("ERROR_CODE", "500");
			cancelPlanResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
			String cancelPlanResponse = gson.toJson(cancelPlanResponseMap);
			setPlanChangeResponse(cancelPlanResponse);
			return -100;
		}
		finally {

			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int retryChangePlanResult(String referenceID, ISPDBConnector ispDBConnector, String agentID)
	{
		boolean flag = true;
		int result = 0;
		Connection connection = null; PreparedStatement preparedStatement = null; ResultSet resultSet = null;
		String planChangeXMlRequest = null, JOB_RESULT = null, FOS_AGENT_ID = agentID, REFERENCE_ID = referenceID, 
				planChangeOpCodeString = "MSO_OP_CUST_CHANGE_PLAN", responseString = null, ERROR_CODE = "NA", ERROR_DESCR = "NA";
		Blob REQ_PAYLOAD_BLOB = null;
		String requestPayloadQuery = "SELECT * FROM FOS_PLAN_CHANGE_MASTER WHERE REFERENCE_ID = ? AND FOS_AGENT_ID = ? AND RESULT != 'SUCCESS'";
		Map<String, String> changePlanResponseMap = new HashMap<String, String>();
		Gson gson = new Gson();
		Timestamp TRANSACTION_TIMESTAMP = new Timestamp(new Date().getTime());
		try {
			connection = ispDBConnector.getConnection();
			preparedStatement = connection.prepareStatement(requestPayloadQuery);
			preparedStatement.setString(1, referenceID);
			preparedStatement.setString(2, agentID);		
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				REQ_PAYLOAD_BLOB = resultSet.getBlob("REQ_PAYLOAD_BLOB");
				planChangeXMlRequest = blobToStringUtil(REQ_PAYLOAD_BLOB);
				//logger.info("Retry Change Plan Request XML: ");
				//logger.info(xmlString);
			}	
			else
				flag = false; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			if(flag)
			{
				int brmResponseResult = processBRMRequest(planChangeOpCodeString, planChangeXMlRequest);
				responseString = getObrmResponseString();
				if(brmResponseResult == 200)
				{
					logger.info("Change Plan Response: ");
					logger.info(responseString);
					if(responseString.contains("Plan changed successfully"))
					{
						JOB_RESULT = "PLAN CHANGE SUCCESS";
						changePlanResponseMap.put("RESULT", JOB_RESULT);
						changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					}	

					else if(responseString.contains("ERROR_CODE") && responseString.contains("ERROR_DESCR"))
					{
						JSONObject obj = new JSONObject(responseString);
						ERROR_CODE = obj.getJSONObject("MSO_OP_CUST_CHANGE_PLAN_outputFlist").optString("ERROR_CODE","404");
						ERROR_DESCR = obj.getJSONObject("MSO_OP_CUST_CHANGE_PLAN_outputFlist").optString("ERROR_DESCR","UNKNOWN_ERROR");
						JOB_RESULT = "ERROR";
						changePlanResponseMap.put("RESULT", JOB_RESULT);
						changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
						changePlanResponseMap.put("ERROR_CODE", ERROR_CODE);
						changePlanResponseMap.put("ERROR_DESCR", ERROR_DESCR);
						result = 403; //OBRM REFUSED
					}
				}
				else if(brmResponseResult == -100)
				{
					JOB_RESULT = "FAILED";
					changePlanResponseMap.put("RESULT", JOB_RESULT);
					changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					changePlanResponseMap.put("ERROR_CODE", "413");
					changePlanResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
					result = 500; 
				}
			}
		} 
		catch (Exception e) {
			logger.error("Retry Plan Change Exception: ", e);
			responseString = e.getMessage();
			JOB_RESULT = "FAILED";
			changePlanResponseMap.put("RESULT", JOB_RESULT);
			changePlanResponseMap.put("ERROR_CODE", "413");
			changePlanResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
			changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closePreparedStatement(preparedStatement);

			if(flag)
			{
				try {
					Blob RES_PAYLOAD_BLOB = connection.createBlob();
					byte[] responsePayloadByteContent = responseString.getBytes();
					RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

					String insertQuery = "UPDATE FOS_PLAN_CHANGE_MASTER SET "
							+"RESULT = ?, OBRM_RESPONSE_TIMESTAMP = ?, "
							+ "ERROR_CODE = ?, ERROR_DESCR = ?, REQ_PAYLOAD_BLOB = ?, RES_PAYLOAD_BLOB = ? "
							+ "WHERE FOS_AGENT_ID = ? AND REFERENCE_ID = ? ";
					preparedStatement = connection.prepareStatement(insertQuery);
					preparedStatement.setString(1, JOB_RESULT);
					preparedStatement.setTimestamp(2, TRANSACTION_TIMESTAMP);
					preparedStatement.setString(3, ERROR_CODE);
					preparedStatement.setString(4, ERROR_DESCR);
					preparedStatement.setBlob(5, REQ_PAYLOAD_BLOB);
					preparedStatement.setBlob(6, RES_PAYLOAD_BLOB);
					preparedStatement.setString(7, FOS_AGENT_ID);
					preparedStatement.setString(8, REFERENCE_ID);
					preparedStatement.executeUpdate();

					String retryPlanChangeResponse = gson.toJson(changePlanResponseMap);
					setRetryPlanChangeResponse(retryPlanChangeResponse);
					logger.info("Retry Plan Change Result: "+result);
					logger.info("Retry Plan Change Response:");
					logger.info(retryPlanChangeResponse);
				} catch (SQLException | NullPointerException e) {
					logger.error("Retry Change Plan Exception: ", e);
					JOB_RESULT = "FAILED";
					changePlanResponseMap.put("RESULT", JOB_RESULT);
					changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					changePlanResponseMap.put("ERROR_CODE", "500");
					changePlanResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
					String retryPlanChangeResponse = gson.toJson(changePlanResponseMap);
					setRetryPlanChangeResponse(retryPlanChangeResponse);
					return -100;
				}
				finally
				{
					HouseKeeper.closePreparedStatement(preparedStatement);
					ispDBConnector.closeConnection(connection);
				}
			}
			else
			{
				JOB_RESULT = "BAD REQUEST";
				changePlanResponseMap.put("RESULT", JOB_RESULT);
				changePlanResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				changePlanResponseMap.put("ERROR_CODE", "404");
				changePlanResponseMap.put("ERROR_DESCR", "NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY");
				String retryPlanChangeResponse = gson.toJson(changePlanResponseMap);
				setRetryPlanChangeResponse(retryPlanChangeResponse);
				return 404; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			}
		}
		return result;
	}

	public int getPlanListResult(String userID, String userType, ISPDBConnector ispDBConnector) {
		int result = 200;
		String planListSQL = "";

		/*if(userType.equals("L"))
			planListSQL = "select pl.ID, pl.PLAN_LIST_NAME, pl.SERVICE_TYPE, pl.CITY "+
					"from oap.oap_plan_list_t@PRODPINDB pl, oap.OAP_USER_PLAN_LIST_MAP_T@PRODPINDB up "+
					"where pl.ID = up.PLAN_LIST_ID "+
					"and up.USER_ID = '"+userID+"'";
		else if(userType.equals("C"))
			planListSQL = "select p.ID, p.PLAN_LIST_NAME, p.SERVICE_TYPE, p.CITY "+
					" from oap.oap_plan_t@PRODPINDB p, oap.oap_plan_list_plan_map_t@PRODPINDB pm  "+
					" where p.id = pm.plan_id and pm.plan_list_id in (select pl.id from oap.oap_plan_list_t@PRODPINDB pl,  "+
					" oap.oap_user_master_t@PRODPINDB up where up.user_id = '"+userID+"' and pl.city = up.city";*/

		if(userType.equals("L"))
			planListSQL = "SELECT PL.ID, PL.PLAN_LIST_NAME, PL.SERVICE_TYPE, PL.CITY "+
					"FROM OAP.OAP_PLAN_LIST_T@PRODPINDB PL, OAP.OAP_USER_PLAN_LIST_MAP_T@PRODPINDB UP "+
					"WHERE PL.ID = UP.PLAN_LIST_ID AND UP.USER_ID = '"+userID+"'";
		else if(userType.equals("C"))
			planListSQL = "SELECT PL.ID,PL.PLAN_LIST_NAME, PL.SERVICE_TYPE, PL.CITY "+ 
					"FROM OAP.OAP_PLAN_LIST_T@PRODPINDB PL, OAP.OAP_USER_MASTER_T@PRODPINDB UP "+ 
					"WHERE UP.USER_ID = '"+userID+"' AND PL.CITY = UP.CITY";

		logger.info(planListSQL);

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;		
		try {
			Map<String, Object> planList = new LinkedHashMap<String, Object>();
			List<Object> PLANLISTS = new ArrayList<Object>();
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(planListSQL);
			while(resultSet.next())
			{
				String planListID = resultSet.getString("ID");
				String planListName = resultSet.getString("PLAN_LIST_NAME");
				logger.info("Plan List ID: "+planListID);
				logger.info("Plan List Name: "+planListName);
				String serviceType = resultSet.getString("SERVICE_TYPE");
				String city = resultSet.getString("CITY");

				Map<String, Object> PLAN = new LinkedHashMap<String, Object>();
				PLAN.put("PLAN_LIST_ID", planListID);
				PLAN.put("PLAN_LIST_NAME", planListName);
				PLAN.put("SERVICE_TYPE", serviceType);
				PLAN.put("CITY", city);

				PLANLISTS.add(PLAN);
			}
			planList.put("PLAN_LIST", PLANLISTS);
			Gson gson = new Gson();
			String planListDetail = gson.toJson(planList);
			setPlanList(planListDetail);
		} catch (Exception e) {
			logger.error("Plan List Result Exception: ", e);
			result = 500;
		}
		finally{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int getPlanListByIDResult(String planListID, ISPDBConnector ispDBConnector) {
		int result = 200;
		Connection connection = null;
		Statement statementPlan = null;
		ResultSet resulSetPlan = null;
		try
		{
			connection = ispDBConnector.getConnection();
			Map<String, Object> planIDList = new LinkedHashMap<String, Object>();
			String planDetailSQL = "SELECT P.ID, P.PLAN_TYPE, P.PLAN_SUBTYPE, P.CODE, P.PLAN_NAME, "+ 
					"P.DESCRIPTION, P.POID, P.SERVICE_TYPE, P.PAYMENT_TYPE, P.PLAN_CATEGORY, P.DEVICE_TYPE "+
					"FROM OAP.OAP_PLAN_T@PRODPINDB P, OAP.OAP_PLAN_LIST_PLAN_MAP_T@PRODPINDB PM "+ 
					"WHERE P.ID = PM.PLAN_ID AND PM.PLAN_LIST_ID = "+planListID;

			logger.info(planDetailSQL);
			List<Object> PLANS = new ArrayList<Object>();
			statementPlan = connection.createStatement();
			resulSetPlan = statementPlan.executeQuery(planDetailSQL);
			while(resulSetPlan.next())
			{
				Map<String, String> PLAN = new LinkedHashMap<String, String>();
				PLAN.put("PLAN_ID", resulSetPlan.getString("ID"));
				PLAN.put("PLAN_TYPE", resulSetPlan.getString("PLAN_TYPE"));
				PLAN.put("PLAN_SUBTYPE", resulSetPlan.getString("PLAN_SUBTYPE"));
				PLAN.put("CODE", resulSetPlan.getString("CODE"));
				PLAN.put("PLAN_NAME", resulSetPlan.getString("PLAN_NAME"));
				PLAN.put("DESCRIPTION", resulSetPlan.getString("DESCRIPTION"));
				PLAN.put("POID", resulSetPlan.getString("POID"));
				PLAN.put("SERVICE_TYPE", resulSetPlan.getString("SERVICE_TYPE"));
				PLAN.put("PAYMENT_TYPE", resulSetPlan.getString("PAYMENT_TYPE"));
				PLAN.put("PLAN_CATEGORY", resulSetPlan.getString("PLAN_CATEGORY"));
				PLAN.put("DEVICE_TYPE", resulSetPlan.getString("DEVICE_TYPE"));
				PLANS.add(PLAN);
			}
			planIDList.put("ID_BASED_PLAN_LIST", PLANS);

			Gson gson = new Gson();
			String planListByIDDetail = gson.toJson(planIDList);
			setPlanByIDList(planListByIDDetail);
		} catch (Exception e) {
			logger.error("Plan List Result Exception: ", e);
			result = 500;
		}
		finally{
			HouseKeeper.closeResultSet(resulSetPlan);
			HouseKeeper.closeStatement(statementPlan);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int getPlanPriceResult(PlanCodesExtractor planCodesBean, ISPDBConnector ispDBConnector) {
		int result = 200;
		List<String> planCodes = planCodesBean.getPlanCodes();
		if(planCodes != null)
		{
			StringBuffer planCodesBuffer = new StringBuffer();
			/*SELECT ROUND(SUM(AMOUNT),2) AS TOTAL,
			 ROUND(SUM(BASE),2) AS PRICE,
			 ROUND(SUM(NCF),2) AS NCF,
			 ROUND(SUM(GST),2) AS GST
			 FROM PLAN_PRICE WHERE PLAN_CODE IN ('PLT_RJ_ADDL_ADV','ALC_ZEE_ETC_MAIN_ARR');*/
			
			planCodesBuffer.append("SELECT ROUND(SUM(AMOUNT),2) AS TOTAL, "+
			 "ROUND(SUM(BASE),2) AS PRICE, "+
			 "ROUND(SUM(NCF),2) AS NCF, "+
			 "ROUND(SUM(GST),2) AS GST "+
			 "FROM PLAN_PRICE WHERE PLAN_CODE IN (");
			for(String planCode : planCodes)
				planCodesBuffer.append("'"+planCode+"',");
			planCodesBuffer.setLength(planCodesBuffer.length() - 1);
			planCodesBuffer.append(")");

			Map<String, String> planPriceMap = new LinkedHashMap<String, String>();
			String planPriceSQL = planCodesBuffer.toString();
			logger.info(planPriceSQL);

			Connection connection = null;
			Statement statement = null;
			ResultSet resultSet = null;		
			try {
				connection = ispDBConnector.getConnection();
				statement = connection.createStatement();
				resultSet = statement.executeQuery(planPriceSQL);
				if(resultSet.next())
				{
					planPriceMap.put("TOTAL", resultSet.getString("TOTAL"));
					planPriceMap.put("PRICE", resultSet.getString("PRICE"));
					planPriceMap.put("NCF", resultSet.getString("NCF"));
					planPriceMap.put("GST", resultSet.getString("GST"));
					setPlanPriceDetail(new Gson().toJson(planPriceMap));
				}
				else
					result = 201;
				
			} catch (Exception e) {
				logger.error("Plan Price Result Exception: ", e);
				result = 500;
			}
			finally{
				HouseKeeper.closeResultSet(resultSet);
				HouseKeeper.closeStatement(statement);
				ispDBConnector.closeConnection(connection);
			}
		}
		else
			result = 400;
		return result;
	}

	@SuppressWarnings("unused")
	private String convertToJSON(ResultSet resultSet)
			throws Exception {
		JSONArray jsonArray = new JSONArray();
		while (resultSet.next()) {
			int total_rows = resultSet.getMetaData().getColumnCount();
			JSONObject obj = new JSONObject();
			for (int i = 0; i < total_rows; i++) {
				String key = resultSet.getMetaData().getColumnLabel(i + 1).toUpperCase();
				String value = resultSet.getString(i + 1);
				if(key.equalsIgnoreCase("PLAN_POID"))
					value = "0.0.0.1 /plan "+value+" 0";
				obj.put(key, value);
			}
			jsonArray.put(obj);
		}
		return jsonArray.toString();
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
