package com.FOS.Extractor;

import com.BRM.CATV.CancelPlan.CANCEL_PLAN_LISTElement;
import com.BRM.CATV.CancelPlan.DEALElement;
import com.FOS.Validator.LoginValidator;

public class CancelPlanExtractor {
	@com.google.gson.annotations.SerializedName("ACCOUNT_NO")
	private java.lang.String account_no;

	public void setAccount_no(java.lang.String account_no) {
		this.account_no = account_no;
	}

	public java.lang.String getAccount_no() {
		return account_no;
	}

	@com.google.gson.annotations.SerializedName("SERVICE_OBJ")
	private java.lang.String service_obj;

	public void setService_obj(java.lang.String service_obj) {
		this.service_obj = service_obj;
	}

	public java.lang.String getService_obj() {
		return service_obj;
	}

	@com.google.gson.annotations.SerializedName("CANCEL_PLAN_LIST")
	private CANCEL_PLAN_LISTElement[] cancel_plan_list;

	public CANCEL_PLAN_LISTElement[] getCancel_plan_list() {
		return cancel_plan_list;
	}

	public void setCancel_plan_list(CANCEL_PLAN_LISTElement[] cancel_plan_list) {
		this.cancel_plan_list = cancel_plan_list;
	}

	@com.google.gson.annotations.SerializedName("ACCOUNT_POID")
	private java.lang.String account_poid;

	public void setAccount_poid(java.lang.String account_poid) {
		this.account_poid = account_poid;
	}

	public java.lang.String getAccount_poid() {
		return account_poid;
	}

	@com.google.gson.annotations.SerializedName("CUSTOMER_NAME")
	private java.lang.String customer_name;

	public java.lang.String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(java.lang.String customer_name) {
		this.customer_name = customer_name;
	}

	public String getCancelPlanXMlRequest(LoginValidator validator, String userID) {

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<MSO_OP_CUST_CANCEL_PLAN_inputFlist>");
		if(cancel_plan_list != null)
		{
			for(int i=0; i<cancel_plan_list.length; i++)
			{
				stringBuffer.append("<PLAN_LISTS elem='"+i+"'>");
				String CANCEL_PLAN_OBJ = cancel_plan_list[i].getPlan_obj();
				if(CANCEL_PLAN_OBJ != null)
				{
					stringBuffer.append("<PLAN_OBJ>"+CANCEL_PLAN_OBJ+"</PLAN_OBJ>");
					DEALElement[] dealElements = cancel_plan_list[i].getDeals();
					if(dealElements != null)
					{
						for(int j=0; j<dealElements.length; j++)
						{
							DEALElement dealElement = dealElements[j];
							if(dealElement != null)
							{
								String CANCEL_DEAL_OBJ = dealElement.getDeal_obj();
								String CANCEL_PACKAGE_ID = dealElement.getPackage_id();
								stringBuffer.append("<DEALS elem=\""+j+"\">");
								stringBuffer.append("<DEAL_OBJ>"+CANCEL_DEAL_OBJ+"</DEAL_OBJ>");
								stringBuffer.append("<PACKAGE_ID>"+CANCEL_PACKAGE_ID+"</PACKAGE_ID>");
								stringBuffer.append("</DEALS>");

							}
						}
					}

				}
			}
		}
		else
			return null;
		stringBuffer.append("</PLAN_LISTS>");
		stringBuffer.append("<POID>"+account_poid+"</POID>");
		//stringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
		stringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"</PROGRAM_NAME>");
		stringBuffer.append("<SERVICE_OBJ>"+service_obj+"</SERVICE_OBJ>");
		//stringBuffer.append("<USERID>0.0.0.1 /account 115000 0</USERID>");
		stringBuffer.append("<USERID>"+userID+"</USERID>");
		stringBuffer.append("</MSO_OP_CUST_CANCEL_PLAN_inputFlist>");

		String cancelPlanXML = stringBuffer.toString();
		//System.out.println("Cancel Plan XML: \n");
		//System.out.println(cancelPlanXML);
		return cancelPlanXML;
	}

}