package com.cwd.cmeplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�Ա�Ĳ���</b></br>
 * 
 * @author CWD
 * @version 2013.05.19 v1.0 �������ݿ�<br>
 *          2013.06.16 v1.1 �������ݿ⼸���ֶ�<br>
 *          2013.06.23 v1.2 ������ʱ�2���ֶ�<br>
 *          2013.06.05 v1.3 ����ר���ֶ�</br>
 */
public class DBHelper extends SQLiteOpenHelper {

	/**
	 * �������ݿ�
	 * 
	 * @param context
	 *            ������
	 */
	public DBHelper(Context context) {
		super(context, DBData.MUSIC_DB_NAME, null, DBData.MUSIC_DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// �������ֱ�
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBData.MUSIC_TABLENAME
				+ " (" + DBData.MUSIC_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DBData.MUSIC_FILE
				+ " NVARCHAR(100), " + DBData.MUSIC_NAME + " NVARCHAR(100), "
				+ DBData.MUSIC_PATH + " NVARCHAR(300), " + DBData.MUSIC_FOLDER
				+ " NVARCHAR(300), " + DBData.MUSIC_FAVORITE + " INTEGER, "
				+ DBData.MUSIC_TIME + " NVARCHAR(100), " + DBData.MUSIC_SIZE
				+ " NVARCHAR(100), " + DBData.MUSIC_ARTIST + " NVARCHAR(100), "
				+ DBData.MUSIC_FORMAT + " NVARCHAR(100), " + DBData.MUSIC_ALBUM
				+ " NVARCHAR(100), " + DBData.MUSIC_YEARS + " NVARCHAR(100), "
				+ DBData.MUSIC_CHANNELS + " NVARCHAR(100), "
				+ DBData.MUSIC_GENRE + " NVARCHAR(100), " + DBData.MUSIC_KBPS
				+ " NVARCHAR(100), " + DBData.MUSIC_HZ + " NVARCHAR(100))");
		// ������ʱ�
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBData.LYRIC_TABLENAME
				+ " (" + DBData.LYRIC_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DBData.LYRIC_FILE
				+ " NVARCHAR(100), " + DBData.LYRIC_PATH + " NVARCHAR(300))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DBData.MUSIC_TABLENAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBData.LYRIC_TABLENAME);
	}

}
