package com.yunos.tv.yingshi;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.aliyun.imageload.ImageLoader;
import com.aliyun.imageload.OnImageLoadListener;
import com.aliyun.imageload.entity.ImageLoadType;
import com.aliyun.imageload.utils.ImageUtils;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.widget.MaskImageView;
import com.yunos.tv.yingshi.widget.MaskImageView.ImageSize;
import com.yunos.tv.yingshi.widget.ZixunImageView;

public class Global {
	
	private static ImageLoader mImageLoader;
	private static ImageLoader mMaskImageLoader;
	private static ImageLoader mFilmPokerImageLoader;
	private static ImageLoader mAdImageLoader;
	private static ImageLoader mCoverflowImageLoader;
	private static ImageLoader mPlaybackImageLoader;
	private static ImageLoader mZixunIndexLoader;
	private static ImageLoader mSearchListImageLoader;

	/*
	 * 一些默认图。程序启动时不初始化，当必要的时候调用get来取。
	 */
	private static Bitmap mZixunDefaultBitmap = null;//
	private static Bitmap mDefaultReflectionImage = null;//
	private static Bitmap mZixunOverBitmap = null;//
	private static Bitmap mYingshiMiddleOverBitmap = null;//
	private static Bitmap mDefaultBitmapSmall = null;
	private static Bitmap mDefaultBitmapMiddle = null;
	private static Bitmap mDefaultBitmapBig = null;//
	private static Bitmap mDefaultBitmapLarge = null;//
	private static Bitmap mDefaultPlaybackBitmap = null;//回看默认图片
	private static Bitmap mDefaultZixunIndexBitmap = null;
	
	private static Bitmap mSearchHMaskBitmap = null;
	private static Bitmap mSearchVMaskBitmap = null;
	
	
	private static Paint mPaint = null;

	public static ImageLoader getZixunIndexLoader() {
		if (mZixunIndexLoader == null) {
			mZixunIndexLoader = new ImageLoader();
		}
		return mZixunIndexLoader;
	}
	
	public static Bitmap getSearchHMaskBitmap(){
		if (mSearchHMaskBitmap == null) {
			mSearchHMaskBitmap = ImageUtils.decodeBitmap(YingshiApplication.getApplication(), R.drawable.tv_search_list_img_video);
		}
		return mSearchHMaskBitmap;
	}
	
	public static Bitmap getSearchVMaskBitmap(){
		if (mSearchVMaskBitmap == null) {
			mSearchVMaskBitmap = ImageUtils.decodeBitmap(YingshiApplication.getApplication(), R.drawable.tv_search_list_img_film);
		}
		return mSearchVMaskBitmap;
	}
	
	public static ImageLoader getImageLoader() {
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader();
			mImageLoader.setDefaultCacheLoadListener(new OnImageLoadListener() {
				
				@Override
				public void onLoad(ImageView imageview, Bitmap bitmap, ImageLoadType type, String url) {
					switch (type) {
					case default_image:
					case error:
						break;
					case memory:
					case network:
					case disk:
						imageview.setImageBitmap(bitmap);
						break;
					}
				}
			});
		}

