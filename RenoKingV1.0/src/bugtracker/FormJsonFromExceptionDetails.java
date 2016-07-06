package bugtracker;

import java.io.File;
import java.io.FileWriter;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.rifluxyss.therenoking.utils.Utilities;


public class FormJsonFromExceptionDetails  extends AsyncTask<String, Void, Void> {
	FileWriter writer;
	Activity context;
	File exceptionfile;
	Cursor RecordSet;
	BugDatabaseConnection dbConnection;
	
	String message_of_event = "";
	String detailed_message = "";
	Message msg;
	public FormJsonFromExceptionDetails(Activity context){
		this.context = context;
		dbConnection = new BugDatabaseConnection(context); 
		Log.e("inisde", "inside constructor");
		
	}
	@Override
	protected void onPreExecute() {	
		super.onPreExecute();
	}
	@Override
	protected Void doInBackground(String... params) {
		Log.e("before ", "before exceptionfile");
		exceptionfile = Util.createExceptionFile(context);
		Log.e("after ", "after exceptionfile");
		try {
			dbConnection.openDataBase();
			writer = new FileWriter(exceptionfile);
			String select_query = "SELECT * FROM tbl_exception_details";
			
			RecordSet = dbConnection.executeQuery(select_query);
			
			int initial_count = 1;
			writer.append("[");
			if(RecordSet!=null && RecordSet.moveToNext()){
				
				for(int len = 0; len<RecordSet.getCount(); len++){
					message_of_event  = RecordSet.getString(RecordSet.getColumnIndex("message_of_event"));
					detailed_message  = RecordSet.getString(RecordSet.getColumnIndex("detailed_message"));
					String id         = RecordSet.getString(RecordSet.getColumnIndex("auto_id"));
					writer.append("{");
					
					writer.append(Util.formJson("main_event_id", RecordSet.getString(RecordSet.getColumnIndex("main_event_id"))));
					writer.append(Util.formJson("sub_event_id", RecordSet.getString(RecordSet.getColumnIndex("sub_event_id"))));
					writer.append(Util.formJson("project_id", RecordSet.getString(RecordSet.getColumnIndex("project_id"))));
					writer.append(Util.formJson("main_exception", RecordSet.getString(RecordSet.getColumnIndex("main_exception"))));
					writer.append(Util.formJson("class_of_event", RecordSet.getString(RecordSet.getColumnIndex("class_of_event"))));
					writer.append(Util.formJson("message_of_event", message_of_event.contains("\"") ? message_of_event.replace("\"", "\\\"") : message_of_event));
					writer.append(Util.formJson("project_class_name", RecordSet.getString(RecordSet.getColumnIndex("project_class_name"))));
					writer.append(Util.formJson("project_function_name", RecordSet.getString(RecordSet.getColumnIndex("project_function_name"))));
					writer.append(Util.formJson("project_line_no", RecordSet.getString(RecordSet.getColumnIndex("project_line_no"))));
					writer.append(Util.formJson("detailed_message", detailed_message.contains("\"") ? detailed_message.replace("\"", "\\\"") : detailed_message));
					writer.append(Util.formJson("device_name", RecordSet.getString(RecordSet.getColumnIndex("device_name"))));
					writer.append(Util.formJson("device_os_version", RecordSet.getString(RecordSet.getColumnIndex("device_os_version"))));
					writer.append(Util.formJson("event_date", RecordSet.getString(RecordSet.getColumnIndex("event_date"))));
					writer.append(Util.formJson("creation_date", RecordSet.getString(RecordSet.getColumnIndex("creation_date"))));
					writer.append(Util.formEndJson("error_dup_count", RecordSet.getString(RecordSet.getColumnIndex("error_dup_count"))));
					
					ContentValues values = new ContentValues();
					values.put("read", "1");
					dbConnection.updateReadInExceptionDetails(values, id);
					
					if (initial_count == RecordSet.getCount())				
						writer.append("}");
					else
						writer.append("},");
					
					initial_count++;
													
					RecordSet.moveToNext();
				}
				if(RecordSet!=null){
					RecordSet.close();
					RecordSet = null;
				}
				
			}
			
			writer.append("]");
			writer.flush();
			writer.close();
			
			if(dbConnection!=null){
				dbConnection.close();
				dbConnection = null;
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(Utilities.haveInternet(context)){
			new SendExceptionDetails(context).execute();
		}
		return null;
	}

}
