package com.yunos.tv.yingshi.dao.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.aliyun.base.database.AbsSqlDao;
import com.aliyun.base.database.SQLiteHelper;
import com.yunos.tv.yingshi.aidl.Catalog;

public class SqlCatalogDao extends BaseSqlDao<Catalog> {

	public static final String TABLE_NAME = "catalog";
	
	private SqlCatalogDao() {
		super(TABLE_NAME);
	}
	
	public static SqlCatalogDao mCatalogDao;

	public static SqlCatalogDao getCatalogDao() {
		if (mCatalogDao == null) {
			mCatalogDao = new SqlCatalogDao();
		}
		return mCatalogDao;
	}

	@Override 
	public Catalog cursorRowToObject(Cursor cur) {
		Catalog c = new Catalog();
		c.id = cur.getString(cur.getColumnIndex("id"));
		c.name = cur.getString(cur.getColumnIndex("name"));
		c.picUrl = cur.getString(cur.getColumnIndex("picUrl"));
		c.type = cur.getInt(cur.getColumnIndex("type"));
		c.hasProgram = cur.getInt(cur.getColumnIndex("hasProgram"));
		return c;
	}
	
	/**
	 * 
	 * @param type  0:非影视 1:影视
	 * @return
	 */
	public static List<Catalog> getCatalogList(int type, String parent) {
		List<Catalog> result = null;
		
		if ("-1".equals(parent)) {
			result = getCatalogDao().queryForList(null, "type=? AND parent=?", new String[]{String.valueOf(type), "-1"}, 
					null, null, null);
			for (Catalog c: result) {
				c.child = (ArrayList<Catalog>) getCatalogList(type, c.id);
			}
		} else {
			result = getCatalogDao().queryForList(null, "type=? AND parent=?", new String[]{String.valueOf(type), parent}, 
					null, null, null);
		}
		
		return result;
	}
	
	/**
	 * 华数影视还是华数资讯
	 * @param type
	 * @param list
	 * @param parent
	 */
	public static void addCatalogList(int type, List<Catalog> list, String parent) {
		ContentValues[] values = new ContentValues[list.size()];
		
		int k = 0;
		for (Catalog c: list) {
			values[k] = new ContentValues();
			values[k].put("id", c.id);
			values[k].put("name", c.name);
			values[k].put("picUrl", c.picUrl);
			values[k].put("hasProgram", c.hasProgram);
			values[k].put("type", type);
			values[k].put("parent", parent);
			if ("-1".equals(parent)) {
				addCatalogList(type, c.child, c.id);
			}
			
			++k;
		}
		getCatalogDao().replace(values);
	}

}
