package bugtracker;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.http.entity.mime.MultipartEntity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.format.Time;
import android.util.Log;

import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.network.APIClient;

public class Util {
	
	static Time startTime = new Time();
	static Time endTime = new Time();
	
	public static long loading_time = 4;
	public static long splash_time = 5;
	
	
	public static String PERFORMANCE ="Performance";
	public static String ACTIVITY ="Activity";
	public static String SERVER_RESPONSE ="Server Response";
	public static boolean is_dialog_closed =true;
	
	
	public static String className = "";
	public static String methodName = "";
	public static String lineNo = "";
	public static String mainException = "";
	public static String classOfEvent = "";
	public static String messageOfEvent = "";
	public static String projectClassName = "";
	public static String projectFunctionName = "";
	public static String projectLineNo = "";
	public static String detailedMessage = "";
	public static String deviceName = "";
	public static String deviceOsVersion = "";
	public static String eventDate = "";
	public static String creationDate = "";
	
	public static String callPostAPI (Activity currentActivity, String apiCommand, MultipartEntity entity, boolean add_namespace){

		APIClient api_client = new APIClient(currentActivity, apiCommand, add_namespace);

		int status = api_client.postValues( entity);
		String strResponseMessage = "", strError = "error";
		Log.i("*******","status == "+status);		
		if(!haveInternet(currentActivity)){
			String server_response = strError;
			strResponseMessage = server_response;
		}else if (status == APIClient.STATUS_ERROR) {
			String error_msg = api_client.getErrorMessage();
			strResponseMessage = error_msg;
		} else if (status == APIClient.STATUS_FAILED) {
			String failed_msg = api_client.getFailedMessage();
			strResponseMessage = failed_msg;
		} else if (status == APIClient.STATUS_SUCCESS) {
			String server_response = api_client.getResponse();
			strResponseMessage = server_response;
		}else if (status == APIClient.STATUS_UN_SUCCESSFUL) {
			String server_response = strError;
			strResponseMessage = server_response;
		}
		return strResponseMessage;
	}
	public static boolean haveInternet(Context thisActivity) {
		NetworkInfo info = ((ConnectivityManager) thisActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			return true;
		}
		return true;
	}
	public static String formJson(String key, String value){
		return "\"" + key +"\":\"" + value + "\",";
	}

	public static String formEndJson(String key, String value){
		return "\"" + key +"\":\"" + value + "\"";
	}

	public static File createExceptionFile(Activity thisActivity){
		File root = new File(thisActivity.getFilesDir(), "exception");
		if (!root.exists()) {
			root.mkdirs();
		}
		File sendfile = new File(root, "exception.txt");

		return sendfile;
	}

	public static File GetExceptionFile(Activity thisActivity){
		File root = new File(thisActivity.getFilesDir(), "exception/exception.txt");	
		return root;
	}
	
