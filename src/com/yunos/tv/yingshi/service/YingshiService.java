package com.yunos.tv.yingshi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yunos.tv.source.SourceWasu;
import com.yunos.tv.source.entity.Dianbo;
import com.yunos.tv.source.entity.HomeshellMessageList;
import com.yunos.tv.source.entity.PlayBack;
import com.yunos.tv.yingshi.Const;
import com.yunos.tv.yingshi.aidl.Catalog;
import com.yunos.tv.yingshi.aidl.IYingshiService;
import com.yunos.tv.yingshi.dao.sql.SqlCatalogDao;
import com.yunos.tv.yingshi.dao.sql.SqlLastPlayDao;
import com.yunos.tv.yingshi.exception.ErrorCodes;
import com.yunos.tv.yingshi.exception.SourceException;

public class YingshiService extends Service {

	public static final String TAG = "YingshiService";

	IYingshiServiceImpl mBinder = new IYingshiServiceImpl();

//	private Object objLock = new Object();

	@Override
	public IBinder onBind(Intent arg0) {
//		Log.v(TAG, "onBind() called");
		return mBinder;
	}

	public class IYingshiServiceImpl extends IYingshiService.Stub {

		@Override
		public List getLastPlayList() throws RemoteException {
			return SqlLastPlayDao.getLastPlayList(Const.LAST_PLAY_FETCH_NUM);//如果没有数据，则返回的是空list
		}

		@Override
		public Map getCategoryList(int type) throws RemoteException {
			//http://video.tv.yunos.com/video/node/root?type=1
			Map map = new HashMap<String, Object>();
			try {
				List<Catalog> list = SourceWasu.getRootCatalog(type);
				SqlCatalogDao.addCatalogList(type, list, "-1");
				map.put("code", ErrorCodes.NULL.getCode()); // -1
				map.put("list", list);
			} catch (SourceException e) {
				map.put("code", e.getCode());
				map.put("errorMsg", e.getErrorMessage());
				Log.e(TAG, e.getErrorMessage(), e);
			} catch (Exception e1) {
				Log.e(TAG, "YingshiService:getCategoryList", e1);
			}
			return map;
		}
		
		@Override
		public Map getCacheCatatoryList() throws RemoteException {
			List<Catalog> list0 = SqlCatalogDao.getCatalogList(0, "-1");
			List<Catalog> list1 = SqlCatalogDao.getCatalogList(1, "-1");
			Map map = new HashMap<String, Object>();
			map.put("zixun", list0);
			map.put("yingshi", list1);
			return map;
		}

		@Override
		public Map getDianboList() throws RemoteException {
			Map map = new HashMap<String, Object>();
			try {
				List<Dianbo> list = SourceWasu.getDianboList("wasu_2013_0825");
				map.put("code", ErrorCodes.NULL.getCode()); // -1
				map.put("list", list);
			} catch (SourceException e) {
				map.put("code", e.getCode());
				map.put("errorMsg", e.getErrorMessage());
				Log.e(TAG, e.getErrorMessage(), e);
			} catch (Exception e1) {
				Log.e(TAG, "YingshiService:getCategoryList", e1);
			}
			return map;
		}

		@Override
		public Map getMessageList() throws RemoteException {
			Map map = new HashMap<String, Object>();
			try {
				HomeshellMessageList list = SourceWasu.getMessageList(1, 100);
				map.put("code", ErrorCodes.NULL.getCode()); // -1
				map.put("list", list);
			} catch (SourceException e) {
				map.put("code", e.getCode());
				map.put("errorMsg", e.getErrorMessage());
				Log.e(TAG, e.getErrorMessage(), e);
			} catch (Exception e1) {
				Log.e(TAG, "YingshiService:getCategoryList", e1);
			}
			return map;
		}

		@Override
		public Map getPlayBackList() throws RemoteException {
			Map map = new HashMap<String, Object>();
			try {
				PlayBack kanba = SourceWasu.getPlayBackList("wasu_2013_0825");
				map.put("code", ErrorCodes.NULL.getCode()); // -1
				map.put("playback", kanba);
			} catch (SourceException e) {
				map.put("code", e.getCode());
				map.put("errorMsg", e.getErrorMessage());
				Log.e(TAG, e.getErrorMessage(), e);
			} catch (Exception e1) {
				Log.e(TAG, "YingshiService:getCategoryList", e1);
			}
			return map;
		}
		
	}
	
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		Log.v(TAG, "onCreate called");
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		Log.v(TAG, "onDestory() called");
//	}
//
//	@Override
//	public void onStart(Intent intent, int startId) {
//		super.onStart(intent, startId);
//		Log.v(TAG, "onStart() called");
//	}

}
