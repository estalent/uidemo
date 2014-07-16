package com.yunos.tv.yingshi.widget;

import com.yunos.tv.yingshi.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class RowWrapLayout extends LinearLayout {
	private int verticalSpacing,horizontalSpacing;
	public RowWrapLayout(Context context) {
		super(context);
	}

	public RowWrapLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public RowWrapLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		TypedArray tArray = context.obtainStyledAttributes(attrs,R.styleable.RowWrapLayoutAttr);
		verticalSpacing = tArray.getDimensionPixelOffset(R.styleable.RowWrapLayoutAttr_vSpacing, 0);
		horizontalSpacing = tArray.getDimensionPixelOffset(R.styleable.RowWrapLayoutAttr_hSpacing, 0);
		tArray.recycle();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
        int cr = l;
        int cb = 0;
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);  
            int width = child.getMeasuredWidth();  
            int height = child.getMeasuredHeight();
            cr += width;
            if(i == 0){
            	cb += height;
            }
            
            if(cr > r && i != 0){
            	cr = l + width;
            	cb += verticalSpacing + height;
            }
//            Log.i("xxxx","top:" + (cb-height));
            child.layout(cr - width, cb - height, cr, cb);
            cr += horizontalSpacing;
        }
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/*
        for (int i = 0; i < getChildCount(); i++) {  
            final View child = getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);  
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
        */
        int count = getChildCount();
        int totalHeight = 0;
        int r = getRight();
        int l = getLeft();
        int cr = l;
        int cb = 0;
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);  
            int width = child.getMeasuredWidth();  
            int height = child.getMeasuredHeight();
            cr += width;
            if(i == 0){
            	cb += height;
            }
            
            if(cr > r && i != 0){
            	cr = l + width;
            	cb += verticalSpacing + height;
            }
            cr += horizontalSpacing;
        }
        totalHeight = cb - verticalSpacing;
        // 设置容器所需的宽度和高度  
        setMeasuredDimension(widthMeasureSpec, totalHeight);  
    } 
	
}
