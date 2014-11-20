package com.cwd.cmeplayer.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cwd.cmeplayer.R;
import com.cwd.cmeplayer.entity.MusicInfo;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>��ʾ������ϸ��Ϣ�Ի���</b></br>
 * 
 * <br>
 * �����ӻ�����Ч���ĵ����Ի���</br>
 * 
 * @author CWD
 * @version 2013.08.05 v1.0 ��ʾ�Ի������
 */
public class InfoDialog extends TVAnimDialog {

	private TextView name;
	private TextView artist;
	private TextView album;
	private TextView genre;
	private TextView time;
	private TextView format;
	private TextView kbps;
	private TextView size;
	private TextView years;
	private TextView hz;
	private TextView path;
	private Button button;

	public InfoDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public InfoDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected InfoDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_info);

		name = (TextView) findViewById(R.id.dialog_info_name);
		artist = (TextView) findViewById(R.id.dialog_info_artist);
		album = (TextView) findViewById(R.id.dialog_info_album);
		genre = (TextView) findViewById(R.id.dialog_info_genre);
		time = (TextView) findViewById(R.id.dialog_info_time);
		format = (TextView) findViewById(R.id.dialog_info_format);
		kbps = (TextView) findViewById(R.id.dialog_info_kbps);
		size = (TextView) findViewById(R.id.dialog_info_size);
		years = (TextView) findViewById(R.id.dialog_info_years);
		hz = (TextView) findViewById(R.id.dialog_info_hz);
		path = (TextView) findViewById(R.id.dialog_info_path);
		button = (Button) findViewById(R.id.dialog_info_btn_ok);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});

	}

	/**
	 * ���ø�����Ϣ���ϲ���ʾ���������Ϣ
	 * 
	 * @param info
	 *            ������Ϣ����
	 */
	public void setInfo(MusicInfo info) {
		name.setText("����: " + info.getName());
		artist.setText("����: " + info.getArtist());
		album.setText(info.getAlbum());
		genre.setText(info.getGenre());
		time.setText("ʱ��: " + info.getTime());
		format.setText(info.getFormat());
		kbps.setText(info.getKbps());
		size.setText("��С: " + info.getSize());
		years.setText(info.getYears());
		hz.setText(info.getHz());
		path.setText("·��: " + info.getPath());
	}

}
