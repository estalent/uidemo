package com.yunos.tv.yingshi.fragment;

import java.util.List;
import java.util.Stack;

import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.base.net.NetConnectionType;
import com.aliyun.base.net.NetworkManager;
import com.aliyun.base.net.NetworkManager.INetworkListener;
import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.entity.ImageArg;
import com.yunos.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tv.app.widget.FocusedListView;
import com.yunos.tv.app.widget.FocusedListView.onKeyDownListener;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.player.PlayerInvoke;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.ProgramList;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.activity.YingshiDetailActivity;
import com.yunos.tv.yingshi.adapter.ZixunListViewAdapter;
import com.yunos.tv.yingshi.aidl.Catalog;
import com.yunos.tv.yingshi.widget.TabManager;
import com.yunos.tv.yingshi.widget.ZixunImageView;

public class ZixunFragment extends BaseTVFragment {
	private FocusedListView mListView = null;
	private ZixunImageView mAbstractFigure = null;
	private boolean mNeedShowProgress = true;

	private int mRequestPageNo = -1;
	private final int mRequestPageSize = 30;
	private View mContentView, mNodataView;

	ImageLoadParam loadParam;

	private Object mSyncForLoading = new Object();
	private WorkAsyncTask mAsyncTask = null;
	private ZixunListViewAdapter mAdapter = null;
	private Catalog mCatalog = null;
	private List<Program> mProgramList = null;
	static boolean isInsideOut = false;
	static Stack<String> mFragmentStack = new Stack<String>();
	
	public static void clearFragmentStack(Context context) {
		if(ZixunFragment.mFragmentStack != null){
			if(ZixunFragment.mFragmentStack.size()>0)
				ZixunFragment.mFragmentStack.clear();
		}
	}

	private INetworkListener mINetworkListener = new INetworkListener() {
		public void onNetworkChanged(boolean isConnected, NetConnectionType lastType, NetConnectionType currentType) {
			if (isConnected && NetConnectionType.none == lastType) {
				if (mAdapter != null && mAdapter.isEmpty()) {
					loadData();
				}
			}
			if (!isConnected) {
				if (mAsyncTask != null && !mAsyncTask.isCancelled() && mAsyncTask.getStatus() != WorkAsyncTask.Status.FINISHED) {
					mAsyncTask.cancel(true);
					mAsyncTask = null;
				}
			}
		}
	};

	public ZixunFragment() {
	}

	public ZixunFragment(int containerViewId, String tag, Context context) {
		super(R.layout.frag_zixun, containerViewId, tag, context);
	}

	public static ZixunFragment newInstance(Catalog catalog, int containerViewId, Context context) {
		return newInstance(catalog, containerViewId, context, false, "");
	}
	
	private boolean mIsTopic;//是否是专题列表
	private String ppvPath;
	public static ZixunFragment newInstance(Catalog catalog, int containerViewId, Context context, boolean isTopic, String ppvPath) {
		ZixunFragment fragment = new ZixunFragment(containerViewId, catalog.id, context);
		fragment.mCatalog = catalog;
		fragment.mProgramList = null;
		fragment.mIsTopic = isTopic;
		fragment.ppvPath = ppvPath;
		
		Bundle args = fragment.getArguments();
		args.putParcelable("catalog", catalog);
		args.putString("ppvPath", ppvPath);
		fragment.setArguments(args);
		
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NetworkManager.instance().registerStateChangedListener(mINetworkListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		NetworkManager.instance().unregisterStateChangedListener(mINetworkListener);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			TabManager.instance().setCurrentFragment(this);
			if (isInsideOut) {
				mListView.requestFocus();
				isInsideOut = false;
			}
		}
	}

	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		super.onViewCreated(rootView, savedInstanceState);
		if (savedInstanceState != null) {
			mCatalog = savedInstanceState.getParcelable("catalog");
		} else {
			Bundle args = getArguments();
			if (args != null) {
				mCatalog = args.getParcelable("catalog");
				ppvPath = args.getString("ppvPath");
			}
		}
		TabManager.instance().setCurrentFragment(this);
		loadParam = new ImageLoadParam("");
		loadParam.setWorkCache(true);
		loadParam.setImageArg(ImageArg.arg3); // 带倒影的

