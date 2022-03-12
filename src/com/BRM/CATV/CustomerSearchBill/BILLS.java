
package com.BRM.CATV.CustomerSearchBill;

import com.google.gson.annotations.SerializedName;

public class BILLS {

	@SerializedName("ACCOUNT_OBJ")
    private String account_obj;
    
    @SerializedName("BILL_NO")
    private String bill_no;
    
    @SerializedName("DUE")
    private String due;
    
    @SerializedName("END_T")
    private String end_t;
    
    @SerializedName("POID")
    private String poid;
    
    @SerializedName("_elem")
    private String _elem;

	public String getAccount_obj() {
		return account_obj;
	}

	public void setAccount_obj(String account_obj) {
		this.account_obj = account_obj;
	}

	public String getBill_no() {
		return bill_no;
	}

	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}

	public String getDue() {
		return due;
	}

	public void setDue(String due) {
		this.due = due;
	}

	public String getEnd_t() {
		return end_t;
	}

	public void setEnd_t(String end_t) {
		this.end_t = end_t;
	}

	public String getPoid() {
		return poid;
	}

	public void setPoid(String poid) {
		this.poid = poid;
	}

	public String get_elem() {
		return _elem;
	}

	public void set_elem(String _elem) {
		this._elem = _elem;
	}
    
}
