package bugtracker;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

public class SendExceptionDetails extends AsyncTask<String, String, String> {
	Activity thisActivity;
	String message = "";
	String status = "";
	String response;
	
	BugDatabaseConnection dbConnection;	
	MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	public SendExceptionDetails(Activity thisActivity){
		this.thisActivity = thisActivity;
	}
	@Override
	protected void onPreExecute() {	
		super.onPreExecute();
		reqEntity.addPart("action_url", new FileBody((Util.GetExceptionFile(thisActivity))));	
		try {
			reqEntity.addPart("device_type", new StringBody("android"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		dbConnection = new BugDatabaseConnection(thisActivity);
		dbConnection.openDataBase();
	}
	
	@Override
	protected String doInBackground(String... params) {
		response = Util.callPostAPI(thisActivity, null, reqEntity,false);	
		if(!response.equalsIgnoreCase("error")){
			try{
				JSONTokener tokener = new JSONTokener(response);
				JSONArray jsonArray = (JSONArray) tokener.nextValue();
				JSONObject responseObj = jsonArray.getJSONObject(0);
				
				if(responseObj.has("status"))
					status = responseObj.getString("status");
				Log.e("status", status);
				if(status!=null && !status.equals("") && status.equalsIgnoreCase("success")){				
					
					if(responseObj.has("message"))
						message = responseObj.getString("message");	
					    Log.e("message", message);
				}
			}catch(Exception e){
				
			}
			if(!status.equals("") && status.equals("success")){
				String delete_query = "DELETE from tbl_exception_details WHERE read=1";
				dbConnection.executeUpdate(delete_query);
				
			}else{
				if(!BugUserPreferences.getProjectId(thisActivity).equals("")){
					ContentValues values = new ContentValues();
					values.put("read", "0");
					dbConnection.updateUnReadExceptionDetails(values);
				}
			}
			
			if(dbConnection!=null){
				dbConnection.close();
				dbConnection = null;
			}
		}
		return null;
	}
	
}
