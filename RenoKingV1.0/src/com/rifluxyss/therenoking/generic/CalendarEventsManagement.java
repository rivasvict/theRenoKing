package com.rifluxyss.therenoking.generic;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import com.google.gdata.util.ServiceException;
import com.rifluxyss.therenoking.ProspectDetails;
import com.rifluxyss.therenoking.beans.Prospects;
import com.rifluxyss.therenoking.network.APIClient;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.Utilities;

public class CalendarEventsManagement {
	
	static int version = Build.VERSION.SDK_INT;
	static String action = "";
	
    static Date testDate = null;


	public static String createCalendarEvent(Activity thisActivity,
			int STAGE_CATEGORY, Date date, Prospects prospect, int calendarId, String tag) throws Exception {
		
		long startMillis = 0; 
		long endMillis = 0; 
		
		Log.e("","Date is"+date);
		
//		SimpleDateFormat sdfc = new SimpleDateFormat("YYYY-MM-DD");
//		String str = sdfc.format(date);
//		
		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		startMillis = cal.getTimeInMillis();
		endMillis = getFormattedEndDate(date);
		String strTitle = "";
		
//		SimpleDateFormat sdfc = new SimpleDateFormat("YYYY-MM-DD");
//		String str = sdfc.format(date);
//		Log.e("","The New Date is"+str);
//		Log.e("","The New startMillis is"+startMillis);
//		Log.e("","The New endMillis is"+endMillis);

		String startTime = String.valueOf(startMillis);
		String endTime = String.valueOf(endMillis);
        String date1 =  String.valueOf(date);
		String timezone =  getTimeZone();
		
	
		
		if (STAGE_CATEGORY == ProspectDetails.STAGE5_FINISH_DATE){			
			strTitle = "Job Completion Date: "+ prospect.name+ "-" + prospect.phone_number;
			Log.v("", "Event Management--->Job Completion Date: "+strTitle);
		}else if (STAGE_CATEGORY == ProspectDetails.STAGE2_SCHEDULE_APT){
			strTitle = "Estimate Appointment: "+ prospect.name + "-"+prospect.phone_number;
			Log.v("", "Event Management--->Estimate Appointment: "+strTitle);
		}else if (STAGE_CATEGORY == ProspectDetails.STAGE2_CONFIRM_APT){
			strTitle = "Confirm Appointment: "+ prospect.name + "-"+prospect.phone_number;
			Log.v("", "Event Management--->Confirm Appointment: "+strTitle);
		}else if (STAGE_CATEGORY == ProspectDetails.STAGE1_LEFTMSG_NOANSWER){
			strTitle = "Schedule Appointment: "+ prospect.name + "-" + prospect.phone_number;
			Log.v("", "Event Management--->Schedule Appointment: "+strTitle);
		}else if (STAGE_CATEGORY == ProspectDetails.STAGE2_FOLLOWUP_APT){
			strTitle = "Follow up: "+ prospect.name + "-"+prospect.phone_number;
			Log.v("", "Event Management--->Follow up: "+strTitle);
		}else if(STAGE_CATEGORY == ProspectDetails.STAGE1_SCHEDULE_APT){
			strTitle = "Schedule Appointment: "+ prospect.name + "-"+prospect.phone_number;
			Log.v("", "Event Management--->Schedule Appointment: "+strTitle);
		}else if(STAGE_CATEGORY == ProspectDetails.STAGE2_RESCHEDULE_APT){
			strTitle = "Estimate Appointment: "+ prospect.name + "-"+prospect.phone_number;
			Log.v("", "Event Management--->Estimate Appointment:"+strTitle);
		}

		
		DatabaseConnection connection = new DatabaseConnection(thisActivity);
		connection.openDataBase();
		
		String selectGoogleId 	=  "select * from tbl_prospects where prospect_id='"+prospect.prospect_id+"'";
		
		Cursor cursor = connection.executeQuery(selectGoogleId);
		String strGoogleId = "";
		try {
			while(cursor.moveToNext()) {
				strGoogleId = cursor.getString(cursor.getColumnIndex("google_id"));
				Log.e("","strGoogleId"+strGoogleId);
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(strGoogleId.equals("null")){
			strGoogleId = "";
		}
		
		new SyncCalenderTask(prospect, thisActivity, String.valueOf(date), tag, strTitle, strGoogleId).execute("");

		
//		try {
//			if(version < 14){
//				ContentValues event = new ContentValues();
//				event.put("calendar_id", calendarId);	
//				event.put("title", strTitle);
//				event.put("description", prospect.details);				
//				event.put("dtstart", startMillis);
//				event.put("dtend", 	endMillis);
//				// event is added
//				Uri eventsUri = Utilities.getUri(thisActivity); 				
//				thisActivity.getContentResolver().insert(Utilities.getUri(thisActivity), event);	
//			}else{
//				TimeZone timeZone = TimeZone.getDefault();
//				ContentResolver cr = thisActivity.getContentResolver();
//				ContentValues values = new ContentValues();
//				values.put(CalendarContract.Events.CALENDAR_ID, calendarId);				
//				values.put(CalendarContract.Events.TITLE, strTitle);				
//				values.put(CalendarContract.Events.DESCRIPTION, prospect.details);
//				values.put(CalendarContract.Events.DTSTART, startMillis);
//				values.put(CalendarContract.Events.DTEND, endMillis);
//				values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());			
//				cr.insert(Utilities.getUri(thisActivity), values);
//				
//				String id = prospect.calendar_id;
//
//				String where = "_id =" + id + " and " + calendarId + "=" + calendarId;
//		        int count = cr.update(Events.CONTENT_URI, values, where, null);
//		        Log.v("", "count value"+count);
//		        Log.v("", "where"+where);
//		        Log.v("", "values"+values);
//		        Log.v("", "Events.CONTENT_URI"+Events.CONTENT_URI);	
//		        Log.v("", "Time Zone"+timeZone.getID());
//
//		        
//		        
//				  if (count != 1){
//			        	Log.e("","More than one row was updated");
//			            throw new IllegalStateException ("more than one row updated");
//			      }else{
//			        	Log.e("","Event Updated Successfully");		        					
//				  }
//			}	
//		} catch (Exception e) {
//			e.printStackTrace();
//        	Log.e("Calender===>","Failed to find calender event");		        					
//		}
		Log.v("", "calendarId: "+calendarId);
		Log.v("", "startMillis: "+startMillis);
		Log.v("", "strTitle: "+strTitle);
	
		int eventID = getEventID(calendarId, startMillis, strTitle, thisActivity);
		String strEventID = ""+eventID;
		Log.v("", "inserted event id: "+strEventID);

		return strEventID;
	}

		public static String getTimeZone() {
			String strTimeZone = "";
			try {
				/*
				 * Date today = Calendar.getInstance().getTime(); SimpleDateFormat
				 * sdf = new SimpleDateFormat("Z"); strTimeZone = sdf.format(today);
				 */
				TimeZone tz = TimeZone.getDefault();
				Date now = new Date();
				int offsetFromUtc = tz.getOffset(now.getTime()) / 1000;
				strTimeZone = "" + offsetFromUtc;
				Log.e("","strTimeZone == " + strTimeZone);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return strTimeZone;

		}



	public static String updateCalendarEvent(Activity thisActivity, int STAGE_CATEGORY, Date date, 
			Prospects prospect, int calendarId, String tag)	throws ServiceException, IOException {
		
		long startMillis = 0; 
		long endMillis = 0; 
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		startMillis = cal.getTimeInMillis();
		endMillis = getFormattedEndDate(date);
		String strTitle = "";
		Log.e("", "update calendar event time ==>>" + cal.getTime());
		
			
		Log.e("", "update calendar event time ==>>" + new Timestamp(date.getTime()));
		Timestamp t = new Timestamp(date.getTime());

		String dateString=""+t;
        if (dateString != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                try {
					testDate = sdf.parse(dateString);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println("Milliseconds==" + testDate.getTime());

        } 
		
		
	/*	Log.e("", "My Date ===>" + date);

		    Date datenew = null;
		    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		    sdf.setTimeZone(timezone)
		    DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");    
		    formatter.setTimeZone(TimeZone.getTimeZone("GMT+13"));  

		    try {
				DateFormat formatter = null;
				datenew =  formatter .parse(String.valueOf(date));
		    } catch (ParseException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }

		    String convertedDate = new SimpleDateFormat("dd-MM-yyyy").format(datenew);
		    Log.i("ConvertedDate", convertedDate);
	    
	    */
	    
		if (STAGE_CATEGORY == ProspectDetails.STAGE2_SCHEDULE_APT
				|| STAGE_CATEGORY == ProspectDetails.STAGE2_RESCHEDULE_APT){			
			strTitle =  "Estimate Appointment: "+ prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Estimate Appointment"+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE2_FOLLOWUP_APT 
				|| STAGE_CATEGORY == ProspectDetails.STAGE2_CANCEL_APT_FORNOW){
			strTitle =  "Follow up: "+ prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Follow up: "+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE2_CONFIRM_APT){
			strTitle =  "Estimate Appointment(Confirmed): "+ prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Estimate Appointment (Confirmed): "+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE3_ESTIMATE_DATE){
			strTitle =  "Complete Estimate by now. "+ prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Complete Estimate by now."+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE3_WHEN_ESTIMATE_COMPLETED){			
			strTitle =  "Follow up on estimate sent: " + prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Follow up on estimate sent"+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE4_PROJECT_START){
			strTitle =  "New job starting today: " + prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---New job starting today"+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE4_WAITING_OTHER_ESTIMATES){
//			myEntry.setTitle(new PlainTextConstruct("Was waiting for other estimates - Now call: "+ prospect.name + "-"+ prospect.phone_number));			
			strTitle =  "Follow up - Was waiting for other estimates: "+ prospect.name + "-"+ prospect.phone_number;
			Log.v("Updated--->", "Updated ---Follow up - Was waiting for other estimates"+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE5_FINISH_DATE  ){
//			myEntry.setTitle(new PlainTextConstruct("Job Completion Date: "+ prospect.name));
			//myEntry.setTitle(new PlainTextConstruct("Customer care follow up - "+ prospect.name + "-" + prospect.phone_number));
			strTitle =  "Job Completion Date: "+ prospect.name+ "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Job Completion Date"+strTitle);
//		} else if (STAGE_CATEGORY == ProspectDetails.STAGE6_CUSTOMERCARE_FOLLOWUP ){
//			myEntry.setTitle(new PlainTextConstruct("Job Completion Date: "+ prospect.name));
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE6_CUSTOMERCARE_RESCHEDULE){
//		} else if (STAGE_CATEGORY == ProspectDetails.STAGE5_JOB_COMPLETED ){
			strTitle =  "Customer care followup : "+ prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Customer care followup"+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE6_CUSTOMERCARE_SCHEDULE ){
//			myEntry.setTitle(new PlainTextConstruct("Job Completion Date: "+ prospect.name));
			strTitle =  "Customer Care Appointment : "+ prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Customer Care Appointment"+strTitle);
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE1_LEFTMSG_NOANSWER){
			strTitle =  "Schedule Appointment: "+ prospect.name + "-" + prospect.phone_number;
			Log.v("Updated--->", "Updated ---Schedule Appointment"+strTitle);
		} 
		
		long startTime = startMillis/ 1000L;
		long endTime = endMillis/ 1000L;
		long date1 = startMillis/ 1000L;
		String timezone =  getTimeZone();
		
		Log.e("", "startTime" + startTime);
		Log.e("", "endTime" + endTime);
		Log.e("", "date1" + date1);
		Log.e("", "timezone" + timezone);

        String strGoogleId = "";
        
    	DatabaseConnection connection = new DatabaseConnection(thisActivity);
		connection.openDataBase();
		
		String selectGoogleId 	=  "select * from tbl_prospects where prospect_id='"+prospect.prospect_id+"'";
		Cursor cursor = connection.executeQuery(selectGoogleId);

		try {
			while(cursor.moveToNext()) {
				strGoogleId = cursor.getString(cursor.getColumnIndex("google_id"));
				Log.e("","strGoogleId"+strGoogleId);
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(strGoogleId.equals("null")){
			strGoogleId = "";
		}
		
		new SyncCalenderTask(prospect, thisActivity, String.valueOf(date), tag, strTitle, strGoogleId).execute("");

		//String id = prospect.calendar_id.substring(prospect.calendar_id.lastIndexOf("/"), prospect.calendar_id.length());
		String id = prospect.calendar_id;
		Log.v("", "id: "+id + "\n"+C.POST_CALENDAR_GOOGLE + id);	
//		try {
//			if(version<14){
//				ContentValues event = new ContentValues();
//				event.put("calendar_id", calendarId);		
//				event.put("title", strTitle);	
//				event.put("description", prospect.details);				
//				event.put("dtstart", startMillis);
//				event.put("dtend", 	endMillis);
//				// event is added
//				Uri eventsUri = Utilities.getUri(thisActivity); 
//				String where = "_id =" + id +
//		                 " and " + calendarId + "=" + calendarId;
//		        int count = thisActivity.getContentResolver().update(Events.CONTENT_URI, event, where, null);
//		        if (count != 1)
//		            throw new IllegalStateException ("more than one row updated");
//		        else
//		        	Log.e("","Event Updated Successfully");
//			}else{
//				TimeZone timeZone = TimeZone.getDefault();
//				ContentResolver cr = thisActivity.getContentResolver();
//				ContentValues values = new ContentValues();
//				values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
//				values.put("title", "Estimate Appointment: "+ prospect.name + "-" + prospect.phone_number);			
//				values.put(CalendarContract.Events.TITLE,strTitle);	
//				values.put(CalendarContract.Events.DESCRIPTION, prospect.details);
//				values.put(CalendarContract.Events.DTSTART, startMillis);
//				values.put(CalendarContract.Events.DTEND, endMillis);
//				values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());	
//				
//				String where = "_id =" + id + " and " + calendarId + "=" + calendarId;
//		        int count = cr.update(Events.CONTENT_URI, values, where, null);
//		        Log.v("", "count value"+count);
//		        Log.v("", "where"+where);
//		        Log.v("", "values"+values);
//		        Log.v("", "Events.CONTENT_URI"+Events.CONTENT_URI);		        
//
//		        if (count != 1){
//		        	Log.e("","More than one row was updated");
//		            throw new IllegalStateException ("more than one row updated");
//		        }else{
//		        	Log.e("","Event Updated Successfully");		        					
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//	
//
//		}
		
		int eventID = getEventID(calendarId, startMillis,strTitle,thisActivity);
		String strEventID = ""+eventID;
		
		Log.v("", "updated event id: "+strEventID);
		return strEventID;
	}
	
	public static void deleteCalendarEvent(Activity thisActivity,Prospects prospect) 
			throws ServiceException, IOException {
		int eventId = 0;
		
		//String id = prospect.calendar_id.substring(prospect.calendar_id.lastIndexOf("/"),prospect.calendar_id.length());
		
		String id = prospect.calendar_id;		
		Log.v("", "id: " + id + "\n" + C.POST_CALENDAR_GOOGLE + id);
		
		if(id != null && ! id.equals(""))
			eventId = Integer.parseInt(id);
		

		DatabaseConnection connection = new DatabaseConnection(thisActivity);
		connection.openDataBase();
		
		String selectGoogleId 	=  "select * from tbl_prospects where prospect_id='"+prospect.prospect_id+"'";
		
		Cursor cursor = connection.executeQuery(selectGoogleId);
		String strGoogleId = "";
		try {
			while(cursor.moveToNext()) {
				strGoogleId = cursor.getString(cursor.getColumnIndex("google_id"));
				Log.e("","strGoogleId"+strGoogleId);
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(strGoogleId.equals("null")){
			strGoogleId = "";
		}
		
		String tag = "delete";
		new SyncCalenderTask(prospect, thisActivity, strGoogleId, tag).execute("");
		
  		int iNumRowsDeleted = 0;
		try {
			if(version<14){
		  		Uri eventsUri = Utilities.getUri(thisActivity);		     
		  		Uri eventUri = ContentUris.withAppendedId(eventsUri, eventId);
		  		iNumRowsDeleted = thisActivity.getContentResolver().delete(eventUri, null, null);
		  		Log.e("","Deleted " + iNumRowsDeleted + " calendar entry.");
			}else{
				ContentResolver cr = thisActivity.getContentResolver();
		  		Uri eventUri = ContentUris.withAppendedId(Utilities.getUri(thisActivity), eventId);
		  		iNumRowsDeleted = cr.delete(eventUri, null, null);
		  		Log.e("", "Deleted 4.0" + iNumRowsDeleted + " calendar entry.");
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		if(iNumRowsDeleted > 0)
			Log.e("", "id: " + C.POST_CALENDAR_GOOGLE + id + " Deleted!!!");
		else
			Log.e("", "Events Not in the calendar");
	}
	
	
	public static int getEventID(int cal_id, long dtstart,String  strTitle, Activity thisActivity ) {
		int event_id = 0;
				
		try {
			if(version<14){
				String[] projection = new String[] { "_id", "calendar_id", "dtstart", "title" };
				
				Log.e("","title: "+strTitle);
				Log.e("","dtstart: "+dtstart);
		        String selection = "calendar_id="+cal_id + " and title=\"" + strTitle + "\" and dtstart='" +dtstart+"'";

		        String path = "events/";
				//Activity context, String[] projection, String selection, String path
		        Cursor managedCursor = Utilities.getCalendarManagedCursor(thisActivity, projection, selection, path);
		       
		        if (managedCursor != null && managedCursor.moveToFirst()) {
		        	Log.e("","***** Listing Calendar Event Details *****" + managedCursor.getColumnCount());	          
		            do {
		            	Log.e("", "**START Calendar Event Description**");	            	
		                for (int i = 0; i < managedCursor.getColumnCount(); i++) {	                	
		                	String column = managedCursor.getColumnName(i);
		                	String value = managedCursor.getString(i);
		                	if (column.equals("_id")){
		                		Log.e("","datefixed true.");
		                		event_id = Integer.parseInt(value);
		                	}
		                	Log.e("", column + "="+ value);
		                }
		                Log.e("","**END Calendar Event Description**");
		                if (event_id != 0)
		                	break;
		            } while (managedCursor.moveToNext());
		        } else {
		        	Log.e("","No Calendar Entry");
		        }
			}else{
		    	String[] projection = new String[] {
		    				CalendarContract.Events._ID,
			    	        CalendarContract.Events.CALENDAR_ID,
			    	        CalendarContract.Events.DTSTART,
			    	        CalendarContract.Events.TITLE
			    	};

			        String selection = CalendarContract.Events.CALENDAR_ID+"="+cal_id +
			        					" and "+CalendarContract.Events.TITLE+"=\"" + strTitle + "\"" +
			        					" and "+CalendarContract.Events.DTSTART+"='" +dtstart+"'";
			    	Uri uri = CalendarContract.Events.CONTENT_URI;
			    	Cursor managedCursor = thisActivity.managedQuery(uri, projection, selection, null, null);
			    	 if (managedCursor != null && managedCursor.moveToFirst()) {
			    		 Log.e("","***** Listing Calendar Event Details *****" + managedCursor.getColumnCount());
				            do {
				            	Log.e("","**START Calendar Event Description**");
				                for (int i = 0; i < managedCursor.getColumnCount(); i++) {	                	
				                	String column = managedCursor.getColumnName(i);
				                	String value = managedCursor.getString(i);
				                	if (column.equals("_id")){
				                		Log.e("","datefixed true.");
				                		event_id = Integer.parseInt(value);
				                		Log.e("","event_id == "+event_id);
				                	}
				                	Log.e("",column + "="+ value);
				                }
				                Log.e("","**END Calendar Event Description**");
				                if (event_id != 0)
				                	break;
				            } while (managedCursor.moveToNext());
				        } else {
				        	Log.e("","No Calendar Entry");
				        }
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return event_id;
    } 
	

	public static String getFormattedDate(Date date1){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
		String str = sdf.format(date1);
		String seperator = str.substring(str.length() - 5, str.length() - 4);
		Log.v("", "seperator: "+seperator);	
		String str_date = str.substring(0, str.lastIndexOf(seperator) + 1);	
		String time = str.substring(str.lastIndexOf(seperator)+1, str.length());
		String hr = time.substring(0, 2);
		String mins = time.substring(2, 4);
		Log.v("", "hrmins: "+ str_date + hr + ":" + mins);
		//String formatted_date = sdf.format(c.getTime()).replaceAll("(\\+\\d\\d)(\\d\\d)", "$1:$2");
		String formatted_date = str_date + hr + ":" + mins;
		return formatted_date;
	}

	public static long getFormattedEndDate(Date date1){
		long millis;
		Calendar c = Calendar.getInstance();
		c.setTime(date1);
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY)+1);
		millis = c.getTimeInMillis();
		return millis;
	}


}

class SyncCalenderTask extends AsyncTask<String, Long, Integer> {
	
	Prospects prospect;
	Activity thisActivity;
	String action, date, startTime, endTime, timeZone;
    Date tempDate;
    String strResponseMessage = "", strStatus ="", strMessage = "", strId = "", strTitle ="", strGoogleId = "", tag = "";
	String strNewJobId = "", strJobCompletionId = "";
	
	
	SyncCalenderTask(Prospects prospects, Activity thisActivity, String date,  String tag, String strTitle, String strGoogleId){
		this.prospect = prospects;
		this.thisActivity = thisActivity;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.strTitle = strTitle;
		this.strGoogleId = strGoogleId;
		this.tag = tag;
	}
	
	SyncCalenderTask(Prospects prospects, Activity thisActivity,String strGoogleId, String tag){
		this.prospect = prospects;
		this.thisActivity = thisActivity;
		this.strGoogleId = strGoogleId;
		this.tag = tag;
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
	    HashMap<String, String> api_params = new HashMap<String, String>();
	    
		Log.e("", "Sync date" + date);
		Log.e("", "Sync message" + prospect.details);
		Log.e("", "Sync Title" + strTitle);

	
		api_params.put("mode", "sync-google-calendar");
		api_params.put("date", ""+date);
		api_params.put("title", ""+strTitle);
		api_params.put("message", prospect.details);
		api_params.put("prospect_id", prospect.prospect_id);
		
		DatabaseConnection connection = new DatabaseConnection(thisActivity);
		connection.openDataBase();
		
		try
		{
		 String check = "select * from tbl_prospects where new_job_id = 'a'";
		 Cursor cr = connection.executeQuery(check);
		 Log.e("","check ======"+check);
		 Log.e("","cr.getCount() ======"+cr.getCount());
		 }catch(Exception e)
		 {
		 e.printStackTrace();
		 Log.e("","inside exception ======");
		 String upgradeQuery = "ALTER TABLE tbl_prospects ADD COLUMN new_job_id TEXT ";
		 connection.executeUpdate(upgradeQuery);
		 }
		
		try
		{
		 String check = "select * from tbl_prospects where job_completion_id = 'a'";
		 Cursor cr = connection.executeQuery(check);
		 Log.e("","check ======"+check);
		 Log.e("","cr.getCount() ======"+cr.getCount());
		 }catch(Exception e)
		 {
		 e.printStackTrace();
		 Log.e("","inside exception ======");
		 String upgradeQuery = "ALTER TABLE tbl_prospects ADD COLUMN job_completion_id TEXT ";
		 connection.executeUpdate(upgradeQuery);
		 }		
		
	    String selectGoogleId 	=  "select * from tbl_prospects where prospect_id='"+prospect.prospect_id+"'";
		
		Cursor cursor = connection.executeQuery(selectGoogleId);
		try {
			while(cursor.moveToNext()) {
				strNewJobId = cursor.getString(cursor.getColumnIndex("new_job_id"));
				Log.e("","strNewJobId"+strNewJobId);
				strJobCompletionId = cursor.getString(cursor.getColumnIndex("job_completion_id"));
				Log.e("","strJobCompletionId"+strJobCompletionId);
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(tag.equals("insert")){
			Log.e("", "Inserted First");
			api_params.put("action", "insert");
		}else if(strJobCompletionId == null && strTitle.contains("Job Completion Date")){
			Log.e("", "Job Completion");
			api_params.put("action", "insert");
		}else{
			if (!strTitle.contains("Customer care followup")) {
				if (strGoogleId != null) {
					if (tag.equals("delete")) {
						Log.e("", "Delete Event");
						api_params.put("action", "delete");
					} else if(tag.equals("create")){
						api_params.put("action", "insert");
					} else {
						Log.e("", "Update Event");
						api_params.put("action", "update");
					}
				
					if(strTitle.contains("New job starting today") && strNewJobId != null){
						Log.e("", "Reschedule New job Event");
						api_params.put("event", strNewJobId);
					}
					else if(strTitle.contains("Job Completion Date") && strJobCompletionId != null){
						Log.e("", "Reschedule Job Completion Event");
						api_params.put("event", strJobCompletionId);
					}else{
						Log.e("", "Not Rescheduled Event");
						api_params.put("event", strGoogleId);					
					}
				}
			}
		}

		if(strTitle.contains("Customer care followup")){
			Log.e("", "Deleted Events"+ strNewJobId+","+strJobCompletionId);
			api_params.put("deleteevent", strNewJobId+","+strJobCompletionId);
			api_params.put("action", "insert");
		}
			
		action = "google-calendar.php?";

		APIClient apiclient = new APIClient(thisActivity, action, api_params, "tag");
		final int status = apiclient.processAndFetchResponse();	
		Log.v("", "status printed: "+status);
		
		if (status == APIClient.STATUS_SUCCESS){			
			Log.e("", "Sync Success");
			String server_response = apiclient.getResponse();
			strResponseMessage = server_response;
			try {

				JSONObject jsonObj = new JSONObject(strResponseMessage);			

				if (jsonObj.has("status")) {
					strStatus = jsonObj.get("status").toString();
					Log.e("","Status==>" + strStatus);
				}
				if (jsonObj.has("message")) {
					strMessage = jsonObj.get("message").toString();
					Log.e("","Message==>" + strMessage);
				}
				if (jsonObj.has("id")) {
					strId = jsonObj.get("id").toString();
					Log.e("","Google Id==>" + strId);
				}
				
				try
				{
				 String check = "select * from tbl_prospects where google_id = 'a'";
				 Cursor cr = connection.executeQuery(check);
				 Log.e("","check ======"+check);
				 Log.e("","cr.getCount() ======"+cr.getCount());
				 }catch(Exception e)
				 {
				 e.printStackTrace();
				 Log.e("","inside exception ======");
				 String upgradeQuery = "ALTER TABLE tbl_prospects ADD COLUMN google_id TEXT ";
				 connection.executeUpdate(upgradeQuery);
				 }
				
				
				String updateGoogleId 	= "update tbl_prospects set google_id ='"+strId+"' where prospect_id='"+prospect.prospect_id+"'";
				connection.executeUpdate(updateGoogleId);	
				
				if(strTitle.contains("New job starting today")){
					String updateNewJobId 	= "update tbl_prospects set new_job_id ='"+strId+"' where prospect_id='"+prospect.prospect_id+"'";
					connection.executeUpdate(updateNewJobId);
				}		
					
				if(strTitle.contains("Job Completion Date")){
					String updateJobCompletionId 	= "update tbl_prospects set job_completion_id ='"+strId+"' where prospect_id='"+prospect.prospect_id+"'";
					connection.executeUpdate(updateJobCompletionId);
				}
	
			} catch (Exception e) {

				e.printStackTrace();
			}
		}else{
			Log.e("", "Sync Failed");
		}
		return null;
	}
	
		public static String getTimeZone() {
			String strTimeZone = "";
			try {
				/*
				 * Date today = Calendar.getInstance().getTime(); SimpleDateFormat
				 * sdf = new SimpleDateFormat("Z"); strTimeZone = sdf.format(today);
				 */
				TimeZone tz = TimeZone.getDefault();
				Date now = new Date();
				int offsetFromUtc = tz.getOffset(now.getTime()) / 1000;
				strTimeZone = "" + offsetFromUtc;
				Log.e("","strTimeZone == " + strTimeZone);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return strTimeZone;

		}
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
}