	public static void insertCaughtException(Exception e, Activity thisActivity){
		if (BugUserPreferences.getDebugMode(thisActivity).equals("0"))
			return;
		Activity context;
		Exception exception;
		BugDatabaseConnection connection;
		
		context = thisActivity;
		exception = e;
		
		connection = new BugDatabaseConnection(context);
		connection.openDataBase();
		
		final Writer writer = new StringWriter();
		final PrintWriter pWriter = new PrintWriter(writer);
		exception.printStackTrace(pWriter);
		String stackTrace = writer.toString();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		

		if(exception.getCause()!=null){
			Log.e("inside if", "inside if");
			Throwable ex = exception.getCause();
			mainException = exception.getClass().getSimpleName();
			creationDate = format.format(new Date());
			detailedMessage = exception.getMessage();

			classOfEvent = ex.getClass().getSimpleName();
			messageOfEvent = ex.getMessage();

			StackTraceElement[] stackTraceElement = ex.getStackTrace();

			for (int i = 0; i < stackTraceElement.length; i++) {
				Log.e("stackTraceElement[i].getClassName()===>", stackTraceElement[i].getClassName());
				Log.e("context.getClass().getSimpleName()===>", context.getClass().getSimpleName());
				if (stackTraceElement[i].getClassName().endsWith(context.getClass().getSimpleName())){
					projectClassName = stackTraceElement[i].getFileName();
					projectFunctionName = stackTraceElement[i].getMethodName();
					projectLineNo = ""+stackTraceElement[i].getLineNumber();
					break;
				}else{

				}
			}
		}else{
			Log.e("inside else", "inside else");
			classOfEvent = exception.getClass().getSimpleName();
			detailedMessage = exception.getMessage();

			if(exception.getClass().getSuperclass()!=null && !exception.getClass().getSuperclass().equals("Object")){
				mainException = exception.getClass().getSuperclass().getSimpleName();
			}		

			creationDate = format.format(new Date());

			StackTraceElement[] stackTraceElement = exception.getStackTrace();

			for (int i = 0; i < stackTraceElement.length; i++) {
				Log.e("stackTraceElement[i].getClassName()===>", stackTraceElement[i].getClassName());
				Log.e("context.getClass().getSimpleName()===>", context.getClass().getSimpleName());

				if (stackTraceElement[i].getClassName().contains(context.getClass().getSimpleName())){
					projectClassName = stackTraceElement[i].getFileName();
					projectFunctionName = stackTraceElement[i].getMethodName();
					projectLineNo = ""+stackTraceElement[i].getLineNumber();
					break;
				}else{

				}
			}

		}


		Log.e("mainException===>", mainException);
		Log.e("creationDate====>", creationDate);
		Log.e("classOfEvent====>", classOfEvent);
		if(messageOfEvent!=null){
			Log.e("messageOfEvent====>", ""+messageOfEvent);  
		}
		if(detailedMessage!=null){
			Log.e("detailedMessage====>", ""+detailedMessage);  
		}

		if(messageOfEvent == null){
			messageOfEvent = "";
		}

		if(detailedMessage ==  null){
			detailedMessage = classOfEvent;
		}


		Log.e("fileName()====>", ""+projectClassName);
		Log.e("methodName====>", ""+projectFunctionName);
		Log.e("lineNumber====>", ""+projectLineNo);


		deviceName =  Build.MANUFACTURER + android.os.Build.MODEL;  //device name  

		deviceOsVersion = ""+ Build.VERSION.RELEASE;

		Log.e("deviceName==>", ""+deviceName);
		Log.e("deviceOsVersion====>", ""+ Build.VERSION.RELEASE); // device version 

		connection.openDataBase();

		String select_tbl_exception_details_count = "Select * from tbl_exception_details";
		Cursor countRecordSet = connection.executeQuery(select_tbl_exception_details_count);

		if(countRecordSet!=null && (countRecordSet.getCount() <= Integer.parseInt(BugUserPreferences.getMaximumException(context)))){
			Cursor RecordSet = null;

			SQLiteDatabase getDatabase = connection.getWritableDatabase();

			String main_event_id  = "";
			String select_sub_event_id = "";

			String sub_event_id  = "";

			String select_main_event_id = "Select event_id from tbl_main_events where event_name = '"+mainException+"'";       
			RecordSet = connection.executeQuery(select_main_event_id);

			if(RecordSet!=null && RecordSet.moveToNext()){   
				main_event_id = RecordSet.getString(RecordSet.getColumnIndex("event_id"));
				select_sub_event_id = "Select event_id,main_event_id from tbl_sub_events where event_name = '"+classOfEvent+"'";
				Cursor RecordSet5 = connection.executeQuery(select_sub_event_id);

				if(RecordSet5!=null && RecordSet5.moveToNext()){
					sub_event_id = RecordSet5.getString(RecordSet5.getColumnIndex("event_id"));
					
					String select_exception_details_query = "SELECT * FROM tbl_exception_details WHERE sub_event_id=? AND (main_exception=? AND class_of_event=? " +
							"AND message_of_event=? AND " +
							"project_class_name=? AND project_function_name=? " +
							"AND project_line_no=?) ";
					String[] queryvalues = new String[] {sub_event_id+"", mainException, classOfEvent, messageOfEvent, 
							projectClassName, projectFunctionName, projectLineNo};

					SQLiteDatabase sqlite = connection.getReadableDatabase();
					Cursor RecordSet3 = sqlite.rawQuery(select_exception_details_query, queryvalues);
					if(RecordSet3!=null){
						if(RecordSet3.getCount() == 0){
							Log.e("inside inserted", "inside inserted");
							String insertQuery = "INSERT INTO tbl_exception_details";
							insertQuery += "(main_event_id,";
							insertQuery += "sub_event_id,";
							insertQuery += "project_id,";
							insertQuery += "main_exception,";
							insertQuery += "class_of_event,";
							insertQuery += "message_of_event,";
							insertQuery += "project_class_name,";
							insertQuery += "project_function_name,";
							insertQuery += "project_line_no,";
							insertQuery += "detailed_message,";
							insertQuery += "device_name,";
							insertQuery += "device_os_version,";
							insertQuery += "event_date,";
							insertQuery += "creation_date,";
							insertQuery += "error_dup_count";
							insertQuery += ")values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

							SQLiteStatement insertStatement = getDatabase.compileStatement(insertQuery);
							insertStatement.bindString(1, main_event_id );
							insertStatement.bindString(2, sub_event_id);
							insertStatement.bindString(3, BugUserPreferences.getProjectId(context));
							insertStatement.bindString(4, mainException);
							insertStatement.bindString(5, classOfEvent);
							if(messageOfEvent!=null)
								insertStatement.bindString(6,  messageOfEvent);
							else
								insertStatement.bindString(6,  "");
							insertStatement.bindString(7,  projectClassName);
							insertStatement.bindString(8,  projectFunctionName);
							insertStatement.bindString(9,  projectLineNo);
							if(detailedMessage!=null)
								insertStatement.bindString(10, detailedMessage);
							else
								insertStatement.bindString(10,  "");
							insertStatement.bindString(11,  deviceName);
							insertStatement.bindString(12,  deviceOsVersion);
							insertStatement.bindString(13,  creationDate);
							insertStatement.bindString(14,  "");
							insertStatement.bindString(15,  "1");
							insertStatement.executeInsert();
							insertStatement.close();
						}else{
							String select_duplicate_count = "SELECT auto_id,error_dup_count FROM tbl_exception_details WHERE sub_event_id=? AND (main_exception=? AND class_of_event=? " +
     							   "AND message_of_event=? AND " +
     							   "project_class_name=? AND project_function_name=? " +
     							   "AND project_line_no=?) ";
     					   String[] queryvaluesforduplicate = new String[] {sub_event_id, mainException, classOfEvent, messageOfEvent, 
     							   projectClassName, projectFunctionName, projectLineNo};

     					   Cursor duplicateRecordSet = sqlite.rawQuery(select_duplicate_count, queryvaluesforduplicate);
          		   
        						if(duplicateRecordSet!=null && duplicateRecordSet.moveToFirst()){

								String auto_id      = duplicateRecordSet.getString(0);
								int duplicate_count = duplicateRecordSet.getInt(1);

								ContentValues values = new ContentValues();
								values.clear();

								values.put("error_dup_count", ++duplicate_count);
								connection.updateDuplicateErrorRecordCount(values, auto_id);

								if(duplicateRecordSet!=null){
									duplicateRecordSet.close();
									duplicateRecordSet = null;
								}
							}
						}
					}
					if(RecordSet3!=null){
						RecordSet3.close();
						RecordSet3 = null;
					}
				}

				if(RecordSet5!=null){
					RecordSet5.close();
					RecordSet5 = null;
				}
			}else{
				int int_value = 0;
				String select_other_event_id = "Select event_id from tbl_main_events where event_name = '"+"Others"+"'";
				String other_event_id = "";

				Cursor RecordSet6 = connection.executeQuery(select_other_event_id);
				if(RecordSet6!=null && RecordSet6.moveToNext()){
					other_event_id = RecordSet6.getString(RecordSet6.getColumnIndex("event_id"));

					String select_last_event_id = "Select max(event_id) as event_id from tbl_sub_events";
					Cursor RecordSet7 = connection.executeQuery(select_last_event_id);

					if(RecordSet7!=null && RecordSet7.moveToNext()){
						String last_event_id = RecordSet7.getString(RecordSet7.getColumnIndex("event_id"));
						Log.e("last_event_id===>", last_event_id);
						int_value = Integer.parseInt(last_event_id);

						String select_sub_event_name = "Select event_id from tbl_sub_events where event_name= '"+classOfEvent+"'";

						Cursor recordSetSelectEventName = connection.executeQuery(select_sub_event_name);

						 if(recordSetSelectEventName!=null && recordSetSelectEventName.moveToNext()){
	        				   int_value = RecordSet7.getInt(0);
	        			   }else{
	        				   String insertQuery = "INSERT INTO tbl_sub_events";
	        				   insertQuery += "(main_event_id,";
	        				   insertQuery += "event_id,";
	        				   insertQuery += "event_name";
	        				   insertQuery += ") values(?,?,?)";

	        				   SQLiteStatement insertStatement = getDatabase.compileStatement(insertQuery);
	        				   insertStatement.bindString(1, other_event_id);
	        				   insertStatement.bindString(2, ""+  ++int_value);
	        				   insertStatement.bindString(3, classOfEvent);
	        				   insertStatement.executeInsert();
	        				   insertStatement.close();
	        			   }
						if(recordSetSelectEventName!=null){
							recordSetSelectEventName.close();
							recordSetSelectEventName = null;
						}
					}
					if(RecordSet7!=null){
						RecordSet7.close();
						RecordSet7 = null;
					}

					Log.e("sub_event_id", "sub_event_id  ===>"+sub_event_id);

					select_sub_event_id = "Select event_id,main_event_id from tbl_sub_events where event_name = '"+classOfEvent+"'";
					Cursor RecordSet8 = connection.executeQuery(select_sub_event_id);

					if(RecordSet8!=null && RecordSet8.moveToNext())
						sub_event_id = RecordSet8.getString(RecordSet8.getColumnIndex("event_id"));

					if(RecordSet8!=null){
						RecordSet8.close();
						RecordSet8 = null;
					}

					//        		   String select_exception_details_query = "SELECT * FROM tbl_exception_details WHERE sub_event_id='"+int_value+"'AND (main_exception='"+mainException+"' AND class_of_event='"+classOfEvent+"' AND message_of_event='"+messageOfEvent+"' AND project_class_name='"+projectClassName+"' AND project_function_name='"+projectFunctionName+"' AND project_line_no='"+projectLineNo+"') ";
					//        		   Cursor RecordSet3 = connection.executeQuery(select_exception_details_query);
					String select_exception_details_query = "SELECT * FROM tbl_exception_details WHERE sub_event_id=? AND (main_exception=? AND class_of_event=? " +
							"AND message_of_event=? AND " +
							"project_class_name=? AND project_function_name=? " +
							"AND project_line_no=?) ";
					String[] queryvalues = new String[] {int_value+"", mainException, classOfEvent, messageOfEvent, 
							projectClassName, projectFunctionName, projectLineNo};

					SQLiteDatabase sqlite = connection.getReadableDatabase();
					Cursor RecordSet3 = sqlite.rawQuery(select_exception_details_query, queryvalues);
					if(RecordSet3!=null){
						if(RecordSet3.getCount() == 0){
							String insertQuery = "INSERT INTO tbl_exception_details";
							insertQuery += "(main_event_id,";
							insertQuery += "sub_event_id,";
							insertQuery += "project_id,";
							insertQuery += "main_exception,";
							insertQuery += "class_of_event,";
							insertQuery += "message_of_event,";
							insertQuery += "project_class_name,";
							insertQuery += "project_function_name,";
							insertQuery += "project_line_no,";
							insertQuery += "detailed_message,";
							insertQuery += "device_name,";
							insertQuery += "device_os_version,";
							insertQuery += "event_date,";
							insertQuery += "creation_date,";
							insertQuery += "error_dup_count";
							insertQuery += ")values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

							SQLiteStatement insertStatement = getDatabase.compileStatement(insertQuery);
							insertStatement.bindString(1, other_event_id);
							insertStatement.bindString(2, ""+ int_value);
							insertStatement.bindString(3, BugUserPreferences.getProjectId(context));
							insertStatement.bindString(4, mainException);
							insertStatement.bindString(5, classOfEvent);
							if(messageOfEvent!=null)
								insertStatement.bindString(6,  messageOfEvent);
							else
								insertStatement.bindString(6,  messageOfEvent);
							insertStatement.bindString(7,  projectClassName);
							insertStatement.bindString(8,  projectFunctionName);
							insertStatement.bindString(9,  projectLineNo);
							if(detailedMessage!=null)
								insertStatement.bindString(10,  detailedMessage);
							else
								insertStatement.bindString(10,  "");
							insertStatement.bindString(11,  deviceName);
							insertStatement.bindString(12,  deviceOsVersion);
							insertStatement.bindString(13,  creationDate);
							insertStatement.bindString(14,  "");
							insertStatement.bindString(15,  "1");
							insertStatement.executeInsert();
							insertStatement.close();
						}else{
							String select_duplicate_count = "SELECT auto_id,error_dup_count FROM tbl_exception_details WHERE sub_event_id=? AND (main_exception=? AND class_of_event=? " +
									"AND message_of_event=? AND " +
									"project_class_name=? AND project_function_name=? " +
									"AND project_line_no=?) ";
							String[] queryvaluesforduplicate = new String[] {int_value+"", mainException, classOfEvent, messageOfEvent, 
									projectClassName, projectFunctionName, projectLineNo};

							Cursor duplicateRecordSet = sqlite.rawQuery(select_duplicate_count, queryvaluesforduplicate);

							if(duplicateRecordSet!=null && duplicateRecordSet.moveToFirst() ){

								String auto_id      = duplicateRecordSet.getString(0);
								int duplicate_count = duplicateRecordSet.getInt(1);

								ContentValues values = new ContentValues();
								values.clear();

								values.put("error_dup_count", ++duplicate_count);
								connection.updateDuplicateErrorRecordCount(values, auto_id);

								if(duplicateRecordSet!=null){
									duplicateRecordSet.close();
									duplicateRecordSet = null;
								}
							}
						}
					}

					if(RecordSet3!=null){
						RecordSet3.close();
						RecordSet3 = null;

					}
				}
				if(RecordSet6!=null){
					RecordSet6.close();
					RecordSet6 = null;
				}
			}

			if(RecordSet!=null){
				RecordSet.close();
				RecordSet = null;
			}
		}

		if(countRecordSet!=null){
			countRecordSet.close();
			countRecordSet = null;
		}
		
		if(connection!=null){
			connection.close();
			connection = null;
		}
		
		 int count = Util.getLogCount(thisActivity);

	       if((count >= Integer.parseInt(BugUserPreferences.getMinimumException(thisActivity)))){
	    	   new FormJsonFromExceptionDetails(thisActivity).execute();
	       }
	}
	
