package com.FOS.Extractor;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PlanCodesExtractor {
	@SerializedName("PLANCODES")
	private List<String> planCodes = null;

	public List<String> getPlanCodes() {
		return planCodes;
	}

	public void setPlanCodes(List<String> planCodes) {
		this.planCodes = planCodes;
	}
}
