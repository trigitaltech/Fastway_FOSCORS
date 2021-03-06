package com.FOS.Validator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;
//import com.FOS.Utils.AESUtils;
import com.FOS.Utils.PasswordUtils;

public class LoginValidationImplementor implements LoginValidatorImpl
{
	final static Logger logger = LogManager.getLogger(LoginValidationImplementor.class);

	/*@Override
	public Map<String, Object> validateLogin(ISPDBConnector ispDBConnector, LoginValidator ispLoginValidator) {
		int result = 0;
		Map<String, Object> validateLoginResponse = new HashMap<String, Object>();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try 
		{
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			// Checking whether user and device exists or not
			String userSelect = "select AGENT_ID, AGENT_ACCOUNT_NO, AGENT_ACC_POID_ID0, PAYMENT_LIMIT from FOS_AGENT_MASTER where USER_ID = '"+ispLoginValidator.getUserID()+"' "
					+ "and PASSWORD = '"+ispLoginValidator.getPassword()+"' and DEVICE_IMEI = '"+ispLoginValidator.getDeviceIMEI()+"'";
			logger.info(userSelect);
			resultSet = statement.executeQuery(userSelect);
			if(resultSet.next())
			{
				result = 200; //Success (200)
				validateLoginResponse.put("result", result);
				String agentID = resultSet.getString("AGENT_ID");
				validateLoginResponse.put("agentID", agentID);
				validateLoginResponse.put("agentAccountNo", resultSet.getString("AGENT_ACCOUNT_NO"));
				validateLoginResponse.put("agentAccountPOID", resultSet.getLong("AGENT_ACC_POID_ID0"));
				validateLoginResponse.put("paymentLimit", resultSet.getDouble("PAYMENT_LIMIT"));
				logger.info("Agent ID: "+agentID);
			}	
			else
			{
				result = 401; // (401) Unauthorized (User doesn't exist)
				validateLoginResponse.put("result", result);
			}	
		} catch (ClassNotFoundException | IOException | SQLException e) {
			logger.error("Login validation implementor: ",e);
			result = 500; // Database Issue (Internal Server Error)
			validateLoginResponse.put("result", result);
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		logger.info("Validation Result: "+result);
		return validateLoginResponse;
	}*/


