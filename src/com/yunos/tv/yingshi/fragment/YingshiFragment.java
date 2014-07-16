package com.yunos.tv.yingshi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.base.cache.LocalCache;
import com.aliyun.base.net.NetConnectionType;
import com.aliyun.base.net.NetworkManager;
import com.aliyun.base.net.NetworkManager.INetworkListener;
import com.yunos.tv.app.widget.FocusedBasePositionManager;
import com.yunos.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tv.app.widget.FocusedGridView;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.player.PlayerInvoke;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.ProgramList;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.activity.YingshiActivity;
import com.yunos.tv.yingshi.activity.YingshiDetailActivity;
import com.yunos.tv.yingshi.adapter.YingshiGridAdapter;
import com.yunos.tv.yingshi.exception.SourceException;

public class YingshiFragment extends BaseTVFragment {
	public static final String TAG = "YingshiFragment";

	private FocusedGridView mGridView;
	private YingshiGridAdapter mAdapter;

	public static final int PER_PAGE = 18 * 10; // 取5屏数据=90，每次请求数据不能大于100
	private int mGridColumNum = 6; // 每行的个数，　从配置中读b
	public static final int OUT_ROW = 6; // 屏幕外还有OUT_ROW行就加载，默认保留一页

	private int mCurrTotalPage = 0;
	private int mLastPosition = 0;
	private int mCurPosition = 0;
	private volatile boolean mHasNext = true;
	private boolean mIsLoading = false;
	private WorkAsyncTask<ProgramList> mTask = null;
	private WorkAsyncTask<List<Program>> mCacheTask = null;
	
	private View mNodataView;
	private View mContentView;

	private String mCatalogId;
	private String ppvPath;
	private boolean mIsTopic; // 是否是专题列表
	
	public YingshiFragment() {}
	/**
	 * 
	 * @param catalogId
	 * @param containerViewId
	 * @param context
	 * @param isTopic 是否是专题列表
	 */
	public YingshiFragment(String catalogId, int containerViewId, Context context, String ppvPath, boolean isTopic) {
		super(R.layout.frag_yingshi, containerViewId, catalogId, context);
		Bundle args = getArguments();
		args.putString("catalog_id", catalogId);
		args.putString("ppvPath", ppvPath);
		this.setArguments(args);
		this.mCatalogId = catalogId;
		this.ppvPath = ppvPath;
		this.mIsTopic = isTopic;
	}

	public static YingshiFragment newInstance(String catalogId, int containerViewId, Context context, String ppvPath) {
		YingshiFragment yf = new YingshiFragment(catalogId, containerViewId, context, ppvPath, false);
		return yf;
	}
	
	public static YingshiFragment newInstance(String catalogId, int containerViewId, Context context, String ppvPath, boolean isTopic) {
		YingshiFragment yf = new YingshiFragment(catalogId, containerViewId, context, ppvPath, isTopic);
		return yf;
	}
	
