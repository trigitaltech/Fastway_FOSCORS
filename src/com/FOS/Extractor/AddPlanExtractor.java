package com.FOS.Extractor;

import com.BRM.CATV.AddPlan.PLANElement;
import com.BRM.CATV.AddPlan.PLAN_LISTElement;
import com.FOS.Validator.LoginValidator;
import com.google.gson.annotations.SerializedName;

public class AddPlanExtractor {

	@SerializedName("ACCOUNT_NO")
	private String ACCOUNT_NO;

	@SerializedName("ACCOUNT_POID")
	private String ACCOUNT_POID;

	@SerializedName("SERVICE_OBJ")
	private String SERVICE_OBJ;

	@SerializedName("PLAN_LIST")
	private PLAN_LISTElement[] plan_list;

	public String getACCOUNT_NO() {
		return ACCOUNT_NO;
	}

	public void setACCOUNT_NO(String aCCOUNT_NO) {
		ACCOUNT_NO = aCCOUNT_NO;
	}

	public String getACCOUNT_POID() {
		return ACCOUNT_POID;
	}

	public void setACCOUNT_POID(String aCCOUNT_POID) {
		ACCOUNT_POID = aCCOUNT_POID;
	}

	public String getSERVICE_OBJ() {
		return SERVICE_OBJ;
	}

	public void setSERVICE_OBJ(String sERVICE_OBJ) {
		SERVICE_OBJ = sERVICE_OBJ;
	}

	public void setPlan_list(PLAN_LISTElement[] plan_list) {
		this.plan_list = plan_list;
	}

	public PLAN_LISTElement[] getPlan_list() {
		return plan_list;
	}

	public String extractAddPlanXMlRequest(LoginValidator validator, String userID) {

		if(plan_list != null)
		{
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			stringBuffer.append("<MSO_OP_CUST_ADD_PLAN_inputFlist>");
			stringBuffer.append("<POID>"+ACCOUNT_POID+"</POID>");
			stringBuffer.append("<SERVICE_OBJ>"+SERVICE_OBJ+"</SERVICE_OBJ>");
			for(int i=0; i<plan_list.length; i++)
			{
				PLANElement[] plans = plan_list[i].getPlans();
				for(int j=0; j<plans.length; j++){
					stringBuffer.append("<PLAN_LISTS elem=\'"+j+"\'>");
					stringBuffer.append("<PLAN_OBJ>"+plans[j].getPoid()+"</PLAN_OBJ>");
					stringBuffer.append("</PLAN_LISTS>");
				}
			}
			//stringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
			stringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"</PROGRAM_NAME>");
			stringBuffer.append("<DESCR>Others</DESCR>");
			//stringBuffer.append("<USERID>0.0.0.1 /account 115000 0</USERID>");
			stringBuffer.append("<USERID>"+userID+"</USERID>");
			stringBuffer.append("</MSO_OP_CUST_ADD_PLAN_inputFlist>");
			String addPlanXML = stringBuffer.toString();
			//System.out.println("Add Plan XML: \n");
			//System.out.println(addPlanXML);
			return addPlanXML;
		}
		else
			return null;
	}

	/*public String extractTopUpXMlRequest(LoginValidator validator, ISPDBConnector ispDBConnector) {

		String PLAN_POID = PLAN_OBJ.replaceFirst("0.0.0.1 /plan ", "");
		PLAN_POID = PLAN_POID.substring(0, PLAN_POID.indexOf(" "));
		//logger.info("PLN POID: "+PLAN_POID);
		String technologyQuery = "select distinct TECHNOLOGY TECH from pin.hathway_plan_master_t@prodbrm where PLAN_POID ="+PLAN_POID;
		String TECHNOLOGY = "";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(technologyQuery);
			if(resultSet.next())
				TECHNOLOGY = resultSet.getString("TECH");
			logger.info("Technology:"+TECHNOLOGY);
		} catch (ClassNotFoundException | IOException | SQLException e) {
			// TODO Auto-generated catch block
			logger.error("Extract TopUp XMl Request: ", e);
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<MSO_OP_CUST_TOP_UP_BB_PLAN_inputFlist>");
		stringBuffer.append("<MSO_FLD_TECHNOLOGY>"+TECHNOLOGY+"</MSO_FLD_TECHNOLOGY>");
		stringBuffer.append("<ACCOUNT_OBJ>"+ACCOUNT_OBJ+"</ACCOUNT_OBJ>");
		stringBuffer.append("<POID>"+ACCOUNT_POID+"</POID>");
		stringBuffer.append("<SERVICE_OBJ>"+SERVICE_OBJ+"</SERVICE_OBJ>");
		stringBuffer.append("<PLAN_OBJ>"+PLAN_OBJ+"</PLAN_OBJ>");
		stringBuffer.append("<MSO_FLD_DISC_PERCENT>0</MSO_FLD_DISC_PERCENT>");
		stringBuffer.append("<PROGRAM_NAME>FOS|"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
		stringBuffer.append("<USERID>0.0.0.1 /account 5508823614 0</USERID>");
		stringBuffer.append("</MSO_OP_CUST_TOP_UP_BB_PLAN_inputFlist>");
		return stringBuffer.toString();
	}

	public String getPlanRenewalXMlRequest(LoginValidator validator,
			ISPDBConnector ispDBConnector) {
		String PLAN_POID = PLAN_OBJ.replaceFirst("0.0.0.1 /plan ", "");
		PLAN_POID = PLAN_POID.substring(0, PLAN_POID.indexOf(" "));
		//logger.info("PLN POID: "+PLAN_POID);
		String technologyQuery = "select distinct TECHNOLOGY TECH from pin.hathway_plan_master_t@prodbrm where PLAN_POID ="+PLAN_POID;
		String TECHNOLOGY = "";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = ispDBConnector.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(technologyQuery);
			if(resultSet.next())
				TECHNOLOGY = resultSet.getString("TECH");
			logger.info("Technology:"+TECHNOLOGY);
		} catch (ClassNotFoundException | IOException | SQLException e) {
			logger.error("Extract Plan Renewal XMl Request: ", e);
		}
		finally
		{
			HouseKeeper.closeResultSet(resultSet);
			HouseKeeper.closeStatement(statement);
			ispDBConnector.closeConnection(connection);
		}
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<MSO_OP_CUST_RENEW_BB_PLAN_inputFlist>");
		stringBuffer.append("<MSO_FLD_TECHNOLOGY>"+TECHNOLOGY+"</MSO_FLD_TECHNOLOGY>");
		stringBuffer.append("<ACCOUNT_OBJ>"+ACCOUNT_OBJ+"</ACCOUNT_OBJ>");
		stringBuffer.append("<POID>"+ACCOUNT_POID+"</POID>");
		stringBuffer.append("<SERVICE_OBJ>"+SERVICE_OBJ+"</SERVICE_OBJ>");
		stringBuffer.append("<PLAN_OBJ>"+PLAN_OBJ+"</PLAN_OBJ>");
		stringBuffer.append("<MSO_FLD_DISC_PERCENT>0</MSO_FLD_DISC_PERCENT>");
		stringBuffer.append("<PROGRAM_NAME>FOS|"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
		stringBuffer.append("<USERID>0.0.0.1 /account 5508823614 0</USERID>");
		stringBuffer.append("</MSO_OP_CUST_RENEW_BB_PLAN_inputFlist>");
		return stringBuffer.toString();
	}*/

}