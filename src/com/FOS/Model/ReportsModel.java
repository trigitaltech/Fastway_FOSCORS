package com.FOS.Model;

import java.io.IOException;
import java.sql.Connection;
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
import com.FOS.DBConnector.PinDBConnector;
import com.google.gson.Gson;

public class ReportsModel {

	final static Logger logger = LogManager.getLogger(ReportsModel.class);
	private String totalSubscriberCountResponse;
	private String subscriberWiseActivePkgCountResponse;
	private String dailyAndWeeklyCollectionReportResponse;
	private String openAndClosedComplaintsCountResponse;

	public String getTotalSubscriberCountResponse() {
		return totalSubscriberCountResponse;
	}

	public void setTotalSubscriberCountResponse(String totalSubscriberCountResponse) {
		this.totalSubscriberCountResponse = totalSubscriberCountResponse;
	}

	public String getSubscriberWiseActivePkgCountResponse() {
		return subscriberWiseActivePkgCountResponse;
	}

	public void setSubscriberWiseActivePkgCountResponse(
			String subscriberWiseActivePkgCountResponse) {
		this.subscriberWiseActivePkgCountResponse = subscriberWiseActivePkgCountResponse;
	}

	public String getDailyAndWeeklyCollectionReportResponse() {
		return dailyAndWeeklyCollectionReportResponse;
	}

	public void setDailyAndWeeklyCollectionReportResponse(
			String dailyAndWeeklyCollectionReportResponse) {
		this.dailyAndWeeklyCollectionReportResponse = dailyAndWeeklyCollectionReportResponse;
	}

	public String getOpenAndClosedComplaintsCountResponse() {
		return openAndClosedComplaintsCountResponse;
	}

	public void setOpenAndClosedComplaintsCountResponse(
			String openAndClosedComplaintsCountResponse) {
		this.openAndClosedComplaintsCountResponse = openAndClosedComplaintsCountResponse;
	}

