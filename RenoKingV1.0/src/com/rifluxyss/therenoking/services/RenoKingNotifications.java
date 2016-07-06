package com.rifluxyss.therenoking.services;
 
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.rifluxyss.therenoking.CallIntent;
import com.rifluxyss.therenoking.ProspectDetails;
import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.generic.TimeConversion;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
 
public class RenoKingNotifications extends BroadcastReceiver{    
 
    NotificationManager nm;
    DatabaseConnection db;
    String TAG = "com.rifluxyss.therenoking";
    boolean do_notify = true;	
    
    @Override
    public void onReceive(Context context, Intent rintent) {
    	
    	String number = "",status= "",name="" , title ="";    	
    	int p_id =0 , STAGE=0;

    	
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);         
        
        Intent intent = new Intent(context, ProspectDetails.class);
        Intent intentc = new Intent(context, CallIntent.class);        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentc.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        if (Build.VERSION.SDK_INT <= 10){
        	intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        }
        
        if(rintent.hasExtra("number"))
        	number = rintent.getStringExtra("number").trim();
        
        if(rintent.hasExtra("status"))
        	status = rintent.getStringExtra("status");
        
        if(rintent.hasExtra("id"))
        	p_id = rintent.getIntExtra("id", 0);
        
        if(rintent.hasExtra("name"))
        	name = rintent.getStringExtra("name");
        
        if(rintent.hasExtra("STAGE"))
         STAGE = rintent.getIntExtra("STAGE", 0);
         
        if(rintent.hasExtra("title"))
        	title = rintent.getStringExtra("title");
        
        nm.cancel(p_id);        
         
//      Prospects prospect = (Prospects) rintent.getSerializableExtra("prospect");
         
        Log.d(TAG, "p_id: "+p_id);
        Log.d(TAG, "status: "+status);
        Log.d(TAG, "number: "+number);
        Log.d(TAG, "STAGE: "+STAGE);
         
        do_notify = true;
        
        intent.putExtra("prospect_id", p_id);         
        intent.putExtra("number", number);
         
        if (status != null){
            intent.putExtra("status", status);
            if (status.equals(context.getString(R.string.status_confirm_schedule))){
                if (name != null){
                    STAGE = 2;
                    setAlarmManager(context, p_id, name, number, STAGE);
                }
            }
        }
         
        if ( (number != null &&  ! number.equals("") ) || 
        		(status != null && status.equals(context.getString(R.string.status_estimate_completed)))){
            
        	TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telMgr.getSimState();
            if (simState == TelephonyManager.SIM_STATE_ABSENT){
               // Toast.makeText(context, "Please insert sim.", Toast.LENGTH_SHORT).show();
            }
             
            intentc.putExtra("prospect_id", p_id); 
            Log.e(TAG, "number final: "+number);
            Log.e(TAG, "status final: "+status);

            intentc.putExtra("number", number);
            intentc.putExtra("status", status);            
            
           /* CharSequence from = rintent.getStringExtra("title");
            CharSequence message = rintent.getStringExtra("message");
            Notification noti = null;    
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,intentc,0);
            noti = new Notification(R.drawable.ic_launcher,
                    "Renoking Notification", System.currentTimeMillis());
            noti.setLatestEventInfo(context, from, message, contentIntent);
 
            // Hide the notification after its selected
            noti.defaults |= Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS;
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(p_id, noti);*/
            
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);        
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            Log.v("", "rIntent" +rintent.getStringExtra("title"));
            mBuilder.setContentTitle(rintent.getStringExtra("title"));          
            mBuilder.setContentText(rintent.getStringExtra("message"));  
            mBuilder.setAutoCancel(true);
            
            int requestId = (int) System.currentTimeMillis();
             
            // Creates the PendingIntent
             PendingIntent notifyPendingIntent =  PendingIntent.getActivity(context,requestId,intentc, PendingIntent.FLAG_CANCEL_CURRENT);
 
