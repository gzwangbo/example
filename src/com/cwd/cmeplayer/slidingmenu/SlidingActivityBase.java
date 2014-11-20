package com.cwd.cmeplayer.slidingmenu;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�������壬��һ��ʵ�ֵĽϼ򵥣�Ҳ����ȫ������ֲ��������󲻵ò�����ɾ�������޸�
 * ��Դ�����ԣ�http://www.eoeandroid.com/forum.php?mod=viewthread&tid=271752
 * 
 * ���ڶ���Ч�����Ǻ����룬�����ҵ��˸��õ����ӣ� Ч������
 * Դ�����ԣ�http://www.eoeandroid.com/thread-278959-1-1.html
 * ԭ����Դ�룺http://www.eoeandroid.com/forum.php?mod=viewthread
 * &tid=262666&reltid=262043&pre_thread_id=271752&pre_pos=2&ext=
 * 
 * ���ڱ�����ֻ��ҪSlidingListActivity�������Ķ�������ɾ����</b></br>
 * 
 * @author CWD && Ӧ���ǳ���Kris
 * @version 2013.06.03 v1.0 �޸�ȥ�����ִ���
 */
public interface SlidingActivityBase {

	/**
	 * Set the behind view content to an explicit view. This view is placed
	 * directly into the behind view 's view hierarchy. It can itself be a
	 * complex view hierarchy.
	 * 
	 * @param view
	 *            The desired content to display.
	 * @param layoutParams
	 *            Layout parameters for the view.
	 */
	public void setBehindContentView(View view, LayoutParams layoutParams);

	/**
	 * Set the behind view content to an explicit view. This view is placed
	 * directly into the behind view 's view hierarchy. It can itself be a
	 * complex view hierarchy. When calling this method, the layout parameters
	 * of the specified view are ignored. Both the width and the height of the
	 * view are set by default to MATCH_PARENT. To use your own layout
	 * parameters, invoke setContentView(android.view.View,
	 * android.view.ViewGroup.LayoutParams) instead.
	 * 
	 * @param view
	 *            The desired content to display.
	 */
	public void setBehindContentView(View view);

	/**
	 * Set the behind view content from a layout resource. The resource will be
	 * inflated, adding all top-level views to the behind view.
	 * 
	 * @param layoutResID
	 *            Resource ID to be inflated.
	 */
	public void setBehindContentView(int layoutResID);

	/**
	 * Gets the SlidingMenu associated with this activity.
	 * 
	 * @return the SlidingMenu associated with this activity.
	 */
	public SlidingMenu getSlidingMenu();

	/**
	 * Toggle the SlidingMenu. If it is open, it will be closed, and vice versa.
	 */
	public void toggle();

	/**
	 * Close the SlidingMenu and show the content view.
	 */
	public void showContent();

	/**
	 * Open the SlidingMenu and show the menu view.
	 */
	public void showMenu();

	/**
	 * Open the SlidingMenu and show the secondary (right) menu view. Will
	 * default to the regular menu if there is only one.
	 */
	public void showSecondaryMenu();

	/**
	 * Controls whether the ActionBar slides along with the above view when the
	 * menu is opened, or if it stays in place.
	 * 
	 * @param slidingActionBarEnabled
	 *            True if you want the ActionBar to slide along with the
	 *            SlidingMenu, false if you want the ActionBar to stay in place
	 */
	public void setSlidingActionBarEnabled(boolean slidingActionBarEnabled);

}
