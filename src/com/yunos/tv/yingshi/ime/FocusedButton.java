package com.yunos.tv.yingshi.ime;

import com.yunos.tv.yingshi.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewParent;
import android.widget.Button;

public class FocusedButton extends Button {
	private static final String TAG = "FocusedButton";
	private Drawable imgOn;
	public FocusedButton(Context context) {
		super(context);
		init(context,null);
	}
	
	public FocusedButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}
	
	public FocusedButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context,attrs);
	}
	
	private void init(Context context,AttributeSet attrs){
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FocusedButtonAttr);
		imgOn = a.getDrawable(R.styleable.FocusedButtonAttr_selected_icon);
		int left = a.getDimensionPixelSize(R.styleable.FocusedButtonAttr_selected_icon_left, 0);
		int top = a.getDimensionPixelSize(R.styleable.FocusedButtonAttr_selected_icon_top, 0);
		imgOn.setBounds(new Rect(left,top,left + imgOn.getIntrinsicWidth(), top + imgOn.getIntrinsicHeight()));
//		Log.i(TAG,"imgOn:" + imgOn.getBounds());
		boolean mSelected = a.getBoolean(R.styleable.FocusedButtonAttr_selected,false);
		if(mSelected){
			setSelected(mSelected);
		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		ViewParent parent = this.getParent();
		if(parent instanceof FocusedLinearLayout){
			((FocusedLinearLayout)parent).invalidate();
		}
		if(gainFocus){
			setSelected(gainFocus);
		}
	}
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		if(selected){
			ViewParent parent = this.getParent();
			if(parent instanceof FocusedLinearLayout){
				FocusedLinearLayout p = (FocusedLinearLayout)this.getParent();
				for(int i = 0; i < p.getChildCount(); i++){
					if(p.getChildAt(i) != this){
						p.getChildAt(i).setSelected(false);
					}
				}
			}
		}
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(this.isSelected()){
			imgOn.draw(canvas);
		}
	}
}
