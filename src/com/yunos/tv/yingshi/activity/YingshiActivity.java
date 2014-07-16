package com.yunos.tv.yingshi.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yunos.tv.app.LeftNavBar;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.imageload.utils.Md5FileNameGenerator;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.ProgramLists;
import com.yunos.tv.yingshi.BaseTvActivity;
import com.yunos.tv.yingshi.Const;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.aidl.Catalog;
import com.yunos.tv.yingshi.fragment.BaseTVFragment;
import com.yunos.tv.yingshi.fragment.SearchFragment;
import com.yunos.tv.yingshi.fragment.YingshiFragment;
import com.yunos.tv.yingshi.fragment.YingshiIndexFragment;
import com.yunos.tv.yingshi.fragment.ZixunFragment;
import com.yunos.tv.yingshi.fragment.ZixunFragment.AlwaysLostFocusListener;
import com.yunos.tv.yingshi.fragment.ZixunIndexFragment;
import com.yunos.tv.yingshi.widget.TabManager;

public class YingshiActivity extends BaseTvActivity implements AlwaysLostFocusListener {
	public static final String TAG = "YingshiActivity";
	
	private LeftNavBar mLeftNavBar;
	boolean hasToast = false; // 提示按返回键回到顶部菜单
	
	public void setHasToast(boolean b) {
		hasToast = b;
	}
	
	public boolean hasToast() {
		return hasToast;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logd("onCreate");
		ZixunFragment.clearFragmentStack(YingshiActivity.this);
		
		setContentView(R.layout.yingshi);
//		ViewManager.instance().setMaxViewNum(24 + 1);
//		ViewManager.instance().init(R.layout.frag_yingshi_item);
		
		release();
		
//		deleteHttpCache();
		
//		testLoad(0);
	}
	
