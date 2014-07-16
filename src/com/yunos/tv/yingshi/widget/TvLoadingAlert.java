package com.yunos.tv.yingshi.widget;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.yingshi.R;

public class TvLoadingAlert {
	private LinearLayout mLoading;
	
	public TvLoadingAlert(Activity activity) {
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLoading = (LinearLayout) inflater.inflate(R.layout.progress1, null);
		LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lay.gravity = Gravity.CENTER;
		activity.addContentView(mLoading, lay);
	}

	public void showLoading() {
		showLoading(null);
	}
	
	public void showLoading(final String message) {
		TextView text = (TextView) mLoading.findViewById(R.id.text);
		if (message != null) {
			text.setVisibility(View.VISIBLE);
			text.setText(message);
		} else {
			text.setVisibility(View.GONE);
		}
		mLoading.setVisibility(View.VISIBLE);
	}
	
	public void hideLoading() {
		if (mLoading != null) {
			mLoading.setVisibility(View.GONE);
		}
	}

}
