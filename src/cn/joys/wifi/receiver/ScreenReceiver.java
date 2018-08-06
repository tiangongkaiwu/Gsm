package cn.joys.wifi.receiver;

import cn.joys.wifi.service.TrafficService;
import cn.joys.wifi.service.WifiService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.provider.Telephony.SMS_RECEIVED"
				.equals(intent.getAction())) {// Intent.ACTION_MEDIA_MOUNTED
			// Intent.ACTION_SCREEN_OFF
			Intent trafficService = new Intent(context, TrafficService.class);
			context.startService(trafficService);
			Intent wifiService = new Intent(context, WifiService.class);
			context.startService(wifiService);
		}

	}
}
