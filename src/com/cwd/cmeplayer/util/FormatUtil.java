package com.cwd.cmeplayer.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>����һЩת������</b></br>
 * 
 * @author CWD
 * @version 2013.05.18 v1.0
 */
public class FormatUtil {

	/**
	 * ��ʽ��ʱ��
	 * 
	 * @param time
	 *            ʱ��ֵ
	 * @return ʱ��
	 */
	public static String formatTime(int time) {
		// TODO Auto-generated method stub
		if (time == 0) {
			return "00:00";
		}
		time = time / 1000;
		int m = time / 60 % 60;
		int s = time % 60;
		return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
	}

	/**
	 * ��ʽ���ļ���С
	 * 
	 * @param size
	 *            �ļ���Сֵ
	 * @return �ļ���С
	 */
	public static String formatSize(long size) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSize = "0B";
		if (size < 1024) {
			fileSize = df.format((double) size) + "B";
		} else if (size < 1048576) {
			fileSize = df.format((double) size / 1024) + "KB";
		} else if (size < 1073741824) {
			fileSize = df.format((double) size / 1048576) + "MB";
		} else {
			fileSize = df.format((double) size / 1073741824) + "GB";
		}
		return fileSize;
	}

	/**
	 * ������Ĵ���
	 * 
	 * @param s
	 *            ԭ�ַ���
	 * @return GBK����������
	 */
	public static String formatGBKStr(String s) {
		String str = null;
		try {
			str = new String(s.getBytes("ISO-8859-1"), "GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

}