	private void deleteHttpCache() {
		new Thread() {
			public void run() {
				
				try {
					logi("==Thread========start===");
					
					logi("==Thread========35176===");
					List<Program> programList = SourceWasu.getHotProgramList("35176");
					for (Program p :programList) {
						String n = Md5FileNameGenerator.getInstance().getLocalName(p.picUrl);
						Log.i("aabb", n);
						File f = new File("/mnt/sdcard/Android/data/com.yunos.tv.yingshi/cache/cache_http/" + n);
						if (f.exists()) {
							Log.i("aabb", "====Thread====delete===" + n);
							f.delete();
						}
					}
					
					logi("==Thread========35181===");
					programList = SourceWasu.getHotProgramList("35181");
					for (Program p :programList) {
						String n = Md5FileNameGenerator.getInstance().getLocalName(p.picUrl);
						Log.i("aabb", n);
						File f = new File("/mnt/sdcard/Android/data/com.yunos.tv.yingshi/cache/cache_http/" + n);
						if (f.exists()) {
							Log.i("aabb", "===Thread=====delete===" + n);
							f.delete();
						}
					}
					
					logi("==Thread========35177===");
					programList = SourceWasu.getHotProgramList("35177");
					for (Program p :programList) {
						String n = Md5FileNameGenerator.getInstance().getLocalName(p.picUrl);
						Log.i("aabb", n);
						File f = new File("/mnt/sdcard/Android/data/com.yunos.tv.yingshi/cache/cache_http/" + n);
						if (f.exists()) {
							Log.i("aabb", "===Thread=====delete===" + n);
							f.delete();
						}
					}
					
					List<ProgramLists> ps = SourceWasu.getHomeProgramList("35181", 6);
					for (ProgramLists pl : ps) {
						for (Program p : pl.programList) {
							String n = Md5FileNameGenerator.getInstance().getLocalName(p.picUrl);
							Log.i("aabb", n);
							File f = new File("/mnt/sdcard/Android/data/com.yunos.tv.yingshi/cache/cache_http/" + n);
							if (f.exists()) {
								Log.i("aabb", "=Thread=======delete===" + n);
								f.delete();
							}
						}
					}
					
					
					logi("==Thread=====end===");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	@Override
	protected void onDestroy(){
		TabManager.instance().reset();
//		ImageLoader.clearMemory();
//		ViewManager.instance().clear();
		
		super.onDestroy();
		logd("onDestroy");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		BaseTVFragment f = TabManager.instance().getCurrentFragment();
		if (f != null && f.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if (mLeftNavBar != null && !mLeftNavBar.handleBackPress()){
			super.onBackPressed();
		}
	}
	
	private void release() {
		Intent it = getIntent();
		if (it == null) {
	 		showToast(R.string.error_param);
	 		loge(getString(R.string.error_param));
			finish();
			return;
		}
		ArrayList<Catalog> catalogList = it.getParcelableArrayListExtra("catalog");
		if (catalogList != null) {
			printCatalog(catalogList);
			setupTabs(catalogList);
		} else {
			showToast(R.string.catalog_null);
			loge(getString(R.string.catalog_null));
			finish();
			return;
		}
	}
	
	private void printCatalog(List<Catalog> catalogList) {
		logd("===========YingshiActivity intent catalogList:=====start=====\n");
		for (Catalog c : catalogList) {
			logd("name:" + c.name + ",id:" + c.id + ",type:" + c.type + ",hasProgram:" + c.hasProgram
					 + ",attr:" + c.attr + ",picUrl:" + c.picUrl);
		}
		logd("===========YingshiActivity intent catalogList=====end=====\n");
	}
	
	/**
	 * //需要在setContentView之后初始化LeftNavBar LeftNavBar.TYPE_LEFT_NAVBAR 打开左边导航栏界面
	 * LeftNavBar.TYPE_TOP_NAVBAR 打开顶部导航栏界面
	 * **/
	private LeftNavBar getLeftNavBar() {
		if (mLeftNavBar == null) {
			mLeftNavBar = new LeftNavBar(this, LeftNavBar.TYPE_TOP_NAVBAR);
            mLeftNavBar.setOnClickHomeListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	finish();
                }
            });
		}
		return mLeftNavBar; 
	}
	
	private void testLoad(final int type) {
		runTask(new WorkAsyncTask<Catalog>(YingshiActivity.this) {
			@Override
			public Catalog doProgress() throws Exception {
				List<Catalog> list = SourceWasu.getRootCatalog(type);
				for (int i = 0; i < list.size(); ++i) {
					logd("testLoad() list " + i + ": " + list.get(i).name);
				}
				return list.get(3);//2高清专题
			}
			
			@Override
			public void onPost(boolean resultState, Catalog resultObject) throws Exception {
				if (resultState) {
					setupTabs(resultObject.child);
				}
			}
		});
	}

	private void setupTabs(ArrayList<Catalog> catalogList) {

		ActionBar bar = getLeftNavBar();
		
		TabManager.instance().setActionBar(bar);
		
		bar.removeAllTabs();
		bar.setLogo(R.drawable.tui_ic_huashulogo);
		Intent intent = getIntent();
		int k = 0;
		
		String catalogId = intent.getStringExtra("catalog_id");
		String catalogName = intent.getStringExtra("catalog_name"); 
		
		//加搜索
		ActionBar.Tab tabSearch = bar.newTab().setText(R.string.search);
		SearchFragment tf = new SearchFragment(R.id.yingshi_main, this);
		Bundle bundle = new Bundle();
		bundle.putString("catalog_id", catalogId);
		bundle.putString("catalog_name", catalogName);
		tf.setArguments(bundle);
		tabSearch.setTabListener(tf);
		bar.addTab(tabSearch, false);
		TabManager.instance().add(k++, tabSearch);
		
//		//加首页
		boolean hasIndex = false;
		List<String> list = Arrays.asList(Const.INDEX_IDS);
		if (list.contains(catalogId)) {
			hasIndex = true;
		}
		int filmType = intent.getIntExtra("type", 1); //0是资讯  1是影视
		ActionBar.Tab tabIndex = bar.newTab().setText(R.string.index);
		logi("filmType -- " + filmType + ", catalog_id:" + intent.getStringExtra("catalog_id") + ",catalogList.size():" + catalogList.size());
		if (hasIndex) {
			if (filmType == 1) { // 影视
				YingshiIndexFragment indexFragment = YingshiIndexFragment.newInstance(catalogId, R.id.yingshi_main, this, catalogName);
				tabIndex.setTabListener(indexFragment);
				bar.addTab(tabIndex, true);
				TabManager.instance().add(k++, tabIndex);
			} else if (filmType == 0) { // 资讯
				ZixunIndexFragment indexFragment = ZixunIndexFragment.newInstance(catalogId, R.id.yingshi_main, this);
				tabIndex.setTabListener(indexFragment);
				tabIndex.setTag(catalogName);
				bar.addTab(tabIndex, true);
				TabManager.instance().add(k++, tabIndex);
			}
		}
		
		for (int i = 0, len = catalogList.size(); i < len; ++i) {
			Catalog iCatalog = catalogList.get(i);
			ActionBar.Tab tab = bar.newTab().setText(iCatalog.name);
			//hasProgram 0:请求子目录  1:请求节目列表
			//type=1:影视类grid  type=0:非影视类list
			if (iCatalog.hasProgram == 1 && iCatalog.type == 1) {//请求节目列表并且是影视类
				YingshiFragment tf1 = YingshiFragment.newInstance(iCatalog.id, R.id.yingshi_main, this, catalogName + "#" + iCatalog.name);
				tab.setTabListener(tf1);
				boolean selected = (i == 0 && !hasIndex) ? true : false;
				bar.addTab(tab, selected);
			} else {
				ZixunFragment tf1 = ZixunFragment.newInstance(iCatalog, R.id.yingshi_main, this, false, catalogName + "#" + iCatalog.name);
				tab.setTabListener(tf1);
				tf1.setAlwaysLostFocusListener(this);
				boolean selected = (i == 0 && !hasIndex) ? true : false;
				bar.addTab(tab, selected);
			}
			TabManager.instance().add(k++, tab);
		}
		
		logd("setupTabs ok");
	}
	
	@Override
	protected String getLogTag() {
		return TAG;
	}

	@Override
	public void alwaysLostFocus(boolean always) {
		mLeftNavBar.alwaysLostFocus(always);
	}
	
}