package com.rifluxyss.therenoking;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import bugtracker.ExceptionReporter;
import bugtracker.Util;

import com.rifluxyss.therenoking.beans.Prospects;
import com.rifluxyss.therenoking.generic.CalendarEventsManagement;
import com.rifluxyss.therenoking.network.APIClient;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.Utilities;

public class AddProspect extends TheRenoKing {

	Activity thisActivity;

	EditText txtName;
	EditText txtEmail;
	EditText txtCity;
	EditText txtPhone;
	EditText txtAddress;
	EditText txtZip;
	EditText txtProvince;
	EditText txtDetails;

	TextView drpHearAbout;
	TextView drpStartProject;
	TextView drpPriority;
	TextView drpMinMaxBudget;
	TextView lblAddProspect;
	
	String strAddressCheck = "";

	DatabaseConnection db;

    ProgressDialog loading;

	Document doc;

	String name;
	String email;
	String city;
	String phone;
	String address;
	String zipcode;
	String province;
	String details;
	String hear_about_us;
	String start_project;
	String priority;
	String min_max_budget;
	String contact_id;
	Prospects prospect;
	boolean edited = false;

	int selectHearAbout = 0;
	int selectStartProject = 0;
	int selectPriority = 0;
	int selectMinMaxBudget = 0;

	String ERROR_MSG = "";	
	String strFrom = "";
	
    int version = Build.VERSION.SDK_INT;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_prospect);
		thisActivity = this;
		db = new DatabaseConnection(this);
		
		ExceptionReporter.register(thisActivity);

		OnTouchEvent touch_drp = new OnTouchEvent(R.drawable.drop_down_normal, R.drawable.drop_down_normal);

		Button btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClick());
		btnAdd.setOnTouchListener(new OnTouchEvent(R.drawable.add_normal, R.drawable.add_over));

		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtCity = (EditText) findViewById(R.id.txtCity);
		txtPhone = (EditText) findViewById(R.id.txtPhone);
		txtAddress = (EditText) findViewById(R.id.txtAddress);
		txtZip = (EditText) findViewById(R.id.txtZip);
		txtProvince = (EditText) findViewById(R.id.txtProvince);
		txtDetails = (EditText) findViewById(R.id.txtDetails);
		lblAddProspect = (TextView) findViewById(R.id.lblAddProspect);
		drpHearAbout = (TextView) findViewById(R.id.drpHearAbout);
		drpStartProject = (TextView) findViewById(R.id.drpStartProject);
		drpPriority = (TextView) findViewById(R.id.drpPriority);
		drpMinMaxBudget = (TextView) findViewById(R.id.drpMinMaxBudget);

		drpHearAbout.setOnClickListener(new OnClick());
		drpHearAbout.setOnTouchListener(touch_drp);
		drpStartProject.setOnClickListener(new OnClick());
		drpStartProject.setOnTouchListener(touch_drp);
		drpPriority.setOnClickListener(new OnClick());
		drpPriority.setOnTouchListener(touch_drp);
		drpMinMaxBudget.setOnClickListener(new OnClick());
		drpMinMaxBudget.setOnTouchListener(touch_drp);
		
		if(getIntent().getExtras()!=null){
			strFrom = getIntent().getExtras().getString("from");
			if(getIntent().getExtras().getString("address_check") != null){
				strAddressCheck = getIntent().getExtras().getString("address_check");
			}
		}

		if (getIntent().getSerializableExtra("prospect") != null){
			edited = true;
			lblAddProspect.setText(getString(R.string.edit_prospects));
			prospect = (Prospects) getIntent().getSerializableExtra("prospect");
			txtName.setText(prospect.name);
			txtEmail.setText(prospect.email);
			txtCity.setText(prospect.city);
			txtPhone.setText(prospect.phone_number);
			Log.v("", "prospect.address: "+prospect.address);    		
			txtAddress.setText(prospect.address);
			txtZip.setText(prospect.zipcode);
			txtProvince.setText(prospect.province);
			txtDetails.setText(prospect.details);
			if(prospect.referer.equals("")){
				prospect.referer = "others";
			}
			drpHearAbout.setText(prospect.referer);
			drpStartProject.setText(prospect.status_date);
			drpPriority.setText(prospect.priority);
			drpMinMaxBudget.setText(prospect.min_max_budget);
			contact_id = prospect.contact_id;
			btnAdd.setBackgroundResource(R.drawable.save_normal);
			btnAdd.setOnTouchListener(new OnTouchEvent(R.drawable.save_normal, R.drawable.save_over));
		}
	}

