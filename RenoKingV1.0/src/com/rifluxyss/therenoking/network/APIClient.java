package com.rifluxyss.therenoking.network;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.generic.Generic;
import com.rifluxyss.therenoking.utils.Utilities;

public class APIClient {
	public static String NAMESPACE = "";
	private String apiAction, apiParams = "";
	private String response;
	private int status;

	private HashMap<String, String> apiParamsList;
	private Exception api_exception;

	HttpPost httpPost = null;
	HttpResponse httpresponse = null;
	HttpContext localContext;
	StringEntity tmp = null;

	DefaultHttpClient httpClient;
	ProgressDialog dialog;
	Activity thisContext;
	String errorMsg, strprospectid, tag;
	
	public static final int STATUS_INTERNET_FAILED = -1;
	public static final int STATUS_NONE = 0;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAILED = 2;
	public static final int STATUS_ERROR = 3;
	public static final int STATUS_SESSION_EXPIRED = 4;
	public static final int STATUS_UN_SUCCESSFUL = 5;	
	
	public APIClient(Activity thisContext, String methodName,
			HashMap<String, String> paramsList) {
		response = "";
		this.thisContext = thisContext;
		if (Generic.LIVE_WEBSERVICE)
			NAMESPACE = thisContext.getResources().getString(R.string.NAMESPACE);
		else
			NAMESPACE = thisContext.getResources().getString(R.string.DEMO_NAMESPACE);
		if(methodName.equals("sendcampaign")){
			this.apiAction = thisContext.getResources().getString(R.string.API_CAMPAIGNS_SEND);
		}else
			this.apiAction = NAMESPACE + methodName;
		this.apiParamsList = paramsList;
		status = STATUS_NONE;
		api_exception = null;
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		HttpConnectionParams.setSoTimeout(myParams, 10000);
		httpClient = new DefaultHttpClient(myParams);
		localContext = new BasicHttpContext();		
	}
	
	public APIClient(Activity thisContext, String methodName,
			HashMap<String, String> paramsList, String tag) {
		response = "";
		NAMESPACE = thisContext.getResources().getString(R.string.NAMESPACENEW);
		this.thisContext = thisContext;
		this.tag = tag;
		this.apiAction = NAMESPACE + methodName;
		this.apiParamsList = paramsList;
		status = STATUS_NONE;
		api_exception = null;
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		HttpConnectionParams.setSoTimeout(myParams, 10000);
		httpClient = new DefaultHttpClient(myParams);
		localContext = new BasicHttpContext();		
	}
	
	public APIClient(Activity thisContext) {
		response = "";
		this.thisContext = thisContext;	
		apiAction = thisContext.getResources().getString(R.string.API_CAMPAIGNS);			
		status = STATUS_NONE;
		api_exception = null;
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		HttpConnectionParams.setSoTimeout(myParams, 10000);
		httpClient = new DefaultHttpClient(myParams);
		localContext = new BasicHttpContext();		
	}
		
