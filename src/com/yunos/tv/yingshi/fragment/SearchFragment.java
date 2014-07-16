package com.yunos.tv.yingshi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.base.WorkAsyncTask;
import com.aliyun.base.info.MobileInfo;
import com.aliyun.base.net.NetConnectionType;
import com.aliyun.base.net.NetworkManager;
import com.aliyun.base.net.NetworkManager.INetworkListener;
import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.entity.ImageArg;
import com.aliyun.imageload.entity.ImageScaleType;
import com.yunos.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tv.app.widget.FocusedListView;
import com.yunos.tv.app.widget.FocusedListView.onKeyDownListener;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.player.PlayerInvoke;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.HotWord;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.activity.YingshiDetailActivity;
import com.yunos.tv.yingshi.ime.ImeBoard;
import com.yunos.tv.yingshi.ime.ImeBoard.OnKeyWordDetermineListener;
import com.yunos.tv.yingshi.widget.MaskImageView;
import com.yunos.tv.yingshi.widget.MaskImageView.ImageSize;

public class SearchFragment extends BaseTVFragment {
	private static final String TAG = "SearchTVFragment";
	private static final String SAVED_IME_MODE_KEY = "saved_ime_mode";
	private static final String SHOWTYPE_ALL = "";
	private static final String SHOWTYPE_DIANYING = "1";
	private static final String SHOWTYPE_DIANSHIJU = "3";
	private static final String SHOWTYPE_ZIXUN = "0";

	private View rootView = null;

	private ImageLoadParam loadParam;
	private String currentShowType;

	private EditText etKey;
	// private TextView tvResultCount;
	private ImeBoard mImeBoard;
	private TextView tvSearchTipText;

	private ViewGroup layoutNodata;
	private ViewGroup layoutExample;
	private ViewGroup layoutContent;
	private ViewGroup layoutLoading;
	private ViewGroup layoutSearchResult;
	private ViewGroup layoutSearchMenus;

	private ViewGroup layoutHotWrap;
	private ViewGroup rowWrapLayout;

	private static Spanned spannedT9 = null;
	private static Spanned spannedPinyin = null;
	private static Spanned spannedBihua = null;

	private String keyType = null;
	private SharedPreferences share;

	private WorkAsyncTask<ArrayList<Program>> searchTask = null;

	public SearchFragment() {
	}

	public SearchFragment(int containerViewId, Context context) {
		super(R.layout.search, containerViewId, context.getString(R.string.search), context);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putAll(getArguments());
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		logd("onCreate1");
		super.onCreate(savedInstanceState);
		NetworkManager.instance().registerStateChangedListener(mINetworkListener);
		logd("onCreate2");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		NetworkManager.instance().unregisterStateChangedListener(mINetworkListener);
	}

	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		logd("onViewCreated1");
		super.onViewCreated(rootView, savedInstanceState);
		this.rootView = rootView;
		etKey = (EditText) rootView.findViewById(R.id.et_key);
		// tvResultCount = (TextView)
		// rootView.findViewById(R.id.tv_result_count);
		mImeBoard = (ImeBoard) rootView.findViewById(R.id.ime_board);
		tvSearchTipText = (TextView) rootView.findViewById(R.id.search_tip_text);
		layoutExample = (ViewGroup) rootView.findViewById(R.id.layout_search_example);
		layoutNodata = (ViewGroup) rootView.findViewById(R.id.layout_search_nodata);
		layoutContent = (ViewGroup) rootView.findViewById(R.id.layout_search_content);
		layoutLoading = (ViewGroup) rootView.findViewById(R.id.layout_search_loading);
		layoutSearchResult = (ViewGroup) rootView.findViewById(R.id.layout_search_grid);
		layoutSearchMenus = (ViewGroup) rootView.findViewById(R.id.search_type_menus);

		logd("onViewCreated2");
		// tvMenuAll = (TextView) rootView.findViewById(R.id.search_menu_all);
		// tvMenuDianying = (TextView)
		// rootView.findViewById(R.id.search_menu_dianying);
		// tvMenuDianshiju = (TextView)
		// rootView.findViewById(R.id.search_menu_dianshiju);
		// tvMenuZixun = (TextView)
		// rootView.findViewById(R.id.search_menu_zixun);

		// 热词
		layoutHotWrap = (ViewGroup) rootView.findViewById(R.id.layout_hotwords_wrap);
		rowWrapLayout = (ViewGroup) rootView.findViewById(R.id.layout_rowwrap);

