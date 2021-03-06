package com.FOS.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;

import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.Opcode;
import _36._0._21._172.infranetwebsvc.services.brmbaseservices.InfranetWebServiceServiceStub.OpcodeResponse;

import com.BRM.CATV.Payment.CATVPaymentMaster;
import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;
import com.FOS.Extractor.MakePaymentExtractor;
import com.FOS.Validator.LoginValidator;
import com.google.gson.Gson;

public class MakePaymentModel {

	final static Logger logger = LogManager.getLogger(MakePaymentModel.class);
	private String makePaymentResponse;
	private String retryPaymentResponse;

	public String getRetryPaymentResponse() {
		return retryPaymentResponse;
	}

	public void setRetryPaymentResponse(String retryPaymentResponse) {
		this.retryPaymentResponse = retryPaymentResponse;
	}

	public String getMakePaymentResponse() {
		return makePaymentResponse;
	}

	public void setMakePaymentResponse(String makePaymentResponse) {
		this.makePaymentResponse = makePaymentResponse;
	}

	public int makePaymentResult(MakePaymentExtractor extractor, ISPDBConnector ispDBConnector, LoginValidator validator, String agentID, String userID)
	{
		synchronized (this) {
			int result = 0;
			boolean flag = true;
			String FOS_AGENT_ID = agentID;
			String ACCOUNT_NO = extractor.getACCOUNT_NO();
			String ACCOUNT_OBJ = extractor.getACCOUNT_OBJ();
			String ACCOUNT_POID = extractor.getACCOUNT_POID();
			String CUSTOMER_NAME = extractor.getCUSTOMER_NAME();
			String MOBILE_NO = extractor.getMOBILE_NO();
			String EMAIL_ID = extractor.getEMAIL_ID();
			String ADDRESS = extractor.getADDRESS();
			String PACKAGE = extractor.getPLAN_NAME();
			String PREVIOUS_OUTSTANDING = extractor.getPREVIOUS_OUTSTANDING();
			String LAST_INVOICE_AMOUNT = extractor.getLAST_INVOICE();
			String DUE_AMOUNT = extractor.getCURRENT_BALANCE_INC_PENDING_CHARGES();
			String DUE_DATE = extractor.getDUE_DATE();
			String PAYMENT_MODE = extractor.getPAY_TYPE();
			String PAID_AMOUNT = extractor.getPAID_AMOUNT();
			String CHEQUE_NO = extractor.getCHEQUE_NUMBER();
			String CHEQUE_DATE = extractor.getCHEQUE_DATE();
			String CASH_DATE = extractor.getCASH_DATE();
			String BANK_CODE = extractor.getBANK_CODE();
			String BANK_NAME = extractor.getBANK_NAME();
			String REMARKS = extractor.getREMARKS();
			String MSO_FLD_AGREEMENT_NO = extractor.getMSO_FLD_AGREEMENT_NO();
			String SERVICE_OBJ = extractor.getSERVICE_OBJ();
			if(REMARKS == null)
				//REMARKS = PAYMENT_MODE+" PAYMENT";
				REMARKS = "-";
			String ALTERNATE_MOBILE_NO = "-", ALTERNATE_EMAIL_ID ="-";
			ALTERNATE_MOBILE_NO = extractor.getALTERNATE_MOBILE_NO(); ALTERNATE_EMAIL_ID = extractor.getALTERNATE_EMAIL_ID();
			String T_TYPE = "NA", T_ID = "NA", T_STATUS = "NA", T_ORIG_AMOUNT = "NA", T_FIXED_AMOUNT = "NA", T_TAXED_AMOUNT = "NA", T_TOTAL_AMOUNT = "NA", T_PAYABLE_AMOUNT = "NA";
			T_TYPE = extractor.getT_TYPE();		T_ID = extractor.getT_ID();		T_STATUS = extractor.getT_STATUS();
			T_ORIG_AMOUNT = extractor.getT_ORIG_AMOUNT(); T_FIXED_AMOUNT = extractor.getT_FIXED_AMOUNT(); T_TAXED_AMOUNT = extractor.getT_TAXED_AMOUNT();
			T_TOTAL_AMOUNT = extractor.getT_TOTAL_AMOUNT(); T_PAYABLE_AMOUNT = extractor.getT_PAYABLE_AMOUNT();
			String TRANSACTION_STATUS = "FAILED";
			String OBRM_RECEIPT_NO = "NA";
			String LATITUDE = extractor.getLATITUDE(), LONGITUDE = extractor.getLONGITUDE();
			if(LATITUDE == null)	LATITUDE = "NA";
			if(LONGITUDE == null)	LONGITUDE = "NA";
			String xmlString = null;
			if(PAYMENT_MODE.equalsIgnoreCase("CASH"))
				xmlString = extractor.getCashXMLString(validator, userID);
			else if(PAYMENT_MODE.equalsIgnoreCase("CHEQUE"))
				xmlString = extractor.getChequeXMLString(validator, userID);
			else if(PAYMENT_MODE.equalsIgnoreCase("CARD") || PAYMENT_MODE.equalsIgnoreCase("CREDIT") || PAYMENT_MODE.equalsIgnoreCase("DEBIT"))
				xmlString = extractor.getCardXMLString(validator, userID);
			else
				flag = false;
			String REFERENCE_ID = extractor.getUniqueTransactionID();
			String opCodeString = "MSO_OP_PYMT_COLLECT", OBRM_RESPONSE = "NA", ERROR_CODE = "NA", ERROR_DESCR = "NA";
			String jsonString = null;
			Map<String, String> makePaymentResponseMap = new HashMap<String, String>();
			Gson gson = new Gson();
			Timestamp TRANSACTION_TIMESTAMP = new Timestamp(new Date().getTime());

			Connection connection = null;

			Blob requestPayloadBlob = null, responsePayloadBlob = null;
			byte[] requestPayloadByteContent = null, responsePayloadByteContent = null;

			PreparedStatement preparedStatement = null;
			try {
				if(flag)
				{	
					connection = ispDBConnector.getConnection();
					requestPayloadBlob = connection.createBlob();
					requestPayloadBlob.setBytes(1, requestPayloadByteContent);

					responsePayloadBlob = connection.createBlob();
					responsePayloadBlob.setBytes(1, responsePayloadByteContent);

					String insertQuery = "insert into FOS_PAYMENT_TRANS_MASTER (FOS_AGENT_ID, ACCOUNT_NO, ACCOUNT_OBJ, "
							+ "ACCOUNT_POID, CUSTOMER_NAME, MOBILE_NO, EMAIL_ID, ADDRESS, PACKAGE, PREVIOUS_OUTSTANDING, LAST_INVOICE_AMOUNT, "
							+ "DUE_AMOUNT, DUE_DATE, PAYMENT_MODE, PAID_AMOUNT, CHEQUE_NO, CHEQUE_DATE, BANK_CODE, BANK_NAME, REMARKS, TRANSACTION_STATUS, "
							+ "TRANSACTION_TIMESTAMP, OBRM_RECEIPT_NO, REFERENCE_ID, CASH_DATE, ERROR_CODE, ERROR_DESCR, REQ_PAYLOAD_BLOB, RES_PAYLOAD_BLOB, "
							+ "T_TYPE,T_ID,T_STATUS, T_ORIG_AMOUNT, T_FIXED_AMOUNT, T_TAXED_AMOUNT, T_TOTAL_AMOUNT, T_PAYABLE_AMOUNT, ALTERNATE_MOBILE_NO, "
							+ "ALTERNATE_EMAIL_ID, CARD_TRANSACTION_ID, LATITUDE, LONGITUDE, MSO_FLD_AGREEMENT_NO, SERVICE_OBJ) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					preparedStatement = connection.prepareStatement(insertQuery);
					preparedStatement.setLong(1, Long.valueOf(FOS_AGENT_ID));
					preparedStatement.setString(2, ACCOUNT_NO);
					preparedStatement.setString(3, ACCOUNT_OBJ);
					preparedStatement.setString(4, ACCOUNT_POID);
					preparedStatement.setString(5, CUSTOMER_NAME);
					preparedStatement.setString(6, MOBILE_NO);
					preparedStatement.setString(7, EMAIL_ID);
					preparedStatement.setString(8, ADDRESS);
					preparedStatement.setString(9, PACKAGE);
					preparedStatement.setString(10, PREVIOUS_OUTSTANDING);
					preparedStatement.setString(11, LAST_INVOICE_AMOUNT);
					preparedStatement.setString(12, DUE_AMOUNT);
					preparedStatement.setString(13, DUE_DATE);
					preparedStatement.setString(14, PAYMENT_MODE);
					preparedStatement.setString(15, PAID_AMOUNT);
					preparedStatement.setString(16, CHEQUE_NO);
					preparedStatement.setString(17, CHEQUE_DATE);
					preparedStatement.setString(18, BANK_CODE);
					if(BANK_NAME != null)
					{
						BANK_NAME = URLDecoder.decode(BANK_NAME,"UTF-8");
						logger.info("Bank Name: "+BANK_NAME);
					}
					preparedStatement.setString(19, BANK_NAME);
					preparedStatement.setString(20, REMARKS);
					preparedStatement.setString(21, TRANSACTION_STATUS);
					preparedStatement.setTimestamp(22, TRANSACTION_TIMESTAMP);
					preparedStatement.setString(23, OBRM_RECEIPT_NO);
					preparedStatement.setString(24, REFERENCE_ID);
					preparedStatement.setString(25, CASH_DATE);
					preparedStatement.setString(26, ERROR_CODE);
					preparedStatement.setString(27, ERROR_DESCR);
					preparedStatement.setBlob(28, requestPayloadBlob);
					preparedStatement.setBlob(29, responsePayloadBlob);
					preparedStatement.setString(30, T_TYPE);
					preparedStatement.setString(31, T_ID);
					preparedStatement.setString(32, T_STATUS);
					preparedStatement.setString(33, T_ORIG_AMOUNT);
					preparedStatement.setString(34, T_FIXED_AMOUNT);
					preparedStatement.setString(35, T_TAXED_AMOUNT);
					preparedStatement.setString(36, T_TOTAL_AMOUNT);
					preparedStatement.setString(37, T_PAYABLE_AMOUNT);
					preparedStatement.setString(38, ALTERNATE_MOBILE_NO);
					preparedStatement.setString(39, ALTERNATE_EMAIL_ID);
					if(PAYMENT_MODE.equalsIgnoreCase("CARD") || PAYMENT_MODE.equalsIgnoreCase("CREDIT") || PAYMENT_MODE.equalsIgnoreCase("DEBIT"))
						preparedStatement.setString(40, extractor.getCARD_TRANSACTION_ID());
					else
						preparedStatement.setString(40, "NA");
					preparedStatement.setString(41, LATITUDE);
					preparedStatement.setString(42, LONGITUDE);
					preparedStatement.setString(43, MSO_FLD_AGREEMENT_NO);
					preparedStatement.setString(44, SERVICE_OBJ);
					preparedStatement.executeUpdate();

					InfranetWebServiceServiceStub stub = new InfranetWebServiceServiceStub();
					Opcode opcode = new Opcode();
					opcode.setOpcode(opCodeString);
					opcode.setInputXML(xmlString);
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
					OBRM_RESPONSE = jsonString;
					logger.info("JSON Response:");
					logger.info(OBRM_RESPONSE);
					if(jsonString.contains("ERROR_CODE"))
					{
						TRANSACTION_STATUS = "ERROR";
						OBRM_RECEIPT_NO = "NA";
						JSONObject jsonObject = new JSONObject(jsonString);
						ERROR_CODE = jsonObject.getJSONObject("MSO_OP_PYMT_COLLECT_outputFlist").optString("ERROR_CODE", "408");
						ERROR_DESCR = jsonObject.getJSONObject("MSO_OP_PYMT_COLLECT_outputFlist").optString("ERROR_DESCR", "NO_ERROR");
						makePaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
						makePaymentResponseMap.put("ERROR_CODE", ERROR_CODE);
						makePaymentResponseMap.put("ERROR_DESC", ERROR_DESCR);
						makePaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					}	
					else
					{
						TRANSACTION_STATUS = "SUCCESS";
						CATVPaymentMaster catvPaymentMaster = (CATVPaymentMaster)gson.fromJson(jsonString, CATVPaymentMaster.class);
						OBRM_RECEIPT_NO = catvPaymentMaster.getMso_op_pymt_collect_outputflist().getReceipt_no();
						REFERENCE_ID = catvPaymentMaster.getMso_op_pymt_collect_outputflist().getReference_id();
						makePaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
						makePaymentResponseMap.put("OBRM_RECEIPT_NO", OBRM_RECEIPT_NO);
						makePaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					}	

				}
			} catch (Exception e) {
				logger.error("Make Payment: ",e);
				OBRM_RESPONSE = e.getMessage();
				TRANSACTION_STATUS = "FAILED";
				OBRM_RECEIPT_NO = "NA";
				makePaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
				makePaymentResponseMap.put("ERROR_CODE", "413");
				makePaymentResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
				makePaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
			}
			finally
			{
				if(flag)
				{
					try {
						HouseKeeper.closePreparedStatement(preparedStatement);
						requestPayloadBlob = connection.createBlob();
						requestPayloadByteContent = xmlString.getBytes();
						requestPayloadBlob.setBytes(1, requestPayloadByteContent);

						responsePayloadBlob = connection.createBlob();
						responsePayloadByteContent = OBRM_RESPONSE.getBytes();
						responsePayloadBlob.setBytes(1, responsePayloadByteContent);

						String insertQuery = "UPDATE FOS_PAYMENT_TRANS_MASTER SET "
								+"TRANSACTION_STATUS = ?, TRANSACTION_TIMESTAMP = ?, OBRM_RECEIPT_NO = ?, "
								+ "ERROR_CODE = ?, ERROR_DESCR = ?, REQ_PAYLOAD_BLOB = ?, RES_PAYLOAD_BLOB = ? "
								+ "WHERE FOS_AGENT_ID = ? AND REFERENCE_ID = ? ";

						preparedStatement = connection.prepareStatement(insertQuery);
						preparedStatement.setString(1, TRANSACTION_STATUS);
						preparedStatement.setTimestamp(2, TRANSACTION_TIMESTAMP);
						preparedStatement.setString(3, OBRM_RECEIPT_NO);
						preparedStatement.setString(4, ERROR_CODE);
						preparedStatement.setString(5, ERROR_DESCR);
						preparedStatement.setBlob(6, requestPayloadBlob);
						preparedStatement.setBlob(7, responsePayloadBlob);
						preparedStatement.setString(8, FOS_AGENT_ID);
						preparedStatement.setString(9, REFERENCE_ID);
						preparedStatement.executeUpdate();

						String paymentResponse = gson.toJson(makePaymentResponseMap);
						setMakePaymentResponse(paymentResponse);
						if(TRANSACTION_STATUS.equals("SUCCESS"))
							result = 200;
						else if(TRANSACTION_STATUS.equals("ERROR"))
							result = 403; //OBRM REFUSED
						else if(TRANSACTION_STATUS.equals("FAILED"))
							result = 500;
						logger.info("Payment Result: "+result);
						logger.info("Payment Response:");
						logger.info(paymentResponse);

						/*if(result == 200)
						{
							String smsMobileNo = null;
							if(ALTERNATE_MOBILE_NO != null && !ALTERNATE_MOBILE_NO.isEmpty() && ALTERNATE_MOBILE_NO.length() == 10)
								smsMobileNo = ALTERNATE_MOBILE_NO;
							else
								smsMobileNo = MOBILE_NO;
							SMSUtils smsUtils = new SMSUtils();
							smsUtils.sendSMS(ACCOUNT_NO, PAID_AMOUNT, PAYMENT_MODE, smsMobileNo);
						}*/

					} catch (SQLException | NullPointerException e) {
						// TODO Auto-generated catch block
						logger.error("Make Payment: ",e);
						TRANSACTION_STATUS = "FAILED";
						makePaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
						makePaymentResponseMap.put("ERROR_CODE", "500");
						makePaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
						makePaymentResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
						String paymentResponse = gson.toJson(makePaymentResponseMap);
						setMakePaymentResponse(paymentResponse);
						return -100;
					}
					finally{
						HouseKeeper.closePreparedStatement(preparedStatement);
						ispDBConnector.closeConnection(connection);
					}
				}
				else
				{
					TRANSACTION_STATUS = "BAD REQUEST";
					makePaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
					makePaymentResponseMap.put("ERROR_CODE", "404");
					makePaymentResponseMap.put("ERROR_DESCR", "BAD PAYMENT OPTION");
					makePaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					String paymentResponse = gson.toJson(makePaymentResponseMap);
					setMakePaymentResponse(paymentResponse);
					return 404; // BAD PAYMENT OPTION
				}
			}
			return result;
		}
	}

