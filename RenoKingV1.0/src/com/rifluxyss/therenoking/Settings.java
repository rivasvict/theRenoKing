package com.rifluxyss.therenoking;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import bugtracker.ExceptionReporter;
import bugtracker.Util;

import com.rifluxyss.therenoking.services.RenoKingNotifications;
import com.rifluxyss.therenoking.services.StartNotificationService;
import com.rifluxyss.therenoking.utils.RenoPreferences;

public class Settings extends FragmentActivity {

	Activity thisActivity;
	ImageView imgNotify;
	TextView lblWorking, lblFrom, lblTo, lblFromTime, lblToTime;

	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		thisActivity = this;
		setContentView(R.layout.settings);

		ExceptionReporter.register(thisActivity);

		imgNotify = (ImageView) findViewById(R.id.imgNotify);
		lblWorking = (TextView) findViewById(R.id.lblWorking);
		lblFrom = (TextView) findViewById(R.id.lblFrom);
		lblTo = (TextView) findViewById(R.id.lblTo);
		lblFromTime = (TextView) findViewById(R.id.lblFromTime);
		lblToTime = (TextView) findViewById(R.id.lblToTime);
		
		lblFromTime.setOnClickListener(new OnClick());
		lblToTime.setOnClickListener(new OnClick());
		
		
		lblFromTime.setText(RenoPreferences.getWorkingFromTime(thisActivity));	
		lblToTime.setText(RenoPreferences.getWorkingToTime(thisActivity));


		imgNotify
				.setImageResource(RenoPreferences.getNotify(thisActivity) ? R.drawable.on
						: R.drawable.off);
		imgNotify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RenoPreferences.setNotify(thisActivity,
						!RenoPreferences.getNotify(thisActivity));
				imgNotify.setImageResource(RenoPreferences
						.getNotify(thisActivity) ? R.drawable.on
						: R.drawable.off);
				Log.v("", "notify: " + RenoPreferences.getNotify(thisActivity));
				ComponentName component = new ComponentName(thisActivity,
						RenoKingNotifications.class);
				int status = thisActivity.getPackageManager()
						.getComponentEnabledSetting(component);
				Log.v("", "status receiver: " + status);
				if (RenoPreferences.getNotify(thisActivity)) {
					Util.pushActivityInfo(thisActivity, Thread.currentThread(),
							"Notifications Resumed.");
					thisActivity
							.getPackageManager()
							.setComponentEnabledSetting(
									component,
									PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
									PackageManager.DONT_KILL_APP);
					Toast.makeText(thisActivity, "Notifications Resumed.",
							Toast.LENGTH_SHORT).show();
					startNotificationService();
				} else {
					Util.pushActivityInfo(thisActivity, Thread.currentThread(),
							"Notifications Paused.");
					thisActivity
							.getPackageManager()
							.setComponentEnabledSetting(
									component,
									PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
									PackageManager.DONT_KILL_APP);
					RenoPreferences.setNotificationOffTime(thisActivity,
							Calendar.getInstance().getTimeInMillis());
					Toast.makeText(thisActivity, "Notifications Paused.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	class OnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lblFromTime:
				DialogFragment fromFragment = new TimePickerFragment("from");
				fromFragment.show(getSupportFragmentManager(), "timePicker");
				break;
			case R.id.lblToTime:
				DialogFragment toFragment = new TimePickerFragment("to");
				toFragment.show(getSupportFragmentManager(), "timePicker");
				break;
			default:
				break;
			}
		}

	}
	
	

	@SuppressLint("ValidFragment")
	public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		String type = "";
		String strHour = "", strMinute = "";
		TimePickerFragment(String type){
			this.type = type;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			if(!type.equals("") && type.equals("from")){
				strHour = RenoPreferences.getWorkingFromTime(thisActivity).split(":")[0];
				strMinute = RenoPreferences.getWorkingFromTime(thisActivity).split(":")[1];
				hour = Integer.parseInt(strHour);
				minute = Integer.parseInt(strMinute);
			}else{
				strHour = RenoPreferences.getWorkingToTime(thisActivity).split(":")[0];
				strMinute = RenoPreferences.getWorkingToTime(thisActivity).split(":")[1];
				hour = Integer.parseInt(strHour);
				minute = Integer.parseInt(strMinute);
			}
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,	DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			
			if(!type.equals("") && type.equals("from")){
				RenoPreferences.setWorkingFromTime(thisActivity, pad(hourOfDay)+":"+pad(minute));
				lblFromTime.setText(RenoPreferences.getWorkingFromTime(thisActivity));
			}else{
				RenoPreferences.setWorkingToTime(thisActivity, pad(hourOfDay)+":"+pad(minute));
				lblToTime.setText(RenoPreferences.getWorkingToTime(thisActivity));
			}
			
			Log.e("", "start time==>>"+RenoPreferences.getWorkingFromTime(thisActivity));
			Log.e("", "end time==>>"+RenoPreferences.getWorkingToTime(thisActivity));
		}
	}

	private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
 }
	
	private void startNotificationService() {
		Intent i = new Intent(thisActivity, StartNotificationService.class);
		// i.putExtra("screen_state", screenOff);
		thisActivity.startService(i);
	}
}