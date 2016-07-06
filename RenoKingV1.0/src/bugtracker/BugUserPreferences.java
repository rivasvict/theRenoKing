package bugtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BugUserPreferences {
	public BugUserPreferences(){
	}
	
	
	public static void setDebugMode (Context mActivity, String debug_mode){
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = userPreference.edit();
		editor.putString("debug_mode", debug_mode); // value to store		
		editor.commit();	
	}
	
	public static String getDebugMode(Context mActivity){
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(mActivity);
		return userPreference.getString("debug_mode", "0");	
	}
	
	public static void setProjectId (Context mActivity, String project_id){
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = userPreference.edit();
		editor.putString("project_id", project_id); // value to store		
		editor.commit();	
	}
	
	public static String getProjectId(Context mActivity){
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(mActivity);
		return userPreference.getString("project_id", "");	
	}
	public static void setMaximumException (Context mActivity, String max_exception){
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = userPreference.edit();
		editor.putString("max_exception", max_exception); // value to store		
		editor.commit();	
	}
	
	public static String getMaximumException(Context mActivity){
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(mActivity);
		return userPreference.getString("max_exception", "0");	
	}
	public static void setMinimumException (Context mActivity, String min_exception){
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = userPreference.edit();
		editor.putString("min_exception", min_exception); // value to store		
		editor.commit();	
	}
	
	public static String getMinimumException(Context mActivity){
		SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(mActivity);
		return userPreference.getString("min_exception", "0");	
	}
	
}