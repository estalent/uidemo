package com.yunos.tv.yingshi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.base.info.MobileInfo;
import com.yunos.tv.source.entity.JuheSource;
import com.yunos.tv.yingshi.Const;
import com.yunos.tv.yingshi.R;

public class JuheSourceAdapter extends BaseAdapter {
	private ArrayList<JuheSource> player;
	int itemWidth;
	int itemHeight;
	GridView.LayoutParams lp;
	private LayoutInflater mInflater;
	private Context mContext;

	public JuheSourceAdapter(Context context, ArrayList<JuheSource> player) {
		mContext = context;
		this.player = player;
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		itemWidth = MobileInfo.dip2px(context, context.getResources().getDimension(R.dimen.dialog_juhe_source_item_w));
		itemHeight = MobileInfo.dip2px(context, context.getResources().getDimension(R.dimen.dialog_juhe_source_item_h));
		lp = new GridView.LayoutParams(itemWidth, itemHeight);
	}
	
	public int getItemWidth() {
		return itemWidth;
	}

	@Override
	public int getCount() {
		return player.size();
	}

	@Override
	public JuheSource getItem(int position) {
		return player.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.dialog_juhe_item, null);
			convertView.setLayoutParams(lp);
			vh = new ViewHolder();
			vh.image = (ImageView) convertView.findViewById(R.id.icon1);
			vh.text = (TextView) convertView.findViewById(R.id.text1);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		JuheSource source = getItem(position);
		vh.image.setImageDrawable(mContext.getResources().getDrawable(Const.SOURCE_MAP.get(source.key)));
		vh.text.setText(source.key);
		
		return convertView;
	}
	
	class ViewHolder {
		public ImageView image;
		public TextView text;
	}

}