	 public static int getLogCount(Activity thisActivity){
	    	BugDatabaseConnection dbConnection = new BugDatabaseConnection(thisActivity);
	    	dbConnection.openDataBase();
			String select_tbl_exception_details_count = "Select * from tbl_exception_details";
			Cursor countRecordSet = dbConnection.executeQuery(select_tbl_exception_details_count);
			int count = 0;
			if(countRecordSet!=null)
				count  = countRecordSet.getCount();
			if(countRecordSet!=null){
				countRecordSet.close();
				countRecordSet = null;
			}

			if(dbConnection!=null){
				dbConnection.close();
				dbConnection = null;
			}
			
			return count;
	    }
	 
	 public static void setStartTime(){
		 startTime.setToNow();
	 }
	 public static void setEndTime(){
		 endTime.setToNow();
	 }
	 
	 public static void insertLogIntoDB(Activity thisActivity, Thread currentThread, String message, String type){
		 
		 StackTraceElement[] stackTraceElement = currentThread.getStackTrace();

			for (int i = 0; i < stackTraceElement.length; i++) {
			
				if (stackTraceElement[i].getClassName().contains(thisActivity.getClass().getSimpleName())){
					projectClassName = stackTraceElement[i].getFileName();
					projectFunctionName = stackTraceElement[i].getMethodName();
					projectLineNo = ""+stackTraceElement[i].getLineNumber();
					break;
				}
			}
			
			detailedMessage = message;
			
			BugDatabaseConnection dbConnection = new BugDatabaseConnection(thisActivity);
			dbConnection.openDataBase();

			 String strMainEventId = "";
			 String strMainEventName = "";
			 String strSubEventId = "";
		 
			 deviceName =  Build.MANUFACTURER + android.os.Build.MODEL;  //device name  
			 deviceOsVersion = ""+ Build.VERSION.RELEASE;
			 
			 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 eventDate = format.format(new Date());
				
			 String select_main_event_id = "Select event_id,event_name from tbl_main_events where event_name= '"+type+"'";
			 Cursor recordset_main_event_id = dbConnection.executeQuery(select_main_event_id);
			 
			 if(recordset_main_event_id!=null && recordset_main_event_id.moveToNext()){
				 strMainEventId = recordset_main_event_id.getString(0);
				 strMainEventName = recordset_main_event_id.getString(1);
			 }
			 
			 strSubEventId = "0";
			 
			 
			 if(recordset_main_event_id!=null){
				 recordset_main_event_id.close();
				 recordset_main_event_id = null;
			 }
			 
			 SQLiteDatabase sqliteReadable = dbConnection.getReadableDatabase();
			 SQLiteDatabase sqliteWritable = dbConnection.getWritableDatabase();
			 
			 String select_exception_details_query = "SELECT * FROM tbl_exception_details WHERE main_event_id=? AND (main_exception=? " +
						"AND message_of_event=? AND " +
						"project_class_name=? AND project_function_name=? " +
						"AND project_line_no=?) ";			 

				String[] arr_exception_details_value = new String[] {strMainEventId+"", strMainEventName, detailedMessage, 
						projectClassName, projectFunctionName, projectLineNo};
				
				
				Log.e("strMainEventId ===", "strMainEventId ==="+strMainEventId);
				Log.e("mainException ===", "strMainEventName ==="+strMainEventName);
				Log.e("projectClassName ===", "projectClassName ==="+projectClassName);
				Log.e("projectFunctionName ===", "projectFunctionName ==="+projectFunctionName);

				
				Cursor recordset_check_value_exists = sqliteReadable.rawQuery(select_exception_details_query, arr_exception_details_value);				
				
				if(recordset_check_value_exists!=null && recordset_check_value_exists.moveToFirst()){
					String auto_id      = recordset_check_value_exists.getString(recordset_check_value_exists.getColumnIndex("auto_id"));
					int duplicate_count = recordset_check_value_exists.getInt(recordset_check_value_exists.getColumnIndex("error_dup_count"));
					Log.e("duplicate_count ===", "duplicate_count ==="+duplicate_count);
					ContentValues values = new ContentValues();
					values.clear();

					values.put("error_dup_count", ++duplicate_count);
					dbConnection.updateDuplicateErrorRecordCount(values, auto_id);
					
				}else{
					Log.e("", "INSERTING DETAILS!!!");
					String insertQuery = "INSERT INTO tbl_exception_details";
					insertQuery += "(main_event_id,";
					insertQuery += "sub_event_id,";
					insertQuery += "project_id,";
					insertQuery += "main_exception,";
					insertQuery += "project_class_name,";
					insertQuery += "message_of_event,";
					insertQuery += "detailed_message,";
					insertQuery += "project_line_no,";
					insertQuery += "project_function_name,";
					insertQuery += "device_name,";
					insertQuery += "device_os_version,";
					insertQuery += "event_date,";
					insertQuery += "creation_date,";
					insertQuery += "error_dup_count";
					insertQuery += ")values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					SQLiteStatement insertStatement = sqliteWritable.compileStatement(insertQuery);
					insertStatement.bindString(1, strMainEventId);
					insertStatement.bindString(2, ""+strSubEventId);
					insertStatement.bindString(3, BugUserPreferences.getProjectId(thisActivity));
					insertStatement.bindString(4, strMainEventName);
					insertStatement.bindString(5,  projectClassName);
					insertStatement.bindString(6,  detailedMessage);
					insertStatement.bindString(7,  detailedMessage);
					insertStatement.bindString(8,  projectLineNo);
					insertStatement.bindString(9,  projectFunctionName);
					insertStatement.bindString(10,  deviceName);
					insertStatement.bindString(11,  deviceOsVersion);
					insertStatement.bindString(12,  eventDate);
					insertStatement.bindString(13,  "");
					insertStatement.bindString(14,  "1");
					insertStatement.executeInsert();
					insertStatement.close();
				}
				
				 if(recordset_check_value_exists!=null){
						recordset_check_value_exists.close();
						recordset_check_value_exists = null;
				 }
					if(dbConnection!=null){
						dbConnection.close();
						dbConnection = null;
					}					
					int count = Util.getLogCount(thisActivity);

					if((count >= Integer.parseInt(BugUserPreferences.getMinimumException(thisActivity)))){
						new FormJsonFromExceptionDetails(thisActivity).execute();
					}
			
	 }
	 
