package com.rifluxyss.therenoking.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.rifluxyss.therenoking.ProspectDetails;
import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.generic.TimeConversion;

public class Utilities {
	public static final String inputFormat = "HH:mm";
	
	/** GCM Integration **/
	public static String SENDER_ID = "398444970111";
	
	static SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.getDefault());
	
	public static boolean haveInternet(Activity thisActivity) {
		NetworkInfo info = ((ConnectivityManager) thisActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			return true;
		}
		return true;
	}
	
	public static String getDeviceID(Context thisActivity) {
		TelephonyManager tManager = (TelephonyManager) thisActivity
				.getSystemService(Context.TELEPHONY_SERVICE);
		String id = tManager.getDeviceId();
		if (id == null || (id != null && id.equals("")) && id.equals("9774d56d682e549c"))
			id = getUniqueID(thisActivity, tManager);
				
		return id;
	}	
    
	private static String getUniqueID(Context thisActivity, TelephonyManager tm) {		

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						thisActivity.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		return deviceId;
	}
	
	public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	
	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	          "\\@" +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	          "(" +
	          "\\." +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	          ")+"
	 );
	
	public static Message getMessage(int handle_enum) {
		Message msg = new Message();
		msg.what = handle_enum;				
		return msg;
	}
	
	public static String getNodeValue(Document doc, String node) {
		String node_value = null;
		if (doc.getElementsByTagName(node).item(0) != null)
			node_value = doc.getElementsByTagName(node).item(0).getChildNodes().item(0).getNodeValue();
		Log.v("", "Node Value === >"+node_value);
		return node_value;
	}	
	
	public static void showActivity(Activity sourceScreen, Class<?> cls) {
		Intent i = new Intent(sourceScreen, cls);
		sourceScreen.startActivity(i);
		sourceScreen.finish();
	}
	
	public static void showNavigationAlert(final Activity thisActivity,
			String alertMsg, final Class<?> cls) {
		try {
			new AlertDialog.Builder(thisActivity)
					.setTitle("The RenoKing")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									showActivity(thisActivity, cls);
								}
							}).setMessage("" + alertMsg).create().show();
			return;
		} catch (Exception e) {
			Log.e("Alert error", "Alert Err: " + e.toString());
		}
	}
	
	public static void showAlert(final Activity thisActivity, String alertMsg) {
		try {
			new AlertDialog.Builder(thisActivity)					
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							}).setMessage("" + alertMsg).create().show();
			return;
		} catch (Exception e) {
			Log.e("Alert error", "Alert Err: " + e.toString());
		}
	}	
	
	public static void showNotifyAlert(final Context thisActivity, String alertMsg) {
		
		try {
			new AlertDialog.Builder(thisActivity)					
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							}).setMessage("" + alertMsg).create().show();
			return;
		} catch (Exception e) {
			Log.e("Alert error", "Alert Err: " + e.toString());
		}
	}	
	
	@SuppressLint("NewApi")
	public static boolean getSmallScreen(Activity act){
		Display display = act.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		int width = 0;
		int height = 0;
		if (android.os.Build.VERSION.SDK_INT >= 13){
			display.getSize(size);
			width = size.x;
			height = size.y;
		}else{
			width = display.getWidth(); 
			height = display.getHeight();
		}
//		DisplayMetrics displaymetrics = new DisplayMetrics(); 
//		act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//		height = displaymetrics.heightPixels;
//		width = displaymetrics.widthPixels; 
		if (width <= 240 && height <= 320) 
		{ 
			return true;
		} else{
			return false;
		}
	}
	
