package com.rifluxyss.therenoking.utils;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class RenoPreferences {
	
	private static final String strRegisterID = "gcm-registerId";
	private static final String strTimeStamp = "";

	
	public RenoPreferences(){		
	}
	public static void setNumber (Context mActivity, String phone_number){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("phone_number", phone_number);
		editor.commit();	
	}
	
	public static String getNumber (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("phone_number", "");	
	}
	
	public static void setNotify (Context mActivity, boolean notify){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("notify", notify);
		editor.commit();	
	}
	
	public static boolean getNotify (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean("notify", true);
	}
	
	public static void setNotificationOffTime (Context mActivity, long time){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("off_time", time);
		editor.commit();	
	}
	
	public static long getNotificationOffTime (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getLong("off_time", Calendar.getInstance().getTimeInMillis());
	}
	
	public static void setCalledId (Context mActivity, String percentage){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("called_id", percentage);
		editor.commit();	
	}
	
	public static String getCalledId (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("called_id", "");
	}
	
	public static void setWorkingFromTime (Context mActivity, String fromtime){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("from_time", fromtime);
		editor.commit();	
	}
	
	public static String getWorkingFromTime (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("from_time", "8:00");
	}
	
	public static void setWorkingToTime (Context mActivity, String totime){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("to_time", totime);
		editor.commit();	
	}
	
	public static String getWorkingToTime (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("to_time", "16:00");
	}
	
	
	public static boolean setGCMRegisterID(Context mActivity, String register_id) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(strRegisterID, register_id);
		return editor.commit();
	}

	public static String getGCMRegisterID(Context mActivity) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		return prefs.getString(strRegisterID, "");
	}
	
	public static boolean setTimeStamp(Context mActivity, String time_stamp) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(strTimeStamp, time_stamp);
		return editor.commit();
	}

	public static String getTimeStamp(Context mActivity) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		return prefs.getString(strTimeStamp, "");
	}
	
	public static void setStatus (Context mActivity, String strStatus){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("status", strStatus);
		editor.commit();	
	}
	
	public static String getStatus (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("status", "");
	}
	
	public static void setNewjobEventId (Context mActivity, String strStatus){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("new_job_eventid", strStatus);
		editor.commit();	
	}
	
	public static String getNewjobEventId (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("new_job_eventid", "");
	}
	
	public static void setJobCompletionEventId(Context mActivity, String strStatus){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("job_completion_event_id", strStatus);
		editor.commit();	
	}
	
	public static String getJobCompletionEventId (Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("job_completion_event_id", "");
	}
	
}
