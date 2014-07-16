package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import com.yunos.tv.yingshi.R;

public class ZixunListView extends ListView {
	private static final String TAG = "ZixunListView";
	
	private Context mContext;

	/** 记录当前焦点所在区间*/ 
	private final byte FOCUS_INVALID = -2;
	private final byte FOCUS_NONE = -1;
	private final byte FOCUS_MIDDLE = 0;
	private final byte FOCUS_BOTTOM = 1;
	private final byte FOCUS_TOP = 2;
	
	/**焦点状态*/
	private byte mFocusState = FOCUS_INVALID;
	private byte mFocusOldState = FOCUS_INVALID;
	
	/**焦点滑动的时间*/
	private int sDuration = 250;
	
	/**ListView中每个item的高宽*/
	private int itemWidth;
	private int itemHeight;
	/**整个ListView的高度*/
	private int listHeight;
	/**是否已经拿到了item的高度*/
	private boolean hadItemHeight;

	/**选中项焦点框属性值*/
	private Drawable mFocusDrawable;
	private float mFocusExpandingLeft;
	private float mFocusExpandingTop;
	private float mFocusExpandingRight;
	private float mFocusExpandingBottom;
	
	/**是否被调用了setSelection，如果调用了就必须强制刷新焦点图片的位置*/
	private boolean isSetSelection;
	
	//是否是list滑动了，用于focus在Top or Bottom的时候向上或向下滑动列表
	//当ListView滚动到上端和下端，ListView应该替换item的时候
	private boolean isListScroll;
	private int listScrollDstTop;

	/**用于滑动的封装了加速减速器的计数类*/
	private Scroller mScroller;
	
	/**记录离开当前ListView时所在的焦点位置，用于Tab切换时从ListView切换到另外的控件上然后再切换回来还能保持上次焦点所在的位置*/
	private int lastSelection = -1;
	
	public ZixunListView(Context context) {
		super(context);
		initialize(context);
	}
	
