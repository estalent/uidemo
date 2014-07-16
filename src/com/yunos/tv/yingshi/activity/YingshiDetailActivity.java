package com.yunos.tv.yingshi.activity;

import java.util.ArrayList;
import java.util.List;

import net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback;
import net.zhilink.wasualiplayer.services.IWasuAliPlayerService;

import org.json.JSONException;
import org.json.JSONObject;

import yunos.tv.app.LeftNavBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.ams.tyid.TYIDException;
import com.aliyun.ams.tyid.TYIDManager;
import com.aliyun.base.WorkAsyncTask;
import com.aliyun.base.info.MobileInfo;
import com.aliyun.base.net.NetConnectionType;
import com.aliyun.base.net.NetworkManager;
import com.aliyun.base.net.NetworkManager.INetworkListener;
import com.aliyun.imageload.ImageLoadParam;
import com.aliyun.imageload.OnImageLoadListener;
import com.aliyun.imageload.entity.ImageLoadType;
import com.aliyun.imageload.utils.L;
import com.wasu.android.rainbowbox.aidl.WasuUserCallback;
import com.wasu.android.rainbowbox.aidl.WasuUserInterface;
import com.yunos.tv.app.widget.FocusedBasePositionManager;
import com.yunos.tv.app.widget.FocusedBasePositionManager.FocusItemSelectedListener;
import com.yunos.tv.app.widget.FocusedHorizontalListView;
import com.yunos.tv.app.widget.FocusedRelativeLayout;
import com.yunos.tv.app.widget.TvOnDialogClickListener;
import com.yunos.tv.app.widget.TvOnItemClickListener;
import com.yunos.tv.app.widget.TvOnViewClickListener;
import com.yunos.tv.player.WasuPlayInfo;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.AdInfo;
import com.yunos.tv.source.entity.JuheSource;
import com.yunos.tv.source.entity.JuheSourceUrl;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.Source;
import com.yunos.tv.source.entity.Tag;
import com.yunos.tv.yingshi.BaseTvActivity;
import com.yunos.tv.yingshi.Config;
import com.yunos.tv.yingshi.Const;
import com.yunos.tv.yingshi.Global;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.adapter.DetailGridAdapter;
import com.yunos.tv.yingshi.dao.sql.SqlFavorDao;
import com.yunos.tv.yingshi.dao.sql.SqlLastPlayDao;
import com.yunos.tv.yingshi.exception.SourceException;
import com.yunos.tv.yingshi.utils.SystemProUtils;
import com.yunos.tv.yingshi.widget.DetailButton;
import com.yunos.tv.yingshi.widget.JuheSourceDialog;
import com.yunos.tv.yingshi.widget.SizeColorSpan;
import com.yunos.tv.yingshi.widget.XuanjiDialog;
import com.yunos.tv.yingshi.widget.YingshiImageView;

/**
 * 1. 华数视频详情页，只传programId 聚合视频详情页，需要传videoId, classId, isJuheVideo 
 * 2.
 * 3. 必须ppvPath
 */
public class YingshiDetailActivity extends BaseTvActivity {
	public static final String TAG = "YingshiDetailActivity";

	public static final String SCHEME_WASU = "wasu";
	public static final String SCHEME_JUHE = "juhe";

	private static final int DIALOG_ID_YINGSHI_NOT_EXIST = 101;

	private LeftNavBar mLeftNavBar;
	private int mLastplayTagIndex = 0; // 是为了页面刚进来的时候，焦点落在哪个tag按钮上。

	private YingshiImageView mYingshiImageView;
	private ImageView mAdImageView;
	private TextView mTvTitle;
	private TextView mTvDirectorActor;
	private TextView mTvJuqing;
	private FocusedHorizontalListView mGridMaybeLove;
	private DetailGridAdapter mDetailGridAdapter;
	private FocusedRelativeLayout mLinearBtnWrap;
	private TextView mTextGaoqing;
	private CheckBox mBtnFavor;
	private LayoutInflater mInflater;
	private DetailButton mMoreButton;
	private Program mLastPlayProgram;

	private String mProgramId;
	private Program mProgram;

	private BroadcastReceiver mBroadcastReceiver;

	private AlertDialog mLastplayDialog;
	private Dialog mXuanjiDialog;
	private Dialog mJuheSourceDialog;
	private String ppvPath = "华数阿里TV"; //少儿#儿童影院, 外面传进来的
	
	private String mFrom = "";

	// 聚合相关
	private boolean mIsJuheVideo = false; // 是否是聚合视频，由搜索那边放在intent中传过来
	private String mClassId;

	//--------------------
	private static final String ACTION_WASU_PLAY = "com.wasuali.action.player";
	private Program mPlayProgram;
	private int mThisTagIndex; //不同码率的index
	private int mThisSourceIndex; //当前用户点击的相应source下标，不同的集index

	private String mLastSourceFileName;
	private long mLastPlayTime = 0;

	private String source;
	private String high;
	private String playJson;
	//------------------
	private AlertDialog mLoginAlert;//登录提示框

	// 询价机制--------------
	private Object wasualiplayerBindLock = new Object();
	private Object wasuuserOperationBindLock = new Object();
	private BroadcastReceiver mLoginReceiver; //登录的监听器
	
