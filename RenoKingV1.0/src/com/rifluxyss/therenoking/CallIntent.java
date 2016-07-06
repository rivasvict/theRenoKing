package com.rifluxyss.therenoking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.rifluxyss.therenoking.utils.RenoPreferences;

public class CallIntent extends Activity{
	String number = "";
	Activity thisActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		thisActivity = this;
		Bundle extras = getIntent().getExtras();		
		if(extras!= null){
			number = extras.getString("number");
			int id = extras.getInt("prospect_id");
			String status = extras.getString("status");
			RenoPreferences.setCalledId(thisActivity, ""+id);
			RenoPreferences.setStatus(thisActivity, ""+status);
			RenoPreferences.setNumber(thisActivity,  ""+number);

			Log.e("CallIntent", " extras *** NUMBER==>>"+number);
		}	
		Log.e("CallIntent", "NUMBER==>>"+number);
		Intent intent = new Intent(Intent.ACTION_CALL);	
		intent.setData(Uri.parse("tel:" + number));
		startActivity(intent);
		thisActivity.finish();
	}
}
