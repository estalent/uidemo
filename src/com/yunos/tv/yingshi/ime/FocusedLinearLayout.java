package com.yunos.tv.yingshi.ime;


import com.yunos.tv.yingshi.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

public class FocusedLinearLayout extends LinearLayout {
	private static final String TAG = "FocusedLinearLayout";
	private Drawable shadowDrawable;
	public FocusedLinearLayout(Context context) {
		super(context);
		init(context,null);
	}
	
	public FocusedLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}
	
	public FocusedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context,attrs);
	}
	
	private void init(Context context,AttributeSet attrs){
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FocusedLinearLayoutAttr);
		shadowDrawable = a.getDrawable(R.styleable.FocusedLinearLayoutAttr_focus_shadow);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		drawShadow(canvas);
	}
	
	private void drawShadow(Canvas canvas){
		View child = this.getFocusedChild();
		if(shadowDrawable == null || child == null){
			return;
		}

		Rect drawRect = new Rect();
		Rect drawPadding = new Rect();
		Log.i(TAG,"focusedChild:" + child);
		
		if(child instanceof IFocusedRect){
			drawRect = ((IFocusedRect)child).getFocusedRect();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)child.getLayoutParams();
			if(drawRect != null){
				drawRect.left += this.getPaddingLeft() + lp.leftMargin;
				drawRect.right += this.getPaddingLeft() + lp.leftMargin;
				drawRect.top += this.getPaddingTop() + lp.topMargin;
				drawRect.bottom += this.getPaddingTop() + lp.topMargin;
			}
		} else {
//			child.getLocalVisibleRect(drawRect);
			shadowDrawable.getPadding(drawPadding);
			drawRect.left = child.getLeft();
			drawRect.right = child.getRight();
			drawRect.top = child.getTop();
			drawRect.bottom = child.getBottom();
			
			drawRect.left -= drawPadding.left;
			drawRect.right += drawPadding.right;
			drawRect.top -= drawPadding.top;
			drawRect.bottom += drawPadding.bottom;
		}
		
		if(drawRect != null){
			shadowDrawable.setBounds(drawRect);
			shadowDrawable.draw(canvas);
		}
	}
	
	public interface IFocusedRect{
		public Rect getFocusedRect();
	}
	
}
