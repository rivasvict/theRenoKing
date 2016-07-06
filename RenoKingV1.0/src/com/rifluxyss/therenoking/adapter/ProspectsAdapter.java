package com.rifluxyss.therenoking.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import com.rifluxyss.therenoking.AddProspect;
import com.rifluxyss.therenoking.ProspectDetails;
import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.TheRenoKing;
import com.rifluxyss.therenoking.beans.Prospects;
import com.rifluxyss.therenoking.utils.EnumHandler;
import com.rifluxyss.therenoking.utils.Utilities;

public class ProspectsAdapter extends ArrayAdapter<Prospects> {

	/*private ArrayList<Prospects> originalList = new ArrayList<Prospects>();
	private ArrayList<Prospects> prospectsList = new ArrayList<Prospects>();*/
	
	private ArrayList<Prospects> originalList ;
	private ArrayList<Prospects> prospectsList ;
	private ProspectFilter filter;
	Context context;
	Handler mHandler;
	String strSearchFilter = "";

	public ProspectsAdapter(Context context, int textViewResourceId, 
			ArrayList<Prospects> prospectList) {
		super(context, textViewResourceId, prospectList);
		this.context = context;
		
		this.prospectsList = new ArrayList<Prospects>();
		this.prospectsList.addAll(prospectList);
		this.originalList = new ArrayList<Prospects>();
		this.originalList.addAll(prospectList);
		
		/*this.prospectsList = prospectList;
		this.originalList = prospectList;*/
	}

	@Override
	public Filter getFilter() {
		if (filter == null){
			filter = new ProspectFilter();
		}
		return filter;
	}
	
	public void setFilter(){
		filter = null;
	}

	public void setHandler(Handler handler){
		mHandler = handler;
	}

	public void setProspects(ArrayList<Prospects> pros){	
		this.prospectsList = new ArrayList<Prospects>();
		this.prospectsList.addAll(pros);
		this.originalList = new ArrayList<Prospects>();
		this.originalList.addAll(pros);
	
		/*this.prospectsList = pros;
		this.originalList = pros;*/
	}
	
	@Override
	public int getCount() {
		return prospectsList.size();
	}


	private class ViewHolder {
		TextView lblName;
		TextView lblEmail;
		TextView lblPhone;
		TextView lblAddress;
		TextView lblCity;
		Button btnEdit;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.prospect_inflate, null);
			viewHolder = new ViewHolder();			
			viewHolder.lblName = (TextView) convertView.findViewById(R.id.lblName);
			viewHolder.lblEmail = (TextView) convertView.findViewById(R.id.lblEmail);
			viewHolder.lblPhone = (TextView) convertView.findViewById(R.id.lblPhone);	
			viewHolder.lblAddress = (TextView) convertView.findViewById(R.id.lblAddress);	
			viewHolder.lblCity = (TextView) convertView.findViewById(R.id.lblCity);	
			viewHolder.btnEdit = (Button) convertView.findViewById(R.id.btnEdit);	

			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		
		final Prospects prosp = prospectsList.get(position);
		//		Log.v("", "prosp.name: "+prosp.name);
		viewHolder.lblName.setText(prosp.name);
		viewHolder.lblEmail.setText(prosp.email);
		viewHolder.lblPhone.setText(prosp.phone_number);
		if (prosp.address != null && !prosp.address.equals("")){
			viewHolder.lblAddress.setVisibility(View.VISIBLE);
			if(!prosp.zipcode.equals("") && !prosp.province.equals(""))
				viewHolder.lblAddress.setText(prosp.address +"\n"+prosp.zipcode+"\n"+prosp.province);
			else if (!prosp.zipcode.equals(""))
				viewHolder.lblAddress.setText(prosp.address +"\n"+prosp.zipcode);
			else if (!prosp.province.equals(""))
				viewHolder.lblAddress.setText(prosp.address +"\n"+prosp.province);
			else
				viewHolder.lblAddress.setText(prosp.address);
		}else{
			viewHolder.lblAddress.setVisibility(View.GONE);
		}

		if (prosp.city != null && !prosp.city.equals("")){
			viewHolder.lblCity.setVisibility(View.VISIBLE);
			viewHolder.lblCity.setText(prosp.city);
		}else{
			viewHolder.lblCity.setVisibility(View.GONE);
		}

		viewHolder.btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context, AddProspect.class);
				Bundle b = new Bundle();
				b.putSerializable("prospect", prosp);
				i.putExtras(b);
				context.startActivity(i);
			}
		});
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			/*	Message msg = new Message();
				msg.what = EnumHandler.PROSPECT_DETAIL;
				msg.arg1 = position;
				mHandler.sendMessage(msg);*/
				
				Log.v("", "ProspectDetails ========");
				Intent i = new Intent(context, ProspectDetails.class);
				/*i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |  Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);*/
				Bundle b = new Bundle();
	            b.putSerializable("prospect", prosp);
	            i.putExtras(b);
	            context.startActivity(i);
				
			}
		});

		return convertView;
	}
	
	public String getSearch(){
		return strSearchFilter;		
	}

	private class ProspectFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			strSearchFilter = constraint.toString();
			FilterResults result = new FilterResults();
			
			if(constraint != null && constraint.toString().length() > 0)
			{
				ArrayList<Prospects> filteredItems = new ArrayList<Prospects>();

				for(int i = 0, l = originalList.size(); i < l; i++)
				{
					Prospects country = originalList.get(i);
					if(country.name.toString().toLowerCase().startsWith(constraint.toString()))
						filteredItems.add(country);
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			}
			else
			{
				synchronized(this)
				{
					result.values = originalList;
					result.count = originalList.size();					
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {

			prospectsList = (ArrayList<Prospects>)results.values;
			notifyDataSetChanged();
			clear();
			
			for(int i = 0, l = prospectsList.size(); i < l; i++)
				add(prospectsList.get(i));

			Message msg = new Message();
			msg.what = EnumHandler.FILTER_PROSPECTS;
			msg.arg1 = prospectsList.size();			
			mHandler.sendMessage(msg);
			
			Log.e("", "what==>>"+msg.what);
			Log.e("", "arg1==>>"+msg.arg1);

			notifyDataSetInvalidated();
		}
	}
}