package com.yunos.tv.yingshi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.entity.ImageArg;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.Const;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.widget.YingshiImageView;

public class YingshiGridAdapter extends UpdateAdapter {

	private List<Program> mProgramList;
	private ImageLoadParam loadParam;
	private LayoutInflater mInflater;
	private boolean mIsScale;

	public List<Program> getProgramList() {
		return mProgramList;
	}

	public void setProgramList(List<Program> programList) {
		this.mProgramList = programList;
	}

	public void deleteProgram(int postion) {
		this.mProgramList.remove(postion);
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
	public YingshiGridAdapter(Context context, String what) {
		this(context, what, false, true);
	}

	public YingshiGridAdapter(Context context, String what, boolean scale, boolean isPreLoad) {
		super(isPreLoad);
		mProgramList = new ArrayList<Program>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		loadParam = new ImageLoadParam("");
		loadParam.what = what;
		loadParam.setImageArg(ImageArg.middle);

		mIsScale = scale;
	}

	public void clear() {
		this.mProgramList.clear();
	}

	@Override
	public int getCount() {
		if (isPreLoad && mProgramList.size() == 0) {
			return 18;
		}
		return mProgramList.size();
	}

	@Override
	public Program getItem(int position) {
		if (mProgramList.isEmpty()) {
			return null;
		}
		return mProgramList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.frag_yingshi_item, null);
		}

		updateView(position, convertView);

		return convertView;
	}

	public void updateView(int position, View convertView) {
		if (mIsScale) {
			convertView.setScaleX(1.0f);
			convertView.setScaleY(1.0f);
		}
		YingshiImageView image = (YingshiImageView) convertView.findViewById(R.id.image_yingshi_grid);
		TextView title = (TextView) convertView.findViewById(R.id.text_yingshi_grid);
		TextView textGaoqing = (TextView) convertView.findViewById(R.id.text_gaoqing);

		Program info = getItem(position);
		if (info != null) {
			// 从历史播放记录，或收藏界面的时候，需要判断isJuhe
			if (info.isJuheVideo) {
				loadParam.imageUrl = info.picUrl;
			} else {
				int index = info.picUrl.lastIndexOf(".");
				if (index > 0) {
					String suffix = info.picUrl.substring(index);
					loadParam.imageUrl = info.picUrl + Const.IMAGE_SIZE_YINGSHI_GRID + suffix;
				} else {
					loadParam.imageUrl = info.picUrl;
				}
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
		} else {
			image.setIsDrawMask(false);
			textGaoqing.setVisibility(View.GONE);
		}
	}

}
