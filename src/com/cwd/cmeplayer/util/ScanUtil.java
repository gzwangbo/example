package com.cwd.cmeplayer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.cwd.cmeplayer.db.DBDao;
import com.cwd.cmeplayer.entity.FolderInfo;
import com.cwd.cmeplayer.entity.MusicInfo;
import com.cwd.cmeplayer.entity.ScanInfo;
import com.cwd.cmeplayer.list.FolderList;
import com.cwd.cmeplayer.list.LyricList;
import com.cwd.cmeplayer.list.MusicList;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>ɨ�������������ɨ�����ݿ����SD���ĸ����ļ�(��ʱ��������ʹ���첽�߳�ִ��)��
 * ������һ��ѭ����ͬʱ�жϳ�������Ӧ�ĸ���Ѷ�̫��ֻ���½����ݿ�����ֱ�洢</b></br>
 * 
 * 
 * @author CWD
 * @version 2013.05.18 v1.0 ʵ��ɨ��SD�������ݿ����Ӽ���ѯ����<br>
 *          2013.06.15 v2.0 ʵ�ֻ�ȡ��ϸmp3��ǩ���޸����ݿ��¼��Ϣ<br>
 *          2013.06.16 v2.1 �������ݿ��ѯ������bug<br>
 *          2013.06.23 v2.2 �����Ը�ʵ�ɨ��<br>
 *          2013.08.05 v2.3 ������ר�����Ƶ�ɨ�裬�޸�ɨ����ֵļ�������<br>
 *          2013.08.05 v2.4 ����ɨ���ļ��и����б��һ������<br>
 *          2013.08.29 v2.5 ���������ɨ�豨������⣬ԭ����·����ȫ�����Сд���¿�ָ��<br>
 *          2013.08.30 v2.6 �������ɨ����ļ����б��ظ�������</br>
 */
public class ScanUtil {

	private Context context;
	private DBDao db;