	private INetworkListener mINetworkListener = new INetworkListener() {

		@Override
		public void onNetworkChanged(boolean isConnected, NetConnectionType lastType, NetConnectionType currentType) {
			logd("yingshifrag - onNetworkChanged -- connected = " + isConnected + " -- lastType = " + lastType + " -- currentType = " + currentType);
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

	private FocusItemSelectedListener mOnItemSelectedListener = new FocusItemSelectedListener() {
		@Override
		public void onItemSelected(View v, int position, boolean isSelected, View parent) {
			TextView t = (TextView) v.findViewById(R.id.text_yingshi_grid);
			t.setSelected(isSelected);
			if (isSelected) {
				t.setEllipsize(TruncateAt.MARQUEE);
				t.setTextColor(Color.parseColor("#FFffffff"));
				mLastPosition = mCurPosition;// 新位置与老位置之间的距离
				mCurPosition = position;
				onSelectedChange((AdapterView)parent);
			} else {
				t.setTextColor(Color.parseColor("#FF999999"));
				t.setEllipsize(TruncateAt.END);
			} 
		}
	};

	private OnItemClickListener mOnItemClickListener = new TvOnItemClickListener() {

		@Override
		public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
			Program p = mAdapter.getItem(position);
			if (p.showType == 0) {
				PlayerInvoke invoke = new PlayerInvoke(getContext(), p);
				invoke.playForZixun(Global.getRecommendList(mAdapter.getProgramList(), position));
			} else {
				Intent it = new Intent(getContext(), YingshiDetailActivity.class);
				it.putExtra("program_id", p.id);
				it.putExtra("name", p.name);
				it.putExtra("ppvPath", ppvPath);
				startActivity(it);
			}
		}
	};

	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		private boolean isLastRow = false;
		private boolean isFirstRow = false;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				int loadCount = mAdapter.getCount();
				int lastPos = view.getLastVisiblePosition();
				if ((loadCount - 1 - lastPos) / mGridColumNum <= OUT_ROW) {
					loadData();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 最后一个项目出来的时候显示
				if (isLastRow) {
					isLastRow = false;
				}
				break;
			default:
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
				isLastRow = true;
			} else {
				isLastRow = false;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logd("aabb onCreate");
		NetworkManager.instance().registerStateChangedListener(mINetworkListener);
	}

	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		super.onViewCreated(rootView, savedInstanceState);
		logd("onViewCreated");

		if (savedInstanceState != null) {
			logd("onCreateView -- savedInstanceState!=null");
			mCatalogId = savedInstanceState.getString("catalog_id");
			ppvPath = savedInstanceState.getString("ppvPath");
		}

		mGridColumNum = getContext().getResources().getInteger(R.integer.yingshi_grid_colum_num);

		mNodataView = rootView.findViewById(R.id.nodata);
		mContentView = rootView.findViewById(R.id.content);

		float scaleValue = getResources().getDimension(R.dimen.grid_middle_w) / getResources().getDimension(R.dimen.image_middle_w);
		mGridView = (FocusedGridView) rootView.findViewById(R.id.frag_yingshi_gridview);
		mGridView.setFrameRate(Config.FRAME_GRIDVIEW);
		mGridView.setScrollDuration(Config.SCROLLING_DURATION_GRIDVIEW);
		mGridView.setFocusMode(FocusedBasePositionManager.FOCUS_SYNC_DRAW);
		mGridView.setItemScaleValue(scaleValue, scaleValue);
		mGridView.setFocusResId(R.drawable.tui_bg_focus);
		mGridView.setFocusShadowResId(R.drawable.tui_grid_focus);
		mGridView.setFocusViewId(R.id.image_yingshi_grid);
		mAdapter = new YingshiGridAdapter(getContext(), mCatalogId);
		mGridView.setAdapter(mAdapter);
		mGridView.setSelected(true);// 设置Gridview xml中android:listSelector为某一高亮框
		mGridView.setOnItemSelectedListener(mOnItemSelectedListener);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		mGridView.setOnScrollListener(mOnScrollListener);
		mGridView.setFocusable(false);
		loadData();
	}

	private void onSelectedChange(AdapterView view) {
		int lastRow = mLastPosition / mGridColumNum; // 老位置的行数
		int thisRow = mCurPosition / mGridColumNum;
		int diffRow = thisRow - lastRow;
		if (diffRow == 0) { // 行数没发生变化
			return;
		}

		int firstPos = view.getFirstVisiblePosition();
		int lastPos = view.getLastVisiblePosition();
		int loadCount = mAdapter.getCount();
		if (diffRow > 0) {// 屏幕外<=OUT_ROW行时加载数据

			if ((loadCount - 1 - lastPos) / mGridColumNum <= OUT_ROW) {
				loadData();
			}
		}
	}

	private Object mSyncForLoading = new Object();

	private void loadData() {
		if (!mHasNext) {
			logd("loadData -- !mHasNext return");
			return;
		}
		synchronized (mSyncForLoading) {
			if (mIsLoading) {
				logd("loadData -- mIsLoading return");
				return;
			}
			mIsLoading = true;
		}
		if (mCurrTotalPage == 0) {
			mCacheTask = new YingshiCacheTask(getContext());
			runTask(mCacheTask);
		} else {
			mTask = new YingshiWorkAsyncTask(getContext());
			runTask(mTask);
		}
	}
	
	class YingshiCacheTask extends WorkAsyncTask<List<Program>> {
		
		public YingshiCacheTask(Context context) {
			super(context, false);
		}
		
		@Override
		public void onPre() throws Exception {
//			showLoading();
		}

