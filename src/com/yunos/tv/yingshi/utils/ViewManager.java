package com.yunos.tv.yingshi.utils;

import java.util.HashMap;
import java.util.Map;

import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.YingshiApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ViewManager {

	private static ViewManager viewManager = null;

	public synchronized static ViewManager instance() {
		if (null == viewManager) {
			viewManager = new ViewManager();
		}

		return viewManager;
	}

	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private int maxViewNum = 0;

	public void setMaxViewNum(int num) {
		this.maxViewNum = num;
	}

	public int getMaxViewNum() {
		return this.maxViewNum;
	}

	public void init(int resLayoutId) {
		if (viewMap != null && viewMap.isEmpty()) {
			LayoutInflater inflater = (LayoutInflater) YingshiApplication.getApplication().getApplicationContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			for (int index = 0; index < this.maxViewNum; index++) {
				View v = inflater.inflate(resLayoutId, null);
				viewMap.put(index, v);
			}
		}
	}

	public View get(int key) {
		return this.viewMap.get(key);
	}
	
	public void clear() {
		this.viewMap.clear();
	}
	
	public int getSize() {
		return this.viewMap.size();
	}
}