//	Handler rHandler = new Handler(){
//		public void handleMessage(Message msg) {
//			Log.v("", "what: "+msg.what);    		
//			switch (msg.what) {
//			case EnumHandler.SYNC_PROSPECTS:	//101
//				break;
//			}
//		};
//	};
	class OnClick implements OnClickListener{

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btnAdd:
				if (getIntent().getSerializableExtra("prospect") != null){
					Util.pushActivityInfo(thisActivity, Thread.currentThread(), "Edit Button Clicked.");
				    Log.v("", "Edit Button");
//					DialogFragment newFragment = new DatePickerFragment("Schedule Appointment");
//					newFragment.show(getSupportFragmentManager(), "datePicker" + item);

				}else
				Util.pushActivityInfo(thisActivity, Thread.currentThread(), "Add Button Clicked.");
				name = txtName.getText().toString().trim();
				email = txtEmail.getText().toString().trim();
				city = txtCity.getText().toString().trim();
				phone = txtPhone.getText().toString().trim();
				details = txtDetails.getText().toString().trim();
				address = txtAddress.getText().toString().trim();
				zipcode = txtZip.getText().toString().trim();
				province = txtProvince.getText().toString().trim();
				hear_about_us = drpHearAbout.getText().toString().trim();
				start_project = drpStartProject.getText().toString().trim();
				priority = drpPriority.getText().toString().trim();
				min_max_budget = drpMinMaxBudget.getText().toString().trim();
				
				txtName.addTextChangedListener(new TextWatcher()
                {
                    public void afterTextChanged(Editable edt){   
                    	
                    }
					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						txtCity.setError(null);
                        txtEmail.setError(null);
                        txtPhone.setError(null);
                        txtAddress.setError(null);
                        txtZip.setError(null);
                        txtProvince.setError(null);
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						if(txtName.getText().length()>0)
                        {
                             txtCity.setError(null);
                             txtEmail.setError(null);
                             txtPhone.setError(null);
                             txtAddress.setError(null);
                             txtZip.setError(null);
                             txtProvince.setError(null);
                        }
					}
                });	
				txtCity.addTextChangedListener(new TextWatcher()
	                {
	                    public void afterTextChanged(Editable edt){
	                        if(txtCity.getText().length()>0)
	                        {
	                             txtName.setError(null);
	                        }
	                    }

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
							txtName.setError(null);
                            txtEmail.setError(null);
                            txtPhone.setError(null);
                            txtAddress.setError(null);
                            txtZip.setError(null);
                            txtProvince.setError(null);
							
						}

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							// TODO Auto-generated method stub
							if(txtCity.getText().length()>0)
	                        {
	                             txtName.setError(null);
	                             txtEmail.setError(null);
	                             txtPhone.setError(null);
	                             txtAddress.setError(null);
	                             txtZip.setError(null);
	                             txtProvince.setError(null);
	                        }
						}
	                });
				
				txtEmail.addTextChangedListener(new TextWatcher()
                {
                    public void afterTextChanged(Editable edt){}

					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						txtName.setError(null);
                        txtCity.setError(null);
                        txtPhone.setError(null);
                        txtAddress.setError(null);
                        txtZip.setError(null);
                        txtProvince.setError(null);
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						if(txtEmail.getText().length()>0)
                        {
                             txtName.setError(null);
                             txtCity.setError(null);
                             txtPhone.setError(null);
                             txtAddress.setError(null);
                             txtZip.setError(null);
                             txtProvince.setError(null);
                        }
					}
                });
				
				txtAddress.addTextChangedListener(new TextWatcher()
                {
                    public void afterTextChanged(Editable edt){}

					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						txtName.setError(null);
                        txtCity.setError(null);
                        txtPhone.setError(null);
                        txtCity.setError(null);
                        txtZip.setError(null);
                        txtProvince.setError(null);
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						if(txtAddress.getText().length()>0)
                        {
                             txtName.setError(null);
                             txtCity.setError(null);
                             txtPhone.setError(null);
                             txtEmail.setError(null);
                             txtZip.setError(null);
                             txtProvince.setError(null);
                        }
					}
                });
				
				txtZip.addTextChangedListener(new TextWatcher()
                {
                    public void afterTextChanged(Editable edt){}

					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						txtName.setError(null);
                        txtCity.setError(null);
                        txtPhone.setError(null);
                        txtCity.setError(null);
                        txtAddress.setError(null);
                        txtProvince.setError(null);
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						if(txtZip.getText().length()>0)
                        {
                             txtName.setError(null);
                             txtCity.setError(null);
                             txtPhone.setError(null);
                             txtEmail.setError(null);
                             txtAddress.setError(null);
                             txtProvince.setError(null);
                        }
					}
                });
				
				txtProvince.addTextChangedListener(new TextWatcher()
                {
                    public void afterTextChanged(Editable edt){}

					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						txtName.setError(null);
                        txtCity.setError(null);
                        txtPhone.setError(null);
                        txtCity.setError(null);
                        txtAddress.setError(null);
                        txtZip.setError(null);
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						if(txtProvince.getText().length()>0)
                        {
                             txtName.setError(null);
                             txtCity.setError(null);
                             txtPhone.setError(null);
                             txtEmail.setError(null);
                             txtAddress.setError(null);
                             txtZip.setError(null);
                        }
					}
                });
				
				txtPhone.addTextChangedListener(new TextWatcher()
                {
                    public void afterTextChanged(Editable edt){}

					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						txtName.setError(null);
                        txtCity.setError(null);
                        txtProvince.setError(null);
                        txtCity.setError(null);
                        txtAddress.setError(null);
                        txtZip.setError(null);
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						if(txtPhone.getText().length()>0)
                        {
                             txtName.setError(null);
                             txtCity.setError(null);
                             txtProvince.setError(null);
                             txtEmail.setError(null);
                             txtAddress.setError(null);
                             txtZip.setError(null);
                        }
					}
                });
				
				if (name.length() == 0){
					if (Utilities.getSmallScreen(thisActivity))						
						Utilities.showAlert(thisActivity, getStringResource(R.string.enter_name));
					else
						txtName.setError(getStringResource(R.string.enter_name));
					txtName.requestFocus();
				}else if (city.length() == 0){
					if (Utilities.getSmallScreen(thisActivity))						
						Utilities.showAlert(thisActivity, getStringResource(R.string.enter_city));
					else						
						txtCity.setError(getStringResource(R.string.enter_city));
					txtCity.requestFocus();
				}else if (email.length() == 0){
					if (Utilities.getSmallScreen(thisActivity))						
						Utilities.showAlert(thisActivity, getStringResource(R.string.enter_email));
					else
						txtEmail.setError(getStringResource(R.string.enter_email));					
					txtEmail.requestFocus();
				}else if (!Utilities.checkEmail(email)){
					if (Utilities.getSmallScreen(thisActivity))						
						Utilities.showAlert(thisActivity, getStringResource(R.string.enter_valid_email));
					else
						txtEmail.setError(getStringResource(R.string.enter_valid_email));
					txtEmail.requestFocus();
				}else if (phone.length() == 0){
					if (Utilities.getSmallScreen(thisActivity))						
						Utilities.showAlert(thisActivity, getStringResource(R.string.enter_phone));
					else						
						txtPhone.setError(getStringResource(R.string.enter_phone));
					txtPhone.requestFocus();
				}else if (address.length() == 0){
					if (Utilities.getSmallScreen(thisActivity))						
						Utilities.showAlert(thisActivity, getStringResource(R.string.enter_address));
					else						
						txtAddress.setError(getStringResource(R.string.enter_address));
					txtAddress.requestFocus();
				}else if (zipcode.length() == 0){
					if (Utilities.getSmallScreen(thisActivity))						
						Utilities.showAlert(thisActivity, getStringResource(R.string.enter_zip));
					else						
						txtZip.setError(getStringResource(R.string.enter_zip));
					txtZip.requestFocus();
				}else if (province.length() == 0){
					if (Utilities.getSmallScreen(thisActivity))						
						Utilities.showAlert(thisActivity, getStringResource(R.string.enter_state));
					else						
						txtProvince.setError(getStringResource(R.string.enter_state));
					txtProvince.requestFocus();
				}else if (hear_about_us.length() == 0){									
					Utilities.showAlert(thisActivity, getStringResource(R.string.select_hear_about));
					drpHearAbout.requestFocus();
				}else if (start_project.length() == 0){									
					Utilities.showAlert(thisActivity, getStringResource(R.string.select_start_project));
					drpStartProject.requestFocus();
				}else if (priority.length() == 0){									
					Utilities.showAlert(thisActivity, getStringResource(R.string.select_priority));
					drpPriority.requestFocus();
				}else if (min_max_budget.length() == 0){									
					Utilities.showAlert(thisActivity, getStringResource(R.string.select_budget));
					drpMinMaxBudget.requestFocus();
				}else if (details.length() == 0){									
					Utilities.showAlert(thisActivity, getStringResource(R.string.enter_details));
					txtDetails.requestFocus();
				}else if(txtName.getText().toString().matches(".*\\d.*")){    
					Utilities.showAlert(thisActivity,"Name should not contain any numeric values");
				}else if(txtCity.getText().toString().matches(".*\\d.*")){    
					Utilities.showAlert(thisActivity,"City should not contain any numeric values");
				}else if(!txtName.getText().toString().matches("[a-zA-Z.? ]*")){    
					Utilities.showAlert(thisActivity,"Name should not contain any special characters");
				}else if(!txtCity.getText().toString().matches("[a-zA-Z.? ]*")){    
					Utilities.showAlert(thisActivity,"City should not contain any special characters");
				}else if(!txtPhone.getText().toString().matches("[a-zA-Z0-9.? ]*")){    
					Utilities.showAlert(thisActivity,"Phone number should not contain any special characters");
				}else if(!txtZip.getText().toString().matches("[a-zA-Z0-9.? ]*")){    
					Utilities.showAlert(thisActivity,"Zip code should not contain any special characters");
				}else {
					Util.setStartTime();
					loading = showLoading();
					new AddProspectThread().start();					
				}
				

				break;
			case R.id.drpHearAbout:
				 txtName.setError(null);
                 txtCity.setError(null);
                 txtProvince.setError(null);
                 txtEmail.setError(null);
                 txtAddress.setError(null);
                 txtZip.setError(null);
                 txtPhone.setError(null);
				showDropDown(v.getId());
				break;
			case R.id.drpStartProject:
				txtName.setError(null);
                txtCity.setError(null);
                txtProvince.setError(null);
                txtEmail.setError(null);
                txtAddress.setError(null);
                txtZip.setError(null);
                txtPhone.setError(null);
				showDropDown(v.getId());
				break;
			case R.id.drpPriority:
				txtName.setError(null);
                txtCity.setError(null);
                txtProvince.setError(null);
                txtEmail.setError(null);
                txtAddress.setError(null);
                txtZip.setError(null);
                txtPhone.setError(null);
				showDropDown(v.getId());
				break;
			case R.id.drpMinMaxBudget:
				txtName.setError(null);
                txtCity.setError(null);
                txtProvince.setError(null);
                txtEmail.setError(null);
                txtAddress.setError(null);
                txtZip.setError(null);
                txtPhone.setError(null);
				showDropDown(v.getId());
				break;
			default:
				break;
			}
		}
	}

	class AddProspectThread extends Thread {

		String strMessage;
		String strStatus;    	
		String action;
		String query;

		@Override
		public void run() {			
			super.run();
			HashMap<String, String> api_params = new HashMap<String, String>();

			if (edited){
				api_params.put("prospect_id", prospect.prospect_id);
				Log.e("pumka_prospect_id==", "pumka_prospect_id==>"+prospect.pumka_prospect_id);
				if(prospect.pumka_prospect_id!=null && !prospect.pumka_prospect_id.equals(""))
					api_params.put("pumka_prospect_id", prospect.pumka_prospect_id); 

				action = thisActivity.getResources().getString(R.string.api_edit_prospect_data);
			}else{
				action = thisActivity.getResources().getString(R.string.api_add_prospect_data);
			}

			api_params.put("name", name);
			api_params.put("email", email);
			api_params.put("city", city);
			api_params.put("phone", phone);
			api_params.put("address", address);
			api_params.put("zip", zipcode);
			api_params.put("region", province);
			api_params.put("aboutus", hear_about_us);
			api_params.put("message", details);
			api_params.put("start", start_project);
			api_params.put("priority", priority);
			api_params.put("budget", min_max_budget);
			api_params.put("device_id", Utilities.getDeviceID(thisActivity));
			db.openDataBase();

			APIClient apiclient = new APIClient(thisActivity, action, api_params);
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
//						if (!strStatus.equals("error")){							  					
							if (strStatus.equalsIgnoreCase("success")){   
								prospect = new Prospects();
								prospect.prospect_id = Utilities.getNodeValue(doc, "PROSPECT_ID");
								Log.e("prospect_id checking==", "prospect_id==>"+prospect.prospect_id);
								Log.e("edited checking==", "edited==>"+edited);

//								if(Utilities.getNodeValue(doc, "PUMKA_PROSPECT_ID") != null && !Utilities.getNodeValue(doc, "PUMKA_PROSPECT_ID").equals(""))
								prospect.pumka_prospect_id = Utilities.getNodeValue(doc, "PUMKA_PROSPECT_ID");

								Log.e("pumka_prospect_id==", "pumka_prospect_id==>"+prospect.pumka_prospect_id);
								String created_time = Utilities.getNodeValue(doc, "CREATEDTIME");
								created_time = created_time == null ? "" : created_time;  
								SQLiteDatabase getDatabase = db.getWritableDatabase();    						
								query = edited ? db.getProspectUpdateQuery(prospect.prospect_id) : db.getProspectInsertQuery();    							
								SQLiteStatement insert_statement = getDatabase.compileStatement(query);    						
								prospect.name = name;
								prospect.email = email;
								prospect.city = city;
								prospect.phone_number = phone;
								prospect.details = details; 
								prospect.address = address;
								prospect.zipcode = zipcode;
								prospect.province = province;
								prospect.priority = priority;
								prospect.min_max_budget = min_max_budget;

								Log.e("","Add Prospect Respone message"+strMessage);

								if (!edited){
//									SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
////									
//									C.getGroups(prefs);
//									prospect.contact_id = C.createContact(prospect, prefs);
									//prospect.calendar_id = C.createCalendarEvent(C.getCalendarService(prefs), prospect);
									Log.e("", "created_time==>"+created_time);
								//	prospect.calendar_id = C.createCalendarEvent(C.getCalendarService(prefs), ProspectDetails.STAGE1_SCHEDULE_APT, Calendar.getInstance().getTime(), prospect);
									int calID = getCalendarID();
									Log.v("","Calender ID===>"+calID);
									loading.dismiss();
									String tag = "insert";
									prospect.calendar_id = CalendarEventsManagement.createCalendarEvent(thisActivity, ProspectDetails.STAGE1_SCHEDULE_APT, Calendar.getInstance().getTime(), prospect, calID, tag);

									insert_statement.bindLong(1, Integer.parseInt(prospect.prospect_id));
									insert_statement.bindLong(2, Integer.parseInt(prospect.pumka_prospect_id));
									insert_statement.bindString(3, "active");
									insert_statement.bindString(4, name);
									insert_statement.bindString(5, email);
									insert_statement.bindString(6, city);
									insert_statement.bindString(7, phone);
									insert_statement.bindString(8, address);
									insert_statement.bindString(9, zipcode);
									insert_statement.bindString(10, province);
									insert_statement.bindString(11, "1");
									insert_statement.bindString(12, hear_about_us);
									insert_statement.bindString(13, details);
									insert_statement.bindString(14, start_project);
									insert_statement.bindString(15, created_time);
									insert_statement.bindString(16, "");
									insert_statement.bindString(17, prospect.calendar_id);
									insert_statement.bindString(18, priority);
									insert_statement.bindString(19, min_max_budget);
									insert_statement.executeInsert();
									insert_statement.close();
									
									Log.e("","Add Prospect Respone message"+strMessage);

								}else{
//									SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
//									try {
//										ContactsService contactService = C.getGroups(prefs);
//										prospect.contact_id = contact_id;
//										Log.v("", "contact_id: "+prospect.contact_id);
//										String id = prospect.contact_id.substring(prospect.contact_id.lastIndexOf("/"), prospect.contact_id.length());
//										Log.v("", "id: "+id + "\n"+C.POST_CONTACTS_GOOGLE + id);
////										C.updateContactName(contactService, new URL(C.POST_CONTACTS_GOOGLE + id), prospect);
//										// C.createCalendarEvent(local_prospect, prefs);									
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
									
									int calID = getCalendarID();
									Log.v("","Calender ID===>"+calID);
									loading.dismiss();
									Log.v("","Updated on online"+ prospect.google_id);
//									String tag = "update";
//									prospect.calendar_id = CalendarEventsManagement.updateCalendarEvent(thisActivity, ProspectDetails.STAGE1_SCHEDULE_APT, Calendar.getInstance().getTime(), prospect, calID, tag);

									insert_statement.bindString(1, name);
									insert_statement.bindString(2, email);
									insert_statement.bindString(3, city);
									insert_statement.bindString(4, phone);
									insert_statement.bindString(5, address);
									insert_statement.bindString(6, zipcode);
									insert_statement.bindString(7, province);
									insert_statement.bindString(8, hear_about_us);
									insert_statement.bindString(9, details);
									insert_statement.bindString(10, start_project);
									insert_statement.bindString(11, created_time);
									insert_statement.bindString(12, priority);
									insert_statement.bindString(13, min_max_budget);
									insert_statement.executeInsert();
									insert_statement.close();        						
								}						
								//prospect = local_prospect;
								//    						Uri contactUri;
								//    						ContentValues values = new ContentValues();
								//    						values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, 
								//    						    newRingtoneUri.toString());
								//    						thisActivity.getContentResolver().update(contactUri, values, where, args);
							}
//						}
					}
				} catch (Exception e) {    				
					e.printStackTrace();
					/*Intent intent = new Intent(android.content.Intent.ACTION_SEND);
					intent.setType("plain/text");
					intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"satheesh.rifluxyss@gmail.com"});  
					intent.putExtra(Intent.EXTRA_SUBJECT, "RenoKing error"); 
					intent.putExtra(Intent.EXTRA_TEXT, e.toString() + "\n\n" + e.getMessage()); 
					startActivity(Intent.createChooser(intent, "Choice App to send email:"));*/
					Util.insertCaughtException(e, thisActivity);
					strStatus = "error";
					strMessage = apiclient.getErrorMessage();
					Log.e("","Add Prospect Respone message"+strMessage);
				}
			}else{
				strStatus = "error";
				strMessage = apiclient.getErrorMessage();
			}
			db.close();
			runOnUiThread(new Runnable() {
				public void run() {
					dismissLoading(loading);
					if (prospect != null && prospect.contact_id == null)
						Util.pushServerResponseInfo(thisActivity, Thread.currentThread(), "Contact not created in Google Contacts, There is a problem when connecting to Google Server.");
					
					if (prospect != null && prospect.calendar_id == null)
						Util.pushServerResponseInfo(thisActivity, Thread.currentThread(), "Event not created in Google Calendar, There is a problem when connecting to Google Server.");

					showAlert(status, strStatus, strMessage);
				}
			});
		} 	
	}

	
	private void showAlert(final int status, final String strStatus, final String message){
		Util.setEndTime();
		Util.pushPerformanceInfo(thisActivity, Thread.currentThread(), Util.loading_time, "Add Prospect takes more time to create.");
		try {
			new AlertDialog.Builder(thisActivity)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					if (status == APIClient.STATUS_SUCCESS && strStatus != null && !strStatus.equals("error")){
//						if (!edited && strFrom.equals("")){
//							Log.v("", "Edited 1");
//							Intent i = new Intent();									
//							prospect.stage = "1";
//							prospect.prospect_status = "active";
//							i.putExtra("prospect", prospect);
//							setResult(RESULT_OK, i);
//						}else{
//							Log.v("", "Edited 2");							
//							setResult(RESULT_OK);
//					    }
						Log.v("", "Edited 1");
						Intent i = new Intent(thisActivity,ProspectDetails.class);	
						if(edited){
							db.openDataBase();
							String stageselect = "select stage from tbl_prospects where prospect_id = "+prospect.prospect_id;
							Cursor c = db.executeQuery(stageselect);
							String stage = "";
							if (c != null && c.moveToNext()){								
								stage = c.getString(c.getColumnIndex("stage"));
							}							
							prospect.stage = stage;
						}else{
							prospect.stage = "1";
						}
						prospect.prospect_status = "active";
						Bundle b = new Bundle();
						b.putSerializable("prospect", prospect);
						if(strAddressCheck != null || !strAddressCheck.equals("")){
							if(strAddressCheck.equals("address_check")){
								Log.e("", "Address check"+strAddressCheck);
								b.putBoolean("open_date_picker", true);
							}
						}else{
							b.putBoolean("open_date_picker", false);
						}
						i.putExtras(b);
						startActivity(i);											
						finish();					
					}else{
						String bug_message = message + "- Name: "+name+ ", Email: "+email+ ", Phone Number: "+phone;
						Util.pushServerResponseInfo(thisActivity, Thread.currentThread(), bug_message);
					}
				}
			}).setMessage("" + message).create().show();
			return;
		} catch (Exception e) {
			Log.e("Alert error", "Alert Err: " + e.toString());
		}
	}

	private void showDropDown(final int res_id){
		CharSequence[] items = null;
		String title = "";
		int selected_item = 0;
		
		switch (res_id) {
		case R.id.drpHearAbout:
			items = getResources().getStringArray(R.array.hear_about_us);
			if (prospect != null && prospect.referer != null && !prospect.referer.equals("")){
				List<CharSequence> arr = Arrays.asList(items);
				selected_item = arr.indexOf(prospect.referer);
			}else{
				selected_item = selectHearAbout;
			}	
			title = "How did you hear about us:";
			break;
		case R.id.drpStartProject:			
			items = getResources().getStringArray(R.array.start_project);
			if (prospect != null && prospect.status_date != null && !prospect.status_date.equals("")){
				List<CharSequence> arr = Arrays.asList(items);
				selected_item = arr.indexOf(prospect.status_date);
			}else{
				selected_item = selectStartProject;
			}	
			title = "When would you like someone to start your project:";
			break;
		case R.id.drpPriority:
			
			items = getResources().getStringArray(R.array.Priority);
			if (prospect != null && prospect.priority != null && !prospect.priority.equals("")){
				List<CharSequence> arr = Arrays.asList(items);
				selected_item = arr.indexOf(prospect.priority);
			}else{
				selected_item = selectPriority;
			}
			title = getString(R.string.priority);
			break;
		case R.id.drpMinMaxBudget:			
			items = getResources().getStringArray(R.array.MinMaxBudget);
			if (prospect != null && prospect.min_max_budget != null && !prospect.min_max_budget.equals("")){
				List<CharSequence> arr = Arrays.asList(items);
				selected_item = arr.indexOf(prospect.min_max_budget);
			}else{
				selected_item = selectMinMaxBudget;
			}	
			title = getString(R.string.min_max_budget);
			break;

		default:
			break;
		}
		final CharSequence[] dropdown = items;
		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
		builder.setTitle(title);
		builder.setSingleChoiceItems(dropdown, selected_item, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				
				switch (res_id) {
				case R.id.drpHearAbout:
					drpHearAbout.setText(dropdown[item].toString());
					selectHearAbout = item;
					break;
				case R.id.drpStartProject:
					drpStartProject.setText(dropdown[item].toString());
					selectStartProject = item;
					break;
				case R.id.drpPriority:
					drpPriority.setText(dropdown[item].toString());
					selectPriority = item;
					break;
				case R.id.drpMinMaxBudget:
					drpMinMaxBudget.setText(dropdown[item].toString());
					selectMinMaxBudget = item;
					break;
				default:
					break;
				}
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	private int getCalendarID(){
		int cal_id = ListSelectedCalendars();
		Log.v("", "cal_id"+cal_id);
		return cal_id;	
	}
	
/*	private boolean ListCalendarEntry(int cal_id, long dtstart) {
		boolean hasadded = false;
		
		try {
			if(version<14){
				String[] projection = new String[] { "calendar_id", "dtstart", "title" };				
		        String selection = "calendar_id="+cal_id + " and title=\"" + URLDecoder.decode(CalEventTitle) + "\" and dtstart='" +dtstart+"'";
//		        String selection = id+"="+cal_id;
		        String path = "events/";
				//Activity context, String[] projection, String selection, String path
		        Cursor managedCursor = Utilities.getCalendarManagedCursor(thisActivity, projection, selection, path);
		       
		        if (managedCursor != null && managedCursor.moveToFirst()) {
		           print("***** Listing Calendar Event Details *****" + managedCursor.getColumnCount());
		           hasadded = true;
		            do {
		            	print( "**START Calendar Event Description**");
		                for (int i = 0; i < managedCursor.getColumnCount(); i++) {	                	
		                	String column = managedCursor.getColumnName(i);
		                	String value = managedCursor.getString(i);
		                	print( column + "="+ value);
		                }
		                print("**END Calendar Event Description**");
		                if (hasadded)
		                	break;
		            } while (managedCursor.moveToNext());
		        } else {
		        	print("No Calendar Entry");
		        }
			}else{
		    	String[] projection = new String[] {
		    	       CalendarContract.Events.CALENDAR_ID,
		    	       CalendarContract.Events.DTSTART,
		    	       CalendarContract.Events.TITLE
		    	};
		    	
		        String selection = CalendarContract.Events.CALENDAR_ID+"="+cal_id +
		        					" and "+CalendarContract.Events.TITLE+"=\"" + URLDecoder.decode(CalEventTitle) + "\"" +
		        					" and "+CalendarContract.Events.DTSTART+"='" +dtstart+"'";
		        print("selection == "+selection);
		    	Uri uri = CalendarContract.Events.CONTENT_URI;
		    	Cursor managedCursor = managedQuery(uri, projection, selection, null, null);
		    	 if (managedCursor != null && managedCursor.moveToFirst()) {
			           print("***** Listing Calendar Event Details *****" + managedCursor.getColumnCount());
			           hasadded = true;
			            do {
			            	print( "**START Calendar Event Description**");
			                for (int i = 0; i < managedCursor.getColumnCount(); i++) {	                	
			                	String column = managedCursor.getColumnName(i);
			                	String value = managedCursor.getString(i);
			                	print( column + "="+ value);
			                }
			                print("**END Calendar Event Description**");
			                if (hasadded)
			                	break;
			            } while (managedCursor.moveToNext());
			        } else {
			        	print("No Calendar Entry");
			        }
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return hasadded;
    }*/
	
	private int ListSelectedCalendars() {
        int result = 0;
        String[] calId;
		if(version<14){
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
		                 managedCursor.moveToNext();
		            }           
		            result = Integer.parseInt(calId[0]);
		        } else {
		            Log.e("", "No Calendars");
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
	    	Cursor calendarCursor = managedQuery(uri, projection, selection, null, null);
	    	try {
	    		if (calendarCursor != null && calendarCursor.moveToFirst()) {
		            int nameColumn = calendarCursor.getColumnIndex("name");
		            int idColumn = calendarCursor.getColumnIndex("_id");
		            calId = new String[calendarCursor.getCount()];           
		            for (int i = 0; i < calendarCursor.getCount(); i++){
		            	 String calName = calendarCursor.getString(nameColumn);
		                 calId[i] = calendarCursor.getString(idColumn);
		                 Log.e("", "Found Calendar '" + calName + "' (ID="
		                         + calId[i] + ")");
//		                 }
		                 calendarCursor.moveToNext();
		            }           
		            result = Integer.parseInt(calId[0]);
		        } else {
		            Log.e("", "No Calendars");
		        }
			} catch (Exception e) {
				e.printStackTrace();				
			}
		}
        return result;
    }
}
