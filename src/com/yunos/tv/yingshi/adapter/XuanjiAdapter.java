package com.yunos.tv.yingshi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.aliyun.base.info.MobileInfo;
import com.yunos.tv.source.entity.JuheSource;
import com.yunos.tv.source.entity.Tag;
import com.yunos.tv.yingshi.R;

public class XuanjiAdapter extends BaseAdapter {
	private Tag tag;
	int itemWidth;
	GridView.LayoutParams lp;
	private LayoutInflater mInflater;
	private boolean isJuhe = false;
	private JuheSource urlList;
	private String classId;

	public XuanjiAdapter(Context context, Tag tag) {
		this.tag = tag;
		isJuhe = false;
		init(context);
	}

	/**
	 * 聚合选集界面
	 * @param context
	 * @param urlList
	 */
	public XuanjiAdapter(Context context, JuheSource urlList, String classId) {
		this.urlList = urlList;
		isJuhe = true;
		this.classId = classId;
		init(context);
	}

	private void init(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		itemWidth = MobileInfo.dip2px(context, context.getResources().getDimension(R.dimen.grid_xuanji_w));
		lp = new GridView.LayoutParams(itemWidth, itemWidth);
	}

	public int getItemWidth() {
		return itemWidth;
	}

	@Override
	public int getCount() {
		if (isJuhe) {
			return urlList.url.size();
		}
		return tag.source.size();
	}

	@Override
	public String getItem(int position) {
		if (isJuhe) {
			return urlList.url.get(position).deTitle;
		}
		return tag.source.get(position).fileName;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = null;
		if (convertView == null) {
			text = (TextView) mInflater.inflate(R.layout.item_grid_xuanji, null);
			text.setLayoutParams(lp);
			// text = new TextView(mContext);
			// text.setLayoutParams(lp);
			// text.setGravity(Gravity.CENTER);
			// text.setBackgroundResource(R.drawable.tui_bg);
			// text.setTextColor(mContext.getResources().getColor(R.drawable.text_color));
			// text.setTextSize(mContext.getResources().getDimension(R.dimen.tui_text_size_3));
			convertView = text;
		} else {
			text = (TextView) convertView;
		}
		
//		if (isJuhe && "D".equals(classId)) {
//			JuheSourceUrl url = urlList.url.get(position);
//			text.setText(url.description.trim());
//		} else {
//			text.setText(getItem(position).trim());
//		}
		String name = getItem(position).trim();
		if (TextUtils.isDigitsOnly(name)) {
			text.setText(name);
		} else {
			text.setText(String.valueOf(position+1));
		}
		return text;
	}

}
