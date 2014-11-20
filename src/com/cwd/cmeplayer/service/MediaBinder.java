package com.cwd.cmeplayer.service;

import android.os.Binder;

import com.cwd.cmeplayer.entity.MusicInfo;
import com.cwd.cmeplayer.lyric.LyricView;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>���Ʋ���Binder��</b></br>
 * 
 * @author CWD
 * @version 2013.06.23 v1.0 ��Ӧ���ֽӿڵ�ʵ��<br>
 *          2013.07.24 v1.1 ������������ӿ�</br> 2013.07.30 v1.2 ��������SeekBar�ӿ�</br>
 */
public class MediaBinder extends Binder {

	private OnPlayStartListener onPlayStartListener;
	private OnPlayingListener onPlayingListener;
	private OnPlayPauseListener onPlayPauseListener;
	private OnPlayCompleteListener onPlayCompleteListener;
	private OnPlayErrorListener onPlayErrorListener;
	private OnModeChangeListener onModeChangeListener;

	private OnServiceBinderListener onServiceBinderListener;

	protected void playStart(MusicInfo info) {
		if (onPlayStartListener != null) {
			onPlayStartListener.onStart(info);
		}
	}

	protected void playUpdate(int currentPosition) {
		if (onPlayingListener != null) {
			onPlayingListener.onPlay(currentPosition);
		}
	}

	protected void playPause() {
		if (onPlayPauseListener != null) {
			onPlayPauseListener.onPause();
		}
	}

	protected void playComplete() {
		if (onPlayCompleteListener != null) {
			onPlayCompleteListener.onPlayComplete();
		}
	}

	protected void playError() {
		if (onPlayErrorListener != null) {
			onPlayErrorListener.onPlayError();
		}
	}

	protected void modeChange(int mode) {
		if (onModeChangeListener != null) {
			onModeChangeListener.onModeChange(mode);
		}
	}

	/**
	 * ����SeekBarʱ��Ӧ
	 */
	public void seekBarStartTrackingTouch() {
		if (onServiceBinderListener != null) {
			onServiceBinderListener.seekBarStartTrackingTouch();
		}
	}

	/**
	 * �뿪SeekBarʱ��Ӧ
	 * 
	 * @param progress
	 *            ��ǰ����
	 */
	public void seekBarStopTrackingTouch(int progress) {
		if (onServiceBinderListener != null) {
			onServiceBinderListener.seekBarStopTrackingTouch(progress);
		}
	}

	/**
	 * ���ø����ͼ
	 * 
	 * @param lrcView
	 *            �����ͼ
	 * @param isKLOK
	 *            �Ƿ����ڿ���OKģʽ
	 */
	public void setLyricView(LyricView lrcView, boolean isKLOK) {
		if (onServiceBinderListener != null) {
			onServiceBinderListener.lrc(lrcView, isKLOK);
		}
	}

	/**
	 * ���ÿ�������
	 * 
	 * @param command
	 *            ��������
	 */
	public void setControlCommand(int command) {
		if (onServiceBinderListener != null) {
			onServiceBinderListener.control(command);
		}
	}

	public void setOnPlayStartListener(OnPlayStartListener onPlayStartListener) {
		this.onPlayStartListener = onPlayStartListener;
	}

	public void setOnPlayingListener(OnPlayingListener onPlayingListener) {
		this.onPlayingListener = onPlayingListener;
	}

	public void setOnPlayPauseListener(OnPlayPauseListener onPlayPauseListener) {
		this.onPlayPauseListener = onPlayPauseListener;
	}

	public void setOnPlayCompletionListener(
			OnPlayCompleteListener onPlayCompleteListener) {
		this.onPlayCompleteListener = onPlayCompleteListener;
	}

	public void setOnPlayErrorListener(OnPlayErrorListener onPlayErrorListener) {
		this.onPlayErrorListener = onPlayErrorListener;
	}

	public void setOnModeChangeListener(
			OnModeChangeListener onModeChangeListener) {
		this.onModeChangeListener = onModeChangeListener;
	}

	protected void setOnServiceBinderListener(
			OnServiceBinderListener onServiceBinderListener) {
		this.onServiceBinderListener = onServiceBinderListener;
	}

	/**
	 * ��ʼ���Żص��ӿ�
	 */
	public interface OnPlayStartListener {
		/**
		 * ��ʼ����
		 * 
		 * @param info
		 *            ������ϸ��Ϣ
		 */
		public void onStart(MusicInfo info);
	}

	/**
	 * ���ڲ��Żص��ӿ�
	 */
	public interface OnPlayingListener {
		/**
		 * ��ʼ����
		 * 
		 * @param current
		 *            ��ǰ����ʱ��(String����)
		 */
		public void onPlay(int currentPosition);
	}

	/**
	 * ��ͣ���Żص��ӿ�
	 */
	public interface OnPlayPauseListener {
		/**
		 * ��ͣ����
		 */
		public void onPause();
	}

	/**
	 * ������ɻص��ӿ�
	 */
	public interface OnPlayCompleteListener {
		/**
		 * �������
		 */
		public void onPlayComplete();
	}

	/**
	 * ���ų���ص��ӿ�
	 */
	public interface OnPlayErrorListener {
		/**
		 * ���ų���
		 */
		public void onPlayError();
	}

	/**
	 * ����ģʽ���Ļص��ӿ�
	 */
	public interface OnModeChangeListener {
		/**
		 * ����ģʽ���
		 */
		public void onModeChange(int mode);
	}

	/**
	 * �ص��ӿڣ�ֻ����serviceʹ��
	 */
	protected interface OnServiceBinderListener {
		/**
		 * ����SeekBarʱ��Ӧ
		 */
		void seekBarStartTrackingTouch();

		/**
		 * �뿪SeekBarʱ��Ӧ
		 * 
		 * @param progress
		 *            ��ǰ����
		 */
		void seekBarStopTrackingTouch(int progress);

		/**
		 * ���ø��
		 * 
		 * @param lyricView
		 *            �����ͼ
		 * @param isKLOK
		 *            �Ƿ����ڿ���OKģʽ
		 */
		void lrc(LyricView lyricView, boolean isKLOK);

		/**
		 * ���ſ���(���š���ͣ����һ�ס���һ�ס�����ģʽ�л�)
		 * 
		 * @param command
		 *            ��������
		 */
		void control(int command);
	}

}
