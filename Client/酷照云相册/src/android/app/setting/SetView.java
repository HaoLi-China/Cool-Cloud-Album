package android.app.setting;

import android.app.Activity;
import android.app.myCamera.R;
import android.app.util.RWSharedperferences;
import android.app.util.SkinSet;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;

/*
 * 
 * ���ý��棬�ɵ���������ֲ���
 * 
 * */

public class SetView extends Activity implements OnTouchListener,
		OnGestureListener, OnDoubleTapListener {
	private ViewFlipper mFlipper;

	private GestureDetector mGestureDetector;

	private int mCurrentLayoutState;

	private static final int FLING_MIN_DISTANCE = 100;// �ƶ�����
	private static final int FLING_MIN_VELOCITY = 200;// �ƶ��ٶ�

	private RadioGroup img_res, img_qul, auto_focus, auto_save, dely, sh_sd,
			effe, scen, wb, sk;// ��ѡ��ť��

	private RadioButton res1, res2, res3, res4, res5, res6, res7, res8, res9,
			res10, res11, res12, au_fo1, au_fo2, au_sv1, au_sv2, de1, de2, de3,
			de4, qul1, qul2, qul3, qul4, qul5, sh_sd1, sh_sd2, sh_sd3, sh_sd4,
			sh_sd5, scen1, scen2, scen3, scen4, scen5, scen6, scen7, scen8,
			scen9, scen10, scen11, scen12, scen13, scen14, effe1, effe2, effe3,
			effe4, effe5, effe6, effe7, effe8, effe9, wb1, wb2, wb3, wb4, wb5,
			wb6, wb7, wb8, sk1, sk2, sk3, sk4;// ��ѡ��ť

	private String autoSaveState;// �Ƿ��Զ����棨1�ǣ�0��
	private String autoFocusState;// �Ƿ��Զ��Խ���1�ǣ�0��
	private String ImageQualityState;// ��Ƭ������־
	private String ImageResolutionState;// ��Ƭ�ֱ��ʱ�־
	private String delyState;// �����ӳ�״̬
	private String shutterSoundState;// ������״̬
	private String sceneState;// ����ģʽ״̬
	private String effectState;// ����Ч��״̬
	private String whiteBalanceState;// ��ƽ��״̬

	private String skinState = "0";// Ƥ��ģʽ

	private ImageView skinView;// ����ʢ��Ƥ��

	private RWSharedperferences rws;// ��д�����ļ�����

	private Context context;// Context����

	private Bundle bunde;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.set_view);

		skinView = (ImageView) findViewById(R.id.set_view_skin);

		// ȡ��Intent�е�Bundle����
		bunde = this.getIntent().getExtras();

		rws = new RWSharedperferences();

		findView();
		setListener();

		context = this;// ��Context����Ϊthis

		init(); // ��ʼ�������ؼ�
		setRadioButton();// ���õ�ѡ��ť

		readParam();// ��ȡ�����ʼ����

		if (!skinState.contentEquals("")) {
			new SkinSet(skinView, skinState);
		}

		setParam();// �����������
	}

	/*-------------------��ȡ�����ʼ����-----------------*/
	public void readParam() {
		ImageResolutionState = rws.read(this, "ImageResolution");
		autoSaveState = rws.read(this, "autoSave");
		autoFocusState = rws.read(this, "autoFocus");
		ImageQualityState = rws.read(this, "ImageQuality");
		delyState = rws.read(this, "dely");
		shutterSoundState = rws.read(this, "shutterSound");
		sceneState = rws.read(this, "scene");
		effectState = rws.read(this, "effect");
		whiteBalanceState = rws.read(this, "whiteBalance");
		skinState = rws.read(this, "skin");
	}

	/*-----------------���ÿ�ѡ������Եĵ�ѡ��ť----------------*/
	public void setRadioButton() {
		if (bunde.getBoolean("none_effect")) {
			effe1.setEnabled(true);
		}
		if (bunde.getBoolean("mono_effect")) {
			effe2.setEnabled(true);
		}
		if (bunde.getBoolean("negative_effect")) {
			effe3.setEnabled(true);
		}
		if (bunde.getBoolean("solarize_effect")) {
			effe4.setEnabled(true);
		}
		if (bunde.getBoolean("sepia_effect")) {
			effe5.setEnabled(true);
		}
		if (bunde.getBoolean("posterize_effect")) {
			effe6.setEnabled(true);
		}
		if (bunde.getBoolean("whiteboard_effect")) {
			effe7.setEnabled(true);
		}
		if (bunde.getBoolean("blackboard_effect")) {
			effe8.setEnabled(true);
		}
		if (bunde.getBoolean("aqua_effect")) {
			effe9.setEnabled(true);
		}
		if (bunde.getBoolean("auto_scene")) {
			scen1.setEnabled(true);
		}
		if (bunde.getBoolean("beach_scene")) {
			scen2.setEnabled(true);
		}
		if (bunde.getBoolean("candlelight_scene")) {
			scen3.setEnabled(true);
		}
		if (bunde.getBoolean("fireworks_scene")) {
			scen4.setEnabled(true);
		}
		if (bunde.getBoolean("landscape_scene")) {
			scen5.setEnabled(true);
		}
		if (bunde.getBoolean("night_scene")) {
			scen6.setEnabled(true);
		}
		if (bunde.getBoolean("night-portrai_scene")) {
			scen7.setEnabled(true);
		}
		if (bunde.getBoolean("party_scene")) {
			scen8.setEnabled(true);
		}
		if (bunde.getBoolean("portrait_scene")) {
			scen9.setEnabled(true);
		}
		if (bunde.getBoolean("snow_scene")) {
			scen10.setEnabled(true);
		}
		if (bunde.getBoolean("sports_scene")) {
			scen11.setEnabled(true);
		}
		if (bunde.getBoolean("steadyphoto_scene")) {
			scen12.setEnabled(true);
		}
		if (bunde.getBoolean("sunset_scene")) {
			scen13.setEnabled(true);
		}
		if (bunde.getBoolean("theatre_scene")) {
			scen14.setEnabled(true);
		}
		if (bunde.getBoolean("auto_white")) {
			wb1.setEnabled(true);
		}
		if (bunde.getBoolean("cloudy-daylight_white")) {
			wb2.setEnabled(true);
		}
		if (bunde.getBoolean("daylight_white")) {
			wb3.setEnabled(true);
		}
		if (bunde.getBoolean("fluorescent_white")) {
			wb4.setEnabled(true);
		}
		if (bunde.getBoolean("incandescent_white")) {
			wb5.setEnabled(true);
		}
		if (bunde.getBoolean("shade_white")) {
			wb6.setEnabled(true);
		}
		if (bunde.getBoolean("twilight_white")) {
			wb7.setEnabled(true);
		}
		if (bunde.getBoolean("warm-fluorescent_white")) {
			wb8.setEnabled(true);
		}
	}

	/*--------------------��ʼ�������ؼ�-----------------*/
	public void init() {
		// �Զ��Խ��ؼ�
		au_fo1 = (RadioButton) findViewById(R.id.au_fo_on);
		au_fo2 = (RadioButton) findViewById(R.id.au_fo_off);
		auto_focus = (RadioGroup) findViewById(R.id.au_fo);
		auto_focus
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == au_fo1.getId()) {
							rws.write(context, "autoFocus", "0");
						} else if (checkedId == au_fo2.getId()) {
							rws.write(context, "autoFocus", "1");
						}
					}
				});

		// �Զ�����ؼ�
		au_sv1 = (RadioButton) findViewById(R.id.au_sv_on);
		au_sv2 = (RadioButton) findViewById(R.id.au_sv_off);
		auto_save = (RadioGroup) findViewById(R.id.au_sv);
		auto_save
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == au_sv1.getId()) {
							rws.write(context, "autoSave", "0");
						} else if (checkedId == au_sv2.getId()) {
							rws.write(context, "autoSave", "1");
						}
					}
				});

		// �ӳ�����ؼ��ؼ�
		de1 = (RadioButton) findViewById(R.id.de1);
		de2 = (RadioButton) findViewById(R.id.de2);
		de3 = (RadioButton) findViewById(R.id.de3);
		de4 = (RadioButton) findViewById(R.id.de4);
		dely = (RadioGroup) findViewById(R.id.de);
		dely
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == de1.getId()) {
							rws.write(context, "dely", "0");
						} else if (checkedId == de2.getId()) {
							rws.write(context, "dely", "1");
						} else if (checkedId == de3.getId()) {
							rws.write(context, "dely", "2");
						} else if (checkedId == de4.getId()) {
							rws.write(context, "dely", "3");
						}
					}
				});

		// ��Ƭ�����ؼ�
		qul1 = (RadioButton) findViewById(R.id.qul1);
		qul2 = (RadioButton) findViewById(R.id.qul2);
		qul3 = (RadioButton) findViewById(R.id.qul3);
		qul4 = (RadioButton) findViewById(R.id.qul4);
		qul5 = (RadioButton) findViewById(R.id.qul5);
		img_qul = (RadioGroup) findViewById(R.id.img_qul);
		img_qul
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == qul1.getId()) {
							rws.write(context, "ImageQuality", "0");
						} else if (checkedId == qul2.getId()) {
							rws.write(context, "ImageQuality", "1");
						} else if (checkedId == qul3.getId()) {
							rws.write(context, "ImageQuality", "2");
						} else if (checkedId == qul4.getId()) {
							rws.write(context, "ImageQuality", "3");
						} else if (checkedId == qul5.getId()) {
							rws.write(context, "ImageQuality", "4");
						}
					}
				});

		// ��Ƭ�ֱ��ʿؼ�
		res1 = (RadioButton) findViewById(R.id.res1);
		res2 = (RadioButton) findViewById(R.id.res2);
		res3 = (RadioButton) findViewById(R.id.res3);
		res4 = (RadioButton) findViewById(R.id.res4);
		res5 = (RadioButton) findViewById(R.id.res5);
		res6 = (RadioButton) findViewById(R.id.res6);
		res7 = (RadioButton) findViewById(R.id.res7);
		res8 = (RadioButton) findViewById(R.id.res8);
		res9 = (RadioButton) findViewById(R.id.res9);
		res10 = (RadioButton) findViewById(R.id.res10);
		res11 = (RadioButton) findViewById(R.id.res11);
		res12 = (RadioButton) findViewById(R.id.res12);
		img_res = (RadioGroup) findViewById(R.id.img_res);
		img_res
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == res1.getId()) {
							rws.write(context, "ImageResolution", "0");
						} else if (checkedId == res2.getId()) {
							rws.write(context, "ImageResolution", "1");
						} else if (checkedId == res3.getId()) {
							rws.write(context, "ImageResolution", "2");
						} else if (checkedId == res4.getId()) {
							rws.write(context, "ImageResolution", "3");
						} else if (checkedId == res5.getId()) {
							rws.write(context, "ImageResolution", "4");
						} else if (checkedId == res6.getId()) {
							rws.write(context, "ImageResolution", "5");
						} else if (checkedId == res7.getId()) {
							rws.write(context, "ImageResolution", "6");
						} else if (checkedId == res8.getId()) {
							rws.write(context, "ImageResolution", "7");
						} else if (checkedId == res9.getId()) {
							rws.write(context, "ImageResolution", "8");
						} else if (checkedId == res10.getId()) {
							rws.write(context, "ImageResolution", "9");
						} else if (checkedId == res11.getId()) {
							rws.write(context, "ImageResolution", "10");
						} else if (checkedId == res12.getId()) {
							rws.write(context, "ImageResolution", "11");
						}
					}
				});

		// �����ؼ�
		sk1 = (RadioButton) findViewById(R.id.sk1);
		sk2 = (RadioButton) findViewById(R.id.sk2);
		sk3 = (RadioButton) findViewById(R.id.sk3);
		sk4 = (RadioButton) findViewById(R.id.sk4);
		sk = (RadioGroup) findViewById(R.id.sk);
		sk.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == sk1.getId()) {
					rws.write(context, "skin", "0");
					skinView.setBackgroundResource(R.drawable.back4);
				} else if (checkedId == sk2.getId()) {
					rws.write(context, "skin", "1");
					skinView.setBackgroundResource(R.drawable.back5);
				} else if (checkedId == sk3.getId()) {
					rws.write(context, "skin", "2");
					skinView.setBackgroundResource(R.drawable.back6);
				} else if (checkedId == sk4.getId()) {
					rws.write(context, "skin", "3");
					skinView.setBackgroundResource(R.drawable.back7);
				}
			}
		});

		// �������ؼ�
		sh_sd1 = (RadioButton) findViewById(R.id.sh_sd1);
		sh_sd2 = (RadioButton) findViewById(R.id.sh_sd2);
		sh_sd3 = (RadioButton) findViewById(R.id.sh_sd3);
		sh_sd4 = (RadioButton) findViewById(R.id.sh_sd4);
		sh_sd5 = (RadioButton) findViewById(R.id.sh_sd5);
		sh_sd = (RadioGroup) findViewById(R.id.sh_sd);
		sh_sd
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == sh_sd1.getId()) {
							rws.write(context, "shutterSound", "0");
						} else if (checkedId == sh_sd2.getId()) {
							rws.write(context, "shutterSound", "1");
						} else if (checkedId == sh_sd3.getId()) {
							rws.write(context, "shutterSound", "2");
						} else if (checkedId == sh_sd4.getId()) {
							rws.write(context, "shutterSound", "3");
						} else if (checkedId == sh_sd5.getId()) {
							rws.write(context, "shutterSound", "4");
						}
					}
				});

		// ����ģʽ�ؼ�
		scen1 = (RadioButton) findViewById(R.id.scen1);
		scen2 = (RadioButton) findViewById(R.id.scen2);
		scen3 = (RadioButton) findViewById(R.id.scen3);
		scen4 = (RadioButton) findViewById(R.id.scen4);
		scen5 = (RadioButton) findViewById(R.id.scen5);
		scen6 = (RadioButton) findViewById(R.id.scen6);
		scen7 = (RadioButton) findViewById(R.id.scen7);
		scen8 = (RadioButton) findViewById(R.id.scen8);
		scen9 = (RadioButton) findViewById(R.id.scen9);
		scen10 = (RadioButton) findViewById(R.id.scen10);
		scen11 = (RadioButton) findViewById(R.id.scen11);
		scen12 = (RadioButton) findViewById(R.id.scen12);
		scen13 = (RadioButton) findViewById(R.id.scen13);
		scen14 = (RadioButton) findViewById(R.id.scen14);
		scen = (RadioGroup) findViewById(R.id.scen);
		scen
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == scen1.getId()) {
							rws.write(context, "scene", "0");
						} else if (checkedId == scen2.getId()) {
							rws.write(context, "scene", "1");
						} else if (checkedId == scen3.getId()) {
							rws.write(context, "scene", "2");
						} else if (checkedId == scen4.getId()) {
							rws.write(context, "scene", "3");
						} else if (checkedId == scen5.getId()) {
							rws.write(context, "scene", "4");
						} else if (checkedId == scen6.getId()) {
							rws.write(context, "scene", "5");
						} else if (checkedId == scen7.getId()) {
							rws.write(context, "scene", "6");
						} else if (checkedId == scen8.getId()) {
							rws.write(context, "scene", "7");
						} else if (checkedId == scen9.getId()) {
							rws.write(context, "scene", "8");
						} else if (checkedId == scen10.getId()) {
							rws.write(context, "scene", "9");
						} else if (checkedId == scen11.getId()) {
							rws.write(context, "scene", "10");
						} else if (checkedId == scen12.getId()) {
							rws.write(context, "scene", "11");
						} else if (checkedId == scen13.getId()) {
							rws.write(context, "scene", "12");
						} else if (checkedId == scen14.getId()) {
							rws.write(context, "scene", "13");
						}
					}
				});

		// ����Ч��
		effe1 = (RadioButton) findViewById(R.id.effe1);
		effe2 = (RadioButton) findViewById(R.id.effe2);
		effe3 = (RadioButton) findViewById(R.id.effe3);
		effe4 = (RadioButton) findViewById(R.id.effe4);
		effe5 = (RadioButton) findViewById(R.id.effe5);
		effe6 = (RadioButton) findViewById(R.id.effe6);
		effe7 = (RadioButton) findViewById(R.id.effe7);
		effe8 = (RadioButton) findViewById(R.id.effe8);
		effe9 = (RadioButton) findViewById(R.id.effe9);
		effe = (RadioGroup) findViewById(R.id.effe);
		effe
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == effe1.getId()) {
							rws.write(context, "effect", "0");
						} else if (checkedId == effe2.getId()) {
							rws.write(context, "effect", "1");
						} else if (checkedId == effe3.getId()) {
							rws.write(context, "effect", "2");
						} else if (checkedId == effe4.getId()) {
							rws.write(context, "effect", "3");
						} else if (checkedId == effe5.getId()) {
							rws.write(context, "effect", "4");
						} else if (checkedId == effe6.getId()) {
							rws.write(context, "effect", "5");
						} else if (checkedId == effe7.getId()) {
							rws.write(context, "effect", "6");
						} else if (checkedId == effe8.getId()) {
							rws.write(context, "effect", "7");
						} else if (checkedId == effe9.getId()) {
							rws.write(context, "effect", "8");
						}
					}
				});

		// ��ƽ��ؼ�
		wb1 = (RadioButton) findViewById(R.id.wb1);
		wb2 = (RadioButton) findViewById(R.id.wb2);
		wb3 = (RadioButton) findViewById(R.id.wb3);
		wb4 = (RadioButton) findViewById(R.id.wb4);
		wb5 = (RadioButton) findViewById(R.id.wb5);
		wb6 = (RadioButton) findViewById(R.id.wb6);
		wb7 = (RadioButton) findViewById(R.id.wb7);
		wb8 = (RadioButton) findViewById(R.id.wb8);
		wb = (RadioGroup) findViewById(R.id.wb);
		wb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == wb1.getId()) {
					rws.write(context, "whiteBalance", "0");
				} else if (checkedId == wb2.getId()) {
					rws.write(context, "whiteBalance", "1");
				} else if (checkedId == wb3.getId()) {
					rws.write(context, "whiteBalance", "2");
				} else if (checkedId == wb4.getId()) {
					rws.write(context, "whiteBalance", "3");
				} else if (checkedId == wb5.getId()) {
					rws.write(context, "whiteBalance", "4");
				} else if (checkedId == wb6.getId()) {
					rws.write(context, "whiteBalance", "5");
				} else if (checkedId == wb7.getId()) {
					rws.write(context, "whiteBalance", "6");
				} else if (checkedId == wb8.getId()) {
					rws.write(context, "whiteBalance", "7");
				}
			}
		});
	}

	// ��ʼ�������ؼ�����
	public void findView() {
		mFlipper = (ViewFlipper) findViewById(R.id.vf);
		// ע��һ����������ʶ�����
		mGestureDetector = new GestureDetector(this);
		mCurrentLayoutState = 0;
		// ������סViewFlipper,��������ʶ���϶�������
		mFlipper.setLongClickable(true);

		// counttv = (TextView) findViewById(R.id.counttv);
		// lv1 = (ListView) findViewById(R.id.list1);
		// lv2 = (ListView) findViewById(R.id.list2);

		// lv1.setAdapter(new HgroupAdapter(this,null,0));
	}

	/*---------------��mFlipper����һ��listener------------------*/
	public void setListener() {
		mFlipper.setOnTouchListener(this);
	}

	/*-------------�˷�������ָ����ת��ĳ��ҳ�棨δ�õ���---------*/
	public void switchLayoutStateTo(int switchTo) {
		while (mCurrentLayoutState != switchTo) {
			if (mCurrentLayoutState > switchTo) {
				mCurrentLayoutState--;
				mFlipper.setInAnimation(inFromLeftAnimation());
				mFlipper.setOutAnimation(outToRightAnimation());
				mFlipper.showPrevious();
			} else {
				mCurrentLayoutState++;
				mFlipper.setInAnimation(inFromRightAnimation());
				mFlipper.setOutAnimation(outToLeftAnimation());
				mFlipper.showNext();
			}
		}
		;
	}

	/*-----------------������Ҳ����Ķ���Ч��-----------------*/
	protected Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	/*-------------------���������˳��Ķ���Ч�� -------------------*/
	protected Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	/*-----------------�����������Ķ���Ч�� ----------------------*/
	protected Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	/*----------------������Ҳ��˳�ʱ�Ķ���Ч�� ----------------------*/
	protected Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * �û����´������������ƶ����ɿ�����������¼� e1����1��ACTION_DOWN MotionEvent e2�����һ��ACTION_MOVE
	 * MotionEvent velocityX��X���ϵ��ƶ��ٶȣ�����/�� * velocityY��Y���ϵ��ƶ��ٶȣ�����/�� ��������
	 * ��X�������λ�ƴ���FLING_MIN_DISTANCE�����ƶ��ٶȴ���FLING_MIN_VELOCITY������/��
	 */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// ������໬����ʱ������View������Ļʱ��ʹ�õĶ���
			mFlipper.setInAnimation(inFromRightAnimation());
			// ����View�˳���Ļʱ��ʹ�õĶ���
			mFlipper.setOutAnimation(outToLeftAnimation());
			mFlipper.showNext();
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {

			// �����Ҳ໬����ʱ��
			mFlipper.setInAnimation(inFromLeftAnimation());
			mFlipper.setOutAnimation(outToRightAnimation());
			mFlipper.showPrevious();
		}
		return false;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/*-------------------�����������----------------------*/
	public void setParam() {
		switch (Integer.parseInt(autoFocusState)) {
		case 0:
			au_fo1.setChecked(true);
			break;
		case 1:
			au_fo2.setChecked(true);
			break;
		}

		switch (Integer.parseInt(autoSaveState)) {
		case 0:
			au_sv1.setChecked(true);
			break;
		case 1:
			au_sv2.setChecked(true);
			break;
		}

		switch (Integer.parseInt(delyState)) {
		case 0:
			de1.setChecked(true);
			break;
		case 1:
			de2.setChecked(true);
			break;
		case 2:
			de3.setChecked(true);
			break;
		case 3:
			de4.setChecked(true);
			break;
		}

		switch (Integer.parseInt(ImageQualityState)) {
		case 0:
			qul1.setChecked(true);
			break;
		case 1:
			qul2.setChecked(true);
			break;
		case 2:
			qul3.setChecked(true);
			break;
		case 3:
			qul4.setChecked(true);
			break;
		case 4:
			qul5.setChecked(true);
			break;
		}

		switch (Integer.parseInt(ImageResolutionState)) {
		case 0:
			res1.setChecked(true);
			break;
		case 1:
			res2.setChecked(true);
			break;
		case 2:
			res3.setChecked(true);
			break;
		case 3:
			res4.setChecked(true);
			break;
		case 4:
			res5.setChecked(true);
			break;
		case 5:
			res6.setChecked(true);
			break;
		case 6:
			res7.setChecked(true);
			break;
		case 7:
			res8.setChecked(true);
			break;
		case 8:
			res9.setChecked(true);
			break;
		case 9:
			res10.setChecked(true);
			break;
		case 10:
			res11.setChecked(true);
			break;
		case 11:
			res12.setChecked(true);
			break;
		}

		switch (Integer.parseInt(shutterSoundState)) {
		case 0:
			sh_sd1.setChecked(true);
			break;
		case 1:
			sh_sd2.setChecked(true);
			break;
		case 2:
			sh_sd3.setChecked(true);
			break;
		case 3:
			sh_sd4.setChecked(true);
			break;
		case 4:
			sh_sd5.setChecked(true);
			break;
		}

		switch (Integer.parseInt(sceneState)) {
		case 0:
			scen1.setChecked(true);
			break;
		case 1:
			scen2.setChecked(true);
			break;
		case 2:
			scen3.setChecked(true);
			break;
		case 3:
			scen4.setChecked(true);
			break;
		case 4:
			scen5.setChecked(true);
			break;
		case 5:
			scen6.setChecked(true);
			break;
		case 6:
			scen7.setChecked(true);
			break;
		case 7:
			scen8.setChecked(true);
			break;
		case 8:
			scen9.setChecked(true);
			break;
		case 9:
			scen10.setChecked(true);
			break;
		case 10:
			scen11.setChecked(true);
			break;
		case 11:
			scen12.setChecked(true);
			break;
		case 12:
			scen13.setChecked(true);
			break;
		case 13:
			scen14.setChecked(true);
			break;
		}

		switch (Integer.parseInt(effectState)) {
		case 0:
			effe1.setChecked(true);
			break;
		case 1:
			effe2.setChecked(true);
			break;
		case 2:
			effe3.setChecked(true);
			break;
		case 3:
			effe4.setChecked(true);
			break;
		case 4:
			effe5.setChecked(true);
			break;
		case 5:
			effe6.setChecked(true);
			break;
		case 6:
			effe7.setChecked(true);
			break;
		case 7:
			effe8.setChecked(true);
			break;
		case 8:
			effe9.setChecked(true);
			break;
		}

		switch (Integer.parseInt(whiteBalanceState)) {
		case 0:
			wb1.setChecked(true);
			break;
		case 1:
			wb2.setChecked(true);
			break;
		case 2:
			wb3.setChecked(true);
			break;
		case 3:
			wb4.setChecked(true);
			break;
		case 4:
			wb5.setChecked(true);
			break;
		case 5:
			wb6.setChecked(true);
			break;
		case 6:
			wb7.setChecked(true);
			break;
		case 7:
			wb8.setChecked(true);
			break;
		}

		switch (Integer.parseInt(skinState)) {
		case 0:
			sk1.setChecked(true);
			break;
		case 1:
			sk2.setChecked(true);
			break;
		case 2:
			sk3.setChecked(true);
			break;
		case 3:
			sk4.setChecked(true);
			break;
		}
	}
}