	public ZixunListView(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.listViewStyle);
    }

    public ZixunListView(Context context, AttributeSet attrs, int defStyle) {
    	super( context, attrs, defStyle);
		readCustomAttrs(context, attrs);		
		initialize(context);
	}
    
    private void initialize(Context context){
    	mContext = context;
    	mScroller = new Scroller(context);
		
		/**修改默认的ListView的FadingEdge的东西，去掉该效果，达到更流畅*/
		setVerticalFadingEdgeEnabled(false);
    }
    
    /**
     * 读取自定义属性
     * @param context
     * @param attrs
     */
    private void readCustomAttrs(Context context, AttributeSet attrs) {
		TypedArray types = null;
		
		if ( attrs != null ){
			types = context.obtainStyledAttributes(attrs, R.styleable.ZixunListViewAttr);	
		}
		if (types != null) {
			mFocusDrawable = types.getDrawable(R.styleable.ZixunListViewAttr_focus_bg );
			mFocusExpandingLeft = types.getDimension(R.styleable.ZixunListViewAttr_focus_expanding_left, 0);
			mFocusExpandingTop = types.getDimension(R.styleable.ZixunListViewAttr_focus_expanding_top, 0);	
			mFocusExpandingRight = types.getDimension(R.styleable.ZixunListViewAttr_focus_expanding_right, 0);	
			mFocusExpandingBottom = types.getDimension(R.styleable.ZixunListViewAttr_focus_expanding_bottom, 0);	
			types.recycle();			
		}
	}
    
    /**
     * ListView Smooth移动完成的处理
     */
    private synchronized void finishListSmoothScroll(){
    	isListScroll = false;
		
		int firstPos = getFirstVisiblePosition();
		int selectedPos = getSelectedItemPosition();
		logD("finishListSmoothScroll, "	+ "'（'firstPos = " + firstPos + ", selectedPos = " + selectedPos);
		logD("finishListSmoothScroll, mFocusState =="+mFocusState+", listScrollDstTop="+listScrollDstTop);
		if ( mFocusState == FOCUS_BOTTOM ){
			setSelectionFromTop(selectedPos + 1, listScrollDstTop);
//			invalidate();
		}else if ( mFocusState == FOCUS_TOP ){
			setSelectionFromTop(selectedPos - 1, listScrollDstTop);
//			invalidate();				
		}
    }

    /**
     * ListView的ScrollListener
     */
    private OnScrollListener mListScrollListener = new OnScrollListener(){
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			logD("onScrollStateChanged, scrollState = " + scrollState);
			if ( scrollState == OnScrollListener.SCROLL_STATE_IDLE ){
				finishListSmoothScroll();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			logD("onScroll, firstVisibleItem = " + firstVisibleItem + ", visibleItemCount="+visibleItemCount + 
						", totalItemCount="+totalItemCount+ ", selectedPos="+getSelectedItemPosition());
		}
    };
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		itemWidth = getWidth();
		listHeight = getHeight();

		//ListView刚创建的时候没有child，因为还没有setAdapter，但是创建的时候会调用layout，所以要判断一下，要不然会报错
		if (getChildCount() > 0) {
			//如果获取ListView的高度了就不要再调用这个方法了，要不然某些情况高度会变成0，同时对性能也有好处
			if (!hadItemHeight) {
				itemHeight = getChildAt(0).getHeight();
				hadItemHeight = true;
			}
		}
		logD("onLayout, itemWidth="+itemWidth+", itemHeight="+itemHeight+", listHeight"+listHeight);
		logD("onLayout, padding=["+getListPaddingLeft()+", "+getListPaddingTop()+", "+getListPaddingRight()+", "+getListPaddingBottom()+"]");
		logD("onLayout, dividerHeight="+getDividerHeight()+", verticalFadingEdgeLength="+getVerticalFadingEdgeLength());
	}

	//所有的效果都是通过这个回调方法完成，这个方法很重要
	protected void onDraw(Canvas canvas) {	
		View selectedView = getSelectedView();
		int selectedPos = getSelectedItemPosition();
		
		logD("onDraw, selectedPos="+selectedPos);
		
		//没有item被选择，意味着不需要focus，这种情况可能发生在触摸滑动的时候
		if ( selectedView == null ){
			resetFocus();//清除ListView的焦点
			return;
		}else if ( mFocusState == FOCUS_NONE){
			initFocus(getSelectedItemPosition());
		}
		
		logD("onDraw-to draw focus");
		//如果外面调用了ListView的setSelection方法就会刷新并且返回，不执行下面的代码
		logD("onDraw, isSetSelection == " + isSetSelection);
		if ( isSetSelection) {
			logD("onDraw, isSetSelection, getSelectedView() == null" + (null == getSelectedView()));
			if (null != selectedView) {
				drawFocusBg(canvas, 0, selectedView.getTop());
				mScroller.setFinalY(selectedView.getTop());
				mScroller.abortAnimation();
				isSetSelection = false;
			}
		}

		//焦点一边滑动一边刷新，直到Scroller滑动结束，将isScroll置false
		if (mScroller.computeScrollOffset()) {
			//不断的回调onDraw
			logD("onDraw, mScroller.computeScrollOffset() == true");
			invalidate();	 	
		}
		
		drawFocusBg(canvas, 0, mScroller.getCurrY());
	}
	
	/**
	 * 初始化焦点框
	 * @param position
	 */
	private void initFocus(int position){
		logD("initFocus, mFocusState="+mFocusState);
		mFocusOldState = FOCUS_NONE;
		if ( position == getFirstVisiblePosition() ){
			mFocusState = FOCUS_TOP;
		}else if ( position == getLastVisiblePosition() ){
			mFocusState = FOCUS_BOTTOM;
		}else if ( position > getFirstVisiblePosition() && position < getLastVisiblePosition() ){
			mFocusState = FOCUS_MIDDLE;
		}
		
		if(mFocusState != FOCUS_NONE && mFocusState != FOCUS_INVALID){
			isSetSelection = true;
		}
		
	}
	
	/**
	 * 重置Focus（即清除焦点）
	 */
	private void resetFocus(){
		logD("resetFocus, mFocusState="+mFocusState);
		if ( mFocusState != FOCUS_INVALID ){		
			mFocusState = mFocusOldState = FOCUS_NONE;
			lastSelection = -1;
			isSetSelection = false;
		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,	Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		logD("onFocusChanged, gainFocus="+gainFocus+", direction"+direction);
		
		
		if (getChildCount() > 0) {
			if (!gainFocus) {//离开ListView时候记录焦点位置
				lastSelection = getSelectedItemPosition();
				mFocusOldState = mFocusState;
				mFocusState = FOCUS_INVALID;
			} else {//回来时ListView时候记录焦点位置
				if (null != getSelectedView()) {
					if ( mFocusOldState <= FOCUS_NONE ){
						lastSelection = getFirstVisiblePosition();
						initFocus(lastSelection);
					}else{
						mFocusState = mFocusOldState;
						drawFocusBg(null, 0, mScroller.getCurrY());//added by linghu
					}					
					isSetSelection = true;
					setSelectionFromTop(lastSelection, getSelectedView().getTop()-getListPaddingTop());
				}
			}
		}
	}

	@Override
	public void setSelection(int position) {
		super.setSelection(position);
		isSetSelection = true;
	}

	/**
	 * return the number of items at present
	 * 返回当前显示的Item数量
	 * @return the number of items at present
	 */
	public int getItemNum() {
		return getChildCount();
	}
	
	private void startFocusScroll(int startY, int dy){
		if ( mScroller.isFinished() ){
			logD("startScroll, is finihed, real start");
			mScroller.startScroll(0, startY, 0, dy, sDuration);
		}else{
//			mScroller.setFinalY(startY+dy);
			logD("startScroll, not finihed, set final Y");
			//modified by linghu, down is all added
		    if(startY + dy >= this.getBottom()){
				this.setSelection(this.getLastVisiblePosition());
			} else {
				mScroller.setFinalY(startY+dy);
			}
		}
		
	}

	private void smoothScrollList(int direction){
		logD("smoothScrollListBy, direction="+direction);
		if ( !isListScroll ){
			int distance = itemHeight + getDividerHeight();
			if ( direction == FOCUS_UP ){
				setOnScrollListener(mListScrollListener);
				isListScroll = true;
				listScrollDstTop = getSelectedView().getTop()-getListPaddingTop();
				smoothScrollBy(-distance, sDuration);
				/*if(getSelectedItemPosition()==getFirstVisiblePosition()+1){
					smoothScrollToPosition(getSelectedItemPosition()-1);
				}else{
					smoothScrollBy(-distance, sDuration);
				}*/
				
			}else if ( direction == FOCUS_DOWN ){
				setOnScrollListener(mListScrollListener);
				isListScroll = true;
				listScrollDstTop = getSelectedView().getTop()-getListPaddingTop(); 
				smoothScrollBy(distance, sDuration);
			/*	if(getSelectedItemPosition()==getAdapter().getCount() -2){
					smoothScrollToPosition(getSelectedItemPosition()+1);
				}else{
					smoothScrollBy(distance, sDuration);
				}*/
			}
		}		
	}
	
	private void stopSmoothScrollList(){
		logD("stopSmoothScrollList, isListScroll="+isListScroll);
		if ( isListScroll ){
			smoothScrollBy(0, 0);
			finishListSmoothScroll();
		}
		
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}
	
	@Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		if ( isListScroll )
			return false;
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }
	
	private final static long MIN_KEYEVENT_DURATION = 150;
	private long lastKeyDownTime;

	/**ListView的item数量实际上是动态改变的，会在一个数值x和x+1甚至x+2之间徘徊，
	 * 所以利用item的数量来计算焦点的移动是不行的，所以增加的实现此功能的复杂度*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		logD("onKeyDown, keyCode="+keyCode+", keyEvent:"+event);
		//获得当前选中的item
		View view = getSelectedView();
		Log.d("linghu", "selectPosition="+getSelectedItemPosition());
		
		if ( System.currentTimeMillis() - lastKeyDownTime < MIN_KEYEVENT_DURATION 
				|| !mScroller.isFinished() || isListScroll ){
			return true;
		}
		lastKeyDownTime = System.currentTimeMillis();
		
		//下面的就是逻辑上的东西了，在最上，最下，和中间进行不同的移动
			if (null != view ) {
				
				//焦点所在item的最上面, 距离parent容器最上面的距离差
				int nextTop = 0;
				int selectedViewTop = view.getTop();
				int selectedItemPosition = getSelectedItemPosition();
				int lastVisiblePosition = getLastVisiblePosition();
				int firstVisiblePosition = getFirstVisiblePosition();
				int totalCount = getAdapter().getCount();
				
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_DOWN:
					if ( selectedItemPosition < lastVisiblePosition - 1 ) {
						logD("onKeyDown, KEYCODE_DPAD_DOWN, selectedItemPosition < lastVisiblePosition - 1");
						if ( !isListScroll ){
							nextTop = selectedViewTop + itemHeight + getDividerHeight();
							startFocusScroll(selectedViewTop, nextTop - selectedViewTop);
							mFocusState = FOCUS_MIDDLE;
						}
					} else if ( selectedItemPosition == lastVisiblePosition - 1 ) {
						logD("onKeyDown, KEYCODE_DPAD_DOWN, selectedItemPosition == lastVisiblePosition - 1");
						if (mFocusState != FOCUS_BOTTOM) {
							logD("onKeyDown, KEYCODE_DPAD_DOWN, mFocusState != FOCUS_BOTTOM");
							if ( !isListScroll ){
								//nextTop = listHeight - itemHeight - getDividerHeight() - getListPaddingBottom();
								//modified by linghu
								int count = (this.getHeight()-this.getPaddingTop()-this.getPaddingBottom())/(itemHeight+this.getDividerHeight());
								logD("onKeyDown,count= "+count);
								if(totalCount < count){
									nextTop = selectedViewTop + itemHeight + getDividerHeight();
								}else{
									nextTop = listHeight - itemHeight - getDividerHeight() - getVerticalFadingEdgeLength() - getListPaddingBottom();
								} 
								logD("onKeyDown,nextTop="+nextTop+",selectedViewTop="+selectedViewTop+"move distance= "+ (nextTop - selectedViewTop));
								startFocusScroll(selectedViewTop, nextTop - selectedViewTop);
								mFocusState = FOCUS_BOTTOM;
							}
						}else{
							if (isListScroll){
								stopSmoothScrollList();
							}else{
								try {
									logD("onKeyDown, KEYCODE_DPAD_DOWN, mFocusState == FOCUS_BOTTOM");
									if ( selectedItemPosition < (totalCount - 1) ){		
//										smoothScrollList(FOCUS_DOWN);
										Thread.sleep(200);
									}
									
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							 
						}
					}
					break;

				case KeyEvent.KEYCODE_DPAD_UP:
					if (selectedItemPosition > firstVisiblePosition + 1) {
						logD("onKeyDown, KEYCODE_DPAD_UP, selectedItemPosition > firstVisiblePosition");
						if ( !isListScroll ){
							nextTop = selectedViewTop - itemHeight - getDividerHeight();
							startFocusScroll(selectedViewTop, nextTop - selectedViewTop);
							mFocusState = FOCUS_MIDDLE;
						}
					}else if (selectedItemPosition == firstVisiblePosition + 1) {
						logD("onKeyDown, KEYCODE_DPAD_UP, selectedItemPosition == firstVisiblePosition + 1");
						logD( "selectedItemPosition="+selectedItemPosition+"firstVisiblePosition="+firstVisiblePosition);
						if (mFocusState != FOCUS_TOP) {
							Log.d("linghu", "44444444");
							logD("onKeyDown, KEYCODE_DPAD_UP, mFocusState != FOCUS_TOP");
							if ( !isListScroll ){
								nextTop = getDividerHeight() + getListPaddingTop() + getVerticalFadingEdgeLength();
							/*	if(selectedItemPosition == 1){   
									nextTop = 0 + getDividerHeight() + getVerticalFadingEdgeLength(); 
								}else{
									nextTop = getDividerHeight() + getListPaddingTop() + getVerticalFadingEdgeLength();
								}*/
								startFocusScroll(selectedViewTop, nextTop - selectedViewTop);
								mFocusState = FOCUS_TOP;
							}
						} else {
							if ( isListScroll ){
								stopSmoothScrollList();
							}else{
								try {
									logD("onKeyDown, KEYCODE_DPAD_UP, mFocusState == FOCUS_TOP");
									if ( selectedItemPosition > 0 ){
//										smoothScrollList(FOCUS_UP);
										Thread.sleep(200);
									}
								} catch (Exception e) {
									e.printStackTrace();
								} 
							}
						}
					}
					break;
				case KeyEvent.KEYCODE_DPAD_LEFT:
					logD("onKeyDown, KEYCODE_DPAD_LEFT");
					TabManager.instance().moveTo(TabManager.MOVE_TO_PREV_TAB);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					logD("onKeyDown, KEYCODE_DPAD_RIGHT");
					TabManager.instance().moveTo(TabManager.MOVE_TO_NEXT_TAB);
					break;
				default:
					break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**通过此方法设置焦点背景图片*/
	public void setFocusDrawable(Drawable focusDrawable) {
		mFocusDrawable = focusDrawable;
	}
	
	public void setFocusExpanding(int dpLeft, int dpTop, int dpRight, int dpBottom){
		mFocusExpandingLeft = complexToDimension(TypedValue.COMPLEX_UNIT_DIP, dpLeft);
		mFocusExpandingTop = complexToDimension(TypedValue.COMPLEX_UNIT_DIP, dpTop);
		mFocusExpandingRight = complexToDimension(TypedValue.COMPLEX_UNIT_DIP, dpRight);
		mFocusExpandingBottom = complexToDimension(TypedValue.COMPLEX_UNIT_DIP, dpBottom);
	}
	
	private float complexToDimension(int unit, int data){			
		return TypedValue.applyDimension(unit, data, mContext.getResources().getDisplayMetrics());
    }
	
	/**
	 * 画焦点框Focus
	 * @param canvas
	 * @param left
	 * @param top
	 */
	private void drawFocusBg(Canvas canvas, float left, float top){
		if ( mFocusState > FOCUS_NONE && mFocusDrawable != null){
			logD("drawFocusBg, left = " + left + ", top = " + top );
			float el = mFocusExpandingLeft;
			float et = mFocusExpandingTop;
			float er = mFocusExpandingRight;
			float eb = mFocusExpandingBottom;
			int pl = getListPaddingLeft();
			int pr = getListPaddingRight();
			drawByDrawable(canvas, null, mFocusDrawable, left-el+pl, top-et, itemWidth+el+er-pl-pr, itemHeight+et+eb);
		}		
	}

	public static void drawByDrawable(Canvas canvas, Paint paint, Drawable drawable, 
			float dstLeft, float dstTop, float dstWidth, float dstHeight ){
    	
    	if ( canvas == null || drawable == null ) {
    		return;
    	}
    	Matrix matrix = new Matrix();
        Bitmap bitmap = null;
        float sx = 0.0f, sy = 0.0f;
        
        if ( drawable instanceof BitmapDrawable ){
        	bitmap = ((BitmapDrawable)drawable).getBitmap();
        	sx = dstWidth / bitmap.getWidth();
            sy = dstHeight / bitmap.getHeight();
            if ( sx != 1.0f || sy != 1.0f ){
            	if ( paint != null ){
            		paint.setFilterBitmap(true);
            	}
            	matrix.setScale(sx, sy);
            }           
            matrix.postTranslate(dstLeft, dstTop);//将图片平移到（x，y）点的位置
            canvas.drawBitmap(bitmap, matrix, paint);
         
        }else if ( drawable instanceof NinePatchDrawable ){
        	drawable.setBounds((int)dstLeft, (int)dstTop, (int)dstLeft+(int)dstWidth, (int)dstTop+(int)dstHeight);
        	drawable.draw(canvas);
        	
        }
        
    }
	
	private static void logI(String msg){
		Log.i(TAG, msg);
	}
	
	private static void logE(String msg){
		Log.e(TAG, msg);
	}
	
	private static void logD(String msg){
		Log.d(TAG, msg);
	}

}
