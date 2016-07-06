package com.rifluxyss.therenoking.tasks;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ProgressBar;
import bugtracker.Util;

import com.rifluxyss.therenoking.ListProspects;
import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.beans.Prospects;
import com.rifluxyss.therenoking.generic.C;
import com.rifluxyss.therenoking.generic.TimeConversion;
import com.rifluxyss.therenoking.network.APIClient;
import com.rifluxyss.therenoking.utils.DatabaseConnection;
import com.rifluxyss.therenoking.utils.EnumHandler;
import com.rifluxyss.therenoking.utils.RenoPreferences;
import com.rifluxyss.therenoking.utils.Utilities;

public class SyncProspectsThread extends AsyncTask<String, Integer, Void> {

	Activity thisActivity;
	ProgressBar progressbar;
	Handler rHandler;
	DatabaseConnection db;   
	int run_progress = 0;
	SharedPreferences prefs;
//	long one_day = TimeConversion.ONE_DAY;//live
	long one_day = TimeConversion.ONE_MINUTE*5;//demo
//	final long ten_minutes = TimeConversion.ONE_MINUTE *10;//live
//	final long ten_minutes = 60000L;//testing
	String updateDate;
	ArrayList<String> lstDeletedArray = new ArrayList<String>();
	Boolean update = true;
	
    int version = Build.VERSION.SDK_INT;

	
	public SyncProspectsThread(Activity thisContext, ProgressBar progressbar, Handler rHandler){
		this.thisActivity = thisContext;
		this.progressbar = progressbar;
		this.rHandler = rHandler;
		db = new DatabaseConnection(thisContext);
		prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
	}

