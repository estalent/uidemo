package com.yunos.tv.yingshi.ime;

import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.ime.FocusedLinearLayout.IFocusedRect;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.GridView;

public class ImeGrid extends GridView implements IFocusedRect{
	private static final String TAG = "ImeGrid";
	private Context mContext;
	private Drawable listSelector;
	private int focusedItemId;
	private OnBeforeFocusChangeListener mOnBeforeFocusChangeListener;
	public ImeGrid(Context context) {
		super(context);
		initImeGrid(context,null);
	}
	
	public ImeGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		initImeGrid(context,attrs);
	}
	
	public ImeGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initImeGrid(context,attrs);
	}
	
	private void initImeGrid(Context context,AttributeSet attrs){
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.imeGridAttr);
		listSelector = a.getDrawable(R.styleable.imeGridAttr_listSelector);
		focusedItemId = a.getResourceId(R.styleable.imeGridAttr_focusedItemId,-1);
		Log.i(TAG,"listSelector" + listSelector + ",focusedItemId:" + focusedItemId);
		setSelectedChangeListener();
	}
	
	public void setOnBeforeFocusChangeListener(OnBeforeFocusChangeListener l){
		mOnBeforeFocusChangeListener = l;
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,Rect previouslyFocusedRect) {
		if(mOnBeforeFocusChangeListener == null || 
				(mOnBeforeFocusChangeListener != null && !mOnBeforeFocusChangeListener.onBeforeFocusChange(gainFocus, direction, previouslyFocusedRect))){
			super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		}

		ViewParent parent = this.getParent();
		if(parent instanceof FocusedLinearLayout){
			Log.i(TAG,"focuschange");
			((FocusedLinearLayout)parent).invalidate();
		}
	}

	private OnItemSelectedListener mOnItemSelectedListener;
	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelectedListener = listener;
	}
	
	private void setSelectedChangeListener(){
		super.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(mOnItemSelectedListener != null){
					mOnItemSelectedListener.onItemSelected(parent, view, position, id);
				}
				
				ViewParent vParent = ImeGrid.this.getParent();
				if(vParent instanceof FocusedLinearLayout){
					Log.i(TAG,"selectedChange onitemselected");
					((FocusedLinearLayout)vParent).invalidate();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				if(mOnItemSelectedListener != null){
					mOnItemSelectedListener.onNothingSelected(parent);
				}
				ViewParent vParent = ImeGrid.this.getParent();
				if(vParent instanceof FocusedLinearLayout){
					Log.i(TAG,"selectedChange onNothingselected");
					((FocusedLinearLayout)vParent).invalidate();
				}
			}
		});
	}

	@Override
	public Rect getFocusedRect() {
		View child = this.getSelectedView();
		View focusedView = null;
		if(child == null ||  !isFocused()){
			return null;
		}
		
		if(focusedItemId != -1){
			focusedView = child.findViewById(focusedItemId);
		}
		
		if(focusedView == null){
			focusedView = child;
		}
		Log.i(TAG,"drawSelector:" + focusedView);
		
		Rect focusedGlobalRect = new Rect();
		Rect focusedRect = new Rect();
		Rect gridGlobalRect = new Rect();
		Rect selectorPadding = new Rect();
		
		listSelector.getPadding(selectorPadding);
		if(!getGlobalVisibleRect(gridGlobalRect) || !focusedView.getGlobalVisibleRect(focusedGlobalRect)){
			return null;
		}
		
		focusedRect.left = focusedGlobalRect.left - gridGlobalRect.left - selectorPadding.left;
		focusedRect.right = focusedGlobalRect.right - gridGlobalRect.left + selectorPadding.right;
		focusedRect.top = focusedGlobalRect.top - gridGlobalRect.top - selectorPadding.top;
		focusedRect.bottom = focusedGlobalRect.bottom - gridGlobalRect.top + selectorPadding.bottom;
		
		return focusedRect;
	}
	
	public interface OnBeforeFocusChangeListener{
		public boolean onBeforeFocusChange(boolean gainFocus, int direction,Rect previouslyFocusedRect);
	}
	
}
