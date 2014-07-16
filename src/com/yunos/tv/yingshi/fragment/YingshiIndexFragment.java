package com.yunos.tv.yingshi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.adapter.YingshiIndexGridAdapter;

public class YingshiIndexFragment extends IndexFragment {
	public static final String TAG = "YingshiIndexFragment";

	public YingshiIndexFragment() {}
	public YingshiIndexFragment(String catalogId, int containerViewId, Context context, String catalogName) {
		super(R.layout.frag_yingshi_index, catalogId, containerViewId, context, catalogName);
	}	

	public static YingshiIndexFragment newInstance(String catalogId, int containerViewId, Context context, String catalogName) {
		YingshiIndexFragment yf = new YingshiIndexFragment(catalogId, containerViewId, context, catalogName);
		return yf;
	}

	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		super.onViewCreated(rootView, savedInstanceState);
		
		mIndexAdapterr = new YingshiIndexGridAdapter(getContext(), R.layout.frag_yingshi_index_item, this.mCatalogId);
		mIndexAdapterr.setHasCoverFlow(true);//TODO
		mIndexAdapterr.setFragment(this);
		mGridView.setAdapter(mIndexAdapterr);
		loadData();
	}
	
	protected String getLogTag() {
		return TAG;
	}

}