package com.rifluxyss.therenoking;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import bugtracker.BugUserPreferences;
import bugtracker.DebugIconView;
import bugtracker.ExceptionReporter;
import bugtracker.Util;

import com.google.gdata.util.ServiceException;
import com.rifluxyss.therenoking.beans.Data;
import com.rifluxyss.therenoking.beans.ProspectData;
import com.rifluxyss.therenoking.beans.Prospects;
import com.rifluxyss.therenoking.generic.C;
import com.rifluxyss.therenoking.generic.CalendarEventsManagement;
import com.rifluxyss.therenoking.generic.TimeConversion;
import com.rifluxyss.therenoking.network.APIClient;
import com.rifluxyss.therenoking.services.RenoKingNotifications;
import com.rifluxyss.therenoking.tasks.SendMail;
import com.rifluxyss.therenoking.tasks.ShowCampaigns;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.EnumHandler;
import com.rifluxyss.therenoking.utils.ResultHandler;
import com.rifluxyss.therenoking.utils.Utilities;

@SuppressLint("ValidFragment")
public class ProspectDetails extends FragmentActivity  {

	Activity thisActivity;
	DatabaseConnection db;
	ProgressDialog loading;
	ProgressDialog loading_email;
	TextView lblDeadProspect;
	LinearLayout AddContent;

	TextView lblName;
	TextView lblEmail;
	TextView lblPhone;
	TextView lblCity;
	TextView lblAddress;

	ImageView imgNotes;
	int STAGE = 1;
	Account[] accounts;
	String strGmail,  mailid;
	int STAGE_CATEGORY = -1; 

	DialogFragment newFragment;
	
	Boolean contacts;
	
	final int STAGE_ONE = 1;
	final int STAGE_TWO = 2;
	final int STAGE_THREE = 3;
	final int STAGE_FOUR = 4;
	final int STAGE_FIVE = 5;
	final int STAGE_SIX = 6;
	final int STAGE_EIGHT = 8;
	
	String Confirmed_Scheduled_Date;
	
	public static final int STAGE1_SCHEDULE_APT = 101;

	public static final int STAGE2_SCHEDULE_APT = 0;
	public static final int STAGE2_FOLLOWUP_APT = 1;
	public static final int STAGE2_RESCHEDULE_APT = 2;
	public static final int STAGE2_CONFIRM_APT = 3;
	public static final int STAGE2_CANCEL_APT_FORNOW = 4;
	public static final int STAGE2_CANCEL_APT_FOREVER = 5;

	public static final int STAGE3_ESTIMATE_DATE = 31;
	public static final int STAGE3_DECLINE = 32;//
	public static final int STAGE3_WHEN_ESTIMATE_COMPLETED = 33;//
	public static final int STAGE3_PROSPECT_NOT_SHOW = 34;
	public static final int STAGE5_JOB_COMPLETED = 3;
	public static final int STAGE4_PROJECT_START = 41;//
	public static final int STAGE4_WAITING_OTHER_ESTIMATES = 42;
	//	public static final int STAGE4_FINISH_DATE = 43;
	public static final int STAGE4_DECLINE = 44;//

	//	public static final int STAGE5_COMMENTS = 51;
	public static final int STAGE5_FINISH_DATE = 51;
	
	/*public static final int STAGE6_CUSTOMERCARE1 = 61;
	public static final int STAGE6_SETCUSTOMERCARE = 62;*/

	public static final int STAGE6_CUSTOMERCARE_FOLLOWUP = 61;
	public static final int STAGE6_CUSTOMERCARE_SCHEDULE = 62;
	public static final int STAGE6_CUSTOMERCARE_RESCHEDULE = 63;


	public static final int STAGE1_LEFTMSG_NOANSWER = 10;
	public static final int STAGE2_LEFTMSG_NOANSWER = 20;


	public boolean STAGE3_ESTIMATE_NOT_COMPLETED = false;

	StringBuilder date_time;
	Prospects prospect;
	Data data;

	List<Pair<String, List<ProspectData>>> all;
	ArrayList<String> namelist;

	ArrayList<Integer> listCampaignID;				
	ArrayList<String> listCampaignName;

	//	String error_msg = "";
	int statusupdate;

	// Debug mode
	Date date;
	DebugIconView gest;
	int height =0,width=0;
	
	// New Flow chart
	ImageView imgSetDate;
	final int STAGE_ZERO = 0;
//	public static final int STAGE5_COMMENTS = 51;

	SharedPreferences prefs;
	boolean prosAddressCompleted = false;
	
	boolean checkStage2NoMSG = false;
	boolean checkProspectNotShow = false;
	
	boolean checkStage2FU = false;
	boolean checkStage3EC = false;
	
	boolean isProspectDead = false;
    int version = Build.VERSION.SDK_INT;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prospect_detail);
		thisActivity = this;
		db = new DatabaseConnection(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
		ExceptionReporter.register(thisActivity);
		getDebugIconSize();

		ShowCampaigns campaigns = new ShowCampaigns(thisActivity, rHandler);
		campaigns.execute();

		if (getIntent().getExtras().containsKey("prospect")){
			Log.v("", "&&&&&&&&&&&&&&&&&&&&&&&&& LIST PROSPECTS &&&&&&&&&&&&&&&&&&&&&&&&&");
			prospect = (Prospects) getIntent().getSerializableExtra("prospect");
			if (prospect == null){
				Log.v("", "Prospect is null, page finished.");
				finish();
			}
		} else{
			Log.v("", "&&&&&&&&&&&&&&&&&&&&&&&&& FROM NOTIFICATIONS &&&&&&&&&&&&&&&&&&&&&&&&&");
			Log.v("", "prospect_id: "+getIntent().getExtras().getInt("prospect_id"));
			prospect = getProspectData(getIntent().getExtras().getInt("prospect_id"));
			getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			runOnUiThread(new Runnable() {
				public void run() {
					showWhenNotificationClicks(getIntent().getExtras().getString("status"));
				}
			});
		}

		AddContent 	= (LinearLayout) findViewById(R.id.Addcontent);
		lblDeadProspect = (TextView) findViewById(R.id.lblDeadProspect);
		lblName 	= (TextView) findViewById(R.id.lblName);
		lblEmail 	= (TextView) findViewById(R.id.lblEmail);
		lblPhone 	= (TextView) findViewById(R.id.lblPhone); 
		lblCity 	= (TextView) findViewById(R.id.lblCity);
		lblAddress = (TextView) findViewById(R.id.lblAddress);
		imgNotes = (ImageView) findViewById(R.id.imgNotes);
		imgSetDate = (ImageView) findViewById(R.id.imgSetDate);	
		
		
		imgNotes.setOnClickListener(new OnClick());
		setProspectValues();	
		init();
		
		if(getIntent().getExtras().containsKey("open_date_picker")){
			Log.e("", "in the init function");
			if(getIntent().getExtras().getBoolean("open_date_picker") == true){
				newFragment = new DatePickerFragment("Schedule Appointment");
				STAGE_CATEGORY = STAGE2_SCHEDULE_APT;			
				newFragment.show(getSupportFragmentManager(), "datePicker" + STAGE2_SCHEDULE_APT);
			}
		}		
	}

	public void init(){
		Log.v("", "prospect: "+prospect.prospect_id);
		Log.v("", "stage: "+prospect.stage); 
		Log.v("", "prospect_status: "+prospect.prospect_status);

		if (prospect.prospect_status != null && prospect.prospect_status.equals("dead")){
			lblDeadProspect.setVisibility(0);
		}

		STAGE = Integer.parseInt(prospect.stage);
		Log.v("", "init STAGE: "+STAGE);  	
				
		if(STAGE != 1){
			setDataAndAdapter();
			imgSetDate.setVisibility(View.GONE);
		}else{
			imgSetDate.setVisibility(View.VISIBLE);
			imgSetDate.setTag(STAGE_ONE);
			imgSetDate.setOnClickListener(new OnClick());
		}	
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
	    
	    mailid = getUsername();
	    Log.v("", "Current user"+mailid);
	    
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
	private void setProspectValues(){
		prospect.name = prospect.name == null ? "" : prospect.name;
		prospect.email = prospect.email == null ? "" : prospect.email;
		prospect.phone_number = prospect.phone_number == null ? "" : prospect.phone_number;
		prospect.city = prospect.city == null ? "" : prospect.city;
		prospect.address = prospect.address == null ? "" : prospect.address;
		prospect.zipcode = prospect.zipcode == null ? "" : prospect.zipcode;
		prospect.province = prospect.province == null ? "" : prospect.province;
		prospect.referer = prospect.referer == null ? "" : prospect.referer;
		prospect.details = prospect.details == null ? "" : prospect.details;
		prospect.priority = prospect.priority == null ? "" : prospect.priority;
		prospect.min_max_budget = prospect.min_max_budget == null ? "" : prospect.min_max_budget;
		prospect.calendar_id = prospect.calendar_id == null ? "" : prospect.calendar_id;

        Log.v("","Calender id"+prospect.calendar_id);
        Log.v("","Name "+prospect.name);
		lblName.setText(prospect.name);
		lblEmail.setText(prospect.email);
		lblPhone.setText(prospect.phone_number);
		lblPhone.setVisibility(!prospect.phone_number.equals("") ? 0 : 8);
		lblCity.setText(prospect.city);
		if(prospect.referer.equals("")){
			prospect.referer = "others";
		}
		lblAddress.setText(prospect.address +"\n"+prospect.zipcode+"\n"+prospect.province+"\n"+prospect.referer
				+"\n"+prospect.details+"\n"+prospect.priority+"\n"+prospect.min_max_budget);
	}

	private void getDebugIconSize(){
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
	}

	Handler rHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			Log.v("", "what: "+msg.what);
			switch (msg.what) {
			case EnumHandler.SHOW_CAMPAIGN:	//106	
				Bundle b = msg.getData();
				listCampaignID = b.getIntegerArrayList("id");				
				listCampaignName = b.getStringArrayList("name");				
				break;
			case EnumHandler.PROSPECT_DID_NOT_SHOW:	//107	
				Bundle data = msg.getData();
				String strMessage = data.getString("message");
				Log.e("", "strMessage==>>"+strMessage);
				AddNotes(strMessage,107);				
				if (loading_email != null && loading_email.isShowing())
					loading_email.dismiss();
				//Utilities.showAlert(thisActivity, getString(R.string.email_sent));		
				break;		
			}
		};
	};	

	private boolean isProspectDead(){
		Util.pushActivityInfo(thisActivity, Thread.currentThread(), "Prospect is dead - id: (" +prospect.prospect_id+ ")");
		boolean dead = false;
		Cursor c = db.executeQuery("select * from tbl_prospects where prospect_id =" + prospect.prospect_id + " and prospect_status = 'dead'");
		dead = c != null && c.moveToNext();
		c.close();
		return dead;
	}

	String getDesc(String status){
		//		Log.v("", "status: "+status);
		String desc = "";
		if (status.equals("schedule"))
			desc = getString(R.string.apt_reminder_date);
		else if (status.equals("followup"))
			desc = getString(R.string.apt_followup_date);
		else if (status.equals("reschedule"))
			desc = getString(R.string.apt_reminder_date);
		else if (status.equals("confirm"))
			desc = getString(R.string.apt_confirm_date);
		else if (status.equals("dead"))
			desc = getString(R.string.apt_cancelled);	
		else if (status.equals("cancel"))
			desc = getString(R.string.apt_followup_date);
		else if (status.equals("estimate")){
			Log.e("", "Estimated due date");
			desc = getString(R.string.estimate_due_date);
		}else if (status.equals("estimate_to_do"))
			desc = getString(R.string.apt_estimate_to_do);
		else if (status.equals("estimate_complete"))
			desc = getString(R.string.estimate_completed_date);
		else if (status.equals("project_start"))
			desc = getString(R.string.project_start);
		else if (status.equals("waiting_estimate"))
			desc = getString(R.string.project_waiting_estimates);
		else if (status.equals("project_finish"))
			desc = getString(R.string.project_finish_date);
		else if (status.equals("job_completed"))
			desc = getString(R.string.project_completed_date);
		else if (status.equals("customer_care"))
			desc = getString(R.string.customer_care_date);
		return desc;
	}	

	@Override
	protected void onNewIntent(Intent intent) {
		//    	prospect = getProspectData(intent.getStringExtra("prospect_id")); 
		Util.pushActivityInfo(thisActivity, Thread.currentThread(), "onNewIntent calling for id: " + intent.getIntExtra("prospect_id", 0));
		Log.v("", "new intent pospect_id: "+intent.getIntExtra("prospect_id", 0));
		prospect = getProspectData(intent.getIntExtra("prospect_id", 0));

		setProspectValues();

		showWhenNotificationClicks(intent.getStringExtra("status"));
		if(STAGE != 1)
			setDataAndAdapter();

		super.onNewIntent(intent);
	}

	public void setDataAndAdapter(){
		AddContent.removeAllViews();
		imgSetDate.setVisibility(View.GONE);
		db.openDataBase();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
		SimpleDateFormat formatter1 = new SimpleDateFormat("MMMM dd, yyyy");

		boolean dead = isProspectDead();
		boolean isJobCompleted = false;

		try {
			for (int i = 0; i < STAGE; i++)	{
				int stage_iter = i + 1;
				RelativeLayout HeaderInflate 	= (RelativeLayout) View.inflate(thisActivity, R.layout.prospect_detail_header, null);
				LinearLayout.LayoutParams layparams_ = 
						new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getScaledPixel(35));
				HeaderInflate.setLayoutParams(layparams_);
				HeaderInflate.setTag("stage"+stage_iter);
				TextView lblStage 				= (TextView) HeaderInflate.findViewById(R.id.lblStage);    		
				ImageView imgSetDate 			= (ImageView) HeaderInflate.findViewById(R.id.imgSetDate);

				if (stage_iter == STAGE_ONE || stage_iter == STAGE_TWO)
					lblStage.setText(getString(R.string.stage2));
				else if (stage_iter == STAGE_THREE)
					lblStage.setText(getString(R.string.stage3));
				else if (stage_iter == STAGE_FOUR)
					lblStage.setText(getString(R.string.stage4));
				else if (stage_iter == STAGE_FIVE)
					lblStage.setText(getString(R.string.stage5));
				else if (stage_iter == STAGE_SIX)
					lblStage.setText(getString(R.string.stage6));
				else if(stage_iter == STAGE_EIGHT)
					lblStage.setText(getString(R.string.dead));
				else
					lblStage.setText("STAGE "+stage_iter);

//				if(dead){	
//					lblStage.setText(getString(R.string.dead));
//					if (stage_iter == STAGE){
//						imgSetDate.setVisibility(0);
////					lblStage.setText(getString(R.string.dead));
//					}else
//						imgSetDate.setVisibility(8);
//				}
				if (stage_iter == STAGE){
					imgSetDate.setVisibility(0);
				}

				//			if (stage_iter == STAGE_FIVE){
				//				/*imgSetDate.setBackgroundResource(R.drawable.comments_normal);
				//				imgSetDate.setOnTouchListener(new OnTouchEvent(R.drawable.comments_normal, R.drawable.comments_over));*/
				//				imgSetDate.setVisibility(8);
				//			}else{
					imgSetDate.setBackgroundResource(R.drawable.cal_normal);
					imgSetDate.setOnTouchListener(new OnTouchEvent(R.drawable.cal_normal, R.drawable.cal_over));
				//			}

				imgSetDate.setTag(stage_iter);
				imgSetDate.setOnClickListener(new OnClick());
				AddContent.addView(HeaderInflate);
				if (STAGE != 1){
					Log.v("", "STAGE != 1");
					AddContent.removeView((RelativeLayout) AddContent.findViewWithTag("stage1"));
				}

				String query = "select status, reminder_datetime from tbl_schedule where request_id = " 
						+ prospect.prospect_id + " and stage =" + stage_iter;
				Log.v("", "query detail: "+query);
				Cursor c = db.executeQuery(query);

				while (c != null && c.moveToNext()){						
					LinearLayout DescInflate = (LinearLayout) View.inflate(thisActivity, R.layout.prospect_stage_descr, null);
					TextView lblDesc 		= (TextView) DescInflate.findViewById(R.id.lblDesc);
					TextView lblDate 		= (TextView) DescInflate.findViewById(R.id.lblDate);
					Log.v("", "Text Displayed ===>"+c.getString(0));
					lblDesc.setText(getDesc(c.getString(0)));
					
					/*if (stage_iter == STAGE_FIVE){
					lblDesc.setText(c.getString(1) + ": " + c.getString(0));
					lblDate.setText("");
					}else{	    			
					lblDesc.setText(getDesc(c.getString(0)));*/

					Date date = null;
					try {
						Log.v("STAGE", "setDataAndAdapter status: " + c.getString(0));
						if (c.getString(0).equals("job_completed"))
							isJobCompleted = true;
						date = formatter.parse(c.getString(1));
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(date.getTime());
						// Log.v("STAGE", "format remind " + formatter1.format(date) + " at " + (cal.get(Calendar.HOUR) == 0 ? "12" : cal.get(Calendar.HOUR)) + ":" 
						// + Utilities.getMinute(cal.get(Calendar.MINUTE)) + "" + (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
						lblDate.setText(formatter1.format(date) + " at " + (cal.get(Calendar.HOUR) == 0 ? "12" : cal.get(Calendar.HOUR)) + ":" 
								+ Utilities.getMinute(cal.get(Calendar.MINUTE)) + "" + (cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
					} catch (ParseException e) {
						Util.insertCaughtException(e, thisActivity);
						e.printStackTrace();
					}
					//				}

					DescInflate.setVisibility(0);
					AddContent.addView(DescInflate);
				}
				c.close();
			}

			//		Log.v("STAGE", "STAGE_CATEGORY " + STAGE_CATEGORY);

			lblDeadProspect.setVisibility(dead ? 0 : 8);	
			if (isJobCompleted){
				lblDeadProspect.setText("\"Project Completed\"");
				lblDeadProspect.setVisibility(0);
				lblDeadProspect.setTextColor(getResources().getColor(R.color.green));
			}

			/*if(STAGE_CATEGORY == STAGE3_DECLINE || STAGE_CATEGORY == STAGE3_ESTIMATE_DATE
				|| STAGE_CATEGORY == STAGE4_DECLINE || STAGE_CATEGORY == STAGE4_PROJECT_START){
			if (listCampaignName != null)
				showCampaignList(STAGE_CATEGORY);
			}*/
			
			if (db != null) 
				db.close();
		} catch (Exception e) {
			Util.insertCaughtException(e, thisActivity);
			e.printStackTrace();
		}			      	
	}

	private void showWhenNotificationClicks(String status){
		//    	init();	
		if (status != null){
			Log.e("", "status showWhenNotificationClicks=>>"+status);
			if (status.equals(getString(R.string.status_followup))){
				STAGE_CATEGORY = STAGE2_FOLLOWUP_APT;    		
//				return;
			}else if (status.equals(getString(R.string.status_confirm_appointment))){
				STAGE_CATEGORY = STAGE3_ESTIMATE_DATE;
			}else if (status.equals(getString(R.string.status_project_finish))){
				STAGE_CATEGORY = STAGE5_FINISH_DATE;
			}else if(status.equals(getString(R.string.status_enter_estimate))){
				STAGE_CATEGORY = STAGE3_WHEN_ESTIMATE_COMPLETED;
			}
		}else{
			Log.e("", "status showWhenNotificationClicks=>>"+status);
			return;
		}
		showEstimateORJobCompletedAlert();
	}
	
	
	public Prospects getProspectData(int id){
		db.openDataBase();
		String q = "select * from tbl_prospects where prospect_id = "+id;
		Log.v("query","schedule query: "+q);
		Cursor c = db.executeQuery(q);
		Prospects prospect = new Prospects();
		if (c != null && c.moveToNext()){			
			prospect.prospect_id = ""+id;
			prospect.pumka_prospect_id = c.getString(c.getColumnIndex("pumka_prospect_id"));

			prospect.prospect_status = c.getString(c.getColumnIndex("prospect_status"));
			prospect.name = c.getString(c.getColumnIndex("name"));
			prospect.email = c.getString(c.getColumnIndex("email"));
			prospect.city = c.getString(c.getColumnIndex("city"));
			prospect.phone_number = c.getString(c.getColumnIndex("phone_number"));
			prospect.address = c.getString(c.getColumnIndex("address"));
			prospect.zipcode = c.getString(c.getColumnIndex("zipcode"));
			prospect.province = c.getString(c.getColumnIndex("province"));
			prospect.stage = c.getString(c.getColumnIndex("stage"));
			prospect.referer = c.getString(c.getColumnIndex("referer"));
			prospect.details = c.getString(c.getColumnIndex("details"));
			prospect.status_date = c.getString(c.getColumnIndex("status_date"));
			prospect.created_time = c.getString(c.getColumnIndex("created_time"));
			prospect.contact_id = c.getString(c.getColumnIndex("contact_id"));
			prospect.calendar_id = c.getString(c.getColumnIndex("calendar_id"));
			prospect.priority = c.getString(c.getColumnIndex("priority"));
			prospect.min_max_budget = c.getString(c.getColumnIndex("min_max_budget"));

			Log.v("Propsetec","prospect.prospect_id: "+prospect.prospect_id);
			Log.v("Propsetec","prospect.prospect_status: "+prospect.prospect_status);
			Log.v("Propsetec","prospect.name: "+prospect.name);
			Log.v("Propsetec","prospect.email: "+prospect.email);
			Log.v("Propsetec","prospect.city: "+prospect.city);
			Log.v("Propsetec","prospect.phone_number: "+prospect.phone_number);
			Log.v("Propsetec","prospect.address: "+prospect.address);
			Log.v("Propsetec","prospect.zipcode: "+prospect.zipcode);
			Log.v("Propsetec","prospect.province: "+prospect.province);
			Log.v("Propsetec","prospect.stage: "+prospect.stage);
			Log.v("Propsetec","prospect.referer: "+prospect.referer);
			Log.v("Propsetec","prospect.details: "+prospect.details);
			Log.v("Propsetec","prospect.status_date: "+prospect.status_date);
			Log.v("Propsetec","prospect.created_time: "+prospect.created_time);
			Log.v("Propsetec","prospect.contact_id: "+prospect.contact_id);
			Log.v("Propsetec","prospect.calendar_id: "+prospect.calendar_id);
			Log.v("Propsetec","prospect.priority: "+prospect.priority);
			Log.v("Propsetec","prospect.min_max_budget: "+prospect.min_max_budget);
		}else{
			Utilities.showAlert(thisActivity, getString(R.string.no_prospects));
			Utilities.showActivity(thisActivity, SplashActivity.class);
		}

		c.close();
		db.close();
		
		Log.e("","thomas cal id == >>"+prospect.calendar_id);

		return prospect;
	}   

	class OnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			Log.e("", "onclick==>>");

			final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
			if (v.getTag() != null){
				int stage_tag = (Integer) v.getTag();	
				Log.e("", "onclick==>>"+stage_tag);
				switch (stage_tag) {
				case STAGE_ONE:						
					db.openDataBase();		
					boolean dead = isProspectDead();
					
					if (db != null) 
						db.close();

					if(dead){											
						final CharSequence[] pre_scheduled_items = {"Schedule Appointment"};						
						AlertDialog.Builder remind_builder = new AlertDialog.Builder(thisActivity);
						remind_builder.setTitle("Set Action...");
						remind_builder.setItems(pre_scheduled_items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								Log.e("", "stage 1 onclik");
								DialogFragment newFragment = new DatePickerFragment();
								if (pre_scheduled_items[item].equals("Schedule Appointment")){
									if(!checkProspectAddress()){
										try {
											new AlertDialog.Builder(thisActivity)
													.setCancelable(false)
													.setPositiveButton("OK",
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,int whichButton) {
															dialog.cancel();
															Intent i = new Intent(thisActivity, AddProspect.class);
															Bundle b = new Bundle();
															b.putSerializable("prospect", prospect);
															i.putExtra("from", "prospect-details");
															i.putExtra("address_check", "address_check");
															i.putExtras(b);
															startActivityForResult(i, ResultHandler.ADD_PROSPECTS_RESULT);															
												    	}
													}).setMessage(getResources().getString(R.string.no_address_found)).create().show();
											return;										
									    } catch (Exception e) {
											e.printStackTrace();
										}	
									}else{
									STAGE_CATEGORY = STAGE2_SCHEDULE_APT;
									Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_SCHEDULE_APT selected - id : " +prospect.prospect_id);
									STAGE = 1;
									newFragment = new DatePickerFragment("Schedule Appointment");					
									newFragment.show(getSupportFragmentManager(), "datePicker" + item);
									}
								}
							}
						});
						AlertDialog alert = remind_builder.create();
						alert.show();							
					}else{			
					Log.v("","Dead and new");
					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "stage one selected");
					final CharSequence[] pre_scheduled_items;	
				    pre_scheduled_items =new CharSequence[]  {"Schedule Appointment", "Follow up"
							,"Left Message", "No Answer", "Cancel Appointment forever"};
					AlertDialog.Builder remind_builder = new AlertDialog.Builder(thisActivity);
					remind_builder.setTitle("Set Action...");
					remind_builder.setItems(pre_scheduled_items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, final int item) {
							Log.e("", "stage 1 onclik");//							
							newFragment = new DatePickerFragment();
							if (pre_scheduled_items[item].equals("Schedule Appointment")){
								if(!checkProspectAddress()){
									try {
										new AlertDialog.Builder(thisActivity)
												.setCancelable(false)
												.setPositiveButton("OK",
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,int whichButton) {
														dialog.cancel();
														Intent i = new Intent(thisActivity, AddProspect.class);
														Bundle b = new Bundle();
														b.putSerializable("prospect", prospect);
														i.putExtra("from", "prospect-details");
														i.putExtra("address_check", "address_check");
														i.putExtras(b);
														startActivityForResult(i, ResultHandler.ADD_PROSPECTS_RESULT);															
											    	}
												}).setMessage(getResources().getString(R.string.no_address_found)).create().show();
										return;										
								    } catch (Exception e) {
										e.printStackTrace();
									}	
								}
								STAGE_CATEGORY = STAGE2_SCHEDULE_APT;
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_SCHEDULE_APT selected - id : " +prospect.prospect_id);
								STAGE = 1;								
								newFragment = new DatePickerFragment("Schedule Appointment");
								db.openDataBase();
								String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
										+" and (status = 'schedule' or status = 'reschedule')";
								Log.v("query","schedule query: "+q);
								Cursor c = db.executeQuery(q);
								if (c != null && c.moveToNext())
									Toast.makeText(thisActivity, "You have Scheduled already, Please Reschedule Appointment!", Toast.LENGTH_LONG).show();
								else
									newFragment.show(getSupportFragmentManager(), "datePicker" + item);
								
								c.close();
								db.close();																				
							}else if (pre_scheduled_items[item].equals("Follow up")){
									STAGE_CATEGORY = STAGE2_FOLLOWUP_APT;
									Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_FOLLOWUP_APT selected - id : " +prospect.prospect_id);
									newFragment = new DatePickerFragment("Follow up to book the Estimate Appointment");
									newFragment.show(getSupportFragmentManager(), "datePicker" + item);
						   }else if (pre_scheduled_items[item].equals("Cancel Appointment forever")){
								STAGE_CATEGORY = STAGE2_CANCEL_APT_FOREVER;	
								new DeleteCalendarEvent().execute();
						   }else if (pre_scheduled_items[item].equals("Left Message")){
								Log.e("", "stage 1");
								AddNotes("Left Message",1);	
							}else if (pre_scheduled_items[item].equals("No Answer")){
								Log.e("", "stage 1");
								AddNotes("No Answer",1);	
							}
