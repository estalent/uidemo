package com.yunos.tv.player;

import java.util.List;

import net.zhilink.wasualiplayer.services.IWasuAliPlayerCallback;
import net.zhilink.wasualiplayer.services.IWasuAliPlayerService;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.aliyun.ams.tyid.TYIDException;
import com.aliyun.ams.tyid.TYIDManager;
import com.aliyun.base.WorkAsyncTask;
import com.aliyun.imageload.utils.L;
import com.wasu.android.rainbowbox.aidl.WasuUserCallback;
import com.wasu.android.rainbowbox.aidl.WasuUserInterface;
import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.JuheSourceUrl;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.source.entity.Source;
import com.yunos.tv.yingshi.R;
import com.yunos.tv.yingshi.YingshiApplication;
import com.yunos.tv.yingshi.exception.SourceException;
import com.yunos.tv.yingshi.utils.SystemProUtils;

public class PlayerInvoke {
	private static final String ACTION_WASU_PLAY = "com.wasuali.action.player";

	private Program mPlayProgram;
	private int mThisTagIndex;
	private int mThisSourceIndex; // 当前用户点击的相应source下标

	private String mLastSourceFileName;
	private long mLastPlayTime = 0;

	private Context mContext;
	private String source;
	private String high;
	private String playJson;
	private Context mAppliContext;
	
	//保证资讯类视频可以调用
	public PlayerInvoke(Context context, Program p) {
		this(context, p, "", 0);
	}
	
