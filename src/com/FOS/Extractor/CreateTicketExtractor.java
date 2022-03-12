package com.FOS.Extractor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateTicketExtractor {

	
	@SerializedName("CUST_ACCOUNT_NO")
	@Expose
	private String CUST_ACCOUNT_NO;

	@SerializedName("NOTES_TYPE")
	@Expose
	private String NOTES_TYPE;

	@SerializedName("CUSTOMER_NOTES")
	@Expose
	private String CUSTOMER_NOTES;

	@SerializedName("CREATED_ON")
	@Expose
	private String CREATED_ON;

	@SerializedName("RELEVANT_TICKET_ID")
	@Expose
	private String RELEVANT_TICKET_ID;

	@SerializedName("TICKET_ID_ADDED_ON")
	@Expose
	private String TICKET_ID_ADDED_ON;
	
	@SerializedName("STAFF_USER_ID")
	@Expose
	private String STAFF_USER_ID;

	public String getCUST_ACCOUNT_NO() {
		return CUST_ACCOUNT_NO;
	}

	public void setCUST_ACCOUNT_NO(String cUST_ACCOUNT_NO) {
		CUST_ACCOUNT_NO = cUST_ACCOUNT_NO;
	}

	public String getNOTES_TYPE() {
		return NOTES_TYPE;
	}

	public void setNOTES_TYPE(String nOTES_TYPE) {
		NOTES_TYPE = nOTES_TYPE;
	}

	public String getCUSTOMER_NOTES() {
		return CUSTOMER_NOTES;
	}

	public void setCUSTOMER_NOTES(String cUSTOMER_NOTES) {
		CUSTOMER_NOTES = cUSTOMER_NOTES;
	}

	public String getCREATED_ON() {
		return CREATED_ON;
	}

	public void setCREATED_ON(String cREATED_ON) {
		CREATED_ON = cREATED_ON;
	}

	public String getRELEVANT_TICKET_ID() {
		return RELEVANT_TICKET_ID;
	}

	public void setRELEVANT_TICKET_ID(String rELEVANT_TICKET_ID) {
		RELEVANT_TICKET_ID = rELEVANT_TICKET_ID;
	}

	public String getTICKET_ID_ADDED_ON() {
		return TICKET_ID_ADDED_ON;
	}

	public void setTICKET_ID_ADDED_ON(String tICKET_ID_ADDED_ON) {
		TICKET_ID_ADDED_ON = tICKET_ID_ADDED_ON;
	}

	public String getSTAFF_USER_ID() {
		return STAFF_USER_ID;
	}

	public void setSTAFF_USER_ID(String sTAFF_USER_ID) {
		STAFF_USER_ID = sTAFF_USER_ID;
	}

}