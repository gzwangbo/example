package com.cwd.cmeplayer.entity;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>������ϸ��Ϣ</b></br>
 * 
 * @author CWD
 * @version 2013.05.18 v1.0 �ݶ�10����ϸ��Ϣ <br>
 *          2013.06.15 v1.1 �������������ȣ�ɾ����ǩ��<br>
 *          2013.06.16 v1.2 �����ļ�����ǩ�����ڸ�������Ƿ�����ж�<br>
 *          2013.07.07 v1.3 ����audioSessionId��mp3Duration<br>
 *          2013.08.05 v1.4 ����ר��<br>
 *          2013.08.06 v1.5 ���밴��ĸ����ӿ�</br>
 */
public class MusicInfo implements Comparator<Object> {

	private String file;// �ļ���(ͨ���ļ�������ΪΨһ�жϲ��һ�ø��)
	private String time;// ʱ��
	private String size;// ��С
	private String name;// ����
	private String artist;// ������
	private String path;// ·��
	private String format;// ��ʽ(��������)
	private String album;// ר��
	private String years;// ���
	private String channels;// ����
	private String genre;// ���
	private String kbps;// ������
	private String hz;// ������

	private int audioSessionId;// ��Ƶ�ỰID
	private int mp3Duration;// ��ȷ������ʱ��(����SeekBar�ܳ���)

	private boolean favorite;// �Ƿ��

	private Collator collator;

	public MusicInfo() {
		// TODO Auto-generated constructor stub
		collator = Collator.getInstance();
	}

	/**
	 * ����ļ���(ͨ���ļ�������ΪΨһ�жϲ��һ�ø��)
	 * 
	 * @return �ļ���
	 */
	public String getFile() {
		return file;
	}

	/**
	 * �����ļ���(ͨ���ļ�������ΪΨһ�жϲ��һ�ø��)
	 * 
	 * @param file
	 *            �ļ���
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * ���ʱ��
	 * 
	 * @return ʱ��
	 */
	public String getTime() {
		return time;
	}

	/**
	 * ����ʱ��
	 * 
	 * @param time
	 *            ʱ��
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * ��ô�С
	 * 
	 * @return ��С
	 */
	public String getSize() {
		return size;
	}

	/**
	 * ���ô�С
	 * 
	 * @param size
	 *            ��С
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * ��ø���
	 * 
	 * @return ����
	 */
	public String getName() {
		return name;
	}

	/**
	 * ���ø���
	 * 
	 * @param name
	 *            ����
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ���������
	 * 
	 * @return ������
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * ����������
	 * 
	 * @param artist
	 *            ������
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * ���·��
	 * 
	 * @return ·��
	 */
	public String getPath() {
		return path;
	}

	/**
	 * ����·��
	 * 
	 * @param path
	 *            ·��
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * ��ø�ʽ(��������)
	 * 
	 * @return ��ʽ(��������)
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * ���ø�ʽ(��������)
	 * 
	 * @param format
	 *            ��ʽ(��������)
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * ���ר��
	 * 
	 * @return ר��
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * ����ר��
	 * 
	 * @param album
	 *            ר��
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * ������
	 * 
	 * @return ���
	 */
	public String getYears() {
		return years;
	}

	/**
	 * �������
	 * 
	 * @param years
	 *            ���
	 */
	public void setYears(String years) {
		this.years = years;
	}

	/**
	 * �������
	 * 
	 * @return ����(һ�������������)
	 */
	public String getChannels() {
		return channels;
	}

	/**
	 * ��������
	 * 
	 * @param channels
	 *            ����(һ�������������)
	 */
	public void setChannels(String channels) {
		this.channels = channels;
	}

	/**
	 * ��÷��
	 * 
	 * @return ���
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * ���÷��
	 * 
	 * @param genre
	 *            ���
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * ��ñ�����
	 * 
	 * @return ������
	 */
	public String getKbps() {
		return kbps;
	}

	/**
	 * ���ñ�����
	 * 
	 * @param kbps
	 *            ������
	 */
	public void setKbps(String kbps) {
		this.kbps = kbps;
	}

	/**
	 * ��ò�����
	 * 
	 * @return ������
	 */
	public String getHz() {
		return hz;
	}

	/**
	 * ���ò�����
	 * 
	 * @param hz
	 *            ������
	 */
	public void setHz(String hz) {
		this.hz = hz;
	}

	/**
	 * ���audioSessionId
	 * 
	 * @return MediaPlayer.getAudioSessionId()
	 */
	public int getAudioSessionId() {
		return audioSessionId;
	}

	/**
	 * ����audioSessionId
	 * 
	 * @param audioSessionId
	 *            MediaPlayer.getAudioSessionId()
	 */
	public void setAudioSessionId(int audioSessionId) {
		this.audioSessionId = audioSessionId;
	}

	/**
	 * ��þ�ȷ������ʱ��
	 * 
	 * @return ����ʱ��
	 */
	public int getMp3Duration() {
		return mp3Duration;
	}

	/**
	 * ���þ�ȷ������ʱ��
	 * 
	 * @param mp3Duration
	 *            ����ʱ��
	 */
	public void setMp3Duration(int mp3Duration) {
		this.mp3Duration = mp3Duration;
	}

	/**
	 * �Ƿ��
	 * 
	 * @return true||false
	 */
	public boolean isFavorite() {
		return favorite;
	}

	/**
	 * �����
	 * 
	 * @param favorite
	 *            true||false
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@Override
	public int compare(Object object1, Object object2) {
		// TODO Auto-generated method stub
		// ���ַ���ת��Ϊһϵ�б��أ����ǿ����Ա�����ʽ�� CollationKeys��Ƚ�
		// Ҫ�벻���ִ�Сд���бȽ���o1.toString().toLowerCase()
		CollationKey key1 = collator.getCollationKey(object1.toString());
		CollationKey key2 = collator.getCollationKey(object2.toString());
		// ���صķֱ�Ϊ1,0,-1�ֱ������ڣ����ڣ�С�ڡ�Ҫ�밴����ĸ��������Ļ��Ӹ���-����
		return key1.compareTo(key2);
	}

}
