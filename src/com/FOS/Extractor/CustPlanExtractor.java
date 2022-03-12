package com.FOS.Extractor;

import com.google.gson.annotations.SerializedName;

public class CustPlanExtractor 
{
	@SerializedName("CUST_PLAN_POID")
	private String CUST_PLAN_POID;

	@SerializedName("CUST_SERVICE_POID")
	private String CUST_SERVICE_POID;

	public String getCUST_PLAN_POID() {
		return CUST_PLAN_POID;
	}

	public void setCUST_PLAN_POID(String cUST_PLAN_POID) {
		CUST_PLAN_POID = cUST_PLAN_POID;
	}

	public String getCUST_SERVICE_POID() {
		return CUST_SERVICE_POID;
	}

	public void setCUST_SERVICE_POID(String cUST_SERVICE_POID) {
		CUST_SERVICE_POID = cUST_SERVICE_POID;
	}
}