	@Override
	protected Void doInBackground(String... taskparams) {

		
		HashMap<String, String> api_params = new HashMap<String, String>();
		
		db.openDataBase();
		Cursor c = db.executeQuery("select * from tbl_prospects");
		if (c != null && c.moveToFirst()){		
//			api_params.put("device_id", Utilities.getDeviceID(thisActivity));
			api_params.put("device_id", RenoPreferences.getGCMRegisterID(thisActivity));

			Cursor timecursor = db.executeQuery("select timestamp from tbl_timestamp");
			timecursor.moveToFirst();
			Log.v("", "timestamp: "+timecursor.getString(0));
			updateDate = timecursor.getString(0);
			timecursor.close();
		}
		c.close();		
//		setProgress (25);
		Log.v("", "getDeviceID: "+RenoPreferences.getGCMRegisterID(thisActivity));
//		api_params.put("device_id","");
		api_params.put("device_id",RenoPreferences.getGCMRegisterID(thisActivity));
		api_params.put("updated_date", RenoPreferences.getTimeStamp(thisActivity));
	
    	APIClient apiclient = new APIClient(thisActivity, 
				thisActivity.getResources().getString(R.string.api_sync_prospect_datas), api_params);
		int status = apiclient.processAndFetchResponse();	
		Log.v("", "status: "+status);
//		setProgress (45);
		if (status == APIClient.STATUS_SUCCESS){
			Document doc;
			NodeList PROSPECT;
			NodeList DELETEPROSPECT;
			try {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory
						.newDocumentBuilder();
				docBuilder.isValidating();
				DataInputStream in3 = new DataInputStream(
						new ByteArrayInputStream(apiclient.getResponse().getBytes()));
				doc = docBuilder.parse(in3);
				doc.getDocumentElement().normalize();
								
				DELETEPROSPECT = doc.getElementsByTagName("DELETED_PROSPECT");
				if(lstDeletedArray.size() > 0)
					lstDeletedArray.clear();
				if (DELETEPROSPECT.item(0) != null){	
					int deleted_prospect_length = DELETEPROSPECT.getLength();
					Log.e("", "deleted_prospect_length==>>"+deleted_prospect_length);
					for (int i = 0; i < deleted_prospect_length; i++){		
						Node propsect_node = DELETEPROSPECT.item(i);
						if (propsect_node.getNodeType() == Node.ELEMENT_NODE) {
							Element prosElement = (Element) propsect_node;
							if(getTagValue("DELETE_PROSPECT_ID", prosElement) != null)
								lstDeletedArray.add(getTagValue("DELETE_PROSPECT_ID", prosElement));
						}
					}
				}
				Log.e("", "deleted prospect==>>"+lstDeletedArray);
				
				Log.e("", "last timestamp==>>"+Utilities.getNodeValue(doc, "LASTINSERTEDTIME"));
				if (Utilities.getNodeValue(doc, "LASTINSERTEDTIME")!= null && 
						!Utilities.getNodeValue(doc, "LASTINSERTEDTIME").equals("")){
					db.executeUpdate("update tbl_timestamp set timestamp = "+(Utilities.getNodeValue(doc, "LASTINSERTEDTIME"))+"");
					RenoPreferences.setTimeStamp(thisActivity, Utilities.getNodeValue(doc, "LASTINSERTEDTIME"));
				}
				//				setProgress (65);
				PROSPECT = doc.getElementsByTagName("PROSPECT");
				if (PROSPECT.item(0) != null){					
					SQLiteDatabase getDatabase = db.getWritableDatabase();					
					int prospect_length = PROSPECT.getLength();	
					Log.v("", "prospect_length: " +prospect_length);
					for (int i = 0; i < prospect_length; i++){						
						SQLiteStatement insert_statement = getDatabase.compileStatement(db.getProspectInsertQuery());						
						Node propsect_node = PROSPECT.item(i);
						if (propsect_node.getNodeType() == Node.ELEMENT_NODE) {
							
							Prospects prospects = new Prospects();							
							Element prosElement = (Element) propsect_node;							
							prospects.prospect_id = getTagValue("PROSPECT_ID", prosElement);
							if(getTagValue("PUMKA_PROSPECT_ID", prosElement)!=null){
								prospects.pumka_prospect_id = getTagValue("PUMKA_PROSPECT_ID", prosElement);
							}				
							insert_statement.bindLong(1, Integer.parseInt(prospects.prospect_id));							
							if(getTagValue("PUMKA_PROSPECT_ID", prosElement)!=null && !
									getTagValue("PUMKA_PROSPECT_ID", prosElement).equals("")){
								insert_statement.bindLong(2, Integer.parseInt(prospects.pumka_prospect_id));
							}	
							insert_statement.bindString(3, getTagValue("PROSPECT_STATUS", prosElement));
							Log.v("", "Name: "+getTagValue("NAME", prosElement));
							prospects.name = getTagValue("NAME", prosElement);
							prospects.email = getTagValue("EMAIL", prosElement);
							prospects.city = getTagValue("CITY", prosElement);
							prospects.phone_number = getTagValue("PHONE", prosElement);
							//prospects.stage = getTagValue("STAGES", prosElement);
							prospects.stage = getTagValue("CURRENT_STAGES", prosElement);
							prospects.stage = prospects.stage.equals("") ? "1" : prospects.stage;
							prospects.current_stage = getTagValue("CURRENT_STAGES", prosElement);
							prospects.prev_stage = getTagValue("PREVIOUS_STAGES", prosElement);
							
							
							
							if(prospects.current_stage == null){
								Log.e("", "current_stage null==>> ");
								prospects.current_stage = "1";
							}else if(prospects.current_stage.equals("")){
								Log.e("", "current_stage empty==>> ");
								prospects.current_stage = "1";
							}else{
								Log.e("", "current_stage ==>> "+prospects.current_stage);								
							}
							
							if(prospects.prev_stage == null){
								Log.e("", "prev stage null==>> ");
								prospects.prev_stage = "1";
							}else if(prospects.prev_stage.equals("")){
								Log.e("", "prev stage empty==>> ");
								prospects.prev_stage = "1";
							}else{
								Log.e("", "prev stage ==>> "+prospects.prev_stage);								
							}
							
							prospects.address = getTagValue("ADDRESS", prosElement);
							prospects.zipcode = getTagValue("ZIP", prosElement);
							prospects.province = getTagValue("REGION", prosElement);
							prospects.status_date = getTagValue("STATUS_DATE", prosElement);
							prospects.created_time = getTagValue("CREATEDTIME", prosElement);
							prospects.priority = getTagValue("PRIORITY", prosElement);
							prospects.min_max_budget = getTagValue("BUDGET", prosElement);
							prospects.deleted_status = getTagValue("DELETED_STATUS", prosElement);
							prospects.google_id = getTagValue("CALID", prosElement);							
							
							Log.v("", "The updated google id is ====="+prospects.google_id);
							Log.v("", "The updated google id is ====="+prosElement);

							
							Log.v(C.TAG, "************ PRINT TRACE "+i+" ******************");
							
							if (prospects.deleted_status.equals("1") || lstDeletedArray.contains(prospects.prospect_id)){
								Cursor contact_cursor = Utilities.getContactsEmail(thisActivity, prospects.email);
								if (contact_cursor != null && contact_cursor.moveToFirst()){
									Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contact_cursor.getString(1));
							        System.out.println("The uri is " + uri.toString());
							        thisActivity.getContentResolver().delete(uri, null, null);
							        Log.v(C.TAG, "Deleted contact: "+contact_cursor.getString(0)+":"+contact_cursor.getString(1));
								}
								contact_cursor.close();
								insert_statement.close();
								
								continue;
							}
							
							if(Utilities.isWorkingTime(thisActivity)){
								Log.e("", "create calendar event==>>");
//								try {
//									//C.getGroups(prefs);
//									//prospects.contact_id = C.createContact(prospects, prefs);
//									//prospects.calendar_id = C.createCalendarEvent(C.getCalendarService(prefs), ProspectDetails.STAGE1_SCHEDULE_APT, Calendar.getInstance().getTime(), prospects);
////									String tag = "create";
////									prospects.calendar_id = CalendarEventsManagement.createCalendarEvent(thisActivity, ProspectDetails.STAGE1_SCHEDULE_APT, Calendar.getInstance().getTime(), prospects, getCalendarID(), tag);
//	
//								} catch (ServiceException e) {
//									e.printStackTrace();
//								} catch (IOException e) {
//									e.printStackTrace();
//								} catch (OAuthException e) {
//									e.printStackTrace();
//								} catch (Exception e) {
//									e.printStackTrace();
//								}							
							}
															
							Log.v("", "prospects.stage: "+prospects.stage);
							Log.e("", "prospects.contact_id: "+prospects.contact_id);
							Log.e("", "prospects.calendar_id : "+prospects.calendar_id );
							
							insert_statement.bindString(4, prospects.name);
							insert_statement.bindString(5, prospects.email);
							insert_statement.bindString(6, prospects.city);
							insert_statement.bindString(7, prospects.phone_number);
							insert_statement.bindString(8, prospects.address);
							insert_statement.bindString(9, prospects.zipcode);
							insert_statement.bindString(10, prospects.province);
							//insert_statement.bindString(11, prospects.stage);
							insert_statement.bindString(11, prospects.current_stage);
							insert_statement.bindString(12, getTagValue("REFERER", prosElement));
							insert_statement.bindString(13, getTagValue("REQUESTTEXT", prosElement));
							insert_statement.bindString(14, getTagValue("STATUS_DATE", prosElement));
							insert_statement.bindString(15, getTagValue("CREATEDTIME", prosElement));

							
							if(prospects.contact_id != null)
								insert_statement.bindString(16, prospects.contact_id);
							else
								insert_statement.bindString(16, "");
							
							if(prospects.calendar_id != null)
								insert_statement.bindString(17, prospects.calendar_id);	
							else
								insert_statement.bindString(17, "");	
							
							insert_statement.bindString(18, prospects.priority);
							insert_statement.bindString(19, prospects.min_max_budget);
							insert_statement.bindString(20, "new");
							insert_statement.bindString(21, prospects.current_stage);
							insert_statement.bindString(22, prospects.prev_stage);
							insert_statement.bindString(23, prospects.google_id);
							insert_statement.executeInsert();
							insert_statement.close();	
							

							
							Log.e("", "CREATEDTIME==>>"+(getTagValue("CREATEDTIME", prosElement)));

//							if (getTagValue("CREATEDTIME", prosElement) != null)
//								db.executeUpdate("update tbl_timestamp set timestamp = "+(getTagValue("CREATEDTIME", prosElement))+"");
							
//							one_day += ten_minutes; //live							
							one_day += TimeConversion.ONE_MINUTE * 5; //demo	
//							if (i == 10)
//								break;	
							
							publishProgress(i, prospect_length);

							if (isCancelled())
								break;							
						}
					}
					
				
				}
				if(lstDeletedArray.size() > 0){
					String[] IdsArr = lstDeletedArray.toArray(new String[lstDeletedArray.size()]);
					String strSelectedUserId =  Utilities.implode(IdsArr, ",");
					String deleteQuery = "delete from tbl_prospects where prospect_id in (" + strSelectedUserId +")";
					Log.e("", "deleteQuery==>>"+deleteQuery);
					db.executeUpdate(deleteQuery);
				}
				
				
			/*	if (Utilities.getNodeValue(doc, "LASTINSERTEDTIME") != null)
					db.executeUpdate("update tbl_timestamp set timestamp ="+Utilities.getNodeValue(doc, "LASTINSERTEDTIME")+"");*/

			} catch (Exception e) {
				e.printStackTrace();
				Util.insertCaughtException(e, thisActivity);
			}
		}

