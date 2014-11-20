package com.cwd.cmeplayer;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cwd.cmeplayer.util.ScanUtil;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>����������ҳ��</b></br>
 * 
 * <br>
 * ִ�ж�����ɨ�����ݿ⡢��һ�ν��봴������ͼ�꣬
 * �����������http://video.sina.com.cn/v/b/69976687-1784435580.html���ǳ�����˼��������������
 * </br>
 * 
 * @author CWD
 * @version 2013.05.06 v1.0 ��ɶ�������ʱ��ת <br>
 *          2013.07.30 v1.1 �����Է����Ƿ������е��жϣ��Ѿ���������ִ��ɨ������ֱ�ӽ���������</br>
 */
public class LogoActivity extends Activity {

	private Handler mHandler;
	private ScanUtil manager;
  
	private ImageView gifView;// GIF�����ؼ�
	private ImageView logoView;// LOGO�����ؼ�
	
	private int id;//���Է�֧

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isServiceRunning()) {
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			startActivity(intent);
			LogoActivity.this.finish();
		} else {
			initActivity();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHandler != null) {
			mHandler.removeCallbacks(scan);
			mHandler.removeCallbacks(runnable);
		}
	}

	private void initActivity() {
		setContentView(R.layout.activity_logo);

		gifView = (ImageView) findViewById(R.id.activity_logo_gif);
		logoView = (ImageView) findViewById(R.id.activity_logo_name);

		manager = new ScanUtil(getApplicationContext());
		final Animation logoAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.activity_logo);
		logoView.startAnimation(logoAnim);

		// ��������������ʱ����GIF����
		logoAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

				/*
				 * ò��AnimationDrawable��bug��ĳЩ֡�ἷѹ���Σ�û�취ɾ���ˣ�
				 * �����Զ���View��Movieֱ�Ӳ���GIFͼƬ��������ģ������Ứ��������취����
				 * PS�е����֡����Ϊ�������ϳɣ�����ͼƬʧ��������û�в��ã�
				 * ������˵�Զ���View�ο�����һ�汾��C_Me����RunGif��
				 */
				final AnimationDrawable anim = (AnimationDrawable) getResources()
						.getDrawable(R.anim.activity_droidman);
				gifView.setBackgroundDrawable(anim);
				gifView.getViewTreeObserver().addOnPreDrawListener(
						new OnPreDrawListener() {

							@Override
							public boolean onPreDraw() {
								// TODO Auto-generated method stub
								anim.start();
								return true;// ����true����������ʾ����Ч��
							}
						});
			}
		});

		mHandler = new Handler();
		mHandler.post(scan);// ִ��ɨ��
		mHandler.postDelayed(runnable, 5000);// �ӳ�ִ����ת
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(LogoActivity.this, MainActivity.class);
			startActivity(intent);
			LogoActivity.this.finish();
		}
	};

	private Runnable scan = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			manager.scanMusicFromDB();
		}
	};

	/**
	 * �������Ƿ���������
	 * 
	 * @return true/false
	 */
	private boolean isServiceRunning() {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					"com.cwd.cmeplayer.service.MediaService")) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

}
