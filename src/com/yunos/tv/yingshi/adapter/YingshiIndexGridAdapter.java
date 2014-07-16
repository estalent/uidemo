package com.yunos.tv.yingshi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.entity.ImageArg;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.Const;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.widget.YingshiImageView;

public class YingshiIndexGridAdapter extends IndexGridAdapter {

	private ImageLoadParam loadParam;
	
	public YingshiIndexGridAdapter(Context context, int itemLayoutId, String what) {
		super(context, itemLayoutId);
		loadParam = new ImageLoadParam("");
		loadParam.what = what;
		loadParam.setImageArg(ImageArg.middle);
	}
	
	public void updateView(int position, View convertView) {
		Program info = getItem(position);
		if (info == null) {
			return;
		}
		YingshiImageView image = (YingshiImageView) convertView.findViewById(R.id.image_yingshi_grid);
		TextView title = (TextView) convertView.findViewById(R.id.text_yingshi_grid);
		TextView textGaoqing = (TextView) convertView.findViewById(R.id.text_gaoqing);
		
		int index = info.picUrl.lastIndexOf(".");
		if (index > 0) {
			String suffix = info.picUrl.substring(index);
			loadParam.imageUrl = info.picUrl + Const.IMAGE_SIZE_YINGSHI_GRID + suffix;
		} else {
			loadParam.imageUrl = info.picUrl;
		}
		loadParam.setFromAsset(true);
		if (!loadParam.imageUrl.equals(image.tagImage)) {
			Global.getMaskImageLoader().loadImage(loadParam, image);
		}
		
		title.setText(info.name);
		switch (info.rateType) {
		case 0:
			textGaoqing.setVisibility(View.GONE);
			break;
		case 1:
			textGaoqing.setVisibility(View.VISIBLE);
			textGaoqing.setText(R.string.gaoqing);
			break;
		case 2:
			textGaoqing.setVisibility(View.VISIBLE);
			textGaoqing.setText(R.string.languang);
			break;
		default:
			textGaoqing.setVisibility(View.GONE);
			break;
		}
	}

}
