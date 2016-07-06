package com.rifluxyss.therenoking.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import com.rifluxyss.therenoking.utils.DatabaseConnection;

public class Data {
	public static final String TAG = Data.class.getSimpleName();
	Activity thisActivity;
	DatabaseConnection db;
	ArrayList<String> NameList;
	Prospects prospects;
	public Data (Activity thisActivity){
		this.thisActivity = thisActivity;
		db = new DatabaseConnection(thisActivity);
	}
	
	String[] header = { "New Pr", "Book Pr", "Stage 3", "Stage 4", "Stage 5" };	
	
	public List<Pair<String, List<ProspectData>>> getAllData() {
		List<Pair<String, List<ProspectData>>> res = new ArrayList<Pair<String, List<ProspectData>>>();
		
		for (int i = 0; i < header.length; i++) {
			res.add(getOneSection(i));
		}
		
		return res;
	}
	
	public void setNameList(ArrayList<String> namelist){
		NameList = namelist;
	}
	
	public void setPropect(Prospects pros){
		prospects = pros;
	}
	
	public List<ProspectData> getFlattenedData() {
		 List<ProspectData> res = new ArrayList<ProspectData>();
		
		 for (int i = 0; i < header.length; i++) {
			 res.addAll(getOneSection(i).second);
		 }
		 
		 return res;
	}
	
	public Pair<Boolean, List<ProspectData>> getRows(int page) {
		List<ProspectData> flattenedData = getFlattenedData();
		if (page == 1) {
			return new Pair<Boolean, List<ProspectData>>(true, flattenedData.subList(0, 5));
		} else {
			SystemClock.sleep(2000); // simulate loading
			return new Pair<Boolean, List<ProspectData>>(page * 5 < flattenedData.size(), flattenedData.subList((page - 1) * 5, Math.min(page * 5, flattenedData.size())));
		}
	}
	
	private Pair<String, List<ProspectData>> getOneSection(int index){		
		db.openDataBase();		
		ProspectData[][] pros_first = new ProspectData[header.length][];
		String query = "";		
		ProspectData[] pros_second;
		Log.v("", "index: "+ index);
		if (index == 0){
			pros_second = new ProspectData[NameList.size()];
			for (int db_i = 0; db_i < NameList.size(); db_i++){
				ProspectData prosd = new ProspectData();
				prosd.name = NameList.get(db_i);
				pros_second[db_i] = prosd;
			}
		}else{
			query = "select stage, status, reminder_datetime from tbl_schedule " +
					"where request_id = "+prospects.prospect_id+" and stage = "+ (index+1);
			Log.v("", "index: "+ index);	
			Log.v("", "query: "+ query);	
			Cursor c = db.executeQuery(query);
			Log.v("", "c.getCount(): "+ c.getCount());
			pros_second = new ProspectData[c.getCount()];
			if (c != null && c.moveToNext()){
				for (int db_i = 0; db_i < c.getCount(); db_i++){					
					ProspectData prosd = new ProspectData();
					prosd.stage = c.getString(0);
					Log.v("", "prosd.stage: "+prosd.stage);
					prosd.status = c.getString(1);
					prosd.reminder = c.getString(2);
					pros_second[db_i] = prosd;
					c.moveToNext();
				}
			}else{
				int stage = Integer.parseInt(prospects.stage);				
				if (stage == (index+1)){					
					pros_second = new ProspectData[1];
					ProspectData prosd = new ProspectData();
					prosd.stage = ""+index+1;
					pros_second[0] = prosd;
				}
			}
			c.close();
		}
			
		pros_first[index] = pros_second;
		db.close();
		return new Pair<String, List<ProspectData>>(header[index], Arrays.asList(pros_first[index]));
	}
}