		mContentView = rootView.findViewById(R.id.content);
		mNodataView = rootView.findViewById(R.id.nodata);
		mAbstractFigure = (ZixunImageView) rootView.findViewById(R.id.AbstractFigure);
		mListView = (FocusedListView) rootView.findViewById(R.id.infoList);
		mAdapter = new ZixunListViewAdapter(getContext());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mItemClickListener);
		mListView.setOnItemSelectedListener(mOnItemSelectedListener);
		// ListView的焦点及阴影
		mListView.setItemScaleValue(1.05f, 1.05f);
		mListView.setFocusResId(R.drawable.tui_bg_focus);
		mListView.setFocusShadowResId(R.drawable.tui_grid_focus);
		mListView.setFrameRate(Config.FRAME_LISTVIEW);
		mListView.setFocusMode(0);
		mListView.setOnKeyDownListener(new onKeyDownListener() {

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				//如果当前不是一级fragment，则调用activity的onKeyDown，然后传入fragment的onKeyDown
				if (mFragmentStack.size() > 0) {
					return false;
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					return TabManager.instance().moveTo(TabManager.MOVE_TO_PREV_TAB);
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					return TabManager.instance().moveTo(TabManager.MOVE_TO_NEXT_TAB);
				}
				return false;
			}
		});
		// mListView.setFocusViewId(R.id.image_yingshi_grid);

		hideProgress();
		loadAbstractFigure(-1);

		if (mCatalog != null) {
			if (mCatalog.hasProgram == 1) {
				mRequestPageNo = 1;
			}
			loadData();
		} else if (mProgramList != null) {
			mAdapter.setList(null, mProgramList);
		}
	}

	// add
	private void onClickCatalog(Catalog c) {
		synchronized(mFragmentStack){
			mFragmentStack.push(this.mTag);
			ZixunFragment newFragment = ZixunFragment.newInstance(c, getContainerViewId(), getContext());
			newFragment.setIsRequestFocus(true);
			Activity activity = (Activity) getContext();
			FragmentManager fm = activity.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			logd("==onClickCatalog----mTag="+this.mTag+",c.id="+c.id);
			ft.add(getContainerViewId(), newFragment, c.id);
			ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			// fragment.setAlwaysLostFocusListener(mAlwaysLostFocusListener);
			ft.hide(ZixunFragment.this);
			ft.show(newFragment);
			ft.commit();
			fm.executePendingTransactions();
		}
	}

	private void onClickProgram(Program p, int position) {
		if (p.showType == 0) {
			PlayerInvoke invoke = new PlayerInvoke(getContext(), p);
//			invoke.setThisPlayInfo(0, 0);
//			invoke.setLastPlayInfo("1", 0);
			invoke.playForZixun(Global.getRecommendList(mAdapter.getProgramList(), position));
		} else {
			Intent it = new Intent(getContext(), YingshiDetailActivity.class);
			it.putExtra("program_id", p.id);
			it.putExtra("name", p.name);
//			it.putExtra("ppvPath", ppvPath); //TODO 以防咨询里也可以点击详情页
			getContext().startActivity(it);
		}
	}
	
	private FocusItemSelectedListener mOnItemSelectedListener = new FocusItemSelectedListener() {
		@Override
		public void onItemSelected(View v, int position, boolean isSelected, View parent) {
			AdapterView adapterView = (AdapterView) parent;
			if (isSelected) {
				if (adapterView.getAdapter().getCount() >= mRequestPageSize) {
					if (position > 0 && position > adapterView.getAdapter().getCount() - 5) {
						mRequestPageNo++;
						loadData();
					}
				}
			} else {
			}

			TextView tv = (TextView) v.findViewById(R.id.InfoListItemTitle);
			if (tv != null) {
				if (isSelected) {
					tv.setEllipsize(TruncateAt.MARQUEE);
				} else {
					tv.setEllipsize(TruncateAt.END);
				}
			}
		}
	};

	private OnItemClickListener mItemClickListener = new TvOnItemClickListener() {
		public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
			Object obj = mAdapter.getItem(position);
			if (mCatalog != null) {
				if (mCatalog.hasProgram == 0) {
					onClickCatalog((Catalog) obj);
				} else {
					onClickProgram((Program) obj, position);
				}
			} else if (mProgramList != null) {
				onClickProgram((Program) obj, position);
			}

		}
	};

	private void loadData() {
		synchronized (mSyncForLoading) {
			if (mIsLoading) {
				logd("loadData -- mIsLoading return");
				return;
			}
			if (mCatalog != null) {
				if (mCatalog.hasProgram == 1) {// 获取节目列表
					if (mRequestPageNo <= 1) {
						mNeedShowProgress = true;
					} else {
						mNeedShowProgress = false;
					}
				} else {// 有二级目录的，获取一级目录
					mNeedShowProgress = true;
				}
			}

			mIsLoading = true;
		}
		mAsyncTask = new LoadListDataTask(getContext());
		runTask(mAsyncTask);
	}
	
	class LoadListDataTask extends WorkAsyncTask<Object> {
		
		public LoadListDataTask(Context context) {
			super(context);
		}
		
		public void onPre() throws Exception {
			if (mCatalog != null) {
				showProgress();
			}
		}

		@Override
		public Object doProgress() throws Exception {
			if (mCatalog != null) {
				if (mIsTopic) {
					return SourceWasu.getTopicProgramList(mCatalog.id, mRequestPageNo, mRequestPageSize);
				} else if (mCatalog.hasProgram == 1) {// 获取节目列表
					return SourceWasu.getProgramList(mCatalog.id, mRequestPageNo, mRequestPageSize);
				} else {// 有二级目录的，获取一级目录
					return SourceWasu.getSubCatalog(mCatalog.id);
				}
			}
			return null;
		}

		@Override
		public void onPost(boolean resultState, Object obj) throws Exception {
			hideProgress();
			boolean nodata = true;
			if (resultState) {
				if (mCatalog.hasProgram == 1) {// 具体节目列表
					ProgramList pList = (ProgramList) obj;
					// mTotalCount = pList.total;
					if (pList.programList.size() > 0 || pList.total > 0) {
						nodata = false;
					} else {
						nodata = true;
					}
					/*
					 * if (mListItemManager == null) { int totalCount =
					 * mIsFastMode ? mTotalCount : pList.programList.size();
					 * mListItemManager = new
					 * DynamicListItemManager<Program>(totalCount,
					 * mPageSize, mCachePageCount, mItemListener); } // The
					 * server page number from 1 logI(
					 * "onPost, prev, total=" +
					 * mListItemManager.getTotalCount());
					 * mListItemManager.responseItems(pList.programList,
					 * pList.curPage - 1); logI( "onPost, next, total=" +
					 * mListItemManager.getTotalCount());
					 */
					if (mAdapter.mProgramList != null) {
						mAdapter.addProgramList(pList.programList);
					} else {
						mAdapter.setList(null, pList.programList);
					}
				} else {
					List<Catalog> cList = (List<Catalog>) obj;
					mAdapter.setList(cList, null);
					if (cList.size() > 0) {
						nodata = false;
					} else {
						nodata = true;
					}
				}
			}
			setNodataView(nodata);
			mIsLoading = false;
		}

		@Override
		public void onCancel(boolean isSuccess) {
			super.onCancel(isSuccess);
			Log.d("linghu", "onCancel---excute Nodata");
			setNodataView(false);
			hideProgress();
			mIsLoading = false;
		}

	}

	private void setNodataView(boolean noDataVisible) {
		// add by leiming32
		if (mAdapter.getCount() == 0 && noDataVisible) {
			mContentView.setVisibility(View.GONE);
			mNodataView.setVisibility(View.VISIBLE);
			if (this.isRequestFocus()) {
				mNodataView.requestFocus();
			}
		} else {
			mContentView.setVisibility(View.VISIBLE);
			if (this.isRequestFocus()) {
				mListView.requestFocus();
			}

			mNodataView.setVisibility(View.GONE);
		}
	}

	private void showProgress() {
		if (mNeedShowProgress) {
			showZixunLoading("");
		}
	}

	private void hideProgress() {
		hideZixunLoading();
	}

	private void loadAbstractFigure(int selectedItemPosition) {
		String afStr = null;
		logI("loadAbstractFigure, mCatalog: " + mCatalog + " Position:" + selectedItemPosition);

		if (mCatalog != null) {
			afStr = mCatalog.name;
			loadParam.imageUrl = mCatalog.picUrl;

			if (mListView != null && selectedItemPosition >= 0) {
				int itemId = selectedItemPosition;

				logI("loadAbstractFigure, hasProgram: " + mCatalog.hasProgram);

				if (mCatalog.hasProgram == 1) {
					Program p = (Program) mAdapter.getItem(itemId);
					if (p != null) {
						// afStr = p.name;
						logI("loadAbstractFigure, hasProgram, afStr: " + afStr);
						if (p.picUrl != null && p.picUrl.length() > 10) {
							logI("loadAbstractFigure, hasProgram, p.picUrl: " + p.picUrl);
							// loadParam.imageUrl = p.picUrl;
						}
					}
				} else {
					logI("hasProgram == 0, itemId:" + itemId);
					Catalog c = (Catalog) mAdapter.getItem(itemId);

					if (c != null) {
						afStr = c.name;
						logI("mCatalog != null, afStr:" + afStr);
						if (c.picUrl != null && c.picUrl.length() > 10) {
							logI("loadAbstractFigure, hasProgram == 0, c.picUrl: " + c.picUrl);
							loadParam.imageUrl = c.picUrl;
						}
					}
				}
			}
		} else if (mProgramList != null) {
			if (mListView != null && selectedItemPosition >= 0) {
				int itemId = selectedItemPosition;

				if (itemId >= 0 && itemId < mProgramList.size()) {
					String picUrl = mProgramList.get(itemId).picUrl;
					String nodePic = mProgramList.get(itemId).nodePic;
					if (picUrl != null && picUrl.length() > 10) {
						loadParam.imageUrl = picUrl;
						logI("loadAbstractFigure, mProgramList, picUrl: " + picUrl);
					} else {
						loadParam.imageUrl = nodePic;
						logI("loadAbstractFigure, mProgramList, nodePic: " + nodePic);
					}
					afStr = mProgramList.get(itemId).name;
				}
			}
		}

		logI("loadAbstractFigure, afStr:" + afStr);

		/*
		 * if ( afStr != null ){ mAbstractFigureTitle.setText(afStr); }else{
		 * mAbstractFigureTitle.setVisibility(View.GONE); }
		 */

		Global.getZinxunImageLoader().loadImage(loadParam, mAbstractFigure);
	}

	private void logI(String msg) {
		Log.i("zhang", msg);
	}

	// add by leiming32
	private boolean mIsLoading = false;

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		super.onTabUnselected(tab, ft);
		if (mAsyncTask != null && mIsLoading) {
			mAsyncTask.cancel(true);
			hideLoading();
			mIsLoading = false;
			mAsyncTask = null;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		super.onTabSelected(tab, ft);
		if (mAdapter != null && mAdapter.isEmpty()) {
			loadData();
		}
	}

	protected String getLogTag() {
		return "ZixunTVFragment";
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putAll(getArguments());
	};

	AlwaysLostFocusListener mAlwaysLostFocusListener;

	public void setAlwaysLostFocusListener(AlwaysLostFocusListener loseFocus) {
		mAlwaysLostFocusListener = loseFocus;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			synchronized(mFragmentStack){
				if (mFragmentStack.size() > 0) {
					isInsideOut = true;
					String lastString = mFragmentStack.pop();
					logd("onKeyDown() lastString="+lastString);
					// mAlwaysLostFocusListener.alwaysLostFocus(true);//added by
					// linghu
					FragmentManager fm = getFragmentManager();
					if (fm == null) {
						logd("ZixunFragment -- Onkeydown -- ****FragmentManager=null***");
						return true;
					}
					FragmentTransaction ft = fm.beginTransaction();
					ZixunFragment last = (ZixunFragment) fm.findFragmentByTag(lastString);
					// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
					ft.remove(ZixunFragment.this);
					logd("onKeyDown() mFragmentStack:" + mFragmentStack.size()+"lastFragment==null:"+(last==null));
					if(last!=null){
						ft.show(last);
					}
					
					ft.commit();
					fm.executePendingTransactions();
					
					return true;
				}
			}		
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			// 当focusedListview的onkeydown返回false的时候传给activity处理，activity传给fragment
			int pos = mListView.getSelectedItemPosition();
			if (pos == 0 && mFragmentStack.size() > 0) {
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (mFragmentStack.size() > 0) {
				return true;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public interface AlwaysLostFocusListener {
		public void alwaysLostFocus(boolean always);
	}

}
