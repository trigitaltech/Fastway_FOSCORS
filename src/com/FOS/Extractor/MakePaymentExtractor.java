package com.FOS.Extractor;

/*import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;*/
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import com.FOS.Validator.LoginValidator;
import com.google.gson.annotations.SerializedName;

public class MakePaymentExtractor {

	private String uniqueTransactionID;

	@SerializedName("ACCOUNT_NO")
	private String ACCOUNT_NO;

	@SerializedName("ACCOUNT_OBJ")
	private String ACCOUNT_OBJ;

	@SerializedName("ACCOUNT_POID")
	private String ACCOUNT_POID;

	@SerializedName("SERVICE_OBJ")
	private String SERVICE_OBJ;
	
	@SerializedName("ADDRESS")
	private String ADDRESS;

	@SerializedName("BANK_NAME")
	private String BANK_NAME;

	@SerializedName("CASH_DATE")
	private String CASH_DATE;

	@SerializedName("CHEQUE_NUMBER")
	private String CHEQUE_NUMBER;

	@SerializedName("CHEQUE_DATE")
	private String CHEQUE_DATE;

	@SerializedName("PAID_AMOUNT")
	private String PAID_AMOUNT;
	
	@SerializedName("CITY")
	private String CITY;
	
	@SerializedName("MSO_FLD_AGREEMENT_NO")
	private String MSO_FLD_AGREEMENT_NO;
	
	@SerializedName("CURRENT_BALANCE_INC_PENDING_CHARGES")
	private String CURRENT_BALANCE_INC_PENDING_CHARGES;

	@SerializedName("CUSTOMER_NAME")
	private String CUSTOMER_NAME;

	@SerializedName("DUE_DATE")
	private String DUE_DATE;

	@SerializedName("EMAIL_ID")
	private String EMAIL_ID;

	@SerializedName("LAST_INVOICE")
	private String LAST_INVOICE;

	@SerializedName("MOBILE_NO")
	private String MOBILE_NO;

	@SerializedName("PAY_TYPE")
	private String PAY_TYPE;

	@SerializedName("PLAN_NAME")
	private String PLAN_NAME;

	@SerializedName("PREVIOUS_OUTSTANDING")
	private String PREVIOUS_OUTSTANDING;

	@SerializedName("STATE")
	private String STATE;
	
	@SerializedName("BANK_CODE")
	private String BANK_CODE;

	@SerializedName("REMARKS")
	private String REMARKS;
	
	@SerializedName("ALTERNATE_MOBILE_NO")
	private String ALTERNATE_MOBILE_NO;

	@SerializedName("ALTERNATE_EMAIL_ID")
	private String ALTERNATE_EMAIL_ID;
	
	@SerializedName("T_TYPE")
	private String T_TYPE;
	
	@SerializedName("T_ID")
	private String T_ID;
	
	@SerializedName("T_STATUS")
	private String T_STATUS;
	
	@SerializedName("T_ORIG_AMOUNT")
	private String T_ORIG_AMOUNT;

	@SerializedName("T_FIXED_AMOUNT")
	private String T_FIXED_AMOUNT;

	@SerializedName("T_TAXED_AMOUNT")
	private String T_TAXED_AMOUNT;

	@SerializedName("T_TOTAL_AMOUNT")
	private String T_TOTAL_AMOUNT;

	@SerializedName("T_PAYABLE_AMOUNT")
	private String T_PAYABLE_AMOUNT;
	
	@SerializedName("CARD_TRANSACTION_ID")
	private String CARD_TRANSACTION_ID;
	
	@SerializedName("LATITUDE")
	private String LATITUDE;
	
	@SerializedName("LONGITUDE")
	private String LONGITUDE;
	
	public String getUniqueTransactionID() {
		return uniqueTransactionID;
	}

	public void setUniqueTransactionID(String uniqueTransactionID) {
		this.uniqueTransactionID = uniqueTransactionID;
	}

	public String getBANK_CODE() {
		return BANK_CODE;
	}

	public void setBANK_CODE(String bANK_CODE) {
		BANK_CODE = bANK_CODE;
	}

	public String getACCOUNT_NO() {
		return ACCOUNT_NO;
	}

