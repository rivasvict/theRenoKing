package com.rifluxyss.therenoking;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import sample.appsforyourdomain.gmailsettings.GmailSettingsService;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bugtracker.ExceptionReporter;
import bugtracker.SettingsApi;
import bugtracker.Util;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.gdata.util.ServiceException;
import com.rifluxyss.therenoking.generic.C;
import com.rifluxyss.therenoking.generic.Generic;
import com.rifluxyss.therenoking.services.RenoKingReceiver;
import com.rifluxyss.therenoking.tasks.SyncProspectsThread;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.EnumHandler;
import com.rifluxyss.therenoking.utils.RenoPreferences;
import com.rifluxyss.therenoking.utils.Utilities;

public class SplashActivity extends TheRenoKing implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {
	DatabaseConnection dbConnect;
	private SharedPreferences prefs;
	Activity thisActivity;
	LinearLayout progressLyt;
	ProgressBar progress;
	TextView lblError;
	Button btnLogin;	
	RenoKingReceiver renoReceiver;
	SyncProspectsThread SyncProspects;
	Account[] accounts;
	public static String person = "";
	DatabaseConnection connection;
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();   
    String regid, strGmail;
    Context context;
    
    /**
	 * Google plus variables
	 * 
	 */

	private static final String TAG = "android-plus-quickstart";
	private static final int STATE_DEFAULT = 0;
	private static final int STATE_SIGN_IN = 1;
	private static final int STATE_IN_PROGRESS = 2;
	private static final int RC_SIGN_IN = 0;
	private static final int DIALOG_PLAY_SERVICES_ERROR = 0;
	private static final String SAVED_PROGRESS = "sign_in_progress";
	private GoogleApiClient mGoogleApiClient;
	private int mSignInProgress;
	private PendingIntent mSignInIntent;
	private int mSignInError;
	private SignInButton lblGooglePlus;
	String strLoginFailType = "";
	private final int GOOGLE_PLUS = 101;
	
	String strEmail = "", strUserId = "", strFirstName = "", strLastName = "", strDOB = "", strGender = "", strZipCode = "", strConfirmPassword = "",
			strPassword = "", strlongitude = "", strlatitude = "", strprofile = "", strTag = "1", strCity = "", strState = "", strCountry = "",
			strAddress = "", strPlatForm = "", strFuturePayment = "", strRank = "", strDeviceToken = "", strCheckinestablishid, strGoogleToken = "",
			strChekinArr = "", strEstablishType = "", strParentEstablishID = "0", strChildEstablishID = "0";


    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		thisActivity = this;
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		dbConnect = new DatabaseConnection(this);
		context	 = getApplicationContext();
		try {
			dbConnect.createDataBase();
			Util.pushActivityInfo(thisActivity, Thread.currentThread(), "Database created!!!");
		} catch (IOException e) {			
			e.printStackTrace();
		}
		dbConnect.close();	
		init();

		mGoogleApiClient = buildGoogleApiClient();
		
//		if(mGoogleApiClient != null){
//			loadProspects();
//		}
		
	}	

	
		private GoogleApiClient buildGoogleApiClient() {
			return new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
					.addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
		}

	private void init() {
		
		
		RenoPreferences.setNumber(thisActivity, "");
		
		try
		{
			
		dbConnect.openDataBase();
		String check = "select * from tbl_prospects where google_id = 'a'";
		 Cursor cr = dbConnect.executeQuery(check);
		 Log.e("","check ======"+check);
		 Log.e("","cr.getCount() ======"+cr.getCount());
		 }catch(Exception e)
		 {
		 e.printStackTrace();
		 Log.e("","inside exception ======");
		 String upgradeQuery = "ALTER TABLE tbl_prospects ADD COLUMN google_id TEXT ";
		 dbConnect.executeUpdate(upgradeQuery);
		 }
		
		dbConnect.close();
		
/*		GmailSettingsService service = null;
		try {
			service = new GmailSettingsService("The+Reno+King", "therenoking.ca", "davyn@therenoking.ca", "Presence11");
			List users=new ArrayList();
			users.add("davyn");
			service.createLabel(users, "status updates");
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnTouchListener(new OnTouchEvent(R.drawable.login_normal, R.drawable.login_over));
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Util.pushActivityInfo(thisActivity, Thread.currentThread(), "Login Button Clicked");
//				try {
//					Intent ioauth = new Intent(thisActivity, RequestTokenActivity.class);
//					startActivity(ioauth);
//				} catch (Exception e) {
//					Util.insertCaughtException(e, thisActivity);
//				}
////				Intent ioauth = new Intent(thisActivity, RequestTokenActivity.class);
////				startActivity(ioauth);				
////				new FormJsonFromExceptionDetails(thisActivity).execute();
				
				try {
					mGoogleApiClient	=	buildGoogleApiClient();
					mSignInProgress = STATE_SIGN_IN;
					mGoogleApiClient.connect();
				} catch(Exception e) {
					e.printStackTrace();
				}	
			}
		});

		progress = (ProgressBar) findViewById(R.id.progress);
		progress.setBackgroundResource(R.drawable.loading_bg);
		progress.setProgressDrawable(getResources().getDrawable(R.drawable.progress_custom));
		progressLyt = (LinearLayout) findViewById(R.id.progressLyt);
		lblError = (TextView) findViewById(R.id.lblError);
		
		
		accounts = AccountManager.get(this).getAccounts();
		    Log.e("", "Size: " + accounts.length);
		    for (Account account : accounts) {

		        String possibleEmail = account.name;
		        String type = account.type;

		        if (type.equals("com.google")) {
		            strGmail = possibleEmail;

		            Log.e("", "Emails: " + strGmail);
		            break;
		        }
		    }
		    
		    String mailid = getUsername();
		    Log.v("", "Current user"+mailid);
		    
		   

		if(Utilities.haveInternet(thisActivity)){
			try{
				SettingsApi errorhandler = new SettingsApi(thisActivity);
				errorhandler.start();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		ExceptionReporter.register(thisActivity);
	}

	@Override
	protected void onResume() {
		super.onResume();	
		/*Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(thisActivity).getAccounts();
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        String possibleEmail = account.name;
		        Log.e("", "possibleEmail==>>"+possibleEmail);
		        
		    }
		}*/
		
		// Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);    		
            regid = getRegistrationId(context);
            if (regid.equals("")) {
                registerInBackground();
            }        
        } else {
            Log.i("", "No valid Google Play Services APK found.");
            Toast.makeText(thisActivity, "No valid Google Play Services APK found.", Toast.LENGTH_LONG).show();
        }
        