		@Override
		public List<Program> doProgress() throws Exception {
			List<Program> cacheList = (ArrayList<Program>) LocalCache.readData(getContext(), mCatalogId);
			if (cacheList == null) {
				cacheList = (ArrayList<Program>) LocalCache.readAsset(getContext(), "cache_data/" + mCatalogId);
				Log.i(TAG, "localcache -- data -- " + "cache_data/" + mCatalogId);
			} else {
				Log.i(TAG, "localcache -- asset -- " + mCatalogId);
			}
			return cacheList;
		}
		
		@Override
		public void onPost(boolean isSuccess, List<Program> cacheList) throws Exception {
			hideLoading();
			if (cacheList != null) {
				mAdapter.setProgramList(cacheList);
				mAdapter.notifyDataSetChanged();
				mContentView.setVisibility(View.VISIBLE);
				mGridView.setFocusable(true);
			} else {
				Log.e(TAG, "localcahe -- error -- should not be null.");
			}
			mTask = new YingshiWorkAsyncTask(getContext());
			runTask(mTask);
		}
	}
	
	class YingshiWorkAsyncTask extends WorkAsyncTask<ProgramList> {

		public YingshiWorkAsyncTask(Context context) {
			super(context);
		}

		@Override
		public ProgramList doProgress() throws Exception {
			logd("YingshiWorkAsyncTask -- doProgress");
			if (isCancelled()) {
				logd("doProgress -- isCancelled");
				return null;
			}
			ProgramList programList = null;
			if (mIsTopic) {
				programList = SourceWasu.getTopicProgramList(mCatalogId, (mCurrTotalPage + 1), PER_PAGE);// 服务端要求从1开始
			} else {
				programList = SourceWasu.getProgramList(mCatalogId, (mCurrTotalPage + 1), PER_PAGE);// 服务端要求从1开始
			}
			if (mCurrTotalPage == 0) {
				LocalCache.writeData(getContext(), programList.programList, mCatalogId);
			}
			return programList;
		}

		@Override
		public void onPost(boolean resultState, ProgramList pList) throws Exception {
			logd("YingshiWorkAsyncTask -- onPost");
			hideLoading();

			if (pList != null && pList.programList.size() > 0) {
				mHasNext = pList.hasNext;
				logd("YingshiWorkAsyncTask -- onPost -- pList.size():" + pList.programList.size() + ", hasNext:" + mHasNext);
				if (resultState) {
					if (mCurrTotalPage == 0) {
						mAdapter.setProgramList(pList.programList);
					} else {
						mAdapter.getProgramList().addAll(pList.programList);
					}
					mCurrTotalPage++;
					mAdapter.notifyDataSetChanged();
				}
				mGridView.setFocusable(true);
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
		public void onCancel(boolean isSuccess) {
			super.onCancel(isSuccess);
			logd("YingshiWorkAsyncTask -- onCancel");
			if (!isSuccess) {
				loge("onCancel -- !isSuccess");
			}
			hideLoading();

			synchronized (mSyncForLoading) {
				mIsLoading = false;
			}
			setNodataView();
		}
	}
	
	private void setNodataView() {
		if (mAdapter.getProgramList().isEmpty()) {
			mNodataView.setVisibility(View.VISIBLE);
			mContentView.setVisibility(View.GONE);
		} else {
			if (isRequestFocus()) {
				mGridView.requestFocus();
			}
			mNodataView.setVisibility(View.GONE);
			mContentView.setVisibility(View.VISIBLE);
			YingshiActivity activity = (YingshiActivity) getContext();
			if (!activity.hasToast()) {
				activity.setHasToast(true);
				Toast.makeText(getContext(), R.string.toast_to_top, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		super.onTabUnselected(tab, ft);
		if (mTask != null && mIsLoading) {
			logd("onTabUnselected -- cancel task");
			mTask.cancel(true);
			hideLoading();
			mTask = null;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		super.onTabSelected(tab, ft);
		if (mAdapter != null) {
			logd("onTabSelected -- loadData");
			if (mAdapter.getProgramList().isEmpty()) {
				loadData();
			}
		}
	}

	@Override
	protected String getLogTag() {
		return TAG;
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putAll(getArguments());
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		logd("aabb onDestroy");
		NetworkManager.instance().unregisterStateChangedListener(mINetworkListener);
		mAdapter.clear();
		mAdapter = null;
	}

}