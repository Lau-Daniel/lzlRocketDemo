package com.itheima.rocketdemo;

import android.util.Log;

public class LoggerUtil {
	
	private static boolean isDebug = true;
	
	public static void d(String tag, String msg){
		if(!isDebug){
			return;
		}
		Log.d(tag, msg);
	}

}
