package com.rifluxyss.therenoking;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import bugtracker.BugUserPreferences;
import bugtracker.DebugIconView;
import bugtracker.FormJsonFromExceptionDetails;
import bugtracker.Util;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rifluxyss.therenoking.services.RenoKingReceiver;
import com.rifluxyss.therenoking.utils.RenoPreferences;
import com.rifluxyss.therenoking.utils.Utilities;

public class TheRenoKing extends Activity {	

    Activity superActivity;
	DebugIconView gest;
	int height =0,width=0;
	boolean is_dialog_closed = true;
    GoogleCloudMessaging gcm;
    Context context;
    String regid;
	RenoKingReceiver renoReceiver;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		superActivity = this;        
		context = getApplicationContext();
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float ldpi = 0.75f;
		float mdpi = 1.0f;
		float hdpi = 1.5f;
		float xhdpi = 2.0f;
		float xxdpi = 3.0f;
		
		if (metrics.density == ldpi){
			Log.e("","ldpi");
			height = 35;
			width = 37;
		} else if (metrics.density == mdpi){	
			Log.e("","mdpi");
			height = 50;
			width = 50;
		} else if (metrics.density == hdpi){	
			Log.e("","hdpi");
			height = 70;
			width = 72;
		} else if (metrics.density == xhdpi){	
			Log.e("","xhdpi");
			height = 95;
			width = 97;
		} else if (metrics.density == xxdpi){	
			Log.e("","xxdpi");
			height = 95;
			width = 97;
		}  
//              avoid crash in the app
//				Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);
			}
			
