
package com.BRM.CATV.CustomerSearchBill;

import com.google.gson.annotations.SerializedName;

public class CustomerSearchBillMaster {

    @SerializedName("MSO_OP_CUST_SEARCH_BILL_outputFlist")
    private MSO_OP_CUST_SEARCH_BILL_outputFlist mso_op_cust_search_bill_outputflist;

	public MSO_OP_CUST_SEARCH_BILL_outputFlist getMso_op_cust_search_bill_outputflist() {
		return mso_op_cust_search_bill_outputflist;
	}

	public void setMso_op_cust_search_bill_outputflist(
			MSO_OP_CUST_SEARCH_BILL_outputFlist mso_op_cust_search_bill_outputflist) {
		this.mso_op_cust_search_bill_outputflist = mso_op_cust_search_bill_outputflist;
	}
}
