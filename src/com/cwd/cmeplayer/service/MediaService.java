package com.cwd.cmeplayer.service;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cwd.cmeplayer.MainActivity;
import com.cwd.cmeplayer.PlayerActivity;
import com.cwd.cmeplayer.R;
import com.cwd.cmeplayer.ScanActivity;
import com.cwd.cmeplayer.SettingActivity;
import com.cwd.cmeplayer.entity.MusicInfo;
import com.cwd.cmeplayer.list.CoverList;
import com.cwd.cmeplayer.list.FavoriteList;
import com.cwd.cmeplayer.list.FolderList;
import com.cwd.cmeplayer.list.LyricList;
import com.cwd.cmeplayer.list.MusicList;
import com.cwd.cmeplayer.lyric.LyricItem;
import com.cwd.cmeplayer.lyric.LyricParser;
import com.cwd.cmeplayer.lyric.LyricView;
import com.cwd.cmeplayer.service.MediaBinder.OnServiceBinderListener;
import com.cwd.cmeplayer.util.AlbumUtil;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>���Ʋ��ŷ���</b></br>
 * 
 * @author CWD
 * @version 2013.06.23 v1.0 ʵ�ֻ������ŷ���ͨ���ӿ������ͨ�ţ������ʵĽ������� <br>
 *          2013.07.03 v1.1 ����BindService��bug���νӵ�����<br>
 *          2013.07.24 v1.2 ʵ�ֶ����ֵĿ��Ʋ���(���š���ͣ���������л���)<br>
 *          2013.08.06 v1.3 ֧�ֶ��ļ����б�����Ĳ���<br>
 *          2013.08.19 v1.4 ֧�ֿ��ˡ��������<br>
 *          2013.08.20 v1.5 ʵ��Notification��ת�ܹ��ص�ԭ���Ľ��棬����launchMode="singleTask"<br>
 *          2013.08.27 v1.6 �޸���ͣ�����²��Ÿ����ʧ������<br>
 *          2013.08.29 v1.7 ʵ�ֶ����߿ؼ��������</br>
 */
public class MediaService extends Service {

	public static final int CONTROL_COMMAND_PLAY = 0;// ����������Ż�����ͣ
	public static final int CONTROL_COMMAND_PREVIOUS = 1;// ���������һ��
	public static final int CONTROL_COMMAND_NEXT = 2;// ���������һ��
	public static final int CONTROL_COMMAND_MODE = 3;// �����������ģʽ�л�
	public static final int CONTROL_COMMAND_REWIND = 4;// �����������
	public static final int CONTROL_COMMAND_FORWARD = 5;// ����������
	public static final int CONTROL_COMMAND_REPLAY = 6;// ����������ڿ��ˡ������ļ�������

	public static final int ACTIVITY_SCAN = 0x101;// ɨ�����
	public static final int ACTIVITY_MAIN = 0x102;// ������
	public static final int ACTIVITY_PLAYER = 0x103;// ���Ž���
	public static final int ACTIVITY_SETTING = 0x104;// ���ý���

	public static final String INTENT_ACTIVITY = "activity";// ���������ĸ�����
	public static final String INTENT_LIST_PAGE = "list_page";// �б�ҳ��
	public static final String INTENT_LIST_POSITION = "list_position";// �б�ǰ��
	public static final String INTENT_FOLDER_POSITION = "folder_position";// �ļ����б�ǰ��
	public static final String BROADCAST_ACTION_SERVICE = "com.cwd.cmeplayer.action.service";// �㲥��־

	private static final int MEDIA_PLAY_ERROR = 0;
	private static final int MEDIA_PLAY_START = 1;
	private static final int MEDIA_PLAY_UPDATE = 2;
	private static final int MEDIA_PLAY_COMPLETE = 3;
	private static final int MEDIA_PLAY_UPDATE_LYRIC = 4;
	private static final int MEDIA_PLAY_REWIND = 5;
	private static final int MEDIA_PLAY_FORWARD = 6;
	private static final int MEDIA_BUTTON_ONE_CLICK = 7;
	private static final int MEDIA_BUTTON_DOUBLE_CLICK = 8;

