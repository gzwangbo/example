package com.cwd.cmeplayer.custom;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�Զ���Ч��-�������϶�̬�������ֶ���(�ѷ����������������λ�о��ο�)</b></br>
 * 
 * <br>
 * ����������Ч���Լ�ʵ�֣����Ƿ��ֺܶ�����û��������ر������־������⣬
 * ͨ���Լ�onDraw()�ͻ���ʧ�ܶ�TextView��ԭ�����ԣ�ֻ��ά��һ�ֶ���Ч�������ܴﵽ��һView���õ�Ч�������������
 * �ô����Լ��ܹ�������ƣ����Լ��������޸ġ��ⲿ����setTextList()�Ϳ��Կ�������Ч����</br>
 * 
 * 
 * @author CWD
 * @version 2013.07.10 v1.0 ʵ�ֶ�̬ˢ��Ч��
 */
public class ScrollTextView extends TextView {

	private static final int MSG_FIRST = 0;// ��һ������
	private static final int MSG_START = 1;// ����
	private static final int MSG_UPDATE = 2;// ����
	private static final int TIME_START = 5000;// ������5����ʱ
	private static final int TIME_UPDATE = 10;// ����,0.01����ʱ

	private int i;// ��ǰ��
	private int size;// �б�����

	private float height1;// ��һ��ĸ�
	private float height2;// ��һ��ĸ�
	private float speed;// ���ʣ������ⲿ�޸�
	private float x;// X��λ��
	private float y1;// ��һ���Y��λ��
	private float y2;// �ڶ����Y��λ��

	private boolean isAuto = false;

	private ScrollHandler handler;
	private ArrayList<String> arrays;// �ַ�����

	public ScrollTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public ScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		i = 0;// ��0��ʼ
		speed = 3.0f;// �о���Ϊ���ʵ�����
		arrays = new ArrayList<String>();
		handler = new ScrollHandler(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (size == 0 || !isAuto) {
			return;
		}

		if (y2 <= height1) {
			restart();
		}

		final Paint paint = getPaint();
		canvas.drawText(arrays.get(i), x, y1, paint);// ��һ��
		canvas.drawText(arrays.get((i == size - 1) ? 0 : i + 1), x, y2, paint);// �ڶ���
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		this.height1 = (float) h / 2 + (float) getPaddingTop()
				+ (float) getLineHeight() / 4;// ��Ҳ��֪��Ϊʲô���������������е�
		this.height2 = (float) height1 * 2.5f;// ��΢��0.5�ľ��룬������������β��
		reset();
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		try {
			stop();// ����ʱ��Ҫֹͣ
			super.onDetachedFromWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);

		switch (visibility) {
		case View.VISIBLE:// �ɼ�ʱ�ָ�
			if (size > 0) {
				create();
			}
			break;
		case View.INVISIBLE:// ���ɼ�ʱֹͣ����
			stop();
			break;
		default:
			break;
		}
	}

	/**
	 * ��һ������
	 */
	private void create() {
		stop();
		this.i = 0;
		invalidate();
		if (handler != null && !handler.hasMessages(MSG_FIRST)) {
			handler.sendEmptyMessageDelayed(MSG_FIRST, TIME_START);
			isAuto = true;
		}
	}

	/**
	 * ����
	 */
	private void start() {
		if (handler != null && !handler.hasMessages(MSG_START)) {
			handler.sendEmptyMessageDelayed(MSG_START, TIME_START);
			isAuto = true;
		}
	}

	/**
	 * ��ͣ
	 */
	private void pause() {
		if (handler != null && handler.hasMessages(MSG_UPDATE)) {
			handler.removeMessages(MSG_UPDATE);
		}
	}

	/**
	 * ֹͣ
	 */
	private void stop() {
		if (handler != null) {
			if (handler.hasMessages(MSG_FIRST)) {
				handler.removeMessages(MSG_FIRST);
			}
			if (handler.hasMessages(MSG_START)) {
				handler.removeMessages(MSG_START);
			}
			if (handler.hasMessages(MSG_UPDATE)) {
				handler.removeMessages(MSG_UPDATE);
			}
		}
		isAuto = false;
	}

	/**
	 * ִ�и���
	 */
	private void play() {
		this.i = (i == size - 1) ? 0 : i + 1;
		reset();
		update();
	}

	/**
	 * ������ز���
	 */
	private void reset() {
		this.x = getPaddingLeft();
		this.y1 = height1;
		this.y2 = height2;
	}

	/**
	 * ʵʱ����
	 */
	private void update() {
		y1 = y1 - speed;
		y2 = y2 - speed;
		invalidate();
		handler.sendEmptyMessageDelayed(MSG_UPDATE, TIME_UPDATE);
	}

	/**
	 * �ָ���̬��ʾЧ��
	 */
	private void restart() {
		pause();
		start();
	}

	/**
	 * �趨�ַ���
	 * 
	 * @param texts
	 *            �ַ���
	 */
	public void setTextList(ArrayList<String> texts) {
		this.arrays.clear();
		this.arrays = texts;
		this.size = arrays.size();
		create();
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	private static class ScrollHandler extends Handler {

		private WeakReference<ScrollTextView> reference;

		public ScrollHandler(ScrollTextView view) {
			// TODO Auto-generated constructor stub
			reference = new WeakReference<ScrollTextView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (reference.get() != null) {
				ScrollTextView theView = reference.get();
				switch (msg.what) {
				case MSG_FIRST:
					theView.update();
					break;
				case MSG_START:
					theView.play();
					break;
				case MSG_UPDATE:
					theView.update();
					break;
				}
			}
		}
	}

}
