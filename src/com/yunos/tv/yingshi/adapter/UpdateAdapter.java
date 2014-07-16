package com.yunos.tv.yingshi.adapter;

import android.view.View;
import android.widget.BaseAdapter;

public abstract class UpdateAdapter extends BaseAdapter {
	
	protected boolean isPreLoad = true; //是否需要预加载
	
	public UpdateAdapter(boolean isPreLoad) {
		this.isPreLoad = isPreLoad;
	}

	protected boolean mHasCoverFlow = false;

	public abstract void updateView(int position, View convertView);
	
	public boolean hasCoverFlow() {
		return mHasCoverFlow; 
	}

	public void setHasCoverFlow(boolean hasCoverFlow) {
		this.mHasCoverFlow = hasCoverFlow;
	}
}
