package android.app.myCamera;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public abstract class MyAnimationDrawable extends AnimationDrawable {
	private Handler finishHandler; // 判断结束的Handler

	/*-----------------加入动画的每一帧---------------------*/
	public MyAnimationDrawable(AnimationDrawable ad) {
		for (int i = 0; i < ad.getNumberOfFrames(); i++) {
			this.addFrame(ad.getFrame(i), ad.getDuration(i));
		}
	}

	/*---首先用父类的start() 然后启动线程，来调用onAnimationEnd()---*/
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

	/*-------这个方法获得动画的持续时间,之后调用onAnimationEnd-------*/
	public int getTotalDuration() {
		int durationTime = 0;
		for (int i = 0; i < this.getNumberOfFrames(); i++) {
			durationTime += this.getDuration(i);
		}
		return durationTime;
	}

	/*----------------结束时调用的方法-------------------*/
	abstract void onAnimationEnd();
}