		return mImageLoader;
	}
	
	public static ImageLoader getSearchListImageLoader() {
		if (mSearchListImageLoader == null) {
			mSearchListImageLoader = new ImageLoader();
			mSearchListImageLoader.setDefaultCacheLoadListener(new OnImageLoadListener() {
				
				@Override
				public Bitmap processBitmap(ImageView imageview, Bitmap bitmap) {
					Bitmap maskImage = getSearchVMaskBitmap();
					return com.aliyun.imageload.utils.ImageUtils.getOverLapImage(bitmap, maskImage, getPaint(), true);
				}
				
				@Override
				public void onLoad(ImageView imageview, Bitmap bitmap, ImageLoadType type, String url) {
					switch (type) {
					case default_image:
					case error:
						imageview.setImageBitmap(getDefaultBitmap(ImageSize.small));
						break;
					case memory:
					case network:
					case disk:
						imageview.setImageBitmap(bitmap);
						break;
					}
				}
			});
		}

		return mSearchListImageLoader;
	}
	
	public static ImageLoader getPlaybackImageLoader() {
		if (mPlaybackImageLoader == null) {
			mPlaybackImageLoader = new ImageLoader();
			mPlaybackImageLoader.setDefaultCacheLoadListener(new OnImageLoadListener() {

				@Override
				public void onLoad(ImageView imageview, Bitmap bitmap, ImageLoadType type, String url) {
					switch (type) {
					case default_image:
					case error:
						imageview.setImageBitmap(getDefaultBitmap(ImageSize.playback));
						break;
					case memory:
					case network:
					case disk:
						imageview.setImageBitmap(bitmap);
						break;
					}
				}
			});
		}

		return mPlaybackImageLoader;
	}
	
	public static ImageLoader getZinxunImageLoader() {
		if (mFilmPokerImageLoader == null) {
			mFilmPokerImageLoader = new ImageLoader();
			mFilmPokerImageLoader.setDefaultCacheLoadListener(new OnImageLoadListener() {
				@Override
				public Bitmap processBitmap(ImageView object, Bitmap bitmap) {
					// 生成默认的带卷脚的图片
					Bitmap comBitmp = ImageUtils.getOverLapImage(bitmap, getZixunOverBitmap(), getPaint(), true);
//					// 加入带倒影的图片
					return ImageUtils.createReflectionImageWithOrigin(comBitmp, 0.3f, 5);
				}

				@Override
				public void onLoad(ImageView object, Bitmap bitmap, ImageLoadType type, String url) {
					ZixunImageView imageView = (ZixunImageView) object;
					switch (type) {
					case default_image:
					case error:
						imageView.setImageBitmap(getDefaultReflectionImage());
						break;
					case memory:
						imageView.setImageBitmap(bitmap);
						break;
					case disk:
					case network:
						TransitionDrawable td = new TransitionDrawable(new Drawable[] { new BitmapDrawable(getDefaultReflectionImage()),
								new BitmapDrawable(imageView.getResources(), bitmap) });
						td.startTransition(Config.IMAGE_FADE_TIME);
						imageView.setImageDrawable(td);
						break;
					}
				}
			});
		}

		return mFilmPokerImageLoader;
	}

	public static ImageLoader getMaskImageLoader() {
		if (mMaskImageLoader == null) {
			mMaskImageLoader = new ImageLoader();
			mMaskImageLoader.setDefaultCacheLoadListener(new OnImageLoadListener() {

				@Override
				public void onLoad(ImageView object, Bitmap bitmap, ImageLoadType type, String url) {
					MaskImageView maskImage = (MaskImageView) object;
					ImageSize bitmapSize = maskImage.getSize();
					switch (type) {
					case default_image:
						if (!url.equals(maskImage.tagImage)) {
							maskImage.tagImage = url;
							if (bitmapSize == ImageSize.search_h || bitmapSize == ImageSize.search_v) {
								bitmapSize = ImageSize.small;
							}
							maskImage.setImageBitmap(getDefaultBitmap(bitmapSize), false);
						}
						break;
					case error:
						if (bitmapSize == ImageSize.search_h || bitmapSize == ImageSize.search_v) {
							bitmapSize = ImageSize.small;
						}
						maskImage.setImageBitmap(getDefaultBitmap(bitmapSize), false);
						break;
					case memory:
						maskImage.setImageBitmap(bitmap, true);
						break;
					case disk:
					case network:
						TransitionDrawable td = new TransitionDrawable(new Drawable[] { new BitmapDrawable(getDefaultBitmap(bitmapSize)),
								new BitmapDrawable(maskImage.getResources(), bitmap) });
						td.startTransition(Config.IMAGE_FADE_TIME);
						maskImage.setImageDrawable(td, true);
						break;
					}
				}
			});
		}
		return mMaskImageLoader;
	}
	
	public static ImageLoader getCoverFlowImageLoader() {
		if (mCoverflowImageLoader == null) {
			mCoverflowImageLoader = new ImageLoader();
			mCoverflowImageLoader.setDefaultCacheLoadListener(new OnImageLoadListener() {
				@Override
				public void onLoad(ImageView imageView, Bitmap bitmap, ImageLoadType type, String url) {
					MaskImageView maskImage = (MaskImageView) imageView;
					ImageSize bitmapSize = maskImage.getSize();
					switch (type) {
					case default_image:
						if (!url.equals(maskImage.tagImage)) {
							maskImage.tagImage = url;
							maskImage.setImageBitmap(getDefaultBitmap(bitmapSize), false);
						}
						break;
					case error:
						maskImage.setImageBitmap(getDefaultBitmap(bitmapSize), false);
						break;
					case memory:
						maskImage.setImageBitmap(bitmap, false);
						break;
					case disk:
					case network:
						TransitionDrawable td = new TransitionDrawable(new Drawable[] { new BitmapDrawable(getDefaultBitmap(bitmapSize)),
								new BitmapDrawable(maskImage.getResources(), bitmap) });
						td.startTransition(Config.IMAGE_FADE_TIME);
						maskImage.setImageDrawable(td, false);
						break;
					}
				}
			});
		}

		return mCoverflowImageLoader;
	}
	
	public static ImageLoader getAdImageLoader() {
		if (mAdImageLoader == null) {
			mAdImageLoader = new ImageLoader();
		}
		return mAdImageLoader;
	}
	
	public static Bitmap getZixunOverBitmap() {
		if (mZixunOverBitmap == null) {
			mZixunOverBitmap = ImageUtils.getBitmapByResource(YingshiApplication.getApplication().getApplicationContext(),
					R.drawable.tv_homeshell_groupmask_normal);
		}
		return mZixunOverBitmap;
	}
	public static Bitmap getYingshiMiddleOverBitmap() {
		if (mYingshiMiddleOverBitmap == null) {
			mYingshiMiddleOverBitmap = ImageUtils.getBitmapByResource(YingshiApplication.getApplication().getApplicationContext(),
					R.drawable.tv_film_cover_middle_normal);
		}
		return mYingshiMiddleOverBitmap;
	}

	public static Bitmap getZixunDefaultBitmap() {
		if (mZixunDefaultBitmap == null) {
			mZixunDefaultBitmap = ImageUtils.getOverLapImage(getDefaultBitmap(ImageSize.large), getZixunOverBitmap(), getPaint(), false);
		}
		return mZixunDefaultBitmap;
	}

	public static Bitmap getDefaultReflectionImage() {
		if (mDefaultReflectionImage == null) {
			mDefaultReflectionImage = ImageUtils.createReflectionImageWithOrigin(getZixunDefaultBitmap(), 0.3f, 5);
		}
		return mDefaultReflectionImage;
	}
	
	public static Bitmap getDefaultBitmap(ImageSize type) {
		switch (type) {
		case small:
			if (mDefaultBitmapSmall == null) {
				mDefaultBitmapSmall = ImageUtils.decodeBitmap(YingshiApplication.getApplication(), R.drawable.tv_film_black_cover_small);
			}
			return mDefaultBitmapSmall;
		case zixun_index:
			if (mDefaultZixunIndexBitmap == null) {
				mDefaultZixunIndexBitmap = ImageUtils.decodeBitmap(YingshiApplication.getApplication(), R.drawable.tv_film_videoframe_default);
			} 
			return mDefaultZixunIndexBitmap;
		case middle:
			if (mDefaultBitmapMiddle == null) {
				mDefaultBitmapMiddle = ImageUtils.decodeBitmap(YingshiApplication.getApplication(),
						R.drawable.tv_film_black_cover_middle_normal);
			}
			return mDefaultBitmapMiddle;
		case big:
			if (mDefaultBitmapBig == null) {
				mDefaultBitmapBig = ImageUtils.decodeBitmap(YingshiApplication.getApplication(), R.drawable.tv_film_black_cover_big_normal);
			}
			return mDefaultBitmapBig;
		case large:
			if (mDefaultBitmapLarge == null) {
				mDefaultBitmapLarge = ImageUtils.decodeBitmap(YingshiApplication.getApplication(),
						R.drawable.tv_homeshell_black_singlemask_normal);
			}
			return mDefaultBitmapLarge;
		case playback:
			if(mDefaultPlaybackBitmap == null){
				mDefaultPlaybackBitmap = ImageUtils.decodeBitmap(YingshiApplication.getApplication(),
						R.drawable.ic_tv_playback_tvlogo_empty);
			}
			return mDefaultPlaybackBitmap;
		default:
			return null;
		}
	}
	
	protected static Paint getPaint() {
		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setDither(true);// 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
			mPaint.setAntiAlias(true);// 非锯齿效果
			mPaint.setColor(Color.TRANSPARENT);
		}
		return mPaint;
	}
	
	/**
	 * 从一个list取位置p前后共MAX个元素
	 * @param list
	 * @param p指定的位置
	 * @return　maybe null!!
	 */
	public static List<Program> getRecommendList(List<Program> list, int p) {
		if (list == null) {
			return null;
		}
		
		int MAX = 10;
		int mid = MAX / 2;
		int sum = list.size();
		
		if (sum <= 1 || p < 0 || p > sum - 1) {
			return null;
		}
		
		List<Program> result = new ArrayList<Program>();
		p++;
		if (sum <= MAX + 1) {
			if (p == sum || p == 1) {
				result.addAll(list);
				result.remove(p - 1);
				return result;
			}
			result.addAll(list.subList(p, sum));
			result.addAll(list.subList(0, p - 1));
			return result;
		}
		if (p - mid <= 0) {
			if (p == 1) {
				result.addAll(list.subList(1, MAX + 1));
				return result;
			}
			result.addAll(list.subList(p, MAX + 1));
			result.addAll(list.subList(0, p - 1));
			return result;
		} else if (p + mid >= sum) {
			if (p == sum) {
				result.addAll(list.subList(sum - (MAX + 1), sum - 1));
				return result;
			}
			result.addAll(list.subList(p, sum));
			result.addAll(list.subList(sum - (MAX + 1), p - 1));
			return result;
		} else {
			result.addAll(list.subList(p, p + mid));
			result.addAll(list.subList(p - mid - 1, p - 1));
		}
		return result;
	}
	
	private void fade(View view) {
		AlphaAnimation fadeImage = new AlphaAnimation(0.2f, 1);
		fadeImage.setDuration(Config.IMAGE_FADE_TIME);
		fadeImage.setInterpolator(new DecelerateInterpolator());
		view.startAnimation(fadeImage);
	}

}
