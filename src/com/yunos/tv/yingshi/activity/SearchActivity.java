package com.yunos.tv.yingshi.activity;

import yunos.tv.app.LeftNavBar;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aliyun.imageload.ImageLoader;
import com.yunos.tv.yingshi.BaseTvActivity;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.fragment.SearchFragment;
import com.yunos.tv.yingshi.widget.TabManager;

public class SearchActivity extends BaseTvActivity {
	private LeftNavBar mLeftNavBar;	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
		
//	@Override
//	public Theme getTheme() {
//		return AuiResourceFetcher.getTheme(this);
//	}
//	
//	@Override
//	public Resources getResources() {
//		return AuiResourceFetcher.getResources(getBaseContext());
//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yingshi);
		getLeftNavBar();
		setupTabs();
	}
	
	private void setupTabs(){
		ActionBar bar = getLeftNavBar();
		bar.removeAllTabs();
		bar.setTitle(R.string.search);
		//bar.setLogo(R.drawable.tui_ic_huashulogo);
		SearchFragment sf = new SearchFragment(R.id.yingshi_main, this);

		Intent it = getIntent();
		String mShowType = it.getStringExtra("show_type");
		
		Bundle b = new Bundle();
		b.putString("catalog_name", getString(R.string.all));
		b.putString("show_type", mShowType);
		sf.setArguments(b);
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.yingshi_main, sf);
		fragmentTransaction.commit();
		/*
		tab.setTabListener(sf);
		//tab.setTag(1);
		bar.addTab(tab, true);
		*/
	}
	
	//需要在setContentView之后初始化LeftNavBar，LeftNavBar.TYPE_TOP_NAVBAR type表示打开顶部导航栏
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
	
	@Override
	protected void onDestroy(){
		ImageLoader.clearMemory();
		super.onDestroy();
	}
}
