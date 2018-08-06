package cn.joys.wifi.ui;

import cn.joys.wifi.util.URLS;
import cn.joys.wifi.util.WebAppInterface;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application {
	private int position;
	SharedPreferences sp;

	@Override
	public void onCreate() {
		super.onCreate();
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setPosition(0);
		new Thread() {
			public void run() {
				WebAppInterface.validStatusCode(URLS.WEBURL);
			};
		}.start();
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}