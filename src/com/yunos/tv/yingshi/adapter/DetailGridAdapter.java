package com.yunos.tv.yingshi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.base.info.MobileInfo;
import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.entity.ImageArg;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.Const;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.widget.YingshiImageView;

public class DetailGridAdapter extends UpdateAdapter {

	public List<Program> programList;
	private ImageLoadParam loadParam;
	private LayoutInflater mInflater;
	private boolean isJuheVideo = false;

	public void setIsJuheVideo(boolean isJuheVideo) {
		this.isJuheVideo = isJuheVideo;
	}
	
	public DetailGridAdapter(Context context, String what) {
		this(context, what, true);
	}

	public DetailGridAdapter(Context context, String what, boolean preLoad) {
		super(preLoad);
		programList = new ArrayList<Program>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		int w = MobileInfo.dip2px(context, context.getResources().getDimension(R.dimen.image_small_w));
		int h = MobileInfo.dip2px(context, context.getResources().getDimension(R.dimen.image_small_h));
		loadParam = new ImageLoadParam("", w, h);
		loadParam.what = what;
		loadParam.setImageArg(ImageArg.thumbnail);
	}

	@Override
	public int getCount() {
		if (isPreLoad && programList.size() == 0) {
			return 6;
		}
		return programList.size();
	}

	@Override
	public Program getItem(int position) {
		if (programList.isEmpty()) {
			return null;
		}
		return programList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_hlist_maybe_love, null);
			ViewHolder holder = new ViewHolder();
			convertView.setTag(holder);
			holder.image = (YingshiImageView) convertView.findViewById(R.id.image_maybelove);
			holder.text = (TextView) convertView.findViewById(R.id.text_maybelove);
		}
		
		updateView(position, convertView);
		
		return convertView;
	}
	
	@Override
	public void updateView(int position, View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Program info = getItem(position);
		if (info == null) {
			holder.image.setIsDrawMask(false);
			return;
		}
		holder.image.setIsDrawMask(true);
		if (isJuheVideo || info.isJuheVideo) {
			loadParam.imageUrl = info.picUrl;
			loadParam.setWorkCache(true);
		} else {
			loadParam.setWorkCache(false);
			int index = info.picUrl.lastIndexOf(".");
			if (index > 0) {
				String suffix = info.picUrl.substring(index);
				loadParam.imageUrl = info.picUrl + Const.IMAGE_SIZE_DETAIL_SMALL + suffix;
			} else {
				loadParam.imageUrl = info.picUrl;
			}
		}
		Global.getMaskImageLoader().loadImage(loadParam, holder.image);
		holder.text.setText(info.name);
	}

	static class ViewHolder {
		YingshiImageView image;
//		MarqueeTextView text;
		TextView text;
	}

}
