package com.yunos.tv.yingshi.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.base.net.NetConnectionType;
import com.aliyun.base.net.NetworkManager;
import com.aliyun.base.net.NetworkManager.INetworkListener;
import com.yunos.tv.app.widget.FocusedBasePositionManager;
import com.yunos.tv.app.widget.FocusedGridView;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.ChannelInfo;
import com.yunos.tv.source.entity.Kanba;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.adapter.KanbaGridAdapter;
import com.yunos.tv.yingshi.exception.SourceException;

public class KanbaFragment extends BaseTVFragment {
	public static final String TAG = "KanbaFragment";

	private View mNodataView, mContentView;
	private FocusedGridView mGridView;
	private KanbaGridAdapter mAdapter;
	private WorkAsyncTask<Kanba> mTask;
	private boolean mIsLoading = false;
	private Object mSyncForLoading = new Object();
	private String mPalyList;

	public KanbaFragment() {
	}

	public KanbaFragment(int containerViewId, String tag, String channelType, Context context) {
		super(R.layout.frag_playback, containerViewId, tag, context);
	}

	public static KanbaFragment newInstance(int containerViewId, String tag, String channelType, Context context) {
		KanbaFragment playbackFragment = new KanbaFragment(containerViewId, tag, channelType, context);
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
		mGridView.setScrollDuration(Config.SCROLLING_DURATION_GRIDVIEW);
		mGridView.setFocusMode(FocusedBasePositionManager.FOCUS_SYNC_DRAW);
		mGridView.setItemScaleValue(scaleValue, scaleValue);
		mGridView.setFocusResId(R.drawable.tui_bg_focus);
		mGridView.setFocusShadowResId(R.drawable.tui_grid_focus);
		mGridView.setFocusViewId(R.id.channel_lay);
		mAdapter = new KanbaGridAdapter(getContext());
		mGridView.setAdapter(mAdapter);
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
	
	
	private OnItemClickListener mOnItemClickListener = new TvOnItemClickListener() {
		public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
			ChannelInfo channel = mAdapter.getItem(position);
			JSONObject playInfo = new JSONObject();
			try {
				playInfo.put("currentChannel", String.valueOf(channel.channelKey));
				JSONArray listUrl = new JSONArray(mPalyList);
				playInfo.put("playBackUrls", listUrl);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			Intent intent = new Intent("com.wasuali.action.kanbaplayer");
			intent.putExtra("playInfo", playInfo.toString());
			logi("playJson:" + playInfo.toString());
			startActivity(intent);
		}
	};
	
	class PlaybackWorkAsyncTask extends WorkAsyncTask<Kanba> {
		public PlaybackWorkAsyncTask(Context context) {
			super(context);
		}

		public void onPre() throws Exception {
			mNodataView.setVisibility(View.GONE);
			showLoading();
		}

		public Kanba doProgress() throws Exception {
			if (isCancelled()) {
				return null;
			}
			Kanba kanbaList = SourceWasu.getKanbaList("wasu_2013_0825");
			
			if (kanbaList != null && kanbaList.playlist != null) {
				JSONArray array = new JSONArray();
				for (ChannelInfo info : kanbaList.playlist) {
					JSONObject json = new JSONObject();
					json.put("channelKey", String.valueOf(info.channelKey));
					json.put("channelName", info.channelName);
					json.put("playType", String.valueOf(info.playType));
					JSONObject playUrlJSON = new JSONObject();
					playUrlJSON.put("httpUrl", info.playUrl.httpUrl);
					playUrlJSON.put("channelID", info.playUrl.channelID);
					playUrlJSON.put("fccs", info.playUrl.fccs);
					json.put("playUrl", playUrlJSON);
					array.put(json);
				}
				mPalyList = array.toString();
			}

			return kanbaList;
		}

		public void onPost(boolean resultState, Kanba pList) throws Exception {
			hideLoading();
			if (pList == null) {
				setNodataView();
				return;
			}

			if (resultState) {
				mAdapter.setChannelList(pList.playlist);
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