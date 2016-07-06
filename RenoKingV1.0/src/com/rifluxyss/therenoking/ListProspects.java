package com.rifluxyss.therenoking;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import bugtracker.ExceptionReporter;
import bugtracker.FormJsonFromExceptionDetails;
import bugtracker.Util;

import com.rifluxyss.therenoking.adapter.ProspectsAdapter;
import com.rifluxyss.therenoking.beans.Prospects;
import com.rifluxyss.therenoking.generic.TimeConversion;
import com.rifluxyss.therenoking.services.RenoKingNotifications;
import com.rifluxyss.therenoking.tasks.SyncProspectsThread;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.EnumHandler;
import com.rifluxyss.therenoking.utils.ResultHandler;
import com.rifluxyss.therenoking.utils.Utilities;

public class ListProspects extends TheRenoKing {

	Activity thisActivity;
	DatabaseConnection db;

	ListView lvProspects;
	TextView lblNoProspects;
	TextView stage1;
	TextView stage2;
	TextView stage3;
	TextView stage4;
	TextView stage5;
	TextView stage6;
	TextView follwup;
	static TextView dead;
	ProspectsAdapter prospect_adapter;
	ArrayList<Prospects> listProspects = new ArrayList<Prospects>();
	EditText txtSearch;
	static int dead_stage;
	TextView previous;
	TextView current;

	ProgressDialog loading;
	ProgressBar progressbar;
	int STAGE = 1;
	public static String prospects;
	SyncProspectsThread SyncProspects;
	Parcelable mListState;
	String LIST_STATE = "listState";

	long one_min = TimeConversion.ONE_MINUTE * 1; // demo

