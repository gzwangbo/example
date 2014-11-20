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
 * <b>程序启动首页面</b></br>
 * 
 * <br>
 * 执行动画、扫描数据库、第一次进入创建桌面图标，
 * 动画灵感来自http://video.sina.com.cn/v/b/69976687-1784435580.html，非常有意思，可以欣赏欣赏
 * </br>
 * 
 * @author CWD
 * @version 2013.05.06 v1.0 完成动画及定时跳转 <br>
 *          2013.07.30 v1.1 新增对服务是否已运行的判断，已经运行无需执行扫描任务，直接进入主界面</br>
 */
public class LogoActivity extends Activity {

	private Handler mHandler;
	private ScanUtil manager;
  
	private ImageView gifView;// GIF动画控件
	private ImageView logoView;// LOGO动画控件
	
	private int id;//测试分支

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

		// 动画监听，结束时播放GIF动画
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
				 * 貌似AnimationDrawable有bug，某些帧会挤压变形，没办法删减了；
				 * 想用自定义View用Movie直接播放GIF图片，但是在模拟器里会花屏，解决办法就是
				 * PS中导入的帧“作为背景”合成，但是图片失真厉害，没有采用；
				 * 上面所说自定义View参考我上一版本的C_Me音乐RunGif。
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
								return true;// 必须true才能正常显示动画效果
							}
						});
			}
		});

		mHandler = new Handler();
		mHandler.post(scan);// 执行扫描
		mHandler.postDelayed(runnable, 5000);// 延迟执行跳转
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
	 * 检查服务是否正在运行
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
