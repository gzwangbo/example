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
 * <b>歌曲列表页面</b></br>
 * 
 * <br>
 * 在家谋了个打杂的差事，只能抽空晚上写了，而且有电视分神，写代码效率大不如前了。<br>
 * 有很多想法，事多以后就无力去实现了，尽量完成一个好作品！...$_$...<br>
 * 本程序所有代码文件采用GBK编码！<br>
 * <br>
 * 
 * 项目流程：LOGO界面->主界面->SlidingMenu->数据集->扫描界面->数据库->列表显示->MP3标签扫描<br>
 * ->主界面不同模式数据显示->音乐播放及数据交互->移植并实现均衡器效果VisualizerView->自定义歌词LyricView<br>
 * ->自定义动画效果PushView->实现音乐的播放控制及Service相对应的更改->各Dialog的对应显示<br>
 * ->实现主界面更为复杂和详尽的数据显示->实现仿Path菜单->实现其余动画特效->完成设置界面->优化和测试<br>
 * <br>
 * 
 * 由于Android2.3以下不支持均衡器类，所以直接最低安装要求2.3以上，这样也省了我去适配2.3以前的小屏机。<br>
 * 
 * 还存在一些问题，如对文件不存在的相关操作的逻辑自己都搞的混乱，扫描与数据库相关操作有可能会出错，
 * 播放可能产生错误，耳机线控几乎是个鸡肋，还有一些测试未发现的问题。不想写了，留给各位感兴趣的开发者了<br>
 * <br>
 * 
 * 展示歌曲列表，SlidingMenu支撑隐藏列表 <br>
 * 
 * @author CWD
 * @version 2013.05.10 v1.0 完成侧滑菜单模块的功能（已删除）<br>
 *          2013.06.03 v2.0 替换更新侧滑模块，完全替换版本v1.0<br>
 *          2013.06.24 v2.1 实现播放<br>
 *          2013.07.03 v2.2 修改BindService的时机，通过ServiceConnection更新UI<br>
 *          2013.07.29 v2.3 实现播放按钮的相关操作及程序的退出<br>
 *          2013.08.04 v2.4 实现各种Dialog对应的操作<br>
 *          2013.08.19 v2.5 实现快退、快进播放<br>
 *          2013.08.22 v2.6 新增一些广播消息的处理<br>
 *          2013.08.24 v2.7 实现更换背景图<br>
 *          2013.08.31 v3.0 修复真机上跳转后立即返回UI更新停滞的严重问题<br>
 *          2013.09.01 v3.1 实现对文件不存在的相关操作</br>
 */