	public int totalSubscriberCountResult(ISPDBConnector ispDBConnector, String agentAccountPOID) {
		int result = 200;
		List<Map<String, String>> totalSubscriberCountList = null;
		Gson gson = new Gson();
		
		/* String sql =
		 * "SELECT DECODE(SERVICE_STATUS,10100,'ACTIVE',10102,'SUSPENDED',10103,'TERMINATED') STATUS, COUNT(1) COUNT_OF_CUSTOMER " 
		 * + "FROM (SELECT DISTINCT CUSTOMER_ACCOUNT_NO,SERVICE_STATUS,LCO_ACCOUNT_NO, LCO_OBJ_ID0,PP_TYPE,NETWORK_NODE,CONNECTION_TYPE FROM RPT_BASE_FWAPP_LCO_T "
		 * + "WHERE LCO_OBJ_ID0="+agentAccountPOID+") GROUP BY SERVICE_STATUS"; */
		
		String sql ="select DECODE(S.STATUS,10100,'ACTIVE',10102,'SUSPENDED',10103,'TERMINATED') STATUS, COUNT(1) COUNT_OF_CUSTOMER"
				+ " from pin.service_t@prodpindb s,pin.profile_t@prodpindb p,pin.profile_customer_care_t@prodpindb cc "
				+ " where cc.lco_obj_id0 ="+agentAccountPOID+" "
				+ " and cc.obj_id0 = p.poid_id0 and p.account_obj_id0 = s.account_obj_id0 "
				+ " group  by status";
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			totalSubscriberCountList = convertToMapList(resultSet);
			String totalSubscriberCountResponse = gson.toJson(totalSubscriberCountList);
			logger.info("Total Subscriber Count Response: "+totalSubscriberCountResponse);
			setTotalSubscriberCountResponse(totalSubscriberCountResponse);

		} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
			logger.error(e);
			return -100;
		}
		finally {
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int subscriberWiseActivePkgCountResult(ISPDBConnector ispDBConnector, String agentAccountPOID) {
		int result = 200;
		List<Map<String, String>> subscriberWiseActivePkgCountList = null;
		Gson gson = new Gson();
		
		/* String sql =
		 * "SELECT BASE_PLAN_NAME,COUNT(1) PACKAGE_COUNT FROM RPT_BASE_FWAPP_LCO_T "+
		 * "WHERE LCO_OBJ_ID0="+agentAccountPOID+" AND SERVICE_STATUS = 10100 "+
		 * "AND PLAN_STATUS = 1 GROUP BY BASE_PLAN_NAME"; */
		
		String sql ="select pl.name BASE_PLAN_NAME, count(1) PACKAGE_COUNT from pin.plan_t@prodpindb pl, "
				+ " pin.product_t@prodpindb pr, pin.purchased_product_t@prodpindb pp, "
				+ " pin.profile_customer_care_t@prodpindb pcc, pin.profile_t@prodpindb pf "
				+ " where pp.status = 1 and pp.product_obj_id0 = pr.poid_id0 and pr.priority >=100 and pr.priority <140 "
				+ " and pl.poid_id0 = pp.plan_obj_id0 and pcc.lco_obj_id0 = " +agentAccountPOID+" "
				+ " and pcc.obj_id0 = pf.poid_id0 and pf.account_obj_id0 = pp.account_obj_id0 "
				+ " group by pl.name";
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			subscriberWiseActivePkgCountList = convertToMapList(resultSet);
			String subscriberWiseActivePkgCountResponse = gson.toJson(subscriberWiseActivePkgCountList);
			logger.info("Subscriber Wise Active Pkg Count Response: "+subscriberWiseActivePkgCountResponse);
			setSubscriberWiseActivePkgCountResponse(subscriberWiseActivePkgCountResponse);

		} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
			logger.error(e);
			return -100;
		}
		finally {
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int dailyAndWeeklyCollectionReportResult(PinDBConnector pinDBConnector, String userID) {
		int result = 200;
		List<Map<String, String>> dailyAndWeeklyCollectionReportList = null;
		Gson gson = new Gson();
		/*String sql = "SELECT PARTY_ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MOBILE_PHONE, SUB_PKG, ENTITY_CODE, ENTITY_NAME, LOCATION, "
				+ "RECEIPT_NBR, PAYMENT_ID, TO_DATE(PAYMENT_DATE,'DD/MON/YYYY') PAYMENT_DATE, "
				+ "TO_DATE(ENTERED_DATE,'DD/MON/YYYY') ENTERED_DATE, ENTERED_BY, AMOUNT, OUT_STANDING FROM MSO_COLLECTION_REPORT_RPT "
				+ "WHERE ENTITY_CODE = (SELECT ACCOUNT_NO FROM ACCOUNT_T WHERE POID_ID0 = "+agentAccountPOID+") "
				+ "AND PAYMENT_DATE >= ORA2INF(TO_CHAR(SYSDATE-7, 'DD-MON-YYYY'))";*/
		
		/*String sql = "SELECT TO_CHAR(INF2ORA(R.PAYMENT_DATE),'DD-MON-YYYY') PAYMENT_DATE, "+
				"COUNT(0) NO_OF_PAYMENTS, SUM(AMOUNT) TOTAL_AMOUNT "+
				"FROM PIN.MSO_COLLECTION_REPORT_RPT@PRODPINDB R "+
				"WHERE ENTITY_CODE= (SELECT ACCOUNT_NO FROM PIN.ACCOUNT_T@PRODPINDB WHERE POID_ID0 = "+agentAccountPOID+") "+
				"AND R.PAYMENT_DATE >= ORA2INF(TO_CHAR(SYSDATE-7, 'DD-MON-YYYY')) "+
				"GROUP BY R.PAYMENT_DATE";*/
		
		String sql = "SELECT TO_CHAR(INF2ORA(R.PAYMENT_DATE),'DD-MON-YYYY') PAYMENT_DATE, "+
				"COUNT(0) NO_OF_PAYMENTS, SUM(AMOUNT) TOTAL_AMOUNT "+
				"FROM PIN.MSO_COLLECTION_REPORT_RPT@PRODPINDB R "+
				"WHERE ENTERED_BY= '"+userID+"'  "+
				"AND R.PAYMENT_DATE >= ORA2INF(TO_CHAR(SYSDATE-7, 'DD-MON-YYYY')) "+
				"GROUP BY R.PAYMENT_DATE";
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = pinDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			dailyAndWeeklyCollectionReportList = convertToMapList(resultSet);
			String dailyAndWeeklyCollectionReportResponse = gson.toJson(dailyAndWeeklyCollectionReportList);
			logger.info("Daily and Weekly Collection Report Response: "+dailyAndWeeklyCollectionReportResponse);
			setDailyAndWeeklyCollectionReportResponse(dailyAndWeeklyCollectionReportResponse);
		} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
			logger.error(e);
			return -100;
		}
		finally {
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			pinDBConnector.closeConnection(connection);
		}
		return result;
	}

	public int openAndClosedComplaintsCountResult(PinDBConnector pinDBConnector, String agentAccountPOID, String period) {
		int result = 200;
		List<Map<String, String>> openAndClosedComplaintsCountList = null;
		Gson gson = new Gson();
		String sql = null;

		switch (Integer.parseInt(period)) {
		case 1: sql = "SELECT "+
				" CASE "+
				" WHEN TICKET_STATUS = 'CLOSED' "+
				" THEN 'CLOSED' "+
				" ELSE 'OPEN' "+
				" END STATUS,COUNT(1) FROM OAP.MSO_CALL_LOG_RPT@PRODPINDB "+
				" WHERE ENTITY_NO = (SELECT ACCOUNT_NO FROM PIN.ACCOUNT_T@PRODPINDB WHERE POID_ID0 = "+agentAccountPOID+") "+
				" AND TO_DATE(CREATED,'DD/MON/YYYY HH24:MI:SS') >= SYSDATE-"+period+
				" GROUP BY TICKET_STATUS";
		break;

		case 7: sql = "SELECT "+
				" CASE "+
				" WHEN TICKET_STATUS = 'CLOSED' "+
				" THEN 'CLOSED' "+
				" ELSE 'OPEN' "+
				" END STATUS,COUNT(1) FROM OAP.MSO_CALL_LOG_RPT@PRODPINDB "+
				" WHERE ENTITY_NO = (SELECT ACCOUNT_NO FROM PIN.ACCOUNT_T@PRODPINDB WHERE POID_ID0 = "+agentAccountPOID+") "+
				" AND TO_DATE(CREATED,'DD/MON/YYYY HH24:MI:SS') >= SYSDATE-"+period+
				" GROUP BY TICKET_STATUS";
		break;

		default: return -100;
		}
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = pinDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			openAndClosedComplaintsCountList = convertToMapList(resultSet);
			String openAndClosedComplaintsCountResponse = gson.toJson(openAndClosedComplaintsCountList);
			logger.info("Open and Closed Complaints Count Response: "+openAndClosedComplaintsCountResponse);
			setOpenAndClosedComplaintsCountResponse(openAndClosedComplaintsCountResponse);
		} catch (SQLException | NullPointerException | ClassNotFoundException | IOException e) {
			logger.error(e);
			return -100;
		}
		finally {
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			pinDBConnector.closeConnection(connection);
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
