package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yunos.tv.app.widget.FocusedGridView;
import com.yunos.tv.yingshi.R;

/**
 *　每行开头有个文字标记的。首页下面的gridview 
 *
 */
public class TitleFocusedGridView extends FocusedGridView {
	int mScreenHeight ;
	Context mContext;
	public TitleFocusedGridView(Context contxt, AttributeSet attrs, int defStyle) {
		super(contxt, attrs, defStyle);
		mScreenHeight = contxt.getResources().getDisplayMetrics().heightPixels;
		mContext = contxt;
	}

	public TitleFocusedGridView(Context contxt, AttributeSet attrs) {
		super(contxt, attrs);
		mScreenHeight = contxt.getResources().getDisplayMetrics().heightPixels;
		mContext = contxt;
	}

	public TitleFocusedGridView(Context contxt) {
		super(contxt);
		mScreenHeight = contxt.getResources().getDisplayMetrics().heightPixels;
		mContext = contxt;
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (isDrawDivider) {
			drawDivider(canvas);
		}
		drawTitle(canvas);
	}
	
	boolean isDrawDivider = false;
	boolean hasHeaderView = false;
	public void setHasHeaderView(boolean hasHeaderView) {
		this.hasHeaderView = hasHeaderView;
		this.isDrawDivider = hasHeaderView;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		if (getChildCount() > 6) {
//			View v = getChildAt(6);
//			Log.i("aabb", "=====getChildAt=======" + v.getHeight());
//		} 
	}
	
	Drawable mInterlayer = this.getResources().getDrawable(R.drawable.tv_film_divider);
	Rect mMyDrawRect = new Rect();
	void drawDivider(Canvas canvas) {
		int startPos = this.getFirstVisiblePosition();
		if (startPos == 0) {
			View v = getChildAt(0);
			int interlayerHeight = mInterlayer.getIntrinsicHeight();
			mMyDrawRect.left = this.getPaddingLeft();
			mMyDrawRect.right = getWidth() - this.getPaddingRight();
			mMyDrawRect.top = v.getBottom()-5;
			mMyDrawRect.bottom = mMyDrawRect.top + interlayerHeight;
			mInterlayer.setBounds(mMyDrawRect);
			mInterlayer.draw(canvas);
		}
	}

	void drawTitle(Canvas canvas) {
		if (mTitleInterface == null) {
			return;
		}
		int startPos = this.getFirstVisiblePosition();
		int endPos = this.getLastVisiblePosition();
		int numOfColums = this.getNumColumns();
		int location[] = new int[2];
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
//		paint.setShadowLayer(1, 2, 2, Color.GRAY);
		paint.setColor(Color.parseColor("#999999"));
		paint.setTextSize(16);
		this.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1]; 
		x += this.getPaddingLeft();
		y += this.getPaddingTop();
		
		Log.d("TitleFocusedGridView", "drawTitle startPos = " + startPos + ", endPos = " + endPos + ", numOfColums = " + numOfColums);
		for (int pos = startPos; pos < endPos; pos += numOfColums) {
			int currentPos = getNoSelectPosition(pos, pos + numOfColums);
//			Log.i("aabb", "========currentPos=====" + currentPos + ",startPos" + startPos + ",numOfColums:"  +numOfColums);
			if(checkHeaderPosition(currentPos)){
//				Log.i("aabb", "===currentPos===checkHeaderPosition==true=====");
				continue;
			}
			
			View v = this.getChildAt(currentPos - startPos);
			if(null == v){
				continue;
			}
			v.getLocationOnScreen(location);
			if (location[1] < 0 || location[1] > mScreenHeight) {
				continue;
			}
			
			int index = currentPos / numOfColums;
			if (hasHeaderView) {
				index--;
			}
			canvas.drawText(mTitleInterface.getTitle(index), getPaddingLeft()+9, location[1] - y + 28, paint);
		}
	}
	
	int getNoSelectPosition(int startPos, int endPos){
		for(int pos = startPos; pos < endPos; pos++){
			if(pos == this.getSelectedItemPosition() || pos == this.getLastSelectedItemPosition()){
				continue;
			}
			
			return pos;
		}
		
		return startPos;
	}

	TitleInterface mTitleInterface;
	public void setTitleInterface(TitleInterface l) {
		mTitleInterface = l;
	}
}
