package com.yunos.tv.yingshi.ime;

import java.util.ArrayList;
import java.util.List;

import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.ime.ImeGrid.OnBeforeFocusChangeListener;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImeBoard extends FrameLayout {
	private static final String TAG = "SearchTVFragment";
	public static final int IME_MODE_T9 = 1;
	public static final int IME_MODE_PINYIN = 2;
	public static final int IME_MODE_BIHUA = 3;
	
	private Context mContext;
	private GridAdapter mAdapter;
	private GridAdapter mAdapterBihua;
	
	private ImeGrid mGrid;
	private ImeGrid mGridBihua;
	private Button btnModeT9;
	private Button btnModePinYin;
	private Button btnModeBihua;
	
	private int mCurrentMode = IME_MODE_PINYIN;
	private FrameLayout mViewPinYin;
	private PinYinViewHolder mPinyinHolder;
	
	private OnKeyWordDetermineListener mOnKeyWordClickListener;
	private OnImeModeChangeListener mOnImeModeChangeListener;
	
	private void logd(String msg){
		Log.d(TAG, msg);
	}
	
	public ImeBoard(Context context) {
		super(context);
		mContext = context;
		initBoard();
	}
	
	public ImeBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initBoard();
	}
	
	public ImeBoard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initBoard();
	}
		
	public void setImeMode(int mode){
		if(mCurrentMode != mode){
			mCurrentMode = mode;
			if(mCurrentMode == IME_MODE_BIHUA){
				mGridBihua.setVisibility(View.VISIBLE);
				mGrid.setVisibility(View.GONE);
				btnModeBihua.setSelected(true);
			} else {
				mGrid.setVisibility(View.VISIBLE);
				mGridBihua.setVisibility(View.GONE);
				if(mCurrentMode == IME_MODE_T9){
					mGrid.setNextFocusDownId(R.id.btn_ime_t9);
					btnModeT9.setSelected(true);
				} else {
					mGrid.setNextFocusDownId(R.id.btn_ime_pinyin);
					btnModePinYin.setSelected(true);
				}
			}
			if(mOnImeModeChangeListener != null){
				mOnImeModeChangeListener.onChange(mode);
			}
		}
	}
	
	@Override
	public void setNextFocusLeftId(int nextFocusLeftId) {
		super.setNextFocusLeftId(nextFocusLeftId);
		mGrid.setNextFocusLeftId(nextFocusLeftId);
		mGridBihua.setNextFocusLeftId(nextFocusLeftId);
		btnModeT9.setNextFocusLeftId(nextFocusLeftId);
	}

	@Override
	public void setNextFocusRightId(int nextFocusRightId) {
		super.setNextFocusRightId(nextFocusRightId);
		mGrid.setNextFocusRightId(nextFocusRightId);
		mGridBihua.setNextFocusRightId(nextFocusRightId);
		btnModeBihua.setNextFocusRightId(nextFocusRightId);
	}

	@Override
	public void setNextFocusDownId(int nextFocusDownId) {
		super.setNextFocusDownId(nextFocusDownId);
		mGrid.setNextFocusDownId(nextFocusDownId);
		mGridBihua.setNextFocusDownId(nextFocusDownId);
		btnModeBihua.setNextFocusDownId(nextFocusDownId);
		btnModePinYin.setNextFocusDownId(nextFocusDownId);
		btnModeT9.setNextFocusDownId(nextFocusDownId);
	}
	
	@Override
	public void setNextFocusUpId(int nextFocusUpId) {
		super.setNextFocusUpId(nextFocusUpId);
		mGrid.setNextFocusUpId(nextFocusUpId);
		mGridBihua.setNextFocusUpId(nextFocusUpId);
	}
	
	private OnKeyListener mImeOnKeyListener;
	@Override
	public void setOnKeyListener(OnKeyListener l) {
		mImeOnKeyListener = l;
		mGrid.setOnKeyListener(l);
		btnModeBihua.setOnKeyListener(l);
		btnModePinYin.setOnKeyListener(l);
		btnModeT9.setOnKeyListener(l);
    }

	public int getImeMode(){
		return mCurrentMode;
	}
	
	public void setOnImeModeChangeListener(OnImeModeChangeListener l){
		mOnImeModeChangeListener = l;
	}
	public void setOnKeyWordClickListener(OnKeyWordDetermineListener l){
		mOnKeyWordClickListener = l;
	}
	
	private void initBoard(){
		logd("initBoard1");
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.ime_board, this);
		mGrid = (ImeGrid)findViewById(R.id.ime_grid);
		mGridBihua = (ImeGrid)findViewById(R.id.ime_grid_bihua);
		btnModeT9 = (Button)findViewById(R.id.btn_ime_t9);
		btnModePinYin = (Button)findViewById(R.id.btn_ime_pinyin);
		btnModeBihua = (Button)findViewById(R.id.btn_ime_bihua);
		mViewPinYin = (FrameLayout)findViewById(R.id.layout_pinyin_expand);
		logd("initBoard2");
		
		initPinyinHolder();
		logd("initBoard3");
		
		mAdapter = new GridAdapter();
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(mGridItemClick);
		
		mAdapterBihua = new GridAdapter(true);
		mGridBihua.setAdapter(mAdapterBihua);
		mGridBihua.setOnItemClickListener(mGridItemClick);
		mGridBihua.setOnKeyListener(mGridBiHuaOnKey);
		mGridBihua.setOnBeforeFocusChangeListener(mOnBeforeFocusChangeListener);
		
		btnModeT9.setOnClickListener(mBtnModeClick);
		btnModePinYin.setOnClickListener(mBtnModeClick);
		btnModeBihua.setOnClickListener(mBtnModeClick);
		
		btnModeT9.setOnFocusChangeListener(mBtnModeFocused);
		btnModePinYin.setOnFocusChangeListener(mBtnModeFocused);
		btnModeBihua.setOnFocusChangeListener(mBtnModeFocused);

		logd("initBoard4");
		initGrids();
		logd("initBoard5");
	}
	
	//初始化拼音弹出框	
	private void initPinyinHolder(){
		mPinyinHolder = new PinYinViewHolder();
		mPinyinHolder.center = (TextView)findViewById(R.id.ime_py_center);
		mPinyinHolder.left = (TextView)findViewById(R.id.ime_py_left);
		mPinyinHolder.top = (TextView)findViewById(R.id.ime_py_top);
		mPinyinHolder.right = (TextView)findViewById(R.id.ime_py_right);
		mPinyinHolder.bottom = (TextView)findViewById(R.id.ime_py_bottom);
		
		mPinyinHolder.center.setOnClickListener(mPinYinClick);
		mPinyinHolder.left.setOnClickListener(mPinYinClick);
		mPinyinHolder.top.setOnClickListener(mPinYinClick);
		mPinyinHolder.right.setOnClickListener(mPinYinClick);
		mPinyinHolder.bottom.setOnClickListener(mPinYinClick);
		
		mPinyinHolder.center.setOnKeyListener(new PinYinOnKeyListener());
		mPinyinHolder.left.setOnKeyListener(new PinYinOnKeyListener(KeyEvent.KEYCODE_DPAD_LEFT));
		mPinyinHolder.top.setOnKeyListener(new PinYinOnKeyListener(KeyEvent.KEYCODE_DPAD_UP));
		mPinyinHolder.right.setOnKeyListener(new PinYinOnKeyListener(KeyEvent.KEYCODE_DPAD_RIGHT));
		mPinyinHolder.bottom.setOnKeyListener(new PinYinOnKeyListener(KeyEvent.KEYCODE_DPAD_DOWN));
	}
	
	class PinYinOnKeyListener implements OnKeyListener{
		private int disabledKey; 

		public PinYinOnKeyListener(){}
		public PinYinOnKeyListener(int key){
			disabledKey = key;
		}
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			//返回按钮
			if(keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_BACK){
				hidePinYin();
				return true;
			}
			Object objItem = mGrid.getSelectedItem();
			if(objItem != null){
				GridItem item = (GridItem)objItem;
				
				//如果只有三个选项,则只允许上面的字母可以点向下键
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN && item.bottom == null && v != mPinyinHolder.top){
					return true;
				}
				
				if(keyCode == disabledKey){
					return true;
				}
			}
			return false;
		}
		
	}
	
	private OnBeforeFocusChangeListener mOnBeforeFocusChangeListener = new OnBeforeFocusChangeListener() {
		private int lastSelection = 1;
		@Override
		public boolean onBeforeFocusChange(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
			if(gainFocus){
				mGridBihua.setSelection(lastSelection);
			} else {
				lastSelection = mGridBihua.getSelectedItemPosition();
			}
			return true;
		}
	};
	
	private OnKeyListener mGridBiHuaOnKey = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			int setPos = -1;
			if(event.getAction() == KeyEvent.ACTION_DOWN ){
				int pos = mGridBihua.getSelectedItemPosition();
				if(pos == 1){
					if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
						setPos = 3;
					} else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
						setPos = 5;
					}
				} else if((pos == 3 || pos == 5 )&& keyCode == KeyEvent.KEYCODE_DPAD_UP){
					setPos = 1;
				}
			}
			if(setPos != -1){
				mGridBihua.setSelection(setPos);
				return true;
			} else {
				if(mImeOnKeyListener != null){
					return mImeOnKeyListener.onKey(v, keyCode, event);
				}
				return false;
			}
		}
	};
	
	private OnFocusChangeListener mBtnModeFocused = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				if(v == btnModeT9){
					setImeMode(IME_MODE_T9);
				} else if(v == btnModePinYin){
					setImeMode(IME_MODE_PINYIN);
				} else if(v == btnModeBihua){
					setImeMode(IME_MODE_BIHUA);
				}
			}
		}
	};
	
	private OnClickListener mBtnModeClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == btnModeT9){
				setImeMode(IME_MODE_T9);
			} else if(v == btnModePinYin){
				setImeMode(IME_MODE_PINYIN);
			} else if(v == btnModeBihua){
				setImeMode(IME_MODE_BIHUA);
			}
		}
	};

	private OnItemClickListener mGridItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Object clickedItem = parent.getItemAtPosition(position);
			GridItem gridItem = null;
			
			if(clickedItem == null){
				return;
			}
			gridItem = (GridItem)clickedItem;
			if(gridItem.dispatchMode == GridItem.DISPATCH_CLEAR){//清空
				if(mOnKeyWordClickListener != null){
					mOnKeyWordClickListener.onClear();
				}
			} else if(gridItem.dispatchMode == GridItem.DISPATCH_DELETE){//删除
				if(mOnKeyWordClickListener != null){
					mOnKeyWordClickListener.onDelete();
				}
			} else {
				if(mCurrentMode == IME_MODE_T9){
					if(mOnKeyWordClickListener != null){
						mOnKeyWordClickListener.onClicked(gridItem.center);
					}
				} else if(mCurrentMode == IME_MODE_PINYIN){
					if(!gridItem.isExpand){
						if(mOnKeyWordClickListener != null){
							mOnKeyWordClickListener.onClicked(gridItem.center);
						}
					} else {
						showPinYin(gridItem);
					}
				} else if(mCurrentMode == IME_MODE_BIHUA){
					if(mOnKeyWordClickListener != null){
						mOnKeyWordClickListener.onClicked(gridItem.center);
					}
				}
			}
		}
	};
	
	private void initGrids(){
		logd("initGrids1");
		ArrayList<GridItem> list = new ArrayList<GridItem>(12);
		list.add(new GridItem("1"));
		list.add(new GridItem("2","A","B","C",null,true));
		list.add(new GridItem("3","D","E","F",null,true));
		list.add(new GridItem("4","G","H","I",null,true));
		list.add(new GridItem("5","J","K","L",null,true));
		list.add(new GridItem("6","M","N","O",null,true));
		list.add(new GridItem("7","P","Q","R","S",true));
		list.add(new GridItem("8","T","U","V",null,true));
		list.add(new GridItem("9","W","X","Y","Z",true));
		list.add(new GridItem(R.drawable.ic_tv_search_keyboard_clean, getResources().getString(R.string.ime_grid_item_clear), GridItem.DISPATCH_CLEAR));
		list.add(new GridItem("0"));
		list.add(new GridItem(R.drawable.ic_tv_search_keyboard_delete, getResources().getString(R.string.ime_grid_item_delete), GridItem.DISPATCH_DELETE));
		mAdapter.items = list;
		mAdapter.notifyDataSetChanged();
		
		ArrayList<GridItem> listBihua = new ArrayList<GridItem>(6);
		listBihua.add(new GridItem(""));
		listBihua.add(new GridItem("一"));
		listBihua.add(new GridItem(""));
		listBihua.add(new GridItem("丿"));
		listBihua.add(new GridItem("丨"));
		listBihua.add(new GridItem("丶"));
		listBihua.add(new GridItem(R.drawable.ic_tv_search_keyboard_clean, getResources().getString(R.string.ime_grid_item_clear), GridItem.DISPATCH_CLEAR));
		listBihua.add(new GridItem("乛"));
		listBihua.add(new GridItem(R.drawable.ic_tv_search_keyboard_delete, getResources().getString(R.string.ime_grid_item_delete), GridItem.DISPATCH_DELETE));
		mAdapterBihua.items = listBihua;
		mAdapterBihua.notifyDataSetChanged();
		
		logd("initGrids2");
	}
	
	private OnClickListener mPinYinClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			TextView tv = (TextView)v;
			if(mOnKeyWordClickListener != null){
				String str = tv.getText().toString();
				mOnKeyWordClickListener.onClicked(str);
			}
			hidePinYin();
		}
	};
	
	private void hidePinYin(){
		mGrid.requestFocus();
		mViewPinYin.setVisibility(View.GONE);
	}
	
	private void showPinYin(GridItem item){
		
		//把第一个字母放中间,数字放在右边
		mPinyinHolder.setCenter(item.left);
		mPinyinHolder.setLeft(item.top);
		mPinyinHolder.setTop(item.right);
		mPinyinHolder.setRight(item.center);
		mPinyinHolder.setBottom(item.bottom);
		
//		mPinyinHolder.setCenter(item.center);
//		mPinyinHolder.setLeft(item.left);
//		mPinyinHolder.setTop(item.top);
//		mPinyinHolder.setRight(item.right);
//		mPinyinHolder.setBottom(item.bottom);
		
		Rect rBoard = new Rect();
		Rect rGrid = new Rect();
		Rect r = mGrid.getFocusedRect();
		
		if(!mGrid.getGlobalVisibleRect(rGrid) || !getGlobalVisibleRect(rBoard) || r == null){
			return;
		}

		r.left += rGrid.left - rBoard.left;
		r.top += rGrid.top - rBoard.top;
		r.right += rGrid.left - rBoard.left;
		r.bottom += rGrid.top - rBoard.top;
		
		mViewPinYin.setVisibility(View.VISIBLE);
		LayoutParams lp = (LayoutParams)mViewPinYin.getLayoutParams();
		lp.leftMargin =  (r.left + r.right) / 2 - mViewPinYin.getWidth() / 2;
		lp.topMargin = (r.top + r.bottom) / 2 - mViewPinYin.getHeight() / 2;

		mViewPinYin.setLayoutParams(lp);
		mPinyinHolder.center.requestFocus();
	}
	
	class PinYinViewHolder{
		public TextView center;
		public TextView left;
		public TextView top;
		public TextView right;
		public TextView bottom;
		
		public void setCenter(String w){
			setWords(center,w);
		}
		
		public void setLeft(String w){
			setWords(left,w);
		}
		
		public void setTop(String w){
			setWords(top,w);
		}
		
		public void setRight(String w){
			setWords(right,w);
		}
		
		public void setBottom(String w){
			setWords(bottom,w);
		}
		
		private void setWords(TextView v,String w){
			if(v != null){
				if(w != null){
					v.setText(w);
					v.setFocusable(true);
				} else {
					v.setText("");
					v.setFocusable(false);
				}
			}
		}
	}
	
	public interface OnKeyWordDetermineListener{
		public void onClicked(String display);
		public void onDelete();
		public void onClear();
	}
	
	public interface OnImeModeChangeListener{
		public void onChange(int mode);
	}
	
	
	public static class GridItem{
		public static final int DISPATCH_WORD = 0;
		public static final int DISPATCH_CLEAR = 1;
		public static final int DISPATCH_DELETE = 2;
		
		public String center = null;
		public String left = null;
		public String top = null;
		public String right = null;
		public String bottom = null;
		public String tipWords = null;
		public String value = null;
		public int iconRes = -1;
		public boolean isExpand = false;
		public int dispatchMode = DISPATCH_WORD;
		public GridItem(){}
		
		//只限制zhong
		public GridItem(String n){
			center = n;
		}

		//弹出的
		public GridItem(String n,String l,String t,String r,String b,boolean e){
			center = n;
			left = l;
			top = t;
			right = r;
			bottom = b;
			isExpand = e;
		}
		public GridItem(int i,String tip,int d){
			iconRes = i;
			tipWords = tip;
			dispatchMode = d;
		}
		
		public String getBottomWords(){
			String str = left != null? left : ""
						+ top != null? top :""
						+ right != null? right :""
						+ bottom != null? bottom : "";
			if(str.equals("")){
				str = tipWords != null?tipWords:"";
			}
			return str;
		}
	}
	
	class GridAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private boolean isHiddenBottom;
		
		public List<GridItem> items;
		public GridAdapter(){
			inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public GridAdapter(boolean hiddenBottom){
			this();
			isHiddenBottom = hiddenBottom;
		}
		@Override
		public int getCount() {
			return items != null? items.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return items != null && position < items.size()? items.get(position):null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = inflater.inflate(R.layout.ime_grid_item, null);
			}
			TextView tvNum = (TextView)convertView.findViewById(R.id.ime_text_num);
			ImageView image = (ImageView)convertView.findViewById(R.id.ime_icon);
			LinearLayout layoutWords = (LinearLayout)convertView.findViewById(R.id.ime_text_words);
			
			GridItem item = (GridItem)getItem(position);
			if(item.center != null){
				tvNum.setVisibility(View.VISIBLE);
				image.setVisibility(View.GONE);
				tvNum.setText(item.center);
			} else if(item.iconRes != -1) {
				tvNum.setVisibility(View.GONE);
				image.setVisibility(View.VISIBLE);
				image.setImageResource(item.iconRes);
			} else {
				return null;
//				convertView.setVisibility(View.INVISIBLE);
			}
			layoutWords.removeAllViews();
			
			String[] bottomWords = (item.tipWords != null) ? new String[]{item.tipWords}:
									new String[]{item.left,item.top,item.right,item.bottom};
			boolean hasContent = false;
			for(int i = 0; i < bottomWords.length; i ++){
				if(bottomWords[i] != null){
					hasContent = true;
					TextView t = (TextView)inflater.inflate(R.layout.ime_word, null);
					t.setText(bottomWords[i]);
					layoutWords.addView(t);
				}
			}
			if(!hasContent && isHiddenBottom){
				layoutWords.setVisibility(View.GONE);
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutWords.getLayoutParams();
				LinearLayout.LayoutParams lptv = (LinearLayout.LayoutParams)tvNum.getLayoutParams();
				lptv.topMargin = (lp.height + lp.topMargin + lp.bottomMargin) / 2;
				lptv.bottomMargin = (lp.height + lp.topMargin + lp.bottomMargin) / 2;
//				lptv.height = lptv.height + lp.height + lp.topMargin + lp.bottomMargin;
				tvNum.setLayoutParams(lptv);
//				tvNum.setHeight(tvNum.getHeight() + layoutWords.getHeight() + lp.topMargin + lp.bottomMargin);
			}
			return convertView;
		}
	}
}