	 public static void pushPerformanceInfo(Activity thisActivity,Thread t,long loadingTime,String message){
		 if (BugUserPreferences.getDebugMode(thisActivity).equals("0"))
				return;
		 Log.e("Bug Tracker", "Pushing Performance Info");
		 long timeDifference = TimeUnit.MILLISECONDS.toSeconds(endTime.toMillis(true)-startTime.toMillis(true));
				 
		 if(timeDifference>loadingTime){
			 message = message +" takes more than "+loadingTime +" seconds";
		 }else{
			 return;
		 }
		 insertLogIntoDB(thisActivity,t,message,Util.PERFORMANCE);
	 }
	 public static void pushActivityInfo(Activity thisActivity,Thread t, String message){
		 if (BugUserPreferences.getDebugMode(thisActivity).equals("0"))
				return;
		 Log.e("Bug Tracker", "Pushing Activity Info");
		 insertLogIntoDB(thisActivity,t,message,Util.ACTIVITY);
	 }

	 public static void pushServerResponseInfo(Activity thisActivity,Thread t, String message){
		 if (BugUserPreferences.getDebugMode(thisActivity).equals("0"))
				return;
		 Log.e("Bug Tracker", "Pushing Server Response Info");
		 insertLogIntoDB(thisActivity,t,message,Util.SERVER_RESPONSE);
	 }
	 
	 public static void showalert(final Activity thisActivity){
		 		 
		 int log_count  = getLogCount(thisActivity);
			String message = "Current Log Entries : "+log_count +" \nWill be uploaded when count reaches : "
			+ BugUserPreferences.getMinimumException(thisActivity);
			try {
				AlertDialog.Builder dialog = new AlertDialog.Builder(thisActivity);
				dialog.setTitle(thisActivity.getString(R.string.error_report));
				dialog.setPositiveButton(thisActivity.getString(R.string.button_ok),
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.cancel();
						is_dialog_closed = true;
					}
				});
				dialog.setNegativeButton(thisActivity.getString(R.string.button_uploadNow), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new FormJsonFromExceptionDetails(thisActivity).execute();
						is_dialog_closed = true;
					}
				});
				dialog.setCancelable(false);
				dialog.setMessage("" + message);
				dialog.create();
				if(log_count == 0){
					dialog.show().getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
				}else{
					dialog.show();
				}
			} catch (Exception e) {
			}
		}

}
