/*
 * Copyright (C) 2010 Prasanta Paul, http://prasanta-paul.blogspot.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rifluxyss.therenoking.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.rifluxyss.therenoking.ProspectDetails;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.RenoPreferences;

/**
 * Phone Away Widget
 * 
 * @author prasanta
 * 
 */
public class RenoKingReceiver extends BroadcastReceiver {
	
	String TAG = "com.rifluxyss.therenoking";

	ITelephony telephonyService;
	TelephonyManager tm;
	String origin;
	String body;

  @Override
  public void onReceive(Context context, Intent intent) {
		Log.v("", "action: " + intent.getAction());
		Log.v("", "id: " + RenoPreferences.getCalledId(context));
		Log.v("", "notification status : " + RenoPreferences.getStatus(context));
		// TODO Auto-generated method stub
     
		     
		/*
		 * if (intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) != null){
		 * Log.v("",
		 * "phone_number: "+intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
		 * RenoPreferences.setNumber(context,
		 * intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)); }
		 */
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// tm.listen(new PhoneListener(context),
		// PhoneStateListener.LISTEN_CALL_STATE);
		Log.v("", "" + tm.getCallState());
		if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) != null) {
			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
					TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.v("", "TelephonyManager.EXTRA_STATE_RINGING");
				openApp(context);
			} else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
					.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				Log.v("", "TelephonyManager.EXTRA_STATE_IDLE");
				openApp(context);
			} else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
					.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				Log.v("", "TelephonyManager.EXTRA_STATE_OFFHOOK");
				openApp(context);
			}
		}
	}

	@SuppressWarnings("unused")
	private void openApp(final Context context) {
		// boolean isAppOpen = false;
		// ActivityManager activityManager = (ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE);
		// List<RunningTaskInfo> recentTasks =
		// activityManager.getRunningTasks(Integer.MAX_VALUE);
		//
		// for (int i = 0; i < recentTasks.size(); i++) {
		// Log.v("Executed app", "Application executed : "
		// +recentTasks.get(i).baseActivity.getPackageName());
		// if
		// (recentTasks.get(i).baseActivity.getPackageName().equals(context.getPackageName())){
		// isAppOpen = true;
		// break;
		// }
		// }
		// if (!isAppOpen){
		if (!RenoPreferences.getNumber(context).equals("")) {
			try {
				final DatabaseConnection db = new DatabaseConnection(context);
				db.openDataBase();
				String swissNumberStr = RenoPreferences.getNumber(context);
				PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
				try {
					PhoneNumber swissNumberProto = phoneUtil.parse(
							swissNumberStr, "CA");
					swissNumberStr = String.valueOf(swissNumberProto
							.getNationalNumber());
					Log.v("",
							"getCountryCode: "
									+ swissNumberProto.getCountryCode());
					Log.v("",
							"getNationalNumber: "
									+ swissNumberProto.getNationalNumber());
					Log.v("",
							"getPreferredDomesticCarrierCode: "
									+ swissNumberProto
											.getPreferredDomesticCarrierCode());
					Log.v("",
							"hasCountryCode: "
									+ swissNumberProto.hasCountryCode());
					Log.v("", "getRawInput: " + swissNumberProto.getRawInput());
				} catch (NumberParseException e) {
					System.err.println("NumberParseException was thrown: "
							+ e.toString());
				}
				final Cursor c = db
						.executeQuery("select * from tbl_prospects where phone_number like '%"
								+ RenoPreferences.getNumber(context)
								+ "%' and prospect_id = "
								+ RenoPreferences.getCalledId(context));
				Log.v(TAG,
						"Phone Query: "
								+ "select * from tbl_prospects where phone_number like '%"
								+ RenoPreferences.getNumber(context)
								+ "%' and prospect_id = "
								+ RenoPreferences.getCalledId(context));
				Log.e("", "phone query count==>>" + c.getCount());

				if (c != null && c.moveToNext()) {
					Log.v(TAG, "Opening app ************* ");
					RenoPreferences.setNumber(context, "");
					Log.v("Propsetec",
							"prospect_id: "+ c.getString(c.getColumnIndex("prospect_id")));
					// Intent intent = new Intent(context,
					// ProspectDetails.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// intent.putExtra("status",
					// RenoPreferences.getStatus(context));
					// if (Build.VERSION.SDK_INT <= 10){
					// intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
					// Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					// }
					// intent.putExtra("prospect_id",
					// Integer.parseInt(c.getString(c.getColumnIndex("prospect_id"))));
					// if (c != null) c.close();
					// if (db != null) db.close();
					// context.startActivity(intent);

					// if(TelephonyManager.CALL_STATE_RINGING == 1){
					// if(TelephonyManager.CALL_STATE_RINGING == 0){
					// Intent intent = new Intent(context,
					// ProspectDetails.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// intent.putExtra("status",
					// RenoPreferences.getStatus(context));
					// if (Build.VERSION.SDK_INT <= 10){
					// intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
					// Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					// }
					// intent.putExtra("prospect_id",
					// Integer.parseInt(c.getString(c.getColumnIndex("prospect_id"))));
					// if (c != null) c.close();
					// if (db != null) db.close();
					// context.startActivity(intent);
					// }
					// }
//					if (tm.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Log.e("", "This Phone is called");
								Intent intent = new Intent(context,
										ProspectDetails.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra("status",RenoPreferences.getStatus(context));
								if (Build.VERSION.SDK_INT <= 10) {
									intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
											| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
								}
								intent.putExtra("prospect_id",Integer.parseInt(c.getString(c.getColumnIndex("prospect_id"))));
								if (c != null)
									c.close();
								if (db != null)
									db.close();
								context.startActivity(intent);
							}					
						}, 7000);
//					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG,
						"Error in accessing Telephony Manager: " + e.toString());
			}
			// }else{
			// Intent intent = new Intent(context, ListProspects.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(intent);
			// }
		}
	}

	/*
	 * class PhoneListener extends PhoneStateListener{ Context context;
	 * PhoneListener(Context context){ this.context = context; }
	 * 
	 * @Override public void onCallStateChanged(int state, String
	 * incomingNumber) { switch(state) { case
	 * TelephonyManager.CALL_STATE_RINGING: Log.v(TAG,
	 * "TelephonyManager.CALL_STATE_RINGING"); Log.e(TAG,
	 * "CALL_STATE_RINGING: "+incomingNumber); break; case
	 * TelephonyManager.CALL_STATE_OFFHOOK: Log.v(TAG,
	 * "TelephonyManager.CALL_STATE_OFFHOOK"); Log.v(TAG,
	 * "incomingNumber: "+incomingNumber); // openApp(context); break; case
	 * TelephonyManager.CALL_STATE_IDLE: Log.v(TAG,
	 * "TelephonyManager.CALL_STATE_IDLE"); Log.v(TAG,
	 * "incomingNumber: "+incomingNumber); break; }
	 * super.onCallStateChanged(state, incomingNumber); } }
	 */
}
