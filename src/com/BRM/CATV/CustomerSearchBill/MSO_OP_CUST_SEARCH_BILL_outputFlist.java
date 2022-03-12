
package com.BRM.CATV.CustomerSearchBill;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MSO_OP_CUST_SEARCH_BILL_outputFlist {

    @SerializedName("BILLS")
    private List<BILLS> bills = null;
    
    @SerializedName("POID")
    private String poid;

	public List<BILLS> getBills() {
		return bills;
	}

	public void setBills(List<BILLS> bills) {
		this.bills = bills;
	}

	public String getPoid() {
		return poid;
	}

	public void setPoid(String poid) {
		this.poid = poid;
	}
}
