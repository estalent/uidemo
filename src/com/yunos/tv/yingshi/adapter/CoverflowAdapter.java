package com.yunos.tv.yingshi.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aliyun.base.info.MobileInfo;
import com.aliyun.imageload.ImageLoadParam;
import com.yunos.tv.app.widget.CoverFlow;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.widget.MaskImageView;

public class CoverflowAdapter extends BaseAdapter {
	private List<Program> mPorgramList;
	private ImageLoadParam mLoadParam;
	private Context mContext;
	private LayoutInflater mInflater;
	int imageWidth;
	int imageHeight;

	public CoverflowAdapter(Context context, String what) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageWidth = MobileInfo.dip2px(mContext, mContext.getResources().getDimension(R.dimen.image_coverflow_w));
		imageHeight = MobileInfo.dip2px(mContext, mContext.getResources().getDimension(R.dimen.image_coverflow_h));
		mLoadParam = new ImageLoadParam("", imageWidth, imageHeight);
		mLoadParam.what = what;
		mLoadParam.setWorkCache(true);
	}

	public void setProgramList(List<Program> porgramList) {
		this.mPorgramList = porgramList;
	}

	public List<Program> getProgramList() {
		return this.mPorgramList;
	}

	@Override
	public int getCount() {
//		return mPorgramList == null || mPorgramList.size() == 0 ? 0 : Integer.MAX_VALUE;
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		if (mPorgramList != null && mPorgramList.size() > 0) {
			position = position % mPorgramList.size();
		} 
		String n = mPorgramList == null ? ""  : mPorgramList.get(position).name;
		return n;
	}

	@Override
	public long getItemId(int position) {
		if (mPorgramList != null && mPorgramList.size() > 0) {
			position = position % mPorgramList.size();
		}
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MaskImageView imageView;
		TextView text;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_coverflow, null);
			convertView.setLayoutParams(new CoverFlow.LayoutParams(imageWidth, imageHeight));
			imageView = (MaskImageView) convertView.findViewById(R.id.image_yingshi_grid);
			text = (TextView) convertView.findViewById(R.id.text_gaoqing);
		} else {
			imageView = (MaskImageView) convertView.findViewById(R.id.image_yingshi_grid);
			text = (TextView) convertView.findViewById(R.id.text_gaoqing);
		}
		if (mPorgramList != null && mPorgramList.size() > 0) {
			position = position % mPorgramList.size();
			Program p  = mPorgramList.get(position);
			if (!p.type.equals("program")) {
				text.setVisibility(View.VISIBLE);
				text.setText(R.string.topic);
			} else {
				text.setVisibility(View.GONE);
			}
			imageView.setFocusable(false);
			mLoadParam.imageUrl = p.picUrl; 
			mLoadParam.setFromAsset(true);
			if (!mLoadParam.imageUrl.equals(imageView.tagImage)) {
				Global.getCoverFlowImageLoader().loadImage(mLoadParam, imageView);
			}
		} else {
			text.setVisibility(View.GONE);
		}
		return convertView;
	}
}