//							else if (pre_scheduled_items[item].equals("Decline the job & send thankyou card")){
//								 Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE3_DECLINE selected - id : " +prospect.prospect_id);
//									STAGE_CATEGORY = STAGE3_DECLINE;							
//									prospectDead("thankyou");
//									isProspectDead = true;
//									try {
//										//C.deleteCalendarEvent(C.getCalendarService(prefs), prospect);
//										CalendarEventsManagement.deleteCalendarEvent(thisActivity, prospect);
//									} catch (ServiceException e1) {
//										// TODO Auto-generated catch block
//										e1.printStackTrace();
//									} catch (IOException e1) {
//										// TODO Auto-generated catch block
//										e1.printStackTrace();
//									}
//									prospect.calendar_id = "";
//							/*		db.openDataBase();
//									try {
//										SQLiteDatabase getDatabase = db.getWritableDatabase();
//										SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
//										insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
//										insert_statement.bindLong(2, STAGE);
//										insert_statement.bindString(3, getProspectStatus());
//										Calendar cal = Calendar.getInstance();
//										insert_statement.bindString(4, formatter.format(formatter1.parse(getCurrentDateString(cal))));
//										insert_statement.bindString(5, getCurrentDateString(cal));
//										insert_statement.bindLong(6, STAGE_CATEGORY);
//										insert_statement.executeInsert();
//										insert_statement.close();
//									} catch (ParseException e) {
//										Util.insertCaughtException(e, thisActivity);
//										e.printStackTrace();
//									}	
//									db.close();	*/
//									AddContent.removeAllViews();
//									setDataAndAdapter();
//									getMailStages(Calendar.getInstance().getTimeInMillis());
//									if (listCampaignName != null && listCampaignName.size() > 0)
//										showCampaignList(STAGE_CATEGORY);		
//									
//							}
							
						}
					});
					AlertDialog alert = remind_builder.create();
					alert.show();
					}	
					break;
				case STAGE_TWO:
					
					db.openDataBase();		
					boolean secondstagedead = isProspectDead();
					
					if (db != null) 
						db.close();
					
					if(secondstagedead){						
						final CharSequence[] pre_scheduled_items = {"Schedule Appointment"};						
						AlertDialog.Builder remind_builder = new AlertDialog.Builder(thisActivity);
						remind_builder.setTitle("Set Action...");
						remind_builder.setItems(pre_scheduled_items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								Log.e("", "stage 1 onclik");
								DialogFragment newFragment = new DatePickerFragment();
								if (pre_scheduled_items[item].equals("Schedule Appointment")){
									if(!checkProspectAddress()){
										try {
											new AlertDialog.Builder(thisActivity)
													.setCancelable(false)
													.setPositiveButton("OK",
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,int whichButton) {
															dialog.cancel();
															Intent i = new Intent(thisActivity, AddProspect.class);
															Bundle b = new Bundle();
															b.putSerializable("prospect", prospect);
															i.putExtra("from", "prospect-details");
															i.putExtra("address_check", "");
															i.putExtras(b);
															startActivityForResult(i, ResultHandler.ADD_PROSPECTS_RESULT);															
												    	}
													}).setMessage(getResources().getString(R.string.no_address_found)).create().show();
											return;										
									    } catch (Exception e) {
											e.printStackTrace();
										}	
									}else{
									STAGE_CATEGORY = STAGE2_SCHEDULE_APT;
									Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_SCHEDULE_APT selected - id : " +prospect.prospect_id);
									STAGE = 1;
									newFragment = new DatePickerFragment("Schedule Appointment");					
									newFragment.show(getSupportFragmentManager(), "datePicker" + item);
									}
								}
								
							}
						});
						AlertDialog alert = remind_builder.create();
						alert.show();						
						
					}else{
				
					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE_TWO selected");
					final CharSequence[] post_scheduled_items = {"Reschedule Appointment", 
							"Confirm Appointment", "Cancel Appointment for now", 
							"Cancel Appointment forever","Left Message", "No Answer"};
					
					/*final CharSequence[] post_scheduled_items = {"Reschedule Appointment", 
							"Confirm Appointment", "Cancel Appointment for now", 
							"Left Message", "No Answer"};*/
					
					String strCheckFUQuery = "select prospect_status from tbl_prospects where prospect_id = '"+prospect.prospect_id+"'";
					db.openDataBase();
					Log.v("","strCheckFUQuery==>>"+strCheckFUQuery);
					Cursor recordset = db.executeQuery(strCheckFUQuery);
					String strFU = "";
					
					if (recordset != null && recordset.moveToNext()){						
						strFU = recordset.getString(0);
						Log.v("","strFU==>>"+strFU);
					}
					if(recordset != null){
						recordset.close();
						recordset = null;
					}
					if(db!= null)
						db.close();
					
					if(strFU != null && strFU.equalsIgnoreCase("followup")){
						showAfterFollowUp();
					}else{
						AlertDialog.Builder remind_builder1 = new AlertDialog.Builder(thisActivity);
						remind_builder1.setTitle("Set Action...");
						remind_builder1.setItems(post_scheduled_items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								Log.e("", "stage 2 onclik");
								DialogFragment newFragment = new DatePickerFragment();
								if (post_scheduled_items[item].equals("Reschedule Appointment")){
									STAGE_CATEGORY = STAGE2_RESCHEDULE_APT;
									
									Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_RESCHEDULE_APT selected - id : " +prospect.prospect_id);
									newFragment = new DatePickerFragment("Reschedule the estimate appointment");
									newFragment.show(getSupportFragmentManager(), "datePicker" + item);
								}else if (post_scheduled_items[item].equals("Confirm Appointment")){
									STAGE_CATEGORY = STAGE2_CONFIRM_APT;
									loading = ProgressDialog.show(thisActivity, null, "Please wait..");
									new Thread(new Runnable() {
										public void run() {
											Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_CONFIRM_APT selected - id : " +prospect.prospect_id);
											long interval = 0L;
											/*db.openDataBase();
											Cursor remind_c = db.executeQuery("select reminder_datetime from tbl_schedule where request_id = "+prospect.prospect_id 
													+" and (status = 'schedule' or status = 'reschedule' or status = 'followup' or status = 'cancel')");
											String reminder = "";
											
											if (remind_c != null && remind_c.moveToNext()){
												Log.v("date","remind_c ****************");
												reminder = remind_c.getString(0);
											}else{
												runOnUiThread(new Runnable() {												
													@Override
													public void run() {
														Toast.makeText(thisActivity, "Please schedule Appointment!", Toast.LENGTH_SHORT).show();
													}
												});
												return;
											}
											remind_c.close();
											db.close();*/

											//delete follow-up log
											db.openDataBase();
//											dbConnect = new DatabaseConnection(thisActivity);
											String query ="select * from tbl_schedule where request_id = "+ prospect.prospect_id;
										    Cursor recordSet = db.executeQuery(query);
										    Log.v("","Confirm Cursor Appointment"+recordSet);
										    try{										    
										    if (recordSet != null && recordSet.moveToNext()) {
												for (recordSet.moveToFirst(); !recordSet.isAfterLast(); recordSet
														.moveToNext()) {
												String Scheduled_Date = recordSet.getString(recordSet.getColumnIndex("reminder_datetime"));
													Log.v("","Confirm Appointment"+Scheduled_Date);
//											   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
													    try {
//															Date date = sdf.parse(Scheduled_Date);
															date = (Date) formatter.parse(Scheduled_Date);
															Log.v("","Confirm Appointment Date"+date);
														} catch (ParseException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
												}
											}else{
												Log.v("","Confirm Appointment is null");

											}
										   
										    }catch(SQLiteException e){
										    	e.printStackTrace();
										    	Log.v("","SQLite Exception");
										    }
										    if (recordSet != null) {
												recordSet.close();
												recordSet = null;
											}

//											if (db != null) {
//												db.close();
//												db = null;
//											}
										String q = "delete from tbl_schedule where request_id = " + prospect.prospect_id + " " +
													"and stage = "+STAGE + " and (status = 'followup' or status = 'cancel')";
											
											STAGE = 3;
											db.executeUpdate(q);
											
											try {     //Feb 15 2013 07:25 PM								
//												Date date = (Date) formatter.parse(reminder.toString());  
												Calendar cal = Calendar.getInstance();
												interval = cal.getTimeInMillis();
												Date date2 = cal.getTime(); 
												Log.i("date","date: "+date2);
												
												SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
											//	prospect.calendar_id = C.updateCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, date, prospect);
												int calID = getCalendarID();
                                                String tag = "update";
												prospect.calendar_id = CalendarEventsManagement.updateCalendarEvent(thisActivity, STAGE_CATEGORY, date, prospect, calID, tag);
	                                         	Log.v("Updated 2", "Job Completed Management");
												SQLiteDatabase getDatabase = db.getWritableDatabase();
												SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
												insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
												insert_statement.bindLong(2, STAGE);
												insert_statement.bindString(3, getProspectStatus());
//												Calendar cal = Calendar.getInstance();
												insert_statement.bindString(4, formatter.format(date2));							
												insert_statement.bindString(5, getCurrentDateString(cal));
												insert_statement.bindLong(6, STAGE_CATEGORY);
												insert_statement.executeInsert();
												insert_statement.close();

											} catch (Exception e){
												Util.insertCaughtException(e, thisActivity);
												e.printStackTrace();
												interval = System.currentTimeMillis();
											}													
											db.executeUpdate("update tbl_prospects set stage = " +STAGE+ ", prospect_status = 'active' where prospect_id = " +prospect.prospect_id);
											db.executeUpdate("update tbl_schedule set active = 'yes' where request_id = " +prospect.prospect_id);
											db.close();
											
//											final long interval_fin = interval +  (TimeConversion.ONE_MINUTE * 15 ); // live
//											final long interval_fin = interval +  (TimeConversion.ONE_MINUTE * 1 ); // demo

											final long interval_fin = interval;
											
											runOnUiThread(new Runnable() {
												public void run() {
													setAlarmManager(interval_fin);
													prospect.stage = ""+STAGE;							
													setDataAndAdapter();												
													if (loading != null && loading.isShowing()){
														Log.e("", "loading not null");
														loading.dismiss();
													}else{
														Log.e("", "loading  null");
													}
												}
											});										
										}
									}).start();								
								}else if (post_scheduled_items[item].equals("Cancel Appointment for now")){
									//STAGE_CATEGORY = STAGE2_CANCEL_APT_FORNOW;
									STAGE_CATEGORY = STAGE2_FOLLOWUP_APT;
									Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_CANCEL_APT_FORNOW selected - id : " +prospect.prospect_id);
									newFragment = new DatePickerFragment("Enter a follow up date");
									newFragment.show(getSupportFragmentManager(), "datePicker" + item);
								}else if (post_scheduled_items[item].equals("Cancel Appointment forever")){
									STAGE_CATEGORY = STAGE2_CANCEL_APT_FOREVER;	
									new DeleteCalendarEvent().execute();
								}else if (post_scheduled_items[item].equals("Left Message")){
									AddNotes("Left Message",2);	
								}else if (post_scheduled_items[item].equals("No Answer")){
									AddNotes("No Answer",2);	
								}
							}
						});
						AlertDialog alert1 = remind_builder1.create();
						alert1.show();
					}	
					
					}
					
					break;
				case STAGE_THREE:
					showStage3();
					break;
				case STAGE_FOUR:
					db.openDataBase();		
					boolean deaddecline = isProspectDead();
					
					if (db != null) 
						db.close();

					if(deaddecline){
						final CharSequence[] pre_scheduled_items = {"Schedule Appointment"};						
						AlertDialog.Builder remind_builder = new AlertDialog.Builder(thisActivity);
						remind_builder.setTitle("Set Action...");
						remind_builder.setItems(pre_scheduled_items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								Log.e("", "stage 1 onclik");
								DialogFragment newFragment = new DatePickerFragment();
								if (pre_scheduled_items[item].equals("Schedule Appointment")){
									STAGE_CATEGORY = STAGE2_SCHEDULE_APT;
									Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_SCHEDULE_APT selected - id : " +prospect.prospect_id);
									STAGE = 1;
									newFragment = new DatePickerFragment("Schedule Appointment");					
									newFragment.show(getSupportFragmentManager(), "datePicker" + item);
															
								}
								
							}
						});
						AlertDialog alert = remind_builder.create();
						alert.show();
					}else{
					final CharSequence[] project_items = {"New client: Enter Project Start Date", "Waiting for other estimates – Follow up", 
					"Declined Estimate"};

					AlertDialog.Builder project_builder = new AlertDialog.Builder(thisActivity);
					project_builder.setTitle("Set Action...");
					project_builder.setItems(project_items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							Log.e("", "stage 4 onclick");

							DialogFragment newFragment = new DatePickerFragment();		
							STAGE = 4;
							switch (item) {
							case 0:									
								STAGE_CATEGORY = STAGE4_PROJECT_START;
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE4_PROJECT_START selected - id : " +prospect.prospect_id);
								newFragment = new DatePickerFragment("Enter project start date");
								db.openDataBase();
								String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
										+" and status = 'project_start'";
								Log.v("query","project start query: "+q);
								Cursor c = db.executeQuery(q);
								Cursor c1 = null;
								if (c != null && c.moveToNext()){	
									c1 = db.executeQuery("select * from tbl_schedule where request_id = "+prospect.prospect_id 
											+" and status = 'waiting_estimate'");
									if (c1 != null && c1.moveToNext())
										newFragment.show(getSupportFragmentManager(), "datePickerprojectSTAGE4_PROJECT_START" + STAGE4_PROJECT_START);
									else
										Toast.makeText(thisActivity, "You have already set the project start date.", Toast.LENGTH_LONG).show();
								}else
									newFragment.show(getSupportFragmentManager(), "datePickerprojectSTAGE4_PROJECT_START" + STAGE4_PROJECT_START);
								if (c != null) c.close();
								if (c1 != null) c1.close();
								db.close();
								break;
							case 1:							
								STAGE_CATEGORY = STAGE4_WAITING_OTHER_ESTIMATES;
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE4_WAITING_OTHER_ESTIMATES selected - id : " +prospect.prospect_id);
								newFragment = new DatePickerFragment("Schedule Follow Up Date");
								newFragment.show(getSupportFragmentManager(), "STAGE4_WAITING_OTHER_ESTIMATES" + STAGE4_WAITING_OTHER_ESTIMATES);        	
								break;

								/*case 2:							
								STAGE_CATEGORY = STAGE4_FINISH_DATE;
								newFragment = new DatePickerFragment("Enter project finish date");
								newFragment.show(getSupportFragmentManager(), "datePickerprojectSTAGE4_FINISH_DATE" + STAGE4_FINISH_DATE);
								break;	*/						
							case 2:		
								
								db.openDataBase();		
								boolean dead = isProspectDead();
								
								if (db != null) 
									db.close();
								if(dead){											
									final CharSequence[] pre_scheduled_items = {"Schedule Appointment"};						
									AlertDialog.Builder remind_builder = new AlertDialog.Builder(thisActivity);
									remind_builder.setTitle("Set Action...");
									remind_builder.setItems(pre_scheduled_items, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int item) {
											Log.e("", "stage 1 onclik");
											DialogFragment newFragment = new DatePickerFragment();
											if (pre_scheduled_items[item].equals("Schedule Appointment")){
												if(!checkProspectAddress()){
													try {
														new AlertDialog.Builder(thisActivity)
																.setCancelable(false)
																.setPositiveButton("OK",
																new DialogInterface.OnClickListener() {
																	public void onClick(DialogInterface dialog,int whichButton) {
																		dialog.cancel();
																		Intent i = new Intent(thisActivity, AddProspect.class);
																		Bundle b = new Bundle();
																		b.putSerializable("prospect", prospect);
																		i.putExtra("from", "prospect-details");
																		i.putExtra("address_check", "");
																		i.putExtras(b);
																		startActivityForResult(i, ResultHandler.ADD_PROSPECTS_RESULT);															
															    	}
																}).setMessage(getResources().getString(R.string.no_address_found)).create().show();
														return;										
												    } catch (Exception e) {
														e.printStackTrace();
													}	
												}
											}
										}
									});
								}else{
//								{
//									STAGE_CATEGORY = STAGE4_DECLINE;
//									Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE4_DECLINE selected - id : " +prospect.prospect_id);
//									prospectDead("thankyou");
//									AddContent.removeAllViews();
//									getMailStages(Calendar.getInstance().getTimeInMillis());
//									setDataAndAdapter();
//									
//									String query = "update tbl_schedule set status ='schedule' where status = 'estimate' and request_id="+prospect.prospect_id;
//									Log.e("The Declined", "Declined estimate"+query);
//									db.openDataBase();
//									db.executeUpdate(query);									
//									db.close();
//									
//									if (listCampaignName != null && listCampaignName.size() > 0)
//										showCampaignList(STAGE_CATEGORY);
//								}
									 Log.v("", "If success");
								 Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE3_DECLINE selected - id : " +prospect.prospect_id);
									STAGE_CATEGORY = STAGE3_DECLINE;							
									prospectDead("thankyou");
									isProspectDead = true;
									try { 	
					
										//C.deleteCalendarEvent(C.getCalendarService(prefs), prospect);
										CalendarEventsManagement.deleteCalendarEvent(thisActivity, prospect);
									} catch (ServiceException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									prospect.calendar_id = "";
									db.openDataBase();
									try {
										SQLiteDatabase getDatabase = db.getWritableDatabase();
										SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
										insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
										insert_statement.bindLong(2, STAGE);
										insert_statement.bindString(3, getProspectStatus());
										Calendar cal = Calendar.getInstance();
										insert_statement.bindString(4, formatter.format(formatter1.parse(getCurrentDateString(cal))));
										insert_statement.bindString(5, getCurrentDateString(cal));
										insert_statement.bindLong(6, STAGE_CATEGORY);
										insert_statement.executeInsert();
										insert_statement.close();
									} catch (ParseException e) {
										Util.insertCaughtException(e, thisActivity);
										e.printStackTrace();
									}	
									db.close();	
									AddContent.removeAllViews();
									setDataAndAdapter();
									getMailStages(Calendar.getInstance().getTimeInMillis());
									if (listCampaignName != null && listCampaignName.size() > 0)
										showCampaignList(STAGE_CATEGORY);		
								}		
								
								break;							
							default:
								break;
							}
						}
					});
					AlertDialog project_alert = project_builder.create();
					project_alert.show();
					}
					break;
				case STAGE_FIVE:
					/*Intent intent = new Intent(thisActivity, EnterComments.class);
					intent.putExtra("prospect_id", prospect.prospect_id);
					startActivityForResult(intent, ResultHandler.COMMENT_RESULT);*/

					final CharSequence[] five_items = {"Waiting for other estimates", "Reschedule project Start Date",
					"Reschedule project finish Date", "Job Completed"};

					AlertDialog.Builder five_builder = new AlertDialog.Builder(thisActivity);
					five_builder.setTitle("Set Action...");
					five_builder.setItems(five_items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							Log.e("", "stage 4 onclick");
							DialogFragment newFragment = new DatePickerFragment();		
							STAGE = 5;
							switch (item) {
							case 0:						
								STAGE_CATEGORY = STAGE4_WAITING_OTHER_ESTIMATES;
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE4_WAITING_OTHER_ESTIMATES in STAGE 5 selected - id : " +prospect.prospect_id);
								newFragment = new DatePickerFragment("Schedule Follow Up Date");
								newFragment.show(getSupportFragmentManager(), "STAGE4_WAITING_OTHER_ESTIMATES" + STAGE4_WAITING_OTHER_ESTIMATES); 
								break;
								
							case 1:																
							    STAGE_CATEGORY = STAGE4_PROJECT_START;
						        db.openDataBase();
						       	String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id + "and stage = "+STAGE
									+" and status = 'project_start'";
						    	Log.e("** RENO **", "query==>>"+q);
					          	db.executeQuery(q);
					          	db.close();
//					        	SharedPreferences prefss = PreferenceManager.getDefaultSharedPreferences(thisActivity);
//								try {
//									//C.deleteCalendarEvent(C.getCalendarService(prefs), prospect);									
////									CalendarEventsManagement.deleteCalendarEvent(thisActivity, prospect);
//								} catch (Exception e) {
//									Util.insertCaughtException(e, thisActivity);
//									e.printStackTrace();
//								}	
								String strUpdateQuery = "update tbl_prospects set calendar_id = '' where prospect_id = '"+prospect.prospect_id+"'";
								Log.e("", "strUpdateQuery==>>"+strUpdateQuery);
								db.openDataBase();
								db.executeUpdate(strUpdateQuery);
								if(db != null){
									db.close();				
								}								
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE5_START_DATE selected - id : " +prospect.prospect_id);
								newFragment = new DatePickerFragment("Enter project start date");
								newFragment.show(getSupportFragmentManager(), "datePickerprojectSTAGE4_PROJECT_START" + STAGE4_PROJECT_START);
                                break;
							case 2:							
								STAGE_CATEGORY = STAGE5_FINISH_DATE;
								db.openDataBase();
							String query = "delete from tbl_schedule where request_id = " + prospect.prospect_id + " " +
							"and stage = "+STAGE + " and status = 'project_finish'";
//							Log.e("** RENO **", "query==>>"+query);
//								String finsh = "select * from tbl_schedule where request_id = "+prospect.prospect_id + "and stage = "+STAGE
//										+" and status = 'project_finish'";
							    	Log.e("** RENO **", "query==>>"+query);
								db.executeUpdate(query);
								db.close();
								
//								SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
//								try {
//									//C.deleteCalendarEvent(C.getCalendarService(prefs), prospect);									
//									CalendarEventsManagement.deleteCalendarEvent(thisActivity, prospect);
//								} catch (Exception e) {
//									Util.insertCaughtException(e, thisActivity);
//									e.printStackTrace();
//								}				
//								
								String strUpdateQueryy = "update tbl_prospects set calendar_id = '' where prospect_id = '"+prospect.prospect_id+"'";
								Log.e("", "strUpdateQuery==>>"+strUpdateQueryy);
								db.openDataBase();
								db.executeUpdate(strUpdateQueryy);								
								if(db != null){
									db.close();				
								}								
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE5_FINISH_DATE selected - id : " +prospect.prospect_id);
								newFragment = new DatePickerFragment("Enter project finish date");
								newFragment.show(getSupportFragmentManager(), "datePickerprojectSTAGE5_FINISH_DATE" + STAGE5_FINISH_DATE);																	
								break;								
							case 3:
								
								db.openDataBase();
								String projectfinishquery = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
										+" and status = 'project_finish'" ;
								Log.v("query","project finish  query: "+projectfinishquery);
								Cursor c = db.executeQuery(projectfinishquery);
								Log.e("", "c is  ===>"+c.getCount());
								if (c != null && c.moveToNext()){
										
								
 								STAGE_CATEGORY = STAGE5_FINISH_DATE;
//							    String message = getString(R.string.notify_project_finished)+ " "+prospect.name;
//								callAlert(message);
								
								
//								if(loading == null && !loading.isShowing()){
//									loading = ProgressDialog.show(thisActivity, "", "Please wait..");
//								}
								checkStage3EC = false;

								new Thread(){
									@Override
									public void run() {
										//Toast.makeText(thisActivity, "You already set the date for Estimation!", Toast.LENGTH_SHORT).show();
										// create an event to call the prospect 48 hours after the time set in estimate due date.	
										if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
											STAGE_CATEGORY = STAGE6_CUSTOMERCARE_RESCHEDULE;
										}

										SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");	
										SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										Calendar current_cal = Calendar.getInstance();

//										final Calendar cal = db.getReminderDate(STAGE, prospect.prospect_id);
										final Calendar cal = Calendar.getInstance();
										Date date = new Date(cal.getTimeInMillis());

										SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);				
										Log.v("", "calendar_id: "+prospect.calendar_id);

										Log.e("------------time intervel value-----", "time interval==>");
										Log.e("------------STAGE2_SCHEDULE_APT-----", "STAGE2_SCHEDULE_APT==>"+STAGE_CATEGORY);

										long time_interval = Utilities.getTimeInterval(date.getTime(), STAGE_CATEGORY, thisActivity);
										if (STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE)
											time_interval = Utilities.getModifiedInterval(time_interval, thisActivity);
										
										Date datefromlong = new Date(time_interval);
										SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
										String dateText = df2.format(datefromlong);

										Log.e("dateText", "dateText--->"+dateText);

										Date upadtedDate = null;

										SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
										try {
											upadtedDate = format.parse(dateText);  
											System.out.println(date);  
										} catch (ParseException e) {  
											e.printStackTrace();  
										}

										
										try {
											//C.updateCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, upadtedDate, prospect);
			                                String tag = "update";
											CalendarEventsManagement.updateCalendarEvent(thisActivity, STAGE_CATEGORY, upadtedDate, prospect, getCalendarID(), tag);
										} catch (MalformedURLException e) {
											Util.insertCaughtException(e, thisActivity);
											e.printStackTrace();
										} catch (ServiceException e) {
											Util.insertCaughtException(e, thisActivity);
											e.printStackTrace();
										} catch (IOException e) {
											Util.insertCaughtException(e, thisActivity);
											e.printStackTrace();
										}	
										db.openDataBase();
										try {
											SQLiteDatabase getDatabase = db.getWritableDatabase();
											SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
											insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
											insert_statement.bindLong(2, STAGE);
											if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED)
												insert_statement.bindString(3, getProspectStatus());
											else
												insert_statement.bindString(3, "job_completed");
											insert_statement.bindString(4, formatter.format(formatter1.parse(getCurrentDateString(current_cal))));
											insert_statement.bindString(5, getCurrentDateString(current_cal));
											insert_statement.bindLong(6, STAGE_CATEGORY);
											insert_statement.executeInsert();
											insert_statement.close();
											//								db.close();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											Util.insertCaughtException(e, thisActivity);
											e.printStackTrace();
										}
										
										if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED){
											STAGE = 4;
											STAGE3_ESTIMATE_NOT_COMPLETED = false;
										}else if (STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE){
//											STAGE = 5;	
											Log.e("", "***call alert sara***");
//											runOnUiThread(new Runnable() {
//												public void run() {
//////													NotificationManager nm = (NotificationManager) thisActivity.getSystemService(Context.NOTIFICATION_SERVICE);
//////													nm.cancel(Integer.parseInt(prospect.prospect_id));
////													showCampaignList(STAGE_CATEGORY);
													STAGE = 6;
//												}
//											});
//											STAGE = 6;
										}
										
										if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE){
											Log.e("", "** call alert if***");
											db.executeUpdate("update tbl_prospects set prospect_status = 'cc' , stage = '6' where prospect_id = " + prospect.prospect_id);						
										}else{
											Log.e("", "** call alert else***");
											db.executeUpdate("update tbl_prospects set stage = " + STAGE + " where prospect_id = " + prospect.prospect_id);						
										}
										db.close();

										runOnUiThread(new Runnable() {
											public void run() {																	
												if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
													STAGE_CATEGORY = STAGE6_CUSTOMERCARE_RESCHEDULE;
												}
												getMailStages(Calendar.getInstance().getTimeInMillis());
												setAlarmManager(cal.getTimeInMillis());
												setDataAndAdapter();
												
												if (loading != null && loading.isShowing())
													loading.dismiss();	
												
											}
										});		
										
