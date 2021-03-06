package com.FOS.Model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;
import com.FOS.Extractor.SLCExtractor;

public class SLCModel {

	final static Logger logger = LogManager.getLogger(SLCModel.class);
	private String slcResponse;
	
	public String getSLCResponse() {
		return slcResponse;
	}

	public void setSLCResponse(String slcResponse) {
		this.slcResponse = slcResponse;
	}

	public int updateSLCRecord(SLCExtractor extractor,
			ISPDBConnector ispDBConnector, String agentID) {
		int result = 200;
		String FOS_AGENT_ID = agentID;
		String ACCOUNT_NO = extractor.getACCOUNT_NO();
		String SLC_MESSAGE = extractor.getSLC_MESSAGE();
		java.sql.Timestamp REPORTED_TIMESTAMP = new java.sql.Timestamp(new Date().getTime());
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			String insertQuery = "insert into FOS_SLC_MASTER (FOS_AGENT_ID,CUST_ACCOUNT_NO,SLC_MESSAGE,REPORTED_TIMESTAMP)"
					+"values (?,?,?,?)";
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setString(1, FOS_AGENT_ID);
			preparedStatement.setString(2, ACCOUNT_NO);
			preparedStatement.setString(3, SLC_MESSAGE);
			preparedStatement.setTimestamp(4, REPORTED_TIMESTAMP);
			preparedStatement.executeUpdate();
		} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
			logger.error("SLC Model: ", e);
			return result = -100;
		}
		finally
		{
			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}
	
	public int updateSLC(SLCExtractor extractor,
			ISPDBConnector ispDBConnector, String agentID) {
		int result = 200;
		JSONObject jsonObject = new JSONObject();
		String FOS_AGENT_ID = agentID;
		String ACCOUNT_NO = extractor.getACCOUNT_NO();
		String SLC_TYPE = extractor.getSLC_TYPE();
		String SLC_MESSAGE = extractor.getSLC_MESSAGE();
		String SLC_REMARKS = extractor.getSLC_REMARKS();
		long timestamp = new Date().getTime();
		String REFERENCE_ID = "SLC-"+timestamp;
		java.sql.Timestamp REPORTED_TIMESTAMP = new java.sql.Timestamp(new Date().getTime());
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ispDBConnector.getConnection();
			String insertQuery = "insert into FOS_SLC_MASTER (FOS_AGENT_ID,CUST_ACCOUNT_NO,SLC_TYPE,SLC_MESSAGE,SLC_REMARKS,REPORTED_TIMESTAMP,REFERENCE_ID)"
					+"values (?,?,?,?,?,?,?)";
			preparedStatement = connection.prepareStatement(insertQuery);
			preparedStatement.setString(1, FOS_AGENT_ID);
			preparedStatement.setString(2, ACCOUNT_NO);
			preparedStatement.setString(3, SLC_TYPE);
			preparedStatement.setString(4, SLC_MESSAGE);
			preparedStatement.setString(5, SLC_REMARKS);
			preparedStatement.setTimestamp(6, REPORTED_TIMESTAMP);
			preparedStatement.setString(7, REFERENCE_ID);
			preparedStatement.executeUpdate();
			jsonObject.put("REFERENCE_ID", REFERENCE_ID);
			jsonObject.put("RESULT", "SLC UPDATED SUCCESSFULLY");
		} catch (SQLException | NullPointerException | JSONException | ClassNotFoundException | IOException e) {
			logger.error("SLC Model: ", e);
			try {
				jsonObject.put("REFERENCE_ID", REFERENCE_ID);
				jsonObject.put("RESULT", "INTERNAL SERVER ERROR");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return result = -100;
		}
		finally
		{
			HouseKeeper.closePreparedStatement(preparedStatement);
			ispDBConnector.closeConnection(connection);
		}
		setSLCResponse(jsonObject.toString());
		return result;
	}
	
}