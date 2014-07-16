package com.yunos.tv.yingshi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.aliyun.base.info.MobileInfo;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.ProgramLists;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.fragment.IndexFragment;
import com.yunos.tv.yingshi.widget.HeadViewLayout;

/**
 * 它有两个子类  YingshiIndexGridAdapter , ZixunIndexGridAdapter
 * @author leiming32
 *
 */
public abstract class IndexGridAdapter extends UpdateAdapter {

	protected List<ProgramLists> mProgramLists;
	protected LayoutInflater mInflater;
	protected int mNumColumns = 0;
	protected Context mContext;
	protected HeadViewLayout mHeadView = null;
	protected int mItemLayoutId;
	private IndexFragment mIndexFragment; 

	public IndexGridAdapter(Context context, int itemLayoutId) {
		this(context, itemLayoutId, true);
	}
	
	public IndexGridAdapter(Context context, int itemLayoutId, boolean isPreLoad) {
		super(isPreLoad);
		mContext = context;
		mItemLayoutId = itemLayoutId;
		mProgramLists = new ArrayList<ProgramLists>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mNumColumns = context.getResources().getInteger(R.integer.yingshi_grid_colum_num);
	}
	
	public List<ProgramLists> getProgramLists() {
		return mProgramLists;
	}

	public void setProgramLists(List<ProgramLists> programLists) {
		this.mProgramLists = programLists;
	}

	public void deleteProgramLists(int postion) {
		this.mProgramLists.remove(postion);
	}
	
	public void setFragment(IndexFragment indexFragment) {
		this.mIndexFragment = indexFragment;
	}

	public void clear() {
		this.mProgramLists.clear();
	}

	@Override
	public int getCount() {
		if (isPreLoad && mProgramLists.isEmpty()) {
			return 18;
		}
		return this.hasCoverFlow() ? mProgramLists.size() * mNumColumns + mNumColumns : mProgramLists.size() * mNumColumns;
	}

	@Override
	public Program getItem(int position) {
		if (mProgramLists.isEmpty()) {
			return null;
		}
		if (this.hasCoverFlow()) {
			if (position < mNumColumns) {
				return null;
			} else {
				return mProgramLists.get((position - mNumColumns) / mNumColumns).programList.get((position - mNumColumns) % mNumColumns);
			}
		}
		return mProgramLists.get(position / mNumColumns).programList.get(position % mNumColumns);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		if (this.hasCoverFlow()) {
			return 3;
		}
		return super.getViewTypeCount();
	}

	@Override
	public int getItemViewType(int position) {
		if (this.hasCoverFlow()) {
			if (position == 0) {
				return 0;
			} else if (position < mNumColumns) {
				return 1;
			} else {
				return 2;
			}
		} else {
			return super.getItemViewType(position);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (hasCoverFlow()) {
			if (position == 0) {
				if (mHeadView == null) {
					mHeadView = mIndexFragment.getHeadViewLayout();
				}
				return mHeadView;
			}
			if (position < mNumColumns) {
				if (convertView == null) {
					convertView = new HeadViewLayout(mContext);
					int h = MobileInfo.dip2px(mContext, mContext.getResources().getDimension(R.dimen.grid_coverflow_h));
					convertView.setLayoutParams(new AbsListView.LayoutParams(0, h));
					convertView.setFocusable(false);
					convertView.setVisibility(View.GONE);
				}
				return convertView;
			}
		}

		if (null == convertView) {
			convertView = mInflater.inflate(mItemLayoutId, null);
		}
		
		updateView(position, convertView);

		return convertView;
	}

	public abstract void updateView(int position, View convertView);

}
