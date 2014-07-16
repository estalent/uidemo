package com.yunos.tv.yingshi.fragment;

import android.view.KeyEvent;

public interface FragmentOnKeyListener {
	public boolean onKeyUp(int keyCode, KeyEvent event);
	
	public boolean onKeyDown(int keyCode, KeyEvent event);
}
