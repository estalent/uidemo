package com.yunos.tv.yingshi.activity;

import yunos.tv.app.LeftNavBar;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.yingshi.BaseTvActivity;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.fragment.KanbaFragment;
import com.yunos.tv.yingshi.fragment.PlaybackFragment;

public class PlaybackActivity extends BaseTvActivity {
	public static final String TAG = "PlaybackActivity";
	private LeftNavBar mLeftNavBar;
	private String playBackUrls = null;//传给播放器的播放参数
	
	/**
	 * 第一个fragment准备好就OK
	 * @param b
	 */
	public void setPlayBackUrls(String b) {
		synchronized (this) {
			playBackUrls = b;
		}
	}
	
	public String getPlayBackUrls() {
		synchronized (this) {
			return playBackUrls;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yingshi);
		setupTabs();
	}

	@Override
	public void onBackPressed() {
		if (mLeftNavBar != null && !mLeftNavBar.handleBackPress()) {
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && playBackUrls == null) {
			logd("waiting for all channel load ok! KEYCODE_DPAD_RIGHT handled!");
			return true;
		}
		return super.dispatchKeyEvent(event);
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
	
	private void setupTabs() {
		ActionBar bar = getLeftNavBar();
		bar.removeAllTabs();
		bar.setLogo(R.drawable.tui_ic_huashulogo);
		
		ActionBar.Tab all = bar.newTab().setText("全部");
		PlaybackFragment  allFragment = PlaybackFragment.newInstance(R.id.yingshi_main, "全部", "", PlaybackActivity.this);
		all.setTabListener(allFragment);
		bar.addTab(all, true);

		/*ActionBar.Tab local = bar.newTab().setText("本地");
		PlaybackFragment  localFragment = PlaybackFragment.newInstance(R.id.yingshi_main, "本地", "1", PlaybackActivity.this);
		local.setTabListener(localFragment);
		bar.addTab(local, false);*/
		
		ActionBar.Tab satellite = bar.newTab().setText("卫视");
		PlaybackFragment  satelliteFragment = PlaybackFragment.newInstance(R.id.yingshi_main, "卫视", "3", PlaybackActivity.this);
		satellite.setTabListener(satelliteFragment);
		bar.addTab(satellite, false);
		
		ActionBar.Tab cctv = bar.newTab().setText("央视");
		PlaybackFragment  cctvFragment = PlaybackFragment.newInstance(R.id.yingshi_main, "央视", "2", PlaybackActivity.this);
		cctv.setTabListener(cctvFragment);
		bar.addTab(cctv, false);
		
		ActionBar.Tab kanba = bar.newTab().setText("看吧");
		KanbaFragment  kanbaFragment = KanbaFragment.newInstance(R.id.yingshi_main, "看吧", "2", PlaybackActivity.this);
		kanba.setTabListener(kanbaFragment);
		bar.addTab(kanba, false);
	}
	
	@Override
	protected String getLogTag() {
		return TAG;
	}
	
}
