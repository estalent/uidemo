package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.GridView;

public class GridViewEx extends GridView {

	public GridViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GridViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GridViewEx(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int curPos = getSelectedItemPosition();
		int count = getCount();
		Log.i("aabb", "=======curPos:" + curPos);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (curPos < count - 1) {
				this.setSelection(curPos + 1);
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (curPos > 0) {
				this.setSelection(curPos - 1);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
