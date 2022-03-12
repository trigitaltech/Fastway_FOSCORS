package com.FOS.Model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;
import com.FOS.Extractor.CreateTicketExtractor;
import com.FOS.Validator.LoginValidator;
import com.google.gson.Gson;

public class CreateTicketModel {

	final static Logger logger = LogManager.getLogger(CreateTicketModel.class);
	private String createTicketResponse;
	private String ViewTicketsResponse;


	public String getViewTicketsResponse() {
		return ViewTicketsResponse;
	}
	public void setViewTicketsResponse(String ViewTicketsResponse) {
		this.ViewTicketsResponse = ViewTicketsResponse;
	}

	public String getCreateTicketResponse() {
		return createTicketResponse;
	}

	public void setCreateTicketResponse(String createTicketResponse) {
		this.createTicketResponse = createTicketResponse;
	}

	public int createTicketResult(CreateTicketExtractor extractor, ISPDBConnector ispDBConnector, LoginValidator validator, String agentID, String userID)
	{
		synchronized (this) {
			int counter = 0;
			int result = 200;
			String CUST_ACCOUNT_NO = extractor.getCUST_ACCOUNT_NO();
			String NOTES_TYPE = extractor.getNOTES_TYPE();
			String CUSTOMER_NOTES = extractor.getCUSTOMER_NOTES();
			
			/*String CREATED_ON = extractor.getCREATED_ON();
			String RELEVANT_TICKET_ID = extractor.getRELEVANT_TICKET_ID();
			String TICKET_ID_ADDED_ON = extractor.getTICKET_ID_ADDED_ON();
			String STAFF_USER_ID = extractor.getSTAFF_USER_ID();*/

			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try {
				connection = ispDBConnector.getConnection();
				String insertQuery = "INSERT INTO CUSTOMER_NOTES_SELFCARE"
						+ "(CUST_ACCOUNT_NO,NOTES_TYPE,CUSTOMER_NOTES,CREATED_ON)"
						+ " VALUES(?,?,?,SYSTIMESTAMP)";
						/*+ "CREATED_ON, RELEVANT_TICKET_ID, TICKET_ID_ADDED_ON, STAFF_USER_ID) "
						+ "values (?,?,?,?,?,?,?)";*/
				preparedStatement = connection.prepareStatement(insertQuery);
				preparedStatement.setString(++counter, CUST_ACCOUNT_NO);
				preparedStatement.setString(++counter, NOTES_TYPE);
				preparedStatement.setString(++counter, CUSTOMER_NOTES);
			  /*preparedStatement.setString(++counter, CREATED_ON);
				preparedStatement.setString(++counter, RELEVANT_TICKET_ID);
				preparedStatement.setString(++counter, TICKET_ID_ADDED_ON);
				preparedStatement.setString(++counter, STAFF_USER_ID);*/
				preparedStatement.executeUpdate();	
			} catch (Exception e) {
				logger.error("Create Ticket: ",e);
				result = -100;
			}
			return result;
		}
	}

	public int viewTicketsResult(ISPDBConnector ispDBConnector, String accountNo) {
		int result = 200;
		List<Map<String, String>> viewticketsResult = null;
		Gson gson = new Gson();
		String sql = "SELECT CUST_ACCOUNT_NO,NOTES_TYPE,CUSTOMER_NOTES,CREATED_ON,RELEVANT_TICKET_ID,"
				+ "TICKET_ID_ADDED_ON,STAFF_USER_ID FROM "
				+ "CUSTOMER_NOTES_SELFCARE WHERE CUST_ACCOUNT_NO = '"+accountNo.trim()+"'";
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			viewticketsResult = convertToMapList(resultSet);
			String ViewTicketsResponse = gson.toJson(viewticketsResult);
			logger.info("Total Tickets Response: "+ViewTicketsResponse);
			setViewTicketsResponse(ViewTicketsResponse);

		} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
			logger.error(e);
			result = -100;
		}
		finally {
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	private List<Map<String, String>> convertToMapList(ResultSet resultSet) throws SQLException {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map = null;
		while (resultSet.next()) {
			map = new HashMap<String, String>();
			int total_rows = resultSet.getMetaData().getColumnCount();
			for (int i = 0; i < total_rows; i++) {
				String key = resultSet.getMetaData().getColumnLabel(i + 1).toUpperCase();
				String value = resultSet.getString(i + 1);
				map.put(key, value);
			}
			list.add(map);
		}
		return list;
	}
}

