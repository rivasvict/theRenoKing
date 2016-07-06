package com.rifluxyss.therenoking.tasks;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.beans.Prospects;
import com.rifluxyss.therenoking.network.APIClient;
import com.rifluxyss.therenoking.utils.EnumHandler;
import com.rifluxyss.therenoking.utils.Utilities;

public class SendMail extends AsyncTask<String, Integer, Void> {
	Activity thisActivity;
	Prospects prospect;
	String mail_stage;
	String date_time;
	Handler rhandler;
	String strStatus = "", strMessage ="";
	String[] strMsgArray;
	
	
	public SendMail(Activity thisActivity, Prospects prospect, String mail_stage, String date_time, Handler rhandler){
		this.thisActivity = thisActivity;
		this.prospect = prospect;
		this.mail_stage = mail_stage;
		this.date_time = date_time;
		this.rhandler = rhandler;
	}

	@Override
	protected Void doInBackground(String... params) {
		HashMap<String, String> api_params = new HashMap<String, String>();
		api_params.put("prospect_id", prospect.prospect_id);
		api_params.put("contact_id", prospect.pumka_prospect_id);
		api_params.put("stage", mail_stage);
		api_params.put("date_time", date_time);
		/*http://www.therenoking.ca/webservice/api.php?cmd=schedule_email&prospect_id=<prospect_id>&contact_id=<contact_id>
		&stage=1A/2Bi/2Bii/2Biii/2Cv/3Ai/3Aii/3Aiii/3Bi/3Bii/4Ai/4Aii/4Aiii/4Bi*/
		APIClient apiclient = new APIClient(thisActivity, 
				thisActivity.getResources().getString(R.string.api_schedule_email), api_params);
		int status = apiclient.processAndFetchResponse();
		Log.v("", "api_params-->> "+api_params);
		Log.v("", "status: "+status);
		if (status == APIClient.STATUS_SUCCESS){
			Document doc;
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
				NodeList STATUS = doc.getElementsByTagName("STATUS");
				if (STATUS != null){
					Log.v("", "strMessage 00==>"+Utilities.getNodeValue(doc, "STATUS"));
					Log.v("", "strStatus 00==>"+Utilities.getNodeValue(doc, "MESSAGE"));
					strStatus = Utilities.getNodeValue(doc, "STATUS");		
					strMessage = Utilities.getNodeValue(doc, "MESSAGE"); 	
					Log.v("", "strMessage ==>"+strMessage);
					Log.v("", "strStatus ==>"+strStatus);
//					strMessage = "Email 1 sent on 2014-04-08 @@@@@ Email sent on 2014-04-08 @@@@@ Email 3 sent on 2014-04-08";
					 
					if(strMessage.contains("@@@@@")){
						strMsgArray =  strMessage.split("@@@@@");
						
						for (int i=0 ; i < strMsgArray.length; i++){
							Log.e("mail", "strMsgArray==>>"+strMsgArray);
							//strMessage += strMsgArray[i];
						}
					}
					strMessage.replaceAll("@@@@@","\n");
					Log.e("mail", "strStatus==>>"+strStatus);
					Log.e("mail", "new strMessage==>>"+strMessage);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString("message", strMessage);
		msg.setData(b);
		msg.what = EnumHandler.PROSPECT_DID_NOT_SHOW;
		rhandler.sendMessage(msg);		
		super.onPostExecute(result);
	}
}
