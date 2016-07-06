package com.rifluxyss.therenoking.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.rifluxyss.therenoking.ProspectDetails;
import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.RenoPreferences;
import com.rifluxyss.therenoking.utils.Utilities;

public class StartNotificationService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();         

	}

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent sintent, int id) {

		Log.v("", "Service Started...");

		DatabaseConnection db = new DatabaseConnection(getApplicationContext());
		db.openDataBase();
		long timestamp = RenoPreferences.getNotificationOffTime(getApplicationContext());
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
		//		Sep 04 2013 06:24 PM
		String off_time = sdf.format(cal.getTime());
		String query = "select t1.*,t2.name,t2.phone_number from tbl_schedule as t1, tbl_prospects as t2 where t1.schedule_creation_datetime >= '"+off_time+"' and t1.request_id = t2.prospect_id";
		Log.v("", "query: "+query);
		Cursor c = db.executeQuery(query);

		while (c != null && c.moveToNext()){
			Intent intent = new Intent(getApplicationContext(), ProspectDetails.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (Build.VERSION.SDK_INT <= 10){
	        	intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | 
	                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	        }
			String status = c.getString(c.getColumnIndex("status"));
			Log.v("", "service status: "+status);
			String name = c.getString(c.getColumnIndex("name"));
			String reminder = c.getString(c.getColumnIndex("reminder_datetime"));			

			int p_id = c.getInt(c.getColumnIndex("request_id"));
			int category = c.getInt(c.getColumnIndex("stage_category"));

			String number = getPhoneCall(category, c.getString(c.getColumnIndex("phone_number")));

			String message;
			String title = name;
			if (category == ProspectDetails.STAGE3_WHEN_ESTIMATE_COMPLETED){
				message = String.format(getString(R.string.notify_after_estimate_complete), name +"-"+ number);
			} else if (category == ProspectDetails.STAGE1_SCHEDULE_APT
					|| category == ProspectDetails.STAGE2_SCHEDULE_APT ) {
				title = "Schedule Appointment";
				message = reminder +" - "+ name;
			} else if (category == ProspectDetails.STAGE2_RESCHEDULE_APT) {
				title = "Confirm Schedule Appointment";
				message = reminder +" - "+ name;
			} else if (category == ProspectDetails.STAGE2_FOLLOWUP_APT) {
					title = "Follow Up";
					message = reminder +" - "+ name;
			} else if (category == ProspectDetails.STAGE2_CANCEL_APT_FORNOW) {
				title = Utilities.getNotificationMessage(getApplicationContext(), category, name);
				message = reminder +" - "+ name;
			} else if (category == ProspectDetails.STAGE4_PROJECT_START) {
				title = "Project for "+ name +" completed?";
				message = reminder +" - "+ name;
			} else if (category == ProspectDetails.STAGE4_WAITING_OTHER_ESTIMATES) {
				title = name +" - Waiting for other estimates.";
				message = reminder +" - "+ name;
			} else {
				message = Utilities.getNotificationMessage(getApplicationContext(), category, name);
			}

			if (number != null || (status != null && status.equals(getApplicationContext().getString(R.string.status_estimate_completed)))){
				TelephonyManager telMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
				int simState = telMgr.getSimState();
				if (simState == TelephonyManager.SIM_STATE_ABSENT){
					Toast.makeText(getApplicationContext(), "Please insert sim.", Toast.LENGTH_SHORT).show();
					// return;
				}
				intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + number));
				
			/*	intent = new Intent(this.getApplicationContext(), CallIntent.class);
			    intent.putExtra("prospect_id", p_id);			
				intent.putExtra("number", number);*/
				
			} else if (status != null && status.equals(getApplicationContext().getString(R.string.status_confirm_appointment))){
				intent.putExtra("status", status);
			}	

			// compare two date to create notifiaction or create alarm manager
			Calendar remind_cal = Calendar.getInstance();
			try {
				remind_cal.setTime(sdf1.parse(reminder));
			} catch (ParseException e) {				
				e.printStackTrace();
			}

			Calendar curr_cal = Calendar.getInstance();

			if (!curr_cal.before(remind_cal)){
				NotificationManager nm = (NotificationManager) getApplicationContext()
						.getSystemService(Context.NOTIFICATION_SERVICE);
				nm.cancel(p_id);
				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
						intent, PendingIntent.FLAG_CANCEL_CURRENT);

				Notification noti = new Notification(R.drawable.ic_launcher,
						"Renoking Notification", System.currentTimeMillis());
				//noti.setLatestEventInfo(getApplicationContext(), title, message, contentIntent);

				// Hide the notificatisetTimeInMillison after its selected
				//noti.defaults |= Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS;
				//noti.flags |= Notification.FLAG_AUTO_CANCEL;
				//nm.notify(p_id, noti);

				Notification.Builder notiBuilder = new Notification.Builder(getApplicationContext());
				notiBuilder.setSmallIcon(R.drawable.ic_launcher);
				notiBuilder.setTicker("Renoking Notification");

				notiBuilder.setContentTitle(title);
				notiBuilder.setContentText(message);
				notiBuilder.setContentIntent(contentIntent);

				notiBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS);
				notiBuilder.setAutoCancel(true);
				nm.notify(p_id, notiBuilder.getNotification());



				Log.v("", "service notified");
			}else{
				Log.v("", "service notified");
				try {
					setAlarmManager(sdf1.parse(reminder).getTime(), category, p_id, status, name, number, reminder);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}			
		}
	}

	//	private void getTitle(int category, String name, String number, String reminder){
	//		String message;
	//		String title = name;
	//		if (category == ProspectDetails.STAGE3_WHEN_ESTIMATE_COMPLETED){
	//			message = String.format(getString(R.string.notify_after_estimate_complete), name +"-"+ number);
	//		} else if (category == ProspectDetails.STAGE2_SCHEDULE_APT
	//				|| category == ProspectDetails.STAGE2_RESCHEDULE_APT 
	//				|| category == ProspectDetails.STAGE2_FOLLOWUP_APT) {
	//			title = "Confirm Estimate Appointment";
	//			message = reminder +" - "+ name;
	//		} else if (category == ProspectDetails.STAGE2_CANCEL_APT_FORNOW) {
	//			title = Utilities.getNotificationMessage(getApplicationContext(), category, name);
	//			message = reminder +" - "+ name;
	//		} else if (category == ProspectDetails.STAGE4_PROJECT_START) {
	//			title = "Project for "+ name +" completed?";
	//			message = reminder +" - "+ name;
	//		} else if (category == ProspectDetails.STAGE4_WAITING_OTHER_ESTIMATES) {
	//			title = name +" - Waiting for other estimates.";
	//			message = reminder +" - "+ name;
	//		} else {
	//			message = Utilities.getNotificationMessage(getApplicationContext(), category, name);
	//		}
	//	}

	private String getPhoneCall(int category, String number){
		String tempNum;
		if (category == ProspectDetails.STAGE3_WHEN_ESTIMATE_COMPLETED){
			tempNum = number;
		} else if (category == ProspectDetails.STAGE2_SCHEDULE_APT
				|| category == ProspectDetails.STAGE2_RESCHEDULE_APT 
				|| category == ProspectDetails.STAGE2_FOLLOWUP_APT) {
			tempNum = number;
		} else if (category == ProspectDetails.STAGE2_CANCEL_APT_FORNOW) {
			tempNum = number;
		} else if (category == ProspectDetails.STAGE4_WAITING_OTHER_ESTIMATES) {
			tempNum = number;
		} else {
			tempNum = null;
		}
		return tempNum;
	} 

	public void setAlarmManager(long interval, int STAGE_CATEGORY, int prospect_id, String status, String name, String number, String reminder){
		AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), RenoKingNotifications.class);
		intent.putExtra("id", prospect_id);
		intent.putExtra("name", name);
		intent.putExtra("title", name);
		intent.putExtra("status", status);
