package com.itheima.rocketdemo;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class RocketToast implements OnTouchListener {
	private static final String TAG = "RocketToast";
	private View mView;
	private WindowManager mWM;
	private WindowManager.LayoutParams mParams;
	private ImageView mIvRocket;
	private ImageView mIvPark;
	private int mScreenHeight;
	private int mScreenWidth;
	private ImageView mTipView;
	private LayoutParams mTipParams;
	private Context mContext;

	public RocketToast(Context context) {
		this.mContext = context;
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		// 加载布局
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 初始化火箭
		initRocketView(inflate);
		// 初始化发射台
		initTipView(inflate);

		// 设置火箭触摸监听
		mView.setOnTouchListener(this);

		// 获取屏幕宽高
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mScreenHeight = metrics.heightPixels;
		mScreenWidth = metrics.widthPixels;

	}

	private void initRocketView(LayoutInflater inflate) {
		mView = inflate.inflate(R.layout.toast_rocket_layout, null);
		mIvRocket = (ImageView) mView.findViewById(R.id.iv_rocket);
		mIvPark = (ImageView) mView.findViewById(R.id.iv_park);

		mParams = new WindowManager.LayoutParams();

		// 默认位置在左上角
		mParams.gravity = Gravity.LEFT | Gravity.TOP;

		WindowManager.LayoutParams params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// 界面优先
		// params.setTitle("Toast");
	}

	private void initTipView(LayoutInflater inflate) {
		mTipView = (ImageView) inflate.inflate(R.layout.toast_tip_layout, null);
		mTipParams = new WindowManager.LayoutParams();

		// 默认位置在底部居中
		mTipParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

		WindowManager.LayoutParams params = mTipParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
	}

	// 显示吐司
	public void show() {
		if (mView.getParent() != null) {
			mWM.removeView(mView);
		}
		// 初始化位置在左侧居中

		mParams.x = 0;
		mParams.y = (mScreenHeight - mView.getHeight()) / 2;
		mWM.addView(mView, mParams);
	}

	// 隐藏吐司
	public void hide() {
		if (mView.getParent() != null) {
			mWM.removeView(mView);
		}
	}

	// 判断当前火箭是否在提示框中
	private boolean isRocketEnter;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			// 播放火箭动画
			showRocket();
			// 播放发射台动画
			showTip();
			break;

		case MotionEvent.ACTION_MOVE:
			// 鼠标移动位置
			float moveX = event.getRawX();
			float moveY = event.getRawY();

			// 计算火箭是否在发射台
			if (!isRocketEnter && isRocketInTip()) {
				isRocketEnter = true;
				mTipView.setImageResource(R.drawable.desktop_bg_tips_3);
			} else if (isRocketEnter && !isRocketInTip()) {
				mTipView.setImageResource(R.drawable.tip_anim);
				Drawable drawable = mTipView.getDrawable();

				if (drawable instanceof AnimationDrawable) {
					((AnimationDrawable) drawable).start();
				}
				isRocketEnter = false;
			}

			// 保证火箭中心点始终在移动位置
			int x = (int) (moveX - mView.getWidth() / 2f + 0.5f);
			int y = (int) (moveY - mView.getHeight() / 2f + 0.5f);
			mParams.x = x;
			mParams.y = y;
			mWM.updateViewLayout(mView, mParams);

			break;
		case MotionEvent.ACTION_UP:

			// 鼠标抬起位置
			float upX = event.getRawX();
			float upY = event.getRawY();
			// 火箭左上角坐标
			int startX = (int) (upX - mView.getWidth() / 2f + 0.5f);
			int startY = (int) (upY - mView.getHeight() / 2f + 0.5f);
			int endX;
			// Y坐标不变
			int endY = startY;

			if (upX < mScreenWidth / 2) {
				// 左边
				endX = 0;
			} else {
				// 右边
				endX = mScreenWidth - mView.getWidth();
			}
			if (isRocketEnter) {
				// 发射小火箭
				// 发射台左上角坐标
				int[] tipLoc = new int[2];
				mTipView.getLocationOnScreen(tipLoc);
				int tipX = tipLoc[0];
				int tipY = tipLoc[1];
				// 发射台中心坐标
				int tipCenterX = tipX + mTipView.getWidth() / 2;
				int tipCenterY = tipY + mTipView.getHeight() / 2;
				// 计算火箭位于发射台中点时左上角的坐标
				startX = tipCenterX - mView.getWidth() / 2;
				startY = tipCenterY - mView.getHeight() / 2;

				endX = startX;
				// 火箭垂直飞出屏幕
				endY = 0;

				// 同时播放冒烟动画
				playSmokeAnim();

			}

			hideTip();
			playRocketAnim(startX, startY, endX, endY);

			break;
		}
		return true;
	}

	private void playSmokeAnim() {
		Intent intent = new Intent(mContext, SmokeActivity.class);
		// 服务开启Activity
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	private void playRocketAnim(int startX, int startY, int endX, int endY) {
		// 处理X轴动画
		ValueAnimator animX = ValueAnimator.ofInt(startX, endX);
		animX.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int x = (Integer) animation.getAnimatedValue();
				// 火箭x坐标随动画而变化
				mParams.x = x;
				mWM.updateViewLayout(mView, mParams);
			}
		});
		// 处理Y轴动画
		ValueAnimator animY = ValueAnimator.ofInt(startY, endY);
		animY.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int y = (Integer) animation.getAnimatedValue();
				// 火箭y坐标随动画而变化
				mParams.y = y;
				mWM.updateViewLayout(mView, mParams);
			}
		});

		// x和y同时变化
		AnimatorSet set = new AnimatorSet();
		set.playTogether(animX, animY);
		set.setDuration(600);
		set.start();

		// 监听动画播放完成
		set.addListener(new AnimatorListener() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (isRocketEnter) {
					isRocketEnter = false;
					//隐藏视图
					mView.setVisibility(View.GONE);
					mParams.x = 0;
					mParams.y = (mScreenHeight - mView.getHeight()) / 2;
					mWM.updateViewLayout(mView, mParams);
					
					//等待两秒恢复视图
					new Thread(new Runnable() {

						@Override
						public void run() {
							SystemClock.sleep(2000);
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mView.setVisibility(View.VISIBLE);
								}
							});
						}
					}).start();

				}
				hideRocket();
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

	// 显示火箭
	private void showRocket() {
		mIvPark.setVisibility(View.GONE);
		mIvRocket.setVisibility(View.VISIBLE);
		AnimationDrawable drawable = (AnimationDrawable) mIvRocket
				.getDrawable();
		drawable.start();
	}

	// 火箭停靠
	private void hideRocket() {
		mIvPark.setVisibility(View.VISIBLE);
		mIvRocket.setVisibility(View.GONE);

		// 获取火箭左上角坐标
		int[] location = new int[2];
		mView.getLocationOnScreen(location);
		int x = location[0];// x轴
		// 火箭中心
		int rocketBtmX = x + mView.getWidth() / 2;

		if (rocketBtmX <= mScreenWidth / 2) {
			// 左边
			mIvPark.setImageResource(R.drawable.desktop_bg_1);
		} else {
			// 右边
			mIvPark.setImageResource(R.drawable.desktop_bg_2);

		}
	}

	// 显示发射台
	private void showTip() {
		if (mTipView.getParent() != null) {
			mWM.removeView(mTipView);
		}
		// 播放动画
		mTipView.setImageResource(R.drawable.tip_anim);
		Drawable drawable = mTipView.getDrawable();

		if (drawable instanceof AnimationDrawable) {
			((AnimationDrawable) drawable).start();
		}
		mWM.addView(mTipView, mTipParams);
	}

	// 隐藏发射台
	private void hideTip() {
		if (mTipView.getParent() != null) {
			mWM.removeView(mTipView);
		}

	}

	// 判断火箭底部中点是否位于发射台中
	private boolean isRocketInTip() {
		int[] rocketLoc = new int[2];
		mView.getLocationOnScreen(rocketLoc);

		// 火箭底部中心
		int rocketBtmX = rocketLoc[0] + mView.getWidth() / 2;
		int rocketBtnY = rocketLoc[1] + mView.getHeight();

		// 获得发射台左上角坐标
		int[] tipLoc = new int[2];
		mTipView.getLocationOnScreen(tipLoc);
		int tipX = tipLoc[0];
		int tipY = tipLoc[1];

		// 判断火箭底部中心x坐标是否位于发射台中
		boolean isXIn = rocketBtmX >= tipX
				&& rocketBtmX <= tipX + mTipView.getWidth();
		// 判断火箭底部中心y坐标是否位于发射台中
		boolean isYIn = rocketBtnY >= tipY && rocketBtnY <= mScreenHeight;
		return isXIn && isYIn;
	}

	private Handler mHandler = new Handler();
}