//										if(STAGE_CATEGORY == STAGE5_FINISH_DATE || STAGE_CATEGORY == STAGE6_CUSTOMERCARE_FOLLOWUP ){
//											Log.e("", "** call alert if***");
			//
//											db.executeUpdate("update tbl_prospects set prospect_status = 'cc' , stage = '6' where prospect_id = " + prospect.prospect_id);						
//										}else{
//											Log.e("", "** call alert else***");
			//
//											db.executeUpdate("update tbl_prospects set stage = " + STAGE + " where prospect_id = " + prospect.prospect_id);						
//										}
//										db.close();
			//
//										runOnUiThread(new Runnable() {
//											public void run() {																	
//												if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
//													STAGE_CATEGORY = STAGE6_CUSTOMERCARE_FOLLOWUP;
//												}
//												getMailStages(Calendar.getInstance().getTimeInMillis());
//												setAlarmManager(cal.getTimeInMillis());
//												setDataAndAdapter();
//																					
//												if (loading != null && loading.isShowing())
//													loading.dismiss();							
//											}
//										});												

										super.run();
									}
								}.start();
							
								
//								STAGE_CATEGORY = STAGE6_CUSTOMERCARE_RESCHEDULE;
//								
//								SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");	
//								SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//								Calendar current_cal = Calendar.getInstance();
//
////								final Calendar cal = db.getReminderDate(STAGE, prospect.prospect_id);
//								final Calendar cal = Calendar.getInstance();
//								Date date = new Date(cal.getTimeInMillis());
//
//								SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(thisActivity);				
//								Log.v("", "calendar_id: "+prospect.calendar_id);
//
//								Log.e("------------time intervel value-----", "time interval==>");
//								Log.e("------------STAGE2_SCHEDULE_APT-----", "STAGE2_SCHEDULE_APT==>"+STAGE_CATEGORY);
//
//								long time_interval = Utilities.getTimeInterval(date.getTime(), STAGE_CATEGORY, thisActivity);
//								if (STAGE_CATEGORY == STAGE5_FINISH_DATE)
//									time_interval = Utilities.getModifiedInterval(time_interval, thisActivity);
//								
//								Date datefromlong = new Date(time_interval);
//								SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//								String dateText = df2.format(datefromlong);
//
//								Log.e("dateText", "dateText--->"+dateText);
//
//								
//								
//								Date upadtedDate = null;
//
//								SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
//								try {
//									upadtedDate = format.parse(dateText);  
//									System.out.println(date);  
//								} catch (ParseException e) {  
//									e.printStackTrace();  
//								}
//								
//								try {
//								//	C.updateCalendarEvent(C.getCalendarService(prefs1), STAGE_CATEGORY, upadtedDate, prospect);
//									CalendarEventsManagement.updateCalendarEvent(thisActivity, STAGE_CATEGORY, upadtedDate, prospect, getCalendarID());
//								} catch (MalformedURLException e) {
//									Util.insertCaughtException(e, thisActivity);
//									e.printStackTrace();
//								} catch (ServiceException e) {
//									Util.insertCaughtException(e, thisActivity);
//									e.printStackTrace();
//								} catch (IOException e) {
//									Util.insertCaughtException(e, thisActivity);
//									e.printStackTrace();
//								}	
//								db.openDataBase();
//								try {
//									SQLiteDatabase getDatabase = db.getWritableDatabase();
//									SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
//									insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
//									insert_statement.bindLong(2, STAGE);
//									if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED)
//										insert_statement.bindString(3, getProspectStatus());
//									else
//										insert_statement.bindString(3, "job_completed");
//									insert_statement.bindString(4, formatter.format(formatter1.parse(getCurrentDateString(current_cal))));
//									insert_statement.bindString(5, getCurrentDateString(current_cal));
//									insert_statement.bindLong(6, STAGE_CATEGORY);
//									insert_statement.executeInsert();
//									insert_statement.close();
//									//								db.close();
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									Util.insertCaughtException(e, thisActivity);
//									e.printStackTrace();
//								}
//								
//								if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED){
//									STAGE = 4;
//									STAGE3_ESTIMATE_NOT_COMPLETED = false;
//								}else if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
//									STAGE = 5;	
//									
//									runOnUiThread(new Runnable() {
//										public void run() {
////											NotificationManager nm = (NotificationManager) thisActivity.getSystemService(Context.NOTIFICATION_SERVICE);
////											nm.cancel(Integer.parseInt(prospect.prospect_id));
//											showCampaignList(STAGE_CATEGORY);
//										}
//									});
//									Log.e("", "*** set stage 6 ***");
//									STAGE = 6;
//								}else if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE){
//									STAGE = 6;
//								}
//								
//								if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE){
//									Log.e("", "** call alert if***");
//									db.executeUpdate("update tbl_prospects set prospect_status = 'cc' , stage = '6' where prospect_id = " + prospect.prospect_id);						
//								}else{
//									Log.e("", "** call alert else***");
//									db.executeUpdate("update tbl_prospects set stage = " + STAGE + " where prospect_id = " + prospect.prospect_id);						
//								}
//								db.close();
//
//								runOnUiThread(new Runnable() {
//									public void run() {																	
//										if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
//											STAGE_CATEGORY = STAGE6_CUSTOMERCARE_FOLLOWUP;
//										}
//										getMailStages(Calendar.getInstance().getTimeInMillis());
//										setAlarmManager(cal.getTimeInMillis());
//										setDataAndAdapter();
//										
//										if (loading != null && loading.isShowing())
//											loading.dismiss();	
//										
//									}
//								});		
								}else{
									Utilities.showAlert(thisActivity, "Please enter Project Finish Date");
								}
								break;
							default:
								break;
							}
						}
					});
					AlertDialog five_alert = five_builder.create();
					five_alert.show();
					break;			
				case STAGE_SIX:					
					final CharSequence[] six_items = {"Schedule Customer Care Appointment", "Reschedule Customer Care Appointment", 
					"Left Message", "No Answer"};
					AlertDialog.Builder six_builder = new AlertDialog.Builder(thisActivity);
					six_builder.setTitle("Set Action...");
					six_builder.setItems(six_items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							Log.e("", "stage 6 onclick");
							DialogFragment newFragment = new DatePickerFragment();		
							STAGE = 6;
							switch (item) {
							case 0:						
								STAGE_CATEGORY = STAGE6_CUSTOMERCARE_SCHEDULE;
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE6_SETCUSTOMERCARE in STAGE 6 selected - id : " +prospect.prospect_id);
								newFragment = new DatePickerFragment("Set customer care appointment");
								newFragment.show(getSupportFragmentManager(), "Set customer care appointment" + STAGE6_CUSTOMERCARE_SCHEDULE);

//								STAGE_CATEGORY = STAGE6_CUSTOMERCARE_SCHEDULE;
//										Util.pushActivityInfo(thisActivity,
//												Thread.currentThread(),
//												"STAGE6_SETCUSTOMERCARE in STAGE 6 selected - id : "
//														+ prospect.prospect_id);
//
//										STAGE = 6;
//										newFragment = new DatePickerFragment("Set customer care appointment");
//										db.openDataBase();
//										String q = "select * from tbl_schedule where request_id = "
//												+ prospect.prospect_id
//												+ " and (status = 'customer care appointment schedule' or status = 'customer care appointment reschedule')";
//										Log.v("query", "schedule query: " + q);
//										Cursor c = db.executeQuery(q);
//										if (c != null && c.moveToNext())
//											Toast.makeText(
//													thisActivity,
//													"You have Scheduled already, Please Reschedule Appointment!",
//													Toast.LENGTH_SHORT).show();
//										else
//											newFragment.show(getSupportFragmentManager(),"Set customer care appointment"+ STAGE6_CUSTOMERCARE_SCHEDULE);
//										c.close();
//										db.close();
										break;
							case 1:							
								STAGE_CATEGORY = STAGE6_CUSTOMERCARE_SCHEDULE;
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE6_SETCUSTOMERCARE in STAGE 6 selected - id : " +prospect.prospect_id);
								newFragment = new DatePickerFragment("Reschedule customer care appointment");
								newFragment.show(getSupportFragmentManager(), "Reschedule customer care appointment" + STAGE6_CUSTOMERCARE_SCHEDULE); 
								break;
							case 2:
								AddNotes("Left Message",6);
								break;
							case 3:
								AddNotes("No Answer",6);
								break;
							default:
								break;
							}
						}
					});
					AlertDialog six_alert = six_builder.create();
					six_alert.show();
					break;
				case STAGE_EIGHT:					
					final CharSequence[] pre_scheduled_itemss = {"Schedule Appointment"};						
					AlertDialog.Builder remind_builderr = new AlertDialog.Builder(thisActivity);
					remind_builderr.setTitle("Set Action...");
					remind_builderr.setItems(pre_scheduled_itemss, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							Log.e("", "stage 1 onclik");
							DialogFragment newFragment = new DatePickerFragment();
							if (pre_scheduled_itemss[item].equals("Schedule Appointment")){
								STAGE_CATEGORY = STAGE2_SCHEDULE_APT;
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_SCHEDULE_APT selected - id : " +prospect.prospect_id);
								STAGE = 1;
								newFragment = new DatePickerFragment("Schedule Appointment");					
								newFragment.show(getSupportFragmentManager(), "datePicker" + item);														
							}							
						}
					});
					AlertDialog alertt = remind_builderr.create();
					alertt.show();
				}
				}else if (v.getId() == R.id.imgNotes){
					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "Notes Button selected - id : " +prospect.prospect_id);
					Intent intent = new Intent(thisActivity, Notes.class);
					intent.putExtra("prospect_id", prospect.prospect_id);
					startActivity(intent);
				}	 
		}
	}
	
	
	class DeleteCalendarEvent extends AsyncTask<String, Integer, Void>{
		
		@Override
		protected void onPreExecute() {
			loading = ProgressDialog.show(thisActivity, null, "Deleting event...");
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_CANCEL_APT_FOREVER selected - id : " +prospect.prospect_id);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
			try {
				//C.deleteCalendarEvent(C.getCalendarService(prefs), prospect);
				CalendarEventsManagement.deleteCalendarEvent(thisActivity, prospect);
			} catch (Exception e) {
				Util.insertCaughtException(e, thisActivity);
				e.printStackTrace();
			}				
			Log.e("", "Thomas delete calendar==>>");
			String strUpdateQuery = "update tbl_prospects set calendar_id = '' where prospect_id = '"+prospect.prospect_id+"'";
			Log.e("", "strUpdateQuery==>>"+strUpdateQuery);
			db.openDataBase();
			db.executeUpdate(strUpdateQuery);
			if(db!= null){
				db.close();				
			}
			
			

			runOnUiThread(new Runnable() {
				public void run() {
					prospectDead("dead");
					if (loading != null && loading.isShowing())
						loading.dismiss();
					getMailStages(Calendar.getInstance().getTimeInMillis());
					AddContent.removeAllViews();
//					setDataAndAdapter();
				}
			});			
			return null;
		}
		
	}
	
	/** Add Notes For Prospect **/
	
	private void AddNotes(String strAlert,int STAGE){		
		Log.e("", "AddNotes==>>"+STAGE);	
		Log.e("", "AddNotes==>>"+strAlert);	
		Util.pushActivityInfo(ProspectDetails.this, Thread.currentThread(), "Add notes Clicked.");
		String query  = "INSERT INTO tbl_notes";
		query += "(prospect_id";
		query += ",notes";
		query += ",date_time";
		query += ")values(?,?,?)";		
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");		
		try {
			db.openDataBase();
			SQLiteDatabase getDatabase = db.getWritableDatabase();
			SQLiteStatement insert_statement = getDatabase.compileStatement(query);
			Log.v("", "AddNotes pros id: "+ prospect.prospect_id);
			insert_statement.bindString(1, prospect.prospect_id);	
			
			if(STAGE == 1){
				if(strAlert.equals("Left Message"))
					insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.left_message));
				else
					insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.no_answer));
			}else if(STAGE == 2){
				if(strAlert.equals("Left Message")){
					Log.v("","Left Message clicked"+strAlert);
					insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.stage2_left_message));
				}else{
					Log.v("","No Answer clicked"+strAlert);
					insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.stage2_no_answer));
				}
			}else if(STAGE == 6){
				if(strAlert.equals("Left Message"))
					insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.stage6_left_message));
				else
					insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.stage6_no_answer));
			}else if(STAGE == 107){
				insert_statement.bindString(2,strAlert);
			}else{
				insert_statement.bindString(2, "Showed up to Estimate Appointment, but prospect didn't show");
			}
			
			Calendar current_cal = Calendar.getInstance();
			try {
				insert_statement.bindString(3, formatter.format(formatter1.parse(getCurrentDateString(current_cal))));
			} catch (ParseException e) {						
				e.printStackTrace();
				Util.insertCaughtException(e, ProspectDetails.this);
			}
			