	/*	if(Utilities.isWorkingTime(thisActivity)){
			String strQuery = "select * from tbl_prospects";
			Cursor recordset  = db.executeQuery(strQuery);
			if(recordset!=null && recordset.moveToNext()){
				for(int  i = 0; i < recordset.getCount(); i++){
					Prospects prospects = new Prospects();	
					prospects.prospect_id = recordset.getString(recordset.getColumnIndex("prospect_id"));
					prospects.pumka_prospect_id = recordset.getString(recordset.getColumnIndex("pumka_prospect_id"));
					prospects.name = recordset.getString(recordset.getColumnIndex("name"));
					prospects.email = recordset.getString(recordset.getColumnIndex("email"));
					prospects.city = recordset.getString(recordset.getColumnIndex("city"));
					prospects.phone_number = recordset.getString(recordset.getColumnIndex("phone_number"));
					prospects.stage = recordset.getString(recordset.getColumnIndex("stage"));
					prospects.stage = prospects.stage.equals("") ? "1" : prospects.stage;
					prospects.address = recordset.getString(recordset.getColumnIndex("address"));
					prospects.zipcode = recordset.getString(recordset.getColumnIndex("zipcode"));
					prospects.province = recordset.getString(recordset.getColumnIndex("province"));
				
					prospects.status_date = recordset.getString(recordset.getColumnIndex("status_date"));
					prospects.created_time = recordset.getString(recordset.getColumnIndex("created_time"));
					prospects.priority = recordset.getString(recordset.getColumnIndex("priority"));
					prospects.min_max_budget = recordset.getString(recordset.getColumnIndex("min_max_budget"));
					
					
					
					*//** Add Shedule Appoinment Notification **//*
					AlarmManager am = (AlarmManager) thisActivity.getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(thisActivity, RenoKingNotifications.class);
					intent.putExtra("id", Integer.parseInt(prospects.prospect_id ));
					intent.putExtra("title", "Schedule Appointment!");
					intent.putExtra("name", prospects.name);
					intent.putExtra("number", prospects.phone_number);
					intent.putExtra("stage", 1);	
					intent.putExtra("message",prospects.name +" - "+ prospects.phone_number);

					
					intent.putExtra("status", thisActivity.getString(R.string.status_schedule));
					PendingIntent pendingIntent = PendingIntent.getBroadcast(thisActivity, 
					Integer.parseInt(recordset.getString(recordset.getColumnIndex("prospect_id"))),intent, PendingIntent.FLAG_CANCEL_CURRENT);							
					am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), pendingIntent);
					
					recordset.moveToNext();
				}
			}
			if(recordset!=null)
				recordset.close();
			
		}*/
		
