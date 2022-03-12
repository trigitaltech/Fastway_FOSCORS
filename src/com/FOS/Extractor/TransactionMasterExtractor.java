package com.FOS.Extractor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.FOS.DBConnector.HouseKeeper;
import com.FOS.DBConnector.ISPDBConnector;

public class TransactionMasterExtractor 
{
	final static Logger logger = LogManager.getLogger(TransactionMasterExtractor.class);
	private List<Map<String, String>> agentTransactionsList;
	private List<Map<String, String>> userTransactionsList;
	private List<Map<String, String>> terminalReportList;

	public List<Map<String, String>> getAgentTransactionsList() {
		return agentTransactionsList;
	}

	public void setAgentTransactionsList(
			List<Map<String, String>> agentTransactionsList) {
		this.agentTransactionsList = agentTransactionsList;
	}

	public List<Map<String, String>> getUserTransactionsList() {
		return userTransactionsList;
	}

	public void setUserTransactionsList(
			List<Map<String, String>> userTransactionsList) {
		this.userTransactionsList = userTransactionsList;
	}

	public List<Map<String, String>> getTerminalReportList() {
		return terminalReportList;
	}

	public void setTerminalReportList(List<Map<String, String>> terminalReportList) {
		this.terminalReportList = terminalReportList;
	}
	public int processAgentTransactionsList(String agentID, ISPDBConnector ispDBConnector)
	{
		int result = 200;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
		String agentTransListQuery = "select PACKAGE, CUSTOMER_NAME, ADDRESS, DUE_AMOUNT, REMARKS, PAYMENT_MODE, CASH_DATE, PREVIOUS_OUTSTANDING, LAST_INVOICE_AMOUNT, "
				+ "PAID_AMOUNT, EMAIL_ID, ACCOUNT_NO, DUE_DATE, TRANSACTION_STATUS, OBRM_RECEIPT_NO, REFERENCE_ID, MOBILE_NO, TRANSACTION_TIMESTAMP,  "
				+ "CHEQUE_NO, CHEQUE_DATE, BANK_CODE, BANK_NAME, REFERENCE_ID, REMARKS, CARD_TRANSACTION_ID from (select * from FOS_PAYMENT_TRANS_MASTER "
				+ "where FOS_AGENT_ID = '"+agentID+"' "
				+ "order by TRANSACTION_TIMESTAMP desc) "
				+ "where rownum < 6";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(agentTransListQuery);
			ResultSetMetaData meta = resultSet.getMetaData() ; 
			int numberOfColumns = meta.getColumnCount() ;
			List<Map<String, String>> agentTransactionList = new ArrayList<Map<String, String>>();
			Map<String, String> agentTransactionMap = null;
			while(resultSet.next())
			{
				agentTransactionMap = new HashMap<String, String>();
				for (int i = 1 ; i <= numberOfColumns; i ++ ) {
					String columnName = meta.getColumnName(i);
					String columnValue = resultSet.getString(columnName);
					switch (columnName) {
					case "CASH_DATE": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "CHEQUE_NO": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "CHEQUE_DATE": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "BANK_CODE": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "BANK_NAME": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "TRANSACTION_TIMESTAMP": Date transactionTimestamp = resultSet.getTimestamp(columnName);  
						  if(transactionTimestamp == null)
							  columnValue = "-";
						  else
							  columnValue = dateFormat.format(transactionTimestamp);
						  	break;
					case "CARD_TRANSACTION_ID": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					default:
						break;
					}
					agentTransactionMap.put(columnName, columnValue);
				}
				agentTransactionList.add(agentTransactionMap);
			}
			setAgentTransactionsList(agentTransactionList);
		}
		/*catch (NamingException | SQLException | ClassNotFoundException | IOException e) {*/
			catch (SQLException | ClassNotFoundException | IOException e) {	
			//e.getMessage();
			logger.error("Process Agent last five transactions: ", e);
			return result = -100;
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int processUserTransactionsList(String accountNo, ISPDBConnector ispDBConnector)
	{
		int result = 200;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
		String userTransListQuery = "select PACKAGE, ADDRESS, CUSTOMER_NAME, DUE_AMOUNT, REMARKS, PAYMENT_MODE, CASH_DATE, PREVIOUS_OUTSTANDING, LAST_INVOICE_AMOUNT, "
				+ "PAID_AMOUNT, EMAIL_ID, ACCOUNT_NO, DUE_DATE, TRANSACTION_STATUS, OBRM_RECEIPT_NO, REFERENCE_ID, MOBILE_NO, TRANSACTION_TIMESTAMP,  "
				+ "CHEQUE_NO, CHEQUE_DATE, BANK_CODE, BANK_NAME, REFERENCE_ID, CARD_TRANSACTION_ID from (select * from FOS_PAYMENT_TRANS_MASTER "
				+ "where ACCOUNT_NO = '"+accountNo+"' "
				+ "order by TRANSACTION_TIMESTAMP desc) "
				+ "where rownum < 6";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection(); 
			statement = connection.createStatement();
			resultSet = statement.executeQuery(userTransListQuery);
			ResultSetMetaData meta = resultSet.getMetaData() ; 
			int numberOfColumns = meta.getColumnCount() ;
			List<Map<String, String>> userTransactionList = new ArrayList<Map<String, String>>();
			Map<String, String> userTransactionMap = null;
			while(resultSet.next())
			{
				userTransactionMap = new HashMap<String, String>();
				for (int i = 1 ; i <= numberOfColumns; i ++ ) {
					String columnName = meta.getColumnName(i);
					String columnValue = resultSet.getString(columnName);
					switch (columnName) {
					case "CASH_DATE": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "CHEQUE_NO": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "CHEQUE_DATE": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "BANK_CODE": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "BANK_NAME": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					case "TRANSACTION_TIMESTAMP": Date transactionTimestamp = resultSet.getTimestamp(columnName);  
					  	  if(transactionTimestamp == null)
					  		  columnValue = "-";
					  	  else
					  		  columnValue = dateFormat.format(transactionTimestamp);
					  	  break;
					case "CARD_TRANSACTION_ID": if(columnValue == null || columnValue.isEmpty())
						  columnValue = "-";
						  break;
					default:
						break;
					}
					userTransactionMap.put(columnName, columnValue);
				}
				userTransactionList.add(userTransactionMap);
			}
			setUserTransactionsList(userTransactionList);
		} 
		//catch (NamingException | SQLException e) {
		catch (SQLException | ClassNotFoundException | IOException e) {
			logger.error("Process Customer last five transactions: ", e);
			return result = -100;
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int processTerminalReport(ISPDBConnector ispDBConnector,
			String agentID) {
		int result = 200;
		/*String userTransListQuery = "select payment_mode, count(payment_mode) NUMBER_OF_TRANSACTIONS, sum(paid_amount) SUM_OF_TRANSACTIONS "
				+ "from FOS_PAYMENT_TRANS_MASTER where FOS_AGENT_ID = '"+agentID+"' and TRANSACTION_STATUS = 'SUCCESS' "
				+ "and trunc(transaction_timestamp) = trunc(SYSDATE) group by payment_mode";*/
		
		String userTransListQuery = "select payment_mode, count(payment_mode) NUMBER_OF_TRANSACTIONS, sum(paid_amount) SUM_OF_TRANSACTIONS "
				+ "from FOS_PAYMENT_TRANS_MASTER where FOS_AGENT_ID = '"+agentID+"'"
				+ "and trunc(transaction_timestamp) = trunc(SYSDATE) group by payment_mode";
		
		logger.info(userTransListQuery);
				//+ "between trunc(sysdate,'MM')and LAST_DAY(SYSDATE) group by payment_mode";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(userTransListQuery);
			ResultSetMetaData meta = resultSet.getMetaData() ; 
			int numberOfColumns = meta.getColumnCount() ;
			List<Map<String, String>> terminalReportList = new ArrayList<Map<String, String>>();
			Map<String, String> terminalReportMap = null;
			while(resultSet.next())
			{
				terminalReportMap = new HashMap<String, String>();
				for (int i = 1 ; i <= numberOfColumns; i ++ ) {
					String columnName = meta.getColumnName(i);
					String columnValue = resultSet.getString(columnName);
					terminalReportMap.put(columnName, columnValue);
				}
				terminalReportList.add(terminalReportMap);
			}
			setTerminalReportList(terminalReportList);
		} 
		//catch (NamingException | SQLException e) {
		catch (SQLException | ClassNotFoundException | IOException e) {
			logger.error("Process Terminal Report: ", e);
			return result = -100;
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}
}
