package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;

public class YingshiImageView extends MaskImageView {
	
	public YingshiImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public YingshiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public YingshiImageView(Context context) {
		super(context);
	}
	
	Rect maskBounds = new Rect();
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (!isDrawMask()) {
			return;
		}
		
        canvas.getClipBounds(maskBounds);
       
        if ( this.getMaskDrawable() != null ){
        	drawImageByDrawable(canvas, getPaint(), this.getMaskDrawable(), maskBounds);
        }
		
		//放在ImageLoader的processBitmap中处理，参考Global.java
//		if (mFaceDrawable != null) {
//			int imageViewWidth = getWidth();
//			int imageViewHeight = getHeight();
//			Bitmap faceBitmap = ((BitmapDrawable) mFaceDrawable).getBitmap();
////			Bitmap faceBitmap2 = ImageUtils.getScaleBitmap(faceBitmap, imageViewWidth, imageViewHeight);
//			Paint paint = new Paint();
//			
//			int sourceWidth = faceBitmap.getWidth();
//			int sourceHeight = faceBitmap.getHeight();
//			float scaleWidth = ((float) imageViewWidth) / sourceWidth;
//			float scaleHeight = ((float) imageViewHeight) / sourceHeight;
//			Matrix matrix = new Matrix();
//			matrix.setScale(scaleWidth, scaleHeight);
//			canvas.drawBitmap(faceBitmap, matrix, paint);
//		}
	}
	
	private static Paint mPaint;

	protected static Paint getPaint() {
		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setDither(true);// 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
			mPaint.setAntiAlias(true);// 非锯齿效果
			mPaint.setFilterBitmap(true);// 如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作，加快显示速度
		}
		return mPaint;
	}
	
    private void drawImageByDrawable(Canvas canvas, Paint paint, Drawable drawable, Rect dstRect){
    	
    	if ( canvas == null || drawable == null )
    		return;
    	Matrix matrix = new Matrix(getMatrix());
        Bitmap bitmap = null;
        float sx = 0.0f, sy = 0.0f;
        
        if ( drawable instanceof BitmapDrawable ){
        	bitmap = ((BitmapDrawable)drawable).getBitmap();
        	sx = (float)dstRect.width() / bitmap.getWidth();
            sy = (float)dstRect.height() / bitmap.getHeight();
            if ( sx != 1.0f || sy != 1.0f ){
            	matrix.setScale(sx, sy);
            }
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
            canvas.drawBitmap(bitmap, matrix, paint);
            
        } else if ( drawable instanceof NinePatchDrawable ){
        	drawable.setBounds(dstRect);
        	drawable.draw(canvas);
        }
        
    }
    
}
