package bugtracker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.util.Log;

public class ExceptionReporter extends Activity{
	String mainException = "";
	String classOfEvent = "";
	String messageOfEvent = "";
	String projectClassName = "";
	String projectFunctionName = "";
	String projectLineNo = "";
	String detailedMessage = "";
	String deviceName = "";
	String deviceOsVersion = "";
	String eventDate = "";
	String creationDate = "";
	
	BugDatabaseConnection connection;
	
	private static final String TAG = ExceptionReporter.class.getSimpleName();
	
	/**
	 * Registers this context and returns an error handler object
	 * to be able to manually report errors.
	 * 
	 * @param context The context
	 * @return The error handler which can be used to manually report errors
	 */
	
	public static ExceptionReporter register(Activity context) {
		if (BugUserPreferences.getDebugMode(context).equals("0"))
			return null;
		
		UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
		if (handler instanceof Handler) {
			Handler errHandler = (Handler) handler;
			errHandler.errorHandler.setContext(context);
			return errHandler.errorHandler;
		} else {
			ExceptionReporter errHandler = new ExceptionReporter(handler, context);
			Thread.setDefaultUncaughtExceptionHandler(errHandler.handler);
			return errHandler;
		}
	}

	private void setContext(Activity context) {		
		this.context = context;
		connection = new BugDatabaseConnection(context);
	}

	private Activity context;
	private Handler handler;
	private ExceptionReporter(UncaughtExceptionHandler defaultHandler, Activity context) {
		this.handler = new Handler(defaultHandler);
		this.setContext(context);
	}

	
	private class Handler implements UncaughtExceptionHandler {

		private UncaughtExceptionHandler subject;
		private ExceptionReporter errorHandler;

		private Handler(UncaughtExceptionHandler subject) {
			this.subject = subject;
			this.errorHandler = ExceptionReporter.this;
		}

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			try {
				reportException(thread, ex, null, false);
			} catch (Exception e) {
				Log.e(TAG, "Error while reporting exception", e);
			}
			subject.uncaughtException(thread, ex);
		}

	}

	/**
	 * Sends an error report.
	 * 
	 * @param thread The thread where the exception occurred (e.g. {@link java.lang.Thread#currentThread()})
	 * @param ex The exception
	 */
	public void reportException(Thread thread, Throwable ex) {
		reportException(thread, ex, null, true);
	}

	/**
	 * Sends an error report with an extra message.
	 * 
	 * @param thread The thread where the exception occurred (e.g. {@link java.lang.Thread#currentThread()})
	 * @param ex The exception
	 */
	public void reportException(Thread thread, Throwable exception, String extraMessage) {
		reportException(thread, exception, extraMessage, true);
	}
	
	private void reportException(Thread thread, Throwable exception, String extraMessage, boolean manual) {
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
		
		
		Log.e("thread.getName()===>", thread.getName());
		Log.e("mainException===>", mainException);
		Log.e("creationDate====>", creationDate);
		Log.e("classOfEvent====>", classOfEvent);
		
		if(messageOfEvent!=null){
			Log.e("messageOfEvent====>", ""+messageOfEvent);  
		}
		
		if(messageOfEvent == null){
			messageOfEvent = "";
		}
		
		if(detailedMessage ==  null){
			detailedMessage = classOfEvent;
		}
		
		if(detailedMessage!=null){
			Log.e("detailedMessage====>", ""+detailedMessage);  
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
           String select_main_event_id = "Select event_id from tbl_main_events where event_name = '"+mainException+"'";
           
           RecordSet = connection.executeQuery(select_main_event_id);
           
           String select_sub_event_id = "Select event_id,main_event_id from tbl_sub_events where event_name = '"+classOfEvent+"'";
           String main_event_id  = "";
           String sub_event_id  = "";
          
           if(RecordSet!=null && RecordSet.moveToNext()){   
        	   main_event_id = RecordSet.getString(RecordSet.getColumnIndex("event_id"));
        	   Log.e("main_event_id=======>", sub_event_id);

        	   Cursor RecordSet5 = connection.executeQuery(select_sub_event_id);
        	   
        	   if(RecordSet5!=null && RecordSet5.moveToNext()){
        		   sub_event_id = RecordSet5.getString(RecordSet5.getColumnIndex("event_id"));
            	   Log.e("sub_event_id=======>", sub_event_id);
            	   
            	   String check_id_exists = "Select main_event_id from tbl_sub_events where main_event_id = "+main_event_id+" And event_id = "+sub_event_id+"";
            	   
            	   Cursor RecordSet2 = null;
            	   RecordSet2 = connection.executeQuery(check_id_exists);
            	   if(RecordSet2!=null && RecordSet2.moveToNext()){
            		   
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
            	   if(RecordSet2!=null){
    					RecordSet2.close();
    					RecordSet2 = null;
    				}
    				
        	   }
        	   if(RecordSet5!=null){
    				RecordSet5.close();
    				RecordSet5 = null;
    			}
           }else{
        	   int int_value = 0;
        	   String select_other_event_id = "Select event_id from tbl_main_events where event_name = '"+"Others"+"'";
        	   
        	   Cursor RecordSet6 = connection.executeQuery(select_other_event_id);
        	   if(RecordSet6!=null && RecordSet6.moveToNext()){
        		   String other_event_id = RecordSet6.getString(RecordSet6.getColumnIndex("event_id"));
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
        				   insertQuery += ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
        			   }
        				   else{
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
      
       context.runOnUiThread(new Runnable() {
		
		@Override
		public void run() {
			int count = Util.getLogCount(context);

			if((count >= Integer.parseInt(BugUserPreferences.getMinimumException(context)))){
				Log.e("inside form exception", "inside form exception");
				new FormJsonFromExceptionDetails(context).execute();
			}else{
				Log.e("outside form exception", "outside form exception");
			}

		}
	});
       
      
      
	}
	
	
}
