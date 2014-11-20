package com.cwd.cmeplayer.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cwd.cmeplayer.entity.FolderInfo;
import com.cwd.cmeplayer.entity.MusicInfo;
import com.cwd.cmeplayer.entity.ScanInfo;
import com.cwd.cmeplayer.list.FavoriteList;
import com.cwd.cmeplayer.list.FolderList;
import com.cwd.cmeplayer.list.LyricList;
import com.cwd.cmeplayer.list.MusicList;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�����ݿ����ɾ��ĵ�</b></br>
 * 
 * @author CWD
 * @version 2013.05.19 v1.0 ʵ�������顢�ġ��Ƿ����<br>
 *          2013.06.16 v1.1 ʵ�������������ָ�Ϊ����ļ�¼�����²�ѯ����<br>
 *          2013.06.23 v1.2 ʵ�������������ָ����Ϣ�ļ�¼�������ʲ�ѯ����<br>
 *          2013.08.04 v1.3 ʵ�ֵ������ֵ����ݿ�ɾ��<br>
 *          2013.08.05 v1.4 �����������ֵ�ר����Ϣ��ǰ�澹Ȼ©������ô��Ҫ������...<br>
 *          2013.08.05 v1.5 ����ɨ���ļ��и����б��һ������<br>
 *          2013.08.29 v1.6 ���������ɨ�豨������⣬ԭ�����ļ����к���'��SQL����ѯ����</br>
 */
public class DBDao {

	private DBHelper helper;
	private SQLiteDatabase db;

