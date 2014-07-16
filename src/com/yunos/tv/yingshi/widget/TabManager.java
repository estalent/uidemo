package com.yunos.tv.yingshi.widget;

import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;

import com.yunos.tv.yingshi.fragment.BaseTVFragment;

/**
 * 每当activity使用leftnavbar添加一些tab时，使用tabmanager进行tab左右切换的管理。
 * activity在ondestory时需要调用reset
 *
 */
public class TabManager {
	static TabManager tabManager = null;
	
	private BaseTVFragment mCurrentFragment;
	
	public static final int MOVE_TO_PREV_TAB = 0;
	public static final int MOVE_TO_NEXT_TAB = 1;

	public static TabManager instance() {
		if (null == tabManager) {
			tabManager = new TabManager();
		}

		return tabManager;
	}

	Map<Integer, ActionBar.Tab> tabMap = new HashMap<Integer, ActionBar.Tab>();
	Map<ActionBar.Tab, Integer> revTabMap = new HashMap<ActionBar.Tab, Integer>();
	ActionBar bar = null;
	ActionBar.Tab currentTab = null;

	public void add(int index, ActionBar.Tab tab) {
		tabMap.put(index, tab);
		revTabMap.put(tab, index);
	}

	public void setActionBar(ActionBar bar) {
		this.bar = bar;
	}
	
	public void remove(ActionBar.Tab tab) {
		int index = revTabMap.remove(tab);
		tabMap.remove(index);
	}

	public void setCurrentTab(ActionBar.Tab tab) {
		this.currentTab = tab;
	}
	
	/**
	 * @param direction Either {@link TabManager#MOVE_TO_PREV_TAB} or {@link TabManager#MOVE_TO_NEXT_TAB}
	 * */
	public boolean moveTo(int direction) {
		if (null == currentTab) {
			return false;
		}
		
		if (direction == MOVE_TO_PREV_TAB) {
			if (revTabMap.containsKey(currentTab)) {
				int index = revTabMap.get(currentTab);
				if (index > 0) {
					ActionBar.Tab prevTab = tabMap.get(--index);
					this.bar.selectTab(prevTab);
					return true;
				}
			}
		} else if (direction == MOVE_TO_NEXT_TAB) {
			if (revTabMap.containsKey(currentTab)) {
				int index = revTabMap.get(currentTab);
				if (index < tabMap.size() - 1) {
					ActionBar.Tab nextTab = tabMap.get(++index);
					this.bar.selectTab(nextTab);
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void reset(){
		this.bar = null;
		this.revTabMap.clear();
		this.tabMap.clear();
		this.currentTab = null;
		mCurrentFragment = null;
	}
	
	/**
	 * 设置当前显示的fragment
	 * @param fragment
	 */
	public void setCurrentFragment(BaseTVFragment fragment) {
		this.mCurrentFragment = fragment;
	}
	public BaseTVFragment getCurrentFragment() {
		return mCurrentFragment;
	}
	
}
