package android.app.myCamera;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public abstract class MyAnimationDrawable extends AnimationDrawable {
	private Handler finishHandler; // �жϽ�����Handler

	/*-----------------���붯����ÿһ֡---------------------*/
	public MyAnimationDrawable(AnimationDrawable ad) {
		for (int i = 0; i < ad.getNumberOfFrames(); i++) {
			this.addFrame(ad.getFrame(i), ad.getDuration(i));
		}
	}

	/*---�����ø����start() Ȼ�������̣߳�������onAnimationEnd()---*/
	@Override
	public void start() {
		super.start();
		finishHandler = new Handler();
		finishHandler.postDelayed(new Runnable() {
			public void run() {
				onAnimationEnd();
			}
		}, getTotalDuration());
	}

	/*-------���������ö����ĳ���ʱ��,֮�����onAnimationEnd-------*/
	public int getTotalDuration() {
		int durationTime = 0;
		for (int i = 0; i < this.getNumberOfFrames(); i++) {
			durationTime += this.getDuration(i);
		}
		return durationTime;
	}

	/*----------------����ʱ���õķ���-------------------*/
	abstract void onAnimationEnd();
}