package com.yunos.tv.yingshi.dao.sql;

import com.aliyun.base.database.AbsSqlDao;
import com.aliyun.base.database.SQLiteHelper;
import com.yunos.tv.yingshi.YingshiApplication;

public class BaseSqlDao<T> extends AbsSqlDao<T> {
	
	protected static DBHelper mDBHelper = null;
	
	protected static DBHelper getDBHelper() {
		if (mDBHelper == null) {
			mDBHelper = new DBHelper(YingshiApplication.getApplication());
		}
		return mDBHelper;
	}
	
	public BaseSqlDao(String tableName) {
		super(tableName);
		getDBHelper();
	}

	@Override
	protected SQLiteHelper getSQLiteHelper() {
		return getDBHelper();
	}
	
}
