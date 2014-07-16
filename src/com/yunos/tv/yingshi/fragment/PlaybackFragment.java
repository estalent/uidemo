package com.yunos.tv.yingshi.fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.base.net.NetConnectionType;
import com.aliyun.base.net.NetworkManager;
import com.aliyun.base.net.NetworkManager.INetworkListener;
import com.yunos.tv.app.widget.FocusedBasePositionManager;
import com.yunos.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tv.app.widget.FocusedGridView;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.ChannelInfo;
import com.yunos.tv.source.entity.ChannelInfoList;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.activity.PlaybackActivity;
import com.yunos.tv.yingshi.activity.PlaybackBillActivity;
import com.yunos.tv.yingshi.adapter.PlaybackGridAdapter;
import com.yunos.tv.yingshi.exception.SourceException;

public class PlaybackFragment extends BaseTVFragment {
	public static final String TAG = "PlaybackFragment";

	private View mNodataView, mContentView;
	private FocusedGridView mGridView;
	private PlaybackGridAdapter mAdapter;
	private WorkAsyncTask<ChannelInfoList> mTask;
	private boolean mIsLoading = false;
	private Object mSyncForLoading = new Object();
	private String mChannelType;//频道类型：（默认null或者""全部，1：本地，2：央视，3：卫视）

	public PlaybackFragment() {
	}

	public PlaybackFragment(int containerViewId, String tag, String channelType, Context context) {
		super(R.layout.frag_playback, containerViewId, tag, context);
		this.mChannelType = channelType;
	}

	public static PlaybackFragment newInstance(int containerViewId, String tag, String channelType, Context context) {
		PlaybackFragment playbackFragment = new PlaybackFragment(containerViewId, tag, channelType, context);
		return playbackFragment;
	}

	private INetworkListener mINetworkListener = new INetworkListener() {
		public void onNetworkChanged(boolean isConnected, NetConnectionType lastType, NetConnectionType currentType) {
			logd("onNetworkChanged -- connected = " + isConnected + " -- lastType = " + lastType + " -- currentType = " + currentType);
			if (isConnected && NetConnectionType.none == lastType) {
				if (mAdapter != null && mAdapter.isEmpty()) {
					logd("onNetworkChanged -- conne cted -- loadData");
					loadData();
				}
			}
			if (!isConnected) {
				if (mTask != null && !mTask.isCancelled() && mTask.getStatus() != AsyncTask.Status.FINISHED) {
					logd("onNetworkChanged -- !connected -- cancel task");
					mTask.cancel(true);
					mTask = null;
				}
			}
		}
	};

	// 设置跑马灯的效果
	private FocusItemSelectedListener mOnItemSelectedListener = new FocusItemSelectedListener() {
		public void onItemSelected(View v, int position, boolean isSelected, View parent) {
			AdapterView adapterView = (AdapterView) parent;
			if (v == null || adapterView.getChildCount() == 0) {
				return;
			}
			TextView channelNameTV = (TextView) v.findViewById(R.id.channel_name);
			if (isSelected) {
				channelNameTV.setSelected(true);
				channelNameTV.setSingleLine(true);
				channelNameTV.setEllipsize(TruncateAt.MARQUEE);
//				channelNameTV.startMarquee();
			} else {
				channelNameTV.setSelected(false);
				channelNameTV.setSingleLine(false);
				channelNameTV.setEllipsize(TruncateAt.END);
//				channelNameTV.stopMarquee();
			}
		}
	};

	private OnItemClickListener mOnItemClickListener = new TvOnItemClickListener() {
		public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
			ChannelInfo channel = mAdapter.getItem(position);
			Intent playbackBill = new Intent(getContext(), PlaybackBillActivity.class);
			playbackBill.putExtra("channel_key", channel.channelKey);
			playbackBill.putExtra("channel_name", channel.channelName);
			playbackBill.putExtra("channel_img", channel.channelLogo);
			// playbackBill.putParcelableArrayListExtra("channelList",
			// (ArrayList<ChannelInfo>)mAdapter.getChannelList());
			PlaybackActivity activity = (PlaybackActivity) getContext();
			playbackBill.putExtra("playBackUrls", activity.getPlayBackUrls());
			startActivity(playbackBill);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NetworkManager.instance().registerStateChangedListener(mINetworkListener);
	}

	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		super.onViewCreated(rootView, savedInstanceState);
		mNodataView = rootView.findViewById(R.id.nodata);
		mContentView = rootView.findViewById(R.id.playback_content);

