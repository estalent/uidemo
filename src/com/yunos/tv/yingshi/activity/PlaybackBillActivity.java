package com.yunos.tv.yingshi.activity;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import yunos.tv.app.LeftNavBar;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.base.net.NetConnectionType;
import com.aliyun.base.net.NetworkManager;
import com.aliyun.base.net.NetworkManager.INetworkListener;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.BillDay;
import com.yunos.tv.source.entity.BillMonth;
import com.yunos.tv.source.entity.ChannelBill;
import com.yunos.tv.source.entity.ChannelInfo;
import com.yunos.tv.source.entity.ChannelInfoList;
import com.yunos.tv.yingshi.BaseTvActivity;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.exception.SourceException;
import com.yunos.tv.yingshi.fragment.PlaybackBillFragment;
import com.yunos.tv.yingshi.widget.TabManager;

public class PlaybackBillActivity extends BaseTvActivity{
	public static final String TAG = "PlaybackBillActivity";

	private LeftNavBar mLeftNavBar;
	private int mChannelKey;//频道ID
	public String mPlayBackUrls;//请求播放器播放参数
	private String playBackChannel;//频道的名字
	private String playBackLogo;//频道的图片logo
	
	private WorkAsyncTask<Object> mTask;
	private Object mSyncForLoading = new Object();
	private boolean mIsLoading = false;
	
	private INetworkListener mINetworkListener = new INetworkListener() {
		public void onNetworkChanged(boolean isConnected, NetConnectionType lastType, NetConnectionType currentType) {
			if (!isConnected) {
				showToast(R.string.network_config_message);	
				finish();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NetworkManager.instance().registerStateChangedListener(mINetworkListener);
		setContentView(R.layout.yingshi);
		if (!NetworkManager.isNetworkAvailable(this)) {
			showToast(R.string.network_config_message);	
			finish();
			return;
		}
		
		mChannelKey = getIntent().getIntExtra("channel_key", 0);
		playBackChannel = getIntent().getStringExtra("channel_name");
		playBackLogo = getIntent().getStringExtra("channel_img");
		boolean fromHomeshell = getIntent().getBooleanExtra("from_homeshell", false);
		
		//如果从桌面点进来的某电视台，则需要获取一下所有电视台信息。每次点进来都要取。
		if (fromHomeshell) {
			logi("======fromHomeshell   true============");
			loadPlayBackUrlsData();
		} else {
			logi("======fromHomeshell   false============");
			mPlayBackUrls = getIntent().getStringExtra("playBackUrls");
			loadData();
		}
	}
	
	@Override
	protected void onDestroy() {
		NetworkManager.instance().unregisterStateChangedListener(mINetworkListener);
		TabManager.instance().reset();
		super.onDestroy();
	}
	
	private void loadData() {
		runTask(new WorkAsyncTask<ChannelBill>(this) {
			@Override
			public void onPre() throws Exception {
				showLoading();
			}
			
			@Override
			public ChannelBill doProgress() throws Exception {
				ChannelBill bill = SourceWasu.getPlaybackBill(mChannelKey);
				return bill;
			}
			
			@Override
			public void onPost(boolean isSuccess, ChannelBill bill) throws Exception {
				hideLoading();
				if (isSuccess) {
					setupTabs(bill);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (mLeftNavBar != null && !mLeftNavBar.handleBackPress()) {
			super.onBackPressed();
		}
	}

	private LeftNavBar getLeftNavBar() {
		if (mLeftNavBar == null) {
			mLeftNavBar = new LeftNavBar(this, LeftNavBar.TYPE_TOP_NAVBAR);
			mLeftNavBar.setOnClickHomeListener(new View.OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}
		return mLeftNavBar;
	}
	
	private void setupTabs(ChannelBill bill) {
		if (bill == null || bill.months.size() == 0) {
			showToast(R.string.null_playbackbill);
			finish();
			return;
		}

		LeftNavBar bar = getLeftNavBar();
		bar.removeAllTabs();
		bar.setLogo(R.drawable.tui_ic_huashulogo);
		TabManager.instance().setActionBar(bar);
		
		boolean isFirst = true;
		int tabIndex = 0;
		for (int i = 0, len = bill.months.size(); i < len; ++i) {
			BillMonth cb = bill.months.get(i);
			for (int j = 0, len2 = cb.days.size(); j < len2; ++j) {
				BillDay bd = cb.days.get(j);
				String tabName = null;
				if (i == 0 && j == 0) {
					Calendar c = Calendar.getInstance();
				    c.setTime(new Date());
				    int m = c.get(Calendar.MONTH);
				    int d = c.get(Calendar.DAY_OF_MONTH);
					if (cb.month == (m+1) && bd.day == d) {
						tabName = getString(R.string.today);
					}
				}
				if (tabName == null) {
					tabName = cb.month + getString(R.string.month) + bd.day + getString(R.string.day);
				}
				ActionBar.Tab tab = bar.newTab().setText(tabName);
				PlaybackBillFragment pbf = PlaybackBillFragment.newInstance(R.id.yingshi_main, tabName, PlaybackBillActivity.this, 
						this.mChannelKey, this.playBackChannel);
				pbf.setChannelInfoList(mPlayBackUrls, playBackLogo);
				pbf.setBillItemList(bd.bills);
				tab.setTabListener(pbf);
				bar.addTab(tab, isFirst);
				TabManager.instance().add(tabIndex++, tab);
				if (isFirst) {
					isFirst = false;
				}
			}
		}
		bar.handleBackPress();
	}
	
	@Override
	protected String getLogTag() {
		return TAG;
	}
	
	//---拼凑playBackUrls的task
	private void loadPlayBackUrlsData() {
		synchronized (mSyncForLoading) { 
			if (mIsLoading) {
				return;
			}
			mIsLoading = true;
		}
		mTask = new PlaybackWorkAsyncTask(this);
		runTask(mTask);
	}
	
	class PlaybackWorkAsyncTask extends WorkAsyncTask<Object> {
		public PlaybackWorkAsyncTask(Context context) {
			super(context);
		}

		public void onPre() throws Exception {
			showLoading();
		}
		
		public Object doProgress() throws Exception {
			if (isCancelled()) {
				logd("doProgress -- isCancelled");
				return null;
			} 
			ChannelInfoList channelList = null;
		    channelList = SourceWasu.getPlayBackChannelList("");
		    if (channelList != null && channelList.data != null) {
				JSONArray array = new JSONArray();
				for (ChannelInfo info : channelList.data) {
					JSONObject json = new JSONObject();
					json.put("channelKey", info.channelKey);
					json.put("channelName", info.channelName);
					logi("======channelName==========" + info.channelName);
					json.put("playUrl", info.playBackUrl);
					json.put("playBackBill", info.playBackBill);
					array.put(json);
				}
				mPlayBackUrls = array.toString();
			}
			
			return channelList;
		}
		
		public void onPost(boolean resultState, Object obj) throws Exception {
			hideLoading();
			synchronized (mSyncForLoading) {
				mIsLoading = false;
			}
			loadData();
		}

		@Override
		public void onError(Exception e) {
			if (e instanceof SourceException) {
				SourceException ex = (SourceException) e;
				Toast.makeText(getContext(), ex.getErrorCode().getMessage(), Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onCancel(boolean cancel) {
			super.onCancel(cancel);
			logd("PlaybackWorkAsyncTask -- onCancel");
			if (!cancel) {
				loge("onCancel -- !isSuccess");
			}

			synchronized (mSyncForLoading) {
				mIsLoading = false;
			}
		}
	}
	
}