	private final int MODE_NORMAL = 0;// ˳�򲥷ţ��ŵ����һ��ֹͣ
	private final int MODE_REPEAT_ONE = 1;// ����ѭ��
	private final int MODE_REPEAT_ALL = 2;// ȫ��ѭ��
	private final int MODE_RANDOM = 3;// �漴����
	private final int UPDATE_LYRIC_TIME = 150;// ��ʸ��¼��0.15��
	private final int UPDATE_UI_TIME = 1000;// UI���¼��1��

	private MusicInfo info;// ��������
	private List<LyricItem> lyricList;// ����б�
	private List<Integer> positionList;// �б�ǰ��ϣ�Ŀ�ļ�סǰ�������ŵ����и������������춯��

	private String mp3Path;// mp3�ļ�·��
	private String lyricPath;// ����ļ�·��

	private int mode = MODE_NORMAL;// ����ģʽ(Ĭ��˳�򲥷�)
	private int page = MainActivity.SLIDING_MENU_ALL;// �б�ҳ��(Ĭ��ȫ������)
	private int lastPage = 0;// ��ס��һ�ε��б�ҳ��
	private int position = 0;// �б�ǰ��
	private int folderPosition = 0;// �ļ����б�ǰ��
	private int mp3Current = 0;// ������ǰʱ��
	private int mp3Duration = 0;// ������ʱ��
	private int buttonClickCounts = 0;

	private boolean hasLyric = false;// �Ƿ��и��
	private boolean isCommandPrevious = false;// �Ƿ�������һ�ײ�������

	private MediaPlayer mediaPlayer;
	private MediaBinder mBinder;
	private AlbumUtil albumUtil;
	private LyricView lyricView;

