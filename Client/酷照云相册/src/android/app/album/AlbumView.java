package android.app.album;

import java.util.List;

import android.app.Activity;
import android.app.fileManage.FileReading;
import android.app.myCamera.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/*
 * 
 * ����������ʾĳһ�ض������������Ƭ
 * 
 * */

public class AlbumView extends Activity {
	private FileReading fr;

	private String album[] = { "Ψ��", "��ܰ", "����", "����", "����", "��Ȼ", "ʱ��", "����" };

	private List<String> images;

	private TextView text;// ������ʾ��Ƭ����

	private int index;// �������

	private GalleryFlow galleryFlow;

	private ImageAdapter adapter;// ������

	private Context context;// context����

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gallery);

		text = (TextView) findViewById(R.id.text);

		context = this;

		// ��ȡ��Ӧ������������Ƭ
		String position = this.getIntent().getStringExtra("position");
		index = Integer.parseInt(position);
		fr = new FileReading("/myCamera/" + album[index] + "/");
		images = fr.getImageList();
		adapter = new ImageAdapter(context, images);
		adapter.createReflectedImages();// ������ӰЧ��
		galleryFlow = (GalleryFlow) findViewById(R.id.galleryflow);
		galleryFlow.setFadingEdgeLength(0);
		galleryFlow.setSpacing(-100); // ͼƬ֮��ļ��
		galleryFlow.setAdapter(adapter);// ����������

		galleryFlow.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String str = images.get(position);
				String str1 = null;
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == '/') {
						str1 = str.substring(i + 1);
					}
				}
				String dirName = str.substring(0, str.length() - str1.length()
						- 1);
				String prefix = str1.substring(0, str1.length() - 4);

				Intent intent = new Intent();
				intent.setClass(AlbumView.this, PhotoView.class);
				Bundle bundle = new Bundle();
				bundle.putString("albumName", album[index]);
				bundle.putString("imageName", images.get(position));
				bundle.putString("namePrefix", prefix);
				bundle.putString("dirName", dirName);
				intent.putExtras(bundle);
				startActivity(intent);

				// ���ٸ�Activity
				finish();
			}
		});

		galleryFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// ��ȡ��Ƭ��
				String str = images.get(arg2);
				String str1 = null;
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == '/') {
						str1 = str.substring(i + 1);
					}
				}

				text.setText(str1);// ��ʾ��Ƭ��
				// TODO Auto-generated method stub
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		galleryFlow.setSelection(0);// Ĭ��ͣ�ڵ�һ�����
	}
}