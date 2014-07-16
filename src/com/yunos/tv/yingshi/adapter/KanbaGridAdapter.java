package com.yunos.tv.yingshi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aliyun.base.info.MobileInfo;
import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.entity.ImageScaleType;
import com.yunos.tv.source.entity.ChannelInfo;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;

public class KanbaGridAdapter extends BaseAdapter {
	private List<ChannelInfo> mChannelList;
	private ImageLoadParam loadParam;
	private LayoutInflater mInflater;
	
	public List<ChannelInfo> getChannelList() {
		return mChannelList;
	}

	public void setChannelList(List<ChannelInfo> mChannelList) {
		this.mChannelList = mChannelList;
	}
	
	public void deleteChannel(int postion) {
		this.mChannelList.remove(postion);
	}

	/**
	 * 
	 * @param context
	 * @param sideDip 两边各多少dip
	 * @param gapDip  item间隔多少dip
	 * @param columNum 列数
	 */
	public KanbaGridAdapter(Context context) {
		mChannelList = new ArrayList<ChannelInfo>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		int w = MobileInfo.dip2px(context, 180);
		int h = MobileInfo.dip2px(context, 129);
		loadParam = new ImageLoadParam("", w, h);
		loadParam.setWorkCache(true);
		loadParam.setImageScaleType(ImageScaleType.EXACTLY_STRETCHED);
	}
	
	public void clear(){
		this.mChannelList.clear();
	}
	
	@Override
	public int getCount() {
		return (mChannelList!=null) ? mChannelList.size() : 0;
	}

	@Override
	public ChannelInfo getItem(int position) {
		return (mChannelList!=null) ? mChannelList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.frag_kanba_item, null);
		}
		
		ImageView image = (ImageView) convertView.findViewById(R.id.channel_icon);
		
		ChannelInfo info = mChannelList.get(position);
		loadParam.imageUrl = info.picUrl;
		Global.getPlaybackImageLoader().loadImage(loadParam, image);

		return convertView;
	}

}
