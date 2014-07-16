package com.yunos.tv.yingshi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.aliyun.base.BaseApplication;
import com.aliyun.base.exception.NoNetworkException.NoNetworkHanler;
import com.aliyun.base.net.NetConnectionType;
import com.aliyun.base.net.NetworkManager;
import com.aliyun.base.net.NetworkManager.INetworkListener;
import com.aliyun.base.utils.BundleUtils;
import com.aliyun.imageload.ImageLoadConfig;
import com.aliyun.imageload.ImageLoader;
import com.yunos.tv.app.widget.TvOnDialogClickListener;
import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.dao.sql.SqlLastPlayDao;
import com.yunos.tv.yingshi.utils.SystemProUtils;

public class YingshiApplication extends BaseApplication {

	public static final String TAG = "YingshiApplication";

	private AlertDialog mNetworkAlert = null;
	private BroadcastReceiver mReceiver; // 此接收器用于接收华数播放器播放结束后的通知

	@Override
	public void onCreate() {
		super.onCreate();

		readXml();
		initImageLoader();

		NetworkManager.instance().init(this, new NoNetworkHanler() {
			@Override
			public boolean handle(Context context) {
				showNetDialog(context);
				return true;
			}
		});
		NetworkManager.instance().registerStateChangedListener(mINetworkListener);

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (Const.ACTION_WASU_PLAYER_NOTIFY.equals(action)) {
					Bundle b = intent.getExtras();
					if (b != null) {
						BundleUtils.print(b, TAG);
						Program p = new Program();
						p.isJuheVideo = false;
						p.id = intent.getStringExtra("proId");
						p.showType = intent.getIntExtra("showType", 3);// 默认电视剧
						p.nodeId = intent.getStringExtra("nodeId");// 节目所在栏目的ID
						p.cpCode = intent.getStringExtra("cpId");// 此节目对应的CP的ID
						p.fileId = intent.getStringExtra("fileId");// 所有播放的视频内容ID
						p.name = intent.getStringExtra("proName");
						p.lastPlayFileName = String.valueOf(intent.getIntExtra("series", 1));// 本集集数，电影默认为1。//
																								// playindex存放下标值
						p.lastPlayTime = intent.getLongExtra("currentPosition", 0);// 本电视剧集，上次播放时间点。毫秒数
						if (p.lastPlayTime > 0 && p.lastPlayTime < 1000) {
							p.lastPlayTime = 1000; // 不足一秒的视为1秒
						}

						if (p.showType == 2) { // 我承认，这是件很fuck的事情 !
							p.showType = 3;
						}
						if (p.showType == 1 || p.showType == 3) { // 华数自有资产，只有电影和电视剧才更新数据库
							SqlLastPlayDao.updateLastPlaytime(p);
							sendBroadcast(new Intent(Const.ACTION_UPDATE_TITLE));
						}
					} else {
						Log.e(TAG, "onReceive -- bundle is null");
					}
				}
			}
		};
		this.registerReceiver(mReceiver, new IntentFilter(Const.ACTION_WASU_PLAYER_NOTIFY));
	}

	NetworkManager.INetworkListener mINetworkListener = new INetworkListener() {

		@Override
		public void onNetworkChanged(boolean isConnected, NetConnectionType lastType, NetConnectionType currentType) {
			Log.i(TAG, "=====onNetworkChanged====================" + isConnected);
			if (isConnected) {
				// if (mNetworkAlert != null && mNetworkAlert.isShowing())
				// {//把设置网络的那个提示框消失掉
				// mNetworkAlert.dismiss();
				// mNetworkAlert = null;
				// }
				hideDialog();
				// ImageLoader.setPauseWork(false);
			} /*
			 * else if (lastType != NetConnectionType.none) {
			 * showNoNetworkDialog(YingshiApplication.getApplication()); }
			 */
			if (!isConnected) {
				// ImageLoader.setPauseWork(true);
			}
		}
	};

	public void showNetDialog(Context context) {
		if (mNetworkAlert != null) {
			mNetworkAlert.dismiss();
			mNetworkAlert = null;
		}
		// alertdialog要传activity的context才有yunos:theme主题
		mNetworkAlert = new AlertDialog.Builder(context).setMessage(R.string.network_config_message)
				.setPositiveButton(R.string.netconfig, new TvOnDialogClickListener() {

					@Override
					public void onClicked(DialogInterface dialog, int which) {
						// context.startActivity(new
						// Intent("android.settings.NETWORK_SETTINGS"));
						// mNetworkConfigDialog.dismiss();
						Intent intent = new Intent("android.settings.NETWORK_SETTINGS");
						Bundle bnd = new Bundle();
						bnd.putString("FinishMode", "BACK");
						intent.putExtras(bnd);
						// context.startActivityForResult(intent,
						// Const.REQUEST_CODE_NET_SETTING); // 第二个参数自定义
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// !!
																		// 在activity
																		// context之外启动activity
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
		mNetworkAlert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mNetworkAlert.show();
	}
	
	public void hideDialog() {
		if (mNetworkAlert != null) {
			Log.i(TAG, "application -- hideDialog");
			mNetworkAlert.dismiss();
			mNetworkAlert = null;
		}
	}

	public void readXml() {
		XmlPullParser xml = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			xml = factory.newPullParser();
			String device = SystemProUtils.getDeviceName();
			Log.i(TAG, "readXml -- deviceName:" + device);
			List<String> names = Arrays.asList(getAssets().list("config"));
			Log.i(TAG, "readXml -- asset/config:" + names);
			if (!names.contains(device + ".xml")) {
				return;
			}
			InputStream in = null;
			try {
				in = getAssets().open("config/" + device + ".xml");
			} catch (Exception e) {
				return;
			}
			if (in == null) {
				return;
			}
			xml.setInput(in, "utf-8");
			int tagType = xml.next();
			while (tagType != XmlPullParser.END_DOCUMENT) {
				if (tagType == XmlPullParser.START_TAG && xml.getName().equals("integer")) {
					String attName = xml.getAttributeValue("", "name");
					if (attName.equals("frame_gridview")) {
						xml.next();
						Config.FRAME_GRIDVIEW = Integer.valueOf(xml.getText());
					} else if (attName.equals("frame_listview")) {
						xml.next();
						Config.FRAME_LISTVIEW = Integer.valueOf(xml.getText());
					} else if (attName.equals("frame_list_detail")) {
						xml.next();
						Config.FRAME_LISTVIEW_DETAIL = Integer.valueOf(xml.getText());
					} else if (attName.equals("dur_gridview")) {
						xml.next();
						Config.SCROLLING_DURATION_GRIDVIEW = Integer.valueOf(xml.getText());
					} else if (attName.equals("dur_list_detail")) {
						xml.next();
						Config.SCROLLING_DURATION_LISTVIEW_DETAIL = Integer.valueOf(xml.getText());
					} else if (attName.equals("image_fade_time")) {
						xml.next();
						Config.IMAGE_FADE_TIME = Integer.valueOf(xml.getText());
					}
				}
				tagType = xml.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	private void initImageLoader() {
		ImageLoadConfig config = new ImageLoadConfig();
		config.setThreadCount(5);
		config.setMemCacheSize(12 * 1024); // 限定内存缓存10M
		config.setHttpDiskCacheSize(30 * 1024 * 1024);
		config.setWorkDiskCacheSize(10 * 1024 * 1024);
		config.setAssetCacheDir("cache_img");
		ImageLoader.init(this, config);
	}
	
	@Override
	public void onCrash(String errorMessage) {
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(-1);
	}

	@Override
	public void onTerminate() {
		NetworkManager.instance().release();
		NetworkManager.instance().unregisterStateChangedListener(mINetworkListener);
		super.onTerminate();
	}
}
