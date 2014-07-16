package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class DetailButton extends RelativeLayout {

	public DetailButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DetailButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DetailButton(Context context) {
		super(context);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
//		Log.i("aabb", "========Log.i(tag, msg);==========gainFocus=======" + gainFocus);
//		int count = getChildCount();
//		for (int i = 0; i < count; ++i) {
//			View child = getChildAt(i);
//			child.setSelected(gainFocus);
//		}
//		TextView t1 = (TextView) findViewById(R.id.detail_button_text1);
//		TextView t2 = (TextView) findViewById(R.id.detail_button_text2);
//		ImageView icon = (ImageView) findViewById(R.id.detail_button_icon1);
//		if (t1 != null) {
//			t1.setSelected(gainFocus);
//		}
//		if (t2 != null) {
//			t2.setSelected(gainFocus);
//		}
//		if (icon != null) {
//			icon.setSelected(gainFocus);
//		}
	}

}
