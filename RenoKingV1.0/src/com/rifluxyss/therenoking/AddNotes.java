package com.rifluxyss.therenoking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import bugtracker.BugUserPreferences;
import bugtracker.ExceptionReporter;
import bugtracker.Util;

import com.rifluxyss.therenoking.utils.DatabaseConnection;

public class AddNotes extends TheRenoKing {	
	DatabaseConnection db;
	Activity thisActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_comments);
		thisActivity = this;

		final Bundle extras = getIntent().getExtras();		
		final EditText txtComment 	= (EditText) findViewById(R.id.txtComment);
		Button btnSave 				= (Button) findViewById(R.id.btnSave);
		btnSave.setOnTouchListener(new OnTouchEvent(R.drawable.save_normal, R.drawable.save_over));
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txtComment.getText().toString().trim().length() > 0){
					Util.pushActivityInfo(AddNotes.this, Thread.currentThread(), "Save Notes Button Clicked.");
					String query  = "INSERT INTO tbl_notes";
					query += "(prospect_id";
					query += ",notes";
					query += ",date_time";
					query += ")values(?,?,?)";

					SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy hh:mm aa");

					try {
						db = new DatabaseConnection(AddNotes.this);				
						db.openDataBase();
						SQLiteDatabase getDatabase = db.getWritableDatabase();
						SQLiteStatement insert_statement = getDatabase.compileStatement(query);
						Log.v("", "AddNotes pros id: "+ extras.getInt("prospect_id"));
						insert_statement.bindLong(1, extras.getInt("prospect_id"));	
						
						insert_statement.bindString(2, txtComment.getText().toString());
						Calendar current_cal = Calendar.getInstance();
						try {
							insert_statement.bindString(3, formatter.format(formatter1.parse(getCurrentDateString(current_cal))));
						} catch (ParseException e) {						
							e.printStackTrace();
							Util.insertCaughtException(e, AddNotes.this);
						}

						insert_statement.executeInsert();
						insert_statement.close();
						db.close();


						new AlertDialog.Builder(AddNotes.this)
						.setTitle("The RenoKing")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								setResult(RESULT_OK);
								finish();
							}
						}).setMessage("Notes Added Successfully").setCancelable(false)
						.create().show();
						return;
					} catch (Exception e) {
						Util.insertCaughtException(e, AddNotes.this);
						e.printStackTrace();
					}					
				}else{
					Toast.makeText(AddNotes.this, "Please enter comments.", Toast.LENGTH_LONG).show();
				}
			}
		});

		if(BugUserPreferences.getDebugMode(AddNotes.this).equals("1")){
			ExceptionReporter.register(AddNotes.this);
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
		curr_date_time.append(year).append("-").append(month < 10 ? "0"+(month+1) : (month+1)).append("-").append(day).append(" ")
		.append(hourofday < 10 ? "0"+hourofday : hourofday).append(":").append(mins < 10 ? "0"+mins : mins).append(":").append("00");
		Log.v("", "curr_date_time: "+curr_date_time.toString());

		return curr_date_time.toString();
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	};

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
}