//		intent.putExtra("stage", STAGE);

		String message;
		if (STAGE_CATEGORY == ProspectDetails.STAGE3_WHEN_ESTIMATE_COMPLETED){
			intent.putExtra("number", number);
			message = String.format(getString(R.string.notify_after_estimate_complete), name +"-"+ number);
//			message = "Make a call for the project: " + prospect.name;
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE2_SCHEDULE_APT) {			
			intent.putExtra("status", getString(R.string.status_schedule));
			intent.putExtra("title", "Schedule Appointment");
			intent.putExtra("number", number);
			message = reminder +" - "+ name;
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE2_RESCHEDULE_APT) {			
			intent.putExtra("status", getString(R.string.status_confirm_schedule));
			intent.putExtra("title", "Confirm Schedule Appointment");
			intent.putExtra("number", number);
			message = reminder +" - "+ name;
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE2_FOLLOWUP_APT) {			
			intent.putExtra("status", getString(R.string.status_followup));
			intent.putExtra("title", "Follow Up");
			intent.putExtra("number", number);
			message = reminder +" - "+ name;
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE2_CANCEL_APT_FORNOW) {
			intent.putExtra("title", Utilities.getNotificationMessage(getApplicationContext(), STAGE_CATEGORY, name));
			intent.putExtra("number", number);
			message = reminder +" - "+ name;
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE4_PROJECT_START) {
			intent.putExtra("title", "Project for "+ name + "completed?");
			message = reminder +" - "+ name;
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE4_WAITING_OTHER_ESTIMATES) {
			intent.putExtra("title", name +" - Waiting for other estimates.");		
			intent.putExtra("number", number);
			message = reminder +" - "+ name;
		} else {
			message = Utilities.getNotificationMessage(getApplicationContext(), STAGE_CATEGORY, name);			
		}		
		intent.putExtra("message", message);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), prospect_id,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		interval = Utilities.getTimeInterval(interval, STAGE_CATEGORY,this.getApplicationContext());

		Log.v("","interval: "+ interval);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(interval);		
		Log.v("","notify at: "+ cal.getTime().toString());
		am.set(AlarmManager.RTC_WAKEUP,
				interval, pendingIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
