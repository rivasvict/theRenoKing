package com.rifluxyss.therenoking;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bugtracker.Util;

import com.rifluxyss.therenoking.beans.NotesData;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.ResultHandler;

public class Notes extends TheRenoKing {	
	DatabaseConnection db;
	ImageView add_notes;
	ListView lvNotes;	
	TextView lblNoNotes;
	
	Activity thisActivity;
	int prospect_id;
	NotesAdapter adapter;
	
	ArrayList<NotesData> lstNotes = new ArrayList<NotesData>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments);
		
		thisActivity = this;		
		db = new DatabaseConnection(this);
		
		Bundle extras = getIntent().getExtras();
		prospect_id = Integer.parseInt(extras.getString("prospect_id"));
		Log.v("", "Notes pros id: "+ prospect_id);
		lvNotes = (ListView) findViewById(R.id.lvNotes);
		lblNoNotes = (TextView) findViewById(R.id.lblNoNotes);
		add_notes = (ImageView) findViewById(R.id.add_notes);
		add_notes.setOnTouchListener(new OnTouchEvent(R.drawable.ic_left_normal, R.drawable.ic_left_over));
		add_notes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.pushActivityInfo(thisActivity, Thread.currentThread(), "Add Notes Clicked.");
				Intent intent = new Intent(thisActivity, AddNotes.class);
				intent.putExtra("prospect_id", prospect_id);
				startActivityForResult(intent, ResultHandler.COMMENT_RESULT);
			}
		});
		getNotes();
	}
	
	
	
	
	private void getNotes(){
		lstNotes.clear();
		db.openDataBase();
		Cursor c = db.executeQuery("select * from tbl_notes where prospect_id = "+prospect_id);
		while (c != null && c.moveToNext()){
			NotesData note = new NotesData();
			note.notes = c.getString(c.getColumnIndex("notes"));
			note.date = c.getString(c.getColumnIndex("date_time"));
			Log.v("","Notes List===>"+note.toString());
			lstNotes.add(note);
		}
		
		if (adapter == null){
			adapter = new NotesAdapter();
			lvNotes.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		
		if (c != null && c.getCount() > 0){
			lvNotes.setVisibility(0);
			lblNoNotes.setVisibility(8);
		}else{
			lvNotes.setVisibility(8);
			lblNoNotes.setVisibility(0);
		}
		if (c!= null) c.close();
		db.close();		
	}
	
	private class ViewHolder {
		TextView lblNotes;
		TextView lblDate;
	}
	
	class NotesAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lstNotes.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return lstNotes.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.notes_inflate, null);
				viewHolder = new ViewHolder();			
				viewHolder.lblNotes = (TextView) convertView.findViewById(R.id.lblNotes);
				viewHolder.lblDate = (TextView) convertView.findViewById(R.id.lblDate);

				convertView.setTag(viewHolder);			
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			NotesData note = lstNotes.get(position);
			if(note.notes.equals("")){
				viewHolder.lblNotes.setText("This stage email already sent");
			}else{
				viewHolder.lblNotes.setText(note.notes);
			}
			viewHolder.lblDate.setText(note.date);
			
			return convertView;
		}		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == ResultHandler.COMMENT_RESULT){
			getNotes();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Log.e("","App back button clicked");
//		thisActivity.finish();
//		Utilities.showActivity(thisActivity, ProspectDetails.class);
	}
	
	
	
	@Override
	protected void onResume() {
		Log.e("", "On Resume===>");
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		Log.e("", "On Pause===>");
		super.onPause();
	};
	
	@Override
	protected void onDestroy() {
		Log.e("", "On Destroy===>");
		super.onDestroy();
	}
}