//	private static final String DATABASE_NAME = "renoking-v1.9.db";	
	public static File getBackupDatabaseFile() {
	    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/backup");
	    Log.v("", "Renoking database back up path: " +dir.getAbsolutePath());
	    if (!dir.exists()) {
	        dir.mkdirs();
	    }
	    return new File(dir, DatabaseConnection.DB_NAME);
	}
	
	public static boolean backupDatabase() {
	    File from = new File("/data/data/com.rifluxyss.therenoking/databases/", DatabaseConnection.DB_NAME);
	    File to = getBackupDatabaseFile();
	    try {
	        copyFile(from, to);
	        return true;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    return false;
	}
	
	public static void copyFile(File src, File dst) throws IOException {
	    FileInputStream in = new FileInputStream(src);
	    FileOutputStream out = new FileOutputStream(dst);
	    FileChannel fromChannel = null, toChannel = null;
	    try {
	        fromChannel = in.getChannel();
	        toChannel = out.getChannel();
	        fromChannel.transferTo(0, fromChannel.size(), toChannel); 
	    } finally {
	        if (fromChannel != null) 
	            fromChannel.close();
	        if (toChannel != null) 
	            toChannel.close();
	    }
	}
	
	public static String getTagValue(String sTag, Element eElement) {
		String str;
		NodeList nlList = null;
		Node nValue = null;
		if(eElement.getElementsByTagName(sTag).item(0)!=null){
			nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
			 nValue = (Node) nlList.item(0);
		}	
		
		if (nValue == null)
			str = "";
		else{
			if (nValue.getNodeValue() == null)
				str = "";
			else
				str = nValue.getNodeValue();
		}
		
		return str;
	}
	
	public static String getMinute(int minute){
		String digit = String.valueOf(minute);
		if(digit.length() == 1)
			digit = "0" + digit;
		
		return digit;
		
	}
	
	public static String getNotificationMessage(Context thisCtx, int category, String name){
 		String title = null;
 		switch (category) {
 		case ProspectDetails.STAGE2_SCHEDULE_APT:
// 			title = thisCtx.getString(R.string.notify_pls_confirm_appointment);
 			break;
 		case ProspectDetails.STAGE2_FOLLOWUP_APT:
 			title = thisCtx.getString(R.string.notify_followup_appointment);
 			break;
 		case ProspectDetails.STAGE2_RESCHEDULE_APT:
 			title = thisCtx.getString(R.string.notify_reschedule_appointment);
 			break;
 		case ProspectDetails.STAGE2_CANCEL_APT_FORNOW:
 			title = thisCtx.getString(R.string.notify_followup_appointment);
 			break;
 		case ProspectDetails.STAGE2_CONFIRM_APT:
 			title = thisCtx.getString(R.string.notify_estimate_due_date);
 			break;
 		case ProspectDetails.STAGE3_ESTIMATE_DATE: 			
// 			title = String.format(thisCtx.getString(R.string.notify_estimate_completed), name);
 			title =thisCtx.getString(R.string.notify_estimate_completed);
 			break;
 		case ProspectDetails.STAGE3_WHEN_ESTIMATE_COMPLETED:
 			title = String.format(thisCtx.getString(R.string.notify_estimate_completed), name);
 			break;
 		case ProspectDetails.STAGE4_PROJECT_START:
// 			title = String.format(getString(R.string.notify_estimate_completed), prospect.name);
 			break;
 		case ProspectDetails.STAGE5_FINISH_DATE:
 			title = thisCtx.getString(R.string.notify_project_finish);
 			break;
 		default:
 			break;
 		}
 		return title;
 	}
	
	public static long getTimeInterval(long time, int category,Context thisActivity){
		long interval = 0L; 
		Log.e("getTimeInterval", "STAGE_CATEGORY: "+category);
		Log.e("getTimeInterval", "TIME: "+time);
		switch (category) {
		case ProspectDetails.STAGE2_SCHEDULE_APT:
			interval = time - (TimeConversion.ONE_HOUR * 2); // live
//			interval = time - (TimeConversion.ONE_MINUTE * 2); // demo
			break;
		case ProspectDetails.STAGE2_RESCHEDULE_APT:
			interval = time - (TimeConversion.ONE_HOUR * 2);  // live
//			interval = time - (TimeConversion.ONE_MINUTE * 2); // demo
			break;
		case ProspectDetails.STAGE2_FOLLOWUP_APT:
			//interval = time - (TimeConversion.ONE_HOUR * 2); // live			
			interval = time; // live
//			interval = time  - (TimeConversion.ONE_MINUTE * 2); // demo
			break;
		case ProspectDetails.STAGE2_CONFIRM_APT:
//			interval = time + (TimeConversion.ONE_MINUTE * 15); // live
			interval = time + (TimeConversion.ONE_MINUTE * 1); // demo
			break;
		case ProspectDetails.STAGE2_CANCEL_APT_FORNOW:
			interval = time;
			break;
		case ProspectDetails.STAGE3_ESTIMATE_DATE:
			interval = time - (TimeConversion.ONE_HOUR * 48); // live
//			interval = time - (TimeConversion.ONE_MINUTE * 1); // demo
			break;
		case ProspectDetails.STAGE3_WHEN_ESTIMATE_COMPLETED:
//			interval = time + (TimeConversion.ONE_HOUR * 48); // live
			interval = time + (TimeConversion.ONE_MINUTE * 1); // demo
			break;
		case ProspectDetails.STAGE4_PROJECT_START:
			interval = time - (TimeConversion.ONE_HOUR * 24); // live
//			interval = time - (TimeConversion.ONE_MINUTE * 3); // demo
			break;
		case ProspectDetails.STAGE5_FINISH_DATE:
//			interval = time + (TimeConversion.ONE_DAY * 30); // Live Previous	
//			interval = time +(TimeConversion.ONE_MINUTE * 1); // demo			
			interval = time;
			break;
		case ProspectDetails.STAGE6_CUSTOMERCARE_FOLLOWUP:			
//			interval = time + (TimeConversion.ONE_DAY * 30); // live changed as per flow chart
			interval = time + (TimeConversion.ONE_MINUTE * 1);   // demo
			break;
		case ProspectDetails.STAGE6_CUSTOMERCARE_SCHEDULE:			
			interval = time - (TimeConversion.ONE_HOUR * 2); // live changed as per flow chart
//			interval = time - (TimeConversion.ONE_MINUTE * 2);   // demo
			break;
		case ProspectDetails.STAGE6_CUSTOMERCARE_RESCHEDULE:
//			interval = time + (TimeConversion.ONE_DAY * 7); // live changed as per flow chart
			interval = time + (TimeConversion.ONE_MINUTE * 1);   // demo
			break;
		default:
			interval = time + TimeConversion.ONE_MINUTE;
			break;
		}
		
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(interval);
	    Log.e("getTimeInterval", "updated date"+dateFormat.format(calendar.getTime()));
	   
	/*    if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY  || 
        		calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
    			Log.e("getTimeInterval", "updated date is in weekend");
    			interval = checkDOW(calendar,thisActivity);
        }else{
        	if(checkUpdatedTime(calendar, thisActivity)){
    			Log.e("getTimeInterval", "updated date is in weektime");
    			return interval;
        	}else{
        		Log.e("getTimeInterval", "updated date is in otherthan weektime");
        		interval = checkDOW(calendar,thisActivity);
        	}
        }*/
	    	    
		return interval;
	}
	
	public static long getModifiedInterval(long interval, Activity thisActivity){ 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(interval);
	    Log.e("getTimeInterval", "updated date"+dateFormat.format(calendar.getTime()));
	   
	    /*if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY  || 
        		calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
    			Log.e("getTimeInterval", "updated date is in weekend");
    			interval = checkDOW(calendar,thisActivity);
        }else{
        	if(checkUpdatedTime(calendar, thisActivity)){
    			Log.e("getTimeInterval", "updated date is in weektime");
    			return interval;
        	}else{
        		Log.e("getTimeInterval", "updated date is in otherthan weektime");
        		interval = checkDOW(calendar,thisActivity);
        	}
        }*/
	    	    
		return interval;
	}
	
/*	public static long checkDOW(final Calendar calendar,final Context thisActivity){
		
		if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY  || 
        		calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
    		Log.e("getTimeInterval", "Next Working date is in week time");   		
    		
    		if(Utilities.checkNotifyTime(calendar, thisActivity).equals("1")){
    			
    			calendar.add(Calendar.DATE, 1); // number of days to add    
    			calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(RenoPreferences.getWorkingFromTime(thisActivity).split(":")[0]));
        		calendar.add(Calendar.MINUTE, Integer.parseInt(RenoPreferences.getWorkingFromTime(thisActivity).split(":")[1]));
    		
        		((Activity) thisActivity).runOnUiThread(new Runnable() {				
    				@Override
    				public void run() {
    		    		Toast.makeText(thisActivity, "Your notification set date is in other that your working time, so we have reset to next business working day ( "+ calendar.getTime() +" ) !", Toast.LENGTH_LONG).show();
    				}
    			});
    		}
    		
    		else{
    			calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(RenoPreferences.getWorkingFromTime(thisActivity).split(":")[0]));
        		calendar.add(Calendar.MINUTE, Integer.parseInt(RenoPreferences.getWorkingFromTime(thisActivity).split(":")[1]));
    		}
    				
		}else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
        	Log.e("getTimeInterval", "Next Working date is saturday");
        	calendar.add(Calendar.DATE, 2); 
    		calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(RenoPreferences.getWorkingFromTime(thisActivity).split(":")[0]));
    		calendar.add(Calendar.MINUTE, Integer.parseInt(RenoPreferences.getWorkingFromTime(thisActivity).split(":")[1]));
    		
    		((Activity) thisActivity).runOnUiThread(new Runnable() {				
				@Override
				public void run() {
		    		Toast.makeText(thisActivity, "Your notification set date is in weekday, so we have reset to next business working day ( "+calendar.getTime()+" ) !", Toast.LENGTH_LONG).show();
				}
			});
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
        	Log.e("getTimeInterval", "Next Working date is sunday");
        	calendar.add(Calendar.DATE, 1); 
    		calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(RenoPreferences.getWorkingFromTime(thisActivity).split(":")[0]));
    		calendar.add(Calendar.MINUTE, Integer.parseInt(RenoPreferences.getWorkingFromTime(thisActivity).split(":")[1]));
    	
    		((Activity) thisActivity).runOnUiThread(new Runnable() {				
				@Override
				public void run() {
		    		Toast.makeText(thisActivity, "Your notification set date is in weekday, so we have reset to next business working day ( "+calendar.getTime()+" ) !", Toast.LENGTH_LONG).show();
				}
			});

        }
		return calendar.getTimeInMillis();
	}*/
	
	public static Cursor getContactsEmail(Activity thisActivity, String email)
	{
		// Run query
		Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.LOOKUP_KEY,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Email.DATA
		};
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP +"='1'" +
				" and " + ContactsContract.CommonDataKinds.Email.DATA + "='"+email+"'";
		//showing only visible contacts  
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		return thisActivity.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
	}
	
	public static boolean isWorkingTime(Activity thisActivity){
		boolean isWorkingTime =  false;		
	    Calendar calendar = Calendar.getInstance();
	    int day = calendar.get(Calendar.DAY_OF_WEEK);  
	    Log.i("day==>>",""+ day);
	  /*  if(day != 1 && day != 7){
	    	isWorkingTime = findWorkingTime(calendar,thisActivity);
	    }else{
	    	Log.e("", "Weekend time");
	    	isWorkingTime = false;
	    }*/
	    isWorkingTime = findWorkingTime(calendar,thisActivity);
		return isWorkingTime;		
	}
	
	public static boolean checkUpdatedTime(Calendar cal, Context thisActivity){
		boolean isWorkingTime =  false;	   
	    Calendar calendar = cal;
	    int day = calendar.get(Calendar.DAY_OF_WEEK);  
	    Log.i("day==>>",""+ day);
	    Log.i("day==>>",""+ calendar.get(Calendar.DAY_OF_MONTH));
	   
	  /*  if(day != 1 && day != 7){
	    	   isWorkingTime = findWorkingTime(cal,thisActivity);
	    }else{
	    	Log.e("", "Weekend time");
	    	isWorkingTime = false;
	    }*/
	    isWorkingTime = findWorkingTime(cal,thisActivity);
		return isWorkingTime;		
	}
	
	public static String checkNotifyTime(Calendar cal, Context thisActivity){
		String strAdd = "";
		boolean isWorkingTime =  false;	   
	    Calendar calendar = cal;
	    int day = calendar.get(Calendar.DAY_OF_WEEK);  
	    Log.i("day==>>",""+ day);
	    Log.i("day==>>",""+ calendar.get(Calendar.DAY_OF_MONTH));
	   
	    if(day != 1 && day != 7){
	    	Log.e("","Within week time");
	    	int time = calendar.get(Calendar.HOUR_OF_DAY);
	    	int minute = calendar.get(Calendar.MINUTE);
	    	Log.i("time==>>",""+ time);
	    	
	    	String strStartHour = RenoPreferences.getWorkingFromTime(thisActivity).split(":")[0];
	    	String strStartMinute = RenoPreferences.getWorkingFromTime(thisActivity).split(":")[1];
			int starthour = Integer.parseInt(strStartHour);
			int startminute = Integer.parseInt(strStartMinute);
			
			String strEndHour = RenoPreferences.getWorkingToTime(thisActivity).split(":")[0];
	    	String strEndMinute = RenoPreferences.getWorkingToTime(thisActivity).split(":")[1];
			int endhour = Integer.parseInt(strEndHour);
			int endminute = Integer.parseInt(strEndMinute);
			
			Log.i("start time==>>",""+ starthour);
			Log.i("end time==>>",""+ endhour);
			
			isWorkingTime = findWorkingTime(cal,thisActivity);
			
			if(isWorkingTime){
				strAdd = "0";
			}else{
				strAdd = "1";
			}

			/*if(time <= starthour){
				isWorkingTime = false;
				strAdd = "0";
			}else if(time >= endhour){
				isWorkingTime = false;
				strAdd = "1";
			}*/
	    }else{
	    	Log.e("", "Weekend time");
	    	isWorkingTime = false;
	    }
		return strAdd;	
	}
	
	
	public static long convertMilliseconds(String mytime) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		Date myDate = null;
		try {
			myDate = dateFormat.parse(mytime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long milliseconds = myDate.getTime();
		

		return milliseconds;
	}
	
	public static String implode(String[] array, String separator) {
		StringBuffer out = new StringBuffer();
		boolean first = true;
		for (String v : array) {
			if (first)
				first = false;
			else
				out.append(separator);
			out.append(v);
		}
		Log.e("implode", out.toString());
		return out.toString();
	}
	
	public static boolean findWorkingTime(Calendar cal,Context thisActivity){
		boolean isWorkingTime = false;
		Date date;
		Date datestartTime;
		Date dateEndTime;
	    Calendar calendar = cal;
		   
    	Log.e("","Within week time");
    	int hour = calendar.get(Calendar.HOUR_OF_DAY);
    	int minute = calendar.get(Calendar.MINUTE);
    	Log.i("hour==>>",""+ hour);
    	
    	String strStartHour = RenoPreferences.getWorkingFromTime(thisActivity).split(":")[0];
    	String strStartMinute = RenoPreferences.getWorkingFromTime(thisActivity).split(":")[1];
		int starthour = Integer.parseInt(strStartHour);
		int startminute = Integer.parseInt(strStartMinute);
		
		String strEndHour = RenoPreferences.getWorkingToTime(thisActivity).split(":")[0];
    	String strEndMinute = RenoPreferences.getWorkingToTime(thisActivity).split(":")[1];
		int endhour = Integer.parseInt(strEndHour);
		int endminute = Integer.parseInt(strEndMinute);
		
		Log.i("start time==>>",""+ starthour);
		Log.i("end time==>>",""+ endhour);
		
	    date = parseDate(hour + ":" + minute);
	    datestartTime = parseDate(RenoPreferences.getWorkingFromTime(thisActivity));
	    dateEndTime = parseDate(RenoPreferences.getWorkingToTime(thisActivity));
	    
	    Log.i("datestartTime==>>",""+ datestartTime);
	    Log.i("dateEndTime==>>",""+ dateEndTime);
	    Log.i("date==>>",""+ date);
	    
	    if (date.after(datestartTime) && date.before(dateEndTime)) {
	    	Log.e("","Within the working time");
    		isWorkingTime = true;
	    }else{
	    	Log.e("","other than working time");
    		isWorkingTime = false;
	    }
		return isWorkingTime;
		
    		    		
    
	}
	
	
	public static String getDurationBreakdown(long millis) {
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		StringBuilder sb = new StringBuilder(64);
		if(days > 1 ){
			sb.append(days + " days ");
		}else{
			if(days == 1 ){
				sb.append(days + " day ");
			}else {
//				Log.e("", "less than 24 hours..");
				//less than 24 hours..
			}
		}
		
		sb.append(hours +" hrs");
		sb.append(" : ");
		sb.append(minutes+" mins");
		sb.append(" : ");
		sb.append(seconds+ " secs");
		sb.append("");

		return (sb.toString());
	}
	
	private static Date parseDate(String date) {

	    try {
	        return inputParser.parse(date);
	    } catch (java.text.ParseException e) {
	        return new Date(0);
	    }
	}	 
		public static Cursor getCalendarManagedCursor(Activity context, String[] projection,
	            String selection, String path) {
	        Uri calendars = Uri.parse("content://calendar/"+path);
            Log.v("", "Calender URi"+calendars);
	        Cursor managedCursor = null;
	        try {
	            managedCursor = context.managedQuery(calendars, projection, selection,
	                    null, null);
	            Log.v("", "Calender managedCursor"+managedCursor);
	        } catch (IllegalArgumentException e) {
	            Log.w("Calendar Event", "Failed to get provider at ["
	                    + calendars.toString() + "]");
	        }
	        if (managedCursor == null) {
	            // try again
	            calendars = Uri.parse("content://com.android.calendar/" + path);
	            try {
	                managedCursor = context.managedQuery(calendars, projection, selection,
	                        null, null);
	                Log.v("", "Calender managedCursor 2"+managedCursor);
	            } catch (IllegalArgumentException e) {
	                Log.w("Calendar Event", "Failed to get provider at ["
	                        + calendars.toString() + "]");
	            }
	        }
            Log.v("", "Calender managedCursor 3"+managedCursor);
            return managedCursor;
	    }
		
		public static Uri getUri(Activity context) {
	        Uri calendars = Uri.parse("content://calendar/events/");

	        Cursor managedCursor = null;
	        try {
	            managedCursor = context.managedQuery(calendars, null, null,
	                    null, null);
	        } catch (IllegalArgumentException e) {
	            Log.w("Calendar Event", "Failed to get provider at ["
	                    + calendars.toString() + "]");
	        }

	        if (managedCursor == null) {
	            // try again
	            calendars = Uri.parse("content://com.android.calendar/events/");
	            try {
	                managedCursor = context.managedQuery(calendars, null, null,
	                        null, null);
	            } catch (IllegalArgumentException e) {
	                Log.w("Calendar Event", "Failed to get provider at ["
	                        + calendars.toString() + "]");
	            }
	        }
	        return calendars;
	    }
}