//		if(!Generic.PROSPECTS_LOAD){
//			Log.e("Splash", "Load Prospect");
//		 	loadProspects();
//		}else{
//			Log.e("Splash", "Does not load Prospect");
//			/*Intent i = new Intent(SplashActivity.this, ListProspects.class);
//			startActivity(i);
//			finish();*/
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	};

	Handler rHandler = new Handler(){
		public void handleMessage(Message msg) {
			Log.v("", "what: "+msg.what);    		
			switch (msg.what) {
			case EnumHandler.SYNC_PROSPECTS:	//101	
				Util.setEndTime();
				Util.pushPerformanceInfo(thisActivity, Thread.currentThread(), 30, "Syncing contacts takes more time to update.");
				Intent i = new Intent(SplashActivity.this, ListProspects.class);
				startActivity(i);
				finish();
				break;
			}
		};
	};

	private void loadProspects(){
		if (C.isOAuthSuccessful(prefs)) {
			// OAuth successful, try getting the contacts
			Log.e(C.TAG, "OAuth successful, try getting the contacts");
			Log.e(C.TAG, "PROSPECTS_LOAD==>>"+Generic.PROSPECTS_LOAD);
			if (Utilities.haveInternet(this) && !Generic.PROSPECTS_LOAD){
				btnLogin.setVisibility(View.GONE);
				lblError.setVisibility(View.VISIBLE);
				progressLyt.setVisibility(View.VISIBLE);
				Generic.PROSPECTS_LOAD = true;
				registerCallReceiver();
				Util.pushActivityInfo(thisActivity, Thread.currentThread(), "Sync Contacts Thread started.");
				Util.setStartTime();
				SyncProspects = new SyncProspectsThread(this, progress, rHandler);
				SyncProspects.execute("");
//				new RetreiveFeedTask().execute();
			}else{
				Generic.PROSPECTS_LOAD = false;
				dbConnect.openDataBase();
				Cursor c = dbConnect.executeQuery("select * from tbl_prospects");
				if (c != null && c.moveToNext()){
					Intent i = new Intent(SplashActivity.this, ListProspects.class);
					startActivity(i);
					finish();
				}else{
					Util.pushServerResponseInfo(thisActivity, Thread.currentThread(), getStringResource(R.string.INTERNET_PROBLEM));
					btnLogin.setVisibility(View.VISIBLE);
					lblError.setVisibility(View.VISIBLE);
					lblError.setText(getStringResource(R.string.INTERNET_PROBLEM));
					progressLyt.setVisibility(View.GONE);
				}
				c.close();
				dbConnect.close();
			}
			
		} else {
//			Util.pushServerResponseInfo(thisActivity, Thread.currentThread(), "OAuth failed, no tokens, Click on the Do OAuth Button");
			Log.e(C.TAG, "OAuth failed, no tokens, Click on the Do OAuth Button.");
			Generic.PROSPECTS_LOAD = false;
			//			btnLogin.setVisibility(View.VISIBLE);
			//			lblError.setVisibility(View.VISIBLE);
			//			lblError.setText(getStringResource(R.string.OAUTH_FAILED));
			//			progressLyt.setVisibility(View.GONE);
		}
		
	
	}
	 public String getUsername() {
		    AccountManager manager = AccountManager.get(this);
		    accounts = manager.getAccountsByType("com.google");
		    List<String> possibleEmails = new LinkedList<String>();

		    for (Account account : accounts) {
		        // TODO: Check possibleEmail against an email regex or treat
		        // account.name as an email address only for certain account.type
		        // values.
		        possibleEmails.add(account.name);
		    }

		    if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
		        String email = possibleEmails.get(0);
		        String[] parts = email.split("@");
		        if (parts.length > 0 && parts[0] != null)
		            return parts[0];
		        else
		            return null;
		    } else
		        return null;
		}
	
	
	/*Creating labels for the authenticated admin user.*/
	class RetreiveFeedTask extends AsyncTask<String, Void, Integer> {

		private Exception exception;

		protected Integer doInBackground(String... urls) {
			try {
				GmailSettingsService service = new GmailSettingsService(C.APP_NAME, "therenoking.ca", "info", "presence");
				//GmailSettingsService service = new GmailSettingsService(C.APP_NAME, "therenoking.ca", "info", "kingdavyn11");

				List users=new ArrayList();
				users.add("davyn");
				service.createLabel(users, "Stage 1");
				service.createLabel(users, "Stage 2");
				service.createLabel(users, "Stage 3");
				service.createLabel(users, "Stage 4");
				service.createLabel(users, "Stage 5");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			//	    	catch (OAuthException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			} 
			return null;	        
		}

		protected void onPostExecute(Integer feed) {

		}
	}

	@Override
	protected void onDestroy() {
		if (SyncProspects != null){
			SyncProspects.cancel(true);
			SyncProspects = null;
		}
		Generic.PROSPECTS_LOAD = false;
		super.onDestroy();
	}

	private void registerCallReceiver(){
		if(renoReceiver != null){
			// Receiver is already registered
			return;
		}
		Log.i("", "Register Receiver.........");
		IntentFilter callinf = new IntentFilter("android.intent.action.PHONE_STATE");
		callinf.addAction("android.intent.action.NEW_OUTGOING_CALL");
		renoReceiver = new RenoKingReceiver();
	}	
	
	
	/*
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	
	
	private String getRegistrationId(Context context) {
		
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

	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
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
	

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {	
	
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	        
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("", "This device is not supported.");
	            Toast.makeText(thisActivity, "This device is not supported.", Toast.LENGTH_LONG).show();
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
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
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i("", "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	    RenoPreferences.setGCMRegisterID(thisActivity, regId);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case GOOGLE_PLUS:
				if (data != null) {
					String strResponse = data.getStringExtra("log_in");
					if (strResponse.equals("")) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
								builder.setMessage("Login Successfully...").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
								AlertDialog alert = builder.create();
								alert.show();
							}
						});
					}
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
							builder.setMessage("Could not Login successfully.").setCancelable(false)
									.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
							AlertDialog alert = builder.create();
							alert.show();
						}
					});
				}
				break;				
			case RC_SIGN_IN:
				try {
					if(resultCode	==	RESULT_OK) {
						mGoogleApiClient.connect();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}				
				break;
			default:
				break;
			}
		} else {
			Toast.makeText(thisActivity, "Google plus action cancelled", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
		mSignInIntent = result.getResolution();
		mSignInError = result.getErrorCode();
		if (mSignInProgress == STATE_SIGN_IN) {
			resolveSignInError();
		}
		// onSignedOut();
	}

	private void resolveSignInError() {
		if (mSignInIntent != null) {
			try {
				mSignInProgress = STATE_IN_PROGRESS;
				startIntentSenderForResult(mSignInIntent.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} catch (SendIntentException e) {
				Log.i(TAG, "Sign in intent could not be sent: " + e.getLocalizedMessage());
				e.printStackTrace();
				mSignInProgress = STATE_SIGN_IN;
				mGoogleApiClient.connect();
			}
		} else {
			showDialog(DIALOG_PLAY_SERVICES_ERROR);
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		try {
			Log.i(TAG, "onConnected");
//			Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			person = Plus.AccountApi.getAccountName(mGoogleApiClient);
			Plus.MomentsApi.load(mGoogleApiClient);
//
//			Log.e("", "Person id: " + currentUser.getId());
//			Log.e("", "Person name: " + currentUser.getDisplayName());
//			Log.e("", "Person Birthday: " + currentUser.getBirthday());
			Log.e("", "Person email: " + person);
//
//			if (currentUser.getGender() == 1) {
//				strGender = "female";
//			} else if (currentUser.getGender() == 0) {
//				strGender = "male";
//			} else if (currentUser.getGender() == 2) {
//				strGender = "other";
//			}
//
//			if (currentUser.getDisplayName().contains("")) {
//				strFirstName = currentUser.getDisplayName().split(" ")[0];
//				strLastName = currentUser.getDisplayName().split(" ")[1];
//			} else {
//				strFirstName = currentUser.getDisplayName();
//				strLastName = "";
//			}
			strEmail = person;
//			strPlatForm = "android";
//			strGoogleToken = currentUser.getId();
//			strDeviceToken = RenoPreferences.getGCMRegisterID(thisActivity);
//			if (currentUser.getBirthday() != null) {
//				strDOB = currentUser.getBirthday();
//			} else {
//				strDOB = "";
//			}
//			strPassword = "";
//			strZipCode = "";
            loadProspects();
			Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
			mSignInProgress = STATE_DEFAULT;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onSignedOut() {
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onResult(LoadPeopleResult arg0) {
		// TODO Auto-generated method stub
		
	}


}