	public ScanUtil(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * ��ѯ����ý�������Ŀ¼��ȱ����Ӱ��һ��Ч�ʣ�û���ҵ�ֱ���ṩý���Ŀ¼�ķ���
	 */
	public List<ScanInfo> searchAllDirectory() {
		List<ScanInfo> list = new ArrayList<ScanInfo>();
		StringBuffer sb = new StringBuffer();
		String[] projection = { MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DATA };
		Cursor cr = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, MediaStore.Audio.Media.DISPLAY_NAME);
		String displayName = null;
		String data = null;
		while (cr.moveToNext()) {
			displayName = cr.getString(cr
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			data = cr.getString(cr.getColumnIndex(MediaStore.Audio.Media.DATA));
			data = data.replace(displayName, "");// �滻�ļ�������������һ��Ŀ¼
			if (!sb.toString().contains(data)) {
				list.add(new ScanInfo(data, true));
				sb.append(data);
			}
		}
		cr.close();
		return list;
	}

	/**
	 * ɨ��SD�����֣�¼�����ݿⲢ��������б�ȱ���Ǽ���ϵͳý���û�и���ý���Ŀ¼��ɨ�費��
	 * 
	 * @param scanList
	 *            ����ý�������Ŀ¼
	 */
	public void scanMusicFromSD(List<String> folderList, Handler handler) {
		int count = 0;// ͳ��������
		db = new DBDao(context);
		db.deleteLyric();// ��������Ƿ��Ѿ������жϣ�ȫ��ɾ��������ɨ��
		final int size = folderList.size();
		for (int i = 0; i < size; i++) {
			final String folder = folderList.get(i);
			File file[] = new File(folder).listFiles();
			if (file == null) {
				continue;
			}
			FolderInfo folderInfo = new FolderInfo();
			List<MusicInfo> listInfo = new ArrayList<MusicInfo>();
			for (File temp : file) {
				// ���ļ��ű��棬���滹���ļ��еģ��Ǿ����˰�...
				if (temp.isFile()) {
					String fileName = temp.getName();
					final String path = temp.getPath();
					final String end = fileName.substring(
							fileName.lastIndexOf(".") + 1, fileName.length());
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					// ��¼������Ϣ
					if (end.equalsIgnoreCase("mp3")) {// �����ִ�Сд
						// ��ѯ���������¼
						if (!db.queryExist(fileName, folder)) {
							MusicInfo musicInfo = scanMusicTag(fileName, path);
							// ��һ��ɨ����ϲ���϶�Ϊfalse
							db.add(fileName, musicInfo.getName(), path, folder,
									false, musicInfo.getTime(),
									musicInfo.getSize(), musicInfo.getArtist(),
									musicInfo.getFormat(),
									musicInfo.getAlbum(), musicInfo.getYears(),
									musicInfo.getChannels(),
									musicInfo.getGenre(), musicInfo.getKbps(),
									musicInfo.getHz());
							// �������и����б�
							MusicList.list.add(musicInfo);
							// �����ļ�����ʱ�б�
							listInfo.add(musicInfo);
							count++;
						}
						if (handler != null) {
							Message msg = handler.obtainMessage();
							msg.obj = fileName;
							// Message��handler��ȡ������ֱ�����handler��������Ϣ
							msg.sendToTarget();
						}
					}
					// ��¼�����Ϣ(ֻʶ��LRC���)
					if (end.equalsIgnoreCase("lrc")) {// �����ִ�Сд
						db.addLyric(fileName, path);
						LyricList.map.put(fileName, path);
					}
				}
			}
			if (listInfo.size() > 0) {
				boolean exists = false;
				for (int j = 0; j < FolderList.list.size(); j++) {
					// ���Աȣ�����ͬ��·�����ж��������ͺϲ���û��ֱ����ӡ��˷����Ƚϱ������϶�Ӱ��Ч�ʵ�...
					if (folder.equals(FolderList.list.get(j).getMusicFolder())) {
						// ��ɨ�赽�����ø����ͺϲ��б�
						FolderList.list.get(j).getMusicList().addAll(listInfo);
						exists = true;
						break;// ����ѭ��
					}
				}
				if (!exists) {// ������ͬ��·��������
					// �����ļ����б��ļ���·��
					folderInfo.setMusicFolder(folder);
					// �����ļ����б������Ϣ
					folderInfo.setMusicList(listInfo);
					// �����ļ����б�
					FolderList.list.add(folderInfo);
				}
			}
		}
		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.obj = "ɨ����ɣ���������" + count + "��";
			// Message��handler��ȡ������ֱ�����handler��������Ϣ
			msg.sendToTarget();
		}
		db.close();
	}

	/**
	 * �������ݿ��¼�����и���
	 */
	public void scanMusicFromDB() {
		db = new DBDao(context);
		db.queryAll(searchAllDirectory());
		db.close();
	}

