package com.yunos.tv.yingshi.adapter;

import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.aidl.Catalog;

public class ZixunListViewAdapter extends BaseAdapter {
	private List<Catalog> mCatalogList = null;
	public List<Program> mProgramList = null;
	private LayoutInflater mInflater;
	
	public ZixunListViewAdapter(Context context){
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setList(List<Catalog> catalogList, List<Program> programList){
		mCatalogList = catalogList;
		mProgramList = programList;
		notifyDataSetChanged();
	}
	
	public List<Program> getProgramList() {
		return mProgramList;
	}
	
	public void addProgramList(List<Program> programList){
		if(mProgramList!=null){
			mProgramList.addAll(programList);
		} else {
			mProgramList = programList;
		}
		notifyDataSetChanged();
	}
	
	public int getCount() {
		if ( mCatalogList != null ){
			return mCatalogList.size();
		} else if ( mProgramList != null ){
			return mProgramList.size();
		}
		return 0;
	}
	
	@Override
	public Object getItem(int position) {
		if (position >= 0 && position < getCount()){
			if (mProgramList != null){
				Log.i("ZixunFragment", "mProgramList, position:" + position);
				return mProgramList.get(position);
			} else if (mCatalogList != null){
				Log.i("ZixunFragment", "mCatalogList, position:" + position);
				return mCatalogList.get(position);
			}
		}
		return null;
	}
	
	public void clear(){
		if(mProgramList != null){
			mProgramList.clear();
		}
		
		if(mCatalogList != null){
			mCatalogList.clear();
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		ImageView imageView;
		if (convertView == null) {
			convertView = (LinearLayout) mInflater.inflate(R.layout.frag_zixun_item, null);
		} 
		textView = (TextView) convertView.findViewById(R.id.InfoListItemTitle);
		imageView = (ImageView) convertView.findViewById(R.id.InfoListItemArrow);
		if(mProgramList != null){
			/*textView.setText("[" + mProgramList.get(position).nodeName + "] " + mProgramList.get(position).name);*/
			textView.setText(mProgramList.get(position).name);
			imageView.setVisibility(View.GONE);
		}else if (mCatalogList != null ){
			textView.setText(mCatalogList.get(position).name);
			imageView.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

}
