package com.yy.kaitian.yiliao681.utils;

/**============================================================
 * 版权： 久其软件 版权所有 (c) 
 * 包： com.jiuqi.muchmore.clothing.common.tools
 * 修改记录：
 * 日期                作者           内容
 * =============================================================
 * 2012-6-6       Administrator        
 * ============================================================*/

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 时间格式化类
 * </p>
 * 
 * @author Administrator
 * @version 2012-6-6
 */

public class MyDateUtil {

	/**
	 * yyyy-MM-dd
	 */
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	/**
	 * yyyy-MM-dd
	 */
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	/**
	 * yy-MM-dd
	 */
	public static final String YY_MM_DD = "yy-MM-dd";
	/**
	 * MM/dd HH:mm:ss
	 */
	public static final String MM_DD_HH_MM_SS = "MM/dd HH:mm:ss";
	/**
	 * yyyy-MM-dd HH:mm
	 */
	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	/**
	 * MM/dd HH:mm
	 */
	public static final String MM_DD_HH_MM = "MM/dd HH:mm";

	/**
	 * MM-dd
	 */
	public static final String MM_DD = "MM-dd";

	/**
	 * MM月 dd日 HH:mm
	 */
	public static final String MM_DD_HH_MM1 = "MM月dd日 HH:mm";

	/**
	 * 根据long值，转化为yyyy-MM-dd hh:mm的格式
	 * 
	 * @param timelong
	 * @return String
	 */
	public static String getTimeStamp(long timelong) {
		Date d = new Date(timelong);
		SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
		return df.format(d);
	}

	/**
	 * timepatten就是SimpleDateFormat用到的patten，比如："yyyy-MM-dd HH:mm"
	 * 
	 * @param timelong
	 * @param timepatten
	 * @return String
	 */
	public static String getTimeStamp(long timelong, String timepatten) {
		Date d = new Date(timelong);
		SimpleDateFormat df = new SimpleDateFormat(timepatten);
		return df.format(d);
	}

	/**
	 * timepatten就是SimpleDateFormat用到的patten，比如："yyyy-MM-dd HH:mm"
	 * 
	 * @param
	 * @param timepatten
	 * @return String
	 */
	public static String getTimeStamp(Date date, String timepatten) {
		SimpleDateFormat df = new SimpleDateFormat(timepatten);
		return df.format(date);
	}
	
	/**
	 * 根据年月日的值取得时间片
	 * 
	 * @param mYear
	 * @param mMonth
	 *            自动减去1900
	 * @param mDay
	 * @return long
	 */
	public static long getTimeStampFromYearMonthDay(int mYear, int mMonth,
			int mDay) {
		Date dt = new Date();
		dt.setYear(mYear - 1900);
		dt.setMonth(mMonth);
		dt.setDate(mDay);
		return dt.getTime();
	}

	/**
	 * 解析字符串，返回Date对象，字符串格式默认为yyyy-MM-dd；如果解析出现异常，返回null。
	 * 
	 * @param strDate
	 * @return Date
	 */
	public static Date parseDate(String strDate) {
		return parseDate(strDate, MyDateUtil.YYYY_MM_DD);
	}

	/**
	 * 解析字符串，返回Date对象，可以传入字符串格式进行解析，建议使用DateUtil内置的字符串格式。如果解析出现异常，返回null。
	 * 
	 * @param strDate
	 * @return Date
	 */
	public static Date parseDate(String strDate, String dateFormat) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 根据年月日的值取得时间片
	 * 
	 * @param mYear 自动减去1900
	 * @param mMonth
	 * @param mDay
	 * @return long
	 */
	public static long getTimeStampFromYearMonthDay(int mYear, int mMonth,
			int mDay, int mHour, int mMin) {
		Date dt = new Date();
		dt.setYear(mYear);
		dt.setMonth(mMonth);
		dt.setDate(mDay);
		dt.setHours(mHour);
		dt.setMinutes(mMin);
		return dt.getTime();
	}

	/**
	 * @param dayOfMonth
	 * @param month
	 * @param year
	 * 
	 */
	public static boolean compareDateIsLessThanNow(int year, int month,
			int dayOfMonth) {
		Date dt = new Date();
		int  nowYear = dt.getYear();
		int  nowMonth = dt.getMonth();
		int  nowDayOfMonth = dt.getDate();
		if(year<nowYear){
			return true;
		}
		if(month<nowMonth){
			return true;
		}
		if(dayOfMonth<nowDayOfMonth){
			return true;
		}
		return false;
	}

	public static boolean isEndDateBeforeStartDate(String startDate,String endDate) {
		double start = 0;
		double end = 0;
		try {
			start = Double.valueOf(startDate);
			end = Double.valueOf(endDate);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int  j = startDate.compareToIgnoreCase(endDate);
		Log.e("jieguo ", j+"");
		int i = (start>end)?2:0;
		if(i>0){
			return true;
		}else{
			return false;
		}
	}

}
