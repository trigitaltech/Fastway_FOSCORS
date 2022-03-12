

package com.BRM.CATV.AddPlan;

public class PLAN_LISTElement {
	@com.google.gson.annotations.SerializedName("PLAN_LIST_ID")
	private java.lang.String plan_list_id;

 	public void setPlan_list_id(java.lang.String plan_list_id) {
		this.plan_list_id = plan_list_id;
	}

	public java.lang.String getPlan_list_id() {
		return plan_list_id;
	}

	@com.google.gson.annotations.SerializedName("SERVICE_TYPE")
	private java.lang.String service_type;

 	public void setService_type(java.lang.String service_type) {
		this.service_type = service_type;
	}

	public java.lang.String getService_type() {
		return service_type;
	}

	@com.google.gson.annotations.SerializedName("PLAN_LIST_NAME")
	private java.lang.String plan_list_name;

 	public void setPlan_list_name(java.lang.String plan_list_name) {
		this.plan_list_name = plan_list_name;
	}

	public java.lang.String getPlan_list_name() {
		return plan_list_name;
	}

	@com.google.gson.annotations.SerializedName("PLANS")
	private PLANElement[] plans;

 	public void setPlans(PLANElement[] plans) {
		this.plans = plans;
	}

	public PLANElement[] getPlans() {
		return plans;
	}

	@com.google.gson.annotations.SerializedName("CITY")
	private java.lang.String city;

 	public void setCity(java.lang.String city) {
		this.city = city;
	}

	public java.lang.String getCity() {
		return city;
	}

}