	public void setACCOUNT_NO(String aCCOUNT_NO) {
		ACCOUNT_NO = aCCOUNT_NO;
	}

	public String getACCOUNT_OBJ() {
		return ACCOUNT_OBJ;
	}

	public void setACCOUNT_OBJ(String aCCOUNT_OBJ) {
		ACCOUNT_OBJ = aCCOUNT_OBJ;
	}

	public String getACCOUNT_POID() {
		return ACCOUNT_POID;
	}

	public void setACCOUNT_POID(String aCCOUNT_POID) {
		ACCOUNT_POID = aCCOUNT_POID;
	}

	public String getSERVICE_OBJ() {
		return SERVICE_OBJ;
	}

	public void setSERVICE_OBJ(String sERVICE_OBJ) {
		SERVICE_OBJ = sERVICE_OBJ;
	}

	public String getADDRESS() {
		return ADDRESS;
	}

	public void setADDRESS(String aDDRESS) {
		ADDRESS = aDDRESS;
	}

	public String getBANK_NAME() {
		return BANK_NAME;
	}

	public void setBANK_NAME(String bANK_NAME) {
		BANK_NAME = bANK_NAME;
	}

	public String getCASH_DATE() {
		return CASH_DATE;
	}

	public void setCASH_DATE(String cASH_DATE) {
		CASH_DATE = cASH_DATE;
	}

	public String getCHEQUE_NUMBER() {
		return CHEQUE_NUMBER;
	}

	public String getPAID_AMOUNT() {
		return PAID_AMOUNT;
	}

	public void setPAID_AMOUNT(String pAID_AMOUNT) {
		PAID_AMOUNT = pAID_AMOUNT;
	}

	public void setCHEQUE_NUMBER(String cHEQUE_NUMBER) {
		CHEQUE_NUMBER = cHEQUE_NUMBER;
	}

	public String getCHEQUE_DATE() {
		return CHEQUE_DATE;
	}

	public void setCHEQUE_DATE(String cHEQUE_DATE) {
		CHEQUE_DATE = cHEQUE_DATE;
	}

	public String getCITY() {
		return CITY;
	}

	public void setCITY(String cITY) {
		CITY = cITY;
	}

	public String getMSO_FLD_AGREEMENT_NO() {
		return MSO_FLD_AGREEMENT_NO;
	}

	public void setMSO_FLD_AGREEMENT_NO(String mSO_FLD_AGREEMENT_NO) {
		MSO_FLD_AGREEMENT_NO = mSO_FLD_AGREEMENT_NO;
	}

	public String getCURRENT_BALANCE_INC_PENDING_CHARGES() {
		return CURRENT_BALANCE_INC_PENDING_CHARGES;
	}

	public void setCURRENT_BALANCE_INC_PENDING_CHARGES(
			String cURRENT_BALANCE_INC_PENDING_CHARGES) {
		CURRENT_BALANCE_INC_PENDING_CHARGES = cURRENT_BALANCE_INC_PENDING_CHARGES;
	}

	public String getCUSTOMER_NAME() {
		return CUSTOMER_NAME;
	}

	public void setCUSTOMER_NAME(String cUSTOMER_NAME) {
		CUSTOMER_NAME = cUSTOMER_NAME;
	}

	public String getDUE_DATE() {
		return DUE_DATE;
	}

	public void setDUE_DATE(String dUE_DATE) {
		DUE_DATE = dUE_DATE;
	}

	public String getEMAIL_ID() {
		return EMAIL_ID;
	}

	public void setEMAIL_ID(String eMAIL_ID) {
		EMAIL_ID = eMAIL_ID;
	}

	public String getLAST_INVOICE() {
		return LAST_INVOICE;
	}

	public void setLAST_INVOICE(String lAST_INVOICE) {
		LAST_INVOICE = lAST_INVOICE;
	}

	public String getMOBILE_NO() {
		return MOBILE_NO;
	}

	public void setMOBILE_NO(String mOBILE_NO) {
		MOBILE_NO = mOBILE_NO;
	}

	public String getPAY_TYPE() {
		return PAY_TYPE;
	}

