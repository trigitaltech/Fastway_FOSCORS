package com.FOS.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	public static String getCurDateString()
	{
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Date today = Calendar.getInstance().getTime();        
		String curDate = df.format(today);
		////System.out.println("Report Date: " + reportDate);
		return curDate;
	}
}
