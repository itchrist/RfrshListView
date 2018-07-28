package com.christ.measuredemo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class RefreshApp extends Application {
    private static Handler mHandler;
	private static Context context;
	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler();
		context = getApplicationContext();
	}
	
	public static Handler getHandler(){
		return mHandler;
	}
	
	public static Context getContext(){
		return context;
	}
	

}