	/**
	 * �ؼ�һ������ȡMP3��ϸ��Ϣ��������������֡�������֮���
	 * 
	 * @param path
	 *            �ļ�·��
	 */
	private MusicInfo scanMusicTag(String fileName, String path) {
		File file = new File(path);
		MusicInfo info = new MusicInfo();

		if (file.exists()) {
			try {
				MP3File mp3File = (MP3File) AudioFileIO.read(file);
				MP3AudioHeader header = mp3File.getMP3AudioHeader();

				info.setFile(fileName);
				// ʱ��(�˴�������MediaPlayer��ó��Ȳ�һ�£�������)
				info.setTime(FormatUtil.formatTime((int) (header
						.getTrackLength() * 1000)));
				info.setSize(FormatUtil.formatSize(file.length()));// ��С
				info.setPath(path);// ·��
				info.setFormat("��ʽ: " + header.getEncodingType());// ��ʽ(��������)
				final String channels = header.getChannels();
				if (channels.equals("Joint Stereo")) {
					info.setChannels("����: ������");
				} else {
					info.setChannels("����: " + header.getChannels());// ����
				}
				info.setKbps("������: " + header.getBitRate() + "Kbps");// ������
				info.setHz("������: " + header.getSampleRate() + "Hz");// ������

				if (mp3File.hasID3v1Tag()) {
					Tag tag = mp3File.getTag();
					try {
						final String tempName = tag.getFirst(FieldKey.TITLE);
						if (tempName == null || tempName.equals("")) {
							info.setName(fileName);// ɨ�費�����ļ���
						} else {
							info.setName(FormatUtil.formatGBKStr(tempName));// ����
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setName(fileName);// ɨ�������ļ���
					}

					try {
						final String tempArtist = tag.getFirst(FieldKey.ARTIST);
						if (tempArtist == null || tempArtist.equals("")) {
							info.setArtist("δ֪������");
						} else {
							info.setArtist(FormatUtil.formatGBKStr(tempArtist));// ������
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setArtist("δ֪������");
					}

					try {
						final String tempAlbum = tag.getFirst(FieldKey.ALBUM);
						if (tempAlbum == null || tempAlbum.equals("")) {
							info.setAlbum("ר��: δ֪");
						} else {
							info.setAlbum("ר��: "
									+ FormatUtil.formatGBKStr(tempAlbum));// ר��
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setAlbum("ר��: δ֪");
					}

					try {
						final String tempYears = tag.getFirst(FieldKey.YEAR);
						if (tempYears == null || tempYears.equals("")) {
							info.setYears("���: δ֪");
						} else {
							info.setYears("���: "
									+ FormatUtil.formatGBKStr(tempYears));// ���
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setYears("���: δ֪");
					}

					try {
						final String tempGener = tag.getFirst(FieldKey.GENRE);
						if (tempGener == null || tempGener.equals("")) {
							info.setGenre("���: δ֪");
						} else {
							info.setGenre("���: "
									+ FormatUtil.formatGBKStr(tempGener));// ���
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setGenre("���: δ֪");
					}
				} else if (mp3File.hasID3v2Tag()) {// ����������������������ִ������ķ���
					AbstractID3v2Tag v2Tag = mp3File.getID3v2Tag();
					try {
						final String tempName = v2Tag.getFirst(FieldKey.TITLE);
						if (tempName == null || tempName.equals("")) {
							info.setName(fileName);// ɨ�費�����ļ���
						} else {
							info.setName(FormatUtil.formatGBKStr(tempName));// ����
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setName(fileName);// ɨ�������ļ���
					}

					try {
						final String tempArtist = v2Tag
								.getFirst(FieldKey.ARTIST);
						if (tempArtist == null || tempArtist.equals("")) {
							info.setArtist("δ֪������");
						} else {
							info.setArtist(FormatUtil.formatGBKStr(tempArtist));// ������
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setArtist("δ֪������");
					}

					try {
						final String tempAlbum = v2Tag.getFirst(FieldKey.ALBUM);
						if (tempAlbum == null || tempAlbum.equals("")) {
							info.setAlbum("ר��: δ֪");
						} else {
							info.setAlbum("ר��: "
									+ FormatUtil.formatGBKStr(tempAlbum));// ר��
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setAlbum("ר��: δ֪");
					}

					try {
						final String tempYears = v2Tag.getFirst(FieldKey.YEAR);
						if (tempYears == null || tempYears.equals("")) {
							info.setYears("���: δ֪");
						} else {
							info.setYears("���: "
									+ FormatUtil.formatGBKStr(tempYears));// ���
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setYears("���: δ֪");
					}

					try {
						final String tempGener = v2Tag.getFirst(FieldKey.GENRE);
						if (tempGener == null || tempGener.equals("")) {
							info.setGenre("���: δ֪");
						} else {
							info.setGenre("���: "
									+ FormatUtil.formatGBKStr(tempGener));// ���
						}
					} catch (KeyNotFoundException e) {
						// TODO Auto-generated catch block
						info.setGenre("���: δ֪");
					}
				} else {
					info.setName(fileName);
					info.setArtist("δ֪������");
					info.setAlbum("ר��: δ֪");
					info.setYears("���: δ֪");
					info.setGenre("���: δ֪");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return info;
	}

}
