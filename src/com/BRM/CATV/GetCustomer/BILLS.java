package com.BRM.CATV.GetCustomer;

public class BILLS {
	@com.google.gson.annotations.SerializedName("SUBORDS_TOTAL")
	private java.lang.String subords_total;

 	/*public void setSubords_total(java.lang.Integer subords_total) {
		this.subords_total = subords_total;
	}*/

	public java.lang.Double getSubords_total() {
		if(subords_total == null || subords_total.equalsIgnoreCase(""))
			return 0.0d;
		else
			return Double.valueOf(subords_total);
	}

	@com.google.gson.annotations.SerializedName("BILL_NO")
	private java.lang.String bill_no;

 	public void setBill_no(java.lang.String bill_no) {
		this.bill_no = bill_no;
	}

	public java.lang.String getBill_no() {
		return bill_no;
	}

	@com.google.gson.annotations.SerializedName("CREATED_T")
	private java.lang.String created_t;

 	public void setCreated_t(java.lang.String created_t) {
		this.created_t = created_t;
	}

	public java.lang.String getCreated_t() {
		return created_t;
	}

	@com.google.gson.annotations.SerializedName("POID")
	private java.lang.String poid;

 	public void setPoid(java.lang.String poid) {
		this.poid = poid;
	}

	public java.lang.String getPoid() {
		return poid;
	}

	@com.google.gson.annotations.SerializedName("DISPUTED")
	private java.lang.String disputed;

 	/*public void setDisputed(java.lang.Integer disputed) {
		this.disputed = disputed;
	}*/

	public java.lang.Double getDisputed() {
		if(disputed == null || disputed.equalsIgnoreCase(""))
			return 0.0d;
		else
			return Double.valueOf(disputed);
	}

	@com.google.gson.annotations.SerializedName("PREVIOUS_TOTAL")
	private java.lang.String previous_total;

 	/*public void setPrevious_total(java.lang.Integer previous_total) {
		this.previous_total = previous_total;
	}*/

	public java.lang.Double getPrevious_total() {
		if(previous_total == null || previous_total.equalsIgnoreCase(""))
			return 0.0d;
		else
			return Double.valueOf(previous_total);
	}

	@com.google.gson.annotations.SerializedName("ADJUSTED")
	private java.lang.String adjusted;

 	/*public void setAdjusted(java.lang.Integer adjusted) {
		this.adjusted = adjusted;
	}*/

	public java.lang.Double getAdjusted() {
		if(adjusted == null || adjusted.equalsIgnoreCase(""))
			return 0.0d;
		else
			return Double.valueOf(adjusted);
	}

	@com.google.gson.annotations.SerializedName("elem")
	private java.lang.String elem;

 	/*public void setElem(java.lang.Integer elem) {
		this.elem = elem;
	}*/

	public java.lang.Integer getElem() {
		if(elem == null || elem.equalsIgnoreCase(""))
			return 0;
		else
			return Integer.valueOf(elem);
	}

	@com.google.gson.annotations.SerializedName("RECVD")
	private java.lang.String recvd;

 	/*public void setRecvd(java.lang.Integer recvd) {
		this.recvd = recvd;
	}*/

	public java.lang.Double getRecvd() {
		if(recvd == null || recvd.equalsIgnoreCase(""))
			return 0.0d;
		else
			return Double.valueOf(recvd);
	}

	@com.google.gson.annotations.SerializedName("TOTAL_DUE")
	private java.lang.String total_due;

 	/*public void setTotal_due(java.lang.Integer total_due) {
		this.total_due = total_due;
	}*/

	public java.lang.Double getTotal_due() {
		if(total_due == null || total_due.equalsIgnoreCase(""))
			return 0.0d;
		else
			return Double.valueOf(total_due);
	}

	@com.google.gson.annotations.SerializedName("DUE_T")
	private java.lang.String due_t;

 	public void setDue_t(java.lang.String due_t) {
		this.due_t = due_t;
	}

	public java.lang.String getDue_t() {
		return due_t;
	}

	@com.google.gson.annotations.SerializedName("DUE")
	private java.lang.String due;

 	/*public void setDue(java.lang.Integer due) {
		this.due = due;
	}*/

	public java.lang.Double getDue() {
		if(due == null || due.equalsIgnoreCase(""))
			return 0.0d;
		else
			return Double.valueOf(due);
	}

	@com.google.gson.annotations.SerializedName("CURRENT_TOTAL")
	private java.lang.String current_total;

 	/*public void setCurrent_total(java.lang.Integer current_total) {
		this.current_total = current_total;
	}*/

	public java.lang.Double getCurrent_total() {
		if(current_total == null || current_total.equalsIgnoreCase(""))
			return 0.0d;
		else
			return Double.valueOf(current_total);
	}

}