		db.close();

		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		Log.e("Sync Prospect", "onPostExecute");
		rHandler.sendMessage(Utilities.getMessage(EnumHandler.SYNC_PROSPECTS));
	  //Generic.PROSPECTS_LOAD = false;		
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		  Log.v("", "values" +values);
		int current = values[0];
	    int total = values[1];	  
	    int percentage = (int) (100 * (float)current / (float)total);
//	    Log.v("", "percentage: "+percentage);  
//	   	progressbar.setProgress(percentage);	    	   
	    if(ListProspects.prospects == "stage1"){
	    	 Log.v("", "percentage: stage "+percentage);  
	    }else{
	    	 Log.v("", "percentage: else "+percentage);  
	    	progressbar.setProgress(percentage);	    		
	    }
	 }



	
	private static String getTagValue(String sTag, Element eElement) {
		String str;
		NodeList nlList = null;
		Node nValue = null;
		if(eElement.getElementsByTagName(sTag).item(0)!=null){
			nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
			 nValue = (Node) nlList.item(0);
		}	
		
		if (nValue == null)
			str = "";
		else{
			if (nValue.getNodeValue() == null)
				str = "";
			else
				str = nValue.getNodeValue();
		}
		
		return str;
	}
	
//	private void setProgress (int max_value){
//		while(run_progress < max_value){
//			run_progress++;
//			publishProgress(run_progress);
//			SystemClock.sleep(20);
//		}
//	}
	
	/*public ContactEntry createContact(Prospects prospect) throws IOException, ServiceException, OAuthException {		
		ContactsService myService = new ContactsService(C.APP_NAME);	
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(C.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(C.CONSUMER_SECRET);
		oauthParameters.setOAuthToken(C.getConsumer(prefs).getToken());
		oauthParameters.setOAuthTokenSecret(C.getConsumer(prefs).getTokenSecret());
		myService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

		// Create the entry to insert.
		ContactEntry contact = new ContactEntry();
		// Set the contact's name.
		Name name = new Name();
		final String NO_YOMI = null;
		name.setFullName(new FullName(prospect.name, NO_YOMI));
//		name.setGivenName(new GivenName("Satheesh", NO_YOMI));
//		name.setFamilyName(new FamilyName("Kumar", NO_YOMI));
		contact.setName(name);
//		contact.setContent(new PlainTextConstruct("Notes"));
		
		// Set contact's e-mail addresses.
		Email primaryMail = new Email();
		primaryMail.setAddress(prospect.email);
		primaryMail.setDisplayName(prospect.name);
		primaryMail.setRel("http://schemas.google.com/g/2005#work");
		primaryMail.setPrimary(true);
		contact.addEmailAddress(primaryMail);
		
//		Email secondaryMail = new Email();
//		secondaryMail.setAddress("s.satheeshkumar@rifluxyss.com");
//		secondaryMail.setRel("http://schemas.google.com/g/2005#work");
//		secondaryMail.setPrimary(false);
//		contact.addEmailAddress(secondaryMail);
		
		// Set contact's phone numbers.
		if (prospect.phone_number != null && !prospect.phone_number.equals("")){
			PhoneNumber primaryPhoneNumber = new PhoneNumber();
			primaryPhoneNumber.setPhoneNumber(prospect.phone_number);
			primaryPhoneNumber.setRel("http://schemas.google.com/g/2005#work");
			primaryPhoneNumber.setPrimary(true);
			contact.addPhoneNumber(primaryPhoneNumber);
		}
//		PhoneNumber secondaryPhoneNumber = new PhoneNumber();
//		secondaryPhoneNumber.setPhoneNumber("(984)328-3613");
//		secondaryPhoneNumber.setRel("http://schemas.google.com/g/2005#home");
//		contact.addPhoneNumber(secondaryPhoneNumber);
		
		// Set contact's IM information.
//		Im imAddress = new Im();
//		imAddress.setAddress("satheeshkumar.rifluxyss@gmail.com");
//		imAddress.setRel("http://schemas.google.com/g/2005#home");
//		imAddress.setProtocol("http://schemas.google.com/g/2005#GOOGLE_TALK");
//		imAddress.setPrimary(true);
//		contact.addImAddress(imAddress);
		// Set contact's postal address.
		StructuredPostalAddress postalAddress = new StructuredPostalAddress();
//		postalAddress.setStreet(new Street("Guindy"));
		postalAddress.setCity(new City(prospect.city));
//		postalAddress.setRegion(new Region("TN"));
//		postalAddress.setPostcode(new PostCode("600032"));
//		postalAddress.setCountry(new Country("IN", "India"));
		postalAddress.setFormattedAddress(new FormattedAddress(prospect.city));
		postalAddress.setRel("http://schemas.google.com/g/2005#work");
		postalAddress.setPrimary(true);
		contact.addStructuredPostalAddress(postalAddress);
		// Ask the service to insert the new entry
		URL postUrl = new URL(C.POST_CONTACTS_GOOGLE);
		ContactEntry createdContact = myService.insert(postUrl, contact);
		System.out.println("Contact's ID: " + createdContact.getId());
		return createdContact;
	}
	
	private EventEntry createCalendarEvent(Prospects prospect) throws Exception {
		CalendarService service = new CalendarService(C.APP_NAME);	
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(C.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(C.CONSUMER_SECRET);
		oauthParameters.setOAuthToken(C.getConsumer(prefs).getToken());
		oauthParameters.setOAuthTokenSecret(C.getConsumer(prefs).getTokenSecret());
		service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

		// Send the request and print the response
		URL feedUrl = new URL(C.GET_CALENDAR_FROM_GOOGLE_REQUEST);
		CalendarFeed resultFeed = service.getFeed(feedUrl, CalendarFeed.class);
		System.out.println("Your calendars:");
		System.out.println();
		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
		  CalendarEntry entry = resultFeed.getEntries().get(i);
		  System.out.println("\t" + entry.getTitle().getPlainText());
		  System.out.println("\t" + entry.getId());
		}
		
		URL postUrl =
				new URL(C.POST_CALENDAR_GOOGLE);
		EventEntry myEntry = new EventEntry();

		myEntry.setTitle(new PlainTextConstruct("Make Call to appointment: "+ prospect.name));
		myEntry.setContent(new PlainTextConstruct("Make Call to appointment."));

		Person author = new Person(prospect.name, null, prospect.email);
		myEntry.getAuthors().add(author);
		
		Calendar c = Calendar.getInstance();
		c.getTimeInMillis();
		c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
		c.set(Calendar.HOUR_OF_DAY, 05);
		c.set(Calendar.MINUTE, 00);
		c.set(Calendar.SECOND, 00);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
		
//		Log.e(C.TAG, "sdf2.format(c.getTime()); "+sdf.format(c.getTime()));		
//		DateTime startTime = DateTime.parseDateTime("2013-01-02T15:00:00-08:00");
//		DateTime endTime = DateTime.parseDateTime("2013-01-03T15:00:00-08:00");		
		
		String formatted_date = sdf.format(c.getTime()).replaceAll("(\\+\\d\\d)(\\d\\d)", "$1:$2");
		DateTime startTime = DateTime.parseDateTime(formatted_date);
		DateTime endTime = DateTime.parseDateTime(formatted_date);
		
		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);

		// Send the request and receive the response:
		EventEntry insertedEntry = service.insert(postUrl, myEntry);
		Log.v("", "event id: "+insertedEntry.getId());
		
		return insertedEntry;
	}*/
	
	private int getCalendarID(){
		int cal_id = ListSelectedCalendars();
		Log.v("cal_id", "cal_id"+cal_id);
		return cal_id;	
	}
	
	private int ListSelectedCalendars() {
        int result = 0;
        String[] calId;
		if(version<14){
	        String[] projection = new String[] { "_id", "name" };
	        String selection = "selected=1";
	        String path = "calendars";
			try {
				Log.v("", "managedCursor");
		        Cursor managedCursor = Utilities.getCalendarManagedCursor(thisActivity, projection, selection, path);

		        if (managedCursor != null && managedCursor.moveToFirst()) {


		            int nameColumn = managedCursor.getColumnIndex("name");
		            int idColumn = managedCursor.getColumnIndex("_id");
		            calId = new String[managedCursor.getCount()];           
		            for (int i = 0; i < managedCursor.getCount(); i++){
		            	 String calName = managedCursor.getString(nameColumn);
		                 calId[i] = managedCursor.getString(idColumn);
		                 Log.e("", "Found Calendar '" + calName + "' (ID="
		                         + calId[i] + ")");
//		                 if (calName != null && calName.contains("syedfarakatullah.rifluxyss@gmail.com")) {
//		                     result = Integer.parseInt(calId);
//		                 }
		                 managedCursor.moveToNext();
		            }           
		            result = Integer.parseInt(calId[0]);
		        } else {
		            Log.e("", "No Calendars");
		        }
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}else{
	    	Uri uri = CalendarContract.Calendars.CONTENT_URI;
	    	String[] projection = new String[] {
	    	       CalendarContract.Calendars._ID,
	    	       CalendarContract.Calendars.ACCOUNT_NAME,
	    	       CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
	    	       CalendarContract.Calendars.NAME,
	    	       CalendarContract.Calendars.CALENDAR_COLOR
	    	};
	    	
	    	String selection = "visible=1";	    
			Cursor calendarCursor = thisActivity.managedQuery(uri, projection, selection, null, null);
	    	try {
	    		if (calendarCursor != null && calendarCursor.moveToFirst()) {
		            int nameColumn = calendarCursor.getColumnIndex("name");
		            int idColumn = calendarCursor.getColumnIndex("_id");
		            calId = new String[calendarCursor.getCount()];           
		            for (int i = 0; i < calendarCursor.getCount(); i++){
		            	 String calName = calendarCursor.getString(nameColumn);
		                 calId[i] = calendarCursor.getString(idColumn);
		                 Log.e("", "Found Calendar '" + calName + "' (ID="
		                         + calId[i] + ")");
//		                 }
		                 calendarCursor.moveToNext();
		            }           
		            result = Integer.parseInt(calId[0]);
		        } else {
		            Log.e("", "No Calendars");
		        }
			} catch (Exception e) {
				e.printStackTrace();				
			}
		}
        return result;
    }
	

}
