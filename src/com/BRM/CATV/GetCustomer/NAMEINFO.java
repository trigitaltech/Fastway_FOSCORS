/* Generated by JavaFromJSON */
/*http://javafromjson.dashingrocket.com*/

package com.BRM.CATV.GetCustomer;

public class NAMEINFO {
	@com.google.gson.annotations.SerializedName("MIDDLE_NAME")
	private java.lang.String middle_name;

 	public void setMiddle_name(java.lang.String middle_name) {
		this.middle_name = middle_name;
	}

	public java.lang.String getMiddle_name() {
		return middle_name;
	}

	@com.google.gson.annotations.SerializedName("COMPANY")
	private java.lang.String company;

 	public void setCompany(java.lang.String company) {
		this.company = company;
	}

	public java.lang.String getCompany() {
		return company;
	}

	@com.google.gson.annotations.SerializedName("FIRST_NAME")
	private java.lang.String first_name;

 	public void setFirst_name(java.lang.String first_name) {
		this.first_name = first_name;
	}

	public java.lang.String getFirst_name() {
		return first_name;
	}

	@com.google.gson.annotations.SerializedName("LAST_NAME")
	private java.lang.String last_name;

 	public void setLast_name(java.lang.String last_name) {
		this.last_name = last_name;
	}

	public java.lang.String getLast_name() {
		return last_name;
	}

	@com.google.gson.annotations.SerializedName("elem")
	private java.lang.String elem;

 	/*public void setElem(java.lang.Integer elem) {
		this.elem = elem;
	}*/

	public java.lang.Integer getElem() {
		if(elem == null || elem.equals(""))
			return 0;
		else
		return Integer.valueOf(elem);
	}

	@com.google.gson.annotations.SerializedName("ACCOUNT_TYPE")
	private java.lang.Integer account_type;

 	public void setAccount_type(java.lang.Integer account_type) {
		this.account_type = account_type;
	}

	public java.lang.Integer getAccount_type() {
		return account_type;
	}

}