	// 需要在setContentView之后初始化LeftNavBar，LeftNavBar.TYPE_TOP_NAVBAR type表示打开顶部导航栏
	private LeftNavBar getLeftNavBar() {
		if (mLeftNavBar == null) {
			mLeftNavBar = new LeftNavBar(this, LeftNavBar.TYPE_TOP_NAVBAR);
			mLeftNavBar.setOnClickHomeListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		return mLeftNavBar;
	}

	private void setupTabs() {
		LeftNavBar bar = getLeftNavBar();
		bar.removeAllTabs();
		bar.setLogo(R.drawable.tui_ic_huashulogo);
		bar.setBackgroundDrawable(null);
	}

	@Override
	protected String getLogTag() {
		return TAG;
	}
		
	private IWasuAliPlayerService mWasuAliPlayerService;//播放器service
	private ServiceConnection mWasuAliPlayerServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("yyy", "=====WasuAliPlayerService service Disconnected=====");
			mWasuAliPlayerService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("yyy", "=====WasuAliPlayerService service Connected=====");
			mWasuAliPlayerService = IWasuAliPlayerService.Stub.asInterface(service);
			synchronized (wasualiplayerBindLock) {
				wasualiplayerBindLock.notifyAll();
			}
		}
	};

	private INetworkListener mINetworkListener = new INetworkListener() {
		@Override
		public void onNetworkChanged(boolean isConnected, NetConnectionType lastType, NetConnectionType currentType) {
			if (isConnected) {
				if (mDetailGridAdapter.programList.isEmpty() && !(TextUtils.isEmpty(mProgramId))) {
					logd("onNetworkChanged -- getProgramInfo,id=" + mProgramId);
					getProgramInfo();
					getAdvertisement();
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yingshi_detail);
		setupTabs();

		mInflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		mLinearBtnWrap = (FocusedRelativeLayout) findViewById(R.id.linear_btnWrap);
		getLeftNavBar().getHomeDisplay().setNextFocusDownId(mLinearBtnWrap.getId());
		getLeftNavBar().getHomeDisplay().setNextFocusForwardId(mLinearBtnWrap.getId());
		mLinearBtnWrap.setScale(false);
		mLinearBtnWrap.setFocusResId(R.drawable.tui_bg_focus);
		mLinearBtnWrap.setFocusShadowResId(R.drawable.tui_bg_focus);
		mLinearBtnWrap.setFrameRate(Config.FRAME_GRIDVIEW);
		mLinearBtnWrap.setHorizontalMode(FocusedRelativeLayout.HORIZONTAL_SINGEL);
		mLinearBtnWrap.setScaleMode(FocusedBasePositionManager.SCALED_FIXED_COEF);
		int p = 9;
		mLinearBtnWrap.setManualPadding(p, p, -p, -p);

		mAdImageView = (ImageView) findViewById(R.id.image_ad);
		mYingshiImageView = (YingshiImageView) findViewById(R.id.image_movie);
		mTvTitle = (TextView) findViewById(R.id.text_title);
		mTvDirectorActor = (TextView) findViewById(R.id.text_director_actor);
		mTvJuqing = (TextView) findViewById(R.id.text_juqing);
		mTextGaoqing = (TextView) findViewById(R.id.text_gaoqing);

		float mScaleValue = getResources().getDimension(R.dimen.grid_small_w) / getResources().getDimension(R.dimen.image_small_w);
		mGridMaybeLove = (FocusedHorizontalListView) findViewById(R.id.list_maybe_love);
		mGridMaybeLove.setFrameRate(Config.FRAME_LISTVIEW_DETAIL);
		mGridMaybeLove.setScrollDuration(Config.SCROLLING_DURATION_LISTVIEW_DETAIL);
		mGridMaybeLove.setFocusMode(FocusedBasePositionManager.FOCUS_SYNC_DRAW);
		mGridMaybeLove.setItemScaleValue(mScaleValue, mScaleValue);
		mGridMaybeLove.setFocusResId(R.drawable.tui_bg_focus);
		mGridMaybeLove.setFocusShadowResId(R.drawable.tui_grid_focus);
		mGridMaybeLove.setFocusViewId(R.id.image_maybelove);
		mGridMaybeLove.setSelected(true);
		mGridMaybeLove.setFocusMode(0);
		mGridMaybeLove.setOnItemSelectedListener(new FocusItemSelectedListener() {
			@Override
			public void onItemSelected(View v, int position, boolean isSelected, View view) {
				if (isSelected) {
					TextView t = (TextView) v.findViewById(R.id.text_maybelove);
					t.setEllipsize(TruncateAt.MARQUEE);
					t.setTextColor(Color.parseColor("#ffffff"));
				} else {
					TextView t = (TextView) v.findViewById(R.id.text_maybelove);
					t.setTextColor(Color.parseColor("#999999"));
					t.setEllipsize(TruncateAt.END);
				}
			}
		});
		mGridMaybeLove.setOnItemClickListener(new TvOnItemClickListener() {
			@Override
			public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
				Program p = mDetailGridAdapter.getItem(position);
				Intent it = new Intent(YingshiDetailActivity.this, YingshiDetailActivity.class);
				if (p.isJuheVideo) {
					it.putExtra("isJuheVideo", true);
					it.putExtra("program_id", p.videoId);
					it.putExtra("classId", p.classId);
				} else {
					it.putExtra("program_id", p.id);
					it.putExtra("name", p.name);
//					it.putExtra("ppvPath", "猜你喜欢");
				}
				startActivity(it);
			}
		});
		mDetailGridAdapter = new DetailGridAdapter(this, mProgramId);
		mGridMaybeLove.setAdapter(mDetailGridAdapter);

		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Const.ACTION_UPDATE_TITLE)) {
					updateTitle();
				}
			}
		};
		registerReceiver(mBroadcastReceiver, new IntentFilter(Const.ACTION_UPDATE_TITLE));

		//注册监听器
		IntentFilter filter = new IntentFilter("com.aliyun.xiaoyunmi.action.AYUN_LOGIN_BROADCAST");
		mLoginReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if ("com.aliyun.xiaoyunmi.action.AYUN_LOGIN_BROADCAST".equals(intent.getAction())) {
					Bundle bundle = intent.getExtras();
					if (bundle.containsKey("aliyun_account_login_success")) {
						Boolean value = (Boolean) bundle.get("aliyun_account_login_success");
						Log.d("yyy", "=====YingshiDetailActivity===login_success==Receiver=====value:" + value.toString());
						if (value.toString() == "true") { //登陆成功,检查值为true
							getUserInfo();
						}
					}
				}
			}
		};
		registerReceiver(mLoginReceiver, filter);
		
		NetworkManager.instance().registerStateChangedListener(mINetworkListener);
		requestData();

		// 询价机制--------------
		Intent intent = new Intent("net.zhilink.wasualiplayer.services.WasuAliPlayerService");
		bindService(intent, mWasuAliPlayerServiceConn, BIND_AUTO_CREATE);
		// 是否为华数会员---------
		Intent serviceIntent = new Intent("wasu.intent.action.useroperation");
		bindService(serviceIntent, mWasuUserConnection, Service.BIND_AUTO_CREATE);
	}

	/**
	 * 绑定WasuAliPlayerService服务
	 */
	public void bindWasuAliPlayerService(){
		if (mWasuAliPlayerService == null) {
			Log.d("yyy", "=====bindWasuAliPlayerService()=========");
			synchronized (wasualiplayerBindLock) {
				Intent serviceIntent = new Intent("net.zhilink.wasualiplayer.services.WasuAliPlayerService");
				bindService(serviceIntent, mWasuAliPlayerServiceConn, Context.BIND_AUTO_CREATE);
				try {
					wasualiplayerBindLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 绑定useroperation服务
	 */
	public void bindUserOperation() {
		if(mWasuUserInterface == null){
			Log.d("yyy", "=====bindUserOperation()=========");
			synchronized (wasuuserOperationBindLock) {
				Intent serviceIntent = new Intent("wasu.intent.action.useroperation");
				bindService(serviceIntent, mWasuUserConnection, Service.BIND_AUTO_CREATE);
				try {
					wasuuserOperationBindLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 改为只接收program_id参数，不接收program对象
	 */
	private void requestData() {
		Intent intent = getIntent();
		mFrom = intent.getStringExtra("from");
		// 通过scheme调用
		Uri uri = intent.getData();
		if (uri != null) {
			List<String> path = uri.getPathSegments();
			String type = SCHEME_WASU;
			if (path != null && path.size() > 0) {
				type = path.get(0);
			} else {
				showToast(R.string.toast_uri_error);
				finish();
				return;
			}
			if (SCHEME_JUHE.equals(type)) {
				mIsJuheVideo = true;
				mProgramId = uri.getQueryParameter("videoId")/* "21057"; */;
				mClassId = uri.getQueryParameter("classId")/* "T"; */;
				logi("uri==juhe===videoid:" + mProgramId + ",classid:" + mClassId);
			} else if (SCHEME_WASU.equals(type)) {
				mIsJuheVideo = false;
//				ppvPath = uri.getQueryParameter("ppvPath"); 
				mProgramId = uri.getQueryParameter("programId");
				logi("uri==wasu===programid" + mProgramId);
			} else {
				showToast(R.string.toast_uri_error);
				finish();
				return;
			}
		} else {
			mProgramId = intent.getStringExtra("program_id");
			String ppv_path = intent.getStringExtra("ppvPath");
			if (ppv_path != null) {
				ppvPath = ppv_path;
			}
			mIsJuheVideo = intent.getBooleanExtra("isJuheVideo", false);
			mClassId =intent.getStringExtra("classId") /* "T"; */;
			logi("uri null, requestData=======videoId==" + mProgramId + ",classId:" + mClassId + ",juhe:" + mIsJuheVideo 
					+ ",mProgramId:" + mProgramId);
		}
		mDetailGridAdapter.setIsJuheVideo(mIsJuheVideo);

		if (TextUtils.isEmpty(mProgramId)) {
			showToast(R.string.fail_get_yingshi_detail);
			finish();
			return;
		}
		getProgramInfo();
		getAdvertisement();
	}

	public void runFavouriteAsyncTask() {
		runTask(new WorkAsyncTask<Boolean>(this) {
			@Override
			public Boolean doProgress() {
				try {
					SourceWasu.addUserFavourite(SystemProUtils.getUUID(), String.valueOf(mProgram.id));
				} catch (SourceException e) {
					logd("add user favourite send fail");
					return false;
				}
				logd("add user favourite send success!");
				return true;
			}
		});
	}

	//询价部分
	private void queryPrice() {
		if (mIsJuheVideo) {
			return;
		}
		for (int i = 0, len1 = mProgram.tags.size(); i < len1; i++) {
			Tag tag = mProgram.tags.get(i);
			if (tag.source != null && tag.source.size() > 0) {
				Source source = tag.source.get(0);
				source.price = -0.5f;//正在询价的标识
				getPriceInfo(source.ppvId, i);
			}
		}
	}

	private void getPriceInfo(final String ppVid, final int index) {
		runTask(new WorkAsyncTask<Object>(this) {
			@Override
			public Program doProgress() throws Exception {
				Log.i("yyy", "getPriceInfo()");
				bindWasuAliPlayerService();
				if (mWasuAliPlayerService == null) {
					return null;
				}
				try {
					Log.i("yyy", "mWasuAliPlayerService.queryPrice(), "+"{'ppvId':'" + ppVid + "'," + "'index':'" + index + "'}");
					mWasuAliPlayerService.queryPrice("{'ppvId':'" + ppVid + "'," + "'index':'" + index + "'}", mQueryPriceCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				return null;
			}
		});
	}

	private void getProgramInfo() {
		runTask(new WorkAsyncTask<Program>(this) {
			@Override
			public void onPre() throws Exception {
				showLoading();
			}

			@Override
			public Program doProgress() throws Exception {
				if (TextUtils.isEmpty(mProgramId)) {
					SourceException e = new SourceException();
					e.setErrorMessage("program id is null!");
					throw e;
				}
				Program p = null;
				if (mIsJuheVideo) {
					p = SourceWasu.getProgramJuhe(mProgramId, mClassId);
					p.id = mProgramId; // 默认就是相等的
					p.classId = mClassId; // 默认就有的
					p.isJuheVideo = true;
				} else {
					p = SourceWasu.getProgramV2(mProgramId);
					p.isJuheVideo = false;
				}
				p.ppvPath = ppvPath;
				
				return p;
			}

			@Override
			public void onPost(boolean resultState, Program program) throws Exception {
				hideLoading();
				if (resultState) {
					mProgram = program;
					setProgramInfo();

					updateTitle(); // 取得上次信息，下面初始化按钮后，就将焦点落在那个上面。
					initButtons();
					if (mProgram.recommend != null) {//猜你喜欢
						mDetailGridAdapter.programList = program.recommend;
						mDetailGridAdapter.notifyDataSetChanged();
						mGridMaybeLove.setVisibility(View.VISIBLE);
					} else {
						mDetailGridAdapter.programList = new ArrayList<Program>();
						mDetailGridAdapter.notifyDataSetChanged();
						mGridMaybeLove.setVisibility(View.GONE);
					}
				} else { // by: zhangle bug:341112 因为影视不存在导致的异常
					showDialog(DIALOG_ID_YINGSHI_NOT_EXIST);
				}
			}

			@Override
			public void onError(Exception e) {
				loge("onError -- 0");
				if (e instanceof SourceException) {
					loge("onError -- 1");
					SourceException ex = (SourceException) e;
					loge("onError -- 2 -- " + ex.getErrorCode().getCode());
					switch (ex.getErrorCode()) {
					case SERVERERROR_SYS:
						// 影视不存在
						showDialog(DIALOG_ID_YINGSHI_NOT_EXIST);
						break;
					case SERVERERROR_FAIL:
						break;
					}
				}
			}
		});
	}

	// 显示视频详情
	private void setProgramInfo() {
		ImageLoadParam loadParam = new ImageLoadParam("");
		if (!mIsJuheVideo) {
			int index = mProgram.picUrl.lastIndexOf(".");
			if (index > 0) {
				String suffix = mProgram.picUrl.substring(index);
				loadParam.imageUrl = mProgram.picUrl + Const.IMAGE_SIZE_DETAIL_BIG + suffix;
			} else {
				loadParam.imageUrl = mProgram.picUrl;
			}
		} else {
			loadParam.imageUrl = mProgram.picUrl;
		}
		Global.getMaskImageLoader().loadImage(loadParam, mYingshiImageView);
		switch (mProgram.rateType) {
		case 0:
			mTextGaoqing.setVisibility(View.GONE);
			break;
		case 1:
			mTextGaoqing.setVisibility(View.VISIBLE);
			mTextGaoqing.setText(R.string.gaoqing);
			break;
		case 2:
			mTextGaoqing.setVisibility(View.VISIBLE);
			mTextGaoqing.setText(R.string.languang);
			break;
		default:
			mTextGaoqing.setVisibility(View.GONE);
			break;
		}

		mTvTitle.setText(mProgram.name);
		mTvDirectorActor.setText(String.format(getString(R.string.detail_director_actor),
				(TextUtils.isEmpty(mProgram.director) ? getString(R.string.unknown) : mProgram.director),
				(TextUtils.isEmpty(mProgram.actor) ? getString(R.string.unknown) : mProgram.actor)));
		mTvJuqing.setText(mProgram.description);
	}

	/**
	 * 设置标题，如"标题30集(上次播放到xx集x分x秒)"
	 * 
	 */
	private void updateTitle() {
		if (mProgram == null) {
			loge("error: YingshiDetailActivity updateTitle():program == null");
			return;
		}
		if (mIsJuheVideo) { // 没有续播
			return;
		}
		StringBuilder titleSB = new StringBuilder(mProgram.name);
		String textJiTotal = null, textLastplay = null;
		Resources res = getResources();
		SizeColorSpan span1 = new SizeColorSpan(MobileInfo.sp2px(this, res.getDimension(R.dimen.tui_text_size_3)));
		span1.setColor(res.getColor(R.color.tui_text_color_white));
		SizeColorSpan span2 = null;

		if (mIsJuheVideo) {
			
		} else {
			mLastPlayProgram = SqlLastPlayDao.getLastPlayProgram(mProgram.id);
			if (mLastPlayProgram != null) {
				if (mLastPlayProgram.tags != null && mLastPlayProgram.tags.size() > 0) {
					for (int i = 0, len = mLastPlayProgram.tags.size(); i < len; ++i) {
						if (mLastPlayProgram.lastPlayTagName.equals(mLastPlayProgram.tags.get(i).tag)) {
							mLastplayTagIndex = i;
							break;
						}
					}
				}
			}
		}
		if (mProgram.showType == 3) { //电视剧
			String totalJi = "";
			// 找出几个tag中最多的集数来显示总集数
			if (mIsJuheVideo) {
				totalJi = "";
			} else {
				for (Tag tag : mProgram.tags) {
					String ji = tag.source.get(tag.source.size() - 1).fileName;
					if (!TextUtils.isDigitsOnly(ji)) {
						totalJi = "";
						break;
					}
					if (ji.compareTo(totalJi) > 0) {
						totalJi = ji;
					}
				}
			}
			if (!TextUtils.isEmpty(totalJi)) {
				textJiTotal = totalJi + getString(R.string.ji);
				titleSB.append(textJiTotal);
			}

			// 如果播放器正常播放结束退出，则lastPlayTime为0
			if (mLastPlayProgram != null && mLastPlayProgram.lastPlayTime > 0) {
				textLastplay = "(" + getString(R.string.lastplay_title) + mLastPlayProgram.lastPlayFileName + getString(R.string.ji)
						+ getDuration(mLastPlayProgram.lastPlayTime / 1000) + ")";
				titleSB.append(textLastplay);
			}
		} else /* if (program.showType == 1) */{// 电影
			// 如果播放器正常播放结束退出，则lastPlayTime为0
			if (mLastPlayProgram != null && mLastPlayProgram.lastPlayTime > 0) {
				textLastplay = "(" + getString(R.string.lastplay_title) + getDuration(mLastPlayProgram.lastPlayTime / 1000) + ")";
				titleSB.append(textLastplay);
			}
		}
		SpannableString spannableString = new SpannableString(titleSB.toString());
		int titleLen = mProgram.name.length();
		spannableString.setSpan(span1, 0, titleLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (!TextUtils.isEmpty(textJiTotal) || !TextUtils.isEmpty(textLastplay)) {
			span2 = new SizeColorSpan(MobileInfo.sp2px(this, res.getDimension(R.dimen.tui_text_size_1)));
			span2.setColor(res.getColor(R.color.tui_text_color_grey));
			spannableString.setSpan(span2, titleLen, titleSB.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		mTvTitle.setText(spannableString);
	}

	private View.OnFocusChangeListener mButtonFocusLisnter = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				v.clearFocus();
				mLinearBtnWrap.requestFocus();
			}
		}
	};

	private void initButtons() {
		mLinearBtnWrap.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		if (mIsJuheVideo) {  //聚合类
			DetailButton button = null;
			int size = mProgram.player.size();
			int ID_BASE_2000 = 2000;
			for (int i = 0; i < size; i++) {
				if (i == 3) {
					break;
				}
				JuheSource source = mProgram.player.get(i);
				button = (DetailButton) mInflater.inflate(R.layout.detail_button2, null);
				button.setOnFocusChangeListener(mButtonFocusLisnter);
				button.setId(ID_BASE_2000 + i);
				button.setTag(i);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.leftMargin = 10;
				if (i > 0) {
					lp.addRule(RelativeLayout.RIGHT_OF, ID_BASE_2000 + i - 1);
				}
				// button.setFocusable(false);
				button.setOnTouchListener(mOnTouchListener);
				button.setOnClickListener(mJuheBtnPlayListener);
				ImageView icon = (ImageView) button.findViewById(R.id.detail_button_icon1);
				if (Const.SOURCE_MAP.containsKey(source.key)) {
					int drawResId = Const.SOURCE_MAP.get(source.key);
					icon.setImageDrawable(getResources().getDrawable(drawResId));
				} else {
					ImageLoadParam p = new ImageLoadParam(source.sourcePic);
					Global.getImageLoader().loadImage(p, icon);
				}
				TextView t1 = (TextView) button.findViewById(R.id.detail_button_text1);
				t1.setText(source.key);
				mLinearBtnWrap.addView(button, lp);
			}
			if (size > 3) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.RIGHT_OF, ID_BASE_2000 + 2);
				lp.leftMargin = 10;
				mMoreButton = (DetailButton) mInflater.inflate(R.layout.detail_button2, null);
				StateListDrawable stateDraw = new StateListDrawable();
				stateDraw.addState(new int[] { android.R.attr.state_selected }, getResources().getDrawable(R.drawable.more_focus));
				stateDraw.addState(new int[] { -android.R.attr.state_selected }, getResources().getDrawable(R.drawable.more));
				ImageView icon = (ImageView) mMoreButton.findViewById(R.id.detail_button_icon1);
				icon.setBackgroundDrawable(stateDraw);
				TextView t1 = (TextView) mMoreButton.findViewById(R.id.detail_button_text1);
				t1.setText(R.string.more);
				mMoreButton.setOnFocusChangeListener(mButtonFocusLisnter);
				mMoreButton.setOnTouchListener(mOnTouchListener);
				mMoreButton.setOnClickListener(new TvOnViewClickListener() {

					@Override
					public void onClicked(View v) {
						if (mJuheSourceDialog != null && mJuheSourceDialog.isShowing()) {
							mJuheSourceDialog.dismiss();
						}
						// 找到对应lastPlayFileName的下标
						mJuheSourceDialog = new JuheSourceDialog(YingshiDetailActivity.this, mProgram.player, mLastplayTagIndex);
						mJuheSourceDialog.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								JuheSourceDialog jsd = (JuheSourceDialog) dialog;
								if (jsd.getSelected() != -1) {
									performJuheButtonClick(jsd.getSelected());
								}
							}
						});
						mJuheSourceDialog.show();
					}
				});
				mLinearBtnWrap.addView(mMoreButton, lp);
			}
			mLinearBtnWrap.setVisibility(View.VISIBLE);
			mLinearBtnWrap.requestFocus();
		} else {  //非聚合类
			DetailButton button = null;
			int ID_BASE_1000 = 1000;
			for (int i = 0, len = mProgram.tags.size(); i < len; i++) {
				if (i == 4) {// TODO 容许最多三个
					break;
				}
				button = (DetailButton) mInflater.inflate(R.layout.detail_button1, null);
				button.setOnFocusChangeListener(mButtonFocusLisnter);
				button.setId(ID_BASE_1000 + i);
				button.setTag(i);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.leftMargin = 2;
				if (i > 0) {
					lp.addRule(RelativeLayout.RIGHT_OF, ID_BASE_1000 + i - 1);
				}
				// if (i >= 0 && i <=1) {
				// button.setFocusable(true);
				// } else {
				// button.setFocusable(false);
				// }
				button.setOnTouchListener(mOnTouchListener);
				button.setOnClickListener(mBtnPlayListener);
				TextView t1 = (TextView) button.findViewById(R.id.detail_button_text1);
				TextView t2 = (TextView) button.findViewById(R.id.detail_button_text2);
				t1.setText(mProgram.tags.get(i).tag);
				mLinearBtnWrap.addView(button, lp);
				Log.i("FocusedRelativeLayout", "dispatch: button" + button);
			}
			// checkbox
			mBtnFavor = (CheckBox) mInflater.inflate(R.layout.detail_checkbox, null);
			Log.i("FocusedRelativeLayout", "dispatch: mBtnFavor" + button);
			// mBtnFavor.setFocusable(false);
			mBtnFavor.setOnFocusChangeListener(mButtonFocusLisnter);
			mBtnFavor.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					v.requestFocus();
					if (event.getAction() == KeyEvent.ACTION_UP) {
						mBtnFavor.setChecked(!mBtnFavor.isChecked());
						return true;
					}
					return false;
				}
			});
			mBtnFavor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (mProgram == null) { // fix bug
						return;
					}
					if (mProgram.isFavor != isChecked) {
						mProgram.isFavor = isChecked;
						SqlFavorDao.updateFavor(mProgram);
						if (isChecked) {
							runFavouriteAsyncTask();
							Toast.makeText(YingshiDetailActivity.this, R.string.favor_success, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(YingshiDetailActivity.this, R.string.unfavor_success, Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			int s = mProgram.tags.size() - 1;
			if (s > 3) {
				s = 3;
			}
			// 注：收藏按钮左边最多三个按钮，如果你多于三个，休怪我不客气了!
			lp.addRule(RelativeLayout.RIGHT_OF, ID_BASE_1000 + s);
			lp.leftMargin = 2;
			mLinearBtnWrap.addView(mBtnFavor, lp);

			updateCheckbox(button);
			mLinearBtnWrap.setVisibility(View.VISIBLE);
			mLinearBtnWrap.requestFocus();
		}
		queryPrice();
	}

	private void updateCheckbox(RelativeLayout lay) {
		if (lay == null) {
			Exception e = new RuntimeException("error: YingshiDEtailActivity updateCheckbox(): btn should not be null!");
			Log.e(TAG, "yunos:", e);
		}
		lay.measure(0, 0);

		StateListDrawable stateDraw = new StateListDrawable();
		stateDraw.addState(new int[] { android.R.attr.state_checked }, getResources().getDrawable(R.drawable.tui_ic_btn_favorite_checked));
		stateDraw.addState(new int[] { android.R.attr.state_selected }, getResources().getDrawable(R.drawable.tui_ic_btn_favorite_focus));
		stateDraw.addState(new int[] { -android.R.attr.state_checked }, getResources().getDrawable(R.drawable.tui_ic_btn_favorite_normal));

		int oldPadding = mBtnFavor.getPaddingTop();
		int padding = (int) (mBtnFavor.getPaddingTop() * 0.8);
		mBtnFavor.setPadding(padding, ((int) (oldPadding * 0.7f)), padding, ((int) (oldPadding * 0.7f)));
		mBtnFavor.setCompoundDrawablePadding(-2);
		mBtnFavor.setCompoundDrawablesWithIntrinsicBounds(null, stateDraw, null, null);
		mBtnFavor.getLayoutParams().width = lay.getMeasuredWidth();
		mBtnFavor.getLayoutParams().height = lay.getMeasuredHeight();

		boolean favored = SqlFavorDao.getFavor(mProgramId);
		mProgram.isFavor = favored;
		mBtnFavor.setChecked(favored);
	}

	private void getAdvertisement() {
		runTask(new WorkAsyncTask<AdInfo>(this) {
			@Override
			public AdInfo doProgress() throws Exception {
				AdInfo ad = SourceWasu.getAdvertise();
				return ad;
			}

			@Override
			public void onPost(boolean resultState, AdInfo resultObject) throws Exception {
				if (resultState) {
					if (resultObject == null) {
						mAdImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_ad));
						return;
					}
					ImageLoadParam adParam = new ImageLoadParam(resultObject.recommend);
					Global.getAdImageLoader().loadImage(adParam, new OnImageLoadListener(mAdImageView) {
						@Override
						public void onLoad(ImageView imageView, Bitmap bitmap, ImageLoadType type, String url) {
							switch (type) {
							case memory:
							case network:
							case disk:
								imageView.setImageBitmap(bitmap);
								break;
							case default_image:
							case error:
								imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_ad));
								break;
							}
						}
					});
				} else {
					mAdImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_ad));
				}
			}
		});
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean b = super.dispatchKeyEvent(event);
		Log.i("FocusedRelativeLayout", "dispatch:" + getCurrentFocus());
		return b;
	}

	/**
	 * 兼容鼠标与遥控点击
	 */
	private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// 先获取焦点，然后当事件up的时候，调用一下callOnClick
			v.requestFocus();
			if (event.getAction() == MotionEvent.ACTION_UP) {
				v.callOnClick();
				return true;
			}
			return false;
		}
	};

	private View.OnClickListener mJuheBtnPlayListener = new TvOnViewClickListener() {
		@Override
		public void onClicked(View vv) {
			Integer index = (Integer) vv.getTag();
			performJuheButtonClick(index);
		}
	};

	// 没有续播
	private void performJuheButtonClick(final int clickBtnTagIndex) {
		performJuheXuanjiDialog(clickBtnTagIndex, 0);
	}

	private void performJuheXuanjiDialog(final int clickBtnTagIndex, int initSelectIndex) {
		if (mProgram.player != null) {
			JuheSource currentSource = mProgram.player.get(clickBtnTagIndex);
			int count = currentSource.url.size();
			if (count > 1) {
				// 弹出选集窗口
				if (mXuanjiDialog != null && mXuanjiDialog.isShowing()) {
					mXuanjiDialog.dismiss();
				}
				mXuanjiDialog = new XuanjiDialog(YingshiDetailActivity.this, currentSource, initSelectIndex, mProgram.classId);
				mXuanjiDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						XuanjiDialog xd = (XuanjiDialog) dialog;
						if (xd.getSelected() != -1) {
							invokePlayer(clickBtnTagIndex, xd.getSelected());
						}
					}
				});
				mXuanjiDialog.show();
			} else if (count == 1) {
				invokePlayer(clickBtnTagIndex, 0);
			} else {
				Toast.makeText(YingshiDetailActivity.this, R.string.fail_get_play_source, Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			Toast.makeText(YingshiDetailActivity.this, R.string.fail_get_play_source, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * 逻辑说明： 查询最后播放，如果上次播放过，则弹出提示是否继续播放，否则重播(电影)或重新选集
	 * 注：不管是高清还是流畅，播放的时间是可以对应的。lastplay中不存放是否高清
	 */
	private View.OnClickListener mBtnPlayListener = new TvOnViewClickListener() {
		@Override
		public void onClicked(View vv) {
			final int clickBtnTagIndex = (Integer) vv.getTag();
			// final Program lastPlayProgram =
			// SqlLastPlayDao.getLastPlayProgram(mProgram.id);
			if (mLastPlayProgram != null) {// 上次播过并且非最后正常播完退出，则弹出alertDIalog提示续播
				String message = null;
				int negtiveButtonTextId = 0;
				if (mLastPlayProgram.lastPlayTime > 0) {
					if (mLastPlayProgram.showType == 3) { // 电视剧
						negtiveButtonTextId = R.string.rechoose_play;
						message = getString(R.string.lastplay_title) + mLastPlayProgram.lastPlayFileName + getString(R.string.ji)
								+ getDuration(mLastPlayProgram.lastPlayTime / 1000) + getString(R.string.keep_play_ask);
					} else /* if (lastPlayProgram.showType == 1) */{ // 电影
						negtiveButtonTextId = R.string.replay;
						message = getString(R.string.lastplay_title) + getDuration(mLastPlayProgram.lastPlayTime / 1000)
								+ getString(R.string.keep_play_ask);
					}
				}
				if (negtiveButtonTextId != 0) {// 有上次播放记录且播放时间>0，需要弹出上次播放时间提示用户选择
					if (mLastplayDialog != null && mLastplayDialog.isShowing()) {
						mLastplayDialog.dismiss();
					}
					mLastplayDialog = new AlertDialog.Builder(YingshiDetailActivity.this).setMessage(message)
							.setPositiveButton(R.string.keep_play, new TvOnDialogClickListener() {

								@Override
								public void onClicked(DialogInterface dialog, int which) {
									// 这里-1表示不要更新到数据库里的lastplaytime值
									invokePlayer(clickBtnTagIndex, -1);// 直接续播
								}
							}).setNegativeButton(negtiveButtonTextId, new TvOnDialogClickListener() {

								@Override
								public void onClicked(DialogInterface dialog, int which) {
									// 重新选集或重新开始播放
									// 找到对应lastPlayFileName的那一集的下标
									int initSelectIndex = findInitXuanjiIndex(clickBtnTagIndex);
									mLastPlayProgram = null;
									performXuanjiDialog(clickBtnTagIndex, initSelectIndex);
								}
							}).create();
					mLastplayDialog.show();
				} else {
					// 上次播放过， 重新选集或重新开始播放
					performXuanjiDialog(clickBtnTagIndex);
				}
			} else {// 上次没有播放过
				// 重新选集或重新开始播放
				performXuanjiDialog(clickBtnTagIndex);
			}
		}

		private int findInitXuanjiIndex(int clickBtnTagIndex) {
			if (mProgram.tags != null) {
				Tag currentTag = mProgram.tags.get(clickBtnTagIndex);
				int count = currentTag.source.size();
				if (mLastPlayProgram != null) {
					for (int i = 0; i < count; ++i) {
						Source s = currentTag.source.get(i);
						if (mLastPlayProgram.lastPlayFileName.equals(s.fileName)) {
							return i;
						}
					}
				}
			}
			return 0;
		}
	};

	private void performXuanjiDialog(final int clickBtnTagIndex) {
		performXuanjiDialog(clickBtnTagIndex, 0);
	}

	/**
	 * @param lastPlayFileName
	 *            上次播放的那集name 确保选集对话框定位到上次播放的那集上。如果没有上次播放，则传null
	 */
	private void performXuanjiDialog(final int clickBtnTagIndex, int initSelectIndex) {
		// 判断视频是否有选集,没有则直接播放,有则弹出选集对话框，电影也有可能有选集
		if (mProgram.tags != null) {
			Tag currentTag = mProgram.tags.get(clickBtnTagIndex);
			int count = currentTag.source.size();
			if (count > 1) {
				//如果正在询价就会直接返回
				if (currentTag.source.get(0).price == -0.5f) { // 正在询价
					Log.d("yyy", "正在询价过程中");
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(YingshiDetailActivity.this, YingshiDetailActivity.this.getString(R.string.querying), Toast.LENGTH_LONG).show();
						}
					});
				    return ;
				}
				// 弹出选集窗口
				if (mXuanjiDialog != null && mXuanjiDialog.isShowing()) {
					mXuanjiDialog.dismiss();
				}
				mXuanjiDialog = new XuanjiDialog(YingshiDetailActivity.this, currentTag, initSelectIndex);
				mXuanjiDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						XuanjiDialog xd = (XuanjiDialog) dialog;
						if (xd.getSelected() != -1) {
							int selected = xd.getSelected();
							invokePlayer(clickBtnTagIndex, selected);
						} else {
							if (mLastPlayProgram == null) {
								mLastPlayProgram = SqlLastPlayDao.getLastPlayProgram(mProgram.id);
							}
						}
					}
				});
				mXuanjiDialog.show();
			} else if (count == 1) { // 电影只有一集
				// 直接播放
				invokePlayer(clickBtnTagIndex, 0);
			} else {
				Toast.makeText(YingshiDetailActivity.this, R.string.fail_get_play_source, Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			Toast.makeText(YingshiDetailActivity.this, R.string.fail_get_play_source, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * * 通过选集点击，或者电影直接点击播放。非续播 注意，就算重新选集，点击上次播放那集也从头开始播，不续播。不然想从头播都难了。
	 * 
	 * @param clickBtnTagIndex
	 *            　点击的那个tag的下标值
	 * @param thisJiIndex
	 *            电影传0, 电视剧传集数下标。如果续播，则传-1
	 */
	private void invokePlayer(final int clickBtnTagIndex, int thisJiIndex) {
		String lastPlayFileName = mIsJuheVideo ? "" : "1";
		long lastPlayTime = 0;
		if (mLastPlayProgram != null) {
			lastPlayFileName = mLastPlayProgram.lastPlayFileName;
			lastPlayTime = mLastPlayProgram.lastPlayTime;
			// 找出thisSourceIndex
			if (mIsJuheVideo) {
				mProgram.lastPlayTime = 0; // 没有续播
				JuheSource s = mProgram.player.get(clickBtnTagIndex);
				for (int i = 0, len = s.url.size(); i < len; ++i) {
					JuheSourceUrl url = s.url.get(i);
					if (lastPlayFileName.equals(url.deTitle)) {
						thisJiIndex = i;
						break;
					}
				}
			} else {
				mProgram.lastPlayTime = mLastPlayProgram.lastPlayTime;
				for (int i = 0, len = mProgram.tags.get(clickBtnTagIndex).source.size(); i < len; ++i) {
					Source source = mProgram.tags.get(clickBtnTagIndex).source.get(i);
					if (lastPlayFileName.equals(source.fileName)) {
						thisJiIndex = i;
						break;
					}
				}
			}
		}
		if (thisJiIndex < 0) {
			thisJiIndex = 0;
		}

		_toPlay(clickBtnTagIndex, thisJiIndex, lastPlayFileName, lastPlayTime);
	}

	private void _toPlay(final int clickBtnTagIndex, int thisJiIndex, String lastPlayFileName, long lastPlayTime) {
		String sourceName = "";
		int high = 0;
		if (mIsJuheVideo) {
			sourceName = mProgram.player.get(clickBtnTagIndex).key;
			high = mProgram.player.get(clickBtnTagIndex).high;
		}
		// PlayerInvoke invoke = new PlayerInvoke(this, mProgram, sourceName, high);
		setPlayerInvoke(mProgram, sourceName, high);
		setThisPlayInfo(clickBtnTagIndex, thisJiIndex);
		setLastPlayInfo(lastPlayFileName, lastPlayTime);

		if (mIsJuheVideo) {
			if (mProgram.classId.equals("T") || mProgram.classId.equals("M")) {
				mProgram.lastPlayTagName = mProgram.player.get(clickBtnTagIndex).key;
				mProgram.lastPlayFileName = mProgram.player.get(clickBtnTagIndex).url.get(thisJiIndex).deTitle;
				SqlLastPlayDao.addLastPlayProgram(mProgram);// 如果已存在，则更新一下lastmodifytime，影响排序;
															// 如果不存在，则插入
			}
		} else {
			// 数据库更新最新播放记录
			if (mProgram.showType == 1 || mProgram.showType == 3) {
				mProgram.lastPlayTagName = mProgram.tags.get(clickBtnTagIndex).tag;
				// 这里不保存sourceindex的原因是，由于不同视频源下面的集数不一致，而且华数那边可能还会动态的增加集数。
				// 某些情况下，清晰视频只有22,23集。
				// 过几天又变成了21,22,23集，所以不能保存sourceindex到数据库，不然对应不上了。
				mProgram.lastPlayFileName = mProgram.tags.get(clickBtnTagIndex).source.get(thisJiIndex).fileName;
				SqlLastPlayDao.addLastPlayProgram(mProgram);// 如果已存在，则更新一下lastmodifytime，影响排序;
															// 如果不存在，则插入
			}
		}
		// sendBroadcast(new Intent(Const.ACTION_PLAY));

		play();
	}

	private String getDuration(long seconds) {
		if (seconds == 0) {
			return "0" + getString(R.string.sec);
		}
		StringBuilder sb = new StringBuilder("");
		long h = seconds / 3600;
		long m = seconds % 3600 / 60;
		long s = seconds % 60;
		if (h > 0) {
			sb.append(h + getString(R.string.hour));
		}
		if (m > 0) {
			sb.append(m + getString(R.string.min));
		}
		if (s > 0) {
			sb.append(s + getString(R.string.sec));
		}
		return sb.toString();
	}

	@Override
	protected void onDestroy() {
		Log.i("yyy", "onDestory");
		// 询价的服务
		unbindService(mWasuAliPlayerServiceConn);
		// 检测华数会员的服务
		unbindService(mWasuUserConnection);
		// ----------
		unregisterReceiver(mBroadcastReceiver);
		unregisterReceiver(mLoginReceiver);
		NetworkManager.instance().unregisterStateChangedListener(mINetworkListener);
		logi("onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		if (mLastplayDialog != null) {
			mLastplayDialog.dismiss();
			mLastplayDialog = null;
		}
		if (mXuanjiDialog != null) {
			mXuanjiDialog.dismiss();
			mXuanjiDialog = null;
		}
		if (mJuheSourceDialog != null) {
			mJuheSourceDialog.dismiss();
			mJuheSourceDialog = null;
		}
		super.onStop();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		Log.i("aabb", "===========ff=====" + mProgramId + ",class:" + mClassId);
		switch (id) {
		case DIALOG_ID_YINGSHI_NOT_EXIST:
			dialog = new AlertDialog.Builder(this).setMessage(R.string.fail_get_yingshi_detail_server_sys).setCancelable(false)
					.setPositiveButton(android.R.string.ok, new TvOnDialogClickListener() {
						public void onClicked(DialogInterface dialog, int whichButton) {
							if (!TextUtils.isEmpty(mFrom)) { // 从收藏或历史播放来的
								boolean fromFavor = FavorActivity.FAV.equals(mFrom);
								if (fromFavor) {// 收藏的只有华数资产
									SqlFavorDao.deleteById(mProgramId);// 收藏的只有华数资产
								} else {// 播放历史记录
									if (mIsJuheVideo) {
										SqlLastPlayDao.deleteById(mProgramId, mClassId);
									} else {
										SqlLastPlayDao.deleteById(mProgramId);
									}
								}
							}
							dialog.dismiss();
							finish();
						}
					}).create();
			dialog.show();
			break;
		}
		return dialog;
	}

	//----------------------
	public void setPlayerInvoke(Program p, String source, int high) {
		this.mPlayProgram = p;
		this.source = source;
		// 2:超清，1: 高清, 0:标清
		switch (high) {
		case 0:
			this.high = getString(R.string.biaoqing);
			break;
		case 1:
			this.high = getString(R.string.gaoqing);
			break;
		case 2:
			this.high = getString(R.string.chaoqing);
			break;
		default:
			this.high = getString(R.string.biaoqing);
		}
	}

	/**
	 * 设置当前用户选择的播放信息
	 * 
	 * @param thisTagIndex
	 *            当前选择的视频源下标
	 * @param thisJiIndex
	 *            当前用户选择的视频源下的第几集，是下标值。如果电影只有一集，则默认为0
	 */
	public void setThisPlayInfo(int thisTagIndex, int thisJiIndex) {
		this.mThisTagIndex = thisTagIndex;
		this.mThisSourceIndex = thisJiIndex;
	}

	/**
	 * 设置上次播放信息
	 * 
	 * @param lastSourceFileName
	 *            上次播放的集数名字
	 * @param lastPlayTime
	 *            上次那集的播放时间
	 */
	public void setLastPlayInfo(String lastSourceFileName, long lastPlayTime) {
		this.mLastSourceFileName = lastSourceFileName;
		this.mLastPlayTime = lastPlayTime;
	}

	//发送给服务端播放记录
	public void runLogAsyncTask() {
		WorkAsyncTask<Boolean> asyncTask = new WorkAsyncTask<Boolean>(this) {
			@Override
			public Boolean doProgress() {
				try {
					String fileId = mPlayProgram.fileId;
					if (mPlayProgram.tags != null && mPlayProgram.tags.get(mThisTagIndex) != null
							&& mPlayProgram.tags.get(mThisTagIndex).source != null
							&& mPlayProgram.tags.get(mThisTagIndex).source.get(mThisSourceIndex) != null) {
						fileId = mPlayProgram.tags.get(mThisTagIndex).source.get(mThisSourceIndex).fileId;
					}
					SourceWasu.sendPlayLog(SystemProUtils.getUUID(), mPlayProgram.id, fileId);

					// 将数据库中未发送的日志都重新发送一遍,发送完成后删除
				} catch (SourceException e) {
					// 春哥说了，只管发，不要管它出错。
					// Log.e("PlayerINvoke","player send log fail", e);
					return false;
					// 发生网络错误,则将日志内容保存到数据库中,下次一起发送
					// if(e.getCode() == 3 || e.getCode() == 4){
					// //将内容保存到数据库
					// }
				}
				return true;
			}
		};

		asyncTask.execute();
	}

	private IWasuAliPlayerCallback mQueryPriceCallback = new IWasuAliPlayerCallback.Stub() {
		@Override
		public void onResultPrice(String jsonString) throws RemoteException {
			Log.d("yyy", "QueryPriceCallback-------onResultPrice()"+jsonString);
			JSONObject priceResult;
			try {
				priceResult = new JSONObject(jsonString);
				if(priceResult.getInt("success") == 2){ //1成功 2失败
				   final String failDescription = priceResult.getString("checkMessage");
				   runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(YingshiDetailActivity.this, failDescription, Toast.LENGTH_LONG).show();
						}
				   });
				   return ;
				}
				final float price = (float) priceResult.getDouble("price");
				Log.i("yyy", "QueryPriceCallback price:" +  priceResult.getDouble("price") + jsonString);
				String ppvId = priceResult.optString("ppvId");
				final int btnIndex = priceResult.optInt("index");
				if (mProgram.tags == null || mProgram.tags.size() == 0) {
					return;
				}
				Tag tag = mProgram.tags.get(btnIndex);//点击的那个码率 
				if(mProgram.showType == 3){//电视剧, 只同一个码率的各个集只询价一次，但是所有集我们都给他赋值一样的价格
					for (int j = 0, len2 = tag.source.size(); j < len2; ++j) {
						Source source = tag.source.get(j);
						source.price = price;
					}
				}
				for (int j = 0, len2 = tag.source.size(); j < len2; ++j) {
					Source source = tag.source.get(j);
					if (source.ppvId.equals(ppvId)) {
						source.price = price;
						runOnUiThread(new Runnable() {
							public void run() {
								TextView text = (TextView) mLinearBtnWrap.getChildAt(btnIndex).findViewById(R.id.detail_button_text2);
								if (price > 0.00f) {
									if(mProgram.showType == 3){
										text.setText(String.valueOf(price) + getString(R.string.yuanperji));
									}else{
										text.setText(String.valueOf(price) + getString(R.string.yuan));
									}
								} else {
									text.setText(getString(R.string.free));
								}
							}
						});
						break;
					}
				} 
			} catch (JSONException e) {
				L.d("IWasuAliPlayerCallback-----JSONException");
				e.printStackTrace();
			}
		}
	};

	public void play() {
		playJson = null;
		if (mPlayProgram.isJuheVideo) {
			WasuPlayInfo playInfo = new WasuPlayInfo(mPlayProgram, this.source, this.high);
			if (mPlayProgram.player != null && mPlayProgram.player.get(mThisTagIndex) != null
					&& mPlayProgram.player.get(mThisTagIndex).url != null
					&& mPlayProgram.player.get(mThisTagIndex).url.get(mThisSourceIndex) != null) {
				JuheSourceUrl s = mPlayProgram.player.get(mThisTagIndex).url.get(mThisSourceIndex);
				playJson = playInfo.getJsonString(mThisTagIndex, s.deTitle, mLastSourceFileName, mLastPlayTime);
			} else {
				playJson = playInfo.getJsonString(mThisTagIndex, "", "0", 0); // fuck
			}

			startPlay(playJson);
		} else {
			WasuPlayInfo playInfo = new WasuPlayInfo(mPlayProgram, ppvPath);
			Log.i("yyy", "play====ppvPath=====" + ppvPath);
			//向服务端发送播放记录
			runLogAsyncTask();
			
			if (mPlayProgram.tags != null && mPlayProgram.tags.get(mThisTagIndex) != null
					&& mPlayProgram.tags.get(mThisTagIndex).source != null
					&& mPlayProgram.tags.get(mThisTagIndex).source.get(mThisSourceIndex) != null) {
				Source s = mPlayProgram.tags.get(mThisTagIndex).source.get(mThisSourceIndex);
				playInfo.setPrice(String.valueOf(s.price));
				playJson = playInfo.getJsonString(mThisTagIndex, s.fileName, mLastSourceFileName, mLastPlayTime);
				Log.i("yyy", "playJson:" + playJson);
				
				if (s.price > -0.5f) {// 如果已经询价成功
					if (s.price > 0.00f) {// 如果price>0,检测是否为华数会员
						Log.d("yyy", "如果price>0,检测是否为华数会员");
						CheckIsWasuVip();
					} else {// 如果price==0,直接进入播放流程
						Log.d("yyy", "如果price=0,免费的片子直接进入播放器");
						
						startPlay(playJson);
					}
				} else {// 如果未询价成功，重新询价---------
					if (s.price == -0.5f) { // 正在询价
						Log.d("yyy", "正在询价过程中");
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(YingshiDetailActivity.this, YingshiDetailActivity.this.getString(R.string.querying), Toast.LENGTH_LONG).show();
							}
						});
						return;
					} else { // 重新询价
						Log.i("yyy", "重新询价");
						bindWasuAliPlayerService();
						if (mWasuAliPlayerService == null) {
							return ;
						}
						try {
							mWasuAliPlayerService.queryPrice("{'ppvId':'" + playInfo.ppvId + "'," + "'index':'" + mThisSourceIndex + "'}", mQueryPriceCallback);
							Log.i("yyy", "calling queryPrice, ppvId:" + playInfo.ppvId);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			} else {// 资讯类，没有上次播放信息 TODO 播放器已支持续播，将来要改
				playJson = playInfo.getJsonString(mThisTagIndex, "", "0", 0);
				startPlay(playJson);
			}
		}
	}

	/**
	 * 通过Intent调用播放器
	 */
	public void startPlay(String playJson) {
		Intent intent = new Intent(ACTION_WASU_PLAY);
		intent.putExtra("playInfo", playJson);
		Log.d("yyy", "playJson:" + playJson);
		startActivity(intent);
	}

	public WasuUserInterface mWasuUserInterface;

	/**
	 * 华数判断会员service的ServiceConnection
	 */
	public ServiceConnection mWasuUserConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d("yyy", "=======WasuUserServiceConnected===onServiceConnected#####=");
			mWasuUserInterface = WasuUserInterface.Stub.asInterface(service);
			synchronized (wasuuserOperationBindLock) {
				wasuuserOperationBindLock.notifyAll();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mWasuUserInterface = null;
			Log.d("yyy", "====LoadingDialog=====WasuUserServiceDisconnected====");
		}
	};

	/**
	 * 检查华数会员
	 */
	public void CheckIsWasuVip() {
		Log.d("yyy", "=====CheckIsWasuVip()=====");
		bindUserOperation();
		if(mWasuUserInterface == null){
			return ;
		}
		try {
			mWasuUserInterface.checkIsVIP(wasuVipCallback); //检查是否为华数会员
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("yyy", "mWasuUserInterface.checkIsVIP(wasuVipCallback)--->" + e);
		}
	}

	/**
	 * 判断是否为华数会员的回调
	 */
	public WasuUserCallback wasuVipCallback = new WasuUserCallback.Stub() {
		@Override
		public void onResult(boolean result) throws RemoteException {
			Log.d("yyy", "#####wasuVipCallback####" + result);
			if (result) {// 是华数的会员，则调用播放器播放
				Log.d("yyy", "是会员---进入播放器");				
				WasuPlayInfo playInfo = new WasuPlayInfo(mPlayProgram, ppvPath);
				if (mPlayProgram.tags != null && mPlayProgram.tags.get(mThisTagIndex) != null
						&& mPlayProgram.tags.get(mThisTagIndex).source != null
						&& mPlayProgram.tags.get(mThisTagIndex).source.get(mThisSourceIndex) != null) {
					Source s = mPlayProgram.tags.get(mThisTagIndex).source.get(mThisSourceIndex);
					playInfo.setPrice(String.valueOf(s.price));
					playJson = playInfo.getJsonString(mThisTagIndex, s.fileName, mLastSourceFileName, mLastPlayTime);
				}
				startPlay(playJson);
			} else {// 如果不是华数的会员，产看淘宝登录帐号状态
				Log.d("yyy", "不是会员--->进入注册流程");
				// 判断淘宝帐号是否登录
				if (getLoginKeyCode() != 200) {// 如果没有登录
					Log.d("yyy", "不是会员--->如果没有登录");
					runOnUiThread(new Runnable() {
						public void run() {
							showLoginDialog();
						}
					});					
				} else {// 如果已经登录
					Log.d("yyy", "不是会员--->已经登录");
					getUserInfo();
				}
			}
		}

	};

	private TYIDManager mTYIDManager = null;//获取用户信息的类
	/**
	 * 判断是否登录
	 */
	private int getLoginKeyCode() {
		int mKeyCode = 0;
		try {
			if (mTYIDManager == null) {
				mTYIDManager = TYIDManager.get(this);
			}
			mKeyCode = mTYIDManager.yunosGetLoginState();
			Log.d("yyy", "=======getLoginKeyCode()=====TYIDManager===" + mKeyCode);
		} catch (TYIDException e1) {
			Log.d("yyy", "=======getLoginKeyCode()====TYIDException===");
			e1.printStackTrace();
		}
		return mKeyCode;
	}

	/**
	 * 登录后获取用户信息
	 */
	private void getUserInfo() {
		try {
			if (mTYIDManager == null) {
				mTYIDManager = TYIDManager.get(this);
			}
			String loginName = mTYIDManager.yunosGetLoginId();
			if (loginName != null) {
				Log.d("yyy", "====getUserInfo()==loginName != null ==" + loginName);
				Message msg = new Message();
				msg.obj = loginName;
				mHandle.sendMessage(msg);
			} else {
				Log.d("yyy", "====getUserInfo()==loginName null ==");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Handler mHandle = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				String mAccount = (String) msg.obj;
				Log.d("yyy", "====myHandle===注册华数会员=");
				try {
					mWasuUserInterface.registerWasuVIP(mAccount, "", "", "", wasuVipCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else {
				Log.d("yyy", "====myHandle== msg null====");
			}
		}
	};

	/**
	 * 显示登录提示框
	 */
	public void showLoginDialog() {
		Log.d("yyy", "====showLoginDialog()=调用登录===");
		if (mLoginAlert != null) {
			mLoginAlert.dismiss();
			mLoginAlert = null;
		}
		mLoginAlert = new AlertDialog.Builder(YingshiDetailActivity.this).setMessage(R.string.please_login_first)
				.setPositiveButton(R.string.please_login_go, new TvOnDialogClickListener() {
					@Override
					public void onClicked(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClassName("com.yunos.account", "com.yunos.account.AccountLoginIndex");
						intent.putExtra("from", "com.yunos.tv.yingshi");
						startActivity(intent);
					}
				}).setNegativeButton(android.R.string.cancel, new TvOnDialogClickListener() {
					@Override
					public void onClicked(DialogInterface dialog, int which) {
					}
				}).setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

					}
				}).create();
		mLoginAlert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mLoginAlert.show();
	}
	
	public void hideDialog() {
		if (mLoginAlert != null) {
			mLoginAlert.dismiss();
			mLoginAlert = null;
		}
	}

}
