package com.FOS.Validator;

import com.google.gson.annotations.SerializedName;

public class LoginValidator{
	@SerializedName("USERID")
	private String userID;

	@SerializedName("PASSWORD")
	private String password;

	@SerializedName("DEVICEIMEI")
	private String deviceIMEI;

	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
		//System.out.println("UserID: "+this.userID);
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		//System.out.println("Password: "+this.password);
	}
	public String getDeviceIMEI() {
		return deviceIMEI;
	}
	public void setDeviceIMEI(String deviceIMEI) {
		this.deviceIMEI = deviceIMEI;
		//System.out.println("IMEI: "+this.deviceIMEI);
	}
}
