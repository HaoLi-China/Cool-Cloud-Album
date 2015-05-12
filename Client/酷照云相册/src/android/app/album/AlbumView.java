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
 * 该类用于显示某一特定相册中所有相片
 * 
 * */

public class AlbumView extends Activity {
	private FileReading fr;

	private String album[] = { "唯美", "温馨", "欢乐", "浪漫", "城市", "自然", "时尚", "其它" };

	private List<String> images;

	private TextView text;// 用于显示照片名称

	private int index;// 相册索引

	private GalleryFlow galleryFlow;

	private ImageAdapter adapter;// 适配器

	private Context context;// context对象

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gallery);

		text = (TextView) findViewById(R.id.text);

		context = this;

		// 获取对应索引相册里的照片
		String position = this.getIntent().getStringExtra("position");
		index = Integer.parseInt(position);
		fr = new FileReading("/myCamera/" + album[index] + "/");
		images = fr.getImageList();
		adapter = new ImageAdapter(context, images);
		adapter.createReflectedImages();// 创建倒影效果
		galleryFlow = (GalleryFlow) findViewById(R.id.galleryflow);
		galleryFlow.setFadingEdgeLength(0);
		galleryFlow.setSpacing(-100); // 图片之间的间距
		galleryFlow.setAdapter(adapter);// 设置适配器

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

				// 销毁该Activity
				finish();
			}
		});

		galleryFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 获取照片名
				String str = images.get(arg2);
				String str1 = null;
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == '/') {
						str1 = str.substring(i + 1);
					}
				}

				text.setText(str1);// 显示照片名
				// TODO Auto-generated method stub
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		galleryFlow.setSelection(0);// 默认停在第一个相册
	}
}