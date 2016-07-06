package bugtracker;

import java.io.IOException;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.rifluxyss.therenoking.R;

public class SettingsApi extends Thread{
		
	Activity thisActivity;
	
	String status="";
	String message="";
	String response="";
	
	Resources res;
	MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	
	BugDatabaseConnection bugDBConnect;
	
	
	public  SettingsApi(Activity thisActivity){
		this.thisActivity = thisActivity;
		res  = thisActivity.getResources();
	}
	
	public void run() {
		
		bugDBConnect = new BugDatabaseConnection(thisActivity);
		try {
			bugDBConnect.createDataBase();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String api_command = res.getString(R.string.settings_api);
		response = Util.callPostAPI(thisActivity, api_command, reqEntity,true);
		
		if(!response.equalsIgnoreCase("error")){
			try{
				JSONArray responseArr = new JSONArray(response);
				JSONObject responseObj = (JSONObject) responseArr.get(0);
				
				String debug_mode = responseObj.getString("debug_mode");
				String project_id = responseObj.getString("project_id");
				String max_exception = responseObj.getString("max_exception");
				String min_exception = responseObj.getString("min_exception");

				Log.v("debug_mode", debug_mode);
				Log.v("project_id", project_id);
				Log.v("max_exception", max_exception);
				Log.v("min_exection", min_exception);
				
				BugUserPreferences.setDebugMode(thisActivity, debug_mode);
//				BugUserPreferences.setDebugMode(thisActivity, "0");
				BugUserPreferences.setProjectId(thisActivity, project_id);
				BugUserPreferences.setMaximumException(thisActivity, max_exception);
				BugUserPreferences.setMinimumException(thisActivity, min_exception);
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	
	}
}
