package com.cwd.cmeplayer;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwd.cmeplayer.adapter.MusicAdapter;
import com.cwd.cmeplayer.adapter.SlidingAdapter;
import com.cwd.cmeplayer.db.DBDao;
import com.cwd.cmeplayer.dialog.DeleteDialog;
import com.cwd.cmeplayer.dialog.InfoDialog;
import com.cwd.cmeplayer.dialog.MenuDialog;
import com.cwd.cmeplayer.dialog.ScanDialog;
import com.cwd.cmeplayer.dialog.TVAnimDialog.OnTVAnimDialogDismissListener;
import com.cwd.cmeplayer.entity.MusicInfo;
import com.cwd.cmeplayer.list.CoverList;
import com.cwd.cmeplayer.list.FavoriteList;
import com.cwd.cmeplayer.list.FolderList;
import com.cwd.cmeplayer.list.MusicList;
import com.cwd.cmeplayer.service.MediaBinder;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayCompleteListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayErrorListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayPauseListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayStartListener;
import com.cwd.cmeplayer.service.MediaBinder.OnPlayingListener;
import com.cwd.cmeplayer.service.MediaService;
import com.cwd.cmeplayer.slidingmenu.SlidingListActivity;
import com.cwd.cmeplayer.slidingmenu.SlidingMenu;
import com.cwd.cmeplayer.util.FormatUtil;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�����б�ҳ��</b></br>
 * 
 * <br>
 * �ڼ�ı�˸����ӵĲ��£�ֻ�ܳ������д�ˣ������е��ӷ���д����Ч�ʴ���ǰ�ˡ�<br>
 * �кܶ��뷨���¶��Ժ������ȥʵ���ˣ��������һ������Ʒ��...$_$...<br>
 * ���������д����ļ�����GBK���룡<br>
 * <br>
 * 
 * ��Ŀ���̣�LOGO����->������->SlidingMenu->���ݼ�->ɨ�����->���ݿ�->�б���ʾ->MP3��ǩɨ��<br>
 * ->�����治ͬģʽ������ʾ->���ֲ��ż����ݽ���->��ֲ��ʵ�־�����Ч��VisualizerView->�Զ�����LyricView<br>
 * ->�Զ��嶯��Ч��PushView->ʵ�����ֵĲ��ſ��Ƽ�Service���Ӧ�ĸ���->��Dialog�Ķ�Ӧ��ʾ<br>
 * ->ʵ���������Ϊ���Ӻ��꾡��������ʾ->ʵ�ַ�Path�˵�->ʵ�����ද����Ч->������ý���->�Ż��Ͳ���<br>
 * <br>
 * 
 * ����Android2.3���²�֧�־������࣬����ֱ����Ͱ�װҪ��2.3���ϣ�����Ҳʡ����ȥ����2.3��ǰ��С������<br>
 * 
 * ������һЩ���⣬����ļ������ڵ���ز������߼��Լ�����Ļ��ң�ɨ�������ݿ���ز����п��ܻ����
 * ���ſ��ܲ������󣬶����߿ؼ����Ǹ����ߣ�����һЩ����δ���ֵ����⡣����д�ˣ�������λ����Ȥ�Ŀ�������<br>
 * <br>
 * 
 * չʾ�����б�SlidingMenu֧�������б� <br>
 * 
 * @author CWD
 * @version 2013.05.10 v1.0 ��ɲ໬�˵�ģ��Ĺ��ܣ���ɾ����<br>
 *          2013.06.03 v2.0 �滻���²໬ģ�飬��ȫ�滻�汾v1.0<br>
 *          2013.06.24 v2.1 ʵ�ֲ���<br>
 *          2013.07.03 v2.2 �޸�BindService��ʱ����ͨ��ServiceConnection����UI<br>
 *          2013.07.29 v2.3 ʵ�ֲ��Ű�ť����ز�����������˳�<br>
 *          2013.08.04 v2.4 ʵ�ָ���Dialog��Ӧ�Ĳ���<br>
 *          2013.08.19 v2.5 ʵ�ֿ��ˡ��������<br>
 *          2013.08.22 v2.6 ����һЩ�㲥��Ϣ�Ĵ���<br>
 *          2013.08.24 v2.7 ʵ�ָ�������ͼ<br>
 *          2013.08.31 v3.0 �޸��������ת����������UI����ͣ�͵���������<br>
 *          2013.09.01 v3.1 ʵ�ֶ��ļ������ڵ���ز���</br>
 */
