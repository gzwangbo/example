package com.cwd.cmeplayer.entity;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>ɨ����Ϣ��SD��·�����û��Ƿ�ѡ</b></br>
 * 
 * @author CWD
 * @version 2013.05.18 v1.0
 */
public class ScanInfo {

	private String folderPath;// �ļ���·��
	private boolean isChecked;// �û��Ƿ�ѡ

	public ScanInfo(String folderPath, boolean isChecked) {
		// TODO Auto-generated constructor stub
		this.folderPath = folderPath;
		this.isChecked = isChecked;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