	public APIClient(Activity thisContext, String strURL, boolean add_namespace){
		this.apiAction = strURL;
		status = STATUS_NONE;
		errorMsg = "";
		response = "";
		this.thisContext = thisContext;
		
		if(add_namespace){
			NAMESPACE = strURL;
		}else{
			NAMESPACE = thisContext.getResources().getString(R.string.EXCEPTION_NAMESPACE_LIVE);
		}
		
		this.apiAction = NAMESPACE;
		
		api_exception = null;
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 10000);
		HttpConnectionParams.setSoTimeout(myParams, 10000);
		httpClient = new DefaultHttpClient(myParams);
		localContext = new BasicHttpContext();	
	}

	private void log(String msg) {
		Log.i("The Reno King", msg);
	}

	public int processAndFetchResponse() {
		this.status = STATUS_NONE;
		UrlEncodedFormEntity p_entity = null;
		if(apiParamsList != null){
			String param_keys[] = this.apiParamsList.keySet()
					.toArray(new String[0]);
			String param_values[] = this.apiParamsList.values().toArray(
					new String[0]);
			apiParams = "";
	
			List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
	
			for (int param_i = 0; param_i < this.apiParamsList.size(); param_i++) {
				apiParams += "" + param_keys[param_i] + "=" + param_values[param_i]
						+ "&";
				nvps.add(new BasicNameValuePair(param_keys[param_i],
						param_values[param_i]));
			}
			
			try {
				p_entity = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			if (apiParams != null && apiParams.trim().length() > 0)
				apiParams = apiParams.substring(0, apiParams.lastIndexOf('&'));
			// log("Params = " + apiParams + "; Params count = "
			// + apiParamsList.size());
			log("Params :  = " + nvps);
		}
		
		log("Calling API URL = " + apiAction + "&" + apiParams);		
	
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.RFC_2109);	
		
		httpPost = new HttpPost(apiAction);

		response = null;
		httpPost.setHeader("User-Agent", "SET YOUR USER AGENT STRING HERE");
		httpPost.setHeader("Accept","text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		
		try {
			if(!apiParams.trim().equals(""))
				tmp = new StringEntity(apiParams, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log("HttpUtils : UnsupportedEncodingException : " + e);
		}
		
		httpPost.setEntity(p_entity);

		try {
			DefaultHttpClient client = new DefaultHttpClient();
			httpresponse = client.execute(httpPost, localContext);
			
			if (httpresponse != null) {
				String responseContentType = httpresponse.getFirstHeader(
						"Content-type").getValue().trim();
				log("Response Content Type = " + responseContentType);
				response = (EntityUtils.toString(httpresponse.getEntity())).replaceAll("^^^^^", "");
				
				log("Servr Response => " + response);
				
				if (!response.equals("")){
					this.status = STATUS_SUCCESS;
				}else{
					this.status = STATUS_UN_SUCCESSFUL;
				}
			} else {
				this.status = STATUS_ERROR;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
		} catch (SocketException e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
		}	
		
		setResponse();
		
		return status;
	}
	
	public int postValues(MultipartEntity entity){
		this.status = STATUS_NONE;
		String ret = null;				
		response = null;
		MultipartEntity p_entity = entity;
		HttpClient client = new DefaultHttpClient();  
		HttpPost post = new HttpPost(apiAction);
		log("Calling API URL = " + apiAction);
		post.setEntity(p_entity); 		

		try {			
			httpresponse = client.execute(post);			
			
			if (httpresponse != null) {
				String responseContentType = httpresponse.getFirstHeader("Content-type").getValue().trim();
				log("Response Content Type = " + responseContentType);
				ret = (EntityUtils.toString(httpresponse.getEntity())).replaceAll("^^^^^", "");				
				response = ret;
				log("Servr Response => " + response);
				String checkSrResponse = "";
				if (!response.equals("")){
					checkSrResponse = "" + response.trim().charAt(0);
				}				
//				status_code = httpresponse.getStatusLine().getStatusCode();				
				if (checkSrResponse.equals("[")){
					Log.i("", "Api client success");
					this.status = STATUS_SUCCESS;
				}else if (responseContentType.equals("text/xml")|| responseContentType.equals("text/html")){
					Log.i("", "Api client content type");
					this.status = STATUS_SUCCESS;
				}else{
					Log.i("", "Api client unsuccess");
					this.status = STATUS_UN_SUCCESSFUL;
				}				
			} else {
				this.status = STATUS_ERROR;
				this.errorMsg = "error";
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
			this.errorMsg = "error";
		} catch (SocketException e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
			this.errorMsg = "error";
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
			this.errorMsg = "error";
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
			this.errorMsg = "error";
		} catch (Exception e) {
			e.printStackTrace();
			log("" + e);
			this.status = STATUS_ERROR;
			this.errorMsg = "error";
		}
		return status;
	}
	
	public void setResponse(){		
		if(!Utilities.haveInternet(thisContext)){
			response = getFailedMessage();
		}else if (status == APIClient.STATUS_ERROR) {
			response = getErrorMessage();
		} else if (status == APIClient.STATUS_FAILED) {
			response = getFailedMessage();
		} else if (status == APIClient.STATUS_SUCCESS) {
			response = getResponse();
		}else if (status == APIClient.STATUS_UN_SUCCESSFUL) {
			response = getErrorMessage();
		}
	}
	
	public static boolean getSuccess(int status) {
		boolean success = true;
		switch (status) {
			case STATUS_ERROR:
				success = false;
				break;
			case STATUS_FAILED:
				success = false;
				break;
			case STATUS_NONE:
				success = false;
				break;
			case STATUS_SESSION_EXPIRED:
				success = false;
				break;
			case STATUS_UN_SUCCESSFUL:
				success = false;
				break;			
		}		
		return success;
	}
	
	public int getStatus() {
		return status;
	}

	public Exception getException() {
		return api_exception;
	}

	public String getResponse() {		
		return response;
	}

	public String getFailedMessage() {
		return thisContext.getResources().getString(R.string.INTERNET_PROBLEM);
	}

	public String getErrorMessage() {
		return thisContext.getResources().getString(R.string.API_PROBLEM);
	}
}


