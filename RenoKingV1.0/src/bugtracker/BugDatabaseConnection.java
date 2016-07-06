package bugtracker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BugDatabaseConnection extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.rifluxyss.therenoking/databases/";

	private static String DB_NAME = "exception_v.1.2.mp3";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	public String errorMsg;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public BugDatabaseConnection(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		SQLiteDatabase db_Read = null;
		if (!dbExist) {
			db_Read = this.getReadableDatabase();
			db_Read.close();
			try {
				copyDataBase();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Error("Error creating database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	public boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {
		} catch (Exception ee) {
			Log.i("Log", "Exception while checkDatabase()...");
		}
		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	void copyDataBase() throws IOException {

		String outFileName = DB_PATH + DB_NAME;

		OutputStream myOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int length;

		InputStream myInput = myContext.getAssets().open(DB_NAME);
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myInput.close();

		myOutput.flush();
		myOutput.close();

	}

	void createNewDataBase() {
		try {
			String MY_DATABASE_NAME = DB_NAME; // DB_PATH +
			SQLiteDatabase myDB = null;
			myDB = this.myContext.openOrCreateDatabase(MY_DATABASE_NAME, 0,
					null);
			openDataBase();
			executeUpdate("CREATE TABLE user(id INT(11), name varchar(50))");
			Log.i("LOG", "DB Created!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	public Cursor executeQuery(String sql) throws SQLException {

		Cursor c = null;
		try {
			this.errorMsg = "";
			c = myDataBase.rawQuery(sql, null);
		} catch (Exception e) {
			this.errorMsg = "" + e.toString();
			e.printStackTrace();
		}

		return c;
	}

	public boolean executeUpdate(String sql) throws SQLException {
		boolean qryExecuted = false;
		Log.i("Update qry = ", sql);
		try {
			this.errorMsg = "";
			myDataBase.execSQL(sql);
			qryExecuted = true;
		} catch (Exception e) {
			this.errorMsg = "" + e.toString();
			e.printStackTrace();
			qryExecuted = false;
		}
		return qryExecuted;
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	

	// insert user table
	
	public void insertUserDetails(ContentValues values) {
		myDataBase.insert("tbl_users", null, values);
	}

	public void updateUserDetails(ContentValues values, String userid) {
		myDataBase.update("tbl_users", values, "user_id=" + userid,	null) ;
	}

	
	public boolean checkuserId(int idvalue){
		boolean user_avail = false;
		Cursor c = null;
		String where="user_id="+idvalue;
		c = myDataBase.query("tbl_users", null, where, null, null, null, null);		
		user_avail = c!= null && c.moveToNext() && c.getCount()>0;		
		c.close();

		return user_avail;  
	}
	
	public int getLoginStatusValue(int userId){
		Cursor c = null;
		int status = 0;
		String[] columns = {"status"};
		
		String where = "user_id="+userId;

		c = myDataBase.query("tbl_users", columns, where, null, null, null, null);	

		if (c!= null && c.moveToFirst() && c.getCount()>0){ 
			status = c.getInt(c.getColumnIndex("status"));
		}

		Log.v("", "user_id ==>"+status);
		c.close();
		return status; 
	}
	
	public String getUserTypeValue(int userId){
		Cursor c = null;
		String user_type = "";
		String[] columns = {"role"};
		
		String where = "user_id="+userId;

		c = myDataBase.query("tbl_users", columns, where, null, null, null, null);	

		if (c!= null && c.moveToFirst() && c.getCount()>0){ 
			user_type = c.getString(c.getColumnIndex("role"));
		}
		Log.v("", "user_type==>"+user_type);
		c.close();
		return user_type; 
	}
	
	public void updateUserStatus(ContentValues loginvalue, String userid){
		boolean y = myDataBase.update("tbl_users", loginvalue, "user_id="+ userid , null) > 0;
		Log.v("", "status updated: " +y);
	}
	
	// insert business table
	public void insertBusinessDetails(ContentValues values) {
		myDataBase.insert("tbl_business", null, values);
	}

	public void updateBusinessDetails(ContentValues values, String userid) {
		myDataBase.update("tbl_business", values, "business_id=" + userid,	null) ;
	}

	
	public boolean checkBusinessId(int idvalue){
		boolean user_avail = false;
		Cursor c = null;
		String where="business_id="+idvalue;
		c = myDataBase.query("tbl_business", null, where, null, null, null, null);		
		user_avail = c!= null && c.moveToNext() && c.getCount()>0;		
		c.close();

		return user_avail;  
	}
	
	
	// claims table
	
	
	public void insertClaimsDetails(ContentValues values) {
		myDataBase.insert("tbl_claim", null, values);
	}
	
	public void updateClaimsDetails(ContentValues values, String userid) {
		myDataBase.update("tbl_claim", values, "claim_id=" + userid,	null) ;
	}

	public boolean checkClaimId(int idvalue){
		boolean user_avail = false;
		Cursor c = null;
		String where="claim_id="+idvalue;
		c = myDataBase.query("tbl_claim", null, where, null, null, null, null);		
		user_avail = c!= null && c.moveToNext() && c.getCount()>0;		
		c.close();

		return user_avail;  
	}
	
	// product table
	
	public void insertProductDetails(ContentValues values) {
		myDataBase.insert("tbl_product", null, values);
	}
	
	public void updateProductDetails(ContentValues values, String product_id) {
		myDataBase.update("tbl_product", values, "product_id=" + product_id,	null) ;
	}
	
	public boolean checkProductId(int idvalue){
		boolean user_avail = false;
		Cursor c = null;
		String where="product_id="+idvalue;
		c = myDataBase.query("tbl_product", null, where, null, null, null, null);		
		user_avail = c!= null && c.moveToNext() && c.getCount()>0;		
		c.close();

		return user_avail;  
	}
	
	public void updateRedeemStatus(ContentValues loginvalue, String userid){
		boolean y = myDataBase.update("tbl_claim", loginvalue, "user_id="+ userid , null) > 0;
		Log.v("", "redeem updated: " +y);
	}
	
	
	// category table

	public void insertCategoryDetails(ContentValues values) {
		myDataBase.insert("tbl_category", null, values);
	}

	public void updateCategoryDetails(ContentValues values, String category_id) {
		myDataBase.update("tbl_category", values, "category_id=" + category_id,	null) ;
	}

	public boolean checkCategoryId(int idvalue){
		boolean user_avail = false;
		Cursor c = null;
		String where="category_id="+idvalue;
		c = myDataBase.query("tbl_category", null, where, null, null, null, null);		
		user_avail = c!= null && c.moveToNext() && c.getCount()>0;		
		c.close();
		return user_avail;  
	}
	
	// sub category table

		public void insertSubCategoryDetails(ContentValues values) {
			myDataBase.insert("tbl_sub_category", null, values);
		}

		public void updateSubCategoryDetails(ContentValues values, String category_id) {
			myDataBase.update("tbl_sub_category", values, "sub_category_id=" + category_id,	null) ;
		}

		public boolean checkSubCategoryId(int idvalue){
			boolean user_avail = false;
			Cursor c = null;
			String where="sub_category_id="+idvalue;
			c = myDataBase.query("tbl_sub_category", null, where, null, null, null, null);		
			user_avail = c!= null && c.moveToNext() && c.getCount()>0;		
			c.close();
			return user_avail;  
		}

		public void insertMainEvents(ContentValues values){
			myDataBase.insert("tbl_main_events", null, values);
		}
		public void insertSubEvents(ContentValues values){
			myDataBase.insert("tbl_sub_events", null, values);
		}
		
		public void deleteProducts(){
			myDataBase.delete("tbl_product", null, null);
		}
		public void deleteBusinesses(){
			myDataBase.delete("tbl_business", null, null);
		}
		
		
		
		
		// exception details
		
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
