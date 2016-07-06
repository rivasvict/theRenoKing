package com.rifluxyss.therenoking.generic;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.City;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.EventEntry;
import com.google.gdata.data.extensions.FormattedAddress;
import com.google.gdata.data.extensions.FullName;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.PostCode;
import com.google.gdata.data.extensions.Region;
import com.google.gdata.data.extensions.Street;
import com.google.gdata.data.extensions.StructuredPostalAddress;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.PreconditionFailedException;
import com.google.gdata.util.ServiceException;
import com.rifluxyss.therenoking.ProspectDetails;
import com.rifluxyss.therenoking.SplashActivity;
import com.rifluxyss.therenoking.beans.Prospects;


public class C {

	public static final String TAG = "TheRenoKing";

	//	public static final String CONSUMER_KEY 	= "anonymous";
	//	public static final String CONSUMER_SECRET 	= "anonymous";

	public static final String CONSUMER_KEY 	= "www.therenoking.ca";
	public static final String CONSUMER_SECRET = "059i2ccF1NbsD7dAzj3De1cp";
	
	

	public static final String SCOPE 			= "https://www.google.com/m8/feeds/ http://www.google.com/calendar/feeds/ https://apps-apis.google.com/a/feeds/emailsettings/2.0/";
	public static final String REQUEST_URL 	= "https://www.google.com/accounts/OAuthGetRequestToken";
	public static final String ACCESS_URL 	= "https://www.google.com/accounts/OAuthGetAccessToken";  
	public static final String AUTHORIZE_URL 	= "https://www.google.com/accounts/OAuthAuthorizeToken";

	public static final String GET_CALENDAR_FROM_GOOGLE_REQUEST = "https://www.google.com/calendar/feeds/default/allcalendars/full?alt=json";
	public static final String GET_CONTACTS_FROM_GOOGLE_REQUEST = "https://www.google.com/m8/feeds/contacts/default/full?alt=json";
	public static final String GET_GROUPS_FROM_GOOGLE_REQUEST = "https://www.google.com/m8/feeds/groups/default/full";

	public static final String POST_CONTACTS_GOOGLE = "https://www.google.com/m8/feeds/contacts/default/full";

	public static final String POST_CALENDAR_GOOGLE = "https://www.google.com/calendar/feeds/default/private/full";
	public static final String CALENDAR_EVENT_URL = "http://www.google.com/calendar/feeds/default/events";

	public static final String ENCODING 		= "UTF-8";

	public static final String	OAUTH_CALLBACK_SCHEME	= "oauth-renoking";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	public static final String	APP_NAME                = "The+Reno+King";

	public static final String	GROUP_ID                = "group_id";
	public static final String	GROUP_NAME              = "System Group: My Contacts";	

	

