package com.rifluxyss.therenoking;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bugtracker.ExceptionReporter;

public class Statistics extends TheRenoKing {

	Activity thisActivity;
	LinearLayout lnrDateRange,lnrConversionRange;
	TextView lblFilter,lblDateRange,lblFromRange,lblToRange;
	int		filterPos = 0;
	int		dateRangePos = -1;
	int		fromPos = -1, toPos = -1;
	String  strFrom = "", strTo = "";
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		thisActivity = this;
		setContentView(R.layout.statistics);
		
		TextView lblFilterTitle = (TextView) findViewById(R.id.lblFilterTitle);
		lblFilter = (TextView) findViewById(R.id.lblFilter);
		lblDateRange = (TextView) findViewById(R.id.lblDateRange);	
		lblFromRange = (TextView) findViewById(R.id.lblFromRange);
		lblToRange = (TextView) findViewById(R.id.lblToRange);
		lnrDateRange = (LinearLayout) findViewById(R.id.lnrDateRange);
		lnrConversionRange = (LinearLayout) findViewById(R.id.lnrConversionRange);
		

		
		lblFilter.setText(getResources().getStringArray(R.array.stages_filter)[filterPos]);

		
		ExceptionReporter.register(thisActivity);
		loadGraph();
		
		
		lblFilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder filter = new AlertDialog.Builder(thisActivity);
				filter.setSingleChoiceItems(getResources().getStringArray(R.array.stages_filter), filterPos,new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						lblFilter.setText(getResources().getStringArray(R.array.stages_filter)[which]);
						filterPos = which;
						if(filterPos == 1){
							dateRangePos =0;							
							lnrDateRange.setVisibility(View.VISIBLE);
							lnrConversionRange.setVisibility(View.GONE);
							lblDateRange.setText(getResources().getStringArray(R.array.date_range_filter)[dateRangePos]);
						}else if(filterPos == 2){
							lnrDateRange.setVisibility(View.GONE);
							lnrConversionRange.setVisibility(View.VISIBLE);
							fromPos = 1;
							toPos = 2;
							lblFromRange.setText(getResources().getStringArray(R.array.conversion_range)[fromPos]);
							lblToRange.setText(getResources().getStringArray(R.array.conversion_range)[toPos]);
							strFrom = lblFromRange.getText().toString();
							strTo = lblToRange.getText().toString();
						}else{
							lnrDateRange.setVisibility(View.GONE);
							lnrConversionRange.setVisibility(View.GONE);
						}
						dialog.cancel();
						loadGraph();
					}
				}).create().show();	
			}
		});
		
		lblDateRange.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AlertDialog.Builder date = new AlertDialog.Builder(thisActivity);
						date.setSingleChoiceItems(getResources().getStringArray(R.array.date_range_filter), dateRangePos,new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								lblDateRange.setText(getResources().getStringArray(R.array.date_range_filter)[which]);
								dateRangePos = which;								
								dialog.cancel();
								loadGraph();
							}
						}).create().show();	
			}
		});
		
		lblFromRange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder from = new AlertDialog.Builder(thisActivity);
				from.setSingleChoiceItems(getResources().getStringArray(R.array.conversion_range),	fromPos, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								lblFromRange.setText(getResources().getStringArray(R.array.conversion_range)[which]);
								strFrom = lblFromRange.getText().toString();
								fromPos = which;
								dialog.cancel();							
								loadGraph();							
							}
						}).create().show();
			}
		});
		
		lblToRange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder to = new AlertDialog.Builder(thisActivity);
				to.setSingleChoiceItems(getResources().getStringArray(R.array.conversion_range),
						toPos, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								lblToRange.setText(getResources().getStringArray(R.array.conversion_range)[which]);
								strTo = lblToRange.getText().toString();
								toPos = which;
								dialog.cancel();
								loadGraph();
							}
						}).create().show();
			}
		});
		
	}
	
	
	private void loadGraph(){
		Log.v("", "Crashes here");
		StagesBarChart bar_chart = new StagesBarChart(filterPos,dateRangePos,fromPos,toPos);
		Log.v("", "Crashes here comes");
		GraphicalView mChartView = bar_chart.execute(thisActivity);			
		RelativeLayout barLyt = (RelativeLayout) findViewById(R.id.relGraphlayout);
		barLyt.addView(mChartView);
	}
}