package com.itheima.rocketdemo;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class SmokeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smoke);

		// 播放动画
		ImageView ivTop = (ImageView) findViewById(R.id.iv_smoke_top);
		ImageView ivBottom = (ImageView) findViewById(R.id.iv_smoke_bottom);

		// 透明度动画
		// ivTop.setAlpha(alpha)
		ObjectAnimator topAnim1 = ObjectAnimator.ofFloat(ivTop, "alpha", 0, 1);
		ObjectAnimator topAnim2 = ObjectAnimator.ofFloat(ivTop, "alpha", 1, 0);
		AnimatorSet topSet = new AnimatorSet();
		topSet.playSequentially(topAnim1, topAnim2);

		ObjectAnimator bottomAnim1 = ObjectAnimator.ofFloat(ivBottom, "alpha",
				0, 1);
		ObjectAnimator bottomAnim2 = ObjectAnimator.ofFloat(ivBottom, "alpha",
				1, 0);
		AnimatorSet bottomSet = new AnimatorSet();
		bottomSet.playSequentially(bottomAnim1, bottomAnim2);

		// 拉伸
		ObjectAnimator topScale1 = ObjectAnimator.ofFloat(ivTop, "scaleY", 1,
				1.5f);
		ObjectAnimator topScale2 = ObjectAnimator.ofFloat(ivTop, "scaleY",
				1.5f, 1.3f);
		AnimatorSet ScaleSet = new AnimatorSet();
		ScaleSet.playSequentially(topScale1, topScale2);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(ScaleSet, topSet, bottomSet);
		set.setDuration(1000);
		set.start();
		// 动画播放完成消失
		set.addListener(new AnimatorListener() {

			// 动画播放完成回调
			@Override
			public void onAnimationEnd(Animator animation) {
				finish();
			}

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
	}

}