//			if(STAGE!=2){
				insert_statement.executeInsert();
				insert_statement.close();
//			}
			
			db.close();			
		} catch (Exception e) {
			Util.insertCaughtException(e, ProspectDetails.this);
			e.printStackTrace();
		}
		
		/** Calendar Event Created Time **/		
		if(STAGE == 1){
			Log.e("", "Left message or No Answer ===>: "+date);		
			STAGE_CATEGORY = STAGE1_LEFTMSG_NOANSWER;
			Calendar c = Calendar.getInstance();		
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			long currentmilliseconds = c.getTimeInMillis();			
			long add48Hours = currentmilliseconds  + (TimeConversion.ONE_HOUR * 48 ); // live 
//			long add48Hours = currentmilliseconds  + (TimeConversion.ONE_MINUTE * 1 ); // demo 			
			c.setTimeInMillis(add48Hours);
			String date = dateformat.format(c.getTime());
			Log.e("", "STAGE1_LEFTMSG_NOANSWER DATE: "+date);					
			try {  
			    Date resheduledate = dateformat.parse(date); 
			    loading = ProgressDialog.show(thisActivity, null, "Loading...");
//				new SetDateTime(resheduledate).execute();
				new UpdateLeftMessageEvent(resheduledate).start();
			} catch (ParseException e) {  
			    e.printStackTrace();  
			}			
		}else if(STAGE == 2){
			Log.e("", "Left message or No Answer ===>: "+date);		
			STAGE_CATEGORY = STAGE2_LEFTMSG_NOANSWER;
			checkStage2NoMSG = true;
			STAGE_CATEGORY = STAGE2_FOLLOWUP_APT;
			Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_FOLLOWUP_APT selected - id : " +prospect.prospect_id);
//			DatePickerFragment newFragment = new DatePickerFragment("Follow up to book the Estimate Appointment",mHandler,strAlert,STAGE);
//			newFragment.show(getSupportFragmentManager(), "datePicker" + 2);
			
		}else if(STAGE == 6){
		    Calendar c = Calendar.getInstance();		
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			long currentmilliseconds = c.getTimeInMillis();			
			long addOneWeek = currentmilliseconds  + (TimeConversion.ONE_DAY * 7 ); // live 
//			long addOneWeek = currentmilliseconds  + (TimeConversion.ONE_MINUTE * 1 ); // demo 		

			c.setTimeInMillis(addOneWeek);
			String date = dateformat.format(c.getTime());
			Log.e("", "STAGE6_CUSTOMERCARE_RESCHEDULE: Date "+date);
			try {  
			    Date resheduledate = dateformat.parse(date); 
			    loading = ProgressDialog.show(thisActivity, null, "Loading...");
//				new SetDateTime(resheduledate).execute();
				new UpdateLeftMessageEvent(resheduledate).start();
			} catch (ParseException e) {  
			    e.printStackTrace();  
			}
		}		
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			if(b!=null){
				String strUpdate = b.getString("update");				
				
				if(!strUpdate.equals("") && strUpdate.equals("yes")){
					Log.e("", "Str Update ===>"+strUpdate);
					String strAlert = b.getString("alert");
					int stage = b.getInt("stage");
					String query  = "INSERT INTO tbl_notes";
					query += "(prospect_id";
					query += ",notes";
					query += ",date_time";
					query += ")values(?,?,?)";		
					SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");		
					try {
						db.openDataBase();
						SQLiteDatabase getDatabase = db.getWritableDatabase();
						SQLiteStatement insert_statement = getDatabase.compileStatement(query);
						Log.v("", "AddNotes pros id: "+ prospect.prospect_id);
						insert_statement.bindString(1, prospect.prospect_id);			
						if(STAGE == 1){
							if(strAlert.equals("Left Message"))
								insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.left_message));
							else
								insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.no_answer));
						}else if(STAGE == 2){
							if(strAlert.equals("Left Message"))
								insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.stage2_left_message));
							else
								insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.stage2_no_answer));
						}else if(STAGE == 6){
							if(strAlert.equals("Left Message"))
								insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.stage6_left_message));
							else
								insert_statement.bindString(2, ProspectDetails.this.getResources().getString(R.string.stage6_no_answer));
						}
						Calendar current_cal = Calendar.getInstance();
						try {
							insert_statement.bindString(3, formatter.format(formatter1.parse(getCurrentDateString(current_cal))));
						} catch (ParseException e) {						
							e.printStackTrace();
							Util.insertCaughtException(e, ProspectDetails.this);
						}
						insert_statement.executeInsert();
						insert_statement.close();
						
						db.close();
						
					} catch (Exception e) {
						Util.insertCaughtException(e, ProspectDetails.this);
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	class UpdateLeftMessageEvent extends Thread{
		Date date ;
		UpdateLeftMessageEvent(Date date){
			this.date = date;
		}		
		public void run(){	
			Log.e("", "STAGE1_LEFTMSG_NOANSWER STAGE_CATEGORY: "+STAGE_CATEGORY);					

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
			if (prospect.calendar_id != null && ! prospect.calendar_id.equals("")){
				try {
					//prospect.calendar_id = C.updateCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, date, prospect);
                    String tag = "update";
					prospect.calendar_id = CalendarEventsManagement.updateCalendarEvent(thisActivity, STAGE_CATEGORY, date, prospect, getCalendarID(), tag);
				} catch (ServiceException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				createCalendarEventFirstTime(prefs, STAGE_CATEGORY, date);
			}			
			long interval = 0L;				
			interval = date.getTime();
			
			Log.e("", "UpdateLeftMessageEvent==>>"+interval);
			
			AlarmManager am = (AlarmManager) thisActivity.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(thisActivity, RenoKingNotifications.class);
			intent.putExtra("id", Integer.parseInt(prospect.prospect_id ));
			intent.putExtra("title", "Schedule Appointment!");
			intent.putExtra("name", prospect.name);
			intent.putExtra("number", prospect.phone_number);
			intent.putExtra("stage", 1);	
			intent.putExtra("message",prospect.name +" - "+ prospect.phone_number);					
			intent.putExtra("status", thisActivity.getString(R.string.status_schedule));
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(thisActivity, 
			Integer.parseInt(prospect.prospect_id ),intent, PendingIntent.FLAG_CANCEL_CURRENT);							
			am.set(AlarmManager.RTC_WAKEUP,interval, pendingIntent);
			
			if(loading != null && loading.isShowing())
				loading.cancel();
		}
	}
	

	public void showStage3(){
		//		Log.e("", "enterEstimateDueDate");
		//		DialogFragment newFragment = new DatePickerFragment("Enter Estimate Due date:");
		//		STAGE_CATEGORY = STAGE3_ESTIMATE_DATE;
		//		db.openDataBase();
		//		String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
		//				+" and status = 'estimate'";
		//		Log.v("query","schedule query: "+q);
		//		Cursor c = db.executeQuery(q);
		//		if (c != null && c.moveToNext())
		//			Toast.makeText(thisActivity, "You have Estimated already!", Toast.LENGTH_SHORT).show();
		//		else
		//			newFragment.show(getSupportFragmentManager(), "datePickerestimate" + STAGE3_ESTIMATE_DATE);
		//		c.close();
		//		db.close();

		final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");

	/*	final CharSequence[] estim_items = {"Appointment successful: Enter Estimate Due Date", 
				"Decline the job & send thank you card", 
				"Prospect did not show - send email", "Reschedule Appointment"};*/
		db.openDataBase();		
		boolean dead = isProspectDead();
		
		if (db != null) 
			db.close();

		if(dead){
			final CharSequence[] pre_scheduled_items = {"Schedule Appointment"};						
			AlertDialog.Builder remind_builder = new AlertDialog.Builder(thisActivity);
			remind_builder.setTitle("Set Action...");
			remind_builder.setItems(pre_scheduled_items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					Log.e("", "stage 1 onclik");
					DialogFragment newFragment = new DatePickerFragment();
					if (pre_scheduled_items[item].equals("Schedule Appointment")){
						STAGE_CATEGORY = STAGE2_SCHEDULE_APT;
						Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_SCHEDULE_APT selected - id : " +prospect.prospect_id);
						STAGE = 1;
						newFragment = new DatePickerFragment("Schedule Appointment");					
						newFragment.show(getSupportFragmentManager(), "datePicker" + item);
												
					}
					
				}
			});
			AlertDialog alert = remind_builder.create();
			alert.show();
		}else{
			Log.e("", "stage3"+checkStage3EC);
			db.openDataBase();
			String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
					+" and status = 'estimate'" ;
			Log.v("query","scheduled estimated query: "+q);
			Cursor c = db.executeQuery(q);
			Log.e("", "c is  ===>"+c.getCount());
			if (c != null && c.moveToNext()){
				Log.e("", "c is not null ===>"+c);
				CharSequence[] estim_items = {"Decline job", "Is the estimate complete?"};
				showAlertAt3ai(estim_items);
			}			
//			if(checkStage3EC){
//				CharSequence[] estim_items = {"Decline job", "Is the estimate complete?"};
//				showAlertAt3ai(estim_items);
//			}
			else{
				Log.e("", "c is null ===>"+c);
				CharSequence[] estim_items = {"Reschedule Appointment", 
						"Appointment successful: Enter Estimate Due Date", 
						"Prospect did not show - send email", "Decline the job & send thank you card"};
				showAlert(estim_items);
			} 
		}		
	}

	void showAlertAt3ai(CharSequence[] estim_items){
		final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");

		AlertDialog.Builder estim_builder = new AlertDialog.Builder(thisActivity);
		estim_builder.setTitle("Set Action...");
		estim_builder.setItems(estim_items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				STAGE = 3;
				switch (item) {				
					case 0:		
						Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE3_DECLINE selected - id : " +prospect.prospect_id);
						STAGE_CATEGORY = STAGE3_DECLINE;							
						
						Log.e("", "prospect id ==>> "+ prospect.prospect_id);
						Log.e("", "prospect calendar id ==>> "+ prospect.calendar_id);
						
						try {
							//C.deleteCalendarEvent(C.getCalendarService(prefs), prospect);
							CalendarEventsManagement.deleteCalendarEvent(thisActivity, prospect);
						} catch (ServiceException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
						prospectDead("thankyou");
						isProspectDead = true;
						prospect.calendar_id = "";


			/*		db.openDataBase();
					try {
						SQLiteDatabase getDatabase = db.getWritableDatabase();
						SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
						insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
						insert_statement.bindLong(2, STAGE);
						insert_statement.bindString(3, getProspectStatus());
						Calendar cal = Calendar.getInstance();
						insert_statement.bindString(4, formatter.format(formatter1.parse(getCurrentDateString(cal))));
						insert_statement.bindString(5, getCurrentDateString(cal));
						insert_statement.bindLong(6, STAGE_CATEGORY);
						insert_statement.executeInsert();
						insert_statement.close();
					} catch (ParseException e) {
						Util.insertCaughtException(e, thisActivity);
						e.printStackTrace();
					}	
					db.close();	*/

					AddContent.removeAllViews();
					setDataAndAdapter();
					getMailStages(Calendar.getInstance().getTimeInMillis());
					if (listCampaignName != null && listCampaignName.size() > 0)
						showCampaignList(STAGE_CATEGORY);
					break;
				case 1:
					STAGE_CATEGORY = STAGE3_WHEN_ESTIMATE_COMPLETED;
					callAlert("Complete estimate by now");
					break;
				default:
					break;
				}

			}
		});
		AlertDialog est_alert = estim_builder.create();
		est_alert.show();
	}
	
	void showAlert(CharSequence[] estim_items){
		final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");

		AlertDialog.Builder estim_builder = new AlertDialog.Builder(thisActivity);
		estim_builder.setTitle("Set Action...");
		estim_builder.setItems(estim_items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				STAGE = 3;
				switch (item) {
				case 0:
					STAGE_CATEGORY = STAGE2_RESCHEDULE_APT;
					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_RESCHEDULE_APT selected - id : " +prospect.prospect_id);
					DatePickerFragment newFragment = new DatePickerFragment("Reschedule the estimate appointment");
					newFragment.show(getSupportFragmentManager(), "datePicker" + item);
					break;							
				case 1:					
						STAGE_CATEGORY = STAGE3_ESTIMATE_DATE;						

						Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE3_ESTIMATE_DATE selected - id : " +prospect.prospect_id);
						DialogFragment newFragment1 = new DatePickerFragment("Enter Estimate Due date:");
						db.openDataBase();
						
						String qa = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
								+" and status = 'estimate'";
						Cursor c = db.executeQuery(qa);
						if (c != null && c.moveToNext())
							Toast.makeText(thisActivity, "You have Estimated already!", Toast.LENGTH_LONG).show();
						else
							newFragment1.show(getSupportFragmentManager(), "datePickerestimate" + STAGE3_ESTIMATE_DATE);
						
//						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
//
//					try {
//						C.getGroups(prefs);
//						prospect.contact_id = C.createContact(prospect, prefs);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (ServiceException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (OAuthException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//						Toast.makeText(thisActivity, "Called", Toast.LENGTH_SHORT).show();
						/*Calendar cal = Calendar.getInstance();
						Date date2 = cal.getTime(); 
						
						insertDetailsDB(date2, cal);
						Log.v("query","insertDetailsDB: ");*/
//						SQLiteDatabase getDatabase = db.getWritableDatabase();
//						SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
//						insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
//						insert_statement.bindLong(2, STAGE);
//						insert_statement.bindString(3, getProspectStatus());
////						Calendar cal = Calendar.getInstance();
//						insert_statement.bindString(4, formatter.format(date2));							
//						insert_statement.bindString(5, getCurrentDateString(cal));
//						insert_statement.bindLong(6, STAGE_CATEGORY);
//						insert_statement.executeInsert();
//						insert_statement.close();
//						String q = "update tbl_schedule set status ='"+"estimate"+"' where request_id='"+prospect.prospect_id+"'";
//						String q = "update tbl_schedule set stage = 3 where stage = 4 where request_id='"+prospect.prospect_id+"'";
//						db.executeUpdate(q);
//						Log.v("query","schedule query: "+q);
						c.close();
						db.close();
//						setDataAndAdapter();
					break;					
				case 2:		
////					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE3_PROSPECT_NOT_SHOW selected - id : " +prospect.prospect_id);
//					
//					STAGE_CATEGORY = STAGE3_PROSPECT_NOT_SHOW;
////					prospectDead("thankyou");
////					setDataAndAdapter();  
////					getMailStages(Calendar.getInstance().getTimeInMillis());
//					
//					AddNotes("Prospect did not show",STAGE_THREE);	
//					db.openDataBase();
//					
//					String query = "delete from tbl_schedule where request_id = " + prospect.prospect_id + " " +
//							"and stage = "+STAGE + " and status = 'schedule'  or status = 'reschedule' or status = 'followup'";
//					Log.e("** RENO **", "query==>>"+query);
//					db.executeUpdate(query);	
//					
//					STAGE = 2;
//					Calendar current_cal = Calendar.getInstance();
//					Date current_date = null;
//					try {
//						current_date = formatter1.parse(getCurrentDateString(current_cal));
//					} catch (ParseException e2) {
//						e2.printStackTrace();
//					}
//					db.close();
//					
////					STAGE_CATEGORY = STAGE2_FOLLOWUP_APT;
//					checkProspectNotShow = true;
////					insertDetailsDB(date, c);					
//					db.openDataBase();								
//					db.executeUpdate("update tbl_prospects set stage = " +STAGE+ ", prospect_status = 'followup' where prospect_id = " +prospect.prospect_id);
//					db.executeUpdate("update tbl_schedule set active = 'yes' where request_id = " +prospect.prospect_id);
//
//					db.close();
//					
//					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_FOLLOWUP_APT selected - id : " +prospect.prospect_id);
					STAGE_CATEGORY = STAGE2_FOLLOWUP_APT;
					newFragment = new DatePickerFragment("Follow up to book the Estimate Appointment");
					newFragment.show(getSupportFragmentManager(), "datePicker" + 1);

					break;
				case 3:	   	 Log.v("", "If success");
							 Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE3_DECLINE selected - id : " +prospect.prospect_id);
								STAGE_CATEGORY = STAGE3_DECLINE;							
								prospectDead("thankyou");
								isProspectDead = true;
								try { 	
				
									//C.deleteCalendarEvent(C.getCalendarService(prefs), prospect);
									CalendarEventsManagement.deleteCalendarEvent(thisActivity, prospect);
								} catch (ServiceException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								prospect.calendar_id = "";
								db.openDataBase();
								try {
									SQLiteDatabase getDatabase = db.getWritableDatabase();
									SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
									insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
									insert_statement.bindLong(2, STAGE);
									insert_statement.bindString(3, getProspectStatus());
									Calendar cal = Calendar.getInstance();
									insert_statement.bindString(4, formatter.format(formatter1.parse(getCurrentDateString(cal))));
									insert_statement.bindString(5, getCurrentDateString(cal));
									insert_statement.bindLong(6, STAGE_CATEGORY);
									insert_statement.executeInsert();
									insert_statement.close();
								} catch (ParseException e) {
									Util.insertCaughtException(e, thisActivity);
									e.printStackTrace();
								}	
								db.close();	
								AddContent.removeAllViews();
								setDataAndAdapter();
								getMailStages(Calendar.getInstance().getTimeInMillis());
								if (listCampaignName != null && listCampaignName.size() > 0)
									showCampaignList(STAGE_CATEGORY);			
						
					      						
					break;
				case 4:
					STAGE_CATEGORY = STAGE3_WHEN_ESTIMATE_COMPLETED;
					showEstimateORJobCompletedAlert();
					break;
				default:
					break;
				}

			}
		});
		AlertDialog est_alert = estim_builder.create();
		est_alert.show();
	}
	
	
	int getScaledPixel(int size){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);	    
		return (int) (size * dm.scaledDensity);
	}
	
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		Handler mHandler;
		String message = "";
		String strAlert = "";
		int NoteSTAGE ;
		public DatePickerFragment(){

		}

		public DatePickerFragment(String message){
			this.message = message;
		}
		
		public DatePickerFragment(String message, Handler mHandler,String strAlert, int STAGE){
			this.message = message;
			this.mHandler = mHandler;
			this.strAlert = strAlert;
			NoteSTAGE = STAGE;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker 
			
			Calendar cal = Calendar.getInstance();
			if (STAGE_CATEGORY == STAGE2_RESCHEDULE_APT || STAGE_CATEGORY == STAGE2_FOLLOWUP_APT 
					|| STAGE_CATEGORY == STAGE5_FINISH_DATE)   			
				cal = db.getReminderDate(STAGE, prospect.prospect_id);

			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			
			
			// Create a new instance of DatePickerDialog and return it
			if(!message.equals("")){
				DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
				dialog.setMessage(message);
				return dialog;
			}else{
				return new DatePickerDialog(getActivity(), this, year, month, day);
			}
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			// 2012-12-18 04:15:05    		
			date_time = new StringBuilder();
			date_time.append(year);
			date_time.append("-");    		
			date_time.append(month < 10 ? "0"+(month+1) : (month+1));
			date_time.append("-");    		
			date_time.append(day < 10 ? "0"+day : day);
			date_time.append(" ");
			Log.v("", "date_time: "+date_time.toString());
			
		    Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.YEAR, year);
	        calendar.set(Calendar.MONTH, month); 
	        calendar.set(Calendar.DAY_OF_MONTH, day);
	        
	        
	/*        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY  || 
	        		calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
        		showAlertPicker(thisActivity, "Please select valid working day!");
	        }else{
	        	if(mHandler != null){	        		
	        		DialogFragment newFragment = new TimePickerFragment(mHandler, strAlert, NoteSTAGE);
	        		newFragment.show(getSupportFragmentManager(), "timePicker" );	        	
	        	}else{
	        		DialogFragment newFragment = new TimePickerFragment();
	        		newFragment.show(getSupportFragmentManager(), "timePicker");
	        	}
	        }*/
	        
	        if(mHandler != null){	        		
        		DialogFragment newFragment = new TimePickerFragment(mHandler, strAlert, NoteSTAGE);
        		newFragment.show(getSupportFragmentManager(), "timePicker" );	        	
        	}else{
        		DialogFragment newFragment = new TimePickerFragment();
        		newFragment.show(getSupportFragmentManager(), "timePicker");
        	}
	       
		}
		
		@Override
		public void onCancel(DialogInterface dialog) {			
			super.onCancel(dialog);
			if(mHandler != null){
		        Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("update", "no");
				
				msg.setData(b);			
				mHandler.sendMessage(msg);
	        }
		}
	}

    private  void showAlertPicker(Context thisActivity , String message) {
		try {
			new AlertDialog.Builder(thisActivity).setTitle("The RenoKing")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,	int whichButton) {
									dialog.cancel();									
									DialogFragment newFragmentComplete; // changes
					        		newFragmentComplete = new DatePickerFragment();
									newFragmentComplete.show(getSupportFragmentManager(), "datepicker" );
								}
							}).setMessage(message).create().show();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@SuppressLint("ValidFragment")
	public class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {
		Calendar c ;
		Handler mHandler;
		String strAlert = "";
		int NoteSTAGE ;
		
		TimePickerFragment(){
			
		}

		TimePickerFragment(Handler mHandler,String strAlert, int STAGE){
			this.mHandler = mHandler;
			this.strAlert = strAlert;
			NoteSTAGE = STAGE;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			// Use the current time as the default values for the picker
			c = Calendar.getInstance();
			if (STAGE_CATEGORY == STAGE2_RESCHEDULE_APT || STAGE_CATEGORY == STAGE2_FOLLOWUP_APT)   			
				c = db.getReminderDate(STAGE, prospect.prospect_id);

			int hour = c.get(Calendar.HOUR_OF_DAY);			
			int minute = c.get(Calendar.MINUTE);			
			Log.v("", "HOUR_OF_DAY: "+hour);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			Util.setStartTime();
			
			Calendar calendar = Calendar.getInstance();
		/*	String strYear = date_time.toString().split("-")[0].trim();
			String strMonth = date_time.toString().split("-")[1].trim();
			String strDateTime = date_time.toString().split("-")[2].trim();
			String strDate = strDateTime.split(" ")[0].trim();
			Log.e("", "strDateTime==>>"+strDateTime);
			calendar.set(Calendar.YEAR, Integer.parseInt(strYear));
		    calendar.set(Calendar.MONTH, Integer.parseInt(strMonth)); 
		    calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(strDate));*/
		    
			date_time.append(hourOfDay < 10 ? "0"+hourOfDay : hourOfDay);
			date_time.append(":");
			date_time.append(minute < 10 ? "0"+minute : minute);
			date_time.append(":");
			date_time.append("00");
	       
	        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
	        calendar.set(Calendar.MINUTE, minute); 
	        Log.e("", "hourOfDay==>>"+hourOfDay);
	        
			/*if(Utilities.checkUpdatedTime(calendar, thisActivity)){
				SimpleDateFormat formatter = null;
				Date date = null;
				try {
					formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					date = (Date) formatter.parse(date_time.toString());  
					Log.i("test",""+date);				
				} catch (Exception e){
					Util.insertCaughtException(e, thisActivity);
					e.printStackTrace();				
				}
				loading = ProgressDialog.show(thisActivity, null, "Please wait..");
				new SetDateTime(date).execute();
				
			}else{
				showAlertPicker(thisActivity, "Please select valid working time!");
			}*/
			
			SimpleDateFormat formatter = null;
			Date date = null;
			try {
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = (Date) formatter.parse(date_time.toString());  
				Log.i("test",""+date);				
			} catch (Exception e){
				Util.insertCaughtException(e, thisActivity);
				e.printStackTrace();				
			}
			
//			if(STAGE_CATEGORY == STAGE3_ESTIMATE_DATE && !checkStage3EC){
//				checkStage3EC = true;				
//				insertDetailsDB(date, c);
//			
//			}else{
//				checkStage3EC = false;
//			}	
			
			if(STAGE_CATEGORY == STAGE2_FOLLOWUP_APT){
				insertDetailsDB(date, c);
			}
			
//			if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_SCHEDULE){
//			}

			
			loading = ProgressDialog.show(thisActivity, null, "Please wait..");
//			
			if(STAGE_CATEGORY == STAGE5_FINISH_DATE){
//				db.openDataBase();
//				String check_date = "select reminder_datetime from tbl_schedule where request_id = "+prospect.prospect_id+" and status = 'project_start'";
//				Log.v("query","scheduled estimated query: "+check_date);
//				Cursor c = db.executeQuery(check_date);
//				String project_start = "";
//				if(c != null && c.moveToNext()){
//					int count = c.getCount(); 
//					project_start = c.getString(count - 1);
//					Log.e("", "Date in Record"+project_start);
//					Log.e("", "Date after parse"+Date.parse(project_start));
//				}	
//				
//				if(c != null){
//					c.close();
//					c = null;
//				}
//				db.close();
//			    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
//			    try {
//			    	 Date convertedDate  = dateFormat.parse(project_start);
//			        boolean dateg = convertedDate.before(date);
//			        Log.e("", "Start Finish"+convertedDate);
//			        Log.e("", "Start Finish"+date);			   
//			        Log.e("", "Start Finish"+dateg);
//			        
//			    } catch (ParseException e) {
//			        // TODO Auto-generated catch block
//			        e.printStackTrace();
//			    }
//			   int dategot =  date.compareTo(convertedDate);
			   
		
//			    if(dategot > 0){
					new SetDateTime(date).execute();
//			    }else{
//			    	Utilities.showAlert(thisActivity, "Project finish date should be greater than Project start date");
//			    	loading.dismiss();
//			    }	
//		        loading.dismiss();
	
			}else{
				new SetDateTime(date).execute();
			}
			
			if(mHandler != null){
		        Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("update", "yes");
				b.putString("alert", strAlert);	
				b.putInt("stage", NoteSTAGE);	
				msg.setData(b);			
				mHandler.sendMessage(msg);
	        }
			
		}
		
		
		
		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			if(mHandler != null){
		        Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("update", "no");				
				msg.setData(b);			
				mHandler.sendMessage(msg);
	        }
		}
	}

	class SetDateTime extends AsyncTask<String, Integer, Void>{		
		boolean scheduleConflict = false;
		Date date;
		SetDateTime(Date date){
			this.date = date;
		}

		@Override
		protected Void doInBackground(String... params) {
			
			scheduleConflict = false;
			try {				
				Calendar c = Calendar.getInstance();
				long interval = 0L;				
				interval = date.getTime();
				long curr_milli = c.getTimeInMillis();
				long select_milli = date.getTime();
				Log.i("RenoKing","curr_milli: "+curr_milli);
				Log.i("RenoKing","select_milli: "+select_milli);		
				long time_diff = select_milli - curr_milli;
				Log.i("RenoKing","time diff: : "+time_diff);
				
				long limit_time_diff = 55000L;
				Log.i("RenoKing","time diff limit_time_diff: : "+limit_time_diff);

				/*if (STAGE_CATEGORY == STAGE3_ESTIMATE_DATE)
					limit_time_diff = (TimeConversion.ONE_HOUR * 48) - 55000L;*/

				if (time_diff > limit_time_diff){
					if (canUpdateCalendar()){
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
						try {
							/*if (STAGE_CATEGORY == STAGE2_SCHEDULE_APT || STAGE_CATEGORY == STAGE2_FOLLOWUP_APT){
								if (prospect.calendar_id != null && !prospect.calendar_id.equals("")){
									prospect.calendar_id = C.updateCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, date, prospect);
								}else{
									createCalendarEventFirstTime(prefs, date);
								}
							}else{*/
							
							
							if(STAGE_CATEGORY == STAGE3_ESTIMATE_DATE && !checkStage3EC){
								checkStage3EC = true;				
								insertDetailsDB(date, c);
							
							}else{
								checkStage3EC = false;
							}	
							
								Log.e("", "prospect calendar_id: "+prospect.calendar_id);
								if (STAGE_CATEGORY == STAGE5_FINISH_DATE){		
									/*if(prospect.calendar_id != null  && !prospect.calendar_id.equals(""))										
										prospect.calendar_id = C.updateCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, date, prospect);
									else
										prospect.calendar_id = C.createCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, date, prospect);*/
									//prospect.calendar_id = C.createCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, date, prospect);
									Log.e("", "finish date calendar_id: "+prospect.calendar_id);
                                    String tag = "update";
									prospect.calendar_id = CalendarEventsManagement.updateCalendarEvent(thisActivity, STAGE_CATEGORY, date, prospect, getCalendarID(), tag);
								
									//prospect.calendar_id = CalendarEventsManagement.createCalendarEvent(thisActivity, STAGE_CATEGORY, date, prospect, getCalendarID());
								}else{
									
									if (prospect.calendar_id != null && !prospect.calendar_id.equals("")){
										if(STAGE_CATEGORY == STAGE2_RESCHEDULE_APT){
											//C.deleteCalendarEvent(C.getCalendarService(prefs), prospect);
											//prospect.calendar_id = C.createCalendarEvent(C.getCalendarService(prefs), STAGE2_RESCHEDULE_APT, date, prospect);
											CalendarEventsManagement.deleteCalendarEvent(thisActivity, prospect);
											
											createCalendarEventFirstTime(prefs,STAGE_CATEGORY, date);

										}else{
										//	prospect.calendar_id = C.updateCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, date, prospect);
                                            String tag = "update";
											prospect.calendar_id = CalendarEventsManagement.updateCalendarEvent(thisActivity, STAGE_CATEGORY, date, prospect, getCalendarID(), tag);

										}
									}else{
										Log.e("", "create calendar event first time");
										createCalendarEventFirstTime(prefs,STAGE_CATEGORY, date);
									}
								}
//							}
							Log.v("", "calendar_id: "+prospect.calendar_id);
						} catch (MalformedURLException e) {
							Util.insertCaughtException(e, thisActivity);
							e.printStackTrace();
						} catch (ServiceException e) {
							Util.insertCaughtException(e, thisActivity);
							e.printStackTrace();
						} catch (IOException e) {
							Util.insertCaughtException(e, thisActivity);
							e.printStackTrace();
						}
					}		
					if(Contacts.DISPLAY_NAME.contains("Esack")){
						Log.v("","Contact Exists");
					}else{
		            	Log.v("","Contact Added");
		            }
					
//		            if(Phone.LABEL.contains(prospect.name)){
//		            	Log.v("","Contact Exists");
//		            }else{
//		            	Log.v("","Contact Added");
//		            }
		            
					if(STAGE_CATEGORY ==STAGE3_ESTIMATE_DATE){
						db.openDataBase();
						String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
								+" and status = 'estimate'";
						Log.v("query","schedule query: "+q);
						Cursor ca = db.executeQuery(q);
						if (ca != null && ca.moveToNext())	{
							Log.v("","Contact Exists");
						}else{
							Log.v("","Contact Added");
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
							C.getGroups(prefs);
							prospect.contact_id = C.createContact(prospect, prefs);
						}							
						ca.close();
						db.close();										
					}					
					db.openDataBase();
					if (STAGE_CATEGORY == STAGE2_RESCHEDULE_APT){
						String query = "delete from tbl_schedule where request_id = " + prospect.prospect_id + " " +
								"and stage = "+STAGE + " and status = 'schedule' or status = 'reschedule' or status = 'followup' ";
						Log.e("** RENO **", "query==>>"+query);
//						db.executeUpdate(query);
					}else if (STAGE_CATEGORY == STAGE2_FOLLOWUP_APT){
						String query = "delete from tbl_schedule where request_id = " + prospect.prospect_id + " " +
								"and stage = "+STAGE + " and status = 'schedule'  or status = 'reschedule' or status = 'followup'";
						Log.e("** RENO **", "query==>>"+query);
						db.executeUpdate(query);
					}else if(STAGE_CATEGORY == STAGE5_FINISH_DATE){
						String query = "delete from tbl_schedule where request_id = " + prospect.prospect_id + " " +
								"and stage = "+STAGE + " and status = 'project_finish'";
						Log.e("** RENO **", "query==>>"+query);
						db.executeUpdate(query);
					}
					
					db.close();

					if (STAGE_CATEGORY == STAGE2_SCHEDULE_APT || STAGE_CATEGORY == STAGE2_FOLLOWUP_APT
							|| STAGE_CATEGORY == STAGE2_RESCHEDULE_APT || STAGE_CATEGORY == STAGE2_CONFIRM_APT
							|| STAGE_CATEGORY == STAGE2_CANCEL_APT_FORNOW || STAGE_CATEGORY == STAGE2_CANCEL_APT_FOREVER)
						STAGE = 2;

					
					if(STAGE_CATEGORY != STAGE3_ESTIMATE_DATE){
						Log.v("", "Estimate 88"+STAGE_CATEGORY);
						insertDetailsDB(date, c);
					}
					
					
					
					db.openDataBase();

					if (STAGE_CATEGORY == STAGE2_RESCHEDULE_APT){
						STAGE = 2;
						db.executeUpdate("delete from tbl_schedule where status = '"+getString(R.string.status_enter_estimate)+"' and request_id = " +prospect.prospect_id);
						db.executeUpdate("update tbl_schedule set stage = 2 where stage = 3 and request_id = " +prospect.prospect_id);
					} else if (STAGE_CATEGORY == STAGE4_PROJECT_START || STAGE_CATEGORY == STAGE5_FINISH_DATE){
						STAGE = 5;
					} else if (STAGE_CATEGORY == STAGE4_WAITING_OTHER_ESTIMATES){
						Log.v("","Estimate Waiting === >");
						STAGE = 4;
						db.executeUpdate("delete from tbl_schedule where status = '"+getString(R.string.status_project_start)+"' and request_id = " +prospect.prospect_id);
						db.executeUpdate("update tbl_schedule set stage = 4 where stage = 5 and request_id = " +prospect.prospect_id);
					}
					
					if (STAGE_CATEGORY == STAGE2_FOLLOWUP_APT)
						db.executeUpdate("update tbl_prospects set stage = " +STAGE+ ", prospect_status = 'followup' where prospect_id = " +prospect.prospect_id);						
					else
						db.executeUpdate("update tbl_prospects set stage = " +STAGE+ ", prospect_status = 'active' where prospect_id = " +prospect.prospect_id);

					if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE)
						db.executeUpdate("update tbl_prospects set prospect_status = 'cc' , stage = '6' where prospect_id = " + prospect.prospect_id);						

					
					db.executeUpdate("update tbl_schedule set active = 'yes' where request_id = " +prospect.prospect_id);

					db.close();

					if (STAGE_CATEGORY == STAGE2_CANCEL_APT_FORNOW){
						runOnUiThread(new Runnable() {
							public void run() {
								prospectDead("dead");
							}
						});
					}

					final long settime = interval;
					runOnUiThread(new Runnable() {
						public void run() {							
							setAlarmManager(settime);
							Log.e("", "set date and time==>>"+STAGE_CATEGORY);							
							getMailStages(settime);	
							setDataAndAdapter();
							
							if ((STAGE_CATEGORY == STAGE3_ESTIMATE_DATE || STAGE_CATEGORY == STAGE4_PROJECT_START)
									&& (listCampaignName != null && listCampaignName.size() > 0) && !STAGE3_ESTIMATE_NOT_COMPLETED){
								
								Log.e("** RENO **", " Estimate due date or project start date");
								
								if (STAGE_CATEGORY == STAGE4_PROJECT_START && getWaitingForEstimates())
									return;
								
								Log.e("** RENO **", " showCampaignList ");

								showCampaignList(STAGE_CATEGORY);
							}
							
							if (STAGE_CATEGORY == STAGE4_PROJECT_START){
								STAGE_CATEGORY = STAGE5_FINISH_DATE;
								Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE5_FINISH_DATE selected - id : " +prospect.prospect_id);
								DatePickerFragment newFragment = new DatePickerFragment("Enter project finish date");
								newFragment.show(getSupportFragmentManager(), "datePickerprojectSTAGE4_FINISH_DATE" + STAGE5_FINISH_DATE);
							}					
						}
					});
					//new ChangeStage().start();
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(thisActivity, "Selected date and time should be greater than Current date and time", Toast.LENGTH_LONG).show();
							
						}
					});
				}
			} catch (Exception e) {
				if (loading != null && loading.isShowing())
					loading.dismiss();
				e.printStackTrace();
				Util.insertCaughtException(e, thisActivity);
			}			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			runOnUiThread(new Runnable() {
				public void run() {					
					if (loading != null && loading.isShowing())
						loading.dismiss();
					Util.setEndTime();
					Util.pushPerformanceInfo(thisActivity, Thread.currentThread(), 5, "Creating Calendar event takes more time - id:" +
							" "+prospect.prospect_id+" STAGE: "+STAGE+ " STAGE_CATEGORY: "+STAGE_CATEGORY);
				}
			});
			super.onPostExecute(result);
		}
	}
	
	class ChangeStage extends Thread{
		String stage;
		String strCurrentStage = "", strPrevStage = "";
		ChangeStage(){			
		}
		public void run(){
			db.openDataBase();	
			
			String strSelectQuery = "select stage,current_stage, prev_stage from tbl_prospects where prospect_id = '"+prospect.prospect_id+"'";			
			Cursor recordSet =  db.executeQuery(strSelectQuery);
			if(recordSet != null && recordSet.moveToNext()){	
				stage 			= recordSet.getString(0);	
				strCurrentStage = recordSet.getString(1);	
				strPrevStage    = recordSet.getString(2);	
			}	
			
			if(recordSet!= null){
				recordSet.close();
				recordSet = null;
			}
	
			Log.e("", "stage==>>"+stage);
			Log.e("", "strCurrentStage==>>"+strCurrentStage);
			Log.e("", "strPrevStage==>>"+strPrevStage);
			
			HashMap<String, String> api_params = new HashMap<String, String>();
			api_params.put("prospect_id", prospect.prospect_id);
			api_params.put("pumka_prospect_id",prospect.pumka_prospect_id );			
			api_params.put("current_stage",""+stage);
			api_params.put("prev_stage",strCurrentStage);
			APIClient apiclient = new APIClient(thisActivity, 
					thisActivity.getResources().getString(R.string.api_change_stage), api_params);
			int status = apiclient.processAndFetchResponse();	
			Log.v("", "status: "+status);
			if (status == APIClient.STATUS_SUCCESS){
				Document doc;
				NodeList RESULT;
				try {
					DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docBuilderFactory
							.newDocumentBuilder();
					docBuilder.isValidating();
					DataInputStream in3 = new DataInputStream(
							new ByteArrayInputStream(apiclient.getResponse().getBytes()));
					doc = docBuilder.parse(in3);
					doc.getDocumentElement().normalize();
					
					
					RESULT = doc.getElementsByTagName("RESULT");
				
					if (RESULT.item(0) != null){	
						int result_length = RESULT.getLength();
						Log.e("", "result_length==>>"+result_length);
						for (int i = 0; i < result_length; i++){		
							Node propsect_node = RESULT.item(i);
							if (propsect_node.getNodeType() == Node.ELEMENT_NODE) {
								Element prosElement = (Element) propsect_node;
								if(getTagValue("STATUS", prosElement) != null &&
										getTagValue("STATUS", prosElement).equalsIgnoreCase("success")){
									
									String strUpdateQuery = "update tbl_prospects " +
											"set current_stage = '"+stage+"' and prev_stage = '"+strCurrentStage+"' where prospect_id = '"+prospect.prospect_id+"'";
									
									//db.executeUpdate(strUpdateQuery);
									db.executeQuery(strUpdateQuery);
								}
							}
						}
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					Util.insertCaughtException(e, thisActivity);
				}
			}
			if(db!= null)
				db.close();
		}
	}

	public void insertDetailsDB(Date date, Calendar c){
		db.openDataBase();
		String strProspectStatus = "";
		SimpleDateFormat formatter1 = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
		SQLiteDatabase getDatabase = db.getWritableDatabase();

		SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
		insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
		insert_statement.bindLong(2, STAGE);
		strProspectStatus = getProspectStatus(); 
		Log.e("insertDetailsDB", "date==>>"+date);
		Log.e("insertDetailsDB", "strProspectStatus==>>"+strProspectStatus);
		insert_statement.bindString(3, strProspectStatus);
		insert_statement.bindString(4, formatter1.format(date));
		c.setTime(date);
		insert_statement.bindString(5, getCurrentDateString(c));
		insert_statement.bindLong(6, STAGE_CATEGORY);
		insert_statement.executeInsert();
		insert_statement.close();
		db.close();
		Log.e("** RENO **", "insertDetailsDB==>> stage == >> "+ STAGE + ", STAGE_CATEGORY ==>>"+ STAGE_CATEGORY);
	}
	
	private void createCalendarEventFirstTime(SharedPreferences prefs,int catagory, Date date){
		try {
			//prospect.calendar_id = C.createCalendarEvent(C.getCalendarService(prefs), catagory, date, prospect);
            String tag = "create";
            if(catagory != STAGE2_RESCHEDULE_APT){
            	prospect.calendar_id = CalendarEventsManagement.createCalendarEvent(thisActivity, catagory, date, prospect, getCalendarID(), tag);
    			Log.v("", "prospect.calendar_id"+prospect.calendar_id);
            }
            
            if(catagory == STAGE2_RESCHEDULE_APT){
            	tag= "update";
            	prospect.calendar_id = CalendarEventsManagement.updateCalendarEvent(thisActivity, catagory, date, prospect, getCalendarID(), tag);

            }
			//C.getGroups(prefs);
			//prospect.contact_id = C.createContact(prospect, prefs);
			db.openDataBase();
			SQLiteDatabase getDatabase = db.getWritableDatabase();
			SQLiteStatement insert_statement = getDatabase.compileStatement(db.getIDUpdateQuery(prospect.prospect_id));
			if(prospect.contact_id != null)
				insert_statement.bindString(1, prospect.contact_id);
			else
			insert_statement.bindString(1,"");
			insert_statement.bindString(2, prospect.calendar_id);
			insert_statement.executeInsert();
			insert_statement.close(); 
			db.close();
		} catch (Exception e) {
			Util.insertCaughtException(e, thisActivity);
 			e.printStackTrace();
		}
	}

	private boolean getWaitingForEstimates(){
		boolean waiting = false;
		db.openDataBase();		
		Cursor c1 = db.executeQuery("select * from tbl_schedule where request_id = "+prospect.prospect_id 
				+" and status = 'waiting_estimate'");

		if (c1 != null && c1.moveToNext())	
			waiting = true;
		else			
			waiting = false;

		if (c1 != null) c1.close();
		db.close();

		return waiting;
	}

	@SuppressLint("SimpleDateFormat")
	public void setAlarmManager(long interval){
		String message;
		AlarmManager am = (AlarmManager) thisActivity.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(thisActivity, RenoKingNotifications.class);
		//		intent.putExtra("prospect", prospect);
		intent.putExtra("id", Integer.parseInt(prospect.prospect_id));
		intent.putExtra("name", prospect.name);
		intent.putExtra("title", prospect.name);
		intent.putExtra("status", getProspectStatus());
		intent.putExtra("stage", STAGE);	
		
		Log.e("", "Seting Notifications for (in setAlarmManager function): " + STAGE_CATEGORY);
		
		if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED){
			intent.putExtra("number", prospect.phone_number);
			intent.putExtra("title", String.format(getString(R.string.notify_after_estimate_complete), prospect.name));
			message = prospect.name + " - "+ prospect.phone_number;
		} else if (STAGE_CATEGORY == STAGE2_SCHEDULE_APT || STAGE_CATEGORY == STAGE2_RESCHEDULE_APT) {
			intent.putExtra("status", getString(R.string.status_confirm_schedule));
			intent.putExtra("title", "Confirm Estimate Appointment");
			intent.putExtra("number", prospect.phone_number);			
			message = prospect.name + " - "+ prospect.phone_number;
		} else if (STAGE_CATEGORY == STAGE2_FOLLOWUP_APT) {
//			intent.putExtra("status", getString(R.string.status_confirm_schedule));
			intent.putExtra("status", getString(R.string.status_followup));
			intent.putExtra("title", "Follow up:");
			intent.putExtra("number", prospect.phone_number);			
			//intent.putExtra("number", prospect.phone_number);
			message = prospect.name + " - "+ prospect.phone_number;
		}else if (STAGE_CATEGORY == STAGE2_CANCEL_APT_FORNOW) {
			intent.putExtra("title", "Follow up:" );
			intent.putExtra("number", prospect.phone_number);
//			message = sdf.format(cal.getTime()) +" - "+ prospect.name;
			message = prospect.name + " - "+ prospect.phone_number;
		} else if (STAGE_CATEGORY == STAGE4_PROJECT_START) {
			intent.putExtra("title", "New job starting tomorrow, confirm with client");	
			intent.putExtra("number", prospect.phone_number);
			message = prospect.name + " - "+ prospect.phone_number;
		} else if (STAGE_CATEGORY == STAGE4_WAITING_OTHER_ESTIMATES) {
			intent.putExtra("title", "Follow up" +" - Was waiting for other estimates.");
			intent.putExtra("number", prospect.phone_number);	
			message = prospect.name + " - "+ prospect.phone_number;
		} else if (STAGE_CATEGORY == STAGE5_FINISH_DATE) {
			Log.e("", "finish date notification");
			intent.putExtra("title", getString(R.string.notify_project_finished));			
			message = prospect.name + " - "+ prospect.phone_number;
		}else if(STAGE_CATEGORY == STAGE3_ESTIMATE_DATE){
			intent.putExtra("title", getResources().getString(R.string.notify_estimate_completed));
			message = prospect.name + " - "+ prospect.phone_number;
			intent.putExtra("message", message);
		}else if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_FOLLOWUP){
			intent.putExtra("number", prospect.phone_number);
			intent.putExtra("title", getString(R.string.customer_care_followup));
			intent.putExtra("stage", 6);
			intent.putExtra("number", prospect.phone_number);					
			message = prospect.name + " - "+ prospect.phone_number;
		}else if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_SCHEDULE){
			intent.putExtra("number", prospect.phone_number);
			intent.putExtra("title", getString(R.string.confirm_customercare_appoinment));
			intent.putExtra("stage", 6);
			intent.putExtra("number", prospect.phone_number);
			Calendar cal = db.getReminderDate(STAGE, prospect.prospect_id);
			Log.e("STAGE6_CUSTOMERCARE_SCHEDULE", "cal==>>"+cal.getTime());
			interval = cal.getTimeInMillis();			
			message = prospect.name + " - "+ prospect.phone_number;
		}else if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE){
			intent.putExtra("number", prospect.phone_number);
			intent.putExtra("title", getString(R.string.customer_care_followup));
			intent.putExtra("stage", 6);
			Calendar cal = db.getReminderDate(STAGE, prospect.prospect_id);
			interval = cal.getTimeInMillis();
			message = prospect.name + " - "+ prospect.phone_number;
		}else if(STAGE_CATEGORY == STAGE2_CONFIRM_APT){
			intent.putExtra("title",Utilities.getNotificationMessage(thisActivity, STAGE_CATEGORY, prospect.name));
			message = prospect.name + " - "+ prospect.phone_number;
		}else {
			Log.v("", "setAlarmManager: " + "else");
			message = Utilities.getNotificationMessage(thisActivity, STAGE_CATEGORY, prospect.name);
		}
		Log.v("","Reno King Notes"+message);
		intent.putExtra("message", message);	

		PendingIntent pendingIntent = PendingIntent.getBroadcast(thisActivity, Integer.parseInt(prospect.prospect_id),
				intent, PendingIntent.FLAG_CANCEL_CURRENT);		
		
		interval = Utilities.getTimeInterval(interval, STAGE_CATEGORY, thisActivity);
		Log.v("","interval: "+ interval);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(interval);		
		Log.v("","notify at: "+ cal.getTime().toString());
		am.set(AlarmManager.RTC_WAKEUP,interval, pendingIntent);
	}

	String getQuery(){
		String query  = "INSERT INTO tbl_schedule";
		query += "(request_id";
		query += ",stage";
		query += ",status";
		query += ",reminder_datetime";
		query += ",schedule_creation_datetime";
		query += ",stage_category";
		return query += ")values(?,?,?,?,?,?)";
	}

	public String getProspectStatus(){
		String status = null;
		switch (STAGE_CATEGORY) {
		case STAGE2_SCHEDULE_APT:
			status = getString(R.string.status_schedule);
			break;
		case STAGE2_FOLLOWUP_APT:
			status = getString(R.string.status_followup);
			break;
		case STAGE2_RESCHEDULE_APT:
			status = getString(R.string.status_reschedule);
			break;
		case STAGE2_CONFIRM_APT:
			status = getString(R.string.status_confirm_appointment);
			break;
		case STAGE2_CANCEL_APT_FORNOW:
			status = getString(R.string.status_cancel_appointment_fornow);
			break;
		case STAGE2_CANCEL_APT_FOREVER:
			status = getString(R.string.status_dead);
			break;
		case STAGE3_ESTIMATE_DATE:
			status = getString(R.string.status_enter_estimate);
			break;
		case STAGE3_DECLINE:
			status = getString(R.string.status_dead);
			break;
		case STAGE3_WHEN_ESTIMATE_COMPLETED:
			status = getString(R.string.status_estimate_completed);
			break;
		case STAGE4_PROJECT_START:
			status = getString(R.string.status_project_start);
			break;
		case STAGE4_WAITING_OTHER_ESTIMATES:
			status = getString(R.string.status_waiting_estimates);
			break;
		case STAGE5_FINISH_DATE:
			status = getString(R.string.status_project_finish);
			break;
		case STAGE4_DECLINE:
			status = getString(R.string.status_dead);
			break;
		case STAGE6_CUSTOMERCARE_SCHEDULE:
			status = getString(R.string.status_customer_care);
			break;
		case STAGE6_CUSTOMERCARE_RESCHEDULE:
			status = getString(R.string.status_customer_care);
			break;
			/*case STAGE5_COMMENTS:
			status = getString(R.string.status_comments);
			break;*/
		default:
			break;
		}
		return status;
	}

	//	String getNotificationMessage(){
	//		String title = null;
	//		switch (STAGE_CATEGORY) {
	//		case STAGE2_SCHEDULE_APT:
	//			//			title = getString(R.string.notify_pls_confirm_appointment);
	//			break;
	//		case STAGE2_FOLLOWUP_APT:
	//			title = getString(R.string.notify_followup_appointment);
	//			break;
	//		case STAGE2_RESCHEDULE_APT:
	//			title = getString(R.string.notify_reschedule_appointment);
	//			break;
	//		case STAGE2_CANCEL_APT_FORNOW:
	//			title = getString(R.string.notify_followup_appointment);
	//			break;
	//		case STAGE2_CONFIRM_APT:
	//			title = getString(R.string.notify_estimate_due_date);
	//			break;
	//		case STAGE3_ESTIMATE_DATE:
	//			Log.e("", "Estimate completed notification name: "+prospect.name);
	//			title = String.format(getString(R.string.notify_estimate_completed), prospect.name);
	//			break;
	//		case STAGE3_WHEN_ESTIMATE_COMPLETED:
	//			title = String.format(getString(R.string.notify_estimate_completed), prospect.name);
	//			break;
	//		case STAGE4_PROJECT_START:
	////			title = String.format(getString(R.string.notify_estimate_completed), prospect.name);
	//			break;
	//		case STAGE4_FINISH_DATE:
	//			title = getString(R.string.notify_project_finish);
	//			break;
	//		default:
	//			break;
	//		}
	//		return title;
	//	}

	private boolean canUpdateCalendar(){  
		Log.v("", "canUpdateCalendar: " + (STAGE_CATEGORY != STAGE2_CANCEL_APT_FOREVER 
				&& STAGE_CATEGORY != STAGE3_DECLINE));
		return STAGE_CATEGORY != STAGE2_CANCEL_APT_FOREVER
				&& STAGE_CATEGORY != STAGE3_DECLINE;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ResultHandler.COMMENT_RESULT:	
				STAGE = 5;
				setDataAndAdapter();
				break;	
			case ResultHandler.ADD_PROSPECTS_RESULT:	
				Log.v("Edited", "EDIT_PROSPECTS_RESULT");
//				prosAddressCompleted = true;	
				Log.v("", "Result ok");
				Util.pushActivityInfo(thisActivity, Thread.currentThread(),
						"Prospect added successfully and redirects to Prospect detail page");
				Intent i = new Intent(thisActivity, ProspectDetails.class);
				Bundle b = new Bundle();
				b.putSerializable("prospect", data.getSerializableExtra("prospect"));
				b.putBoolean("open_date_picker", true);
				i.putExtras(b);
				startActivity(i);
			    break;
			default:
				break;				
			}
		}
	}

	private void showEstimateDueDate(){
		Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE3_ESTIMATE_DATE selected - id : " +prospect.prospect_id);
		DialogFragment newFragment1 = new DatePickerFragment("Enter Estimate Due date:");
		STAGE_CATEGORY = STAGE3_ESTIMATE_DATE;
		db.openDataBase();
		String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
				+" and status = 'estimate'";
		Log.v("query","schedule query: "+q);
		Cursor c = db.executeQuery(q);
		if (c != null && c.moveToNext())
			Toast.makeText(thisActivity, "You have Estimated already!", Toast.LENGTH_LONG).show();
		else
			newFragment1.show(getSupportFragmentManager(), "datePickerestimate" + STAGE3_ESTIMATE_DATE);
		c.close();
		db.close();
	}
	
	private void showEstimateORJobCompletedAlert(){
		String message = "";
		Log.e("", "stage category showEstimateORJobCompletedAlert==>>"+ STAGE_CATEGORY);
		if (STAGE_CATEGORY == STAGE3_ESTIMATE_DATE){
			showStage3();
		} else if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED){
			//  message = getString(R.string.notify_estimate_completed);
//			message = "Estimate for "+ prospect.name +" completed?";// changes			
			Log.e("", "STAGE3_WHEN_ESTIMATE_COMPLETED PROSPECT ID ==>>"+ prospect.prospect_id);

			message = "Is the estimate for "+ prospect.name +" complete?";// changes
			String alertQuery = "select status from tbl_schedule where request_id = "+prospect.prospect_id 
					+" and status = 'estimate_complete'" ;
			db.openDataBase();
			Cursor c = db.executeQuery(alertQuery);
						
//			callAlert(message);
			Log.e("", "STAGE3_WHEN_ESTIMATE_COMPLETED PROSPECT ID ==>>"+c.getCount());

			if(c.getCount() != 0)
					Toast.makeText(thisActivity, "Estimate Already Completed for "+prospect.name+"!", Toast.LENGTH_SHORT).show();
			else
				callAlert(message);
			
			c.close();
			db.close();

			/*CharSequence[] estim_items = {"Decline job", "Is the estimate complete?"};
			showAlertAt3ai(estim_items);*/
			
		}else if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
			message = getString(R.string.notify_project_finished)+ " "+prospect.name;
			callAlert(message);
		}else if(STAGE_CATEGORY == STAGE2_FOLLOWUP_APT){
			STAGE_CATEGORY = STAGE2_SCHEDULE_APT;
/*			Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_SCHEDULE_APT selected - id : " +prospect.prospect_id);
			STAGE = 1;
			DatePickerFragment newFragment = new DatePickerFragment("Schedule Appointment");
			db.openDataBase();
			String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
					+" and (status = 'schedule' or status = 'reschedule')";
			Log.v("query","schedule query: "+q);
			Cursor c = db.executeQuery(q);
			if (c != null && c.moveToNext())
				Toast.makeText(thisActivity, "You have Scheduled already, Please Reschedule Appointment!", Toast.LENGTH_SHORT).show();
			else
				newFragment.show(getSupportFragmentManager(), "datePicker" + 0);
			c.close();
			db.close();	*/				
			checkStage2FU = true;
			showAfterFollowUp();
		}
	}

	
	void showAfterFollowUp(){
		Util.pushActivityInfo(thisActivity, Thread.currentThread(), "stage one selected");					
		final CharSequence[] pre_scheduled_items = {"Schedule Appointment", "Follow up", 
				"Cancel Appointment forever","Left Message", "No Answer"};						
		AlertDialog.Builder remind_builder = new AlertDialog.Builder(thisActivity);
		remind_builder.setTitle("Set Action...");
		remind_builder.setItems(pre_scheduled_items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				Log.e("", "stage 1 onclik");
				checkStage2FU = false;
				DialogFragment newFragment = new DatePickerFragment();
				if (pre_scheduled_items[item].equals("Schedule Appointment")){
					STAGE_CATEGORY = STAGE2_SCHEDULE_APT;
					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_SCHEDULE_APT selected - id : " +prospect.prospect_id);
					STAGE = 1;
					newFragment = new DatePickerFragment("Schedule Appointment");
					db.openDataBase();
					String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
							+" and (status = 'schedule' or status = 'reschedule')";
					Log.v("query","schedule query: "+q);
					Cursor c = db.executeQuery(q);
//					if (c != null && c.moveToNext())
//						Toast.makeText(thisActivity, "You have Scheduled already, Please Reschedule Appointment!", Toast.LENGTH_SHORT).show();
//					else
						newFragment.show(getSupportFragmentManager(), "datePicker" + item);
					c.close();
					db.close();
				}else if (pre_scheduled_items[item].equals("Follow up")){
					STAGE_CATEGORY = STAGE2_FOLLOWUP_APT;
					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE2_FOLLOWUP_APT selected - id : " +prospect.prospect_id);
					newFragment = new DatePickerFragment("Follow up to book the Estimate Appointment");
					newFragment.show(getSupportFragmentManager(), "datePicker" + item);
				}else if (pre_scheduled_items[item].equals("Cancel Appointment forever")){
					STAGE_CATEGORY = STAGE2_CANCEL_APT_FOREVER;	
					new DeleteCalendarEvent().execute();
				}else if (pre_scheduled_items[item].equals("Left Message")){
					Log.e("", "stage 1");
					AddNotes("Left Message",1);	
				}else if (pre_scheduled_items[item].equals("No Answer")){
					Log.e("", "stage 1");
					AddNotes("No Answer",1);	
				}
				
			}
		});
		AlertDialog alert = remind_builder.create();
		alert.show();
	}
	
	
	public void callAlert(String message){
		try {
			new AlertDialog.Builder(thisActivity)
			.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton) {	
//					if(loading == null && !loading.isShowing()){
//						loading = ProgressDialog.show(thisActivity, "", "Please wait..");
//					}
					checkStage3EC = false;

					new Thread(){
						@Override
						public void run() {
							//Toast.makeText(thisActivity, "You already set the date for Estimation!", Toast.LENGTH_SHORT).show();
							// create an event to call the prospect 48 hours after the time set in estimate due date.	
							if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
								STAGE_CATEGORY = STAGE6_CUSTOMERCARE_RESCHEDULE;
							}

							SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");	
							SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Calendar current_cal = Calendar.getInstance();

//							final Calendar cal = db.getReminderDate(STAGE, prospect.prospect_id);
							final Calendar cal = Calendar.getInstance();
							Date date = new Date(cal.getTimeInMillis());

							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);				
							Log.v("", "calendar_id: "+prospect.calendar_id);

							Log.e("------------time intervel value-----", "time interval==>");
							Log.e("------------STAGE2_SCHEDULE_APT-----", "STAGE2_SCHEDULE_APT==>"+STAGE_CATEGORY);

							long time_interval = Utilities.getTimeInterval(date.getTime(), STAGE_CATEGORY, thisActivity);
							if (STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE)
								time_interval = Utilities.getModifiedInterval(time_interval, thisActivity);
							
							Date datefromlong = new Date(time_interval);
							SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
							String dateText = df2.format(datefromlong);

							Log.e("dateText", "dateText--->"+dateText);

							Date upadtedDate = null;

							SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
							try {
								upadtedDate = format.parse(dateText);  
								System.out.println(date);  
							} catch (ParseException e) {  
								e.printStackTrace();  
							}

							
							try {
								//C.updateCalendarEvent(C.getCalendarService(prefs), STAGE_CATEGORY, upadtedDate, prospect);
                                String tag = "update";
								CalendarEventsManagement.updateCalendarEvent(thisActivity, STAGE_CATEGORY, upadtedDate, prospect, getCalendarID(), tag);
							} catch (MalformedURLException e) {
								Util.insertCaughtException(e, thisActivity);
								e.printStackTrace();
							} catch (ServiceException e) {
								Util.insertCaughtException(e, thisActivity);
								e.printStackTrace();
							} catch (IOException e) {
								Util.insertCaughtException(e, thisActivity);
								e.printStackTrace();
							}	
							db.openDataBase();
							try {
								SQLiteDatabase getDatabase = db.getWritableDatabase();
								SQLiteStatement insert_statement = getDatabase.compileStatement(getQuery());
								insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
								insert_statement.bindLong(2, STAGE);
								if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED)
									insert_statement.bindString(3, getProspectStatus());
								else
									insert_statement.bindString(3, "job_completed");
								insert_statement.bindString(4, formatter.format(formatter1.parse(getCurrentDateString(current_cal))));
								insert_statement.bindString(5, getCurrentDateString(current_cal));
								insert_statement.bindLong(6, STAGE_CATEGORY);
								insert_statement.executeInsert();
								insert_statement.close();
								//								db.close();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								Util.insertCaughtException(e, thisActivity);
								e.printStackTrace();
							}
							
							if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED){
								STAGE = 4;
								STAGE3_ESTIMATE_NOT_COMPLETED = false;
							}else if (STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE){
//								STAGE = 5;	
								Log.e("", "***call alert sara***");
//								runOnUiThread(new Runnable() {
//									public void run() {
//////										NotificationManager nm = (NotificationManager) thisActivity.getSystemService(Context.NOTIFICATION_SERVICE);
//////										nm.cancel(Integer.parseInt(prospect.prospect_id));
////										showCampaignList(STAGE_CATEGORY);
										STAGE = 6;
//									}
//								});
//								STAGE = 6;
							}
							
							if(STAGE_CATEGORY == STAGE6_CUSTOMERCARE_RESCHEDULE){
								Log.e("", "** call alert if***");
								db.executeUpdate("update tbl_prospects set prospect_status = 'cc' , stage = '6' where prospect_id = " + prospect.prospect_id);						
							}else{
								Log.e("", "** call alert else***");
								db.executeUpdate("update tbl_prospects set stage = " + STAGE + " where prospect_id = " + prospect.prospect_id);						
							}
							db.close();

							runOnUiThread(new Runnable() {
								public void run() {																	
									if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
										STAGE_CATEGORY = STAGE6_CUSTOMERCARE_RESCHEDULE;
									}
									getMailStages(Calendar.getInstance().getTimeInMillis());
									setAlarmManager(cal.getTimeInMillis());
									setDataAndAdapter();
									
									if (loading != null && loading.isShowing())
										loading.dismiss();	
									
								}
							});		
							
