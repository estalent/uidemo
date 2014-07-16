package com.yunos.tv.yingshi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.adapter.ZixunIndexGridAdapter;

public class ZixunIndexFragment extends IndexFragment {
	public static final String TAG = "ZixunIndexFragment";
	
	public ZixunIndexFragment() {}
	public ZixunIndexFragment(String catalogId, int containerViewId, Context context) {
		super(R.layout.frag_zixun_index, catalogId, containerViewId, context);
	}
	public static ZixunIndexFragment newInstance(String catalogId, int containerViewId, Context context) {
		ZixunIndexFragment yf = new ZixunIndexFragment(catalogId, containerViewId, context);
		return yf;
	}
	
	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		super.onViewCreated(rootView, savedInstanceState);
		mIndexAdapterr = new ZixunIndexGridAdapter(getContext(), R.layout.frag_zixun_index_item, this.mCatalogId);
		mIndexAdapterr.setHasCoverFlow(true);
		mIndexAdapterr.setFragment(this);
		mGridView.setAdapter(mIndexAdapterr);
		loadData();
	}
	
	@Override
	protected String getLogTag() {
		return TAG;
	}
}