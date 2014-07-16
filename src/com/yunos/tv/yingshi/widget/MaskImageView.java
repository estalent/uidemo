package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yunos.tv.yingshi.R;

/**
 * 这个应用总共四种尺cun
 * size: 0:small 1:middle 2:big 3:homeshell
 */
public class MaskImageView extends ImageView {
	private ImageSize mImageSize = ImageSize.middle; // 0:small  1:middle  2:big 3:homeshell
//	private Drawable maskDrawable = null;
	private static Drawable mSmallMask = null;
	private static Drawable mMiddleMask = null;
	private static Drawable mBigMask = null;
	private static Drawable mLargeMask = null;
	private static Drawable mZixunIndexMask = null;
	private static Drawable mSearchHMask = null;
	private static Drawable mSearchVMask = null;
	
//	public boolean isLoad = false;
	public String tagImage = null;
	
	private boolean mIsDrawMask = true;
	
	public MaskImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		readMaskDrawable(context, attrs);
	}

	public MaskImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		readMaskDrawable(context, attrs);
	}

	public MaskImageView(Context context) {
		super(context);
	}
	
	private void readMaskDrawable(Context context, AttributeSet attrs) {
		TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.YingshiImageViewAttr);
//		if (maskDrawable == null) {
//			maskDrawable = types.getDrawable(R.styleable.YingshiImageViewAttr_face);
//		}
		int size = types.getInt(R.styleable.YingshiImageViewAttr_size, 1);//middle
		switch (size) {
		case 0:
			setImageSize(ImageSize.small);
			if (mSmallMask == null) {
				mSmallMask = types.getDrawable(R.styleable.YingshiImageViewAttr_face);
			}
			break;
		case 1:
			setImageSize(ImageSize.middle);
			if (mMiddleMask == null) {
				mMiddleMask = types.getDrawable(R.styleable.YingshiImageViewAttr_face);
			}
			break;
		case 2:
			setImageSize(ImageSize.big);
			if (mBigMask == null) {
				mBigMask = types.getDrawable(R.styleable.YingshiImageViewAttr_face);
			}
			break;
		case 3:
			setImageSize(ImageSize.large);
			if (mLargeMask == null) {
				mLargeMask = types.getDrawable(R.styleable.YingshiImageViewAttr_face);
			}
			break;
		case 4:
			setImageSize(ImageSize.zixun_index);
			if (mZixunIndexMask == null) {
				mZixunIndexMask = types.getDrawable(R.styleable.YingshiImageViewAttr_face);
			}
			break;
		case 5:
			setImageSize(ImageSize.search_h);
			if(mSearchVMask == null){
				mSearchVMask = getContext().getResources().getDrawable(R.drawable.tv_search_list_img_film);
			}
			if(mSearchHMask == null){
				mSearchHMask = getContext().getResources().getDrawable(R.drawable.tv_search_list_img_video);
			}
			break;
		case 6:
			setImageSize(ImageSize.search_v);
			if(mSearchVMask == null){
				mSearchVMask = getContext().getResources().getDrawable(R.drawable.tv_search_list_img_film);
			}
			if(mSearchHMask == null){
				mSearchHMask = getContext().getResources().getDrawable(R.drawable.tv_search_list_img_video);
			}
			break;
		default: // middle
			setImageSize(ImageSize.middle);
			if (mMiddleMask == null) {
				mMiddleMask = context.getResources().getDrawable(R.drawable.tv_film_cover_middle_normal);
			}
			break;
		}
		types.recycle();
	}

	public ImageSize getSize() {
		return mImageSize;
	}

	public void setImageSize(ImageSize size) {
		this.mImageSize = size;
	}
	
	public Drawable getMaskDrawable() {
		Drawable ret = null;
		switch (mImageSize) {
		case small:
			ret = mSmallMask;
			break;
		case middle:
			ret = mMiddleMask;
			break;
		case big:
			ret = mBigMask;
			break;
		case large:
			ret = mLargeMask;
			break;
		case zixun_index:
			ret = mZixunIndexMask;
			break;
		case search_h:
			ret = mSearchHMask;
			break;
		case search_v:
			ret = mSearchVMask;
			break;
		default:
			ret = mMiddleMask;
			break;
		}
		return ret;
	}
	
//	public void setMaskDrawable(int maskDrawableId){
//    	maskDrawable = getContext().getResources().getDrawable(maskDrawableId);   
//    }
    
//    public void setMaskDrawable(Drawable maskDrawable){    
//    	this.maskDrawable = maskDrawable;        
//    }
	
	public enum ImageSize {
		small, middle, big, large, zixun_index, playback,search_h,search_v
	}
	
	public void setImageBitmap(Bitmap bm, boolean isDrawMask) {
		mIsDrawMask = isDrawMask;
		setImageBitmap(bm);
	}
	
	public void setImageDrawable(Drawable db, boolean isDrawMask) {
		mIsDrawMask = isDrawMask;
		setImageDrawable(db);
	}
	
	protected boolean isDrawMask() {
		return mIsDrawMask;
	}
	
	public void setIsDrawMask(boolean b) {
		this.mIsDrawMask = b;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		super.onDraw(canvas);
	}
	
}
