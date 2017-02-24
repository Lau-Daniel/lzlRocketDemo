package com.itheima.rocketdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void open(View v) {
		//开启服务
		Intent service = new Intent(this, RocketService.class);
		startService(service);
		finish();
	}

}
