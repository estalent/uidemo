package com.yunos.tv.yingshi.dao.sql;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.yunos.tv.source.entity.Program;
import com.yunos.tv.yingshi.Const;

public class SqlFavorDao extends BaseSqlDao<Program> {

	public static final String TABLE_NAME = "favor";

	private SqlFavorDao() {
		super(TABLE_NAME); 
	}
	
	public static SqlFavorDao mSqlFavorDao;

	public static SqlFavorDao getSqlFavorDao() {
		if (mSqlFavorDao == null) {
			mSqlFavorDao = new SqlFavorDao();
		}
		return mSqlFavorDao; 
	}

	@Override
	public Program cursorRowToObject(Cursor cur) {
		Program c = new Program();
		c.id = cur.getString(cur.getColumnIndex("id"));
		c.picUrl = cur.getString(cur.getColumnIndex("picUrl"));
		c.name = cur.getString(cur.getColumnIndex("name"));
		c.rateType = cur.getInt(cur.getColumnIndex("rateType"));
		c.isFavor = cur.getInt(cur.getColumnIndex("isfavor")) == 1;
		
		return c;
	}
	
	public static List<Program> getFavorList() {
		return getSqlFavorDao().queryForList(null, "isfavor='1'", null, null, null, "date desc");
	}
	
	/**
	 * 增加favor，最多200条
	 * @param p
	 * @return
	 */
	public static boolean updateFavor(Program p) {
		if (p.isFavor) {
			int count = getSqlFavorDao().getDataCount();
//			Log.i("aabb", "updateFavor -- count:" + count);
			//delete from favor where id in (select id from favor order by id limit 0,2);
			if (count >= Const.MAX_SIZE_FAVOR) {// 已达到上限，一次删除6个
				long relt = getSqlFavorDao().delete("date in (select date from favor order by date limit 0,6)");
//				long relt = getSqlFavorDao().delete("date in (select min(date) from favor)");
//				Log.i("aabb", "updateFavor -- delete result:" + relt);
			} 
			
			ContentValues cv = new ContentValues();
			cv.put("id", p.id);
			cv.put("picUrl", p.picUrl);
			cv.put("name", p.name);
			cv.put("rateType", p.rateType);
			cv.put("isfavor", p.isFavor ? 1 : 0);
			cv.put("date", System.currentTimeMillis());
			return getSqlFavorDao().replace(cv) != -1;
		} else {
			return getSqlFavorDao().delete("id=?", new String[] { p.id }) != -1;
		}
	}
	
	public static boolean getFavor(String id) {
		Program result = getSqlFavorDao().queryForObject(null, "id=?", new String[] { id }, null, null, null);
		if (result != null) {
			return result.isFavor;
		}
		return false;
	}
	
	public static boolean deleteById(String id) {
		long s = getSqlFavorDao().delete( "id=?", new String[] { id });
		return s != 0;
	}
	
}
