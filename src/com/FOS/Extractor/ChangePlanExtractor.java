package com.FOS.Extractor;

import com.BRM.CATV.ChangePlan.CUR_PLAN_LISTElement;
import com.BRM.CATV.ChangePlan.DEALElement;
import com.BRM.CATV.ChangePlan.NEW_PLAN_LISTElement;
import com.FOS.Validator.LoginValidator;

public class ChangePlanExtractor {
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

	@com.google.gson.annotations.SerializedName("NEW_PLAN_LIST")
	private NEW_PLAN_LISTElement[] new_plan_list;

	public void setNew_plan_list(NEW_PLAN_LISTElement[] new_plan_list) {
		this.new_plan_list = new_plan_list;
	}

	public NEW_PLAN_LISTElement[] getNew_plan_list() {
		return new_plan_list;
	}

	@com.google.gson.annotations.SerializedName("CUR_PLAN_LIST")
	private CUR_PLAN_LISTElement[] cur_plan_list;

	public void setCur_plan_list(CUR_PLAN_LISTElement[] cur_plan_list) {
		this.cur_plan_list = cur_plan_list;
	}

	public CUR_PLAN_LISTElement[] getCur_plan_list() {
		return cur_plan_list;
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

	public String getchangePlanXMlRequest(LoginValidator validator, String userID) {

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<MSO_OP_CUST_CHANGE_PLAN_inputFlist>");
		stringBuffer.append("<FLAGS>0</FLAGS>");
		stringBuffer.append("<PLAN_LISTS elem='0'>");
		if(cur_plan_list != null)
		{
			for(int i=0; i<cur_plan_list.length; i++)
			{
				String CUR_PLAN_OBJ = cur_plan_list[i].getPlan_obj();
				if(CUR_PLAN_OBJ != null)
				{
					stringBuffer.append("<PLAN_OBJ>"+CUR_PLAN_OBJ+"</PLAN_OBJ>");
					stringBuffer.append("<FLAGS>0</FLAGS>");
					DEALElement[] dealElements = cur_plan_list[i].getDeals();
					if(dealElements != null)
					{
						for(int j=0; j<dealElements.length; j++)
						{
							DEALElement dealElement = dealElements[j];
							if(dealElement != null)
							{
								String CUR_DEAL_OBJ = dealElement.getDeal_obj();
								String CUR_PACKAGE_ID = dealElement.getPackage_id();
								stringBuffer.append("<DEALS elem=\""+j+"\">");
								stringBuffer.append("<DEAL_OBJ>"+CUR_DEAL_OBJ+"</DEAL_OBJ>");
								stringBuffer.append("<PACKAGE_ID>"+CUR_PACKAGE_ID+"</PACKAGE_ID>");
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
		stringBuffer.append("<PLAN_LISTS elem='1'>");
		if(new_plan_list != null)
		{
			for(int i=0; i<new_plan_list.length; i++)
			{
				String NEW_PLAN_OBJ = new_plan_list[i].getPlan_obj();
				if(NEW_PLAN_OBJ != null)
				{
					stringBuffer.append("<PLAN_OBJ>"+NEW_PLAN_OBJ+"</PLAN_OBJ>");
					stringBuffer.append("<FLAGS>1</FLAGS>");
					DEALElement[] dealElements = new_plan_list[i].getDeals();
					if(dealElements != null)
					{
						for(int j=0; j<dealElements.length; j++)
						{
							DEALElement dealElement = dealElements[j];
							if(dealElement != null)
							{
								String NEW_DEAL_OBJ = dealElement.getDeal_obj();
								stringBuffer.append("<DEALS elem=\""+j+"\">");
								stringBuffer.append("<DEAL_OBJ>"+NEW_DEAL_OBJ+"</DEAL_OBJ>");
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
		stringBuffer.append("<DESCR>Others</DESCR>");
		//stringBuffer.append("<USERID>0.0.0.1 /account 115000 0</USERID>");
		stringBuffer.append("<USERID>"+userID+"</USERID>");
		stringBuffer.append("</MSO_OP_CUST_CHANGE_PLAN_inputFlist>");

		String changePlanXML = stringBuffer.toString();
		//System.out.println("Change Plan XML: \n");
		//System.out.println(changePlanXML);
		return changePlanXML;
	}

}