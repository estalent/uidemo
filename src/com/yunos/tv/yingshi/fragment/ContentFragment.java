//package com.yunos.tv.yingshi.fragment;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.view.View;
//
//import com.yunos.tv.app.ui.TabFragmentActivity.TabListener;
//import com.yunos.tv.yingshi.R;
//import com.yunos.tv.yingshi.aidl.Catalog;
//
//public class ContentFragment implements TabListener {
//
//	private boolean isZixun = false;
//	private Catalog catalog;
//
//	FragmentManager manager;
//
//	public static ContentFragment instance(FragmentManager manager, boolean isZixun, Catalog catalog) {
//		ContentFragment mf = new ContentFragment();
//		mf.manager = manager;
//		mf.isZixun = isZixun;
//		mf.catalog = catalog;
//		return mf;
//	}
//
//	@Override
//	public void onCreateView(View v) {
//		{
//			Fragment fragment = this.manager.findFragmentByTag(catalog.name);
//			if (fragment == null) {
//				if (isZixun) {
//					fragment = ZixunFragment.newInstance(catalog, v.getId());
//				} else {
//					fragment = new YingshiFragment(catalog.id, v.getId());
//				}
//				
//				FragmentTransaction ft = this.manager.beginTransaction();
//				ft.add(v.getId(), fragment, catalog.name);
//				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//				ft.commit();
//			}
//		}
//	}
//
//}
