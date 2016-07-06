package com.rifluxyss.therenoking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.rifluxyss.therenoking.utils.RenoPreferences;
import com.rifluxyss.therenoking.utils.Utilities;

public class GCMIntentService extends GCMBaseIntentService {

	String strTokenID = "";
	String strUserId = "";
	
	
	public GCMIntentService() {
		super(Utilities.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context arg0, String registrationId) {
		strTokenID = registrationId;
		RenoPreferences.setGCMRegisterID(arg0, registrationId);
		
	}
	
    @Override
	protected void onUnregistered(Context arg0, String arg1) {
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.e(TAG, "new message= ");
		
		String title 	= arg1.getStringExtra("title");
		String message 	= arg1.getStringExtra("msg");

		if(title == null)
			title = "";
		
		if(message == null)
			message = "";
		
		CharSequence contentTitle = title;	//intent.getStringExtra("me");              // ticker-text		
		CharSequence contentMessage = ""+message; 	//intent.getStringExtra("me");  	// message title

		Log.e("", "title==>>"+contentTitle);
		Log.e("", "contentMessage==>>"+contentMessage);
		
	
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);		
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setContentTitle(contentTitle.toString().split("-")[0]);
        mBuilder.setContentText(contentMessage);   
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        
        Intent notifyIntent =   new Intent(this, SplashActivity.class);
        notifyIntent.putExtra("title", contentTitle);
     	// Sets the Activity to start in a new, empty task
     	//notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     	
     	  int requestId = (int) System.currentTimeMillis();
     
     	// Creates the PendingIntent
     	PendingIntent notifyPendingIntent =  PendingIntent.getActivity(arg0,requestId,notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

     	// Puts the PendingIntent into the notification builder
     	mBuilder.setContentIntent(notifyPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
        
	}
	
	

	@Override
	protected void onError(Context arg0, String errorId) {
	}
	
	

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
	
	
}
