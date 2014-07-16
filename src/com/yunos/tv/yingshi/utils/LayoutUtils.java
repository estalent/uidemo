package com.yunos.tv.yingshi.utils;

import android.content.Context;

public class LayoutUtils {

	/**
	 * 将dip值转换px值
	 * @param context
	 * @param dipValue
	 * @return 
	 */
	public static int dipTopx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
