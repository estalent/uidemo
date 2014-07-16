package com.yunos.tv.yingshi.widget;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

class AnimateSetDrawable extends ProxyDrawable {

	private String TAG = "AnimateSetDrawable";
	private AnimationSet mAnimationSet;
	private Transformation mTransformation = new Transformation();

	public AnimateSetDrawable(Drawable target) {
		super(target);
	}

	public AnimateSetDrawable(Drawable target, AnimationSet animationSet) {
		super(target);
		mAnimationSet = animationSet;
	}

	public AnimationSet getAnimationSet() {
		return mAnimationSet;
	}

	public void setAnimationSet(AnimationSet animationSet) {
		mAnimationSet = animationSet;
	}
	
	public void setAnimationSet(Animation animation) {
		if(mAnimationSet!=null)
			mAnimationSet.addAnimation(animation);
	}
	
	public boolean hasStarted() {
		return mAnimationSet != null && mAnimationSet.hasStarted();
	}

	public boolean hasEnded() {
		return mAnimationSet == null || mAnimationSet.hasEnded();
	}

	@Override
	public void draw(Canvas canvas) {
		Drawable dr = getProxy();
		if (dr != null) {
			int sc = canvas.save();
			if (mAnimationSet != null) {
				mAnimationSet.getTransformation(
						AnimationUtils.currentAnimationTimeMillis(),
						mTransformation);		
				canvas.concat(mTransformation.getMatrix());
			}
			dr.draw(canvas);
			canvas.restoreToCount(sc);
		}
	}
}
