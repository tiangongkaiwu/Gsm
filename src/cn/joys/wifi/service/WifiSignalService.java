package cn.joys.wifi.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WifiSignalService extends Service {

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
}
