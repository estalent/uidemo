package com.yunos.tv.yingshi.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.utils.L;
import com.yunos.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tv.app.widget.FocusedListView;
import com.yunos.tv.app.widget.FocusedListView.onKeyDownListener;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.BillItem;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.adapter.PlaybackListViewAdapter;
import com.yunos.tv.yingshi.widget.TabManager;

public class PlaybackBillFragment extends BaseTVFragment{
	private static final String TAG = "PlaybackBillFragment";
	
	private View mContentView, mNodataView;
	private FocusedListView mListView;
	private List<BillItem> mBillItemList;
	private PlaybackListViewAdapter mAdapter;
	private ImageView mChannelImag;//左边展示的频道图片
	private String mChannelName;//左边的频道名称
	ImageLoadParam loadParam; 

	private String playBackUrls;//传入的播放时所需的参数
	private int mChannelKey;//频道的id
	private String mChannelLogoImg; //频道的logo
	private Object mSyncForLoading = new Object();
	private WorkAsyncTask<String> mTask;
	private boolean mIsLoading = false;
	public static long huikanDelayTime = 1000 * 60 * 60;//延迟1小时
	private String tabName;
	
	public void setChannelInfoList(String playBackUrls, String logoUrl) {
		this.playBackUrls = playBackUrls;
		this.mChannelLogoImg = logoUrl;
	}

	public void setBillItemList(List<BillItem> programList) {
		this.mBillItemList = programList;
	}

	public PlaybackBillFragment() { 
	}
	
	public PlaybackBillFragment(int containerViewId, String tag, Context context, int channelKey, String channelName) {
		super(R.layout.frag_playback_bill, containerViewId, tag, context);
		this.mChannelKey = channelKey;
		this.mChannelName = channelName; 
		this.tabName = tag;
	}

	public static PlaybackBillFragment newInstance(int containerViewId, String tag, Context context, int channelKey, String channelName) {
		PlaybackBillFragment fragment = new PlaybackBillFragment(containerViewId, tag, context, channelKey, channelName);
		return fragment;
	}
	
	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		super.onViewCreated(rootView, savedInstanceState);
		
		mContentView = rootView.findViewById(R.id.bill_content);
		mNodataView = rootView.findViewById(R.id.nodata);
		mChannelImag = (ImageView)rootView.findViewById(R.id.channel_img);
		TextView  mChanName = (TextView)rootView.findViewById(R.id.channel_name);
		mChanName.setSelected(true);
		
