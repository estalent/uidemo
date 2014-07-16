package com.yunos.tv.yingshi.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.source.entity.BillItem;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.fragment.PlaybackBillFragment;

public class PlaybackListViewAdapter extends BaseAdapter {
	public List<BillItem> mProgramList;
	private LayoutInflater mInflater;
	private static String mTime;
	public static long duringTime;
	private SimpleDateFormat dateformat;
	
	public void setTime(String time){
		if(mTime != null && !mTime.equals("")){
			mTime = time;
			duringTime = Long.valueOf(mTime) - System.currentTimeMillis();
		}
	}
	
	public PlaybackListViewAdapter(Context context){
		dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setList(List<BillItem> programList){
		mProgramList = programList;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		 return (mProgramList != null) ? mProgramList.size() : 0 ;
	}
	
	@Override
	public BillItem getItem(int position) {
		return (mProgramList != null && mProgramList.size()>0) ? mProgramList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView timeTV;
		TextView nameTV;
		TextView hasPlayTV;
		if (convertView == null) { 
			convertView = (LinearLayout) mInflater.inflate(R.layout.frag_playback_bill_item, null);
		} 
		timeTV = (TextView) convertView.findViewById(R.id.program_time);
		nameTV = (TextView) convertView.findViewById(R.id.program_name);
		hasPlayTV = (TextView) convertView.findViewById(R.id.program_hasplay);
		if(mProgramList != null){
			BillItem bill = mProgramList.get(position);
			timeTV.setText(bill.value.substring(0, 5));
			nameTV.setText(bill.name);
			
			Date endTime, currentTime;
			Log.d("xxx","bill.startTime="+bill.startTime+",endTime="+bill.endTime+ "----duringTime=" + duringTime);
		    try {
		    	if(mTime == null || mTime.equals("")){
					currentTime = new Date();
				}else{
					currentTime = new Date(Long.valueOf(mTime));
				}
		    	endTime = dateformat.parse(bill.endTime);
		    	if(currentTime.getTime() - endTime.getTime() > PlaybackBillFragment.huikanDelayTime){
		    		hasPlayTV.setText("");
		    		hasPlayTV.setVisibility(View.GONE);
		    	} else {
		    		hasPlayTV.setText("等候播出");
			    	hasPlayTV.setVisibility(View.VISIBLE);
		    	}
		    /*	startTime = dateformat.parse(bill.startTime);
		    	endTime = dateformat.parse(bill.endTime);
		    	if(currentTime.compareTo(startTime) < 0){
		    		hasPlayTV.setText("  未播出");
			    } else if(currentTime.compareTo(endTime) < 0){
			    	hasPlayTV.setText("正在播出");
			    } else {
			    	hasPlayTV.setText("已经播出");
			    }*/
		    	/*startTime = dateformat.parse(bill.startTime);
		    	endTime = dateformat.parse(bill.endTime);
		    	if(currentTime.compareTo(startTime) < 0){
		    		hasPlayTV.setText("  未播出");
			    } else if(currentTime.compareTo(endTime) < 0){
			    	hasPlayTV.setText("正在播出");
			    } else {
			    	if(currentTime.getTime() - endTime.getTime() < PlaybackBillFragment.huikanDelayTime){
			    		hasPlayTV.setText("稍后观看");
			    	}else{
			    		hasPlayTV.setText("已经播出");
			    	}
			    }*/
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}
}
