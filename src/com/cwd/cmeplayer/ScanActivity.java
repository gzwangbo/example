package com.cwd.cmeplayer;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.cwd.cmeplayer.adapter.ScanAdapter;
import com.cwd.cmeplayer.service.MediaService;
import com.cwd.cmeplayer.util.ScanUtil;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�����б�ҳ��</b></br>
 * 
 * <br>
 * ִ�и���ɨ��������Ҫ��ɨ��SD���������� </br>
 * 
 * @author CWD
 * @version 2013.05.19 v1.0 �󶨿ؼ� <br>
 *          2013.05.27 v1.1 �����첽ɨ�����񼰸���UI <br>
 *          2013.06.06 v1.2 ����ɨ����ɺ��˳�(ף��λ�߿�ѧ�ӳɹ�)<br>
 *          2013.08.01 v1.3 ����ɨ����ɼ�¼SharedPreferences<br>
 *          2013.08.22 v1.4 ��setResult����ΪsendBroadcast</br>
 */
public class ScanActivity extends Activity {

	private final String INFO_NORMAL = "ɨ�����";
	private final String INFO_SCAN = "����ɨ��";
	private final String INFO_WAIT = "...�ȴ�ɨ��...";
	private final String INFO_FINISH = "ɨ�����";

	private boolean scaning = true;// Ĭ������ɨ��
	private boolean canReturn = true;// Ĭ�������˳�

	private ImageButton btnReturn;// ҳ�������ť
	private Button btnScan;// ɨ�谴ť

	private TextView scanText;// ɨ��ʱ�����ָ���
	private ListView scanList;// ý���Ŀ¼�б�

	private ScanHandler handler;
	private ScanUtil manager;
	private ScanAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		btnReturn = (ImageButton) findViewById(R.id.activity_scan_ib_return);
		btnScan = (Button) findViewById(R.id.activity_scan_btn_scan);
		scanText = (TextView) findViewById(R.id.activity_scan_text);
		scanList = (ListView) findViewById(R.id.activity_scan_lv);
		btnScan.setText(INFO_NORMAL);
		scanText.setText(INFO_WAIT);

		manager = new ScanUtil(getApplicationContext());
		adapter = new ScanAdapter(getApplicationContext(),
				manager.searchAllDirectory());
		scanList.setAdapter(adapter);

		// ҳ�����
		btnReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (canReturn) {// ɨ�����ݲ����˳�
					if (!scaning) {
						sendUpdateBroadcast();// ֪ͨ��һ��ҳ�����
					}
					finish();
				}
			}
		});

		// ִ��ɨ�裬��Ϊfalseʱ�ٴε��ɨ�谴ť�˳�ɨ��
		btnScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (scaning) {
					scaning = false;
					canReturn = false;
					new ScanTask().execute();
					btnScan.setText(INFO_SCAN);
					btnScan.setEnabled(false);
				} else {
					sendUpdateBroadcast();// ֪ͨ��һ��ҳ�����
					finish();
				}
			}
		});

		handler = new ScanHandler(this);

		Intent intent = new Intent(MediaService.BROADCAST_ACTION_SERVICE);
		intent.putExtra(MediaService.INTENT_ACTIVITY,
				MediaService.ACTIVITY_SCAN);
		sendBroadcast(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!canReturn) {// ɨ�����ݲ����˳����˴�Ϊ!�ж�
				return true;
			} else {
				if (!scaning) {
					sendUpdateBroadcast();// ֪ͨ��һ��ҳ�����
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ��ΪActivity��launchMode="SingleTask"ʱ����ִ��onActivityResult��������Ϊ���͹㲥
	 */
	private void sendUpdateBroadcast() {
		Intent intent = new Intent(MainActivity.BROADCAST_ACTION_SCAN);
		sendBroadcast(intent);
	}

	/**
	 * ���ָ���
	 * 
	 * @param text
	 *            ����
	 */
	private void updateText(String text) {
		scanText.setText(text);
	}

	/**
	 * ִ��ɨ��������첽����Ƕ����
	 * 
	 * @author CWD
	 * @version 2013.05.27 v1.0 ʵ��ɨ��
	 * 
	 */
	private class ScanTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			manager.scanMusicFromSD(adapter.getPath(), handler);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SharedPreferences preferences = getSharedPreferences(
					MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
			preferences.edit().putBoolean(MainActivity.PREFERENCES_SCAN, true)
					.commit();// ���ɨ���¼
			btnScan.setText(INFO_FINISH);
			btnScan.setEnabled(true);
			canReturn = true;
		}

	}

	/**
	 * ʵʱ����UI��̬Ƕ����
	 * 
	 * ֮��������д������ΪHandler�Ǹ������ڴ�й¶�Ķ����ҿ��ǳԹ����ģ����������ÿ�����Ч����
	 * 
	 * @author CWD
	 * @version 2013.05.27 v1.0 ʵ�ָ���UI
	 */
	private static class ScanHandler extends Handler {

		private WeakReference<ScanActivity> mReference;

		public ScanHandler(ScanActivity activity) {
			// TODO Auto-generated constructor stub
			mReference = new WeakReference<ScanActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (mReference.get() != null) {
				ScanActivity theActivity = mReference.get();
				theActivity.updateText(msg.obj.toString());
			}
		}

	}

}
