package com.cwd.cmeplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cwd.cmeplayer.custom.PushView;
import com.cwd.cmeplayer.custom.VisualizerView;
import com.cwd.cmeplayer.dialog.AboutDialog;
import com.cwd.cmeplayer.dialog.InfoDialog;
import com.cwd.cmeplayer.entity.MusicInfo;
import com.cwd.cmeplayer.list.CoverList;
import com.cwd.cmeplayer.lyric.LyricView;
import com.cwd.cmeplayer.service.MediaBinder;
import com.cwd.cmeplayer.service.MediaBinder.OnModeChangeListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayCompleteListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayErrorListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayPauseListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayStartListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayingListener;
import com.cwd.cmeplayer.service.MediaService;
import com.cwd.cmeplayer.util.FormatUtil;
import com.cwd.cmeplayer.util.ImageUtil;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>��������ҳ��</b></br>
 * 
 * @author CWD
 * @version 2013.06.30 v1.0 ʵ�ֻ������Ž���Ϳ��ӻ�Ч��<br>
 *          2013.07.03 v1.1 ʵ�ְ�ť����<br>
 *          2013.07.30 v1.2 ʵ�ֶ�SeekBar�������޸�֮ǰ����BUG<br>
 *          2013.08.13 v1.3 ʵ�ַ�Path�˵�<br>
 *          2013.08.16 v1.4 ʵ���ҵ����ť������Ч<br>
 *          2013.08.19 v1.5 ʵ�ֿ��ˡ��������<br>
 *          2013.08.22 v1.6 ���˳������Ϊ�㲥֪ͨMainActivity��ȡ��ԭ�ȵ�onActivityResult<br>
 *          2013.08.22 v1.6.1 ������Path�˵����Զ���ظ���������<br>
 *          2013.08.24 v1.7 ʵ�ָ�������ͼ�͸�ʸ���ɫ<br>
 *          2013.08.27 v2.0 ʵ�ֺ���ģʽ���޸ĺ������л�����߼�<br>
 *          2013.08.28 v2.1 ʵ��ר������3D��ת����<br>
 *          2013.08.30 v2.2 ʵ�ֶ�ϵͳý������������</br>
 */
