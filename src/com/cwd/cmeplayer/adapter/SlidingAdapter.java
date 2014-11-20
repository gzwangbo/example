package com.cwd.cmeplayer.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cwd.cmeplayer.R;

/**
 * By CWD 2013 Open Source Project
 * 
 * <br>
 * <b>�໬�˵�������</b></br>
 * ΪʲôҪ��TypedArray���μ�http://www.eoeandroid.com/thread-233677-1-1.html
 * ��http://developer
 * .android.com/guide/topics/resources/more-resources.html#TypedArray
 * 
 * @author CWD
 * @version 2013.05.12 v1.0 ʵ���б�����
 */
public class SlidingAdapter extends BaseAdapter {

	private TypedArray icons;
	private String[] texts;
	private int white;

	public SlidingAdapter(Context context) {
		// TODO Auto-generated constructor stub
		Resources res = context.getResources();
		icons = res.obtainTypedArray(R.array.sliding_list_icon);
		texts = res.getStringArray(R.array.sliding_list_text);
		white = res.getColor(R.color.white);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return icons.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView textView;
		if (convertView == null) {
			textView = new TextView(parent.getContext());
			textView.setLayoutParams(new ListView.LayoutParams(
					ListView.LayoutParams.FILL_PARENT, 70));// ���ÿ��
			textView.setPadding(20, 0, 0, 0);// ���ü��
			textView.setCompoundDrawablePadding(20);// ����ͼƬ�����ֵļ��
			textView.setGravity(Gravity.CENTER_VERTICAL);// ��ֱ����
			textView.setTextColor(white);// ��ɫ
			textView.setTextSize(16);// ��С
		} else {
			textView = (TextView) convertView;
		}

		textView.setText(texts[position]);
		textView.setCompoundDrawablesWithIntrinsicBounds(
				icons.getDrawable(position), null, null, null);

		return textView;
	}

}
