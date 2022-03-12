package com.FOS.Extractor;

import com.google.gson.annotations.SerializedName;

public class CustomerPlanDetailExtractor 
{
	@SerializedName("ACCOUNT_OBJ")
	private String ACCOUNT_OBJ;
	
	@SerializedName("ACCOUNT_POID")
	private String ACCOUNT_POID;
	
	@SerializedName("SERVICE_OBJ")
	private String SERVICE_OBJ;
	
	@SerializedName("PLAN_OBJ")
	private String PLAN_OBJ;
	
	@SerializedName("FLAGS")
	private String FLAGS;
	
	@SerializedName("BILL_WHEN")
	private String BILL_WHEN;
	
	@SerializedName("CITY")
	private String CITY;

	public String getACCOUNT_OBJ() {
		return ACCOUNT_OBJ;
	}

	public void setACCOUNT_OBJ(String aCCOUNT_OBJ) {
		ACCOUNT_OBJ = aCCOUNT_OBJ;
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

	public String getPLAN_OBJ() {
		return PLAN_OBJ;
	}

	public void setPLAN_OBJ(String pLAN_OBJ) {
		PLAN_OBJ = pLAN_OBJ;
	}

	public String getFLAGS() {
		return FLAGS;
	}

	public void setFLAGS(String fLAGS) {
		FLAGS = fLAGS;
	}

	public String getBILL_WHEN() {
		return BILL_WHEN;
	}

	public void setBILL_WHEN(String bILL_WHEN) {
		BILL_WHEN = bILL_WHEN;
	}

	public String getCITY() {
		return CITY;
	}

	public void setCITY(String cITY) {
		CITY = cITY;
	}
	
	public String extractCustPlanPricePayload()
	{
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<MSO_OP_CUST_CALC_PRICE_inputFlist>");
		stringBuffer.append("<ACTION>ADD PLAN</ACTION>");
		stringBuffer.append("<DATA_ARRAY elem=\"0\">");
		stringBuffer.append("<ACCOUNT_OBJ>"+ACCOUNT_OBJ+"</ACCOUNT_OBJ>");
		stringBuffer.append("<PLAN_OBJ>"+PLAN_OBJ+"</PLAN_OBJ>");
		stringBuffer.append("<POID>"+ACCOUNT_POID+"</POID>");
		stringBuffer.append("<SERVICE_OBJ>"+SERVICE_OBJ+"</SERVICE_OBJ>");
		stringBuffer.append("<USERID>0.0.0.1 /account 5508823614 0</USERID>");
		stringBuffer.append("<ACCOUNT_OBJ>"+getACCOUNT_OBJ()+"</ACCOUNT_OBJ>");
		stringBuffer.append("</DATA_ARRAY>");
		stringBuffer.append("<FLAGS>"+FLAGS+"</FLAGS>");
		stringBuffer.append("<POID>"+ACCOUNT_POID+"</POID>");
		stringBuffer.append("<PROGRAM_NAME>FOS</PROGRAM_NAME>");
		stringBuffer.append("</MSO_OP_CUST_CALC_PRICE_inputFlist>");
		String payloadRequest = stringBuffer.toString();
		return payloadRequest;
	}
}