	public int retryPaymentResult(String referenceID, ISPDBConnector ispDBConnector, String agentID)
	{
		boolean flag = true;
		int result = 0;
		Connection connection = null; PreparedStatement preparedStatement = null; ResultSet resultSet = null;
		String xmlString = null, TRANSACTION_STATUS = null, OBRM_RECEIPT_NO = null, FOS_AGENT_ID = agentID, REFERENCE_ID = referenceID, 
				opCodeString = "MSO_OP_PYMT_COLLECT", OBRM_RESPONSE = null, ERROR_CODE = "NA", ERROR_DESCR = "NA", jsonString = null;
		Blob REQ_PAYLOAD_BLOB = null;
		String requestPayloadQuery = "SELECT REQ_PAYLOAD_BLOB FROM FOS_PAYMENT_TRANS_MASTER WHERE REFERENCE_ID = ? AND FOS_AGENT_ID = ? AND OBRM_RECEIPT_NO = 'NA'";
		Map<String, String> retryPaymentResponseMap = new HashMap<String, String>();
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
				xmlString = blobToStringUtil(REQ_PAYLOAD_BLOB);
				logger.info("Retry Payment Request XML: ");
				logger.info(xmlString);
			}	
			else
				flag = false; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			if(flag)
			{
				InfranetWebServiceServiceStub stub = new InfranetWebServiceServiceStub();
				Opcode opcode = new Opcode();
				opcode.setOpcode(opCodeString);
				opcode.setInputXML(xmlString);
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
				OBRM_RESPONSE = jsonString;
				logger.info("JSON Response:");
				logger.info(OBRM_RESPONSE);
				if(jsonString.contains("ERROR_CODE"))
				{
					TRANSACTION_STATUS = "ERROR";
					OBRM_RECEIPT_NO = "NA";
					JSONObject jsonObject = new JSONObject(jsonString);
					ERROR_CODE = jsonObject.getJSONObject("MSO_OP_PYMT_COLLECT_outputFlist").optString("ERROR_CODE", "408");
					ERROR_DESCR = jsonObject.getJSONObject("MSO_OP_PYMT_COLLECT_outputFlist").optString("ERROR_DESCR", "NO_ERROR");
					retryPaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
					retryPaymentResponseMap.put("ERROR_CODE", ERROR_CODE);
					retryPaymentResponseMap.put("ERROR_DESC", ERROR_DESCR);
					retryPaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}	
				else
				{
					TRANSACTION_STATUS = "SUCCESS";
					CATVPaymentMaster catvPaymentMaster = (CATVPaymentMaster)gson.fromJson(jsonString, CATVPaymentMaster.class);
					OBRM_RECEIPT_NO = catvPaymentMaster.getMso_op_pymt_collect_outputflist().getReceipt_no();
					REFERENCE_ID = catvPaymentMaster.getMso_op_pymt_collect_outputflist().getReference_id();
					retryPaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
					retryPaymentResponseMap.put("OBRM_RECEIPT_NO", OBRM_RECEIPT_NO);
					retryPaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				}
			}
		} 
		catch (Exception e) {
			logger.error("Retry Payment: ",e);
			OBRM_RESPONSE = e.getMessage();
			logger.info("");
			TRANSACTION_STATUS = "FAILED";
			OBRM_RECEIPT_NO = "NA";
			retryPaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
			retryPaymentResponseMap.put("ERROR_CODE", "413");
			retryPaymentResponseMap.put("ERROR_DESCR", "PLEASE TRY AGAIN AFTER SOMETIME");
			retryPaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
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
					byte[] responsePayloadByteContent = OBRM_RESPONSE.getBytes();
					RES_PAYLOAD_BLOB.setBytes(1, responsePayloadByteContent);

					String insertQuery = "UPDATE FOS_PAYMENT_TRANS_MASTER SET "
							+"TRANSACTION_STATUS = ?, TRANSACTION_TIMESTAMP = ?, OBRM_RECEIPT_NO = ?, "
							+ "ERROR_CODE = ?, ERROR_DESCR = ?, REQ_PAYLOAD_BLOB = ?, RES_PAYLOAD_BLOB = ? "
							+ "WHERE FOS_AGENT_ID = ? AND REFERENCE_ID = ? ";
					preparedStatement = connection.prepareStatement(insertQuery);
					preparedStatement.setString(1, TRANSACTION_STATUS);
					preparedStatement.setTimestamp(2, TRANSACTION_TIMESTAMP);
					preparedStatement.setString(3, OBRM_RECEIPT_NO);
					preparedStatement.setString(4, ERROR_CODE);
					preparedStatement.setString(5, ERROR_DESCR);
					preparedStatement.setBlob(6, REQ_PAYLOAD_BLOB);
					preparedStatement.setBlob(7, RES_PAYLOAD_BLOB);
					preparedStatement.setString(8, FOS_AGENT_ID);
					preparedStatement.setString(9, REFERENCE_ID);
					preparedStatement.executeUpdate();

					String retryPaymentResponse = gson.toJson(retryPaymentResponseMap);
					setRetryPaymentResponse(retryPaymentResponse);
					if(TRANSACTION_STATUS.equals("SUCCESS"))
						result = 200;
					else if(TRANSACTION_STATUS.equals("ERROR"))
						result = 403; //OBRM REFUSED
					else if(TRANSACTION_STATUS.equals("FAILED"))
						result = 500;
					logger.info("Retry Payment Result: "+result);
					logger.info("Retry Payment Response:");
					logger.info(retryPaymentResponse);

				} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					logger.error("Retry Payment",e);
					TRANSACTION_STATUS = "FAILED";
					retryPaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
					retryPaymentResponseMap.put("ERROR_CODE", "500");
					retryPaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
					retryPaymentResponseMap.put("ERROR_DESCR", "INTERNAL SERVER ERROR");
					String retryPaymentResponse = gson.toJson(retryPaymentResponseMap);
					setRetryPaymentResponse(retryPaymentResponse);
					return -100;
				}
				finally{
					HouseKeeper.closePreparedStatement(preparedStatement);
					ispDBConnector.closeConnection(connection);
				}
			}
			else
			{
				TRANSACTION_STATUS = "BAD REQUEST";
				retryPaymentResponseMap.put("RESULT", TRANSACTION_STATUS);
				retryPaymentResponseMap.put("ERROR_CODE", "404");
				retryPaymentResponseMap.put("ERROR_DESCR", "NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY");
				retryPaymentResponseMap.put("REFERENCE_ID", REFERENCE_ID);
				String retryPaymentResponse = gson.toJson(retryPaymentResponseMap);
				setRetryPaymentResponse(retryPaymentResponse);
				return 404; // NO_ENTRIES_FOUND_FOR_THIS_USER / UNAUTHORIZED TO RETRY
			}	
		}
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
			logger.error("Blob to String: ",e);
		}
		return strOut.toString();
	}
}
