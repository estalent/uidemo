package com.yunos.tv.yingshi.fragment;

import yunos.tv.AuiResourceFetcher;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aliyun.base.BaseFragment;
import com.aliyun.base.info.BaseAppInfo;
import com.aliyun.imageload.ImageLoader;
import com.yunos.tv.yingshi.YingshiApplication;
import com.yunos.tv.yingshi.widget.TabManager;
import com.yunos.tv.yingshi.widget.TvLoadingAlert;
import com.yunos.tv.yingshi.widget.ZixunLoadingAlert;

public class BaseTVFragment extends BaseFragment implements TabListener, FragmentOnKeyListener {
	
	private int mLayoutResId;
	private int mContainerViewId;
	protected String mTag;
	private Context mContext;
	private boolean mRequestFocus = false;
	
	/**
	 * 请求完结果之后吸取焦点
	 * @param isReqeustFocus
	 */
	public void setIsRequestFocus(boolean isReqeustFocus) {
		mRequestFocus = isReqeustFocus;
	}
	
	protected boolean isRequestFocus() {
		return this.mRequestFocus;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public BaseTVFragment() {
		logd("yinghshi BaseTVFragment -- BaseTVFragment() --  tag:" + mTag);
	}
	
	public BaseTVFragment(int layoutResId, int containerViewId, String tag, Context context) {
		logd("yinghshi BaseTVFragment -- BaseTVFragment(2) --  tag:" + tag);
		this.mLayoutResId = layoutResId;
		this.mContainerViewId = containerViewId;
		this.mTag = tag;
		this.mContext = context;
		Bundle args = new Bundle();
		args.putInt("layout_id", layoutResId);
		args.putInt("container_id", containerViewId);
		args.putString("tag_name", tag);
		this.setArguments(args);
	}
	
	public int getContainerViewId() {
		return this.mContainerViewId;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logd("yinghshi BaseTVFragment -- onCreate --  tag:" + mTag);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		logd("yinghshi BaseTVFragment -- onAttach --  tag:" + mTag);
		this.mContext = activity;
	}
	
	@Override
	public void onDestroy() {
		logd("yinghshi BaseTVFragment -- onDestroy --  tag:" + mTag);
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		logd("yinghshi BaseTVFragment -- onCreateView --  tag:" + mTag + ", mLayoutResId:" + mLayoutResId + ",container"+container);
		if (savedInstanceState != null) {
			mLayoutResId = savedInstanceState.getInt("layout_id");
			mTag = savedInstanceState.getString("tag_name");
			mContainerViewId = savedInstanceState.getInt("container_id");
		}
		logd("yinghshi BaseTVFragment -- onCreateView 2--  tag:" + mTag + ", mLayoutResId:" + mLayoutResId);
		
		if (mLayoutResId == 0) {
			logd("yinghshi BaseTVFragment -- onCreateView 3 -- mLayoutResId==0");
			Activity activity = (Activity) getContext();
			activity.finish();
			return null;
		}
		
		inflater = AuiResourceFetcher.getLayoutInflater(YingshiApplication.getApplication().getApplicationContext());
		return inflater.inflate(mLayoutResId, container, false);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		logd("yinghshi BaseTVFragment -- onTabReselected --  tag:" + mTag);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		logd("yinghshi BaseTVFragment -- onTabSelected --  tag:" + mTag);
		TabManager.instance().setCurrentTab(tab);
		
		FragmentManager fm = getFragmentManager();
		if (fm != null) {
			Fragment f = fm.findFragmentByTag(mTag);
			if (f == null) {
				ft.add(mContainerViewId, this, mTag);
				ft.show(this);
			} else {
				ft.show(this);
			}
		} else {
			ft.add(mContainerViewId, this, mTag);
			ft.show(this);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		logd("yinghshi BaseTVFragment -- onTabUnselected --  tag:" + mTag);
		ft.hide(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putAll(getArguments());
	}
	
	protected ZixunLoadingAlert mZixunLoading;
	
	public void showZixunLoading() {
		showZixunLoading("");
	}

	public void showZixunLoading(final String message) {
		if (mZixunLoading == null){
			mZixunLoading = new ZixunLoadingAlert(getActivity());
		}
		mZixunLoading.setMessage(message);
		mZixunLoading.setVisibility(View.VISIBLE);
	}

	public void hideZixunLoading() {
		if (mZixunLoading != null) {
			mZixunLoading.setVisibility(View.GONE);
		}
	}
	
	///////////////////
	protected TvLoadingAlert mLoading;
	
	public void showLoading() {
		showLoading(null);
	}
	
	public void showLoading(final String message) {
		if (mLoading == null) {
			mLoading = new TvLoadingAlert(getActivity());
		}
		mLoading.showLoading(message);
	}
	
	public void hideLoading() {
		if (mLoading == null) {
			mLoading = new TvLoadingAlert(getActivity());
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
	
	protected void logd(String msg) {
		if (BaseAppInfo.isDebug) {
			Log.d(getLogTag(), msg);
		}
	}
	
	protected String getLogTag() {
		return "yingshi";
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}