	public static void clearCredentials(Activity thisActivity) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(thisActivity);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
	}

	public static boolean isOAuthSuccessful(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, null);
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, null);
//		if (token != null && secret != null)
//			return true;
//		else 
//			return false;
		
		if (SplashActivity.person != null)
			return true;
	    else 
	    	return false;
	}


	public static OAuthConsumer getConsumer(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(C.CONSUMER_KEY, C.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		return consumer;
	}

	public static ContactsService getContactService(SharedPreferences prefs){
		ContactsService contactService = new ContactsService(C.APP_NAME);	
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(C.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(C.CONSUMER_SECRET);
		oauthParameters.setOAuthToken(C.getConsumer(prefs).getToken());
		oauthParameters.setOAuthTokenSecret(C.getConsumer(prefs).getTokenSecret());
		try {
			contactService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
		} catch (OAuthException e) {			
			e.printStackTrace();
		}
		return contactService;
	}

	public static ContactsService getGroups(SharedPreferences prefs)
			throws ServiceException, IOException, OAuthException {
		String group_id = null; 
		ContactsService contactService = getContactService(prefs);

		// Request the feed
		URL feedUrl = new URL(C.GET_GROUPS_FROM_GOOGLE_REQUEST);
		ContactGroupFeed resultFeed = contactService.getFeed(feedUrl, ContactGroupFeed.class);

		for (ContactGroupEntry groupEntry : resultFeed.getEntries()) {
			System.out.println("Atom Id: " + groupEntry.getId());
			System.out.println("Group Name: " + groupEntry.getTitle().getPlainText());
			System.out.println("Last Updated: " + groupEntry.getUpdated());

			if (groupEntry.getTitle().getPlainText().equals(GROUP_NAME)){
				group_id = groupEntry.getId();
				final Editor edit = prefs.edit();
				edit.putString(GROUP_ID, group_id);
				edit.commit();
				break;
			}	    
		}

		return contactService;
	}

	public static String createContact(Prospects prospect, SharedPreferences prefs) throws IOException, ServiceException, OAuthException {	
		//		ContactsService myService = new ContactsService(C.APP_NAME);	
		//		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		//		oauthParameters.setOAuthConsumerKey(C.CONSUMER_KEY);
		//		oauthParameters.setOAuthConsumerSecret(C.CONSUMER_SECRET);
		//		oauthParameters.setOAuthToken(C.getConsumer(prefs).getToken());
		//		oauthParameters.setOAuthTokenSecret(C.getConsumer(prefs).getTokenSecret());
		//		myService.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
		ContactEntry createdContact = null;
		try {
			// Create the entry to insert.
			ContactEntry contact = new ContactEntry();
			// Set the contact's name.
			Name name = new Name();
			final String NO_YOMI = null;
			name.setFullName(new FullName(prospect.name, NO_YOMI));
			//			name.setGivenName(new GivenName("Satheesh", NO_YOMI));
			//			name.setFamilyName(new FamilyName("Kumar", NO_YOMI));
			contact.setName(name);
			//			contact.setContent(new PlainTextConstruct("Notes"));

			// Set contact's e-mail addresses.
			Email primaryMail = new Email();
			primaryMail.setAddress(prospect.email);
			primaryMail.setDisplayName(prospect.name);
			primaryMail.setRel("http://schemas.google.com/g/2005#work");
			primaryMail.setPrimary(true);
			contact.addEmailAddress(primaryMail);

			//			Email secondaryMail = new Email();
			//			secondaryMail.setAddress("s.satheeshkumar@rifluxyss.com");
			//			secondaryMail.setRel("http://schemas.google.com/g/2005#work");
			//			secondaryMail.setPrimary(false);
			//			contact.addEmailAddress(secondaryMail);

			// Set contact's phone numbers.
			if (prospect.phone_number != null && !prospect.phone_number.equals("")){
				PhoneNumber primaryPhoneNumber = new PhoneNumber();
				primaryPhoneNumber.setPhoneNumber(prospect.phone_number);
				primaryPhoneNumber.setRel("http://schemas.google.com/g/2005#work");
				primaryPhoneNumber.setPrimary(true);
				contact.addPhoneNumber(primaryPhoneNumber);
			}

			contact.setContent(new PlainTextConstruct(prospect.details));

			//			PhoneNumber secondaryPhoneNumber = new PhoneNumber();
			//			secondaryPhoneNumber.setPhoneNumber("(984)328-3613");
			//			secondaryPhoneNumber.setRel("http://schemas.google.com/g/2005#home");
			//			contact.addPhoneNumber(secondaryPhoneNumber);

			// Set contact's IM information.
			//			Im imAddress = new Im();
			//			imAddress.setAddress("satheeshkumar.rifluxyss@gmail.com");
			//			imAddress.setRel("http://schemas.google.com/g/2005#home");
			//			imAddress.setProtocol("http://schemas.google.com/g/2005#GOOGLE_TALK");
			//			imAddress.setPrimary(true);
			//			contact.addImAddress(imAddress);
			// Set contact's postal address.
			StructuredPostalAddress postalAddress = new StructuredPostalAddress();
			System.out.println("prospect.address: " + prospect.address);
			if (prospect.address != null && !prospect.address.equals(""))
				postalAddress.setStreet(new Street(prospect.address));
			postalAddress.setCity(new City(prospect.city));		
			if (prospect.zipcode != null && !prospect.zipcode.equals(""))
				postalAddress.setPostcode(new PostCode(prospect.zipcode));
			if (prospect.province != null && !prospect.province.equals(""))
				postalAddress.setRegion(new Region(prospect.province));
			//			postalAddress.setCountry(new Country("IN", "India"));
			postalAddress.setFormattedAddress(new FormattedAddress(prospect.city));
			postalAddress.setRel("http://schemas.google.com/g/2005#work");
			postalAddress.setPrimary(true);
			contact.addStructuredPostalAddress(postalAddress);

			if (prefs.getString(GROUP_ID, null) != null){
				GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo();        
				groupMembershipInfo.setDeleted(false);
				groupMembershipInfo.setHref(URLDecoder.decode(prefs.getString(GROUP_ID, null), "UTF-8"));
				contact.addGroupMembershipInfo(groupMembershipInfo);
			}

			// Ask the service to insert the new entry
			URL postUrl = new URL(C.POST_CONTACTS_GOOGLE);
			createdContact = getContactService(prefs).insert(postUrl, contact);
			System.out.println("Contact's ID: " + createdContact.getId());
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (createdContact == null)
			return "";

		return createdContact.getId();
	}

	public static ContactEntry updateContactName(
			ContactsService myService, URL contactURL, Prospects prospect)
					throws ServiceException, IOException {
		// First retrieve the contact to updated.
		ContactEntry entryToUpdate = myService.getEntry(contactURL, ContactEntry.class);
		entryToUpdate.getName().getFullName().setValue(prospect.name);
		entryToUpdate.getEmailAddresses().clear();

		Email primaryMail = new Email();
		primaryMail.setAddress(prospect.email);
		primaryMail.setDisplayName(prospect.name);
		primaryMail.setRel("http://schemas.google.com/g/2005#work");
		primaryMail.setPrimary(true);		
		entryToUpdate.getEmailAddresses().add(primaryMail);		

		if (prospect.phone_number != null && !prospect.phone_number.equals("")){
			PhoneNumber primaryPhoneNumber = new PhoneNumber();
			primaryPhoneNumber.setPhoneNumber(prospect.phone_number);
			primaryPhoneNumber.setRel("http://schemas.google.com/g/2005#work");
			primaryPhoneNumber.setPrimary(true);
			entryToUpdate.getPhoneNumbers().clear();
			entryToUpdate.getPhoneNumbers().add(primaryPhoneNumber);
		}

		StructuredPostalAddress postalAddress = new StructuredPostalAddress();
		if (prospect.address != null && !prospect.address.equals(""))
			postalAddress.setStreet(new Street(prospect.address));
		postalAddress.setCity(new City(prospect.city));
		postalAddress.setFormattedAddress(new FormattedAddress(prospect.city));
		postalAddress.setRel("http://schemas.google.com/g/2005#work");
		postalAddress.setPrimary(true);		
		entryToUpdate.getStructuredPostalAddresses().clear();
		entryToUpdate.getStructuredPostalAddresses().add(postalAddress);

		entryToUpdate.setContent(new PlainTextConstruct(prospect.details));

		URL editUrl = new URL(entryToUpdate.getEditLink().getHref());
		try {
			ContactEntry contactEntry = myService.update(editUrl, entryToUpdate);
			System.out.println("Updated: " + contactEntry.getUpdated().toString());
			return contactEntry;
		} catch (PreconditionFailedException e) {
			// Etags mismatch: handle the exception.
		}
		return null;
	}

	public static String createCalendarEvent1(CalendarService service, int STAGE_CATEGORY, 
			Date date, Prospects prospect) throws Exception {
		/*CalendarService service = new CalendarService(C.APP_NAME);	
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(C.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(C.CONSUMER_SECRET);boolean scheduleConflict = false;
		oauthParameters.setOAuthToken(C.getConsumer(prefs).getToken());
		oauthParameters.setOAuthTokenSecret(C.getConsumer(prefs).getTokenSecret());
		service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());*/

		// Send the request and print the response
		/*URL feedUrl = new URL(C.GET_CALENDAR_FROM_GOOGLE_REQUEST);
		CalendarFeed resultFeed = service.getFeed(feedUrl, CalendarFeed.class);
		System.out.println("Your calendars:");
		System.out.println();
		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
		  CalendarEntry entry = resultFeed.getEntries().get(i);
		  System.out.println("\t" + entry.getTitle().getPlainText());
		  System.out.println("\t" + entry.getId());
		}*/

		String formatted_date = getFormattedDate(date);
		String formatted__end_date = getFormattedEndDate(date);
		DateTime startTime = DateTime.parseDateTime(formatted_date);
		DateTime endTime = DateTime.parseDateTime(formatted__end_date);

		URL postUrl = new URL(C.POST_CALENDAR_GOOGLE);

		CalendarQuery myQuery = new CalendarQuery(postUrl);
		myQuery.setMinimumStartTime(startTime);
//		myQuery.setMaximumStartTime(startTime);
		myQuery.setMaxResults(5);

		// Send the request and receive the response:
		CalendarEventFeed resultFeed = service.query(myQuery, CalendarEventFeed.class);
		
		int i = resultFeed.getEntries().size();
		Log.v("", "conflict size: " + i);
//		for (int j = 0; j < i; j++) {
//			CalendarEventEntry entry = resultFeed.getEntries().get(j);
//			Log.v("", "entry: " + entry.getTitle().getPlainText());
//		}
//		if (i > 0) return "";

		EventEntry myEntry = new EventEntry();

//		myEntry.setTitle(new PlainTextConstruct("New Prospect - Call to Book Appointment: "+ prospect.name + "-" + prospect.phone_number));
		if (STAGE_CATEGORY == ProspectDetails.STAGE5_FINISH_DATE)
			myEntry.setTitle(new PlainTextConstruct("Job Completion Date: "+ prospect.name+ "-" + prospect.phone_number));
		else if (STAGE_CATEGORY == ProspectDetails.STAGE2_SCHEDULE_APT)
			myEntry.setTitle(new PlainTextConstruct("Estimate Appointment: "+ prospect.name + "-"+prospect.phone_number));
		else if (STAGE_CATEGORY == ProspectDetails.STAGE2_CONFIRM_APT)
			myEntry.setTitle(new PlainTextConstruct("Estimate Appointment: "+ prospect.name + "-"+prospect.phone_number));
		else if (STAGE_CATEGORY == ProspectDetails.STAGE1_LEFTMSG_NOANSWER)
			myEntry.setTitle(new PlainTextConstruct("Schedule Appointment: "+ prospect.name + "-" + prospect.phone_number));
		else if (STAGE_CATEGORY == ProspectDetails.STAGE2_FOLLOWUP_APT)
			myEntry.setTitle(new PlainTextConstruct("Follow up: "+ prospect.name + "-"+prospect.phone_number));
		else if(STAGE_CATEGORY == ProspectDetails.STAGE1_SCHEDULE_APT)
			myEntry.setTitle(new PlainTextConstruct("Schedule Appointment: "+ prospect.name + "-"+prospect.phone_number));
		else if(STAGE_CATEGORY == ProspectDetails.STAGE2_RESCHEDULE_APT)
			myEntry.setTitle(new PlainTextConstruct("Schedule Appointment: "+ prospect.name + "-"+prospect.phone_number));

			
		
		/*else if (STAGE_CATEGORY == ProspectDetails.STAGE2_FOLLOWUP_APT)
			myEntry.setTitle(new PlainTextConstruct("Follow up to book the Estimate Appointment: "+ prospect.name));
		else
			myEntry.setTitle(new PlainTextConstruct("Estimate Appointment: "+ prospect.name + "-" + prospect.phone_number));*/
		
		myEntry.setContent(new PlainTextConstruct(prospect.details));

		Person author = new Person(prospect.name, null, prospect.email);
		myEntry.getAuthors().add(author);

		//		Calendar c = Calendar.getInstance();
		//		c.getTimeInMillis();
		//		c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
		//		c.set(Calendar.HOUR_OF_DAY, 05);
		//		c.set(Calendar.MINUTE, 00);
		//		c.set(Calendar.SECOND, 00);
		//		
		//		String formatted_date = getFormattedDate(c.getTime());
		//		String formatted__end_date = getFormattedEndDate(c.getTime());

		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);

		Where whereEvent = new Where();
		whereEvent.setValueString(prospect.address+ ", " + prospect.city);
		myEntry.addLocation(whereEvent);

		// Send the request and receive the response:
		EventEntry insertedEntry = service.insert(postUrl, myEntry);

		Log.v("", "event id: "+insertedEntry.getId());

		return insertedEntry.getId();
	}

	public static String updateCalendarEvent1 (
			CalendarService service, int STAGE_CATEGORY, Date date, Prospects prospect)
					throws ServiceException, IOException {
		
		String formatted_date = getFormattedDate(date);
		String formatted__end_date = getFormattedEndDate(date);
		DateTime startTime = DateTime.parseDateTime(formatted_date);
		DateTime endTime = DateTime.parseDateTime(formatted__end_date);
		
		CalendarQuery myQuery = new CalendarQuery(new URL(C.POST_CALENDAR_GOOGLE));
		myQuery.setMinimumStartTime(startTime);
//		myQuery.setMaximumStartTime(startTime);
		myQuery.setMaxResults(5);

		// Send the request and receive the response:
		CalendarEventFeed resultFeed = service.query(myQuery, CalendarEventFeed.class);
		
		int i = resultFeed.getEntries().size();
		Log.v("", "conflict size: " + i);
//		for (int j = 0; j < i; j++) {
//			CalendarEventEntry entry = resultFeed.getEntries().get(j);
//			Log.v("", "entry: " + entry.getTitle().getPlainText());
//		}
//		if (i > 0) return "";

		Log.v("", "prospect.calendar_id: " + prospect.calendar_id);
		String id = prospect.calendar_id.substring(prospect.calendar_id.lastIndexOf("/"), prospect.calendar_id.length());
		Log.v("", "id: "+id + "\n"+C.POST_CALENDAR_GOOGLE + id);		
		EventEntry myEntry = service.getEntry(new URL(C.POST_CALENDAR_GOOGLE + id),EventEntry.class);		
		if (STAGE_CATEGORY == ProspectDetails.STAGE2_SCHEDULE_APT 
				|| STAGE_CATEGORY == ProspectDetails.STAGE2_RESCHEDULE_APT){			
			myEntry.setTitle(new PlainTextConstruct("Estimate Appointment: "+ prospect.name + "-" + prospect.phone_number));			
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE2_FOLLOWUP_APT 
				|| STAGE_CATEGORY == ProspectDetails.STAGE2_CANCEL_APT_FORNOW){
			myEntry.setTitle(new PlainTextConstruct("Follow up: "+ prospect.name + "-" + prospect.phone_number));
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE2_CONFIRM_APT){
			myEntry.setTitle(new PlainTextConstruct("Estimate Appointment (Confirmed): "+ prospect.name + "-" + prospect.phone_number));
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE3_ESTIMATE_DATE){
			myEntry.setTitle(new PlainTextConstruct("Complete Estimate by now. "+ prospect.name + "-" + prospect.phone_number));
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE3_WHEN_ESTIMATE_COMPLETED){
			myEntry.setTitle(new PlainTextConstruct("Follow up on estimate sent: " + prospect.name + "-" + prospect.phone_number));
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE4_PROJECT_START){
			myEntry.setTitle(new PlainTextConstruct("New job starting today: " + prospect.name + "-" + prospect.phone_number));			
		}else if (STAGE_CATEGORY == ProspectDetails.STAGE4_WAITING_OTHER_ESTIMATES){
//			myEntry.setTitle(new PlainTextConstruct("Was waiting for other estimates - Now call: "+ prospect.name + "-"+ prospect.phone_number));			
			myEntry.setTitle(new PlainTextConstruct("Follow up - Was waiting for other estimates: "+ prospect.name + "-"+ prospect.phone_number));			
		} else if (STAGE_CATEGORY == ProspectDetails.STAGE5_FINISH_DATE  ){
//			myEntry.setTitle(new PlainTextConstruct("Job Completion Date: "+ prospect.name));
			//myEntry.setTitle(new PlainTextConstruct("Customer care follow up - "+ prospect.name + "-" + prospect.phone_number));
			myEntry.setTitle(new PlainTextConstruct("Job Completion Date: "+ prospect.name+ "-" + prospect.phone_number));

		}else if (STAGE_CATEGORY == ProspectDetails.STAGE6_CUSTOMERCARE_FOLLOWUP ){
//			myEntry.setTitle(new PlainTextConstruct("Job Completion Date: "+ prospect.name));
			myEntry.setTitle(new PlainTextConstruct("Customer care followup : "+ prospect.name + "-" + prospect.phone_number));
		}else if (STAGE_CATEGORY == ProspectDetails.STAGE6_CUSTOMERCARE_SCHEDULE ){
//			myEntry.setTitle(new PlainTextConstruct("Job Completion Date: "+ prospect.name));
			myEntry.setTitle(new PlainTextConstruct("Customer Care Appointment : "+ prospect.name + "-" + prospect.phone_number));
		}else if (STAGE_CATEGORY == ProspectDetails.STAGE1_LEFTMSG_NOANSWER){
			myEntry.setTitle(new PlainTextConstruct("Schedule Appointment: "+ prospect.name + "-" + prospect.phone_number));
		}

		Person author = new Person(prospect.name, null, prospect.email);
		myEntry.getAuthors().add(author);
		
		if (STAGE_CATEGORY != ProspectDetails.STAGE2_CONFIRM_APT){
			When eventTimes = new When();
			eventTimes.setStartTime(startTime);
			eventTimes.setEndTime(endTime);
			myEntry.addTime(eventTimes);
		}

		Where whereEvent = new Where();
		whereEvent.setValueString(prospect.address+ ", " + prospect.city);
		myEntry.addLocation(whereEvent);

		URL editUrl = new URL(myEntry.getEditLink().getHref());

		// Send the request and receive the response:		
		EventEntry updatedEntry = service.update(editUrl, myEntry);
		Log.v("", "event id: "+updatedEntry.getId());	
		return updatedEntry.getId();
	}
	
	public static void deleteCalendarEvent1 (CalendarService service, Prospects prospect)
					throws ServiceException, IOException {		
		String id = prospect.calendar_id.substring(prospect.calendar_id.lastIndexOf("/"),
					prospect.calendar_id.length());
		Log.v("", "id: "+id + "\n"+C.POST_CALENDAR_GOOGLE + id);
		
		/*Query myQuery = new Query(new URL(C.CALENDAR_EVENT_URL));
		myQuery.setFullTextQuery(prospect.name);

		CalendarEventFeed myResultsFeed = service.query(myQuery,
		    CalendarEventFeed.class);
		if (myResultsFeed.getEntries().size() > 0) {
		  CalendarEventEntry firstMatchEntry = (CalendarEventEntry)
		      myResultsFeed.getEntries().get(0);
		  String myEntryTitle = firstMatchEntry.getTitle().getPlainText();
		  Log.v("", "myEntryTitle: "+myEntryTitle);	
		}*/
		
		CalendarEventEntry myEntry = service.getEntry(new URL(C.POST_CALENDAR_GOOGLE + id),
				CalendarEventEntry.class);		
		myEntry.delete();
		//		URL editUrl = new URL(myEntry.getEditLink().getHref());		
//		service.delete(editUrl);
		Log.v("", "id: "+ C.POST_CALENDAR_GOOGLE + id + " Deleted!!!");	
	}

	public static CalendarService getCalendarService(SharedPreferences prefs) 
			throws OAuthException{
		CalendarService service = new CalendarService(C.APP_NAME);
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(C.CONSUMER_KEY);
		oauthParameters.setOAuthConsumerSecret(C.CONSUMER_SECRET);
		oauthParameters.setOAuthToken(C.getConsumer(prefs).getToken());
		oauthParameters.setOAuthTokenSecret(C.getConsumer(prefs).getTokenSecret());
		service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());

		return service;
	}

	public static String getFormattedDate(Date date1){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
		String str = sdf.format(date1);
		String seperator = str.substring(str.length() - 5, str.length() - 4);
		Log.v("", "seperator: "+seperator);	
		String str_date = str.substring(0, str.lastIndexOf(seperator) + 1);	
		String time = str.substring(str.lastIndexOf(seperator)+1, str.length());
		String hr = time.substring(0, 2);
		String mins = time.substring(2, 4);
		Log.v("", "hrmins: "+ str_date + hr + ":" + mins);
		//String formatted_date = sdf.format(c.getTime()).replaceAll("(\\+\\d\\d)(\\d\\d)", "$1:$2");
		String formatted_date = str_date + hr + ":" + mins;
		return formatted_date;
	}

	public static String getFormattedEndDate(Date date1){
		Calendar c = Calendar.getInstance();
		c.setTime(date1);
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY)+1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
		String str = sdf.format(c.getTime());
		String seperator = str.substring(str.length() - 5, str.length() - 4);
		Log.v("", "seperator: "+seperator);	
		String str_date = str.substring(0, str.lastIndexOf(seperator) + 1);	
		String time = str.substring(str.lastIndexOf(seperator)+1, str.length());
		String hr = time.substring(0, 2);
		String mins = time.substring(2, 4);
		Log.v("", "hrmins: "+ str_date + hr + ":" + mins);
		//String formatted_date = sdf.format(c.getTime()).replaceAll("(\\+\\d\\d)(\\d\\d)", "$1:$2");
		String formatted_date = str_date + hr + ":" + mins;
		return formatted_date;
	}

}
