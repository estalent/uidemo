package com.yunos.tv.yingshi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.aliyun.base.info.MobileInfo;
import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.entity.ImageArg;
import com.aliyun.imageload.entity.ImageScaleType;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.widget.YingshiImageView;

public class ZixunIndexGridAdapter extends IndexGridAdapter {

	private ImageLoadParam loadParam;

	public ZixunIndexGridAdapter(Context context, int itemLayoutId, String what) {
		super(context, itemLayoutId);
		int w = MobileInfo.dip2px(context, 160);
		int h = MobileInfo.dip2px(context, 120);
		loadParam = new ImageLoadParam("", w, h);
		loadParam.setImageScaleType(ImageScaleType.EXACTLY);
		loadParam.what = what;
		loadParam.setWorkCache(true);
		loadParam.setImageArg(ImageArg.arg2);
	}

	public void updateView(int position, View convertView) {
		Program info = getItem(position);
		if (info == null) {
			return;
		}
		YingshiImageView imageview = (YingshiImageView) convertView.findViewById(R.id.image_yingshi_grid);
		TextView title = (TextView) convertView.findViewById(R.id.text_yingshi_grid);

		if (!TextUtils.isEmpty(info.picUrl)) {
			loadParam.imageUrl = info.picUrl;
		} else {
			loadParam.imageUrl = "";
		}
		loadParam.setFromAsset(true);
		if (!loadParam.imageUrl.equals(imageview.tagImage)) {
			Global.getMaskImageLoader().loadImage(loadParam, imageview);
		}
		title.setText(info.name);
	}

}
