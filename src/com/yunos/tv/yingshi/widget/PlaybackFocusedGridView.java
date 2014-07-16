package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yunos.tv.app.widget.FocusedGridView;
import com.yunos.tv.yingshi.R;

public class PlaybackFocusedGridView extends FocusedGridView {
	Drawable mInterlayer = this.getResources().getDrawable(R.drawable.tv_playback_shelf);
	Rect mMyDrawRect = new Rect();

	public PlaybackFocusedGridView(Context contxt, AttributeSet attrs, int defStyle) {
		super(contxt, attrs, defStyle);
	}

	public PlaybackFocusedGridView(Context contxt, AttributeSet attrs) {
		super(contxt, attrs);
	}

	public PlaybackFocusedGridView(Context contxt) {
		super(contxt);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int count = getChildCount();
		if (count > 0) {
			View v = getChildAt(0);

			if (v != null) {
				int gridview_height = this.getHeight();
				int interlayerHeight = mInterlayer.getIntrinsicHeight();
				int blockGapHeight = v.getHeight();

				mMyDrawRect.left = 0;
				mMyDrawRect.right = getWidth();
				int initPos = v.getTop();
				Log.i("aabb", "initPos:" + initPos + ",blockGapHeight:" + blockGapHeight + ", interlayerHeight:" 
						+ interlayerHeight + ",gridview_height:" + gridview_height);
				for (int i = initPos; i <= gridview_height; i += blockGapHeight) {
					mMyDrawRect.top = i;
					mMyDrawRect.bottom = mMyDrawRect.top + interlayerHeight;
					mInterlayer.setBounds(mMyDrawRect);
					mInterlayer.draw(canvas);
				}
			}

			super.onDraw(canvas);
		}
	}
}
