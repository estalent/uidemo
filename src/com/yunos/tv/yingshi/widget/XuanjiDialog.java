package com.yunos.tv.yingshi.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.app.widget.TvOnViewClickListener;
import com.yunos.tv.source.entity.JuheSource;
import com.yunos.tv.source.entity.Tag;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.adapter.XuanjiAdapter;

public class XuanjiDialog extends Dialog {
	private GridView /*FocusedGridView */mGridView;
	
	private XuanjiAdapter mAdapter;

	private int mSelected = -1;
	
	
	
	/**
	 * 
	 * @param context
	 * @param total 总共的集数
	 */
	public XuanjiDialog(Context context, Tag tag, final int firstSelectIndex) {
		super(context, R.style.my_dialog);
		setContentView(R.layout.dialog_xuanji);
		
		mGridView = (GridView) findViewById(R.id.grid_xuanji);
		mAdapter = new XuanjiAdapter(context, tag);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new TvOnItemClickListener() {
			@Override
			public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
				mSelected = position;
				dismiss();
			}
		});
		findViewById(R.id.text_close).setOnClickListener(new TvOnViewClickListener() {
			
			@Override
			public void onClicked(View v) {
				dismiss();
			}
		});
		setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				mGridView.setSelection(firstSelectIndex);
			}
		});
	}
	
	public XuanjiDialog(Context context, JuheSource urlList, final int firstSelectIndex, String classId) {
		super(context, R.style.my_dialog);
		setContentView(R.layout.dialog_xuanji);
		
		mGridView = (GridView) findViewById(R.id.grid_xuanji);
		mAdapter = new XuanjiAdapter(context, urlList, classId);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new TvOnItemClickListener() {
			@Override
			public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
				mSelected = position;
				dismiss();
			}
		});
		findViewById(R.id.text_close).setOnClickListener(new TvOnViewClickListener() {
			
			@Override
			public void onClicked(View v) {
				dismiss();
			}
		});
		setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				mGridView.setSelection(firstSelectIndex);
			}
		});
	}
	
	public int getSelected() {
		return this.mSelected;
	}
	
}