//			private Thread.UncaughtExceptionHandler handleAppCrash = new Thread.UncaughtExceptionHandler() {
//				@Override
//				public void uncaughtException(Thread thread, Throwable exception) {
//					printE("exception ======="+exception.toString());
//					android.os.Process.killProcess(android.os.Process.myPid());  
//					System.exit(10); 
//				}
//			};
			
			public static void print(String message) {
				Log.i("Mehab", message);
			}

			public void printE(String message) {
				Log.e("Mehab", message);
			}

			public void printV(String message) {
				Log.v("Mehab", message);
			}


	@Override
	protected void onResume() {    	
		super.onResume();
		Log.v("", "OnResume() called");
		if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);    		
            regid = getRegistrationId(context);
            if (regid.equals("")) {
                registerInBackground();
            }        
        } 
		
		registerCallReceiver();
		
		
		if (BugUserPreferences.getDebugMode(superActivity).equals("1")){ 
			final Bitmap debug = BitmapFactory.decodeResource(getResources(),R.drawable.debug_icon_bottom);
			gest = new DebugIconView(superActivity,debug, width, height);
			gest.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {				
					if(Util.is_dialog_closed){
						Util.showalert(superActivity);
						Util.is_dialog_closed = false;
					}
				}
			});
			gest.show();
		}
	}

	private void registerCallReceiver() {
		// TODO Auto-generated method stub
		if(renoReceiver != null){
			// Receiver is already registered
			Log.i("", "Register Receiver if.........");
			return;
		}
		Log.i("", "Register Receiver else.........");
		IntentFilter callinf = new IntentFilter("android.intent.action.PHONE_STATE");
		callinf.addAction("android.intent.action.NEW_OUTGOING_CALL");
		renoReceiver = new RenoKingReceiver();
	}

	private String getRegistrationId(Context context2) {
		   final SharedPreferences prefse = getGCMPreferences(context);
		    String registrationId = prefse.getString(PROPERTY_REG_ID, "");
		    
		    
			
		    if (registrationId.equals("")) {
		        Log.i("", "Registration not found.");
		        return "";
		    }
		    // Check if app was updated; if so, it must clear the registration ID
		    // since the existing regID is not guaranteed to work with the new
		    // app version.
		    int registeredVersion = prefse.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		    int currentVersion = getAppVersion(context);
		    if (registeredVersion != currentVersion) {
		        Log.i("", "App version changed.");
		        return "";
		    }
		    return registrationId;
	}
	
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	private void registerInBackground() {
		// TODO Auto-generated method stub
		   new AsyncTask<Integer, Long, String>(){
		       
				@Override
				protected String doInBackground(Integer... params) {
					String msg = "";
		            try {
		                if (gcm == null) {
		                    gcm = GoogleCloudMessaging.getInstance(context);
		                }
		                regid = gcm.register(Utilities.SENDER_ID);
		                msg = "Device registered, registration ID=" + regid;

		               

		                // For this demo: we don't need to send it because the device
		                // will send upstream messages to a server that echo back the
		                // message using the 'from' address in the message.

		                // Persist the regID - no need to register again.
		                storeRegistrationId(context, regid);
		                
		                // You should send the registration ID to your server over HTTP,
		                // so it can use GCM/HTTP or CCS to send messages to your app.
		                // The request to your server should be authenticated if your app
		                // is using accounts.
		               // sendRegistrationIdToBackend();
		            } catch (IOException ex) {
		                msg = "Error :" + ex.getMessage();
		                // If there is an error, don't just keep trying to register.
		                // Require the user to click a button again, or perform
		                // exponential back-off.
		            }
		            return msg;
				}
				
				protected void onPostExecute(String result) {
					Log.e("", "result==>>"+result);
				};

		    }.execute(null, null, null);
	}

	private void storeRegistrationId(Context context2, String regId) {
		// TODO Auto-generated method stub
		final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i("", "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	    RenoPreferences.setGCMRegisterID(superActivity, regId);
	}

	private boolean checkPlayServices() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		try{
			if (gest != null)
				gest.remove();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public ProgressDialog showLoading(){
		ProgressDialog loading = ProgressDialog.show(superActivity, null, "Please wait");
		return loading;
	}

	public void showCancellableLoading(ProgressDialog loading, String message){
		loading = ProgressDialog.show(superActivity, null, message);
		loading.setCancelable(true);
	}

	public void dismissLoading(ProgressDialog loading){
		if (loading != null && loading.isShowing())
			loading.dismiss();
	}

	public String getStringResource(int res_id){    	
		return superActivity.getResources().getString(res_id);    	
	}

	class OnTouchEvent implements OnTouchListener {
		int normal,select,normalColor,selectColor;

		public OnTouchEvent(int normal, int select) {
			this.normal = normal;
			this.select = select;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int touchAction = event.getAction();
			Button btn = null;
			try{
				if(v.getParent()== btn || v == btn)
					btn  = (Button) v;				

			}catch (Exception e) {
				e.printStackTrace();
			}			
			if (touchAction == MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(select);
			} else if (touchAction == MotionEvent.ACTION_UP
					|| touchAction == MotionEvent.ACTION_CANCEL) {
				v.setBackgroundResource(normal);
			}
			return false;
		}
	}

	class ImageTouchEvent implements OnTouchListener {
		int normal,select;

		public ImageTouchEvent(int normal, int select) {
			this.normal = normal;
			this.select = select;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int touchAction = event.getAction();
			ImageView img = (ImageView) v;
			if (touchAction == MotionEvent.ACTION_DOWN) {
				img.setImageResource(select);
			} else if (touchAction == MotionEvent.ACTION_UP
					|| touchAction == MotionEvent.ACTION_CANCEL) {
				img.setImageResource(normal);
			}
			return false;
		}
	}

	public void showalert(String message, int logCount){
		try {
			AlertDialog.Builder dialog = new AlertDialog.Builder(superActivity);
			dialog.setTitle(getString(R.string.error_report));
			dialog.setPositiveButton(getString(R.string.button_ok),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					dialog.cancel();
					is_dialog_closed = true;
				}
			});
			
			dialog.setNegativeButton(getString(R.string.button_uploadNow), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					new FormJsonFromExceptionDetails(superActivity).execute();
					is_dialog_closed = true;
				}
			});
			dialog.setMessage("" + message);
			dialog.setCancelable(true);
			dialog.create();
			if(logCount == 0){
				dialog.show().getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
			}else{
				dialog.show();
			}
			return;
		} catch (Exception e) {
		}
	}
}
