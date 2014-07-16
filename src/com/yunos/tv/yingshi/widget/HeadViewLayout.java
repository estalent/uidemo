package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.yunos.tv.app.widget.CoverFlow;
import com.yunos.tv.yingshi.R;

public class HeadViewLayout extends LinearLayout {

	public HeadViewLayout(Context context) {
		super(context);
	}

	public HeadViewLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HeadViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		CoverFlow coverFlow = (CoverFlow) findViewById(R.id.yingshi_coverflow);
		if (coverFlow == null) {
			return super.onKeyUp(keyCode, event);
		}
		boolean b = coverFlow.onKeyUp(keyCode, event);
		return b;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		CoverFlow coverFlow = (CoverFlow) findViewById(R.id.yingshi_coverflow);
		if (coverFlow == null) {
			return super.onKeyDown(keyCode, event);
		}
		return coverFlow.onKeyDown(keyCode, event);
	}

}
