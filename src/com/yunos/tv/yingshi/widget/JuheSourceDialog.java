package com.yunos.tv.yingshi.widget;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.source.entity.JuheSource;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.adapter.JuheSourceAdapter;

public class JuheSourceDialog extends Dialog {
	private GridView mGridView;
	
	private JuheSourceAdapter mAdapter;

	private int mSelected = -1;
	
	public JuheSourceDialog(Context context, ArrayList<JuheSource> player, final int firstSelectIndex) {
		super(context, R.style.my_dialog);
		setContentView(R.layout.dialog_juhe);
		
		mGridView = (GridView) findViewById(R.id.grid_xuanji);
		mAdapter = new JuheSourceAdapter(context, player);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new TvOnItemClickListener() {
			@Override
			public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
				mSelected = position;
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
