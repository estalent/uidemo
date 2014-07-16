package com.yunos.tv.yingshi.widget;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;

public class SizeColorSpan extends AbsoluteSizeSpan {
	private int color;

	
	public SizeColorSpan(int size, boolean dip) {
		super(size, dip);
		// TODO Auto-generated constructor stub
	}



	public SizeColorSpan(int size) {
		super(size);
		// TODO Auto-generated constructor stub
	}



	public SizeColorSpan(Parcel src) {
		super(src);
		// TODO Auto-generated constructor stub
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public void updateDrawState(TextPaint tp) {
		super.updateDrawState(tp);
		tp.setColor(color);
	}
}
