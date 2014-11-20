package com.cwd.cmeplayer.custom;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�Զ���Ч��-�������϶�̬�������ֶ���(Ϊʵ�ָ�Ч�������˽϶�ʱ��)</b></br>
 * 
 * <br>
 * ���������޸ģ�Դ������http://www.jb51.net/article/37193.htm�����ԭ���Լ�ʵ�ֵ�ScrollTextView��
 * ���Խ�Ϊ��õı���TextView���������ԡ�XML�����ֱ��ʹ��TextView���������ԣ� ֻ�ǲ�����ְ��� ��</br>
 * 
 * 
 * @author CWD
 * @version 2013.07.21 v1.0 ʵ�ֶ�̬ˢ��Ч��
 */
public class PushView extends TextSwitcher implements ViewSwitcher.ViewFactory {

	private int index;
	private int size;

	private AttributeSet attrs;
	private ArrayList<String> arrays;// �ַ�����

	public PushView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public PushView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.attrs = attrs;
		init();
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		// TODO Auto-generated method stub
		index = 0;
		arrays = new ArrayList<String>();
		setFactory(this);
		setInAnimation(animIn());
		setOutAnimation(animOut());
	}

	/**
	 * ���붯��
	 * 
	 * @return TranslateAnimation
	 */
	private Animation animIn() {
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 1.0f,
				Animation.RELATIVE_TO_PARENT, -0.0f);
		anim.setDuration(1500);
		anim.setInterpolator(new LinearInterpolator());
		return anim;
	}

	/**
	 * �뿪����
	 * 
	 * @return TranslateAnimation
	 */
	private Animation animOut() {
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f);
		anim.setDuration(1500);
		anim.setInterpolator(new LinearInterpolator());
		return anim;
	}

	// ���ﷵ�ص�TextView���������ǿ�����View
	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		MarqueeTextView t = new MarqueeTextView(getContext(), attrs);
		t.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
		return t;// XML����ֱ������TextView������ʹ�ã�����LayoutParams���ܴ�ֱ����
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		try {
			removeCallbacks(runnable);// ҳ������ʱ��Ҫ�Ƴ�
			super.onDetachedFromWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �����ַ�����������������Ч��
	 * 
	 * @param texts
	 *            �ַ�����
	 */
	public void setTextList(ArrayList<String> texts) {
		removeCallbacks(runnable);
		this.index = 0;
		this.size = texts.size();
		this.arrays.clear();
		this.arrays = texts;
		setText(null);
		postDelayed(runnable, 500);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			setText(arrays.get(index));
			if (size > 1) {
				index = (index == size - 1) ? 0 : index + 1;
				postDelayed(this, 5000);
			}
		}
	};

	/**
	 * Ŀ��ֻ��һ�����������û�ý���ά�������Ч������ϧ�����ϻ�ִ�������Ч�������������϶�����ʱ�������Ҳ�����ˣ�
	 * ��APIû���ṩ�޸��ӳ�������ʱ��ķ��������Բ���������ʵ���ˣ�������Ч��Ҳ���������⿪��
	 * 
	 * @author CWD
	 * @version 2013.07.18 v1.0
	 */
	private class MarqueeTextView extends TextView {

		public MarqueeTextView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		public MarqueeTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean isFocused() {
			// TODO Auto-generated method stub
			return true;// ���û�ý���
		}
	}

}
