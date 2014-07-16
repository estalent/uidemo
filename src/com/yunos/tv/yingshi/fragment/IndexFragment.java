package com.yunos.tv.yingshi.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.base.cache.LocalCache;
import com.aliyun.base.info.MobileInfo;
import com.yunos.tv.app.widget.CoverFlow;
import com.yunos.tv.app.widget.FocusedBasePositionManager;
import com.yunos.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.player.PlayerInvoke;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.ProgramLists;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.activity.YingshiActivity;
import com.yunos.tv.yingshi.activity.YingshiDetailActivity;
import com.yunos.tv.yingshi.activity.YingshiListActivity;
import com.yunos.tv.yingshi.adapter.CoverflowAdapter;
import com.yunos.tv.yingshi.adapter.IndexGridAdapter;
import com.yunos.tv.yingshi.exception.SourceException;
import com.yunos.tv.yingshi.widget.HeadViewLayout;
import com.yunos.tv.yingshi.widget.TitleFocusedGridView;
import com.yunos.tv.yingshi.widget.TitleInterface;

public class IndexFragment extends BaseTVFragment {
	public static final String TAG = "IndexFragment";
	
	protected String catalogName;

	protected CoverFlow mCoverFlow;
	protected TitleFocusedGridView mGridView;
	protected IndexGridAdapter mIndexAdapterr;
	
	protected Object mWaitingLock = new Object(); // 如果mGridTask结束但mCoverFlowTask没结束，就等着
	
	private Object mSyncCacheLoading = new Object();
	private boolean mIsCacheLoading = false;
	
	//coverflow部分数据请求同步锁
	private Object mSyncForCoverFlowLoading = new Object();
	private boolean mIsCoverFlowLoading = false;
	protected WorkAsyncTask<List<Program>> mCoverFlowTask = null;
	protected WorkAsyncTask<Boolean> cacheTask = null;
	
	//grid部分数据请求同步锁
	private Object mSyncForGridLoading = new Object();
	private boolean mIsGridLoading = false;
	protected WorkAsyncTask<List<ProgramLists>> mGridTask = null;
	
	protected View mNodataView;
	protected View mContentView;

	protected LayoutInflater mInflater;
	protected String mCatalogId;
	
	protected boolean mHasCoverflow = false;

	protected CoverflowAdapter mCoverflowAdapter;
	
	public IndexFragment() {}
	public IndexFragment(int layoutId, String catalogId, int containerViewId, Context context) {
		this(layoutId, catalogId, containerViewId, context, "");
	}
	
	public IndexFragment(int layoutId, String catalogId, int containerViewId, Context context, String catalogName) {
		super(layoutId, containerViewId, catalogId, context);
		Bundle args = getArguments();
		args.putString("catalog_id", catalogId);
		args.putString("catalog_name", catalogName);
		this.setArguments(args);
		this.mCatalogId = catalogId;
		this.catalogName = catalogName;
	}
	