public class PlayerActivity extends Activity implements OnClickListener,
		OnLongClickListener, OnTouchListener, OnSeekBarChangeListener {

	private final String TIME_NORMAL = "00:00";
	private final int[] modeImage = { R.drawable.player_btn_mode_normal_style,
			R.drawable.player_btn_mode_repeat_one_style,
			R.drawable.player_btn_mode_repeat_all_style,
			R.drawable.player_btn_mode_random_style };

	private int skinId;// ����ͼID
	private int colorId;// ��ʸ���ɫֵ
	private int musicPosition;// ��ǰ���Ÿ�������

	private boolean isFavorite = false;// ��ǰ�����Ƿ�Ϊ�
	private boolean isPortraitActivity = true;// ��ǰ�Ƿ�Ϊ����ģʽ
	private boolean isFirstTransition3dAnimation = true;// 3D��ת����ִֻ��һ��

	private ImageButton btnMode;// ����ģʽ��ť
	private ImageButton btnReturn;// ���ذ�ť
	private ImageButton btnPrevious;// ��һ�װ�ť
	private ImageButton btnPlay;// ���ź���ͣ��ť
	private ImageButton btnNext;// ��һ�װ�ť
	private ImageButton btnFavorite;// �ҵ����ť
	private ImageButton menuButton;// �˵�(��ť)
	private ImageButton menuAbout;// �˵�(����)
	private ImageButton menuInfo;// �˵�(��������)
	private ImageButton menuSetting;// �˵�(����)
	private ImageButton menuExit;// �˵�(�˳�)

	private RelativeLayout skin;// ����ͼ
	private RelativeLayout menu;// �˵�

	private TextView currentTime;// ��ǰʱ��
	private TextView totalTime;// ��ʱ��
	private SeekBar seekBar;// ������
	private SeekBar seekVolumeBar;// ����������
	private PushView mp3Name;// ����
	private PushView mp3Info;// ������Ϣ����
	private PushView mp3Artist;// ������
	private ImageView mp3Cover;// ר��ͼƬ
	private ImageView mp3Favorite;// �ҵ������ͼƬ
	private LyricView lyricView;// �����ͼ
	private PopupWindow popupVolume;
	private VisualizerView visualizer;// ��������ͼ

	private Intent playIntent;
	private MediaBinder binder;
	private MusicInfo musicInfo;
	private AudioManager audioManager;
	private SharedPreferences preferences;
	private ServiceConnection serviceConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		init();// ��ʼ��

		Intent intent = new Intent(MediaService.BROADCAST_ACTION_SERVICE);
		intent.putExtra(MediaService.INTENT_ACTIVITY,
				MediaService.ACTIVITY_PLAYER);
		sendBroadcast(intent);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		int id1 = preferences.getInt(MainActivity.PREFERENCES_SKIN,
				R.drawable.skin_bg1);
		int id2 = preferences.getInt(MainActivity.PREFERENCES_LYRIC,
				Color.argb(250, 251, 248, 29));
		if (skinId != id1) {// �ж��Ƿ��������ͼ
			skinId = id1;
			if (isPortraitActivity) {
				skin.setBackgroundResource(skinId);
			}
		}
		if (colorId != id2) {// �ж��Ƿ���ĸ�ʸ���ɫ
			colorId = id2;
			lyricView.setLyricHighlightColor(colorId);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		if (serviceConnection != null) {
			unbindService(serviceConnection);// һ��Ҫ��finish֮ǰ�����
			serviceConnection = null;
		}
		if (isPortraitActivity) {
			visualizer.releaseVisualizerFx();// ��ͣ�������ֿ��ӻ����涯��
		}
	}

	/*
	 * ��2.3.3��ģ�����л�������к������ز��������������4.0��ģ����������
	 * 
	 * ��֪��2.3.3���ֻ����Ƿ�Ҳ������������???
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);

		if (serviceConnection != null) {
			unbindService(serviceConnection);// �л����ǵý��
			serviceConnection = null;
		}

		if (popupVolume.isShowing()) {
			popupVolume.dismiss();// �л�ǰ��ʧ������
		}

		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			isPortraitActivity = true;
			initPortraitActivity();// ���³�ʼ����������
		} else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isPortraitActivity = false;
			if (visualizer != null) {
				visualizer.releaseVisualizerFx();// �ͷ����ֿ��ӻ�����
			}
			initLandscapeActivity();// ���³�ʼ����������
		}
	}

	/**
	 * ��ʼ�����
	 */
	private void init() {
		musicPosition = getIntent().getIntExtra(
				MainActivity.BROADCAST_INTENT_POSITION, 0);
		preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		skinId = preferences.getInt(MainActivity.PREFERENCES_SKIN,
				R.drawable.skin_bg1);
		colorId = preferences.getInt(MainActivity.PREFERENCES_LYRIC,
				Color.argb(250, 251, 248, 29));
		playIntent = new Intent(getApplicationContext(), MediaService.class);

		Configuration config = getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			isPortraitActivity = true;
			initPortraitActivity();// ��ʼ����������
		} else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isPortraitActivity = false;
			initLandscapeActivity();// ��ʼ����������
		}
		initPopupVolume();// ��ʼ����������
	}

	/**
	 * ��ʼ������ģʽ
	 */
	private void initPortraitActivity() {
		setContentView(R.layout.activity_player);

		// /////////////////////////// ��ʼ�������� /////////////////////////// //
		skin = (RelativeLayout) findViewById(R.id.activity_player_skin);
		btnReturn = (ImageButton) findViewById(R.id.activity_player_ib_return);
		btnMode = (ImageButton) findViewById(R.id.activity_player_ib_mode);
		btnPrevious = (ImageButton) findViewById(R.id.activity_player_ib_previous);
		btnPlay = (ImageButton) findViewById(R.id.activity_player_ib_play);
		btnNext = (ImageButton) findViewById(R.id.activity_player_ib_next);
		btnFavorite = (ImageButton) findViewById(R.id.activity_player_ib_favorite);
		seekBar = (SeekBar) findViewById(R.id.activity_player_seek);
		currentTime = (TextView) findViewById(R.id.activity_player_tv_time_current);
		totalTime = (TextView) findViewById(R.id.activity_player_tv_time_total);
		mp3Name = (PushView) findViewById(R.id.activity_player_tv_name);
		mp3Info = (PushView) findViewById(R.id.activity_player_tv_info);
		mp3Artist = (PushView) findViewById(R.id.activity_player_tv_artist);
		mp3Cover = (ImageView) findViewById(R.id.activity_player_cover);
		mp3Favorite = (ImageView) findViewById(R.id.activity_player_iv_favorite);
		lyricView = (LyricView) findViewById(R.id.activity_player_lyric);
		visualizer = (VisualizerView) findViewById(R.id.activity_player_visualizer);

		currentTime.setText(TIME_NORMAL);
		totalTime.setText(TIME_NORMAL);
		btnReturn.setOnClickListener(this);
		btnMode.setOnClickListener(this);
		btnPrevious.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnFavorite.setOnClickListener(this);
		mp3Cover.setOnClickListener(this);
		btnPrevious.setOnLongClickListener(this);
		btnNext.setOnLongClickListener(this);
		btnPrevious.setOnTouchListener(this);
		btnNext.setOnTouchListener(this);
		seekBar.setOnSeekBarChangeListener(this);

		btnMode.setImageResource(modeImage[preferences.getInt(
				MainActivity.PREFERENCES_MODE, 0)]);
		skin.setBackgroundResource(skinId);
		lyricView.setLyricHighlightColor(colorId);
		// /////////////////////////// ��ʼ�������� /////////////////////////// //

		// ////////////////////////// ��ʼ������� ////////////////////////// //
		serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				binder = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				binder = (MediaBinder) service;
				if (binder != null) {
					binder.setOnPlayStartListener(new OnPlayStartListener() {

						@Override
						public void onStart(MusicInfo info) {
							// TODO Auto-generated method stub
							mp3Name.setText(info.getName());
							currentTime.setText(TIME_NORMAL);
							totalTime.setText(info.getTime());
							seekBar.setMax(info.getMp3Duration());
							// �����������ֿ��ӻ����涯��
							visualizer.setupVisualizerFx(info
									.getAudioSessionId());
							ArrayList<String> list = new ArrayList<String>();
							list.add(info.getFormat());
							list.add("��С: " + info.getSize());
							list.add(info.getGenre());
							list.add(info.getAlbum());
							list.add(info.getYears());
							list.add(info.getChannels());
							list.add(info.getKbps());
							list.add(info.getHz());
							mp3Info.setTextList(list);
							mp3Artist.setText(info.getArtist());
							isFirstTransition3dAnimation = true;
							if (CoverList.cover == null) {
								startTransition3dAnimation(BitmapFactory
										.decodeResource(getResources(),
												R.drawable.player_cover_default));
							} else {
								startTransition3dAnimation(CoverList.cover);
							}
							btnPlay.setImageResource(R.drawable.player_btn_pause_style);
							isFavorite = info.isFavorite();
							btnFavorite
									.setImageResource(isFavorite ? R.drawable.player_btn_favorite_star_style
											: R.drawable.player_btn_favorite_nostar_style);
							musicInfo = info;
						}
					});
					binder.setOnPlayingListener(new OnPlayingListener() {

						@Override
						public void onPlay(int currentPosition) {
							// TODO Auto-generated method stub
							seekBar.setProgress(currentPosition);
							currentTime.setText(FormatUtil
									.formatTime(currentPosition));
						}
					});
					binder.setOnPlayPauseListener(new OnPlayPauseListener() {

						@Override
						public void onPause() {
							// TODO Auto-generated method stub
							btnPlay.setImageResource(R.drawable.player_btn_play_style);
						}
					});
					binder.setOnPlayCompletionListener(new OnPlayCompleteListener() {

						@Override
						public void onPlayComplete() {
							// TODO Auto-generated method stub

						}
					});
					binder.setOnPlayErrorListener(new OnPlayErrorListener() {

						@Override
						public void onPlayError() {
							// TODO Auto-generated method stub
							// �������ļ�������ҲӦִ�д��б��Ƴ�����������д�ˡ����Է����㲥��ȥ��
						}
					});
					binder.setOnModeChangeListener(new OnModeChangeListener() {

						@Override
						public void onModeChange(int mode) {
							// TODO Auto-generated method stub
							btnMode.setImageResource(modeImage[mode]);
						}
					});
					binder.setLyricView(lyricView, true);// ���ø����ͼ���ǿ���OKģʽ
				}
			}
		};
		bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		// ////////////////////////// ��ʼ������� ////////////////////////// //

		initPathMenu();// ��ʼ���˵�
	}

	/**
	 * ��ʼ������ģʽ��ע�����android:configChanges="keyboard|keyboardHidden|orientation"��
	 * �����л����ֻ��ִ��onConfigurationChanged������
	 * 
	 * ��ʵ������ģʽid����д��һ���ģ��������ظ��󶨣������Ҳ���ȥ���ˡ�
	 */
	private void initLandscapeActivity() {
		setContentView(R.layout.activity_player_landscape);

		// /////////////////////////// ��ʼ�������� /////////////////////////// //
		mp3Name = (PushView) findViewById(R.id.activity_player_landscape_tv_list);
		btnPrevious = (ImageButton) findViewById(R.id.activity_player_landscape_ib_previous);
		btnPlay = (ImageButton) findViewById(R.id.activity_player_landscape_ib_play);
		btnNext = (ImageButton) findViewById(R.id.activity_player_landscape_ib_next);
		mp3Cover = (ImageView) findViewById(R.id.activity_player_landscape_cover);
		lyricView = (LyricView) findViewById(R.id.activity_player_landscape_lyric);
		lyricView.setLyricHighlightColor(colorId);

		btnPrevious.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		mp3Cover.setOnClickListener(this);
		btnPrevious.setOnLongClickListener(this);
		btnNext.setOnLongClickListener(this);
		btnPrevious.setOnTouchListener(this);
		btnNext.setOnTouchListener(this);
		// /////////////////////////// ��ʼ�������� /////////////////////////// //

		// ////////////////////////// ��ʼ������� ////////////////////////// //
		serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				binder = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				binder = (MediaBinder) service;
				if (binder != null) {
					binder.setOnPlayStartListener(new OnPlayStartListener() {

						@Override
						public void onStart(MusicInfo info) {
							// TODO Auto-generated method stub
							ArrayList<String> list = new ArrayList<String>();
							list.add(info.getName());
							list.add(info.getArtist());
							list.add(info.getFormat());
							list.add("��С: " + info.getSize());
							list.add(info.getGenre());
							list.add(info.getAlbum());
							list.add(info.getYears());
							list.add(info.getChannels());
							list.add(info.getKbps());
							list.add(info.getHz());
							mp3Name.setTextList(list);

							Bitmap bitmap = null;
							isFirstTransition3dAnimation = true;
							if (CoverList.cover == null) {
								bitmap = BitmapFactory
										.decodeResource(
												getResources(),
												R.drawable.player_landscape_cover_default);
								bitmap = ImageUtil
										.createReflectionBitmap(bitmap);
							} else {
								bitmap = ImageUtil
										.createReflectionBitmap(CoverList.cover);
							}
							startTransition3dAnimation(bitmap);
							btnPlay.setBackgroundResource(R.drawable.player_landscape_btn_pause_style);
						}
					});
					binder.setOnPlayPauseListener(new OnPlayPauseListener() {

						@Override
						public void onPause() {
							// TODO Auto-generated method stub
							btnPlay.setBackgroundResource(R.drawable.player_landscape_btn_play_style);
						}
					});
					binder.setOnPlayErrorListener(new OnPlayErrorListener() {

						@Override
						public void onPlayError() {
							// TODO Auto-generated method stub
							// �������ļ�������ҲӦִ�д��б��Ƴ�����������д�ˡ����Է����㲥��ȥ��
						}
					});
					binder.setLyricView(lyricView, false);// ���ø����ͼ������ģʽ
				}
			}
		};
		bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		// ////////////////////////// ��ʼ������� ////////////////////////// //
	}

	/**
	 * ��ʼ���˵����
	 */
	private void initPathMenu() {
		menu = (RelativeLayout) findViewById(R.id.activity_player_menu);
		menuButton = (ImageButton) findViewById(R.id.activity_player_ib_menu);
		menuAbout = (ImageButton) findViewById(R.id.activity_player_ib_menu_about);
		menuInfo = (ImageButton) findViewById(R.id.activity_player_ib_menu_info);
		menuSetting = (ImageButton) findViewById(R.id.activity_player_ib_menu_setting);
		menuExit = (ImageButton) findViewById(R.id.activity_player_ib_menu_exit);

		menuButton.setOnClickListener(this);
		menuAbout.setOnClickListener(this);
		menuInfo.setOnClickListener(this);
		menuSetting.setOnClickListener(this);
		menuExit.setOnClickListener(this);
		menu.setOnTouchListener(this);
	}

	/**
	 * ��ʼ�������������
	 */
	private void initPopupVolume() {
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		View view = LayoutInflater.from(this).inflate(R.layout.popup_volume,
				null);// ���봰�������ļ�
		popupVolume = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, false);// ����PopupWindow����
		seekVolumeBar = (SeekBar) view.findViewById(R.id.pupup_volume_seek);
		seekVolumeBar.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		seekVolumeBar.setOnSeekBarChangeListener(this);
		popupVolume.setBackgroundDrawable(new BitmapDrawable());// �����߿���ʧ
		popupVolume.setOutsideTouchable(true);// ���õ��������ߴ�����ʧ
	}

	/**
	 * �������ڶԻ���
	 */
	private void showAboutDialog() {
		AboutDialog aboutDialog = new AboutDialog(this);
		aboutDialog.show();
	}

	/**
	 * ������������Ի���
	 */
	private void showInfoDialog() {
		if (musicInfo != null) {
			InfoDialog infoDialog = new InfoDialog(this);
			infoDialog.show();
			infoDialog.setInfo(musicInfo);
		} else {
			Toast.makeText(getApplicationContext(), "���޸�������",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ���˰�ť�¼�
	 * 
	 * @param enabled
	 *            true/false
	 */
	private void setMenuEnabled(boolean enabled) {
		menuButton.setEnabled(enabled);
		menuAbout.setEnabled(enabled);
		menuInfo.setEnabled(enabled);
		menuSetting.setEnabled(enabled);
		menuExit.setEnabled(enabled);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_player_ib_return:// ���ذ�ť����
			finish();
			break;

		case R.id.activity_player_ib_mode:// ����ģʽ�л���ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_MODE);
			}
			break;

		case R.id.activity_player_ib_previous:// ��һ�װ�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PREVIOUS);
			}
			break;

		case R.id.activity_player_ib_play:// ���Ű�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PLAY);
			}
			break;

		case R.id.activity_player_ib_next:// ��һ�װ�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_NEXT);
			}
			break;

		case R.id.activity_player_ib_favorite:// �ҵ����ť����
			if (seekBar.getMax() > 0) {// ���жϱ�ʾ���Ź�����
				Intent intent = new Intent(
						MainActivity.BROADCAST_ACTION_FAVORITE);
				intent.putExtra(MainActivity.BROADCAST_INTENT_POSITION,
						musicPosition);// ֻ��Position��ȥ
				sendBroadcast(intent);
				if (isFavorite) {// ��Ϊִ�и����ҵ����һ���ɹ�����Ӧͨ�����ͻ�ִ��������ߵ�״̬��
					btnFavorite
							.setImageResource(R.drawable.player_btn_favorite_nostar_style);
					isFavorite = false;
				} else {// ���ܸܺ��ӣ�����д��...(Ƶ���������Ҫ������)
					btnFavorite
							.setImageResource(R.drawable.player_btn_favorite_star_style);
					startFavoriteImageAnimation();// ��ͨ����������ֹƵ�����
					isFavorite = true;
				}
			}
			break;

		case R.id.activity_player_cover:// �����������ڴ���
			seekVolumeBar.setProgress(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			popupVolume.showAsDropDown(mp3Name);
			break;

		case R.id.activity_player_ib_menu:// �رղ˵�
			startMenuRotateAnimationOut(R.id.activity_player_ib_menu);
			break;

		case R.id.activity_player_ib_menu_about:// �������ڱ����
			menuAbout.startAnimation(startAmplifyIconAnimation());
			menuInfo.startAnimation(startShrinkIconAnimation());
			menuSetting.startAnimation(startShrinkIconAnimation());
			menuExit.startAnimation(startShrinkIconAnimation());
			startMenuRotateAnimationOut(R.id.activity_player_ib_menu_about);
			break;

		case R.id.activity_player_ib_menu_info:// ������������
			menuInfo.startAnimation(startAmplifyIconAnimation());
			menuAbout.startAnimation(startShrinkIconAnimation());
			menuSetting.startAnimation(startShrinkIconAnimation());
			menuExit.startAnimation(startShrinkIconAnimation());
			startMenuRotateAnimationOut(R.id.activity_player_ib_menu_info);
			break;

		case R.id.activity_player_ib_menu_setting:// ��ת���ý���
			menuSetting.startAnimation(startAmplifyIconAnimation());
			menuAbout.startAnimation(startShrinkIconAnimation());
			menuInfo.startAnimation(startShrinkIconAnimation());
			menuExit.startAnimation(startShrinkIconAnimation());
			startMenuRotateAnimationOut(R.id.activity_player_ib_menu_setting);
			break;

		case R.id.activity_player_ib_menu_exit:// �˳�
			menuExit.startAnimation(startAmplifyIconAnimation());
			menuAbout.startAnimation(startShrinkIconAnimation());
			menuInfo.startAnimation(startShrinkIconAnimation());
			menuSetting.startAnimation(startShrinkIconAnimation());
			startMenuRotateAnimationOut(R.id.activity_player_ib_menu_exit);
			break;

		case R.id.activity_player_landscape_ib_previous:// ����ģʽ��һ�װ�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PREVIOUS);
			}
			break;

		case R.id.activity_player_landscape_ib_play:// ����ģʽ���Ű�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PLAY);
			}
			break;

		case R.id.activity_player_landscape_ib_next:// ����ģʽ��һ�װ�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_NEXT);
			}
			break;

		case R.id.activity_player_landscape_cover:// �����������ڴ���
			seekVolumeBar.setProgress(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			popupVolume.showAsDropDown(mp3Name);
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_player_ib_previous:// ����ģʽ����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_REWIND);
			}
			break;

		case R.id.activity_player_ib_next:// ����ģʽ���
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_FORWARD);
			}
			break;

		case R.id.activity_player_landscape_ib_previous:// ����ģʽ����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_FORWARD);
			}
			break;

		case R.id.activity_player_landscape_ib_next:// ����ģʽ���
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_FORWARD);
			}
			break;
		}
		return true;// ����true����׼��ִ��onClick
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_player_menu:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (menuButton.isShown()) {
					if (menuButton.isEnabled()) {
						startMenuRotateAnimationOut(R.id.activity_player_ib_menu);
					}
					return true;// ���ƴ���Dialog���������������ʧ
				} else if (mp3Favorite.isShown()) {
					return true;// ����ִ�����������¼�
				}
			}
			break;

		case R.id.activity_player_ib_previous:// ����ģʽ���ֲ���
			if (binder != null && event.getAction() == MotionEvent.ACTION_UP) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_REPLAY);
			}
			break;

		case R.id.activity_player_ib_next:// ����ģʽ���ֲ���
			if (binder != null && event.getAction() == MotionEvent.ACTION_UP) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_REPLAY);
			}
			break;

		case R.id.activity_player_landscape_ib_previous:// ����ģʽ���ֲ���
			if (binder != null && event.getAction() == MotionEvent.ACTION_UP) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_REPLAY);
			}
			break;

		case R.id.activity_player_landscape_ib_next:// ����ģʽ���ֲ���
			if (binder != null && event.getAction() == MotionEvent.ACTION_UP) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_REPLAY);
			}
			break;
		}
		return false;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		switch (seekBar.getId()) {
		case R.id.activity_player_seek:
			if (fromUser && seekBar.getMax() > 0) {
				currentTime.setText(FormatUtil.formatTime(progress));
			}
			break;

		case R.id.pupup_volume_seek:
			if (fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
			}
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		if (seekBar.getId() == R.id.activity_player_seek) {
			if (binder != null) {
				binder.seekBarStartTrackingTouch();
			}
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		if (seekBar.getId() == R.id.activity_player_seek) {
			if (binder != null) {
				binder.seekBarStopTrackingTouch(seekBar.getProgress());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (isPortraitActivity) {// ֻ������ģʽ��ִ��
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				if (popupVolume.isShowing()) {
					popupVolume.dismiss();// ���������
				}
				if (menuButton.isShown()) {
					if (menuButton.isEnabled()) {
						startMenuRotateAnimationOut(R.id.activity_player_ib_menu);
					}
				} else {
					startMenuRotateAnimationIn();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (menuButton.isShown()) {
					if (menuButton.isEnabled()) {
						startMenuRotateAnimationOut(R.id.activity_player_ib_menu);
					}
					return true;
				}
			}
		}
		if (popupVolume.isShowing()) {
			if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {// ��������������
				seekVolumeBar.setProgress(seekVolumeBar.getProgress() + 1);
			} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
				seekVolumeBar.setProgress(seekVolumeBar.getProgress() - 1);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �˵�չ������
	 */
	private void startMenuRotateAnimationIn() {
		AnimationSet animationSet = new AnimationSet(true);
		RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		Animation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);

		animationSet.addAnimation(rotateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setDuration(500);
		animationSet.setFillAfter(true);
		animationSet.setAnimationListener(new AnimationListener() {

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
				setMenuEnabled(true);
			}
		});

		menuButton.setVisibility(View.VISIBLE);
		menuButton.startAnimation(animationSet);
		startIconTranslateAnimationIn();
	}

	/**
	 * �˵��رն���
	 * 
	 * @param id
	 *            ButtonId
	 */
	private void startMenuRotateAnimationOut(final int id) {
		setMenuEnabled(false);
		AnimationSet animationSet = new AnimationSet(true);
		RotateAnimation rotateAnimation = new RotateAnimation(0.0f, -360.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

		animationSet.addAnimation(rotateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setDuration(500);
		animationSet.setFillAfter(true);

		animationSet.setAnimationListener(new AnimationListener() {

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
				menuButton.setVisibility(View.GONE);
				menuButton.setClickable(false);
				menuButton.setFocusable(false);
				switch (id) {
				case R.id.activity_player_ib_menu_about:// ��ʾ����
					showAboutDialog();
					break;

				case R.id.activity_player_ib_menu_info:// ��ʾ��������
					showInfoDialog();
					break;

				case R.id.activity_player_ib_menu_setting:// ��ת���ý���
					Intent intent = new Intent(getApplicationContext(),
							SettingActivity.class);
					startActivity(intent);
					break;

				case R.id.activity_player_ib_menu_exit:// �˳�����
					sendBroadcast(new Intent(MainActivity.BROADCAST_ACTION_EXIT));
					finish();
					break;
				}
			}
		});
		menuButton.startAnimation(animationSet);
		if (id == R.id.activity_player_ib_menu) {
			startIconTranslateAnimationOut();
		}
	}

	/**
	 * ͼ��Ķ���(�붯��)
	 */
	private void startIconTranslateAnimationIn() {
		final int w = menuButton.getWidth() / 2;
		final int h = menuButton.getHeight() / 2;
		for (int i = 1; i < 5; i++) {// ��1��ʼĿ���ǹ��˵�menuButton
			ImageButton imageButton = (ImageButton) menu.getChildAt(i);
			imageButton.setVisibility(View.VISIBLE);
			MarginLayoutParams params = (MarginLayoutParams) imageButton
					.getLayoutParams();

			AnimationSet animationset = new AnimationSet(false);
			RotateAnimation rotateAnimation = new RotateAnimation(0.0f,
					-360.0f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setInterpolator(new LinearInterpolator());
			Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
			TranslateAnimation translateAnimation = null;
			switch (i) {
			case 1:
				translateAnimation = new TranslateAnimation(params.rightMargin
						+ w, 0.0f, 0.0f, 0.0f);
				break;

			case 2:
				translateAnimation = new TranslateAnimation(0.0f, 0.0f,
						params.bottomMargin + h, 0.0f);
				break;

			case 3:
				translateAnimation = new TranslateAnimation(-params.leftMargin
						- w, 0.0f, 0.0f, 0.0f);
				break;

			case 4:
				translateAnimation = new TranslateAnimation(0.0f, 0.0f,
						-params.topMargin - h, 0.0f);
				break;
			}
			translateAnimation.setInterpolator(new OvershootInterpolator(2F));// �����ٻ����Ķ�����Ч��

			animationset.addAnimation(rotateAnimation);// ��ת���������λ�ƶ�����ִ��
			animationset.addAnimation(alphaAnimation);
			animationset.addAnimation(translateAnimation);// �Լ��ĸ�˳���֪��Ϊʲô
			animationset.setDuration(500);
			animationset.setFillAfter(true);

			imageButton.startAnimation(animationset);
		}
	}

	/**
	 * ͼ��Ķ���(������)
	 */
	private void startIconTranslateAnimationOut() {
		final int w = menuButton.getWidth() / 2;
		final int h = menuButton.getHeight() / 2;
		for (int i = 1; i < 5; i++) {// ��1��ʼĿ���ǹ��˵�menuButton
			final ImageButton imageButton = (ImageButton) menu.getChildAt(i);
			MarginLayoutParams params = (MarginLayoutParams) imageButton
					.getLayoutParams();

			AnimationSet animationset = new AnimationSet(false);
			RotateAnimation rotateAnimation = new RotateAnimation(360.0f, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation.setInterpolator(new LinearInterpolator());
			Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
			TranslateAnimation translateAnimation = null;

			switch (i) {
			case 1:
				translateAnimation = new TranslateAnimation(0.0f,
						params.rightMargin + w, 0.0f, 0.0f);
				break;

			case 2:
				translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f,
						params.bottomMargin + h);
				break;

			case 3:
				translateAnimation = new TranslateAnimation(0f,
						-params.leftMargin - w, 0.0f, 0.0f);
				break;

			case 4:
				translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f,
						-params.topMargin - h);
				break;
			}

			animationset.addAnimation(rotateAnimation);// ��ת���������λ�ƶ�����ִ��
			animationset.addAnimation(alphaAnimation);
			animationset.addAnimation(translateAnimation);// �Լ��ĸ�˳���֪��Ϊʲô
			animationset.setDuration(500);
			animationset.setFillAfter(true);

			animationset.setAnimationListener(new AnimationListener() {

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
					imageButton.setVisibility(View.GONE);
				}
			});
			imageButton.startAnimation(animationset);
		}
	}

	/**
	 * icon��С��ʧ�Ķ���
	 */
	private Animation startShrinkIconAnimation() {
		ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
				0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(300);
		scaleAnimation.setFillAfter(true);
		return scaleAnimation;
	}

	/**
	 * icon�Ŵ󽥱���ʧ�Ķ���
	 */
	private Animation startAmplifyIconAnimation() {
		AnimationSet animationset = new AnimationSet(true);

		ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f,
				4.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

		animationset.addAnimation(scaleAnimation);
		animationset.addAnimation(alphaAnimation);
		animationset.setDuration(300);
		animationset.setFillAfter(true);

		return animationset;
	}

	/**
	 * �ҵ��ͼƬ����
	 */
	private void startFavoriteImageAnimation() {
		AnimationSet animationset = new AnimationSet(false);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setInterpolator(new OvershootInterpolator(5F));// �����ٻ����Ķ�����Ч��
		scaleAnimation.setDuration(700);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(500);
		alphaAnimation.setStartOffset(700);

		animationset.addAnimation(scaleAnimation);
		animationset.addAnimation(alphaAnimation);
		animationset.setFillAfter(true);

		animationset.setAnimationListener(new AnimationListener() {

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
				mp3Favorite.setVisibility(View.GONE);
			}
		});
		mp3Favorite.setVisibility(View.VISIBLE);
		mp3Favorite.startAnimation(animationset);
	}

	/**
	 * ר�����淭ת����
	 * 
	 * @param bitmap
	 *            ר������ͼ
	 */
	private void startTransition3dAnimation(final Bitmap bitmap) {
		final int w = mp3Cover.getWidth() / 2;
		final int h = mp3Cover.getHeight() / 2;
		final MarginLayoutParams params = (MarginLayoutParams) mp3Cover
				.getLayoutParams();

		final Rotate3dAnimation rotation1 = new Rotate3dAnimation(0.0f, 90.0f,
				params.leftMargin + w, params.topMargin + h, 300.0f, true);
		rotation1.setDuration(500);
		rotation1.setFillAfter(true);
		rotation1.setInterpolator(new AccelerateInterpolator());

		final Rotate3dAnimation rotation2 = new Rotate3dAnimation(270.0f,
				360.0f, params.leftMargin + w, params.topMargin + h, 300.0f,
				false);// ��ת����
		rotation2.setDuration(500);
		rotation2.setFillAfter(true);
		rotation2.setInterpolator(new AccelerateInterpolator());

		rotation1.setAnimationListener(new AnimationListener() {

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
				if (isFirstTransition3dAnimation) {
					isFirstTransition3dAnimation = false;
					mp3Cover.setImageBitmap(bitmap);
					mp3Cover.startAnimation(rotation2);
				}
			}
		});
		mp3Cover.startAnimation(rotation1);
	}

	/**
	 * ��ֲ��ApiDemos��ĵ�Rotate3dAnimation
	 * 
	 * �����и�bug��180�ȷ�תͼƬ�ᷴ����������ֻ�����е���תһ���ַ�ת������Ч���Ͳ��ˣ�������λȥ������
	 */
	private class Rotate3dAnimation extends Animation {
		private final float mFromDegrees;
		private final float mToDegrees;
		private final float mCenterX;
		private final float mCenterY;
		private final float mDepthZ;
		private final boolean mReverse;
		private Camera mCamera;

		/**
		 * Creates a new 3D rotation on the Y axis. The rotation is defined by
		 * its start angle and its end angle. Both angles are in degrees. The
		 * rotation is performed around a center point on the 2D space, definied
		 * by a pair of X and Y coordinates, called centerX and centerY. When
		 * the animation starts, a translation on the Z axis (depth) is
		 * performed. The length of the translation can be specified, as well as
		 * whether the translation should be reversed in time.
		 * 
		 * @param fromDegrees
		 *            the start angle of the 3D rotation
		 * @param toDegrees
		 *            the end angle of the 3D rotation
		 * @param centerX
		 *            the X center of the 3D rotation
		 * @param centerY
		 *            the Y center of the 3D rotation
		 * @param reverse
		 *            true if the translation should be reversed, false
		 *            otherwise
		 */
		public Rotate3dAnimation(float fromDegrees, float toDegrees,
				float centerX, float centerY, float depthZ, boolean reverse) {
			mFromDegrees = fromDegrees;
			mToDegrees = toDegrees;
			mCenterX = centerX;
			mCenterY = centerY;
			mDepthZ = depthZ;
			mReverse = reverse;
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
			mCamera = new Camera();
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			final float fromDegrees = mFromDegrees;
			float degrees = fromDegrees
					+ ((mToDegrees - fromDegrees) * interpolatedTime);

			final float centerX = mCenterX;
			final float centerY = mCenterY;
			final Camera camera = mCamera;

			final Matrix matrix = t.getMatrix();

			camera.save();
			if (mReverse) {
				camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
			} else {
				camera.translate(0.0f, 0.0f, mDepthZ
						* (1.0f - interpolatedTime));
			}
			camera.rotateY(degrees);
			camera.getMatrix(matrix);
			camera.restore();

			matrix.preTranslate(-centerX, -centerY);
			matrix.postTranslate(centerX, centerY);
		}
	}

}
