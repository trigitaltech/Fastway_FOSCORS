package com.BRM.CATV.CancelPlan;

public class CANCEL_PLAN_LISTElement {
	@com.google.gson.annotations.SerializedName("PLAN_OBJ")
	private java.lang.String plan_obj;

 	public void setPlan_obj(java.lang.String plan_obj) {
		this.plan_obj = plan_obj;
	}

	public java.lang.String getPlan_obj() {
		return plan_obj;
	}

	@com.google.gson.annotations.SerializedName("DEALS")
	private DEALElement[] deals;

 	public void setDeals(DEALElement[] deals) {
		this.deals = deals;
	}

	public DEALElement[] getDeals() {
		return deals;
	}

}