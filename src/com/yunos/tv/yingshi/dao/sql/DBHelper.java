package com.yunos.tv.yingshi.dao.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.aliyun.base.database.SQLiteHelper;

/**
 * a200: 第一次发布dbversion是19，第二次发布时dbversion为21
 * @author leiming32
 *
 */

public class DBHelper extends SQLiteHelper {

	public static final String DATABASE_NAME = "yunostv.db";

	public static final int VERSION = 21;

	public DBHelper(Context context) {
		this(context, null);
	}

	public DBHelper(Context context, CursorFactory factory) {
		super(context, DATABASE_NAME, factory, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS 'favor' ('id' TEXT PRIMARY KEY, "
				+ "'picUrl' TEXT, 'name' TEXT, 'rateType' INTEGER, 'isfavor' INTEGER, 'date' INTEGER);");

		db.execSQL("CREATE TABLE IF NOT EXISTS 'lastplay' ('id' TEXT, 'name' TEXT, 'picUrl' TEXT,"
				+ "'showType' INTEGER,'lastPlayTagName' TEXT, 'lastPlayFileName' TEXT, 'lastPlayTime' INTEGER, "
				+ "'isJuheVideo' INTEGER, 'classId' TEXT, 'date' INTEGER, PRIMARY KEY('id', 'classId'));");

		db.execSQL("CREATE TABLE IF NOT EXISTS 'catalog' ('id' TEXT, 'name' TEXT, 'picUrl' TEXT,"
				+ "'hasProgram' INTEGER, 'type' INTEGER, 'parent' TEXT, PRIMARY KEY('id', 'type'));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if (oldVersion < 19) {
			db.execSQL("DROP TABLE IF EXISTS 'favor'");
			db.execSQL("DROP TABLE IF EXISTS 'lastplay'");
			db.execSQL("DROP TABLE IF EXISTS 'catalog'");

			onCreate(db);
		} else if (oldVersion == 19) {
			//已车撤销数据库的ppvPath的修改。
			return;
		}
	}
}