		mListView = (FocusedListView)rootView.findViewById(R.id.programList);
		mAdapter = new PlaybackListViewAdapter(getContext());
		mListView.setAdapter(mAdapter);
		loadTimeData();
		//ListView的焦点及阴影
		mListView.setItemScaleValue(1.05f, 1.05f);
		mListView.setFocusResId(R.drawable.tui_bg_focus);
		mListView.setFocusShadowResId(R.drawable.tui_grid_focus);
		mListView.setOnItemClickListener(mItemClickListener);
		mListView.setFrameRate(Config.FRAME_LISTVIEW);
		mListView.setFocusMode(0);
		mListView.setFocusPositionMode(FocusedListView.FOCUS_ITEM_REMEMBER_LAST);
		mListView.setFocusViewId(R.id.program_lay);
		mAdapter = new PlaybackListViewAdapter(getContext());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemSelectedListener(new FocusItemSelectedListener() {
			public void onItemSelected(View v, int position, boolean isSelected, View parent) {
				AdapterView adapterView = (AdapterView) parent;
				if (v == null || adapterView.getChildCount() == 0) {
					return;
				}
				TextView tv = (TextView) v.findViewById(R.id.program_name);
				TextView timeTV = (TextView) v.findViewById(R.id.program_time);
				TextView hasPlayTV = (TextView) v.findViewById(R.id.program_hasplay);
				if(tv != null){
					if(isSelected){
						BillItem bill = mAdapter.getItem(position);
						if (isCanPlay(bill)) {
							tv.setTextColor(getResources().getColor(R.color.tui_text_color_white));
							timeTV.setTextColor(getResources().getColor(R.color.tui_text_color_white));
							hasPlayTV.setTextColor(getResources().getColor(R.color.tui_text_color_white));
						} else {
							tv.setTextColor(getResources().getColor(R.color.tui_text_color_grey));
							timeTV.setTextColor(getResources().getColor(R.color.tui_text_color_grey));
							hasPlayTV.setTextColor(getResources().getColor(R.color.tui_text_color_grey));
						}			
						tv.setSelected(true);
//						tv.startMarquee();
						tv.setEllipsize(TruncateAt.MARQUEE);
					}else{
						tv.setTextColor(getResources().getColor(R.color.tui_text_color_grey));
						timeTV.setTextColor(getResources().getColor(R.color.tui_text_color_grey));
						hasPlayTV.setTextColor(getResources().getColor(R.color.tui_text_color_grey));
						tv.setSelected(false);
						tv.setEllipsize(TruncateAt.END);
//						tv.stopMarquee();
					}
				}
			}
		});
		mListView.setOnKeyDownListener(new onKeyDownListener() {
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					return TabManager.instance().moveTo(TabManager.MOVE_TO_PREV_TAB);
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					return TabManager.instance().moveTo(TabManager.MOVE_TO_NEXT_TAB);
				}
				return false;
			}
		});
		mChanName.setText(mChannelName);
		
		loadParam = new ImageLoadParam("");
		loadParam.imageUrl = this.mChannelLogoImg;
		Global.getPlaybackImageLoader().loadImage(loadParam, mChannelImag);
		
	    mAdapter.setList(mBillItemList);
	    mListView.setSelected(true);
		if(tabName.equals("今日")){
			int firstPosition = adjustSelection();
			L.d("<<<<<<firstPosition=>>>>>"+firstPosition);
			if(firstPosition >0){
				mListView.smoothScrollToPositionFromTop(firstPosition + 1, mListView.getPaddingTop());
				mListView.setSelection(firstPosition);
			}
		}
		 mAdapter.notifyDataSetChanged();
	}
	
	public int adjustSelection(){
		int firstSelectionPosition = 0;
		if(this.mBillItemList != null){			
			for(int t=0; t<this.mBillItemList.size(); t++){
				if (isCanPlay(this.mBillItemList.get(t))) {
					firstSelectionPosition = t;
					break;
				}
			}
		}
		return firstSelectionPosition;
	}
	
	private boolean isCanPlay(BillItem bill){
		Date endTime; 
		long currentTime, endTimeInMinS;
		try {
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
			currentTime = PlaybackListViewAdapter.duringTime + System.currentTimeMillis();
			endTime = dateformat.parse(bill.endTime);
			endTimeInMinS = endTime.getTime();
			if(currentTime - endTimeInMinS > huikanDelayTime){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	private void loadTimeData() {
		synchronized (mSyncForLoading) { 
			if (mIsLoading) {
				return;
			}
			mIsLoading = true;
		}
		mTask = new TimeWorkAsyncTask(getContext());
		runTask(mTask);
	}
	
	class TimeWorkAsyncTask extends WorkAsyncTask<String> {
		public TimeWorkAsyncTask(Context context) {
			super(context);
		}
		
		public String doProgress() throws Exception {
			if (isCancelled()) {
				logd("getTime----doProgress -- isCancelled");
				return null;
			}
			String time = SourceWasu.getServerSystemTime();
			return time;
		}
		
		public void onPost(boolean resultState, String time) throws Exception {
			if (time == null || time.equals("")) {
				mIsLoading = false;
				return;
			}

			if (resultState) {
				mAdapter.setTime(time);
			}

			synchronized (mSyncForLoading) {
				mIsLoading = false;
			}
	    }
	}
	
	private OnItemClickListener mItemClickListener = new TvOnItemClickListener() {
		public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
			if(PlaybackListViewAdapter.duringTime <= 0){
				loadTimeData();
			}
			BillItem bill = mAdapter.getItem(position);
			if(isCanPlay(bill)){
				invokeHuikanPlayer(bill);
			}
		}
	};
	
	public void invokeHuikanPlayer(BillItem bill) {
		JSONObject playInfo = new JSONObject();
		try {
			playInfo.put("currentChannel", String.valueOf(mChannelKey));
			playInfo.put("channelName", mChannelName);
			playInfo.put("currentProgram_name", bill.name);
			playInfo.put("startTime", bill.startTime);
			playInfo.put("endTime", bill.endTime);
			
			logd("currentChannel:" + String.valueOf(mChannelKey));
			logd("channelName:" + mChannelName);
			logd("currentProgram_name:" + bill.name);
			logd("startTime:" + bill.startTime);
			logd("endTime:" + bill.endTime);
			
			JSONArray array = new JSONArray(playBackUrls);
			playInfo.put("playBackUrls", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Intent intent = new Intent("com.wasuali.action.huifangplayer");
		logd("playInfo:" + playInfo.toString());
		intent.putExtra("playInfo", playInfo.toString());
		startActivity(intent);
	}
	
	protected String getLogTag() {
		return TAG;
	}

}