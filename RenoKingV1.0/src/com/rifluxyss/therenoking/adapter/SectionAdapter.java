package com.rifluxyss.therenoking.adapter;

import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rifluxyss.therenoking.R;
import com.rifluxyss.therenoking.beans.Data;
import com.rifluxyss.therenoking.beans.ProspectData;
import com.rifluxyss.therenoking.utils.EnumHandler;
import com.rifluxyss.therenoking.utils.Utilities;

public class SectionAdapter extends AmazingAdapter {
	
	List<Pair<String, List<ProspectData>>> all;
	Activity context;
	Data data;
	Handler handler;
	final int HANDLERESULTS = 302;
	public SectionAdapter(Activity mContext, int textViewResourceId, List<Pair<String, List<ProspectData>>> listAll, Handler handler) {
		super(mContext, textViewResourceId);
		this.all = listAll;
		this.context = mContext;
		this.handler = handler;
	}
	
	static class ProspectsViewHolder{
		TextView lblDesc;
		TextView lblDate;
	}	
	
	@Override
	public int getCount() {
		int res = 0;
		try {
			for (int i = 0; i < all.size(); i++) {
				res += all.get(i).second.size();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
//	public void setIndex(int index){
//		rolo_index = index;
//	}
	
	public void setData(List<Pair<String, List<ProspectData>>> listall){
		all = listall;
	}
	
//	public void setSorting(Data Rolodata, boolean letsmarkusers){
//		data = Rolodata;		
//	}

	@Override
	public ProspectData getItem(int position) {
		int c = 0;
		for (int i = 0; i < all.size(); i++) {
			if (position >= c && position < c + all.get(i).second.size()) {
				return all.get(i).second.get(position - c);
			}
			c += all.get(i).second.size();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected void onNextPageRequested(int page) {
	}

	@Override
	protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
		if (displaySectionHeader) {			
			RelativeLayout inflate = (RelativeLayout) view.findViewById(R.id.lnrStage);
			inflate.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams layparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
					getScaledPixel(35));
			inflate.setLayoutParams(layparams);			
			TextView lSectionTitle = (TextView) view.findViewById(R.id.lblStage);
			String title = getSections()[getSectionForPosition(position)];
			lSectionTitle.setText(title);			
			ImageView setaction = (ImageView) view.findViewById(R.id.imgSetDate);	
			int msg = 0;
			if (title.equals(context.getString(R.string.stage1))){
				setaction.setVisibility(View.GONE);				
			}else if (title.equals(context.getString(R.string.stage2))){
				setaction.setVisibility(View.VISIBLE);
				msg = EnumHandler.SET_REMINDER;
			}else if (title.equals(context.getString(R.string.stage3))){
				setaction.setVisibility(View.VISIBLE);
				msg = EnumHandler.SET_ESTIMATE;
			}else if (title.equals(context.getString(R.string.stage4))){
				setaction.setVisibility(View.GONE);
			}else if (title.equals(context.getString(R.string.stage5))){
				setaction.setVisibility(View.GONE);
			}else{
				setaction.setVisibility(View.GONE);
			}
			final int handle = msg;
			setaction.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
					handler.sendMessage(Utilities.getMessage(handle));
				}
			});
		} else {
			view.findViewById(R.id.lnrStage).setVisibility(View.GONE);
		}
	}
	
	int getScaledPixel(int size){		
	    DisplayMetrics dm = new DisplayMetrics();
	    context.getWindowManager().getDefaultDisplay().getMetrics(dm);	    
		return (int) (size * dm.scaledDensity); 
	}

	@Override
	public View getAmazingView(final int position, View convertView, ViewGroup parent) {
		View res = convertView;
		final ProspectsViewHolder viewHolder;
		try {
			if (res == null){
				res = context.getLayoutInflater().inflate(R.layout.prospect_stage_descr, null);
				viewHolder = new ProspectsViewHolder();
				viewHolder.lblDesc = (TextView) res.findViewById(R.id.lblDesc);
				viewHolder.lblDate = (TextView) res.findViewById(R.id.lblDate);
				res.setTag(viewHolder);
			}else{
				viewHolder = (ProspectsViewHolder)res.getTag();
			}
			
			final ProspectData pros = getItem(position);
			if (pros.name != null){		
				viewHolder.lblDesc.setText(pros.name);
			}else if (pros.status != null && pros.reminder != null){
				viewHolder.lblDesc.setText(getDesc(pros.status));
				viewHolder.lblDate.setText(pros.reminder);
			}else{
				viewHolder.lblDesc.setText("");
				viewHolder.lblDate.setText("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return res;
	}
	
	String getDesc(String desc){
    	String stat = "";
    	if (desc.equals("schedule"))
    		stat = context.getString(R.string.apt_reminder_date);
    	else if (desc.equals("followup"))
    		stat = context.getString(R.string.apt_followup_date);
    	else if (desc.equals("reschedule"))
    		stat = context.getString(R.string.apt_reminder_date);
    	else if (desc.equals("confirm"))
    		stat = context.getString(R.string.apt_confirm_date);
    	else if (desc.equals("dead"))
    		stat = context.getString(R.string.apt_cancelled);	
    	else if (desc.equals("cancel"))
    		stat = context.getString(R.string.apt_followup_date);
    	return stat;
    }

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		RelativeLayout lSectionHeader = (RelativeLayout) header;
		TextView txtheader = (TextView) lSectionHeader.findViewById(R.id.lblStage);
		txtheader.setText(getSections()[getSectionForPosition(position)]);
		txtheader.setTextColor(alpha << 24 | (0x000000));
//		txtheader.setBackgroundColor(alpha << 24 | (0xD8E2BC));
//		String title = getSections()[getSectionForPosition(position)];
//		if (!title.equals(context.getString(R.string.stage1))){
//			lSectionHeader.setVisibility(View.VISIBLE);
//		}else{
//			lSectionHeader.setVisibility(View.GONE);
//		}
	}

	@Override
	public int getPositionForSection(int section) {
		if (section < 0) section = 0;
		if (section >= all.size()) section = all.size() - 1;
		int c = 0;
		for (int i = 0; i < all.size(); i++) {
			if (section == i) { 
				return c;
			}
			c += all.get(i).second.size();
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		int c = 0;
		for (int i = 0; i < all.size(); i++) {
			if (position >= c && position < c + all.get(i).second.size()) {
				return i;
			}
			c += all.get(i).second.size();
		}
		return -1;
	}

	@Override
	public String[] getSections() {
		String[] res = new String[all.size()];
		for (int i = 0; i < all.size(); i++) {
			res[i] = all.get(i).first;
		}
		return res;
	}
}
