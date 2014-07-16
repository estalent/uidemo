package com.yunos.tv.yingshi.activity;

import java.util.List;

import yunos.tv.app.LeftNavBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.aliyun.base.WorkAsyncTask;
import com.yunos.tv.app.widget.FocusedBasePositionManager;
import com.yunos.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tv.app.widget.FocusedGridView;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.BaseTvActivity;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.adapter.YingshiGridAdapter;
import com.yunos.tv.yingshi.dao.sql.SqlFavorDao;
import com.yunos.tv.yingshi.dao.sql.SqlLastPlayDao;

/**
 * 收藏或历史播放记录
 * 
 */
public class FavorActivity extends BaseTvActivity {
	protected static final String TAG = "FavorActivity";
	public static final String FAV = "FAV";
	public static final String HIS = "HIS";

	private LeftNavBar mLeftNavBar;

	private FocusedGridView mGridView;
	private YingshiGridAdapter mAdapter;
	private View mNoData;
	private String mTitle;
	private int mCurrSelectedPosition = 0;
	private String titleName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favor);
		mTitle = getIntent().getStringExtra("title");

		mNoData = findViewById(R.id.nodata_favor);
		if (mTitle.equals(FAV)) {
			titleName = getString(R.string.favor_title);
			TextView t = (TextView) findViewById(R.id.nodata_text1);
			t.setText(R.string.favor_null);
		} else if (mTitle.equals(HIS)) {
			titleName = getString(R.string.lastest_play);
			TextView t = (TextView) findViewById(R.id.nodata_text1);
			t.setText(R.string.play_null);
		}
		// ViewManager.instance().setMaxViewNum(24 + 1);
		// ViewManager.instance().init(R.layout.frag_yingshi_item);

		setupTabs();

		float mScaleValue = getResources().getDimension(R.dimen.grid_middle_w) / getResources().getDimension(R.dimen.image_middle_w);
		mGridView = (FocusedGridView) findViewById(R.id.gridimage);
		mGridView.setItemScaleValue(mScaleValue, mScaleValue);
		// mGridView.setFocusResId(R.id.image_yingshi_grid);
		mGridView.setFrameRate(Config.FRAME_GRIDVIEW);
		mGridView.setScrollDuration(Config.SCROLLING_DURATION_GRIDVIEW);
		mGridView.setFocusMode(FocusedBasePositionManager.FOCUS_SYNC_DRAW);
		mGridView.setOnScrollListener(null);
		mGridView.setFocusResId(R.drawable.tui_bg_focus);
		mGridView.setFocusShadowResId(R.drawable.tui_grid_focus);
		mGridView.setFocusViewId(R.id.image_yingshi_grid);
		mGridView.setOnItemSelectedListener(mOnItemSelectedListener);

		mAdapter = new YingshiGridAdapter(this, TAG, true, false);
		mGridView.setAdapter(mAdapter);
		mGridView.setSelected(true);
		mGridView.setOnItemClickListener(new TvOnItemClickListener() {
			@Override
			public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
				mCurrSelectedPosition = position;
				Intent intent = new Intent(FavorActivity.this, YingshiDetailActivity.class);
				Program p = mAdapter.getItem(position);
				if (p.isJuheVideo) {
					intent.putExtra("isJuheVideo", true);
					intent.putExtra("program_id", p.id);
					intent.putExtra("classId", p.classId);
				} else {
					intent.putExtra("program_id", p.id);
//					intent.putExtra("ppvPath", p.ppvPath);
				}
				intent.putExtra("name", p.name);
				intent.putExtra("from", mTitle);
				startActivity(intent);
			}
		});
	}

	private FocusItemSelectedListener mOnItemSelectedListener = new FocusItemSelectedListener() {
		@Override
		public void onItemSelected(View v, int position, boolean isSelected, View parent) {
			AdapterView adapterView = (AdapterView) parent;
			if (v == null || adapterView.getChildCount() == 0) {
				return;
			}
			TextView t = (TextView) v.findViewById(R.id.text_yingshi_grid);
			t.setSelected(isSelected);
			if (isSelected) { 
				t.setTextColor(Color.parseColor("#FFffffff"));
				t.setEllipsize(TruncateAt.MARQUEE);
				// t.startMarquee();
			} else {
				t.setTextColor(Color.parseColor("#FF999999"));
				// t.stopMarquee();
				t.setEllipsize(TruncateAt.END);
			}
		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		getLeftNavBar().setTitle(titleName);
		loadData(mTitle);
	}

	private void loadData(String name) {
		runTask(new WorkAsyncTask<List<Program>>(this, false) {
			@Override
			public List<Program> doProgress() throws Exception {
				if (mTitle.equals(FAV)) {
					return SqlFavorDao.getFavorList();
				} else {
					return SqlLastPlayDao.getLastPlayList(100);
				}
			}

			@Override
			public void onPost(boolean resultState, List<Program> resultObject) throws Exception {
				if (resultState) {
					mAdapter.setProgramList(resultObject);
					mAdapter.notifyDataSetChanged();
					setNodataView();
				}
			}
		});
	}

	private void setNodataView() {
		int size = mAdapter.getCount();
		if (size > 0) {
			getLeftNavBar().setTitle(titleName + "(" + size + ")");
		}
		if (size == 0) {
			mNoData.setVisibility(View.VISIBLE);
			mGridView.setVisibility(View.GONE);
		} else {
			mNoData.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
			mGridView.requestFocus();

			int diff = mCurrSelectedPosition - (mAdapter.getCount() - 1);
			if (diff > 0) {
				// for (int i = 0; i < diff; ++i) {
				// mGridView.subSelectPosition();
				// }

				mGridView.setSelection(mAdapter.getCount() - 1);
			} else {
				mGridView.setSelection(mCurrSelectedPosition);
			}

		}
	}

	// 需要在setContentView之后初始化LeftNavBar，LeftNavBar.TYPE_TOP_NAVBAR type表示打开顶部导航栏
	private LeftNavBar getLeftNavBar() {
		if (mLeftNavBar == null) {
			mLeftNavBar = new LeftNavBar(this, LeftNavBar.TYPE_TOP_NAVBAR);
			mLeftNavBar.setOnClickOptionListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		return mLeftNavBar;
	}

	private void setupTabs() {
		LeftNavBar bar = getLeftNavBar();
		bar.setTitle(titleName);
		bar.setLogo(R.drawable.tui_ic_huashulogo);
	}

}