             // Puts the PendingIntent into the notification builder
             mBuilder.setContentIntent(notifyPendingIntent);
 
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(p_id, mBuilder.build()); 
            return;
          
        } else if (status != null && status.equals(context.getString(R.string.status_confirm_appointment))){
            intent.putExtra("status", status);
        }
         
        if (do_notify){             
            /*CharSequence from = rintent.getStringExtra("title");
            CharSequence message = rintent.getStringExtra("message");
            Notification noti = null;    
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            noti = new Notification(R.drawable.ic_launcher,
                    "Renoking Notification", System.currentTimeMillis());
            noti.setLatestEventInfo(context, from, message, contentIntent);
 
            // Hide the notification after its selected
            noti.defaults |= Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS;
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(p_id, noti);*/
            
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);        
            mBuilder.setSmallIcon(R.drawable.ic_launcher);
            Log.v("", "rIntent" +rintent.getStringExtra("title"));
            mBuilder.setContentTitle(rintent.getStringExtra("title"));       
            mBuilder.setContentText(rintent.getStringExtra("message"));
            mBuilder.setAutoCancel(true);
            
            int requestId = (int) System.currentTimeMillis();
             
            // Creates the PendingIntent
             PendingIntent notifyPendingIntent =  PendingIntent.getActivity(context,requestId,intent, PendingIntent.FLAG_CANCEL_CURRENT);
 
             // Puts the PendingIntent into the notification builder
             mBuilder.setContentIntent(notifyPendingIntent);
 
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(p_id, mBuilder.build());
        }
    }
     
    public String getProspectId(){
        return TAG;        
    }
     
    public void setAlarmManager(Context thisActivity, int p_id, String name, String number, int STAGE){
        AlarmManager am = (AlarmManager) thisActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(thisActivity, RenoKingNotifications.class);        
//      intent.putExtra("prospect", prospect);
        intent.putExtra("id", p_id);
        intent.putExtra("name", name);
        intent.putExtra("title","Confirm Schedule Appointment");
        intent.putExtra("status", thisActivity.getString(R.string.status_confirm_schedule));
        intent.putExtra("stage", STAGE);
        intent.putExtra("number", number);
         
        DatabaseConnection db = new DatabaseConnection(thisActivity);
        Calendar remind_cal = db.getReminderDate(STAGE, ""+p_id);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
        Log.v("", "formatted date for notification: " + sdf.format(remind_cal.getTime()));
        String message = sdf.format(remind_cal.getTime()) +" - "+ name;        
             
        intent.putExtra("message", message);
 
        PendingIntent pendingIntent = PendingIntent.getBroadcast(thisActivity, p_id,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
         
        Calendar cal_curr = Calendar.getInstance();
//        long interval = cal_curr.getTimeInMillis() + TimeConversion.ONE_MINUTE * 1;
        long interval = cal_curr.getTimeInMillis() + TimeConversion.ONE_MINUTE * 30;
         
        Log.v("","interval: "+ interval);        
        cal_curr.setTimeInMillis(interval);        
        Log.v("","notify at: "+ cal_curr.getTime().toString());
        am.set(AlarmManager.RTC_WAKEUP,interval, pendingIntent);         
        /*if (remind_cal.getTimeInMillis() > cal_curr.getTimeInMillis()){
            Log.v("","interval: "+ interval);        
            cal_curr.setTimeInMillis(interval);        
            Log.v("","notify at: "+ cal_curr.getTime().toString());
            am.set(AlarmManager.RTC_WAKEUP,
                    interval, pendingIntent);
        }else{
            do_notify = false;
            nm.cancel(p_id);
            db.openDataBase();
            db.executeUpdate("update tbl_prospects set prospect_status = 'dead' where prospect_id = " +p_id);
            db.executeUpdate("update tbl_schedule set active = 'no' where request_id = " +p_id);
            db.close();
        }*/
    }
}
