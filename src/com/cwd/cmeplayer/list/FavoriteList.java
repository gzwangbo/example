package com.cwd.cmeplayer.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cwd.cmeplayer.entity.MusicInfo;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>����һ�����õ���ϲ�������б�</b></br>
 * 
 * @author CWD
 * @version 2013.05.19 v1.0 ��������<br>
 *          2013.08.06 v1.1 ��������ĸ���򷽷�</br>
 */
public class FavoriteList {

	public static final List<MusicInfo> list = new ArrayList<MusicInfo>();

	/**
	 * ����ĸ����
	 */
	public static void sort() {
		Collections.sort(list, new MusicInfo());
	}

}
