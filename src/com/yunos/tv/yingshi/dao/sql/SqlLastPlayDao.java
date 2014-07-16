package com.yunos.tv.yingshi.dao.sql;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.yunos.tv.source.entity.Program;

/**
 * 对于聚合视频，不同的视频源，电视剧集数都不一样的。
 * @author leiming32
 *
 */
public class SqlLastPlayDao extends BaseSqlDao<Program> {

	public static final String TABLE_NAME = "lastplay";
	
	private SqlLastPlayDao() {
		super(TABLE_NAME);
	}
	 
	public static SqlLastPlayDao mLastPlayDao;

	public static SqlLastPlayDao getLastPlayDao() {
		if (mLastPlayDao == null) {
			mLastPlayDao = new SqlLastPlayDao();
		}
		return mLastPlayDao;
	}

	@Override
	public Program cursorRowToObject(Cursor cur) {
		Program c = new Program();
		c.id = cur.getString(cur.getColumnIndex("id"));
		c.name = cur.getString(cur.getColumnIndex("name"));
		c.picUrl = cur.getString(cur.getColumnIndex("picUrl"));
		c.showType = cur.getInt(cur.getColumnIndex("showType"));
		c.isJuheVideo = cur.getInt(cur.getColumnIndex("isJuheVideo")) == 1;
		c.classId = cur.getString(cur.getColumnIndex("classId"));
		
		c.lastPlayTagName = cur.getString(cur.getColumnIndex("lastPlayTagName"));
		c.lastPlayFileName = cur.getString(cur.getColumnIndex("lastPlayFileName"));
		c.lastPlayTime = cur.getInt(cur.getColumnIndex("lastPlayTime"));
		return c;
	}
	
	/**
	 * 取最后播放列表
	 * @param count  数量，若传-1则返回所有
	 * @return
	 */
	public static List<Program> getLastPlayList(int count) {
		if (count > -1) {
			return getLastPlayDao().queryForList(null, null, null, null, null, "date desc limit 0," + count);
		}
		return getLastPlayDao().queryForList(null, null, null, null, null, "date desc");
	} 
	
	/**
	 * 往数据库里添加或替换
	 * @param p
	 */
	public static void addLastPlayProgram(Program p) {
		if (p.isJuheVideo) {
			ContentValues cv = new ContentValues();
			
			cv.put("id", p.id);
			cv.put("name", p.name);
			cv.put("picUrl", p.picUrl);
			cv.put("showType", p.showType);
			cv.put("isJuheVideo", 1);
			cv.put("classId", p.classId);
			
			cv.put("lastPlayTagName", p.lastPlayTagName);//上次播放tag名字
			cv.put("lastPlayFileName", p.lastPlayFileName);//上次播放fileName
			cv.put("lastPlayTime", p.lastPlayTime);//上次播放时间
			
			cv.put("date", System.currentTimeMillis());
			
			getLastPlayDao().replace(cv);
		} else {
			ContentValues cv = new ContentValues();
			
			cv.put("id", p.id);
			cv.put("name", p.name);
			cv.put("picUrl", p.picUrl);
			cv.put("showType", p.showType);
			cv.put("isJuheVideo",  0);
			cv.put("classId", Program.DEFAULT_CLASSID);
			
			cv.put("lastPlayTagName", p.lastPlayTagName);//上次播放tag名字
			cv.put("lastPlayFileName", p.lastPlayFileName);//上次播放fileName
			cv.put("lastPlayTime", p.lastPlayTime);//上次播放时间
			
			cv.put("date", System.currentTimeMillis());
			
			getLastPlayDao().replace(cv);
		}
	}
	
	public static void updateLastPlaytime(Program p) {
//		Program lp = getLastPlayProgram(p.id);
//		/*
//		 *　对资讯来说，数据库里没有，不要更新。
//		 */
//		if (lp == null) {
//			return;
//		}
		ContentValues cv = new ContentValues();
		
		if (p.isJuheVideo) {
			if (p.classId.equals("M")) {
				cv.put("lastPlayFileName", p.lastPlayFileName);//上次播放fileName
			}
			cv.put("lastPlayTime", p.lastPlayTime);//上次播放时间
			getLastPlayDao().update(cv, "id=? AND classId=?", new String[] { p.id, p.classId });
		} else {
			/*
			 *对电影来说，播放完毕后传过来的series为1
			 *而第一次插入记录的时候 lastPlayFileName是file_name字段。播放完毕后更新就把lastPlayFileName覆盖了。
			 * 只对电视剧进行lastPlayFileName的更新。
			 */
			if (p.showType == 3) {
				cv.put("lastPlayFileName", p.lastPlayFileName);//上次播放fileName
			}
			cv.put("lastPlayTime", p.lastPlayTime);//上次播放时间
			getLastPlayDao().update(cv, "id=? AND classId=?", new String[] { p.id, Program.DEFAULT_CLASSID });
		}
	}
	
	/**
	 * get the last play program
	 * @param programId
	 * @return null if not found
	 */
	public static Program getLastPlayProgram(String id) {
		return getLastPlayDao().queryForObject(null,"id=? AND classId=?", new String[] { id, Program.DEFAULT_CLASSID }, null, null, null);
	}
	
	public static boolean deleteById(String id, String classId) {
		long s = getLastPlayDao().delete( "id=? AND classId=?", new String[] { id, classId });
		return s != 0; 
	} 
	
	public static boolean deleteById(String id) {
		return deleteById(id, Program.DEFAULT_CLASSID);
	} 
	
}
