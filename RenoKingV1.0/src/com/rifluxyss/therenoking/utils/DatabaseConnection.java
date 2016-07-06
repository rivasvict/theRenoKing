package com.rifluxyss.therenoking.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseConnection extends SQLiteOpenHelper{
	 
	public static String DB_PATH = "/data/data/com.rifluxyss.therenoking/databases/";
//    private static String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/";
    public static String DB_NAME = "renoking-v2.8.db";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
 
    public String errorMsg;
    
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseConnection(Context context) { 
    	super(context, DB_NAME, null, 1);
    	this.myContext = context;	
    }
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{ 
    	boolean dbExist = checkDataBase();
    	SQLiteDatabase db_Read = null; 
    	if(!dbExist){
    		db_Read = this.getReadableDatabase();    		
    		try {
    			copyDataBase();
    		} catch (Exception e) {
    			e.printStackTrace();
    			throw new Error("Error creating database");
    		}
    		db_Read.close();
    	} 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){
//    	SQLiteDatabase checkDB = null;
//    	try{
    		String myPath = DB_PATH + DB_NAME;
//    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
//    		checkDB = new File(myPath).exists();
//    			
//    	}catch(SQLiteException e){ 
//    	}catch(Exception ee){
//    		Log.i("Log","Exception while checkDatabase()...");
//    	}
//    	if(checkDB != null){
//    		checkDB.close();
//    	}

    	return new File(myPath).exists();
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    void copyDataBase() throws IOException{    	
 
    	String outFileName = DB_PATH + DB_NAME;
 
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	byte[] buffer = new byte[1024];
    	int length;
    	
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
    	myInput.close();
    	
    	myOutput.flush();
    	myOutput.close();
 
    }
    
   
    void createNewDataBase(){
    	try{
        	String MY_DATABASE_NAME = DB_NAME;	//DB_PATH + 
        	SQLiteDatabase myDB = null;
        	myDB = this.myContext.openOrCreateDatabase(MY_DATABASE_NAME, 0, null);
        	openDataBase();
        	executeUpdate("CREATE TABLE user(id INT(11), name varchar(50))");
        	Log.i("LOG", "DB Created!" );
        }catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    
    public Cursor executeQuery(String sql) throws SQLException{
    	 
    	Cursor c = null;
    	try{
    		this.errorMsg = "";
    		c = myDataBase.rawQuery(sql , null);    	
    	}catch(Exception e){
    		this.errorMsg = "" + e.toString();
    		e.printStackTrace();
    	}

    	return c;
    }
    public boolean executeUpdate(String sql) throws SQLException{
    	boolean qryExecuted = false; 	
    	Log.i("Update qry = ",sql);
    	try{
    		this.errorMsg = "";
    		myDataBase.execSQL(sql);
    		qryExecuted = true;
    	}catch(Exception e){
    		this.errorMsg = "" + e.toString();
    		e.printStackTrace();
    		qryExecuted = false;
    	}
    	return qryExecuted;
    }
    @Override
	public synchronized void close() {
 	    if(myDataBase != null)
		    myDataBase.close();
	    super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
	
	public String getProspectInsertQuery(){
		String insert_prospects  = "INSERT INTO tbl_prospects";
		insert_prospects += "(prospect_id";
		insert_prospects += ",pumka_prospect_id";
		insert_prospects += ",prospect_status";
		insert_prospects += ",name";
		insert_prospects += ",email";
		insert_prospects += ",city";
		insert_prospects += ",phone_number";
		insert_prospects += ",address";
		insert_prospects += ",zipcode";
		insert_prospects += ",province";
		insert_prospects += ",stage";
		insert_prospects += ",referer";
		insert_prospects += ",details";
		insert_prospects += ",status_date";
		insert_prospects += ",created_time";
		insert_prospects += ",contact_id";
		insert_prospects += ",calendar_id";
		insert_prospects += ",priority";
		insert_prospects += ",min_max_budget";
		insert_prospects += ",status_new";
		insert_prospects += ",current_stage";
		insert_prospects += ",prev_stage";
		insert_prospects += ",google_id";
		return insert_prospects += ")values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}
	
	public String getProspectUpdateQuery(String prospect_id){
		String update_prospects  = "UPDATE tbl_prospects SET";
		update_prospects += " name = ?";
		update_prospects += ",email = ?";
		update_prospects += ",city = ?";
		update_prospects += ",phone_number = ?";
		update_prospects += ",address = ?";
		update_prospects += ",zipcode = ?";
		update_prospects += ",province = ?";
		update_prospects += ",referer = ?";
		update_prospects += ",details = ?";
		update_prospects += ",status_date = ?";
		update_prospects += ",created_time = ?";
		update_prospects += ",priority = ?";
		update_prospects += ",min_max_budget = ?";
		return update_prospects += " where prospect_id = " + prospect_id;
	}
	
	public String getIDUpdateQuery(String prospect_id){
		String update_prospects  = "UPDATE tbl_prospects SET";
		update_prospects += " contact_id = ?";
		update_prospects += ",calendar_id = ?";
		return update_prospects += " where prospect_id = " + prospect_id;
	}
	
	public Calendar getReminderDate(int STAGE, String prospect_id){
		
		Calendar cal = Calendar.getInstance();
		openDataBase();
		
		String query = "";
		Cursor remind;
		try {
		if (STAGE == 1 || STAGE == 2)
			query = "select reminder_datetime from tbl_schedule where request_id = "+prospect_id 
			+" and (status = 'schedule' or status = 'reschedule' or status = 'followup')";
		else if (STAGE == 3)
			query = "select reminder_datetime from tbl_schedule where request_id = "+prospect_id 
			+" and status = 'estimate'";
		else if (STAGE == 4 || STAGE == 5)
			query = "select reminder_datetime from tbl_schedule where request_id = "+prospect_id 
			+" and status = 'project_start'";
		else if (STAGE == 6)
			query = "select reminder_datetime from tbl_schedule where request_id = "+prospect_id 
			+" and status = 'project_start'";

		Log.e("", "getReminderDate query: "+query);

		remind = executeQuery(query);
		String reminder = "";
		if (remind != null && remind.moveToNext()){
			reminder = remind.getString(0);
			Log.e("", "reminder : "+reminder);
		}
		
		Log.e("STAGE--", "STAGE--->"+STAGE);
		
		
		if (remind != null) 
			remind.close();
		
		close();
		
		if(reminder != null && !reminder.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm aa");		
			Log.e("", "date : "+sdf.parse(reminder.toString()).toString());
			cal.setTime(sdf.parse(reminder.toString()));
		}
		
		} catch (Exception e) {
			e.printStackTrace();
			cal = Calendar.getInstance();
		}

		return cal;	
	}
	
	public void updateReadInExceptionDetails(ContentValues values, String read_value) {
		myDataBase.update("tbl_exception_details", values, null,	null) ;
	}
			
	public void updateUnReadExceptionDetails(ContentValues values) {
		String where="read=1";
		myDataBase.update("tbl_exception_details", values, where, null) ;
	}
	public void updateDuplicateErrorRecordCount(ContentValues values, String auto_id) {
		myDataBase.update("tbl_exception_details", values, "auto_id="+auto_id, null) ;
	}
}