	private OnItemClickListener mOnItemClickListener = new TvOnItemClickListener() {

		@Override
		public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
			Program p = mIndexAdapterr.getItem(position);
			if (p.showType == 0) {
				PlayerInvoke invoke = new PlayerInvoke(getContext(), p);
				int numColumns = mGridView.getNumColumns();
				invoke.playForZixun(Global.getRecommendList(mIndexAdapterr.getProgramLists().get((position - numColumns) / numColumns).programList, 
						(position - numColumns) % numColumns));
			} else {
				Intent it = new Intent(getContext(), YingshiDetailActivity.class);
				it.putExtra("program_id", p.id);
				it.putExtra("name", p.name);
				int numColumns = mGridView.getNumColumns();
				it.putExtra("ppvPath", catalogName + "#" + mIndexAdapterr.getProgramLists().get((position - numColumns) / numColumns).nodeName);
				startActivity(it);
			}
		}
	};

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logd("onCreate");
	}

	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		super.onViewCreated(rootView, savedInstanceState);
		logd("onViewCreated");

		if (savedInstanceState != null) {
			logd("onCreateView -- savedInstanceState!=null");
			mCatalogId = savedInstanceState.getString("catalog_id");
			catalogName = savedInstanceState.getString("catalog_name");
		}
		
		mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mNodataView = rootView.findViewById(R.id.nodata);
		mContentView = rootView.findViewById(R.id.content);

		mGridView = (TitleFocusedGridView) rootView.findViewById(R.id.frag_yingshi_gridview);
		mGridView.setFrameRate(Config.FRAME_GRIDVIEW);
		mGridView.setScrollDuration(Config.SCROLLING_DURATION_GRIDVIEW);
		mGridView.setFocusMode(FocusedBasePositionManager.FOCUS_SYNC_DRAW);
		mGridView.setItemScaleValue(1.1f, 1.1f);
		mGridView.setFocusResId(R.drawable.tui_bg_focus);
		mGridView.setFocusShadowResId(R.drawable.tui_grid_focus);
		mGridView.setFocusViewId(R.id.image_yingshi_grid);
		mGridView.setTitleInterface(new TitleInterface() {
			
			@Override
			public String getTitle(int row) {
				if (mIndexAdapterr == null) {
					return "";
				}
				List<ProgramLists> list = mIndexAdapterr.getProgramLists();
				if (list != null && !list.isEmpty()) {
					return list.get(row).nodeName;
				}
				return "";
			}
		});
		mGridView.setSelected(true);// 设置Gridview xml中android:listSelector为某一高亮框
		mGridView.setOnItemSelectedListener(new FocusItemSelectedListener() {
			@Override
			public void onItemSelected(View v, int position, boolean isSelected, View parent) {
				if (isSelected) {
					if (v instanceof HeadViewLayout) {
						if (mCoverFlow != null) {
							mCoverFlow.setForceGainFocus(true);
						}
					} else {
						TextView t = (TextView) v.findViewById(R.id.text_yingshi_grid);
						t.setSelected(true);
						t.setTextColor(Color.parseColor("#FFffffff"));
						t.setEllipsize(TruncateAt.MARQUEE);
					}
				} else {
					if (v instanceof HeadViewLayout) {
						if (mCoverFlow != null) {
							mCoverFlow.setForceGainFocus(false);
						}
					} else {
						TextView t = (TextView) v.findViewById(R.id.text_yingshi_grid);
						t.setTextColor(Color.parseColor("#FF999999"));
						t.setSelected(false);
						t.setEllipsize(TruncateAt.END);
					}
				}
			}
		});
		mGridView.setOnItemClickListener(mOnItemClickListener);
		mGridView.setFocusable(false);
		mGridView.setHeaderPosition(0);
		mGridView.setHasHeaderView(true);
		//注：在子类的onViewCreated中进行loadData();
	}
	
	protected HeadViewLayout mHeadViewLayout = null;
	public HeadViewLayout getHeadViewLayout() {
		if (mHeadViewLayout == null) {
			mHeadViewLayout = (HeadViewLayout) mInflater.inflate(R.layout.yinghi_coverflow, null);
			mHeadViewLayout.setFocusable(false);
			Resources res = getContext().getResources();
			int padding = MobileInfo.dip2px(getContext(), res.getDimension(R.dimen.index_leftpadding));
			int w = (MobileInfo.getScreenWidthPx(getContext()) - 2 * padding);
			int h = MobileInfo.dip2px(getContext(), res.getDimension(R.dimen.grid_coverflow_h));
			mHeadViewLayout.setLayoutParams(new AbsListView.LayoutParams(w, h));
			mCoverFlow = (CoverFlow) mHeadViewLayout.findViewById(R.id.yingshi_coverflow);
			mCoverFlow.setFocusable(false);
			mCoverFlow.setSelector(res.getDrawable(R.drawable.tui_bg_focus));
			mCoverflowAdapter = new CoverflowAdapter(getContext(), this.mCatalogId);
			mCoverFlow.setGravity(Gravity.CENTER_VERTICAL);
			// 设置切换时的动画时间
			mCoverFlow.setAnimationDuration(res.getInteger(R.integer.coverflow_anim_during));
			// 设置item之间的间隔，水平方向的
			mCoverFlow.setSpacing(MobileInfo.dip2px(getContext(), res.getDimension(R.dimen.coverflow_h_spacing)));
			// 设置标题文字和item之间的间隔
			mCoverFlow.setCoverFlowTextSpacing(MobileInfo.dip2px(getContext(), res.getDimension(R.dimen.coverflow_textspacinig)));
			// 设置选择框的上下左右padding，可以为负值
			int selectorPadding = MobileInfo.dip2px(getContext(), res.getDimension(R.dimen.coverflow_selector_padding));
			mCoverFlow.setSelectorPadding(selectorPadding, selectorPadding, selectorPadding, selectorPadding);
			mCoverFlow.setMidItemCount(res.getInteger(R.integer.coverflow_mid_count));
//			mCoverFlow.setMaxRotationAngle(30);
			mCoverFlow.setCoverFlowTextMaxLine(1);
			mCoverFlow.setEllipsize(CoverFlow.TruncateAt.MARQUEE);
			mCoverFlow.setAdapter(mCoverflowAdapter);
			mCoverFlow.setOnItemClickListener(new TvOnItemClickListener() {
		
				@Override
				public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
					if (mCoverflowAdapter == null || mCoverflowAdapter.getProgramList() == null || mCoverflowAdapter.getProgramList().size() == 0) {
						return;
					}
					String topicName =(String) mCoverflowAdapter.getItem(position);
					position = position % mCoverflowAdapter.getProgramList().size();
					Program p = mCoverflowAdapter.getProgramList().get(position);
					if (p.type != null && "topic".equals(p.type)) {
						Intent it = new Intent(getContext(), YingshiListActivity.class);
						it.putExtra("id", p.id);
						it.putExtra("name", p.name);
						it.putExtra("picUrl", p.picUrl);
						it.putExtra("type", p.nodeType); // 0非影视  1影视
						it.putExtra("hasProgram", 1);//0目录列表  1视频列表
						it.putExtra("isTopic", true); // 代表是专题列表
						getContext().startActivity(it);
						return;
					}
					if (p.type != null && "program".equals(p.type)) {
						Intent it = new Intent(getContext(), YingshiDetailActivity.class);
						it.putExtra("program_id", p.id);
						it.putExtra("name", p.name);
						it.putExtra("ppvPath", catalogName + "#" + topicName);
						startActivity(it);
						return;
					}
				}
			});
			mCoverFlow.setDrawShadowImage(false);
			mCoverFlow.setSelection(7);
		}
		return mHeadViewLayout;
	}
	
	protected void loadData() {
		synchronized (mSyncCacheLoading) {
			if (mIsCacheLoading) {
				return;
			}
			mIsCacheLoading = true;
		}
		
		cacheTask = new LocalCacheAsyncTask(getContext());
		runTask(cacheTask);
	}
	
	protected void loadCoverFlowData() {
		synchronized (mSyncForCoverFlowLoading) {
			if (mIsCoverFlowLoading) {
				logd("loadCoverFlowData -- mIsCoverFlowLoading return");
				return;
			}
			mIsCoverFlowLoading = true;
		}
		mCoverFlowTask = new IndexCoverFlowAsyncTask(getContext());
		runTask(mCoverFlowTask);
	}
	
	protected void loadGridData() {
		synchronized (mSyncForGridLoading) {
			if (mIsGridLoading) {
				logd("IndexWorkAsyncTask -- loadGridData -- mIsGridLoading return");
				return;
			}
			mIsGridLoading = true;
		}
		mGridTask = new IndexGridWorkAsyncTask(getContext());
		runTask(mGridTask);
	}
	
	class LocalCacheAsyncTask extends WorkAsyncTask<Boolean> {
		List<Program> hotList = null;
		List<ProgramLists> gridList = null;

		public LocalCacheAsyncTask(Context context) {
			super(context, false);
		}
		
		@Override
		public void onPre() throws Exception {
//			showLoading();
		}
		
		@Override
		public Boolean doProgress() throws Exception {
			Log.i(TAG, "catalogId:" + mCatalogId);
			List<Program> hotList = (ArrayList<Program>) LocalCache.readData(getContext(), "INDEX_HOT_" + mCatalogId);
			if (hotList == null) {
				hotList = (ArrayList<Program>) LocalCache.readAsset(getContext(), "cache_data/" + "INDEX_HOT_" + mCatalogId);
				Log.d(TAG, "localcache -- coverflow -- asset " + "cache_data/" + "INDEX_HOT_" + mCatalogId);
			} else {
				Log.d(TAG, "localcache -- coverflow -- data " + "INDEX_HOT_" + mCatalogId);
			}
			List<ProgramLists> cacheList = (ArrayList<ProgramLists>) LocalCache.readData(getContext(), "INDEX_" + mCatalogId);
			if (cacheList == null) {
				cacheList = (ArrayList<ProgramLists>) LocalCache.readAsset(getContext(), "cache_data/" + "INDEX_" + mCatalogId);
				Log.d(TAG, "localcache -- grid -- asset " + "cache_data/" + "INDEX_" + mCatalogId);
			} else {
				Log.d(TAG, "localcache -- grid -- data " + "INDEX_" + mCatalogId);
			} 
			if (hotList == null || cacheList == null) {
				return false;
			}
			setHotList(hotList);
			print(hotList);
			
			setGridList(cacheList);
			return true;
		}
		
		void print(List<Program> list) {
			if (list == null) {
				Log.d(TAG, "localcache -- print -- null");
				return ;
			}
			int k = 0;
			for (Program p: list) {
				Log.d(TAG, "localcache -- print -- position:" + (k++) + ",name:" + p.name + ",pic:" + p.picUrl);
			}
		}
		
		@Override
		public void onPost(boolean isSuccess, Boolean obj) throws Exception {
			hideLoading();
			
			if (obj) {
				mHasCoverflow = false;
				if (hotList != null && hotList.size() > 0) {
					mHasCoverflow = true;
					getHeadViewLayout();
					mCoverflowAdapter.setProgramList(hotList);
				}
				if (mHasCoverflow) {
					mGridView.setHeaderPosition(0);
					mCoverFlow.reset();
					mCoverflowAdapter.notifyDataSetChanged();
					mCoverFlow.setSelection(777);
				}
				mIndexAdapterr.setHasCoverFlow(mHasCoverflow);
				mGridView.setHasHeaderView(mHasCoverflow);
				
				if (gridList != null) {
					mIndexAdapterr.setProgramLists(gridList);
					mIndexAdapterr.notifyDataSetChanged();
					mGridView.setFocusable(true);
					mContentView.setVisibility(View.VISIBLE);
				}
			} else {
				Log.e(TAG, "localcache -- error -- should not be null.");
			}
			loadCoverFlowData();
			loadGridData();
			synchronized (mSyncCacheLoading) {
				mIsCacheLoading = false;
			}
		}
		
		public List<Program> getHotList() {
			return hotList;
		}

		public void setHotList(List<Program> hotList) {
			this.hotList = hotList;
		}

		public List<ProgramLists> getGridList() {
			return gridList;
		}

		public void setGridList(List<ProgramLists> gridList) {
			this.gridList = gridList;
		}
	}
	
	class IndexCoverFlowAsyncTask extends WorkAsyncTask<List<Program>> {
		
		public IndexCoverFlowAsyncTask(Context context) {
			super(context);
		}
		
		@Override
		public void onPre() throws Exception {
		}
		
		@Override
		public List<Program> doProgress() throws Exception {
			List<Program> programList = SourceWasu.getHotProgramList(mCatalogId);
			LocalCache.writeData(getContext(), programList, "INDEX_HOT_" + mCatalogId);
			return programList;
		}
		
		@Override
		public void onPost(boolean isSuccess, List<Program> list) throws Exception {
			if (isSuccess) {
				mHasCoverflow = false;
				if (list != null && list.size() > 0) {
					mHasCoverflow = true;
					getHeadViewLayout(); // 为了让mCoverflowAdapter不为空
					mCoverflowAdapter.setProgramList(list);
				}
				if (mHasCoverflow) {
					mGridView.setHeaderPosition(0);
				}
				mIndexAdapterr.setHasCoverFlow(mHasCoverflow);
				mGridView.setHasHeaderView(mHasCoverflow);
				
				YingshiActivity activity = (YingshiActivity) getContext();
				if (!activity.hasToast()) {
					activity.setHasToast(true);
					Toast.makeText(getContext(), R.string.toast_to_top, Toast.LENGTH_LONG).show();
				}
			}
			synchronized (mSyncForCoverFlowLoading) {
				mIsCoverFlowLoading = false;
			}
			synchronized (mWaitingLock) {
				mWaitingLock.notifyAll();
			}
		}
		
		@Override
		public void onCancel(boolean isSuccess) {
			mHasCoverflow = false;
			mGridView.setHasHeaderView(mHasCoverflow);
			mIndexAdapterr.setHasCoverFlow(mHasCoverflow);
			synchronized (mSyncForCoverFlowLoading) {
				mIsCoverFlowLoading = false;
			}
			synchronized (mWaitingLock) {
				mWaitingLock.notifyAll();
			}
		}
		
	};
	
	class IndexGridWorkAsyncTask extends WorkAsyncTask<List<ProgramLists>> {

		public IndexGridWorkAsyncTask(Context context) {
			super(context);
		}

		@Override
		public void onPre() throws Exception {
			logd("IndexGridWorkAsyncTask -- onPre");
			mNodataView.setVisibility(View.GONE);
		} 

		@Override
		public List<ProgramLists> doProgress() throws Exception {
			logd("IndexGridWorkAsyncTask -- doProgress");
			if (isCancelled()) {
				logd("doProgress -- isCancelled");
				return null;
			}
			int columNum = getResources().getInteger(R.integer.yingshi_grid_colum_num);
			List<ProgramLists> programLists = SourceWasu.getHomeProgramList(mCatalogId, columNum);
			Iterator<ProgramLists> it = programLists.iterator();
			//TODO 将不足6个的列表删除
			while (it.hasNext()) {
				ProgramLists pl = it.next();
				if (pl == null || pl.programList == null || pl.programList.size() < columNum) {
					it.remove();
				}
			}
			LocalCache.writeData(getContext(), programLists, "INDEX_" + mCatalogId);
			synchronized (mWaitingLock) {
				while (mIsCoverFlowLoading) {
					try {
		            	mWaitingLock.wait();
		            } catch (InterruptedException e) {}
				}
			}
			return programLists;
		}

		@Override
		public void onPost(boolean resultState, List<ProgramLists> pList) throws Exception {
			logd("IndexGridWorkAsyncTask -- onPost");
			hideLoading();
			if ((pList == null || pList.isEmpty()) && mIndexAdapterr.getProgramLists().size() == 0) {
				mNodataView.setVisibility(View.VISIBLE);
				mContentView.setVisibility(View.GONE);
			} else {
				if (resultState) {
					mNodataView.setVisibility(View.GONE);
					mContentView.setVisibility(View.VISIBLE);
					
					if (pList != null && pList.size() > 0) {
						mIndexAdapterr.setProgramLists(pList);
						mIndexAdapterr.notifyDataSetChanged();
						mGridView.setFocusable(true);
					}
					if (mHasCoverflow) {
						mCoverFlow.reset();
						mCoverflowAdapter.notifyDataSetChanged();
						mCoverFlow.setSelection(777);
					}
				}
			}
			synchronized (mSyncForGridLoading) {
				mIsGridLoading = false;
			}
		}

		@Override
		public void onError(Exception e) {
			hideLoading();
			if (e instanceof SourceException) {
				SourceException ex = (SourceException) e;
				Toast.makeText(getContext(), ex.getErrorCode().getMessage(), Toast.LENGTH_SHORT).show();
			}
			synchronized (mSyncForGridLoading) {
				mIsGridLoading = false;
			}
		}

		@Override
		public void onCancel(boolean isSuccess) {
			super.onCancel(isSuccess);
			logd("IndexGridWorkAsyncTask -- onCancel");
			hideLoading();
			if (!isSuccess) {
				logd("IndexGridWorkAsyncTask -- onCancel -- !isSuccess");
			}

			synchronized (mSyncForGridLoading) {
				mIsGridLoading = false;
			}
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		super.onTabUnselected(tab, ft);
//		if (mTask != null && mIsLoading) {
//			logd("onTabUnselected -- cancel task");
//			mTask.cancel(true);
//			hideLoading();
//			mTask = null;
//		}
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		super.onTabSelected(tab, ft);
		if (mIndexAdapterr != null) {
			logd("IndexWorkAsyncTask -- onTabSelected -- loadData");
			if (mIndexAdapterr.getProgramLists().isEmpty()) {
				loadData();
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putAll(getArguments());
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		logd("IndexWorkAsyncTask -- onDestroy");
	}
	
	@Override
	protected String getLogTag() {
		return TAG;
	}
}