	public PlayerInvoke(Context context, Program p, String source, int high) {
		this.mContext = context;
		this.mPlayProgram = p;
		this.source = source;
//		this.high = high;
		// 2:超清，1: 高清, 0:标清
		switch (high) {
		case 0:
			this.high = context.getString(R.string.biaoqing);
			break;
		case 1:
			this.high = context.getString(R.string.gaoqing);
			break;
		case 2:
			this.high = context.getString(R.string.chaoqing);
			break;
		default:
			this.high = context.getString(R.string.biaoqing);
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

	public void runLogAsyncTask() {
		WorkAsyncTask<Boolean> asyncTask = new WorkAsyncTask<Boolean>(mContext) {
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

	private IWasuAliPlayerCallback myBoundServiceCallback = new IWasuAliPlayerCallback.Stub() {
		@Override
		public void onResultPrice(String jsonString) throws RemoteException {
			JSONObject priceResult;
			try {
				priceResult = new JSONObject(jsonString);
				float price = (float) priceResult.getDouble("price");
				Log.i("yyy", "IWasuAliPlayerCallback"+jsonString);
				Log.i("yyy", "IWasuAliPlayerCallback"+priceResult.getInt("price"));
				if(price > 0){
				//-----------是否为华数会员----------------------
				CheckIsWasuVip();
					
				}else{
					startPlay(playJson);
				}
			} catch (JSONException e) {
				L.d("IWasuAliPlayerCallback-----JSONException");
				e.printStackTrace();
			}
			
			
		}
	};
	
//	public void play(Context context, IWasuAliPlayerService mBoundService, ServiceConnection myBoundServiceConn) {
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.setAction(ACTION_WASU_PLAY);
//		playJson = null;
//		if (mPlayProgram.isJuheVideo) {
//			WasuPlayInfo playInfo = new WasuPlayInfo(mPlayProgram, this.source, this.high);
//			if (mPlayProgram.player != null && mPlayProgram.player.get(mThisTagIndex) != null && mPlayProgram.player.get(mThisTagIndex).url != null
//					&& mPlayProgram.player.get(mThisTagIndex).url.get(mThisSourceIndex) != null) {
//				JuheSourceUrl s = mPlayProgram.player.get(mThisTagIndex).url.get(mThisSourceIndex);
//				playJson = playInfo.getJsonString(mThisTagIndex, s.deTitle, mLastSourceFileName, mLastPlayTime);
//			} else {
//				playJson = playInfo.getJsonString(mThisTagIndex, "", "0", 0); // fuck situation　应该不会出现
//			}
//			
//			intent.putExtra("playInfo", playJson);
//			Log.i("PlayerInvoke", "playJson:" + playJson);
//			mContext.startActivity(intent);
//		} else {
//			WasuPlayInfo playInfo = new WasuPlayInfo(mPlayProgram);
//			//向服务端发送播放记录
//			runLogAsyncTask();
//			if (mPlayProgram.tags != null && mPlayProgram.tags.get(mThisTagIndex) != null && mPlayProgram.tags.get(mThisTagIndex).source != null
//					&& mPlayProgram.tags.get(mThisTagIndex).source.get(mThisSourceIndex) != null) {
//				Source s = mPlayProgram.tags.get(mThisTagIndex).source.get(mThisSourceIndex);
//				playJson = playInfo.getJsonString(mThisTagIndex, s.fileName, mLastSourceFileName, mLastPlayTime);
//				
//				//询价相关部分----------
//				Log.i("yyy", "PlayerInvoke-->play()---->query price part");
//				if (null == mBoundService) {
//					Intent serviceIntent = new Intent();
//					serviceIntent.setAction("net.zhilink.wasualiplayer.services.WasuAliPlayerService");
//					context.bindService(serviceIntent, myBoundServiceConn, Context.BIND_AUTO_CREATE);
//				}
//				Log.i("yyy", "PlayerInvoke-->play()---->111111111111111111");
//				try {
////					mBoundService.queryPrice("{'ppvId':'" + playInfo.ppvId + "'}", myBoundServiceCallback);
//					mBoundService.queryPrice("{'ppvId':'" + 100000036 + "'}", myBoundServiceCallback);
//					Log.i("yyy", "calling queryPrice, ppvId:" + playInfo.ppvId);
//				} catch (RemoteException e) {
//  					e.printStackTrace();
//				}
//				//---------------
//			} else {// 资讯类，没有上次播放信息 TODO 播放器已支持续播，将来要改
//				playJson = playInfo.getJsonString(mThisTagIndex, "", "0", 0);
//				
//				intent.putExtra("playInfo", playJson);
//				Log.i("PlayerInvoke", "playJson:" + playJson);
//				mContext.startActivity(intent);
//			}
//		}
//		
//	}
	
	public void startPlay(String playJson){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setAction(ACTION_WASU_PLAY);
		intent.putExtra("playInfo", playJson);
		Log.i("PlayerInvoke", "playJson:" + playJson);
		mContext.startActivity(intent);
	}
	
	public void playForZixun(List<Program> recommends) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setAction(ACTION_WASU_PLAY);
		WasuPlayInfo playInfo = new WasuPlayInfo(mPlayProgram);
		
		String playJson = playInfo.getJsonForZixun();
		if (recommends != null) {
			intent.putExtra("recommends", WasuPlayInfo.getRecommendsForZixun(recommends));
		}
		intent.putExtra("playInfo", playJson);
		
		Log.i("PlayerInvoke", "playJson:" + playJson);
		mContext.startActivity(intent);
	}
	
	
	/*WasuUserInterface userInterface = new WasuUserInterface(){
		@Override
		public IBinder asBinder() {
			return null;
		}

		@Override
		public void checkIsVIP(WasuUserCallback callback) throws RemoteException {
			
		}

		@Override
		public void registerWasuVIP(String taccount, String zaccount, String tphone, String temail, WasuUserCallback callback)
				throws RemoteException {
			
		}
	};*/
	
	
	public Object mWasuUserInterfaceBindLock = new Object();
	public WasuUserInterface mWasuUserInterface;
	
	/**
	 * 华数判断会员service的ServiceConnection
	 */
	public ServiceConnection mWasuUserConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mWasuUserInterface = WasuUserInterface.Stub.asInterface(service);
			Log.d("yyy", "=======WasuUserServiceConnected===11111=");
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
	public void CheckIsWasuVip(){
		if (mWasuUserInterface == null) {
			Log.d("yyy", "=======mWasuUserInterface null===");
			Intent serviceIntent = new Intent("wasu.intent.action.useroperation");
			mContext.bindService(serviceIntent, mWasuUserConnection, Service.BIND_AUTO_CREATE);
		} else {
			Log.d("yyy", "=======mWasuUserInterface !=null===");
		}
		
		try {
			//检查是否为华数会员
			mWasuUserInterface.checkIsVIP(wasuVipCallback);
			
			synchronized (mWasuUserInterfaceBindLock) {
				mWasuUserInterfaceBindLock.notifyAll();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断是否为华数会员的回调
	 */
	WasuUserCallback wasuVipCallback = new WasuUserCallback(){
		@Override
		public IBinder asBinder() {
			return null;
		}

		@Override
		public void onResult(boolean result) throws RemoteException {
			Log.d("yyy", "##########WasuUserCallback##########1"+result);
			if(result){//是华数的会员，则调用播放器播放
				Log.d("yyy", "是会员---进入播放器");
				Intent playIntent = new Intent(Intent.ACTION_MAIN);
				playIntent.setAction(ACTION_WASU_PLAY);
				playIntent.putExtra("playInfo", playJson);
				Log.i("PlayerInvoke", "playJson:" + playJson);
				mContext.startActivity(playIntent);
			}else{//如果不是华数的会员，产看淘宝登录帐号状态
				Log.d("yyy", "不是会员--->进入注册流程");
				//判断淘宝帐号是否登录
				if (getKeyCode() != 200) {//如果没有登录
					Log.d("yyy", "====MetroUserFragment==result===code !=200== ");
					Intent intent = new Intent();
					intent.setClassName("com.yunos.account", "com.yunos.account.AccountLoginIndex");
					intent.putExtra("from", "com.yunos.tv.yingshi");
					mContext.startActivity(intent);
				} else {//如果已经登录
					Log.d("yyy", "====MetroUserFragment==result===code =200== ");
					getUserInfo();
				}
		
			}
		}
	};
	
	private TYIDManager mTYIDManager = null;
	private int mKeyCode = 0;
	//判断是否登录
	private int getKeyCode() {
		try {
			if (mAppliContext == null) {
				mAppliContext = YingshiApplication.getApplication().getApplicationContext();
			}
			if (mTYIDManager == null) {
				mTYIDManager = TYIDManager.get(mAppliContext);
			}			
			mKeyCode = mTYIDManager.yunosGetLoginState();		
			Log.d("yyy", "=========MetroUserFragment======mTYIDManager==="+mKeyCode);			
		} catch (TYIDException e1) {
			Log.d("yyy", "=========MetroUserFragment======TYIDException===");
			e1.printStackTrace();
		}
		return mKeyCode;
	}
	
	private void getUserInfo() {
		if (mKeyCode == 200) {
			Log.d("yyy", "====MetroUserFragment==code 200=");
			try {
				if (mTYIDManager == null) {
					mTYIDManager = TYIDManager.get(mAppliContext);
				}
				String loginName = mTYIDManager.yunosGetLoginId();
				if (loginName != null) {
					Log.d("yyy", "====MetroUserFragment==retCode===200==" + loginName);
					Message msg = new Message();
					msg.obj = loginName;
					myHandle.sendMessage(msg);
				} else {
					Log.d("yyy", "====MetroUserFragment==loginName null==");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.d("yyy", "====MetroUserFragment==code !200=");
		}
	}
	
	Handler myHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				String mAccount = (String) msg.obj;
				Log.d("yyy", "=========MetroUserFragment======myHandle===");
				try {
					mWasuUserInterface.registerWasuVIP(mAccount, "", "", "", wasuVipCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}					
			} else {
				Log.d("yyy", "=========MetroUserFragment======myHandle==null====");
			}	
		}		
	};
	
	/*public void bindWasuUserInterface() {
		synchronized (mWasuUserInterfaceBindLock) {
			Log.d("yyy", "====LoadingDialog=====bindWasuUserInterface====");
			Intent serviceIntent = new Intent("wasu.intent.action.useroperation");
			mContext.bindService(serviceIntent, mWasuUserConnection, Context.BIND_AUTO_CREATE);
//			mWasuUserInterfaceIsBind = true;
			try {
				mWasuUserInterfaceBindLock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	
/*	public void a(){
		//注册华数会员
		 mWasuUserInterface.registerWasuVIP("ss", "ss", "15557169628", "zhaoyanchang@163.com", new WasuUserCallback.Stub() {
			@Override
			public void onResult(boolean result) throws RemoteException {
				Log.d("yyy", "注册会员"+result);
				if(result){
					Log.d("yyy", "注册会员成功");
				}else{
					Log.d("yyy", "注册会员失败");
				}
			}
		});
	}
	*/
	
}