	public void setPAY_TYPE(String pAY_TYPE) {
		PAY_TYPE = pAY_TYPE;
	}

	public String getPLAN_NAME() {
		return PLAN_NAME;
	}

	public void setPLAN_NAME(String pLAN_NAME) {
		PLAN_NAME = pLAN_NAME;
	}

	public String getPREVIOUS_OUTSTANDING() {
		return PREVIOUS_OUTSTANDING;
	}

	public void setPREVIOUS_OUTSTANDING(String pREVIOUS_OUTSTANDING) {
		PREVIOUS_OUTSTANDING = pREVIOUS_OUTSTANDING;
	}

	public String getSTATE() {
		return STATE;
	}

	public void setSTATE(String sTATE) {
		STATE = sTATE;
	}
	
	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}

	public String getALTERNATE_MOBILE_NO() {
		return ALTERNATE_MOBILE_NO;
	}

	public void setALTERNATE_MOBILE_NO(String aLTERNATE_MOBILE_NO) {
		ALTERNATE_MOBILE_NO = aLTERNATE_MOBILE_NO;
	}

	public String getALTERNATE_EMAIL_ID() {
		return ALTERNATE_EMAIL_ID;
	}

	public void setALTERNATE_EMAIL_ID(String aLTERNATE_EMAIL_ID) {
		ALTERNATE_EMAIL_ID = aLTERNATE_EMAIL_ID;
	}

	public String getT_TYPE() {
		return T_TYPE;
	}

	public void setT_TYPE(String t_TYPE) {
		T_TYPE = t_TYPE;
	}

	public String getT_ID() {
		return T_ID;
	}

	public void setT_ID(String t_ID) {
		T_ID = t_ID;
	}

	public String getT_STATUS() {
		return T_STATUS;
	}

	public void setT_STATUS(String t_STATUS) {
		T_STATUS = t_STATUS;
	}

	public String getT_ORIG_AMOUNT() {
		return T_ORIG_AMOUNT;
	}

	public void setT_ORIG_AMOUNT(String t_ORIG_AMOUNT) {
		T_ORIG_AMOUNT = t_ORIG_AMOUNT;
	}

	public String getT_FIXED_AMOUNT() {
		return T_FIXED_AMOUNT;
	}

	public void setT_FIXED_AMOUNT(String t_FIXED_AMOUNT) {
		T_FIXED_AMOUNT = t_FIXED_AMOUNT;
	}

	public String getT_TAXED_AMOUNT() {
		return T_TAXED_AMOUNT;
	}

	public void setT_TAXED_AMOUNT(String t_TAXED_AMOUNT) {
		T_TAXED_AMOUNT = t_TAXED_AMOUNT;
	}

	public String getT_TOTAL_AMOUNT() {
		return T_TOTAL_AMOUNT;
	}

	public void setT_TOTAL_AMOUNT(String t_TOTAL_AMOUNT) {
		T_TOTAL_AMOUNT = t_TOTAL_AMOUNT;
	}

	public String getT_PAYABLE_AMOUNT() {
		return T_PAYABLE_AMOUNT;
	}

	public void setT_PAYABLE_AMOUNT(String t_PAYABLE_AMOUNT) {
		T_PAYABLE_AMOUNT = t_PAYABLE_AMOUNT;
	}

	public String getCARD_TRANSACTION_ID() {
		return CARD_TRANSACTION_ID;
	}

	public void setCARD_TRANSACTION_ID(String cARD_TRANSACTION_ID) {
		CARD_TRANSACTION_ID = cARD_TRANSACTION_ID;
	}
	
	public String getLATITUDE() {
		return LATITUDE;
	}

	public void setLATITUDE(String lATITUDE) {
		LATITUDE = lATITUDE;
	}

	public String getLONGITUDE() {
		return LONGITUDE;
	}

	public void setLONGITUDE(String lONGITUDE) {
		LONGITUDE = lONGITUDE;
	}

	public String getCashXMLString(LoginValidator validator, String userID) {
		
		//Date startDate = new Date();
		UUID uuid = UUID.randomUUID();
		String uniqueTransactionID = Long.toString(uuid.getLeastSignificantBits(), 1);
		uniqueTransactionID = uniqueTransactionID.replace("-", "FOS_");
		setUniqueTransactionID(uniqueTransactionID);
		/*String uniqueTransactionID = (UUID.randomUUID()).toString().toUpperCase();
		uniqueTransactionID = "FOS_"+uniqueTransactionID;
		setUniqueTransactionID(uniqueTransactionID);*/
		
		/*Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTime(startDate);
		endDateCalendar.add(Calendar.DATE, 30); // add 30 days

		Date endDate = endDateCalendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");*/
		SimpleDateFormat cashDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		SimpleDateFormat utcCashDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'");
		long cashDateInMillis = 0;
		try {
			if(getCASH_DATE().contains("T"))
				cashDateInMillis = utcCashDateFormat.parse(getCASH_DATE()).getTime()/1000;
			else
				cashDateInMillis = cashDateFormat.parse(getCASH_DATE()).getTime()/1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		StringBuffer cashStringBuffer = new StringBuffer();
		cashStringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		cashStringBuffer.append("<MSO_OP_PYMT_COLLECT_inputFlist>");
		/*cashStringBuffer.append("<MSO_FLD_PYMT_CHANNEL>5</MSO_FLD_PYMT_CHANNEL>");*/
		cashStringBuffer.append("<MSO_FLD_PYMT_CHANNEL>2</MSO_FLD_PYMT_CHANNEL>");
		cashStringBuffer.append("<MSO_FLD_SERVICE_TYPE>0</MSO_FLD_SERVICE_TYPE>");
		cashStringBuffer.append("<CHARGES elem=\'0\'>");
		cashStringBuffer.append("<ACCOUNT_OBJ>"+getACCOUNT_OBJ()+"</ACCOUNT_OBJ>");
		cashStringBuffer.append("<AMOUNT>"+getPAID_AMOUNT()+"</AMOUNT>");
		cashStringBuffer.append("<COMMAND>0</COMMAND>");
		cashStringBuffer.append("<PAYMENT>");
		cashStringBuffer.append("<INHERITED_INFO>");
		cashStringBuffer.append("<CASH_INFO elem=\'0\'>");
		cashStringBuffer.append("<RECEIPT_NO>"+uniqueTransactionID+"</RECEIPT_NO>");
		cashStringBuffer.append("<EFFECTIVE_T>"+cashDateInMillis+"</EFFECTIVE_T>");
		cashStringBuffer.append("</CASH_INFO>");
		cashStringBuffer.append("</INHERITED_INFO>");
		cashStringBuffer.append("</PAYMENT>");
		cashStringBuffer.append("<PAY_TYPE>10011</PAY_TYPE>");
		cashStringBuffer.append("</CHARGES><DESCR>"+getREMARKS()+"</DESCR>");
		cashStringBuffer.append("<POID>"+getACCOUNT_POID()+"</POID>");
		//cashStringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
		cashStringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"</PROGRAM_NAME>");
		cashStringBuffer.append("<TYPE>0</TYPE>");
		//cashStringBuffer.append("<USERID>0.0.0.1 /account 5508823614 0</USERID>");
		cashStringBuffer.append("<MSO_FLD_AGREEMENT_NO>"+getMSO_FLD_AGREEMENT_NO()+"</MSO_FLD_AGREEMENT_NO>");
		cashStringBuffer.append("<SERVICE_OBJ>"+getSERVICE_OBJ()+"</SERVICE_OBJ>");
		//cashStringBuffer.append("<USERID>0.0.0.1 /account 115000 0</USERID>");
		cashStringBuffer.append("<USERID>"+userID+"</USERID>");
		cashStringBuffer.append("</MSO_OP_PYMT_COLLECT_inputFlist>");
		String xmlString = cashStringBuffer.toString();
		return xmlString;
	}

	public String getChequeXMLString(LoginValidator validator, String userID) {

		String uniqueTransactionID = (UUID.randomUUID()).toString().toUpperCase();
		uniqueTransactionID = "FOS_"+uniqueTransactionID;
		setUniqueTransactionID(uniqueTransactionID);
		
		/*Date startDate = new Date();
		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTime(startDate);
		endDateCalendar.add(Calendar.DATE, 30); // add 30 days
		Date endDate = endDateCalendar.getTime();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");*/
		SimpleDateFormat chequeDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat utcChequeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'");
		long chequeDateInMillis = 0;
		try {
			if(getCHEQUE_DATE().contains("T"))
				chequeDateInMillis = utcChequeDateFormat.parse(getCHEQUE_DATE()).getTime()/1000;
			else
				chequeDateInMillis = chequeDateFormat.parse(getCHEQUE_DATE()).getTime()/1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String bankName = getBANK_NAME();
		try {
			bankName = URLDecoder.decode(bankName,"UTF-8").replace("&", "&amp;");
			bankName = bankName.replace(" &amp; ", "&amp;");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuffer chequeStringBuffer = new StringBuffer();
		chequeStringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		chequeStringBuffer.append("<MSO_OP_PYMT_COLLECT_inputFlist>");
		/*chequeStringBuffer.append("<MSO_FLD_PYMT_CHANNEL>5</MSO_FLD_PYMT_CHANNEL>");*/
		chequeStringBuffer.append("<MSO_FLD_PYMT_CHANNEL>2</MSO_FLD_PYMT_CHANNEL>");
		chequeStringBuffer.append("<MSO_FLD_SERVICE_TYPE>0</MSO_FLD_SERVICE_TYPE>");
		chequeStringBuffer.append("<CHARGES elem=\'0\'>");
		chequeStringBuffer.append("<ACCOUNT_OBJ>"+getACCOUNT_OBJ()+"</ACCOUNT_OBJ>");
		chequeStringBuffer.append("<AMOUNT>"+getPAID_AMOUNT()+"</AMOUNT>");
		chequeStringBuffer.append("<COMMAND>0</COMMAND>");
		chequeStringBuffer.append("<PAYMENT><INHERITED_INFO>");
		chequeStringBuffer.append("<CHECK_INFO elem=\'0\'>");
		chequeStringBuffer.append("<BANK_ACCOUNT_NO></BANK_ACCOUNT_NO>");
		chequeStringBuffer.append("<BANK_CODE></BANK_CODE>");
		chequeStringBuffer.append("<CHECK_NO>"+getCHEQUE_NUMBER()+"</CHECK_NO>");
		chequeStringBuffer.append("<BRANCH_NO></BRANCH_NO>");
		chequeStringBuffer.append("<BANK_NAME>"+bankName+"</BANK_NAME>");
		chequeStringBuffer.append("<RECEIPT_NO>"+uniqueTransactionID+"</RECEIPT_NO>");
		chequeStringBuffer.append("<EFFECTIVE_T>"+chequeDateInMillis+"</EFFECTIVE_T>");
		chequeStringBuffer.append("</CHECK_INFO>");
		chequeStringBuffer.append("</INHERITED_INFO>");
		chequeStringBuffer.append("</PAYMENT>");
		chequeStringBuffer.append("<PAY_TYPE>10012</PAY_TYPE>");
		chequeStringBuffer.append("</CHARGES>");
		chequeStringBuffer.append("<DESCR>"+getREMARKS()+"</DESCR>");
		chequeStringBuffer.append("<POID>"+getACCOUNT_POID()+"</POID>");
		//chequeStringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
		chequeStringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"</PROGRAM_NAME>");
		chequeStringBuffer.append("<TYPE>0</TYPE>");
		//chequeStringBuffer.append("<USERID>0.0.0.1 /account 5508823614 0</USERID>");
		chequeStringBuffer.append("<MSO_FLD_AGREEMENT_NO>"+getMSO_FLD_AGREEMENT_NO()+"</MSO_FLD_AGREEMENT_NO>");
		chequeStringBuffer.append("<SERVICE_OBJ>"+getSERVICE_OBJ()+"</SERVICE_OBJ>");
		//chequeStringBuffer.append("<USERID>0.0.0.1 /account 115000 0</USERID>");
		chequeStringBuffer.append("<USERID>"+userID+"</USERID>");
		chequeStringBuffer.append("</MSO_OP_PYMT_COLLECT_inputFlist>");
		String xmlString = chequeStringBuffer.toString();
		return xmlString;
	}
	
	public String getCardXMLString(LoginValidator validator, String userID) {

		/*String uniqueTransactionID = "FOS_"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"_"+getCARD_TRANSACTION_ID();
		setUniqueTransactionID(uniqueTransactionID);*/
		
		String uniqueTransactionID = (UUID.randomUUID()).toString().toUpperCase();
		uniqueTransactionID = "FOS_"+uniqueTransactionID;
		setUniqueTransactionID(uniqueTransactionID);
		
		SimpleDateFormat cashDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		SimpleDateFormat utcCashDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'");
		long cardDateInMillis = 0;
		try {
			if(getCASH_DATE().contains("T"))
				cardDateInMillis = utcCashDateFormat.parse(getCASH_DATE()).getTime()/1000;
			else
				cardDateInMillis = cashDateFormat.parse(getCASH_DATE()).getTime()/1000;
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		StringBuffer cardStringBuffer = new StringBuffer();
		cardStringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		cardStringBuffer.append("<MSO_OP_PYMT_COLLECT_inputFlist>");
		cardStringBuffer.append("<MSO_FLD_PYMT_CHANNEL>9</MSO_FLD_PYMT_CHANNEL>");
		cardStringBuffer.append("<MSO_FLD_SERVICE_TYPE>0</MSO_FLD_SERVICE_TYPE>");
		cardStringBuffer.append("<CHARGES elem=\'0\'>");
		cardStringBuffer.append("<ACCOUNT_OBJ>"+getACCOUNT_OBJ()+"</ACCOUNT_OBJ>");
		cardStringBuffer.append("<AMOUNT>"+getPAID_AMOUNT()+"</AMOUNT>");
		cardStringBuffer.append("<COMMAND>0</COMMAND>");
		cardStringBuffer.append("<PAYMENT><INHERITED_INFO>");
		cardStringBuffer.append("<WIRE_TRANSFER_INFO elem=\'0\'>");
		cardStringBuffer.append("<BANK_ACCOUNT_NO></BANK_ACCOUNT_NO>");
		cardStringBuffer.append("<BANK_CODE></BANK_CODE>");
		cardStringBuffer.append("<EFFECTIVE_T>"+cardDateInMillis+"</EFFECTIVE_T>");
		cardStringBuffer.append("<WIRE_TRANSFER_ID>"+uniqueTransactionID+"</WIRE_TRANSFER_ID>");
		cardStringBuffer.append("</WIRE_TRANSFER_INFO>");
		cardStringBuffer.append("</INHERITED_INFO>");
		cardStringBuffer.append("</PAYMENT>");
		cardStringBuffer.append("<PAY_TYPE>10013</PAY_TYPE>");
		cardStringBuffer.append("</CHARGES>");
		cardStringBuffer.append("<DESCR>CARD TRANSACTION ID: "+getCARD_TRANSACTION_ID()+", REMARKS: "+getREMARKS()+"</DESCR>");
		cardStringBuffer.append("<POID>"+getACCOUNT_POID()+"</POID>");
		//cardStringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"_"+validator.getDeviceIMEI()+"</PROGRAM_NAME>");
		cardStringBuffer.append("<PROGRAM_NAME>FWFOS|"+validator.getUserID()+"</PROGRAM_NAME>");
		cardStringBuffer.append("<TYPE>0</TYPE>");
		//cardStringBuffer.append("<USERID>0.0.0.1 /account 5508823614 0</USERID>");
		cardStringBuffer.append("<MSO_FLD_AGREEMENT_NO>"+getMSO_FLD_AGREEMENT_NO()+"</MSO_FLD_AGREEMENT_NO>");
		cardStringBuffer.append("<SERVICE_OBJ>"+getSERVICE_OBJ()+"</SERVICE_OBJ>");
		//cardStringBuffer.append("<USERID>0.0.0.1 /account 115000 0</USERID>");
		cardStringBuffer.append("<USERID>"+userID+"</USERID>");
		cardStringBuffer.append("</MSO_OP_PYMT_COLLECT_inputFlist>");
		String xmlString = cardStringBuffer.toString();
		return xmlString;
	}
}