	String strSearchProspect = "";

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_prospect);
		thisActivity = this;
		db = new DatabaseConnection(this);
		getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		Log.v("", "sd card path: "
				+ Environment.getExternalStorageDirectory().getAbsolutePath());

		OnTouchEvent touch_left = new OnTouchEvent(R.drawable.ic_left_normal,
				R.drawable.ic_left_over);
		OnTouchEvent touch_right = new OnTouchEvent(R.drawable.ic_right_normal,
				R.drawable.ic_right_over);

		stage1 = (TextView) findViewById(R.id.stage1);
		stage1.setVisibility(View.VISIBLE);
		stage2 = (TextView) findViewById(R.id.stage2);
		stage1.setBackgroundColor(Color.TRANSPARENT);
		stage1.setTextColor(Color.parseColor("#741B1C"));
		previous = stage1;
		stage3 = (TextView) findViewById(R.id.stage3);
		stage4 = (TextView) findViewById(R.id.stage4);
		stage5 = (TextView) findViewById(R.id.stage5);
		stage6 = (TextView) findViewById(R.id.stage6);
		dead = (TextView) findViewById(R.id.dead);
		follwup = (TextView) findViewById(R.id.followup);
		stage1.setOnClickListener(new OnClick());
		stage2.setOnClickListener(new OnClick());
		stage3.setOnClickListener(new OnClick());
		stage4.setOnClickListener(new OnClick());
		stage5.setOnClickListener(new OnClick());
		stage6.setOnClickListener(new OnClick());

		follwup.setOnClickListener(new OnClick());
		dead.setOnClickListener(new OnClick());

		lblNoProspects = (TextView) findViewById(R.id.lblNoProspects);
		lvProspects = (ListView) findViewById(R.id.lvProspects);
		lvProspects.setTextFilterEnabled(true);
		ImageView add_prospect = (ImageView) findViewById(R.id.add_prospect);
		add_prospect.setOnClickListener(new OnClick());
		add_prospect.setOnTouchListener(touch_right);

		ImageView statistics = (ImageView) findViewById(R.id.statistics);
		statistics.setOnClickListener(new OnClick());
		statistics.setOnTouchListener(touch_left);

		ImageView settings = (ImageView) findViewById(R.id.settings);
		settings.setOnClickListener(new OnClick());
		settings.setOnTouchListener(touch_right);

		ImageView search = (ImageView) findViewById(R.id.search);
		search.setOnClickListener(new OnClick());
		search.setOnTouchListener(touch_left);
		    
		txtSearch = (EditText) findViewById(R.id.txtSearch);
		txtSearch.addTextChangedListener(new TextWatcher() {
			
			public void afterTextChanged(Editable s) {
				
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// current.setBackgroundResource(R.drawable.stage_bg);
				// current.setTextColor(Color.parseColor("#ffffff"));
				if (prospect_adapter != null)
					prospect_adapter.getFilter().filter(s.toString());
			}
		});

		ExceptionReporter.register(thisActivity);

		if (Utilities.isWorkingTime(thisActivity)) {
			Log.e("", "add notification");
			db.openDataBase();
			String strQuery = "select * from tbl_prospects where status_new = 'new'";
			Cursor recordset = db.executeQuery(strQuery);
			if (recordset != null && recordset.moveToNext()) {
				for (int i = 0; i < recordset.getCount(); i++) {
					Prospects prospects = new Prospects();
					prospects.prospect_id = recordset.getString(recordset
							.getColumnIndex("prospect_id"));
					prospects.pumka_prospect_id = recordset.getString(recordset
							.getColumnIndex("pumka_prospect_id"));
					prospects.name = recordset.getString(recordset
							.getColumnIndex("name"));
					prospects.email = recordset.getString(recordset
							.getColumnIndex("email"));
					prospects.city = recordset.getString(recordset
							.getColumnIndex("city"));
					prospects.phone_number = recordset.getString(recordset
							.getColumnIndex("phone_number"));
					prospects.stage = recordset.getString(recordset
							.getColumnIndex("stage"));
					prospects.stage = prospects.stage.equals("") ? "1"
							: prospects.stage;
					prospects.address = recordset.getString(recordset
							.getColumnIndex("address"));
					prospects.zipcode = recordset.getString(recordset
							.getColumnIndex("zipcode"));
					prospects.province = recordset.getString(recordset
							.getColumnIndex("province"));

					prospects.status_date = recordset.getString(recordset
							.getColumnIndex("status_date"));
					prospects.created_time = recordset.getString(recordset
							.getColumnIndex("created_time"));
					prospects.priority = recordset.getString(recordset
							.getColumnIndex("priority"));
					prospects.min_max_budget = recordset.getString(recordset
							.getColumnIndex("min_max_budget"));

					/** Add Shedule Appoinment Notification **/
					AlarmManager am = (AlarmManager) thisActivity
							.getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(thisActivity,
							RenoKingNotifications.class);
					intent.putExtra("id",
							Integer.parseInt(prospects.prospect_id));
					intent.putExtra("title", "Schedule Appointment!");
					intent.putExtra("name", prospects.name);
					intent.putExtra("number", prospects.phone_number);
					intent.putExtra("stage", 1);
					intent.putExtra("message", prospects.name + " - "
							+ prospects.phone_number);
					intent.putExtra("status",
							thisActivity.getString(R.string.status_schedule));

					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							thisActivity, Integer.parseInt(recordset
									.getString(recordset
											.getColumnIndex("prospect_id"))),
							intent, PendingIntent.FLAG_CANCEL_CURRENT);
					am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
							pendingIntent);

					// one_min += TimeConversion.ONE_MINUTE * 5; //demo

					recordset.moveToNext();
				}
			}
			if (recordset != null)
				recordset.close();

			String strUpdateQuery = "UPDATE tbl_prospects SET status_new = 'old' WHERE status_new = 'new' ";
			db.executeUpdate(strUpdateQuery);

			db.close();

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v("", "onResume");
		loading = showLoading();
		new GetProspects().execute("");
	}

	class GetProspects extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			// listProspects = new ArrayList<Prospects>();
			String query = null;
			Log.v("", "stage: " + STAGE);
			listProspects.clear();
			if (STAGE == 0) {
//				query = "select * from tbl_prospects where prospect_status = 'dead' ORDER BY name COLLATE NOCASE";
			} else if (STAGE == 6) {
				query = "select * from tbl_prospects where prospect_status = 'followup' and stage = 2 or stage = 6 ORDER BY name COLLATE NOCASE";				/*
				 * query =
				 * "select * from tbl_prospects where prospect_status = 'active' and stage = 2 and "
				 * +
				 * "prospect_id in (select request_id from tbl_schedule where "
				 * +
				 * "(status = 'followup' or status = 'cancel') and stage = 2) ORDER BY name COLLATE NOCASE"
				 * ;
				 */
			} else if (STAGE == 2) {
				query = "select * from tbl_prospects where prospect_status = 'active' and stage = 2 ORDER BY name COLLATE NOCASE";
			} else if (STAGE == 7) {
				query = "select * from tbl_prospects where prospect_status = 'cc' and stage = 6 ORDER BY name COLLATE NOCASE";

			} else if(STAGE == 8){
				dead_stage = 5;
				query = "select * from tbl_prospects where prospect_status = 'dead' ORDER BY name COLLATE NOCASE";
			} else {			
				query = "select * from tbl_prospects where prospect_status = 'active' and stage = "
						+ STAGE + " ORDER BY name COLLATE NOCASE";
			}

			Log.v("", "query: " + query);

			db.openDataBase();
			Cursor c = db.executeQuery(query);
			while (c != null && c.moveToNext()) {
				Prospects prospects = new Prospects();
				prospects.prospect_id = c.getString(c.getColumnIndex("prospect_id"));
				prospects.pumka_prospect_id = c.getString(c.getColumnIndex("pumka_prospect_id"));
				prospects.prospect_status = c.getString(c.getColumnIndex("prospect_status"));
				prospects.name = c.getString(c.getColumnIndex("name"));
				prospects.email = c.getString(c.getColumnIndex("email"));
				prospects.city = c.getString(c.getColumnIndex("city"));
				prospects.phone_number = c.getString(c.getColumnIndex("phone_number"));
				prospects.address = c.getString(c.getColumnIndex("address"));
				prospects.zipcode = c.getString(c.getColumnIndex("zipcode"));
				prospects.province = c.getString(c.getColumnIndex("province"));
				prospects.stage = c.getString(c.getColumnIndex("stage"));
				prospects.referer = c.getString(c.getColumnIndex("referer"));
				prospects.details = c.getString(c.getColumnIndex("details"));
				prospects.status_date = c.getString(c.getColumnIndex("status_date"));
				prospects.created_time = c.getString(c.getColumnIndex("created_time"));
				prospects.contact_id = c.getString(c.getColumnIndex("contact_id"));
				prospects.calendar_id = c.getString(c.getColumnIndex("calendar_id"));
				prospects.priority = c.getString(c.getColumnIndex("priority"));
				prospects.min_max_budget = c.getString(c.getColumnIndex("min_max_budget"));
				listProspects.add(prospects);
			}

			c.close();
			db.close();
			Log.v("", "list prospects.stage: ");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			if (listProspects.size() > 0) {

				lblNoProspects.setVisibility(View.GONE);
				lvProspects.setVisibility(View.VISIBLE);
				if (prospect_adapter == null) {
					prospect_adapter = new ProspectsAdapter(thisActivity,
							R.layout.prospect_inflate, listProspects);
					lvProspects.setAdapter(prospect_adapter);
				} else {
					prospect_adapter.setProspects(listProspects);
					prospect_adapter.notifyDataSetChanged();
				}

				prospect_adapter.setHandler(rHandler);

				if (!prospect_adapter.getSearch().equals(""))
					txtSearch.setText(prospect_adapter.getSearch());
				else
					txtSearch.setText("");

				/*
				 * lvProspects.setOnItemClickListener(new OnItemClickListener()
				 * {
				 * 
				 * @Override public void onItemClick(AdapterView<?> parent, View
				 * view, int position, long id) { // TODO Auto-generated method
				 * stub Log.v("",
				 * "item prospects.stage: "+listProspects.get(position).stage);
				 * 
				 * Log.v("",
				 * "txtSearch: "+txtSearch.getText().toString().trim().
				 * equals(""));
				 * 
				 * if(!txtSearch.getText().toString().trim().equals(""))
				 * strSearchProspect = txtSearch.getText().toString().trim();
				 * Intent i = new Intent(thisActivity, ProspectDetails.class);
				 * Bundle b = new Bundle(); b.putSerializable("prospect",
				 * listProspects.get(position)); i.putExtras(b);
				 * startActivity(i); } });
				 * lvProspects.setOnItemLongClickListener(new
				 * OnItemLongClickListener() {
				 * 
				 * @Override public boolean onItemLongClick(AdapterView<?>
				 * parent, View view, final int position, long id) { try { new
				 * AlertDialog.Builder(thisActivity) .setTitle("The RenoKing")
				 * .setPositiveButton("Yes", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int whichButton) { Intent i =
				 * new Intent(thisActivity, AddProspect.class); Bundle b = new
				 * Bundle(); b.putSerializable("prospect",
				 * listProspects.get(position)); i.putExtras(b);
				 * startActivity(i); } }).setNegativeButton("No", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int whichButton) {
				 * dialog.cancel(); }
				 * }).setMessage("Do you want to edit this Prospect?"
				 * ).create().show(); } catch (Exception e) {
				 * Log.e("Alert error", "Alert Err: " + e.toString()); } return
				 * false; } });
				 */
			} else {
				if (listProspects.size() > 0) {
					prospect_adapter.setProspects(listProspects);
					prospect_adapter.notifyDataSetChanged();
					lblNoProspects.setVisibility(View.VISIBLE);
					lvProspects.setVisibility(View.GONE);
				} else {
					lblNoProspects.setVisibility(View.VISIBLE);
					lvProspects.setVisibility(View.GONE);
				}
				/*
				 * prospect_adapter.setProspects(listProspects);
				 * prospect_adapter.notifyDataSetChanged();
				 * lblNoProspects.setVisibility(View.VISIBLE);
				 * lvProspects.setVisibility(View.GONE);
				 */

			}
			dismissLoading(loading);

			super.onPostExecute(result);

		}
	}

	Handler rHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.v("", "what: " + msg.what);
			switch (msg.what) {
			case EnumHandler.FILTER_PROSPECTS:
				if (msg.arg1 > 0) {
					lvProspects.setVisibility(View.VISIBLE);
					lblNoProspects.setVisibility(View.GONE);
				} else {
					lvProspects.setVisibility(View.GONE);
					lblNoProspects.setVisibility(View.VISIBLE);
				}
				break;
			case EnumHandler.PROSPECT_DETAIL:
				Log.v("", "txtSearch: "
						+ txtSearch.getText().toString().trim().equals(""));
				if (!txtSearch.getText().toString().trim().equals(""))
					strSearchProspect = txtSearch.getText().toString().trim();
				Intent i = new Intent(thisActivity, ProspectDetails.class);
				Bundle b = new Bundle();
				b.putSerializable("prospect", listProspects.get(msg.arg1));
				i.putExtras(b);
				startActivity(i);
				break;
			}
		};
	};

	class OnClick implements OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.add_prospect:
				Util.pushActivityInfo(thisActivity, Thread.currentThread(),
						"Add Prospect Button clicked.");
				startActivityForResult(new Intent(thisActivity,	AddProspect.class), ResultHandler.ADD_PROSPECTS_RESULT);				
				break;
			case R.id.settings:
				Util.pushActivityInfo(thisActivity, Thread.currentThread(),
						"Settings Button clicked.");
				// Utilities.showAlert(thisActivity,
				// getStringResource(R.string.under_development));
				Log.v("",
						"Utilities.backupDatabase(): "
								+ Utilities.backupDatabase());
				startActivity(new Intent(thisActivity, Settings.class));
				new FormJsonFromExceptionDetails(thisActivity).execute();
				break;
			case R.id.statistics:
				Util.pushActivityInfo(thisActivity, Thread.currentThread(),
						"Statistics Button clicked.");
				// Utilities.showAlert(thisActivity,
				// getStringResource(R.string.under_development));
				startActivity(new Intent(thisActivity, Statistics.class));
				break;
			case R.id.search:
				Util.pushActivityInfo(thisActivity, Thread.currentThread(),
						"Search Button clicked.");
				if (txtSearch.getVisibility() == 0) {
					txtSearch.setVisibility(8);
					txtSearch.setText("");
				} else {
					txtSearch.setVisibility(0);
				}
				break;
			case R.id.stage1:
				SyncProspects = new SyncProspectsThread(thisActivity,
						progressbar, rHandler);
				SyncProspects.execute("");
				STAGE = 1;
				prospects = "stage1";
				setBG((TextView) v);
				// lvProspects.setVisibility(View.GONE);
				// lblNoProspects.setVisibility(View.VISIBLE);
				loading = showLoading();
				new GetProspects().execute("");
				break;
			case R.id.stage2:
				STAGE = 2;
				setBG((TextView) v);
				// loading = showLoading();
				new GetProspects().execute("");
				break;
			case R.id.stage3:
				STAGE = 3;
				setBG((TextView) v);
				// loading = showLoading();
				new GetProspects().execute("");
				break;
			case R.id.stage4:
				STAGE = 4;
				setBG((TextView) v);
				// loading = showLoading();
				new GetProspects().execute("");
				break;
			case R.id.stage5:
				STAGE = 5;
				setBG((TextView) v);
				// loading = showLoading();
				new GetProspects().execute("");
				break;
			case R.id.stage6:
				STAGE = 7;
				setBG((TextView) v);
				// loading = showLoading();
				new GetProspects().execute("");
				break;
			case R.id.followup:
				STAGE = 6;
				setBG((TextView) v);
				// loading = showLoading();
				new GetProspects().execute("");
				break;
			case R.id.dead:
				STAGE = 8;
				setBG((TextView) v);
				// loading = showLoading();
				new GetProspects().execute("");
				break;
			default:
				break;
			}
		}
	}

	void setBG(TextView currenttxt) {
		current = currenttxt;
		previous.setBackgroundResource(R.drawable.stage_bg);
		previous.setTextColor(Color.parseColor("#ffffff"));
		currenttxt.setBackgroundColor(Color.TRANSPARENT);
		currenttxt.setTextColor(Color.parseColor("#741B1C"));
		previous = currenttxt;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ResultHandler.ADD_PROSPECTS_RESULT:
				Log.v("", "Result ok");
				Util.pushActivityInfo(thisActivity, Thread.currentThread(),
						"Prospect added successfully and redirects to Prospect detail page");
				Intent i = new Intent(thisActivity, ProspectDetails.class);
				Bundle b = new Bundle();
				b.putSerializable("prospect",data.getSerializableExtra("prospect"));
				b.putBoolean("open_date_picker", true);
				i.putExtras(b);
				startActivity(i);
				break;
		
			default:
				break;
			}
		}
	}
}
