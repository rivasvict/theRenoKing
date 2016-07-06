package com.rifluxyss.therenoking.tasks;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import bugtracker.Util;

import com.rifluxyss.therenoking.network.APIClient;
import com.rifluxyss.therenoking.utils.EnumHandler;
import com.rifluxyss.therenoking.utils.Utilities;

public class ShowCampaigns extends AsyncTask<String, Void, Void> {
	Activity thisActivity;
	Handler rHandler;

	ArrayList<Integer> listCampaignID = new ArrayList<Integer>();
	ArrayList<String> listCampaignName = new ArrayList<String>();

	public ShowCampaigns(Activity thisContext, Handler rHandler){
		this.thisActivity = thisContext;
		this.rHandler = rHandler;
	}

	@Override
	protected Void doInBackground(String... arg0) {
		APIClient apiclient = new APIClient(thisActivity);
		int status = apiclient.processAndFetchResponse();
		if (status == APIClient.STATUS_SUCCESS){
			Document doc;
			NodeList CAMPAIGN;
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
				CAMPAIGN = doc.getElementsByTagName("CAMPAIGN");
				if (CAMPAIGN.item(0) != null){									
					int campaign_length = CAMPAIGN.getLength();	
					for (int i = 0; i < campaign_length; i++){
						Node campaign_node = CAMPAIGN.item(i);
						if (campaign_node.getNodeType() == Node.ELEMENT_NODE) {
							Element campElement = (Element) campaign_node;							
							listCampaignID.add(Integer.parseInt(Utilities.getTagValue("CAMPAIGN_ID", campElement)));
							listCampaignName.add(Utilities.getTagValue("CAMPAIGN_NAME", campElement));							
						}
					}
				}
			}catch (Exception e) {
				Util.insertCaughtException(e, thisActivity);
				e.printStackTrace();
			}
		}else{
			Util.pushServerResponseInfo(thisActivity, Thread.currentThread(), "Error in getting Campaigns list.");
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Message msg = new Message();
		msg.what = EnumHandler.SHOW_CAMPAIGN;
		Bundle b = new Bundle();
		b.putIntegerArrayList("id", listCampaignID);
		b.putStringArrayList("name", listCampaignName);
		msg.setData(b);
		rHandler.sendMessage(msg);
	}

}