//							if(STAGE_CATEGORY == STAGE5_FINISH_DATE || STAGE_CATEGORY == STAGE6_CUSTOMERCARE_FOLLOWUP ){
//								Log.e("", "** call alert if***");
//
//								db.executeUpdate("update tbl_prospects set prospect_status = 'cc' , stage = '6' where prospect_id = " + prospect.prospect_id);						
//							}else{
//								Log.e("", "** call alert else***");
//
//								db.executeUpdate("update tbl_prospects set stage = " + STAGE + " where prospect_id = " + prospect.prospect_id);						
//							}
//							db.close();
//
//							runOnUiThread(new Runnable() {
//								public void run() {																	
//									if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
//										STAGE_CATEGORY = STAGE6_CUSTOMERCARE_FOLLOWUP;
//									}
//									getMailStages(Calendar.getInstance().getTimeInMillis());
//									setAlarmManager(cal.getTimeInMillis());
//									setDataAndAdapter();
//																		
//									if (loading != null && loading.isShowing())
//										loading.dismiss();							
//								}
//							});												

							super.run();
						}
					}.start();
				}
			}).setNegativeButton("No",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {		
					DialogFragment newFragmentComplete; // changes
					DialogFragment newFragmentFinish;
					if (STAGE_CATEGORY == STAGE3_WHEN_ESTIMATE_COMPLETED){
						STAGE_CATEGORY = STAGE3_ESTIMATE_DATE;
						STAGE3_ESTIMATE_NOT_COMPLETED = true;
						Log.e("", "call alert function..");
						newFragmentComplete = new DatePickerFragment(getResources().getString(R.string.notify_when_will_completed));
						getMailStages(Calendar.getInstance().getTimeInMillis());
						newFragmentComplete.show(getSupportFragmentManager(), "datePickerestimate" + STAGE3_ESTIMATE_DATE);
					}else if (STAGE_CATEGORY == STAGE5_FINISH_DATE){
						//STAGE_CATEGORY = STAGE4_PROJECT_START;	
						STAGE_CATEGORY = STAGE5_FINISH_DATE;
						newFragmentFinish = new DatePickerFragment(getResources().getString(R.string.notify_when_project_completed));
						newFragmentFinish.show(getSupportFragmentManager(), "datePickerproject" + STAGE5_FINISH_DATE);
					}
				}
			}).setMessage(message).create().show();
			return;
		} catch (Exception e) {
			Util.insertCaughtException(e, thisActivity);
			e.printStackTrace();
		}
	}

	private String getCurrentDateString(Calendar c){    	
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hourofday = c.get(Calendar.HOUR_OF_DAY);
		int mins = c.get(Calendar.MINUTE);
		c.set(Calendar.SECOND, 0);

		StringBuilder curr_date_time = new StringBuilder();
		curr_date_time.append(year).append("-").append(month < 10 ? "0"+(month+1) : (month+1)).append("-").append(day < 10 ? "0"+day : day).append(" ")
		.append(hourofday < 10 ? "0"+hourofday : hourofday).append(":").append(mins < 10 ? "0"+mins : mins).append(":").append("00");
		Log.v("", "curr_date_time: "+curr_date_time.toString());

		return curr_date_time.toString();
	}

	public void prospectDead(String reason){
		db.openDataBase();
		if (STAGE_CATEGORY == STAGE2_CANCEL_APT_FORNOW)
			db.executeUpdate("update tbl_prospects set prospect_status = 'followup' where prospect_id = " +prospect.prospect_id);
		else
			db.executeUpdate("update tbl_prospects set prospect_status = 'dead' where prospect_id = " +prospect.prospect_id);
		db.executeUpdate("update tbl_schedule set active = 'no' where request_id = " +prospect.prospect_id);
		db.close();
		lblDeadProspect.setVisibility(0);
	}

	private void showCampaignList(final int stageType){ 
		Log.e("", "listCampaignName==>>"+listCampaignName);
		String[] campaign_name = listCampaignName.toArray(new String [listCampaignName.size()]);
		AlertDialog.Builder campaign_builder = new AlertDialog.Builder(thisActivity);		
		campaign_builder.setTitle("Send Campaign");
		campaign_builder.setCancelable(true);
		campaign_builder.setItems(campaign_name, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
//				Util.setStartTime();
				if (prospect.address != null && !prospect.address.equals("")){
					loading = ProgressDialog.show(thisActivity, "", "Please Wait...");
					new PostCampaignThread(item,stageType).start();
				}else{
					Utilities.showAlert(thisActivity, "You must enter prospect's address to send campaign");
				}
				/*switch (item) {
				
				case 0:		
					loading = ProgressDialog.show(thisActivity, "", "Please Wait...");
					new PostCampaignThread(item,stageType).start();
					break;					
				case 1:		
					loading = ProgressDialog.show(thisActivity, "", "Please Wait...");
					new PostCampaignThread(item,stageType).start();
					break;
				case 2:		
					loading = ProgressDialog.show(thisActivity, "", "Please Wait...");
					new PostCampaignThread(item,stageType).start();
					break;							
				case 3:		
					
					break;							
				default:
					break;
				}*/
			}
		});
		AlertDialog campaign_alert = campaign_builder.create();
		
		campaign_alert.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				Log.v("", "Oncancel calls: " +STAGE_CATEGORY);
				if (STAGE_CATEGORY == STAGE4_PROJECT_START){
					STAGE_CATEGORY = STAGE5_FINISH_DATE;
					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE5_FINISH_DATE selected - id : " +prospect.prospect_id);
					DatePickerFragment newFragment = new DatePickerFragment("Enter project finish date");
					newFragment.show(getSupportFragmentManager(), "datePickerprojectSTAGE4_FINISH_DATE" + STAGE5_FINISH_DATE);
				}
			}
		});
		campaign_alert.show();
	}

	class PostCampaignThread extends Thread {
		int item,stageType;
		PostCampaignThread(int item,int stageType){
			this.item = item;
			this.stageType = stageType;
		}

		String strMessage;
		String strStatus;    	
		String action;
		String query;
		Document doc;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			HashMap<String, String> api_params = new HashMap<String, String>();		

			action = thisActivity.getResources().getString(R.string.API_CAMPAIGNS_SEND);    		
			api_params.put("prospect_id", prospect.pumka_prospect_id);
			api_params.put("campaign_id", ""+listCampaignID.get(item));
			if(STAGE3_DECLINE==stageType)
				api_params.put("stage_type", "ed3");
			else if(STAGE3_ESTIMATE_DATE==stageType)
				api_params.put("stage_type", "ec3");
			else if(STAGE4_PROJECT_START==stageType)
				api_params.put("stage_type", "ec4");
			else if(STAGE4_DECLINE==stageType)
				api_params.put("stage_type", "ed4");
			else if(STAGE5_FINISH_DATE==stageType)
				api_params.put("stage_type", "ec4");

			Log.e("", "prospect_id"+prospect.pumka_prospect_id);
			Log.e("", "campaign_id"+listCampaignID.get(item));
			Log.e("", "stage_type"+stageType);

			APIClient apiclient = new APIClient(thisActivity, "sendcampaign", api_params);
			final int status = apiclient.processAndFetchResponse();	
			Log.v("", "status: "+status);

			if (status == APIClient.STATUS_SUCCESS){

				try {
					DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docBuilderFactory
							.newDocumentBuilder();
					docBuilder.isValidating();
					DataInputStream in3 = new DataInputStream(
							new ByteArrayInputStream(apiclient.getResponse().getBytes()));
					doc = docBuilder.parse(in3);
					doc.getDocumentElement().normalize();
					NodeList STATUS = doc.getElementsByTagName("STATUS");
					if (STATUS != null){
						strStatus = Utilities.getNodeValue(doc, "STATUS");		
						strMessage = Utilities.getNodeValue(doc, "MESSAGE");
						Log.v("","strmessage"+strMessage);
					}
				} catch (Exception e) {    				
					e.printStackTrace();
					Util.insertCaughtException(e, thisActivity);
				}
			}else{
				strMessage = apiclient.getErrorMessage();
			}

			if(loading!=null && loading.isShowing()){
				loading.cancel();
				loading = null;
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Util.setEndTime();
					Util.pushPerformanceInfo(thisActivity, Thread.currentThread(), 3, "Sending campaigns takes more than 3 seconds. - id: " +prospect.prospect_id);
					if (status != APIClient.STATUS_SUCCESS)
						Util.pushServerResponseInfo(thisActivity, Thread.currentThread(), strMessage);
					try {
						new AlertDialog.Builder(thisActivity)					
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
								Log.v("", "Alert calls: " +STAGE_CATEGORY);
								if (STAGE_CATEGORY == STAGE4_PROJECT_START){
									STAGE_CATEGORY = STAGE5_FINISH_DATE;
									Util.pushActivityInfo(thisActivity, Thread.currentThread(), "STAGE5_FINISH_DATE selected - id : " +prospect.prospect_id);
									DatePickerFragment newFragment = new DatePickerFragment("Enter project finish date");
									newFragment.show(getSupportFragmentManager(), "datePickerprojectSTAGE4_FINISH_DATE" + STAGE5_FINISH_DATE);
								}
							}
						}).setMessage(strMessage).setCancelable(false).create().show();
						return;
					} catch (Exception e) {
						Log.e("Alert error", "Alert Err: " + e.toString());
					}
				}
			});
		} 	
	}

	class OnTouchEvent implements OnTouchListener {
		int normal, select;

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

	private void getMailStages(long interval){
		String mail = "";
		Log.e("", "getMailStages==>>"+STAGE_CATEGORY);
		switch (STAGE_CATEGORY) {
		
		case STAGE2_SCHEDULE_APT:			
				mail = "2Bi"; // 30 minutes after setting the Estimate Appointment			
			break;
		case STAGE2_FOLLOWUP_APT:	
			if(!checkStage2NoMSG && !checkProspectNotShow){
				Log.e("", "2Biii");
				mail = "2Biii"; // 30 minutes after selecting 2Biii in the app	
			}else if(checkStage2NoMSG){
				Log.e("", "2cv");
				mail = "2Cv"; 
				checkStage2NoMSG = false;
			}else if(checkProspectNotShow){
				Log.e("", "3Aii");
				mail = "3Aii"; 
				checkProspectNotShow = false;
			}
			break;
		/*case STAGE2_RESCHEDULE_APT:
			mail = getString(R.string.notify_reschedule_appointment);
			break;*/
		case STAGE2_CANCEL_APT_FORNOW:
			mail = "2Bii"; // 30 minutes after selecting 2Bii in the app 
			break;
		case STAGE2_CANCEL_APT_FOREVER:
			mail = "2Bii"; // 30 minutes after selecting 2Bii in the app
			break;
			/*case STAGE2_CONFIRM_APT:
			mail = "2Bi"; // 30 minutes after setting the Estimate Appointment
			break;*/
		case STAGE3_ESTIMATE_DATE:
			if(!STAGE3_ESTIMATE_NOT_COMPLETED)
				mail = "3Ai"; // 60 minutes after setting the “estimate due date”
			else{
				mail = "3Bii";
				STAGE3_ESTIMATE_NOT_COMPLETED = false;
			}
			break;
		case STAGE3_PROSPECT_NOT_SHOW:
			mail = "3Aii"; // Immediately after selecting “prospect did not show - send email”
			break;
		case STAGE3_WHEN_ESTIMATE_COMPLETED:
			if(!STAGE3_ESTIMATE_NOT_COMPLETED)
				mail = "3Bi"; // 50 hours after “Estimate Complete” is selected.
			else
				mail = "3Bii"; // Immediately after selecting “No” to “Estimate complete?” Once I enter the new date and time the email will be sent.
			break;
		case STAGE3_DECLINE:
			mail = "3Aiii"; // 30 minutes after selecting 3Aiii in the app ... decline job.
			break;
		case STAGE4_PROJECT_START:
			mail = "4Ai"; // Immediately after selecting “Enter project start date”
			break;
		case STAGE4_WAITING_OTHER_ESTIMATES:
			mail = "4Aii"; // 50 hours after “Estimate Complete” is selected. (same 3Bi)
			break;
		case STAGE5_FINISH_DATE:
			mail = "4Bi"; // 16 days after “Job complete” is selected at stage 4Bi.
			break;
		case STAGE4_DECLINE:
			mail = "4Aiii"; // Immediately after selecting “Decline” in stage 4Aiii
			break;
		case STAGE2_RESCHEDULE_APT:
			//mail = "2Biv"; //  Not implemented			
			mail = "2Cii"; 
			break;		
		case STAGE6_CUSTOMERCARE_FOLLOWUP:
			mail = "6A"; //  Not implemented
			break;
		case STAGE6_CUSTOMERCARE_SCHEDULE:
			mail = "6A"; //  Not implemented
			break;
		case STAGE6_CUSTOMERCARE_RESCHEDULE:
			mail = "6B"; //  Not implemented
			break;
		default:
			break;
		}

//		interval = Utilities.getTimeInterval(interval, STAGE_CATEGORY);
		SimpleDateFormat sdf_date = new SimpleDateFormat("MMM dd yyyy");		
		SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm aa");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(interval);
		String date = sdf_date.format(cal.getTime()) + "###" + sdf_time.format(cal.getTime());

		Log.v("", "mail date: "+date);

		if (!mail.equals("")){
			loading_email = ProgressDialog.show(thisActivity, null, "Sending mail...");
			loading_email.setCancelable(true);			
			new SendMail(thisActivity, prospect, mail, date, rHandler).execute();
			loading_email.dismiss();
		}
	}

	private boolean isScheduled(){
		boolean scheduled = false;
		db.openDataBase();
		String q = "select * from tbl_schedule where request_id = "+prospect.prospect_id 
				+" and (status = 'schedule' or status = 'reschedule')";
		Log.v("query","schedule query: "+q);
		Cursor c = db.executeQuery(q);
		if (c != null && c.moveToNext())
			scheduled = true;
		c.close();
		db.close();
		return scheduled;		
	}

	@Override
	protected void onResume() {    	
		super.onResume();
		Log.e("", "on resume");
		if (BugUserPreferences.getDebugMode(thisActivity).equals("1")){			
			Bitmap debug = BitmapFactory.decodeResource(getResources(),R.drawable.debug_icon_bottom);  
			gest = new DebugIconView(thisActivity, debug, width, height);
			gest.setOnClickListener(new View.OnClickListener() {
				@Override 
				public void onClick(View v) {				
					if(Util.is_dialog_closed){
						Util.showalert(thisActivity);
						Util.is_dialog_closed = false;
					}		
				}
			});
			gest.show();
		}
		
		if(prosAddressCompleted){
			showEstimateDueDate();
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e("", "on start");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.e("", "onRestart");
	}
	
	

/*	@Override
	protected void onPause() {
		super.onPause();
		Log.e("", "on pause");

		try{
			if (gest != null)
				gest.remove();
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	boolean checkProspectAddress(){
		DatabaseConnection dbConnect;
		Cursor RecordSet;
		boolean hasAddress = false;
		dbConnect = new DatabaseConnection(thisActivity);
		dbConnect.openDataBase();
		String selectQuery = "select address,province,zipcode  from tbl_prospects where prospect_id = '"+prospect.prospect_id+"'";
		RecordSet  = dbConnect.executeQuery(selectQuery);
		String strAddress = "", strProvince = "", strZipcode = "";
		if(RecordSet!= null && RecordSet.moveToNext()){
			strAddress = RecordSet.getString(RecordSet.getColumnIndex("address"));
			strProvince = RecordSet.getString(RecordSet.getColumnIndex("province"));
			strZipcode = RecordSet.getString(RecordSet.getColumnIndex("zipcode"));
		}
		
		if(RecordSet!= null){
			RecordSet.close();
			RecordSet = null;
		}
		
		if(dbConnect!= null){
			dbConnect.close();
			dbConnect = null;
		}
		if(!strAddress.equals("") && !strProvince.equals("") && !strZipcode.equals("")){
			hasAddress = true;
		}
		
		return hasAddress;		
	}
	
	private static String getTagValue(String sTag, Element eElement) {
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
	
	private int getCalendarID(){
		int cal_id = ListSelectedCalendars();
		Log.v("", "update cal_id"+cal_id);
		return cal_id;	
	}
	
    private int ListSelectedCalendars() {
        int result = 0;
        String[] calId;
		if(version<14){
		String username = 	getUsername();
	        String[] projection = new String[] { "_id", "name" };
	        String selection = "selected=1";
	        String path = "calendars";
			try {		       
		       Cursor managedCursor = Utilities.getCalendarManagedCursor(thisActivity, projection, selection, path);		         
		        if (managedCursor != null && managedCursor.moveToFirst()) {
		            int nameColumn = managedCursor.getColumnIndex("name");
		            int idColumn = managedCursor.getColumnIndex("_id");
		            calId = new String[managedCursor.getCount()];           
		            for (int i = 0; i < managedCursor.getCount(); i++){
		            	 String calName = managedCursor.getString(nameColumn);
		                 calId[i] = managedCursor.getString(idColumn);
		                 Log.e("", "Found Calendar '" + calName + "' (ID="
		                         + calId[i] + ")");
//		                 if (calName != null && calName.contains("syedfarakatullah.rifluxyss@gmail.com")) {
//		                     result = Integer.parseInt(calId);
//		                 }
		                 if(calId[i].contains("rifluxyss.t4")){
				            	Log.v("","Calender"+calId[i]);
				            }
		                 managedCursor.moveToNext();
		            }      
		          result = Integer.parseInt(calId[2]);
		        } else {
		            Log.e("","No Calendars found");		           
		        }
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}else{
	    	Uri uri = CalendarContract.Calendars.CONTENT_URI;
	    	String[] projection = new String[] {
	    	       CalendarContract.Calendars._ID,
	    	       CalendarContract.Calendars.ACCOUNT_NAME,
	    	       CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
	    	       CalendarContract.Calendars.NAME,
	    	       CalendarContract.Calendars.CALENDAR_COLOR
	    	};

	        String selection = "visible=1";
	        Log.v("", "calenderCursor uri  === >"+uri);
	        Log.v("", "calenderCursor projection  === >"+projection);
	        Log.v("", "calenderCursor selection  === >"+selection);	        
	    	Cursor calendarCursor = managedQuery(uri, projection, selection, null, null);
	    	
	    	try {
	    		Log.v("", "calenderCursor  === >"+calendarCursor);
	    	Log.v("", "calenderCursor Count  === >"+calendarCursor.getCount());
	    		if (calendarCursor != null && calendarCursor.moveToFirst()) {
	    			int nameColumn = calendarCursor.getColumnIndex("name");
		            int idColumn = calendarCursor.getColumnIndex("_id");
		            calId = new String[calendarCursor.getCount()];           
		            for (int i = 0; i < calendarCursor.getCount(); i++){
		            	 String calName = calendarCursor.getString(nameColumn);
		                 calId[i] = calendarCursor.getString(idColumn);
		                
		                 Log.e("", "Found Calendar '" + calName + "' (ID="+ calId[i] + ")");
//		                 }
		                 Log.v("", "Found strGmail '"+strGmail);

		                 Log.v("", "Found calName '"+calName);

		                 if(calName.equals(strGmail)){		                	 
				            	Log.v("","Calender"+calId[i]);
				                result = Integer.parseInt(calId[i]);
				         }
		                 calendarCursor.moveToNext();
		            }           
//		            result = Integer.parseInt(calId[2]);
		            Log.v("","result===>"+result);
		        } else {
		            Log.e("", "No Calendars Detect");
	            }
			} catch (Exception e) {
				e.printStackTrace();				
			}
		}
		Log.v("","result===>"+result);
        return result;
    }
    
}


