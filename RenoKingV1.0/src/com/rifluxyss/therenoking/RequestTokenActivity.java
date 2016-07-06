package com.rifluxyss.therenoking;

import java.net.URLEncoder;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import bugtracker.ExceptionReporter;

import com.google.gdata.client.GoogleService;
import com.rifluxyss.therenoking.generic.C;


public class RequestTokenActivity extends TheRenoKing {	
	
    private OAuthConsumer consumer; 
    private OAuthProvider provider;
    private SharedPreferences prefs;
    boolean requested = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	ExceptionReporter.register(RequestTokenActivity.this);
    	try {
    		consumer = new CommonsHttpOAuthConsumer(C.CONSUMER_KEY, C.CONSUMER_SECRET);
    		provider = new CommonsHttpOAuthProvider(
    				C.REQUEST_URL  + "?scope=" + URLEncoder.encode(C.SCOPE, C.ENCODING) + "&xoauth_displayname=" + C.APP_NAME,
    				C.ACCESS_URL,
    				C.AUTHORIZE_URL);
    	} catch (Exception e) {
    		Log.e(C.TAG, "Error creating consumer / provider",e);
    	}

//    	getRequestToken();    	
    	new RetrieveRequestToken().execute();
    	
    }

	
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent); 
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(C.OAUTH_CALLBACK_SCHEME)) {
			Log.v(C.TAG, "Callback received : " + uri);
			Log.i(C.TAG, "Retrieving Access Token");
//			getAccessToken(uri);
			new RetrieveAccessToken().execute(uri);
		}else{			
			this.finish();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v(C.TAG, "Denied Access Token onResume");
	}
	
//	private void getRequestToken() {
//		try {
//			Log.d(C.TAG, "getRequestToken() called");
//			requested = true;
//			String url = provider.retrieveRequestToken(consumer, C.OAUTH_CALLBACK_URL);
//			
//			
//		} catch (Exception e) {
//			Log.e(C.TAG, "Error retrieving request token", e);
//		}
//	}
	
	private class RetrieveRequestToken extends AsyncTask<Context, String, String> {		

		@Override
		protected String doInBackground(Context... params) {			
			try {
				return provider.retrieveRequestToken(consumer, C.OAUTH_CALLBACK_URL);
			} catch (Exception e) {
//				Log.e(C.TAG, "Error retrieving request token", e);
				Log.e(C.TAG, "Error retrieving request token:" +e.toString());
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				// saving the token
				Intent intent = new Intent(Intent.ACTION_VIEW, 
						Uri.parse(result)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP 
								| Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
				startActivity(intent);
			}
		}
	}
	
	private class RetrieveAccessToken extends AsyncTask<Uri, String, String> {
		
		Uri uri;

		@Override
		protected String doInBackground(Uri... params) {
			uri = params[0];
			try {
				final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
				provider.retrieveAccessToken(consumer, oauth_verifier);
				
				final Editor edit = prefs.edit();
				edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
				edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
				edit.commit();
				
				String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
				String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
				

				consumer.setTokenWithSecret(token, secret);
//				this.startActivity(new Intent(this ,OAuthMain.class));
				finish();
			} catch (Exception e) {				
				Log.e(C.TAG, "Access Token Retrieval Error", e);				
				finish();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {			
			
		}
	}
	
	
	/*private void getAccessToken(Uri uri) {
		final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
		try {
			provider.retrieveAccessToken(consumer, oauth_verifier);

			final Editor edit = prefs.edit();
			edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
			edit.putString(OAuth.OAUTH_TOKEN_SECRET, consumer.getTokenSecret());
			edit.commit();
			
			String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
			String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
			
			consumer.setTokenWithSecret(token, secret);
//			this.startActivity(new Intent(this ,OAuthMain.class));
			this.finish();
			Log.i(C.TAG, "Access Token Retrieved");
			
		} catch (Exception e) {
			Log.e(C.TAG, "Access Token Retrieval Error", e);
			this.finish();
		}
	}*/
	
}