	@Override
	public Map<String, Object> validateLogin(ISPDBConnector ispDBConnector, LoginValidator ispLoginValidator) {
		int result = 0;
		Map<String, Object> validateLoginResponse = new HashMap<String, Object>();
		/*if(new AESUtils().isLicenceValid(ispDBConnector))
		{*/
		PasswordUtils passwordUtils = new PasswordUtils();
		String md5Password = passwordUtils.getEncryptedMD5String(ispLoginValidator.getPassword());

		if(md5Password != null)
		{
			Connection connection = null;
			Statement statement = null;
			ResultSet resultSet = null;
			try 
			{
				connection = ispDBConnector.getConnection();
				statement = connection.createStatement();
				//Checking whether user and device exists or not
				/*String userSelect = "SELECT USER_ID, POID, SUBSTR (POID_ID0, 1,INSTR(POID_ID0,' ') - 1) POID_ID0, MOBILE_NUMBER "
						+ "FROM OAP.OAP_USER_MASTER_T WHERE USER_NAME = '"+ispLoginValidator.getUserID()+"' AND PASSWORD = '"+md5Password+"'";*/

				String lcoSelect = "SELECT A.ACCOUNT_NO, WS.LCO_OBJ_ID0, UM.USER_ID, UM.POID,  "+
						"("+
						"  CASE WHEN UM.POID_ID0 LIKE '% %' "+
						"  THEN SUBSTR (UM.POID_ID0, 1,INSTR(UM.POID_ID0,' ') - 1) "+
						"  ELSE "+
						"  UM.POID_ID0 "+
						"  END "+
						") POID_ID0, UM.MOBILE_NUMBER, DECODE(UPPER(UM.COMPANY_NAME),'COLLECTION','0',1) APP_ACCESS, "+
						"(SELECT COUNT(0) "+
						"FROM "+
						"FOS_BILLING_DAYS_MASTER "+
						"WHERE "+
						"DAY = TO_NUMBER(TO_CHAR(SYSDATE,'DD'))) AS SERVICE_STATUS "+
						"FROM PIN.ACCOUNT_T@PRODPINDB A, PIN.PROFILE_T@PRODPINDB P, PIN.MSO_WHOLESALE_INFO_T@PRODPINDB WS, "+
						"OAP.OAP_USER_MASTER_T@PRODPINDB UM WHERE A.ACCOUNT_NO = UM.USER_ID "+
						"AND A.POID_ID0 = P.ACCOUNT_OBJ_ID0 AND P.POID_ID0 = WS.OBJ_ID0 "+
						"AND UM.USER_NAME = '"+ispLoginValidator.getUserID()+"' AND UM.PASSWORD = '"+md5Password+"' "+ 
						"AND UM.ACTIVE_STATUS <> 1";

				String custSelect = "SELECT A.ACCOUNT_NO, '0' AS LCO_OBJ_ID0, UM.USER_ID, UM.POID,  "+
						"UM.PASSWORD , "+
						"( "+
						"  CASE WHEN UM.POID_ID0 LIKE '% %'  "+
						"  THEN SUBSTR (UM.POID_ID0, 1,INSTR(UM.POID_ID0,' ') - 1)  "+
						"  ELSE  "+
						"  UM.POID_ID0  "+
						"  END  "+
						") POID_ID0, UM.MOBILE_NUMBER, DECODE(UPPER(UM.COMPANY_NAME),'COLLECTION','0',1) APP_ACCESS,  "+
						"(SELECT COUNT(0)  "+
						"FROM  "+
						"FOS_BILLING_DAYS_MASTER  "+
						"WHERE  "+
						"DAY = TO_NUMBER(TO_CHAR(SYSDATE,'DD'))) AS SERVICE_STATUS  "+
						"FROM PIN.ACCOUNT_T@PRODPINDB A, PIN.PROFILE_T@PRODPINDB P,  "+
						"OAP.OAP_USER_MASTER_T@PRODPINDB UM WHERE A.ACCOUNT_NO = UM.USER_ID  "+
						"AND A.POID_ID0 = P.ACCOUNT_OBJ_ID0 "+
						"AND UM.USER_NAME = '"+ispLoginValidator.getUserID()+"'  "+
						"AND UM.PASSWORD = '"+md5Password+"' "+
						"AND UM.ACTIVE_STATUS <> 1";
				if(ispLoginValidator.getUserID().startsWith("CR-"))
				{
					validateLoginResponse.put("USER_TYPE", "C");
					logger.info(custSelect);
					resultSet = statement.executeQuery(custSelect);
				}
				else
				{
					validateLoginResponse.put("USER_TYPE", "L");
					logger.info(lcoSelect);
					resultSet = statement.executeQuery(lcoSelect);
				}
				if(resultSet.next())
				{
					result = 200; //Success (200)
					validateLoginResponse.put("result", result);
					String agentID = resultSet.getString("POID_ID0");
					validateLoginResponse.put("agentID", agentID);
					validateLoginResponse.put("agentAccountNo", resultSet.getString("ACCOUNT_NO"));
					Long lcoObjID = resultSet.getLong("LCO_OBJ_ID0");
					Long poidID = resultSet.getLong("POID_ID0");
					validateLoginResponse.put("agentAccountPOID", lcoObjID != 0 ? lcoObjID : poidID);
					validateLoginResponse.put("paymentLimit", 50000.00);
					validateLoginResponse.put("POID", resultSet.getString("POID"));
					validateLoginResponse.put("MOBILE_NUMBER", resultSet.getString("MOBILE_NUMBER"));
					validateLoginResponse.put("APP_ACCESS", resultSet.getString("APP_ACCESS"));
					validateLoginResponse.put("SERVICE_STATUS", resultSet.getString("SERVICE_STATUS"));
					validateLoginResponse.put("USER_ID", resultSet.getString("USER_ID"));
					logger.info("Agent ID: "+agentID);
				}	
				else
				{
					result = 401; // (401) Unauthorized (User doesn't exist)
					validateLoginResponse.put("result", result);
				}	
			} catch (SQLException | ClassNotFoundException | IOException e) {
				logger.error("Login validation implementor: ",e);
				result = 500; // Database Issue (Internal Server Error)
				validateLoginResponse.put("result", result);
			}
			finally
			{
				HouseKeeper.closeResultSet(resultSet);
				HouseKeeper.closeStatement(statement);
				ispDBConnector.closeConnection(connection);
			}
			logger.info("Validation Result: "+result);
			return validateLoginResponse;
		}	
		else
		{
			result = 400; //Bad Request
			logger.info("Result: "+result);
			validateLoginResponse.put("result", result);
			return validateLoginResponse;
		}

		/*}
		else
		{
			result = 498; //License Expired
			logger.info("Result: "+result);
			validateLoginResponse.put("result", result);
			return validateLoginResponse;
		}*/
	}
}