public class MainActivity extends SlidingListActivity implements
		OnClickListener, OnLongClickListener, OnTouchListener,
		OnTVAnimDialogDismissListener {

	// (0~4��ӦSlidingAdapter��position�����ɸ���)
	public static final int SLIDING_MENU_SCAN = 0;// �໬->ɨ�����
	public static final int SLIDING_MENU_ALL = 1;// �໬->ȫ������
	public static final int SLIDING_MENU_FAVORITE = 2;// �໬->�ҵ��
	public static final int SLIDING_MENU_FOLDER = 3;// �໬->�ļ���
	public static final int SLIDING_MENU_EXIT = 4;// �໬->�˳�����
	public static final int SLIDING_MENU_FOLDER_LIST = 5;// �໬->�ļ���->�ļ����б�

	public static final int DIALOG_DISMISS = 0;// �Ի�����ʧ
	public static final int DIALOG_SCAN = 1;// ɨ��Ի���
	public static final int DIALOG_MENU_REMOVE = 2;// �����б��Ƴ��Ի���
	public static final int DIALOG_MENU_DELETE = 3;// �����б���ʾɾ���Ի���
	public static final int DIALOG_MENU_INFO = 4;// ��������Ի���
	public static final int DIALOG_DELETE = 5;// ����ɾ���Ի���

	public static final String PREFERENCES_NAME = "settings";// SharedPreferences����
	public static final String PREFERENCES_MODE = "mode";// �洢����ģʽ
	public static final String PREFERENCES_SCAN = "scan";// �洢�Ƿ�ɨ���
	public static final String PREFERENCES_SKIN = "skin";// �洢����ͼ
	public static final String PREFERENCES_LYRIC = "lyric";// �洢��ʸ�����ɫ

	public static final String BROADCAST_ACTION_SCAN = "com.cwd.cmeplayer.action.scan";// ɨ��㲥��־
	public static final String BROADCAST_ACTION_MENU = "com.cwd.cmeplayer.action.menu";// �����˵��㲥��־
	public static final String BROADCAST_ACTION_FAVORITE = "com.cwd.cmeplayer.action.favorite";// ϲ���㲥��־
	public static final String BROADCAST_ACTION_EXIT = "com.cwd.cmeplayer.action.exit";// �˳�����㲥��־
	public static final String BROADCAST_INTENT_PAGE = "com.cwd.cmeplayer.intent.page";// ҳ��״̬
	public static final String BROADCAST_INTENT_POSITION = "com.cwd.cmeplayer.intent.position";// ��������

	private final String TITLE_ALL = "�����б�";
	private final String TITLE_FAVORITE = "�ҵ��";
	private final String TITLE_FOLDER = "�ļ���";
	private final String TITLE_NORMAL = "�����ֲ���";
	private final String TIME_NORMAL = "00:00";

	private int skinId;// ����ͼID
	private int slidingPage = SLIDING_MENU_ALL;// ҳ��״̬
	private int playerPage;// ���͸�PlayerActivity��ҳ��״̬
	private int musicPosition;// ��ǰ���Ÿ�������
	private int folderPosition;// �ļ����б�����
	private int dialogMenuPosition;// ��ס���������б�˵��ĸ�������

	private boolean canSkip = true;// ��ֹ�û�Ƶ�������ɶ�ν������󶨣�true��������
	private boolean bindState = false;// �����״̬

	private String mp3Current;// ������ǰʱ��
	private String mp3Duration;// ������ʱ��
	private String dialogMenuPath;// ��ס���������б�˵��ĸ���·��

	private TextView mainTitle;// �б����
	private TextView mainSize;// ��������
	private TextView mainArtist;// ������
	private TextView mainName;// ��������
	private TextView mainTime;// ����ʱ��
	private ImageView mainAlbum;// ר��ͼƬ

	private ImageButton btnMenu;// �໬�˵���ť
	private ImageButton btnPrevious;// ��һ�װ�ť
	private ImageButton btnPlay;// ���ź���ͣ��ť
	private ImageButton btnNext;// ��һ�װ�ť

	private LinearLayout skin;// ����ͼ
	private LinearLayout viewBack;// ������һ��
	private LinearLayout viewControl;// �ײ����ſ�����ͼ

	private Intent playIntent;
	private MediaBinder binder;
	private MainReceiver receiver;
	private SlidingMenu slidingMenu;
	private MusicAdapter musicAdapter;
	private SharedPreferences preferences;
	private ServiceConnection serviceConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();// ��ʼ��
	}

	/*
	 * ����Ĳ��ֱ�����д��onStart��ģ������ҷ���������ϵ����ת���������ز���ִ��onStart������ִ��onResume��
	 * ����ת���2�������ٷ��أ��ͻ�ִ��onStart������������ν��͡���λ�������ԣ�Ҳ���Ҷ������������Ĳ�͸����
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int id = preferences.getInt(MainActivity.PREFERENCES_SKIN,
				R.drawable.skin_bg1);
		if (skinId != id) {// �ж��Ƿ��������ͼ
			skinId = id;
			skin.setBackgroundResource(skinId);
		}

		Intent intent = new Intent(MediaService.BROADCAST_ACTION_SERVICE);
		intent.putExtra(MediaService.INTENT_ACTIVITY,
				MediaService.ACTIVITY_MAIN);
		sendBroadcast(intent);

		bindState = bindService(playIntent, serviceConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (serviceConnection != null) {
			if (bindState) {
				unbindService(serviceConnection);// һ��Ҫ�����
			}
			serviceConnection = null;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

	/*
	 * ��ʼ��������ع���
	 */
	private void init() {
		initSlidingMenu();// �ȳ�ʼ���໬���
		initActivity();// �ٳ�ʼ��������
		initServiceConnection();// ���ʼ�������
	}

	/*
	 * ��ʼ���໬���
	 * 
	 * <---����SlidingMenu�ļ�������ģʽ--->
	 * 
	 * TOUCHMODE_FULLSCREEN��ȫ��ģʽ����contentҳ���У����������Դ�SlidingMenu
	 * 
	 * TOUCHMODE_MARGIN����Եģʽ����contentҳ���У�������SlidingMenu��
	 * ����Ҫ����Ļ��Ե�����ſ��Դ�SlidingMenu
	 * 
	 * TOUCHMODE_NONE����Ȼ�ǲ���ͨ�����ƴ���
	 */
	private void initSlidingMenu() {
		setBehindContentView(R.layout.activity_main_sliding);

		slidingMenu = getSlidingMenu();
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setShadowWidth(20);

		ListView listView = (ListView) slidingMenu.getMenu().findViewById(
				R.id.activity_main_sliding_list);
		listView.setAdapter(new SlidingAdapter(getApplicationContext()));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (viewBack.getVisibility() != View.GONE) {
					viewBack.setVisibility(View.GONE);
				}
				switch (position) {
				case SLIDING_MENU_SCAN:// ɨ�����
					intentScanActivity();
					break;

				case SLIDING_MENU_ALL:// ȫ������
					if (musicAdapter.getPage() != SLIDING_MENU_ALL) {
						mainTitle.setText(TITLE_ALL);
						musicAdapter.update(SLIDING_MENU_ALL);
						mainSize.setText(musicAdapter.getCount() + "�׸���");
					}
					break;

				case SLIDING_MENU_FAVORITE:// �ҵ��
					if (musicAdapter.getPage() != SLIDING_MENU_FAVORITE) {
						mainTitle.setText(TITLE_FAVORITE);
						musicAdapter.update(SLIDING_MENU_FAVORITE);
						mainSize.setText(musicAdapter.getCount() + "�׸���");
					}
					break;

				case SLIDING_MENU_FOLDER:// �ļ���
					if (musicAdapter.getPage() != SLIDING_MENU_FOLDER) {
						mainTitle.setText(TITLE_FOLDER);
						musicAdapter.update(SLIDING_MENU_FOLDER);
						mainSize.setText(musicAdapter.getCount() + "���ļ���");
					}
					break;

				case SLIDING_MENU_EXIT:// �˳�����
					exitProgram();
					break;
				}
				toggle();// �رղ໬�˵�
			}
		});
	}

	/*
	 * ��ʼ�����������
	 */
	private void initActivity() {
		skin = (LinearLayout) findViewById(R.id.activity_main_skin);
		btnMenu = (ImageButton) findViewById(R.id.activity_main_ib_menu);
		mainTitle = (TextView) findViewById(R.id.activity_main_tv_title);
		mainSize = (TextView) findViewById(R.id.activity_main_tv_count);
		mainArtist = (TextView) findViewById(R.id.activity_main_tv_artist);
		mainName = (TextView) findViewById(R.id.activity_main_tv_name);
		mainTime = (TextView) findViewById(R.id.activity_main_tv_time);
		mainAlbum = (ImageView) findViewById(R.id.activity_main_iv_album);
		viewBack = (LinearLayout) findViewById(R.id.activity_main_view_back);
		viewControl = (LinearLayout) findViewById(R.id.activity_main_view_bottom);
		btnPrevious = (ImageButton) findViewById(R.id.activity_main_ib_previous);
		btnPlay = (ImageButton) findViewById(R.id.activity_main_ib_play);
		btnNext = (ImageButton) findViewById(R.id.activity_main_ib_next);

		mainTitle.setText(TITLE_ALL);
		mainName.setText(TITLE_NORMAL);
		mainTime.setText(TIME_NORMAL + " - " + TIME_NORMAL);
		viewBack.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		viewControl.setOnClickListener(this);
		btnPrevious.setOnClickListener(this);
		btnPlay.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnPrevious.setOnLongClickListener(this);
		btnNext.setOnLongClickListener(this);
		btnPrevious.setOnTouchListener(this);
		btnNext.setOnTouchListener(this);

		musicAdapter = new MusicAdapter(getApplicationContext(),
				SLIDING_MENU_ALL);
		setListAdapter(musicAdapter);
		mainSize.setText(musicAdapter.getCount() + "�׸���");

		playIntent = new Intent(getApplicationContext(), MediaService.class);// �󶨷���
		receiver = new MainReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_ACTION_SCAN);
		filter.addAction(BROADCAST_ACTION_MENU);
		filter.addAction(BROADCAST_ACTION_FAVORITE);
		filter.addAction(BROADCAST_ACTION_EXIT);
		registerReceiver(receiver, filter);

		preferences = getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);// ����Ƿ�ɨ�������
		if (!preferences.getBoolean(PREFERENCES_SCAN, false)) {
			ScanDialog scanDialog = new ScanDialog(this);
			scanDialog.setDialogId(DIALOG_SCAN);
			scanDialog.setOnTVAnimDialogDismissListener(this);
			scanDialog.show();
		}
	}

	/*
	 * ��ʼ�������
	 */
	private void initServiceConnection() {
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
					canSkip = true;// ����
					binder.setOnPlayStartListener(new OnPlayStartListener() {

						@Override
						public void onStart(MusicInfo info) {
							// TODO Auto-generated method stub
							playerPage = musicAdapter.getPage();
							mainArtist.setText(info.getArtist());
							mainName.setText(info.getName());
							mp3Duration = info.getTime();
							if (mp3Current == null) {
								mainTime.setText(TIME_NORMAL + " - "
										+ mp3Duration);
							} else {
								mainTime.setText(mp3Current + " - "
										+ mp3Duration);
							}
							if (CoverList.cover == null) {
								mainAlbum
										.setImageResource(R.drawable.main_img_album);
							} else {
								mainAlbum.setImageBitmap(CoverList.cover);
							}
							btnPlay.setImageResource(R.drawable.main_btn_pause);
						}
					});
					binder.setOnPlayingListener(new OnPlayingListener() {

						@Override
						public void onPlay(int currentPosition) {
							// TODO Auto-generated method stub
							mp3Current = FormatUtil.formatTime(currentPosition);
							mainTime.setText(mp3Current + " - " + mp3Duration);
						}
					});
					binder.setOnPlayPauseListener(new OnPlayPauseListener() {

						@Override
						public void onPause() {
							// TODO Auto-generated method stub
							btnPlay.setImageResource(R.drawable.main_btn_play);
						}
					});
					binder.setOnPlayCompletionListener(new OnPlayCompleteListener() {

						@Override
						public void onPlayComplete() {
							// TODO Auto-generated method stub
							mp3Current = null;
						}
					});
					binder.setOnPlayErrorListener(new OnPlayErrorListener() {

						@Override
						public void onPlayError() {
							// TODO Auto-generated method stub
							dialogMenuPosition = musicPosition;
							removeList();// �ļ��Ѿ������ڱ�����б��Ƴ�
						}
					});
					binder.setLyricView(null, true);// �޸����ͼ
				}
			}
		};
	}

	/**
	 * ������ֵ��ת��ɨ��ҳ��
	 */
	private void intentScanActivity() {
		Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
		startActivity(intent);
	}

	/**
	 * �ӵ�ǰ�����б����Ƴ�
	 */
	private void removeList() {
		MusicInfo info = null;
		int size = 0;
		switch (slidingPage) {
		case MainActivity.SLIDING_MENU_ALL:
			size = MusicList.list.size();
			info = MusicList.list.get(dialogMenuPosition);
			break;

		case MainActivity.SLIDING_MENU_FAVORITE:
			size = FavoriteList.list.size();
			info = FavoriteList.list.get(dialogMenuPosition);
			break;

		case MainActivity.SLIDING_MENU_FOLDER_LIST:
			size = FolderList.list.get(folderPosition).getMusicList().size();
			info = FolderList.list.get(folderPosition).getMusicList()
					.get(dialogMenuPosition);
			break;
		}
		if (dialogMenuPath == null) {
			dialogMenuPath = info.getPath();
		}
		MusicList.list.remove(info);
		FavoriteList.list.remove(info);
		for (int i = 0; i < FolderList.list.size(); i++) {
			FolderList.list.get(i).getMusicList().remove(info);
		}
		musicAdapter.update(slidingPage);
		mainSize.setText(musicAdapter.getCount() + "�׸���");

		DBDao db = new DBDao(getApplicationContext());
		db.delete(dialogMenuPath);// �����ݿ���ɾ��
		db.close();// ����ر�
		if (binder != null && musicPosition == dialogMenuPosition) {
			if (musicPosition == (size - 1)) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PREVIOUS);
			} else {
				playIntent.putExtra(MediaService.INTENT_LIST_PAGE, slidingPage);
				playIntent.putExtra(MediaService.INTENT_LIST_POSITION,
						musicPosition);
				startService(playIntent);// �ӵ�ǰposition������
			}
		}
	}

	/**
	 * �ļ���ɾ��
	 */
	private void deleteFile() {
		File file = new File(dialogMenuPath);
		if (file.delete()) {
			Toast.makeText(getApplicationContext(), "�ļ��ѱ�ɾ��", Toast.LENGTH_LONG)
					.show();
			removeList();// ɾ���󻹵ø����б�
		}
	}

	/**
	 * �˳�����
	 */
	private void exitProgram() {
		stopService(playIntent);
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_main_view_back:// ������һ������
			viewBack.setVisibility(View.GONE);
			mainTitle.setText(TITLE_FOLDER);
			musicAdapter.update(SLIDING_MENU_FOLDER);
			mainSize.setText(musicAdapter.getCount() + "���ļ���");
			break;

		case R.id.activity_main_ib_menu:// �໬��ť����
			showMenu();
			break;

		case R.id.activity_main_view_bottom:// �ײ����ſ�����ͼ����
			if (serviceConnection != null && canSkip) {
				canSkip = false;
				unbindService(serviceConnection);// һ��Ҫ�����
				bindState = false;// ��״̬����Ϊ�����
			}
			Intent intent = new Intent(getApplicationContext(),
					PlayerActivity.class);
			intent.putExtra(BROADCAST_INTENT_POSITION, musicPosition);
			startActivity(intent);
			break;

		case R.id.activity_main_ib_previous:// ��һ�װ�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PREVIOUS);
			}
			break;

		case R.id.activity_main_ib_play:// ���Ű�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PLAY);
			}
			break;

		case R.id.activity_main_ib_next:// ��һ�װ�ť����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_NEXT);
			}
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_main_ib_previous:// ����
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_REWIND);
			}
			break;

		case R.id.activity_main_ib_next:// ���
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
		if (binder != null && event.getAction() == MotionEvent.ACTION_UP) {
			binder.setControlCommand(MediaService.CONTROL_COMMAND_REPLAY);
		}
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		slidingPage = musicAdapter.getPage();
		playIntent.putExtra(MediaService.INTENT_LIST_PAGE, slidingPage);
		musicPosition = position;
		switch (slidingPage) {
		case SLIDING_MENU_FOLDER:// �ļ���
			folderPosition = position;
			viewBack.setVisibility(View.VISIBLE);
			mainTitle.setText(FolderList.list.get(folderPosition)
					.getMusicFolder());
			musicAdapter.setFolderPosition(folderPosition);
			musicAdapter.update(SLIDING_MENU_FOLDER_LIST);
			mainSize.setText(musicAdapter.getCount() + "�׸���");
			return;// ��ִ�в���

		case SLIDING_MENU_FOLDER_LIST:// �ļ��и����б�
			playIntent.putExtra(MediaService.INTENT_FOLDER_POSITION,
					folderPosition);
			break;
		}
		playIntent.putExtra(MediaService.INTENT_LIST_POSITION, musicPosition);
		startService(playIntent);
	}

	@Override
	public void onDismiss(int dialogId) {
		// TODO Auto-generated method stub
		switch (dialogId) {
		case DIALOG_SCAN:// ��ת��ɨ��ҳ��
			intentScanActivity();
			break;

		case DIALOG_MENU_REMOVE:// ִ���Ƴ�
			removeList();
			break;

		case DIALOG_MENU_DELETE:// ��ʾɾ���Ի���
			DeleteDialog deleteDialog = new DeleteDialog(this);
			deleteDialog.setOnTVAnimDialogDismissListener(this);
			deleteDialog.show();
			break;

		case DIALOG_MENU_INFO:// ��ʾ��������
			InfoDialog infoDialog = new InfoDialog(this);
			infoDialog.setOnTVAnimDialogDismissListener(this);
			infoDialog.show();
			switch (slidingPage) {// ������show��ִ��
			case MainActivity.SLIDING_MENU_ALL:
				infoDialog.setInfo(MusicList.list.get(dialogMenuPosition));
				break;

			case MainActivity.SLIDING_MENU_FAVORITE:
				infoDialog.setInfo(FavoriteList.list.get(dialogMenuPosition));
				break;

			case MainActivity.SLIDING_MENU_FOLDER_LIST:
				infoDialog.setInfo(FolderList.list.get(folderPosition)
						.getMusicList().get(dialogMenuPosition));
				break;
			}
			break;

		case DIALOG_DELETE:// ִ��ɾ��
			deleteFile();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ���ڽ��ո����б�˵������������Ϊ��Ĺ㲥
	 */
	private class MainReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent != null) {
				final String action = intent.getAction();

				if (action.equals(BROADCAST_ACTION_EXIT)) {
					exitProgram();
					return;
				} else if (action.equals(BROADCAST_ACTION_SCAN)) {
					if (musicAdapter != null) {
						// ��ɨ��ҳ�淵�صĸ���ȫ�������б�����
						musicAdapter.update(SLIDING_MENU_ALL);
						mainSize.setText(musicAdapter.getCount() + "�׸���");
					}
					return;
				}

				// û�д�ֵ�ľ���ͨ�����Ž������ҵ���ģ�����Ĭ�ϸ�ֵ�ϴε�����ŵ�ҳ�棬Ϊ0��Ĭ��Ϊȫ������
				slidingPage = intent.getIntExtra(BROADCAST_INTENT_PAGE,
						playerPage == 0 ? SLIDING_MENU_ALL : playerPage);
				dialogMenuPosition = intent.getIntExtra(
						BROADCAST_INTENT_POSITION, 0);
				MusicInfo info = null;
				switch (slidingPage) {
				case MainActivity.SLIDING_MENU_ALL:
					info = MusicList.list.get(dialogMenuPosition);
					break;

				case MainActivity.SLIDING_MENU_FAVORITE:
					info = FavoriteList.list.get(dialogMenuPosition);
					break;

				case MainActivity.SLIDING_MENU_FOLDER_LIST:
					info = FolderList.list.get(folderPosition).getMusicList()
							.get(dialogMenuPosition);
					break;
				}

				if (info != null) {
					if (action.equals(BROADCAST_ACTION_MENU)) {
						MenuDialog menuDialog = new MenuDialog(
								MainActivity.this);
						menuDialog
								.setOnTVAnimDialogDismissListener(MainActivity.this);
						menuDialog.show();
						menuDialog.setDialogTitle(info.getName());// ������show��ִ��
					} else if (action.equals(BROADCAST_ACTION_FAVORITE)) {
						// ��ΪԴ�����Ǿ�̬�ģ����Ը�ֵ��infoҲָ���˾�̬���ݵ��ǿ��ڴ棬ֱ�Ӹ�info�����ݾ���
						// ��֪�ҵ����Է񡣶������㲻���ڴ�й¶������
						if (info.isFavorite()) {
							info.setFavorite(false);// ɾ�����
							FavoriteList.list.remove(info);// �Ƴ�
						} else {
							info.setFavorite(true);// ���Ϊϲ��
							FavoriteList.list.add(info);// ����
							FavoriteList.sort();// ��������
						}
						DBDao db = new DBDao(getApplicationContext());
						db.update(info.getName(), info.isFavorite());// �������ݿ�
						db.close();// ����ر�
						musicAdapter.update(musicAdapter.getPage());
						mainSize.setText(musicAdapter.getCount() + "�׸���");
					}
					dialogMenuPath = info.getPath();
				}
			}
		}
	}

}
