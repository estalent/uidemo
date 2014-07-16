package com.yunos.tv.yingshi.widget;

import com.yunos.tv.yingshi.utils.LayoutUtils;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ZixunLoadingAlert extends FrameLayout {
	private TextView mText;

	public ZixunLoadingAlert(Activity activity) {
		super(activity);
		createView(activity);
		LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lay.gravity = Gravity.CENTER;
		activity.addContentView(ZixunLoadingAlert.this, lay);
	}

	public void createView(Activity context) {
		LinearLayout view = new LinearLayout(context);
		LayoutParams viewLay = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		viewLay.gravity = Gravity.CENTER;
		view.setLayoutParams(viewLay);
		view.setPadding((int)LayoutUtils.dipTopx(context, 10), (int)LayoutUtils.dipTopx(context, 20), (int)LayoutUtils.dipTopx(context, 10), (int)LayoutUtils.dipTopx(context, 20));
		view.setGravity(Gravity.RIGHT);
		view.setBackgroundColor(Color.TRANSPARENT);
		view.setOrientation(LinearLayout.HORIZONTAL);
		
		ProgressBar progress = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
//		progress.setProgressDrawable(new ColorDrawable(Color.YELLOW));
		LinearLayout.LayoutParams parpmProgress = new LinearLayout.LayoutParams((int)LayoutUtils.dipTopx(context, 70), (int)LayoutUtils.dipTopx(context, 70));
		parpmProgress.setMargins((int)LayoutUtils.dipTopx(context, 600), 0, 0, 0);
		parpmProgress.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		progress.setLayoutParams(parpmProgress);
		view.addView(progress);
		
		mText = new TextView(context);
		mText.setTextColor(Color.BLACK);
		mText.setText("");
		LinearLayout.LayoutParams paramTextView = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		view.addView(mText);

		ZixunLoadingAlert.this.addView(view);
	}

	public void setMessage(String message) {
		if(message == null){
			mText.setText("");
		}else{
			mText.setText(message);
		}
		
	}

}