		float scaleValue = 1.1f;
		mGridView = (FocusedGridView) rootView.findViewById(R.id.playback_grid);
		mGridView.setFrameRate(Config.FRAME_GRIDVIEW);
		mGridView.setScrollDuration(900);
		mGridView.setFocusMode(FocusedBasePositionManager.FOCUS_SYNC_DRAW);
		mGridView.setItemScaleValue(scaleValue, scaleValue);
		mGridView.setFocusResId(R.drawable.tui_bg_focus);
		mGridView.setFocusShadowResId(R.drawable.tui_grid_focus);
		mGridView.setFocusViewId(R.id.channel_lay);
		mAdapter = new PlaybackGridAdapter(getContext());
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemSelectedListener(mOnItemSelectedListener);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		loadData();
	}

	private void setNodataView() {
		if (mAdapter.getChannelList().isEmpty()) {
			mContentView.setVisibility(View.GONE);
			mNodataView.setVisibility(View.VISIBLE);
		} else {
			mNodataView.setVisibility(View.GONE);
			mContentView.setVisibility(View.VISIBLE);
		}
	}

	// @Override
	// public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	// super.onTabUnselected(tab, ft);
	// if (mTask != null && mIsLoading) {
	// logd("onTabUnselected -- cancel task");
	// mTask.cancel(true);
	// hideLoading();
	// mTask = null;
	// }
	// }
	//
	// @Override
	// public void onTabSelected(Tab tab, FragmentTransaction ft) {
	// super.onTabSelected(tab, ft);
	// if (mAdapter != null && mAdapter.getChannelList().isEmpty()) {
	// loadData();
	// }
	// }

	@Override
	protected String getLogTag() {
		return TAG;
	}

	private void loadData() {
		synchronized (mSyncForLoading) {
			if (mIsLoading) {
				return;
			}
			mIsLoading = true;
		}
		mTask = new PlaybackWorkAsyncTask(getContext());
		runTask(mTask);
	}

	class PlaybackWorkAsyncTask extends WorkAsyncTask<ChannelInfoList> {
		public PlaybackWorkAsyncTask(Context context) {
			super(context);
		}

		public void onPre() throws Exception {
			mNodataView.setVisibility(View.GONE);
			showLoading();
		}

		public ChannelInfoList doProgress() throws Exception {
			if (isCancelled()) {
				logd("doProgress -- isCancelled");
				return null;
			}
			ChannelInfoList channelList = null;
			if (mChannelType.equals("1") || mChannelType.equals("2") || mChannelType.equals("3")) {
				channelList = SourceWasu.getPlayBackChannelList(mChannelType);
			} else {
				//多个fragment，只需要第一个fragment准备全部频道信息。
				channelList = SourceWasu.getPlayBackChannelList("");
				if (channelList != null && channelList.data != null) {
					JSONArray array = new JSONArray();
					for (ChannelInfo info : channelList.data) {
						JSONObject json = new JSONObject();
						json.put("channelKey", info.channelKey);
						json.put("channelName", info.channelName);
						json.put("playUrl", info.playBackUrl);
						json.put("playBackBill", info.playBackBill);
						array.put(json);
					}
					PlaybackActivity activity = (PlaybackActivity) getContext();
					activity.setPlayBackUrls(array.toString());
				}
			}

			return channelList;
		}

		public void onPost(boolean resultState, ChannelInfoList pList) throws Exception {
			hideLoading();
			if (pList == null) {
				setNodataView();
				return;
			}

			if (resultState) {
				mAdapter.setChannelList(pList.data);
				mAdapter.notifyDataSetChanged();
			}

			synchronized (mSyncForLoading) {
				mIsLoading = false;
			}
			setNodataView();
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
			hideLoading();

			synchronized (mSyncForLoading) {
				mIsLoading = false;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		NetworkManager.instance().unregisterStateChangedListener(mINetworkListener);
		mAdapter.clear();
		mAdapter = null;
	}

}