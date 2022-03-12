package com.FOS.Extractor;

import com.google.gson.annotations.SerializedName;

public class SLCExtractor {

	@SerializedName("ACCOUNT_NO")
	private String ACCOUNT_NO;

	@SerializedName("SLC_MESSAGE")
	private String SLC_MESSAGE;

	@SerializedName("SLC_TYPE")
	private String SLC_TYPE;
	
	@SerializedName("SLC_REMARKS")
	private String SLC_REMARKS;
	
	
	public String getACCOUNT_NO() {
		return ACCOUNT_NO;
	}

	public void setACCOUNT_NO(String aCCOUNT_NO) {
		ACCOUNT_NO = aCCOUNT_NO;
	}

	public String getSLC_MESSAGE() {
		return SLC_MESSAGE;
	}

	public void setSLC_MESSAGE(String sLC_MESSAGE) {
		SLC_MESSAGE = sLC_MESSAGE;
	}

	public String getSLC_TYPE() {
		return SLC_TYPE;
	}

	public void setSLC_TYPE(String sLC_TYPE) {
		SLC_TYPE = sLC_TYPE;
	}

	public String getSLC_REMARKS() {
		return SLC_REMARKS;
	}

	public void setSLC_REMARKS(String sLC_REMARKS) {
		SLC_REMARKS = sLC_REMARKS;
	}
}