	/**
	 * �����ͳ�ʼ�����ݿ⣬ʹ����ǵõ���close�����ر����ݿ�
	 * 
	 * @param context
	 */
	public DBDao(Context context) {
		// TODO Auto-generated constructor stub
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	/**
	 * ������������������Ϣ
	 * 
	 * @param fileName
	 *            �ļ���(Ŀ����������ΪΨһ���������ж��Ƿ����)
	 * @param musicName
	 *            ��������
	 * @param musicPath
	 *            ����·��
	 * @param musicFolder
	 *            ���������ļ���
	 * @param isFavorite
	 *            �Ƿ�Ϊ��ϲ������
	 * @param musicTime
	 *            ����ʱ��
	 * @param musicSize
	 *            �����ļ���С
	 * @param musicArtist
	 *            ����������
	 * @param musicFormat
	 *            ���ָ�ʽ(��������)
	 * @param musicAlbum
	 *            ����ר��
	 * @param musicYears
	 *            �������
	 * @param musicChannels
	 *            ��������
	 * @param musicGenre
	 *            ���ַ��
	 * @param musicKbps
	 *            ���ֱ�����
	 * @param musicHz
	 *            ���ֲ�����
	 * @return �����ɹ���������ʧ�ܷ���-1
	 */
	public long add(String fileName, String musicName, String musicPath,
			String musicFolder, boolean isFavorite, String musicTime,
			String musicSize, String musicArtist, String musicFormat,
			String musicAlbum, String musicYears, String musicChannels,
			String musicGenre, String musicKbps, String musicHz) {
		ContentValues values = new ContentValues();
		values.put(DBData.MUSIC_FILE, fileName);
		values.put(DBData.MUSIC_NAME, musicName);
		values.put(DBData.MUSIC_PATH, musicPath);
		values.put(DBData.MUSIC_FOLDER, musicFolder);
		values.put(DBData.MUSIC_FAVORITE, isFavorite ? 1 : 0);// ���ݿⶨ���ֶ�����Ϊ����
		values.put(DBData.MUSIC_TIME, musicTime);
		values.put(DBData.MUSIC_SIZE, musicSize);
		values.put(DBData.MUSIC_ARTIST, musicArtist);
		values.put(DBData.MUSIC_FORMAT, musicFormat);
		values.put(DBData.MUSIC_ALBUM, musicAlbum);
		values.put(DBData.MUSIC_YEARS, musicYears);
		values.put(DBData.MUSIC_CHANNELS, musicChannels);
		values.put(DBData.MUSIC_GENRE, musicGenre);
		values.put(DBData.MUSIC_KBPS, musicKbps);
		values.put(DBData.MUSIC_HZ, musicHz);
		long result = db.insert(DBData.MUSIC_TABLENAME, DBData.MUSIC_FILE,
				values);
		return result;
	}

	/**
	 * �����������ָ����Ϣ
	 * 
	 * @param fileName
	 *            �ļ���(Ŀ����������ΪΨһ���������ж��Ƿ����)
	 * @param lrcPath
	 *            ���·��
	 * @return �����ɹ���������ʧ�ܷ���-1
	 */
	public long addLyric(String fileName, String lrcPath) {
		ContentValues values = new ContentValues();
		values.put(DBData.LYRIC_FILE, fileName);
		values.put(DBData.LYRIC_PATH, lrcPath);
		long result = db.insert(DBData.LYRIC_TABLENAME, DBData.LYRIC_FILE,
				values);
		return result;
	}

	/**
	 * ����������ؼ�¼��ֻ�����û��Ƿ���Ϊ��ϲ������
	 * 
	 * @param musicName
	 *            ��������
	 * @param isFavorite
	 *            �Ƿ�Ϊ��ϲ������(true:1 else false:0)
	 * @return Ӱ�������
	 */
	public int update(String musicName, boolean isFavorite) {
		ContentValues values = new ContentValues();
		values.put(DBData.MUSIC_FAVORITE, isFavorite ? 1 : 0);// ���ݿⶨ���ֶ�����Ϊ����
		int result = db.update(DBData.MUSIC_TABLENAME, values,
				DBData.MUSIC_NAME + "=?", new String[] { musicName });
		return result;
	}

	/**
	 * ��ѯ��Ӧ���������ݿ���Ϣ�Ƿ����
	 * 
	 * ����˴���ҪдSQL��䣬��rawQuery��ѯ����ΪĳЩ�ļ����оʹ���'�����Կ϶�����
	 * 
	 * @param musicName
	 *            ��������
	 * @param musicFolder
	 *            ���������ļ���
	 * @return �Ƿ����
	 */
	public boolean queryExist(String fileName, String musicFolder) {
		boolean isExist = false;
		/*
		 * �������乩��λ�ο��Աȣ�fileName��������'���˴��ر������¾��������SQL����ǻᷢ�������
		 * 
		 * Cursor cursor = db.rawQuery("SELECT * FROM " + DBData.MUSIC_TABLENAME
		 * + " WHERE " + DBData.MUSIC_FILE + "=" + fileName + " AND " +
		 * DBData.MUSIC_FOLDER + "=" + musicFolder + "", null);
		 */
		Cursor cursor = db.query(DBData.MUSIC_TABLENAME, null,
				DBData.MUSIC_FILE + "=? AND " + DBData.MUSIC_FOLDER + "=?",
				new String[] { fileName, musicFolder }, null, null, null);
		if (cursor.getCount() > 0) {
			isExist = true;
		}
		return isExist;
	}

	/**
	 * ��ѯ���ݿⱣ��ĸ�ý���Ŀ¼������������Ϣ�͸��
	 * 
	 * @param scanList
	 *            ����ý�������Ŀ¼
	 */
	public void queryAll(List<ScanInfo> scanList) {
		MusicList.list.clear();
		FolderList.list.clear();
		FavoriteList.list.clear();
		LyricList.map.clear();

		final int listSize = scanList.size();
		Cursor cursor = null;
		// ��ѯ��ý���Ŀ¼������������Ϣ
		for (int i = 0; i < listSize; i++) {
			final String folder = scanList.get(i).getFolderPath();
			cursor = db.rawQuery("SELECT * FROM " + DBData.MUSIC_TABLENAME
					+ " WHERE " + DBData.MUSIC_FOLDER + "='" + folder + "'",
					null);
			List<MusicInfo> listInfo = new ArrayList<MusicInfo>();
			if (cursor != null && cursor.getCount() > 0) {
				FolderInfo folderInfo = new FolderInfo();
				while (cursor.moveToNext()) {
					MusicInfo musicInfo = new MusicInfo();
					final String file = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_FILE));
					final String name = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_NAME));
					final String path = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_PATH));
					final int favorite = cursor.getInt(cursor
							.getColumnIndex(DBData.MUSIC_FAVORITE));
					final String time = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_TIME));
					final String size = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_SIZE));
					final String artist = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_ARTIST));
					final String format = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_FORMAT));
					final String album = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_ALBUM));
					final String years = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_YEARS));
					final String channels = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_CHANNELS));
					final String genre = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_GENRE));
					final String kbps = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_KBPS));
					final String hz = cursor.getString(cursor
							.getColumnIndex(DBData.MUSIC_HZ));

					musicInfo.setFile(file);
					musicInfo.setName(name);
					musicInfo.setPath(path);
					musicInfo.setFavorite(favorite == 1 ? true : false);
					musicInfo.setTime(time);
					musicInfo.setSize(size);
					musicInfo.setArtist(artist);
					musicInfo.setFormat(format);
					musicInfo.setAlbum(album);
					musicInfo.setYears(years);
					musicInfo.setChannels(channels);
					musicInfo.setGenre(genre);
					musicInfo.setKbps(kbps);
					musicInfo.setHz(hz);
					// �������и����б�
					MusicList.list.add(musicInfo);
					// �����ļ�����ʱ�б�
					listInfo.add(musicInfo);
					// �����ҵ���б�
					if (favorite == 1) {
						FavoriteList.list.add(musicInfo);
					}
				}
				// �����ļ����б��ļ���·��
				folderInfo.setMusicFolder(folder);
				// �����ļ����б������Ϣ
				folderInfo.setMusicList(listInfo);
				// �����ļ����б�
				FolderList.list.add(folderInfo);
			}
		}
		// ��ѯ���
		cursor = db.rawQuery("SELECT * FROM " + DBData.LYRIC_TABLENAME, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				final String file = cursor.getString(cursor
						.getColumnIndex(DBData.LYRIC_FILE));
				final String path = cursor.getString(cursor
						.getColumnIndex(DBData.LYRIC_PATH));
				LyricList.map.put(file, path);
			}
		}
		// �ǵùر��α�
		if (cursor != null) {
			cursor.close();
		}
	}

	/**
	 * �����ļ�·����ɾ��������Ϣ
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @return �ɹ�ɾ��������
	 */
	public int delete(String filePath) {
		/*
		 * �����������'�᲻������⣬ɾ���ķ���ֻ�������???
		 */
		int result = db.delete(DBData.MUSIC_TABLENAME, DBData.MUSIC_PATH + "='"
				+ filePath + "'", null);
		return result;
	}

	/**
	 * ɾ�������Ϣ��
	 */
	public void deleteLyric() {
		// ���ܲ����ڸñ���Ҫ���쳣
		try {
			// ��ձ������������
			db.execSQL("delete from " + DBData.LYRIC_TABLENAME + ";");
			db.execSQL("update sqlite_sequence set seq=0 where name='"
					+ DBData.LYRIC_TABLENAME + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ʹ�������ݿ����ر�
	 */
	public void close() {
		db.close();
		db = null;
	}

}