public class MainActivity extends SlidingListActivity implements
		OnClickListener, OnLongClickListener, OnTouchListener,
		OnTVAnimDialogDismissListener {

	// (0~4对应SlidingAdapter的position，不可更改)
	public static final int SLIDING_MENU_SCAN = 0;// 侧滑->扫描歌曲
	public static final int SLIDING_MENU_ALL = 1;// 侧滑->全部歌曲
	public static final int SLIDING_MENU_FAVORITE = 2;// 侧滑->我的最爱
	public static final int SLIDING_MENU_FOLDER = 3;// 侧滑->文件夹
	public static final int SLIDING_MENU_EXIT = 4;// 侧滑->退出程序
	public static final int SLIDING_MENU_FOLDER_LIST = 5;// 侧滑->文件夹->文件夹列表

	public static final int DIALOG_DISMISS = 0;// 对话框消失
	public static final int DIALOG_SCAN = 1;// 扫描对话框
	public static final int DIALOG_MENU_REMOVE = 2;// 歌曲列表移除对话框
	public static final int DIALOG_MENU_DELETE = 3;// 歌曲列表提示删除对话框
	public static final int DIALOG_MENU_INFO = 4;// 歌曲详情对话框
	public static final int DIALOG_DELETE = 5;// 歌曲删除对话框

	public static final String PREFERENCES_NAME = "settings";// SharedPreferences名称
	public static final String PREFERENCES_MODE = "mode";// 存储播放模式
	public static final String PREFERENCES_SCAN = "scan";// 存储是否扫描过
	public static final String PREFERENCES_SKIN = "skin";// 存储背景图
	public static final String PREFERENCES_LYRIC = "lyric";// 存储歌词高亮颜色

	public static final String BROADCAST_ACTION_SCAN = "com.cwd.cmeplayer.action.scan";// 扫描广播标志
	public static final String BROADCAST_ACTION_MENU = "com.cwd.cmeplayer.action.menu";// 弹出菜单广播标志
	public static final String BROADCAST_ACTION_FAVORITE = "com.cwd.cmeplayer.action.favorite";// 喜爱广播标志
	public static final String BROADCAST_ACTION_EXIT = "com.cwd.cmeplayer.action.exit";// 退出程序广播标志
	public static final String BROADCAST_INTENT_PAGE = "com.cwd.cmeplayer.intent.page";// 页面状态
	public static final String BROADCAST_INTENT_POSITION = "com.cwd.cmeplayer.intent.position";// 歌曲索引

	private final String TITLE_ALL = "播放列表";
	private final String TITLE_FAVORITE = "我的最爱";
	private final String TITLE_FOLDER = "文件夹";
	private final String TITLE_NORMAL = "无音乐播放";
	private final String TIME_NORMAL = "00:00";

	private int skinId;// 背景图ID
	private int slidingPage = SLIDING_MENU_ALL;// 页面状态
	private int playerPage;// 发送给PlayerActivity的页面状态
	private int musicPosition;// 当前播放歌曲索引
	private int folderPosition;// 文件夹列表索引
	private int dialogMenuPosition;// 记住弹出歌曲列表菜单的歌曲索引

	private boolean canSkip = true;// 防止用户频繁点击造成多次解除服务绑定，true：允许解绑
	private boolean bindState = false;// 服务绑定状态

	private String mp3Current;// 歌曲当前时长
	private String mp3Duration;// 歌曲总时长
	private String dialogMenuPath;// 记住弹出歌曲列表菜单的歌曲路径

	private TextView mainTitle;// 列表标题
	private TextView mainSize;// 歌曲数量
	private TextView mainArtist;// 艺术家
	private TextView mainName;// 歌曲名称
	private TextView mainTime;// 歌曲时间
	private ImageView mainAlbum;// 专辑图片

	private ImageButton btnMenu;// 侧滑菜单按钮
	private ImageButton btnPrevious;// 上一首按钮
	private ImageButton btnPlay;// 播放和暂停按钮
	private ImageButton btnNext;// 下一首按钮

	private LinearLayout skin;// 背景图
	private LinearLayout viewBack;// 返回上一级
	private LinearLayout viewControl;// 底部播放控制视图

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
		init();// 初始化
	}

	/*
	 * 这里的部分本来是写在onStart里的，但是我发现在真机上点击跳转后立即返回不会执行onStart，但会执行onResume，
	 * 但跳转后空2秒以上再返回，就会执行onStart，这种问题如何解释。各位可以试试，也许我对生命周期理解的不透彻。
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int id = preferences.getInt(MainActivity.PREFERENCES_SKIN,
				R.drawable.skin_bg1);
		if (skinId != id) {// 判断是否更换背景图
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
				unbindService(serviceConnection);// 一定要解除绑定
			}
			serviceConnection = null;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

	/*
	 * 初始化所有相关工作
	 */
	private void init() {
		initSlidingMenu();// 先初始化侧滑相关
		initActivity();// 再初始化主界面
		initServiceConnection();// 后初始化服务绑定
	}

	/*
	 * 初始化侧滑相关
	 * 
	 * <---设置SlidingMenu的几种手势模式--->
	 * 
	 * TOUCHMODE_FULLSCREEN：全屏模式，在content页面中，滑动，可以打开SlidingMenu
	 * 
	 * TOUCHMODE_MARGIN：边缘模式，在content页面中，如果想打开SlidingMenu，
	 * 你需要在屏幕边缘滑动才可以打开SlidingMenu
	 * 
	 * TOUCHMODE_NONE：自然是不能通过手势打开啦
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
				case SLIDING_MENU_SCAN:// 扫描歌曲
					intentScanActivity();
					break;

				case SLIDING_MENU_ALL:// 全部歌曲
					if (musicAdapter.getPage() != SLIDING_MENU_ALL) {
						mainTitle.setText(TITLE_ALL);
						musicAdapter.update(SLIDING_MENU_ALL);
						mainSize.setText(musicAdapter.getCount() + "首歌曲");
					}
					break;

				case SLIDING_MENU_FAVORITE:// 我的最爱
					if (musicAdapter.getPage() != SLIDING_MENU_FAVORITE) {
						mainTitle.setText(TITLE_FAVORITE);
						musicAdapter.update(SLIDING_MENU_FAVORITE);
						mainSize.setText(musicAdapter.getCount() + "首歌曲");
					}
					break;

				case SLIDING_MENU_FOLDER:// 文件夹
					if (musicAdapter.getPage() != SLIDING_MENU_FOLDER) {
						mainTitle.setText(TITLE_FOLDER);
						musicAdapter.update(SLIDING_MENU_FOLDER);
						mainSize.setText(musicAdapter.getCount() + "个文件夹");
					}
					break;

				case SLIDING_MENU_EXIT:// 退出程序
					exitProgram();
					break;
				}
				toggle();// 关闭侧滑菜单
			}
		});
	}

	/*
	 * 初始化主界面相关
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
		mainSize.setText(musicAdapter.getCount() + "首歌曲");

		playIntent = new Intent(getApplicationContext(), MediaService.class);// 绑定服务
		receiver = new MainReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_ACTION_SCAN);
		filter.addAction(BROADCAST_ACTION_MENU);
		filter.addAction(BROADCAST_ACTION_FAVORITE);
		filter.addAction(BROADCAST_ACTION_EXIT);
		registerReceiver(receiver, filter);

		preferences = getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);// 检查是否扫描过歌曲
		if (!preferences.getBoolean(PREFERENCES_SCAN, false)) {
			ScanDialog scanDialog = new ScanDialog(this);
			scanDialog.setDialogId(DIALOG_SCAN);
			scanDialog.setOnTVAnimDialogDismissListener(this);
			scanDialog.show();
		}
	}

	/*
	 * 初始化服务绑定
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
					canSkip = true;// 重置
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
							removeList();// 文件已经不存在必须从列表移除
						}
					});
					binder.setLyricView(null, true);// 无歌词视图
				}
			}
		};
	}

	/**
	 * 带返回值跳转至扫描页面
	 */
	private void intentScanActivity() {
		Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
		startActivity(intent);
	}

	/**
	 * 从当前歌曲列表中移除
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
		mainSize.setText(musicAdapter.getCount() + "首歌曲");

		DBDao db = new DBDao(getApplicationContext());
		db.delete(dialogMenuPath);// 从数据库中删除
		db.close();// 必须关闭
		if (binder != null && musicPosition == dialogMenuPosition) {
			if (musicPosition == (size - 1)) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PREVIOUS);
			} else {
				playIntent.putExtra(MediaService.INTENT_LIST_PAGE, slidingPage);
				playIntent.putExtra(MediaService.INTENT_LIST_POSITION,
						musicPosition);
				startService(playIntent);// 从当前position处播放
			}
		}
	}

	/**
	 * 文件的删除
	 */
	private void deleteFile() {
		File file = new File(dialogMenuPath);
		if (file.delete()) {
			Toast.makeText(getApplicationContext(), "文件已被删除", Toast.LENGTH_LONG)
					.show();
			removeList();// 删除后还得更新列表
		}
	}

	/**
	 * 退出程序
	 */
	private void exitProgram() {
		stopService(playIntent);
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_main_view_back:// 返回上一级监听
			viewBack.setVisibility(View.GONE);
			mainTitle.setText(TITLE_FOLDER);
			musicAdapter.update(SLIDING_MENU_FOLDER);
			mainSize.setText(musicAdapter.getCount() + "个文件夹");
			break;

		case R.id.activity_main_ib_menu:// 侧滑按钮监听
			showMenu();
			break;

		case R.id.activity_main_view_bottom:// 底部播放控制视图监听
			if (serviceConnection != null && canSkip) {
				canSkip = false;
				unbindService(serviceConnection);// 一定要解除绑定
				bindState = false;// 将状态更新为解除绑定
			}
			Intent intent = new Intent(getApplicationContext(),
					PlayerActivity.class);
			intent.putExtra(BROADCAST_INTENT_POSITION, musicPosition);
			startActivity(intent);
			break;

		case R.id.activity_main_ib_previous:// 上一首按钮监听
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PREVIOUS);
			}
			break;

		case R.id.activity_main_ib_play:// 播放按钮监听
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_PLAY);
			}
			break;

		case R.id.activity_main_ib_next:// 下一首按钮监听
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
		case R.id.activity_main_ib_previous:// 快退
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_REWIND);
			}
			break;

		case R.id.activity_main_ib_next:// 快进
			if (binder != null) {
				binder.setControlCommand(MediaService.CONTROL_COMMAND_FORWARD);
			}
			break;
		}
		return true;// 返回true，不准再执行onClick
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
		case SLIDING_MENU_FOLDER:// 文件夹
			folderPosition = position;
			viewBack.setVisibility(View.VISIBLE);
			mainTitle.setText(FolderList.list.get(folderPosition)
					.getMusicFolder());
			musicAdapter.setFolderPosition(folderPosition);
			musicAdapter.update(SLIDING_MENU_FOLDER_LIST);
			mainSize.setText(musicAdapter.getCount() + "首歌曲");
			return;// 不执行播放

		case SLIDING_MENU_FOLDER_LIST:// 文件夹歌曲列表
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
		case DIALOG_SCAN:// 跳转至扫描页面
			intentScanActivity();
			break;

		case DIALOG_MENU_REMOVE:// 执行移除
			removeList();
			break;

		case DIALOG_MENU_DELETE:// 显示删除对话框
			DeleteDialog deleteDialog = new DeleteDialog(this);
			deleteDialog.setOnTVAnimDialogDismissListener(this);
			deleteDialog.show();
			break;

		case DIALOG_MENU_INFO:// 显示歌曲详情
			InfoDialog infoDialog = new InfoDialog(this);
			infoDialog.setOnTVAnimDialogDismissListener(this);
			infoDialog.show();
			switch (slidingPage) {// 必须在show后执行
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

		case DIALOG_DELETE:// 执行删除
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
	 * 用于接收歌曲列表菜单及将歌曲标记为最爱的广播
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
						// 从扫描页面返回的更新全部歌曲列表数据
						musicAdapter.update(SLIDING_MENU_ALL);
						mainSize.setText(musicAdapter.getCount() + "首歌曲");
					}
					return;
				}

				// 没有传值的就是通过播放界面标记我的最爱的，所以默认赋值上次点击播放的页面，为0则默认为全部歌曲
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
						menuDialog.setDialogTitle(info.getName());// 必须在show后执行
					} else if (action.equals(BROADCAST_ACTION_FAVORITE)) {
						// 因为源数据是静态的，所以赋值给info也指向了静态数据的那块内存，直接改info的数据就行
						// 不知我的理解对否。而且这算不算内存泄露？？？
						if (info.isFavorite()) {
							info.setFavorite(false);// 删除标记
							FavoriteList.list.remove(info);// 移除
						} else {
							info.setFavorite(true);// 标记为喜爱
							FavoriteList.list.add(info);// 新增
							FavoriteList.sort();// 重新排序
						}
						DBDao db = new DBDao(getApplicationContext());
						db.update(info.getName(), info.isFavorite());// 更新数据库
						db.close();// 必须关闭
						musicAdapter.update(musicAdapter.getPage());
						mainSize.setText(musicAdapter.getCount() + "首歌曲");
					}
					dialogMenuPath = info.getPath();
				}
			}
		}
	}

}
