package com.yunos.tv.yingshi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.imageload.ImageLoadParam;
import com.yunos.tv.source.entity.ChannelInfo;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;

public class PlaybackGridAdapter extends BaseAdapter {
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
	 * @param sideDip
	 *            两边各多少dip
	 * @param gapDip
	 *            item间隔多少dip
	 * @param columNum
	 *            列数
	 */
	public PlaybackGridAdapter(Context context) {
		mChannelList = new ArrayList<ChannelInfo>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		loadParam = new ImageLoadParam("");
	}

	public void clear() {
		this.mChannelList.clear();
	}

	@Override
	public int getCount() {
		return (mChannelList != null) ? mChannelList.size() : 0;
	}

	@Override
	public ChannelInfo getItem(int position) {
		return (mChannelList != null) ? mChannelList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.frag_playback_item, null);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.channel_icon);
		TextView name = (TextView) convertView.findViewById(R.id.channel_name);

		ChannelInfo info = mChannelList.get(position);
		name.setText(info.channelName);
		loadParam.imageUrl = info.channelLogo;
		Global.getPlaybackImageLoader().loadImage(loadParam, image);

		return convertView;
	}

}
