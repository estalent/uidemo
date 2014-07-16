package com.yunos.tv.yingshi.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.yunos.tv.yingshi.R;

public class TreatyDialog extends Dialog {
	
	private Button mButton;
	
	public OnTreatyOkLinstener mOnTreatyOkLinstener;
	
	public void setmOnTreatyOkLinstener(OnTreatyOkLinstener onTreatyOkLinstener) {
		this.mOnTreatyOkLinstener = onTreatyOkLinstener;
	}

	public TreatyDialog(Context context) {
		super(context, R.style.treaty_dialog);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		setContentView(R.layout.dialog_treaty);
		
		mButton = (Button) findViewById(R.id.text_ok);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnTreatyOkLinstener != null) {
					mOnTreatyOkLinstener.click();
				}
				dismiss();
			}
		});

		setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				mButton.requestFocus();
			}
		});
	}
	
	public interface OnTreatyOkLinstener {
		public void click();
	}
}
