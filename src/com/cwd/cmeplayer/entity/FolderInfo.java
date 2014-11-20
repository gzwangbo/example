package com.cwd.cmeplayer.entity;

import java.util.List;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�����ļ��ж�Ӧ�ĸ�����Ϣ</b></br>
 * 
 * @author CWD
 * @version 2013.05.19 v1.0 �����ļ��и����б�<br>
 *          2013.08.06 v2.0 �������󣬴������趨�����б�</br>
 */
public class FolderInfo {

	private String musicFolder;// ���������ļ���
	private List<MusicInfo> musicList;// �����б�

	/**
	 * ����ļ���·����
	 * 
	 * @return �ļ���·����
	 */
	public String getMusicFolder() {
		return musicFolder;
	}

	/**
	 * �����ļ���·����
	 * 
	 * @param musicFolder
	 *            �ļ���·����
	 */
	public void setMusicFolder(String musicFolder) {
		this.musicFolder = musicFolder;
	}

	/**
	 * ����ļ����µĸ����б�
	 * 
	 * @return �����б�
	 */
	public List<MusicInfo> getMusicList() {
		return musicList;
	}

	/**
	 * �����ļ����¸����б�
	 * 
	 * @param musicList
	 *            �����б�
	 */
	public void setMusicList(List<MusicInfo> musicList) {
		this.musicList = musicList;
	}

}
