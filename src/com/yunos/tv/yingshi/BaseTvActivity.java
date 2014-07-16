package com.yunos.tv.yingshi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.aliyun.base.BaseActivity;
import com.aliyun.base.info.BaseAppInfo;
import com.yunos.tv.yingshi.widget.TreatyDialog;
import com.yunos.tv.yingshi.widget.TreatyDialog.OnTreatyOkLinstener;
import com.yunos.tv.yingshi.widget.TvLoadingAlert;


public class BaseTvActivity extends BaseActivity {
	protected TvLoadingAlert mLoading;
	
	protected TreatyDialog mTreatyDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final SharedPreferences share = this.getSharedPreferences(this.getPackageName(), 0);
		boolean b = share.getBoolean("first_lunch", true); //默认第一次启动
		if (b) { 
			if (mTreatyDialog != null) {
				mTreatyDialog.dismiss();
				mTreatyDialog = null;
			}
			mLoading = new TvLoadingAlert(this);
			mTreatyDialog = new TreatyDialog(this);
			mTreatyDialog.setmOnTreatyOkLinstener(new OnTreatyOkLinstener() {
				
				@Override
				public void click() {
					share.edit().putBoolean("first_lunch", false).commit();
				}
			});
			mTreatyDialog.setCancelable(false);
			mTreatyDialog.show();
		}
	}
	
	public void hideDialog() {
		if (mTreatyDialog != null) {
			mTreatyDialog.dismiss();
			mTreatyDialog = null;
		}
	}
	
	public void showLoading() {
		showLoading(null);
	}
	
	public void showLoading(final String message) {
		if (mLoading == null) {
			mLoading = new TvLoadingAlert(this);
		}
		mLoading.showLoading(message);
	}
	
	public void hideLoading() {
		if (mLoading == null) {
			mLoading = new TvLoadingAlert(this);
		}
		mLoading.hideLoading();
	}
	
	protected void logi(String msg) {
		if (BaseAppInfo.isDebug) {
			Log.i(getLogTag(), msg);
		}
	}
	
	protected void loge(String msg) {
		if (BaseAppInfo.isDebug) {
			Log.e(getLogTag(), msg);
		}
	}
	
	protected void loge(String msg, Throwable tr) {
		if (BaseAppInfo.isDebug) {
			Log.e(getLogTag(), msg, tr);
		}
	}
	
	protected void logd(String msg) {
		if (BaseAppInfo.isDebug) {
			Log.d(getLogTag(), msg);
		}
	}
	
	protected String getLogTag() {
		return "yingshi";
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (BaseAppInfo.isAppBackRun(getApplicationContext())) {
			YingshiApplication app = (YingshiApplication) getApplication();
			app.hideDialog();
		}
		hideDialog();
	}
	
	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		YingshiApplication app = (YingshiApplication) getApplication();
		app.hideDialog();
		hideDialog();
	}
	
//	//  设置完成回调
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//            case Const.REQUEST_CODE_NET_SETTING:
//                checkUpdate();
//                break;
//            default:
//                break;
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//	
//	protected void checkUpdate() {}
}