		spannedT9 = Html.fromHtml(getContext().getResources().getString(R.string.search_input_tip_text_t9));
		spannedPinyin = Html.fromHtml(getContext().getResources().getString(R.string.search_input_tip_text_pinyin));
		spannedBihua = Html.fromHtml(getContext().getResources().getString(R.string.search_input_tip_text_bihua));

		logd("onViewCreated3");
		// tvSearchTipText.setText(spannedT9);
		updateSearchTipText(mImeBoard.getImeMode());
		mImeBoard.setOnKeyWordClickListener(mImeKeyWordListener);
		mImeBoard.setOnImeModeChangeListener(mOnImeModeChangeListener);
		mImeBoard.setOnKeyListener(mOnImeKeyListener);
		initMenus();
		initListViews();
		getHotWords();
		etKey.addTextChangedListener(textWatcher);
		setKeyEvents();

		logd("onViewCreated4");
		share = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
		int imeMode = share.getInt(SAVED_IME_MODE_KEY, ImeBoard.IME_MODE_PINYIN);
		// Log.i("abcde","savestatus save imeMode:" + imeMode);
		mImeBoard.setImeMode(imeMode);

		setRightMenuSelected(SHOWTYPE_ALL);
		keyType = String.valueOf(imeMode);
		logd("onViewCreated5");
	}

	private boolean rightMenuLeftKeyEvent(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN && layoutNodata.isShown()) {
			View bihuaGrid = rootView.findViewById(R.id.ime_grid_bihua);
			View grid = rootView.findViewById(R.id.ime_grid);
			int mode = mImeBoard.getImeMode();
			if (mode == ImeBoard.IME_MODE_BIHUA && bihuaGrid != null) {
				bihuaGrid.requestFocus();
			} else if (grid != null) {
				grid.requestFocus();
			}
			return true;
		}
		return false;
	}

	private void setKeyEvents() {
		TextView tvMenuAll = (TextView) rootView.findViewById(R.id.search_menu_all);
		TextView tvMenuDianying = (TextView) rootView.findViewById(R.id.search_menu_dianying);
		TextView tvMenuDianshiju = (TextView) rootView.findViewById(R.id.search_menu_dianshiju);
		TextView tvMenuZixun = (TextView) rootView.findViewById(R.id.search_menu_zixun);

		tvMenuAll.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					return true;
				}

				return rightMenuLeftKeyEvent(v, keyCode, event);
			}
		});
		tvMenuDianying.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return rightMenuLeftKeyEvent(v, keyCode, event);
			}
		});
		tvMenuDianshiju.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return rightMenuLeftKeyEvent(v, keyCode, event);
			}
		});

		tvMenuZixun.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					return true;
				}
				return rightMenuLeftKeyEvent(v, keyCode, event);
			}
		});

		etKey.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					if (layoutSearchResult.isShown()) {
						layoutSearchResult.requestFocus();
						return true;
					} else if (layoutNodata.isShown()) {
						focusRightSelectedMenu();
						// layoutSearchMenus.requestFocus();
						return true;
					}
				}
				return false;
			}
		});

		// etKey.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// keyType = null;
		// }
		// }
		// });
	}

	private void focusRightSelectedMenu() {
		for (int i = 0; i < layoutSearchMenus.getChildCount(); i++) {
			View child = layoutSearchMenus.getChildAt(i);
			if (child != null && child.isSelected()) {
				child.requestFocus();
			}
		}
	}

	private void updateSearchTipText(int mode) {
		if (mode == ImeBoard.IME_MODE_T9) {
			tvSearchTipText.setText(spannedT9);
		} else if (mode == ImeBoard.IME_MODE_PINYIN) {
			tvSearchTipText.setText(spannedPinyin);
		} else if (mode == ImeBoard.IME_MODE_BIHUA) {
			tvSearchTipText.setText(spannedBihua);
		}
	}

	private ImeBoard.OnImeModeChangeListener mOnImeModeChangeListener = new ImeBoard.OnImeModeChangeListener() {
		@Override
		public void onChange(int mode) {
			keyType = String.valueOf(mode);
			etKey.setText("");
			for (int i = 0; i < layoutSearchResult.getChildCount(); i++) {
				View list = layoutSearchResult.getChildAt(i);
				if (mode == ImeBoard.IME_MODE_BIHUA) {
					list.setNextFocusLeftId(R.id.ime_grid_bihua);
				} else {
					list.setNextFocusLeftId(R.id.ime_grid);
				}
			}
			updateSearchTipText(mode);
		}
	};

	protected String getLogTag() {
		return TAG;
	}

	// item点击事件
	private OnItemClickListener program_onItemClickListener = new TvOnItemClickListener() {
		@Override
		public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
			ListSearchAdapter adapter = (ListSearchAdapter) parent.getAdapter();
			Program p = (Program) adapter.getItem(position);
			if (p.showType == 0 && p.cdn != null && !p.cdn.equals("")) {
				PlayerInvoke invoke = new PlayerInvoke(getContext(), p);
				invoke.playForZixun(Global.getRecommendList(adapter.getProgramList(), position));
			} else {
				Intent i = new Intent(getContext(), YingshiDetailActivity.class);
				if (p.isJuheVideo) {
					i.putExtra("program_id", p.videoId);
					i.putExtra("classId", p.classId);
					i.putExtra("isJuheVideo", p.isJuheVideo);
				} else {
					i.putExtra("program_id", p.id);
//					i.putExtra("ppvPath", "搜索");//TODO
				}
				i.putExtra("name", p.name);
				getContext().startActivity(i);
			}
		}
	};
	/**
	 * 网络监听
	 */
	private INetworkListener mINetworkListener = new INetworkListener() {
		@Override
		public void onNetworkChanged(boolean isConnected, NetConnectionType lastType, NetConnectionType currentType) {
			// logd("onNetworkChanged,isConnected:" + isConnected +
			// ",retryOnNetWorkConnected:" + true);
			if (isConnected && NetConnectionType.none == lastType) {
				if (layoutSearchMenus != null) {
					for (int i = 0; i < layoutSearchMenus.getChildCount(); i++) {
						View v = layoutSearchMenus.getChildAt(i);
						if (currentShowType != null && currentShowType.equals((String) v.getTag())
								&& !(Boolean) v.getTag(R.id.is_requested)) {
							getSearchResult();
							break;
						}
					}
				}
			}
			if (!isConnected) {
				if (searchTask != null && !searchTask.isCancelled() && searchTask.getStatus() != AsyncTask.Status.FINISHED) {
					// logd("onNetworkChanged,cancelTask");
					searchTask.cancel(true);
					searchTask = null;
				}
			}
		}
	};

	/**
	 * 文本变化监听
	 */
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// Log.i(TAG,"textChange");
			cancelSearch();
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (etKey.hasFocus()) {
				keyType = null;
			}
			for (int i = 0; i < layoutSearchMenus.getChildCount(); i++) {
				View v = (TextView) layoutSearchMenus.getChildAt(i);
				v.setTag(R.id.is_requested, false);
			}
			getSearchResult();
		}
	};

	private void initMenus() {
		for (int i = 0; i < layoutSearchMenus.getChildCount(); i++) {
			TextView tv = (TextView) layoutSearchMenus.getChildAt(i);
			tv.setOnFocusChangeListener(mOnMenuFocusChange);
		}

	}

	// ListView初始化
	private void initListViews() {
		for (int i = 0; i < layoutSearchResult.getChildCount(); i++) {
			FocusedListView list = (FocusedListView) layoutSearchResult.getChildAt(i);
			list.setAdapter(new ListSearchAdapter());
			list.setOnItemClickListener(program_onItemClickListener);
			list.setOnItemSelectedListener(mOnListItemSelectedListener);
			list.setOnKeyDownListener(new OnListKeyDownListener(list));
			// ListView的焦点及阴影
			list.setItemScaleValue(1.05f, 1.05f);
			list.setFocusResId(R.drawable.tui_bg_focus);
			list.setFocusShadowResId(R.drawable.tui_grid_focus);
			list.setFrameRate(Config.FRAME_LISTVIEW);
			list.setFocusMode(2);
			list.setFocusPositionMode(FocusedListView.FOCUS_ITEM_REMEMBER_LAST);
		}
	}

	private void setRightMenuSelected(String type) {
		if (currentShowType == null || !currentShowType.equals(type)) {
			currentShowType = type;
			int textSize2 = getContext().getResources().getDimensionPixelSize(R.dimen.tui_text_size_2);
			int textSize3 = getContext().getResources().getDimensionPixelSize(R.dimen.tui_text_size_3);
			for (int i = 0; i < layoutSearchMenus.getChildCount(); i++) {
				TextView tv = (TextView) layoutSearchMenus.getChildAt(i);
				if (type.equals((String) tv.getTag())) {
					tv.setSelected(true);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize3);
				} else {
					tv.setSelected(false);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
				}
			}
		}
	}

	private void showResult(ArrayList<Program> programs) {
		for (int i = 0; i < layoutSearchResult.getChildCount(); i++) {
			FocusedListView list = (FocusedListView) layoutSearchResult.getChildAt(i);
			if (currentShowType.equals((String) list.getTag())) {
				list.setVisibility(View.VISIBLE);
				list.setSelectionFromTop(0, 0);
				ListSearchAdapter adapter = (ListSearchAdapter) list.getAdapter();
				if (programs != null) {
					adapter.setList(programs);
				} else {
					adapter.programs.clear();
				}
				list.setSelection(0);
				adapter.notifyDataSetChanged();

				setRightMenuSelected((String) list.getTag());
				// etKey.setNextFocusRightId(list.getId());
			} else {
				list.setVisibility(View.GONE);
			}
		}

		for (int i = 0; i < layoutSearchMenus.getChildCount(); i++) {
			View v = layoutSearchMenus.getChildAt(i);
			if (currentShowType.equals((String) v.getTag())) {
				v.setTag(R.id.is_requested, true);
				break;
			}
		}
	}

	private OnFocusChangeListener mOnMenuFocusChange = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				setRightMenuSelected((String) v.getTag());
				((TextView) v).setShadowLayer(50.0f, 0.0f, 0.0f, getResources().getColor(R.color.tui_color_search_shadow));
				// Log.i("ccdd","isRequest:" + v.getTag(R.id.is_requested));
				if (v.getTag(R.id.is_requested) == null || !(Boolean) v.getTag(R.id.is_requested)) {
					getSearchResult();
				} else {
					for (int i = 0; i < layoutSearchResult.getChildCount(); i++) {
						FocusedListView list = (FocusedListView) layoutSearchResult.getChildAt(i);
						if (currentShowType.equals((String) list.getTag())) {
							if (list.getCount() != 0) {
								showRight(R.id.layout_search_content);
								list.setVisibility(View.VISIBLE);
							} else {
								showRight(R.id.layout_search_nodata);
							}
						} else {
							list.setVisibility(View.GONE);
						}
					}
				}
			} else {
				((TextView) v).setShadowLayer(0, 0, 0, 0);
			}
			layoutSearchMenus.invalidate();
		}
	};

	private OnKeyListener mOnImeKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && layoutNodata.isShown()) {
				if (v instanceof GridView) {
					GridView gv = (GridView) v;
					if ((gv.getSelectedItemPosition() + 1) % gv.getNumColumns() == 0) {
						focusRightSelectedMenu();
						return true;
					}
				} else if (v instanceof Button) {
					if (v.getId() == R.id.btn_ime_bihua) {
						focusRightSelectedMenu();
						return true;
					}
				}
			}
			return false;
		}
	};

	private ImeBoard.OnKeyWordDetermineListener mImeKeyWordListener = new OnKeyWordDetermineListener() {

		@Override
		public void onDelete() {
			// String kType = String.valueOf(mImeBoard.getImeMode());
			// if(keyType == null || !keyType.equals(kType)){
			// etKey.setText("");
			// }
			// keyType = kType;
			String strText = etKey.getText().toString();
			if (strText.length() > 0) {
				if (strText.length() >= 1) {
					strText = strText.substring(0, strText.length() - 1);
				}
				etKey.setText(strText);
				Spannable text = etKey.getText();
				Selection.setSelection(text, text.length());
			}
		}

		@Override
		public void onClicked(String word) {
			String kType = String.valueOf(mImeBoard.getImeMode());
			if (keyType == null || !keyType.equals(kType)) {
				etKey.setText("");
			}
			keyType = kType;
			etKey.setText(etKey.getText().toString() + word);
			Spannable text = etKey.getText();
			Selection.setSelection(text, text.length());
		}

		@Override
		public void onClear() {
			keyType = String.valueOf(mImeBoard.getImeMode());
			etKey.setText("");
		}
	};

	/**
	 * 热词点击事件
	 */
	private View.OnClickListener hotWordsClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			TextView tv = (TextView) v;
			String text = tv.getText().toString();
			keyType = null;
			etKey.setText(text);
			Spannable spanable = etKey.getText();
			Selection.setSelection(spanable, spanable.length());
		}
	};

	/**
	 * ListView选中监听
	 */
	private FocusItemSelectedListener mOnListItemSelectedListener = new FocusItemSelectedListener() {

		@Override
		public void onItemSelected(View v, int position, boolean isSelected, View view) {
			AdapterView adapterView = (AdapterView) view;
			if (v == null || adapterView.getChildCount() == 0) {
				return;
			}
			TextView tv = (TextView) v.findViewById(R.id.search_item_title);
			TextView tv2 = (TextView) v.findViewById(R.id.search_item_info);
			if (tv != null) {
				tv.setSelected(isSelected);
				tv2.setSelected(isSelected);
				if (isSelected) {
//					tv.startMarquee();
//					tv2.startMarquee();
					tv.setEllipsize(TruncateAt.MARQUEE);
					tv2.setEllipsize(TruncateAt.MARQUEE);
				} else {
//					tv.stopMarquee();
//					tv2.stopMarquee();
					tv.setEllipsize(TruncateAt.END);
					tv2.setEllipsize(TruncateAt.END);
				}
			}
		}
	};

	private class OnListKeyDownListener implements onKeyDownListener {
		private FocusedListView mList = null;

		public OnListKeyDownListener(FocusedListView list) {
			mList = list;
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (mList != null && mList.getSelectedItemPosition() == mList.getCount() - 1 && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return true;
			}
			return false;
		}

	}

	public void showLoading() {
		layoutLoading.setVisibility(View.VISIBLE);
	}

	public void hideLoading() {
		layoutLoading.setVisibility(View.GONE);
	}

	/**
	 * 显示右边的内容
	 * 
	 * @param id
	 */
	private void showRight(int id) {
		// ViewGroup[] groups = {layoutExample,layoutNodata,layoutContent};
		if (id == layoutExample.getId()) {
			layoutExample.setVisibility(View.VISIBLE);
			layoutContent.setVisibility(View.GONE);
			layoutSearchResult.setVisibility(View.GONE);
			etKey.setNextFocusRightId(R.id.layout_rowwrap);
			mImeBoard.setNextFocusRightId(R.id.layout_rowwrap);
		} else if (id == layoutNodata.getId()) {
			layoutExample.setVisibility(View.GONE);
			layoutContent.setVisibility(View.VISIBLE);
			layoutNodata.setVisibility(View.VISIBLE);
			layoutSearchResult.setVisibility(View.GONE);
			etKey.setNextFocusRightId(View.NO_ID);
			mImeBoard.setNextFocusRightId(View.NO_ID);
		} else if (id == layoutContent.getId()) {
			layoutExample.setVisibility(View.GONE);
			layoutNodata.setVisibility(View.GONE);
			layoutContent.setVisibility(View.VISIBLE);
			layoutSearchResult.setVisibility(View.VISIBLE);
			etKey.setNextFocusRightId(R.id.layout_search_grid);
			mImeBoard.setNextFocusRightId(R.id.layout_search_grid);
		}
	}

	// 取消上一次的搜索
	private void cancelSearch() {
		if (searchTask != null) {
			searchTask.cancel(true);
		}
		hideLoading();
	}

	private String replaceBiHua(String str) {
		if (mImeBoard.getImeMode() == ImeBoard.IME_MODE_BIHUA) {
			return str.replace('一', '1').replace('丨', '2').replace('丿', '3').replace('丶', '4').replace('乛', '5');
		}
		return str;
	}

	// 获取搜索结果
	private void getSearchResult() {
		String key = etKey.getText().toString();
		if (null == key || "".equals(key.trim())) {
			showRight(R.id.layout_search_example);
			return;
		}
		searchTask = new WorkAsyncTask<ArrayList<Program>>(getActivity()) {
			@Override
			public void onPre() throws Exception {
				showLoading();
			}

			@Override
			public ArrayList<Program> doProgress() throws Exception {
				ArrayList<Program> programs = null;
				String key = replaceBiHua(etKey.getText().toString());
				programs = SourceWasu.getSearch(key, null, currentShowType, keyType);
				return programs;
			}

			@Override
			public void onPost(boolean resultState, ArrayList<Program> programs) throws Exception {
				hideLoading();
				searchTask = null;
				if (isCancelled()) {
					return;
				}

				if (resultState) {
					showResult(programs);
					if (programs == null || programs.size() == 0) {
						showRight(R.id.layout_search_nodata);
					} else {
						// Log.i(TAG,"programs:" + programs.size());
						showRight(R.id.layout_search_content);
					}
				}
			}

			@Override
			public void onError(Exception e) {
				Log.i(TAG, "httpError:" + e);
				Toast.makeText(getContext(), R.string.fail_get_server_data, Toast.LENGTH_SHORT).show();
			}
		};
		runTask(searchTask);
	}

	// 获取热词并显示
	private void getHotWords() {
		// Log.i(TAG,"getHotWords");
		WorkAsyncTask<ArrayList<HotWord>> hotWordsTask = new WorkAsyncTask<ArrayList<HotWord>>(getActivity()) {
			@Override
			public void onPre() throws Exception {
				layoutHotWrap.setVisibility(View.VISIBLE);
			}

			@Override
			public ArrayList<HotWord> doProgress() throws Exception {
				return SourceWasu.getHotWordList();
			}

			@Override
			public void onPost(boolean resultState, ArrayList<HotWord> words) throws Exception {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// Log.i(TAG,"hotWords:" + words.size());
				if (words == null || words.size() == 0) {
					layoutHotWrap.setVisibility(View.GONE);
				} else {
					for (int i = 0; i < words.size(); i++) {
						TextView tv = (TextView) inflater.inflate(R.layout.search_hot_item, null);
						tv.setText(words.get(i).name);
						// Log.i(TAG,"hotWords " + i + ":" + words.get(i).name +
						// "," + words.get(i).id);
						tv.setOnClickListener(hotWordsClick);
						tv.setVisibility(View.VISIBLE);
						rowWrapLayout.addView(tv);
					}
				}
			}
		};
		runTask(hotWordsTask);
	}

	@Override
	public void onPause() {
		// Log.i("abcde","savestatus save imeMode:" + mImeBoard.getImeMode());
		share.edit().putInt(SAVED_IME_MODE_KEY, mImeBoard.getImeMode()).commit();
		super.onPause();
	}

	class ListSearchAdapter extends BaseAdapter {
		private List<Program> programs;
		private LayoutInflater inflater = null;

		public ListSearchAdapter() {
			programs = new ArrayList<Program>();
			inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int size = MobileInfo.dip2px(getContext(), 80);
			loadParam = new ImageLoadParam("", size, size);
			loadParam.setImageScaleType(ImageScaleType.EXACTLY_STRETCHED);
			loadParam.setWorkCache(true);
			loadParam.setImageArg(ImageArg.thumbnail);
		}

		public List<Program> getProgramList() {
			return programs;
		}

		public void setList(List<Program> programs) {
			this.programs = programs;
		}

		@Override
		public int getCount() {
			return programs == null ? 0 : programs.size();
		}

		@Override
		public Object getItem(int position) {
			return programs == null ? null : programs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_search_result_list, null);
			}

			Program p = (Program) this.getItem(position);
			MaskImageView img = (MaskImageView) convertView.findViewById(R.id.search_item_image);
			TextView tvTitle = (TextView) convertView.findViewById(R.id.search_item_title);
			TextView tvInfo = (TextView) convertView.findViewById(R.id.search_item_info);

			// img.setImageURI(Uri.parse(p.picUrl));
			loadParam.imageUrl = (p.picUrl != null && !p.picUrl.equals("")) ? p.picUrl : p.nodePic;
			tvTitle.setText(p.name);
			if ((p.actor == null || p.actor.equals("")) && (p.nodeName == null || p.nodeName.equals(""))) {
				img.setImageSize(ImageSize.search_h);
				tvInfo.setVisibility(View.GONE);
			} else {
				tvInfo.setVisibility(View.VISIBLE);
				if (String.valueOf(p.showType).equals(SHOWTYPE_DIANSHIJU) || String.valueOf(p.showType).equals(SHOWTYPE_DIANYING)) {
					tvInfo.setText(getContext().getResources().getString(R.string.search_actor) + (p.actor != null ? p.actor : ""));
					img.setImageSize(ImageSize.search_v);
				} else {
					tvInfo.setText(getContext().getResources().getString(R.string.search_catalog) + (p.nodeName != null ? p.nodeName : ""));
					img.setImageSize(ImageSize.search_h);
				}
			}
			img.setScaleType(ScaleType.FIT_CENTER);
			Global.getSearchListImageLoader().loadImage(loadParam, img);

			return convertView;
		}

	}

}
