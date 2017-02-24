package com.itheima.rocketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RocketService extends Service {

	private static final String TAG = "RocketService";
	private RocketToast mToast;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		LoggerUtil.d(TAG, "开启火箭服务");
		mToast = new RocketToast(this);
		mToast.show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LoggerUtil.d(TAG, "关闭火箭服务");
		mToast.hide();
	}

}