	private RemoteViews remoteViews;
	private ServiceHandler mHandler;
	private ServiceReceiver receiver;
	private Notification notification;
	private SharedPreferences preferences;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mediaPlayer = new MediaPlayer();
		mHandler = new ServiceHandler(this);
		mBinder = new MediaBinder();
		albumUtil = new AlbumUtil();
		lyricList = new ArrayList<LyricItem>();
		positionList = new ArrayList<Integer>();

		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.start();
				mp3Current = 0;// ����
				prepared();// ׼������
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				removeAllMsg();// �Ƴ�������Ϣ
				mHandler.sendEmptyMessage(MEDIA_PLAY_COMPLETE);
			}
		});
		mediaPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				removeAllMsg();// �Ƴ�������Ϣ
				mp.reset();
				page = MainActivity.SLIDING_MENU_ALL;
				position = 0;
				File file = new File(mp3Path);
				if (file.exists()) {
					Toast.makeText(getApplicationContext(), "���ų���",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "�ļ��Ѳ�����",
							Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessage(MEDIA_PLAY_ERROR);
				}
				mp3Path = null;
				return true;
			}
		});
		mBinder.setOnServiceBinderListener(new OnServiceBinderListener() {

			@Override
			public void seekBarStartTrackingTouch() {
				// TODO Auto-generated method stub
				if (mediaPlayer.isPlaying()) {
					removeUpdateMsg();
				}
			}

			@Override
			public void seekBarStopTrackingTouch(int progress) {
				// TODO Auto-generated method stub
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.seekTo(progress);
					update();
				}
			}

			@Override
			public void lrc(LyricView lyricView, boolean isKLOK) {
				// TODO Auto-generated method stub
				MediaService.this.lyricView = lyricView;// ��ø����ͼ
				if (MediaService.this.lyricView != null) {
					MediaService.this.lyricView.setKLOK(isKLOK);
				}
			}

			@Override
			public void control(int command) {
				// TODO Auto-generated method stub
				switch (command) {
				case CONTROL_COMMAND_PLAY:// ��������ͣ
					if (mediaPlayer.isPlaying()) {
						pause();
					} else {
						if (mp3Path != null) {
							mediaPlayer.start();
							prepared();
						} else {// ��ָ������²���ȫ�������б�ĵ�һ��
							startServiceCommand();
						}
					}
					break;

				case CONTROL_COMMAND_PREVIOUS:// ��һ��
					previous();
					break;

				case CONTROL_COMMAND_NEXT:// ��һ��
					next();
					break;

				case CONTROL_COMMAND_MODE:// ����ģʽ
					if (mode < MODE_RANDOM) {
						mode++;
					} else {
						mode = MODE_NORMAL;
					}
					switch (mode) {
					case MODE_NORMAL:
						Toast.makeText(getApplicationContext(), "˳�򲥷�",
								Toast.LENGTH_SHORT).show();
						break;

					case MODE_REPEAT_ONE:
						Toast.makeText(getApplicationContext(), "����ѭ��",
								Toast.LENGTH_SHORT).show();
						break;

					case MODE_REPEAT_ALL:
						Toast.makeText(getApplicationContext(), "ȫ��ѭ��",
								Toast.LENGTH_SHORT).show();
						break;

					case MODE_RANDOM:
						Toast.makeText(getApplicationContext(), "�������",
								Toast.LENGTH_SHORT).show();
						break;
					}
					mBinder.modeChange(mode);
					break;

				case CONTROL_COMMAND_REWIND:// ����
					if (mediaPlayer.isPlaying()) {
						removeAllMsg();
						rewind();
					}
					break;

				case CONTROL_COMMAND_FORWARD:// ���
					if (mediaPlayer.isPlaying()) {
						removeAllMsg();
						forward();
					}
					break;

				case CONTROL_COMMAND_REPLAY:// ���ڿ��ˡ������ļ�������
					if (mediaPlayer.isPlaying()) {
						replay();
					}
					break;
				}
			}
		});
		preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		mode = preferences.getInt(MainActivity.PREFERENCES_MODE, MODE_NORMAL);// ȡ���ϴεĲ���ģʽ

		notification = new Notification();// ֪ͨ�����
		notification.icon = R.drawable.ic_launcher;
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, new Intent(getApplicationContext(),
						MainActivity.class), 0);
		remoteViews = new RemoteViews(getPackageName(),
				R.layout.notification_item);

		receiver = new ServiceReceiver();// ע��㲥
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
		intentFilter.addAction(BROADCAST_ACTION_SERVICE);
		registerReceiver(receiver, intentFilter);

		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);// ��ȡ�绰ͨѶ����
		telephonyManager.listen(new ServicePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);// ����һ���������󣬼����绰״̬�ı��¼�
	}

	// sdk2.0���ϻ��ǲ�ʹ��onStart�˰�...
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null && !bundle.isEmpty()) {
				page = bundle.getInt(INTENT_LIST_PAGE, 0);
				position = bundle.getInt(INTENT_LIST_POSITION, 0);
				folderPosition = bundle.getInt(INTENT_FOLDER_POSITION,
						folderPosition);
				play();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer != null) {
			stopForeground(true);
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			removeAllMsg();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		preferences = getSharedPreferences(MainActivity.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		preferences.edit().putInt(MainActivity.PREFERENCES_MODE, mode).commit();// �����ϴεĲ���ģʽ
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		lyricView = null;
		removeAllMsg();// �Ƴ�������Ϣ
		return true;// һ������true������ִ��onRebind
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
		if (mediaPlayer.isPlaying()) {// ������ڲ������°󶨷����ʱ������ע��
			prepared();// ��Ϊ��Ϣ�Ѿ��Ƴ���������Ҫ���¿������²���
		} else {
			if (mp3Path != null) {// ��ͣԭ�Ȳ������¿�ҳ����Ҫ�ָ�ԭ�ȵ�״̬
				mp3Duration = mediaPlayer.getDuration();
				info.setMp3Duration(mp3Duration);
				CoverList.cover = albumUtil.scanAlbumImage(info.getPath());
				mBinder.playStart(info);
				mp3Current = mediaPlayer.getCurrentPosition();
				mBinder.playUpdate(mp3Current);
				mBinder.playPause();
			}
		}
		mBinder.modeChange(mode);
	}

	/**
	 * ���Ų���
	 */
	private void play() {
		int size = 0;
		switch (page) {
		case MainActivity.SLIDING_MENU_ALL:
			size = MusicList.list.size();
			if (size > 0) {
				info = MusicList.list.get(position);
			}
			break;

		case MainActivity.SLIDING_MENU_FAVORITE:
			size = FavoriteList.list.size();
			if (size > 0) {
				info = FavoriteList.list.get(position);
			}
			break;

		case MainActivity.SLIDING_MENU_FOLDER_LIST:
			size = FolderList.list.get(folderPosition).getMusicList().size();
			if (size > 0) {
				info = FolderList.list.get(folderPosition).getMusicList()
						.get(position);
			}
			break;
		}
		if (size > 0) {
			mp3Path = info.getPath();
			lyricPath = LyricList.map.get(info.getFile());
			if (mp3Path != null) {
				initMedia();// �ȳ�ʼ������
				initLrc();// �ٳ�ʼ�����
			}
			lastPage = page;
			if (!isCommandPrevious) {
				positionList.add(position);
			}
			isCommandPrevious = false;
		}
	}

	/**
	 * �Զ����Ų���
	 */
	private void autoPlay() {
		if (mode == MODE_NORMAL) {
			if (position != getSize() - 1) {
				next();
			} else {
				mBinder.playPause();
			}
		} else if (mode == MODE_REPEAT_ONE) {
			play();
		} else {
			next();
		}
	}

	/**
	 * ��һ�ײ���
	 */
	private void previous() {
		int size = getSize();
		if (size > 0) {
			isCommandPrevious = true;
			if (mode == MODE_RANDOM) {
				if (lastPage == page) {
					if (positionList.size() > 1) {
						positionList.remove(positionList.size() - 1);
						position = positionList.get(positionList.size() - 1);
					} else {
						position = (int) (Math.random() * size);
					}
				} else {
					positionList.clear();
					position = (int) (Math.random() * size);
				}
			} else {
				if (position == 0) {
					position = size - 1;
				} else {
					position--;
				}
			}
			startServiceCommand();
		}
	}

	/**
	 * ��һ�ײ���
	 */
	private void next() {
		int size = getSize();
		if (size > 0) {
			if (mode == MODE_RANDOM) {
				position = (int) (Math.random() * size);
			} else {
				if (position == size - 1) {
					position = 0;
				} else {
					position++;
				}
			}
			startServiceCommand();
		}
	}

	/**
	 * ����
	 */
	private void rewind() {
		int current = mp3Current - 1000;
		mp3Current = current > 0 ? current : 0;
		mBinder.playUpdate(mp3Current);
		mHandler.sendEmptyMessageDelayed(MEDIA_PLAY_REWIND, 100);
	}

	/**
	 * ���
	 */
	private void forward() {
		int current = mp3Current + 1000;
		mp3Current = current < mp3Duration ? current : mp3Duration;
		mBinder.playUpdate(mp3Current);
		mHandler.sendEmptyMessageDelayed(MEDIA_PLAY_FORWARD, 100);
	}

	/**
	 * ���ڿ��ˡ������ļ�������
	 */
	private void replay() {
		if (mHandler.hasMessages(MEDIA_PLAY_REWIND)) {
			mHandler.removeMessages(MEDIA_PLAY_REWIND);
		}
		if (mHandler.hasMessages(MEDIA_PLAY_FORWARD)) {
			mHandler.removeMessages(MEDIA_PLAY_FORWARD);
		}
		mediaPlayer.seekTo(mp3Current);
		mHandler.sendEmptyMessage(MEDIA_PLAY_UPDATE);
		if (lyricView != null && hasLyric) {
			lyricView.setSentenceEntities(lyricList);
			mHandler.sendEmptyMessageDelayed(MEDIA_PLAY_UPDATE_LYRIC,
					UPDATE_LYRIC_TIME);// ֪ͨˢ�¸��
		}
	}

	/**
	 * ����б��������
	 * 
	 * @return ����
	 */
	private int getSize() {
		int size = 0;
		switch (page) {
		case MainActivity.SLIDING_MENU_ALL:
			size = MusicList.list.size();
			break;

		case MainActivity.SLIDING_MENU_FAVORITE:
			size = FavoriteList.list.size();
			break;

		case MainActivity.SLIDING_MENU_FOLDER_LIST:
			size = FolderList.list.get(folderPosition).getMusicList().size();
			break;
		}
		return size;
	}

	/**
	 * �ڲ�ģ�������������������
	 */
	private void startServiceCommand() {
		Intent intent = new Intent(getApplicationContext(), MediaService.class);
		intent.putExtra(INTENT_LIST_PAGE, page);
		intent.putExtra(INTENT_LIST_POSITION, position);
		startService(intent);
	}

	/**
	 * ��ʼ��ý�岥����
	 */
	private void initMedia() {
		try {
			removeAllMsg();// �������²�����Ҫ�Ƴ�������Ϣ
			mediaPlayer.reset();
			mediaPlayer.setDataSource(mp3Path);
			mediaPlayer.prepareAsync();
			stopForeground(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ�����
	 */
	private void initLrc() {
		hasLyric = false;
		if (lyricPath != null) {
			try {
				LyricParser parser = new LyricParser(lyricPath);
				lyricList = parser.parser();
				hasLyric = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (lyricView != null) {
				lyricView.clear();
			}
		}
	}

	/**
	 * ׼���ÿ�ʼ���Ź���
	 */
	private void prepared() {
		mHandler.sendEmptyMessage(MEDIA_PLAY_START);// ֪ͨ�����Ѳ���
		if (lyricView != null) {
			// lyricView.clear(); ��ջ�����е�lyricListҲ��գ��ڴ湲����???
			if (hasLyric) {
				lyricView.setSentenceEntities(lyricList);
				mHandler.sendEmptyMessageDelayed(MEDIA_PLAY_UPDATE_LYRIC,
						UPDATE_LYRIC_TIME);// ֪ͨˢ�¸��
			}
		}
	}

	/**
	 * ��ʼ���ţ������ʱ���AudioSessionId������������UI����
	 */
	private void start() {
		mp3Duration = mediaPlayer.getDuration();
		info.setMp3Duration(mp3Duration);
		info.setAudioSessionId(mediaPlayer.getAudioSessionId());
		CoverList.cover = albumUtil.scanAlbumImage(info.getPath());
		mBinder.playStart(info);
		mHandler.sendEmptyMessageDelayed(MEDIA_PLAY_UPDATE, UPDATE_UI_TIME);

		final String artist = info.getArtist();
		final String name = info.getName();
		notification.tickerText = artist + " - " + name;
		if (CoverList.cover == null) {
			remoteViews.setImageViewResource(R.id.notification_item_album,
					R.drawable.main_img_album);
		} else {
			remoteViews.setImageViewBitmap(R.id.notification_item_album,
					CoverList.cover);
		}
		remoteViews.setTextViewText(R.id.notification_item_name, name);
		remoteViews.setTextViewText(R.id.notification_item_artist, artist);
		notification.contentView = remoteViews;
		startForeground(1, notification);// id��Ϊ0��������ʾNotification
	}

	/**
	 * ����UI������MediaPlayer.getCurrentPosition()��bug�����أ��о�ָ�Ĳ���ʱ�����֡����
	 * ����Handler��������Ҫ����ʱ�䣬��Ȼ���1�����ʱʱ�䣬��������ɾͲ�ֹ1���ʱ�䣬
	 * ���Ի��������������������������Խ��ĸо�Խ���ԣ�����ͨ��������ʵ�֣���������������������������
	 */
	private void update() {
		// TODO Auto-generated method stub
		mp3Current = mediaPlayer.getCurrentPosition();
		mBinder.playUpdate(mp3Current);
		mHandler.sendEmptyMessageDelayed(MEDIA_PLAY_UPDATE, UPDATE_UI_TIME);
	}

	/**
	 * ��ͣ����
	 */
	private void pause() {
		removeAllMsg();// �Ƴ�������Ϣ
		mediaPlayer.pause();
		mBinder.playPause();
		stopForeground(true);
	}

	/**
	 * �Ƴ�����UI����Ϣ
	 */
	private void removeUpdateMsg() {
		if (mHandler != null && mHandler.hasMessages(MEDIA_PLAY_UPDATE)) {
			mHandler.removeMessages(MEDIA_PLAY_UPDATE);
		}
	}

	/**
	 * �������
	 */
	private void complete() {
		// TODO Auto-generated method stub
		mBinder.playComplete();
		mBinder.playUpdate(mp3Duration);
		autoPlay();
	}

	/**
	 * ���ų���
	 */
	private void error() {
		mBinder.playError();
		mBinder.playPause();
		positionList.clear();
	}

	/**
	 * ˢ�¸��
	 */
	private void updateLrcView() {
		if (lyricList.size() > 0) {
			lyricView.setIndex(getLrcIndex(mediaPlayer.getCurrentPosition(),
					mp3Duration));
			lyricView.invalidate();
			mHandler.sendEmptyMessageDelayed(MEDIA_PLAY_UPDATE_LYRIC,
					UPDATE_LYRIC_TIME);
		}
	}

	/**
	 * �Ƴ����¸�ʵ���Ϣ
	 */
	private void removeUpdateLrcViewMsg() {
		if (mHandler != null && mHandler.hasMessages(MEDIA_PLAY_UPDATE_LYRIC)) {
			mHandler.removeMessages(MEDIA_PLAY_UPDATE_LYRIC);
		}
	}

	/**
	 * �Ƴ�������Ϣ
	 */
	private void removeAllMsg() {
		removeUpdateMsg();
		removeUpdateLrcViewMsg();
	}

	/**
	 * �����߿�-�����������¼�
	 */
	private void buttonOneClick() {
		buttonClickCounts++;
		mHandler.sendEmptyMessageDelayed(MEDIA_BUTTON_DOUBLE_CLICK, 300);
	}

	/**
	 * �����߿�-��Ӧ������˫���¼�
	 */
	private void buttonDoubleClick() {
		if (buttonClickCounts == 1) {
			mBinder.setControlCommand(CONTROL_COMMAND_PLAY);
		} else if (buttonClickCounts > 1) {
			mBinder.setControlCommand(CONTROL_COMMAND_NEXT);
		}
		buttonClickCounts = 0;
	}

	/**
	 * ���ͬ������
	 */
	private int[] getLrcIndex(int currentTime, int duration) {
		int index = 0;
		int size = lyricList.size();
		if (currentTime < duration) {
			for (int i = 0; i < size; i++) {
				if (i < size - 1) {
					if (currentTime < lyricList.get(i).getTime() && i == 0) {
						index = i;
					}
					if (currentTime > lyricList.get(i).getTime()
							&& currentTime < lyricList.get(i + 1).getTime()) {
						index = i;
					}
				}
				if (i == size - 1 && currentTime > lyricList.get(i).getTime()) {
					index = i;
				}
			}
		}
		int temp1 = lyricList.get(index).getTime();
		int temp2 = (index == (size - 1)) ? 0 : lyricList.get(index + 1)
				.getTime() - temp1;
		return new int[] { index, currentTime, temp1, temp2 };
	}

	private class ServicePhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			if (state == TelephonyManager.CALL_STATE_RINGING
					&& mediaPlayer != null && mediaPlayer.isPlaying()) { // ����
				pause();
			}
		}
	}

	private class ServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent != null) {
				boolean isActionMediaButton = Intent.ACTION_MEDIA_BUTTON
						.equals(intent.getAction());
				if (isActionMediaButton) {// ���ڹ㲥���ȼ������ƣ��˹���δ���ܹ��ܺõ�֧��
					KeyEvent event = (KeyEvent) intent
							.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
					if (event == null) {
						return;
					}
					long eventTime = event.getEventTime() - event.getDownTime();// �������µ��ɿ���ʱ��
					if (eventTime > 1000) {
						mBinder.setControlCommand(CONTROL_COMMAND_PREVIOUS);
						abortBroadcast();// ��ֹ�㲥(���ñ�ĳ����յ��˹㲥�����ܸ���)
					} else {
						if (event.getAction() == KeyEvent.ACTION_UP) {
							mHandler.sendEmptyMessage(MEDIA_BUTTON_ONE_CLICK);
							abortBroadcast();// ��ֹ�㲥(���ñ�ĳ����յ��˹㲥�����ܸ���)
						}
					}
				} else {
					int i = intent.getIntExtra(INTENT_ACTIVITY, ACTIVITY_MAIN);
					switch (i) {
					case ACTIVITY_SCAN:
						intent = new Intent(getApplicationContext(),
								ScanActivity.class);
						break;

					case ACTIVITY_MAIN:
						intent = new Intent(getApplicationContext(),
								MainActivity.class);
						break;

					case ACTIVITY_PLAYER:
						intent = new Intent(getApplicationContext(),
								PlayerActivity.class);
						break;

					case ACTIVITY_SETTING:
						intent = new Intent(getApplicationContext(),
								SettingActivity.class);
						break;
					}
					if (mediaPlayer != null && mediaPlayer.isPlaying()) {
						notification.contentIntent = PendingIntent.getActivity(
								getApplicationContext(), 0, intent,
								PendingIntent.FLAG_UPDATE_CURRENT);
						startForeground(1, notification);// �ù㲥���ڸ�����ת���棬�簴Home������Է���ԭ������
					}
				}
			}
		}
	}

	private static class ServiceHandler extends Handler {

		private WeakReference<MediaService> reference;

		public ServiceHandler(MediaService service) {
			// TODO Auto-generated constructor stub
			reference = new WeakReference<MediaService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (reference.get() != null) {
				MediaService theService = reference.get();
				switch (msg.what) {
				case MEDIA_PLAY_START:
					theService.start();// ���ſ�ʼ
					break;

				case MEDIA_PLAY_UPDATE:
					theService.update();// ����UI
					break;

				case MEDIA_PLAY_COMPLETE:
					theService.complete();// �������
					break;

				case MEDIA_PLAY_ERROR:
					theService.error();// ���ų���
					break;

				case MEDIA_PLAY_UPDATE_LYRIC:
					theService.updateLrcView();// ˢ�¸��
					break;

				case MEDIA_PLAY_REWIND:
					theService.rewind();// �����߳�
					break;

				case MEDIA_PLAY_FORWARD:
					theService.forward();// ����߳�
					break;

				case MEDIA_BUTTON_ONE_CLICK:
					theService.buttonOneClick();// �߿ص����¼�
					break;

				case MEDIA_BUTTON_DOUBLE_CLICK:
					theService.buttonDoubleClick();// �߿�˫���¼�
					break;
				}
			}
		}
	}

}
