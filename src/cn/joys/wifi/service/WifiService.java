package cn.joys.wifi.service;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.joys.wifi.R;
import cn.joys.wifi.ui.MainActivity;
import cn.joys.wifi.ui.MyApplication;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

public class WifiService extends Service {
	private int signal = 78;// 无线信号强度
	private Timer timer;
	private int special = 0;// 额外增强
	private SharedPreferences sp;
	private int hour = 0;// 小时
	private int minutes = 1;// 分钟
	private MyApplication app;
	private TelephonyManager mTelephonyManager;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		sp = getSharedPreferences("wifi", Context.MODE_PRIVATE);
		getWifiSignal();
		app = (MyApplication) getApplication(); // 获得我们的应用程序MyApplication
		hour = sp.getInt("hour", 0);
		minutes = sp.getInt("minutes", 1);
		timer = new Timer();

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (getSimState() == TelephonyManager.SIM_STATE_READY) {
					special = sp.getInt("special", 0);
					minutes = minutes + 1;
					if (minutes == 60) {
						hour = hour + 1;
						minutes = 0;
					}
					Editor editor = sp.edit();
					editor.putInt("hour", hour);
					editor.putInt("minutes", minutes);
					editor.putInt("signal", signal);
					editor.commit();
					setNotification();
				} else {
					cancelNotification();
				}
			}
		}, 10, 60000);
	}

	private int getSimState() {
		return mTelephonyManager.getSimState();
	}

	public void getWifiSignal() {
		TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tel.listen(new PhoneStateMonitor(),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

	}

	public class PhoneStateMonitor extends PhoneStateListener {
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			int signalStr;
			if (signalStrength.getGsmSignalStrength() != 99) {
				signalStr = signalStrength.getGsmSignalStrength() * 2 - 113;
			} else {
				signalStr = signalStrength.getGsmSignalStrength();
			}
			getSignal(signalStr);
			super.onSignalStrengthsChanged(signalStrength);
		}
	}

	public void getSignal(int signalStr) {
		if (signalStr >= -55) {
			Random random = new Random();
			signal = random.nextInt(95) % (95 - 80 + 1) + 80;
		} else if (signalStr >= -70 && signalStr <= -56) {
			Random random = new Random();
			signal = random.nextInt(85) % (85 - 75 + 1) + 75;
		} else if (signalStr >= -85 && signalStr <= -71) {
			Random random = new Random();
			signal = random.nextInt(75) % (75 - 55 + 1) + 55;
		} else if (signalStr >= -96 && signalStr <= -86) {
			Random random = new Random();
			signal = random.nextInt(50) % (50 - 40 + 1) + 40;
		} else {
			signal = 42;
		}
	}

	/**
	 * 判断网络类型
	 * 
	 * @param context
	 * @return
	 */
	public int getConnectedType() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
			return mNetworkInfo.getType();
		} else {
			return -1;
		}
	}

	// 添加常驻通知
	private void setNotification() {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, Notification.FLAG_NO_CLEAR);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		RemoteViews contentView = new RemoteViews(this.getPackageName(),
				R.layout.notification_wifi);
		int totalSignl = signal + special;
		contentView.setTextViewText(R.id.tv_normal, "当前强度：" + totalSignl);
		contentView.setTextViewText(R.id.tv_special, "已增强：" + special + "%");
		if (totalSignl > 80) {
			contentView.setTextViewText(R.id.tv_normal_size,
					"|||||||||||||||||||||||||||||||||||");
			contentView.setTextViewText(R.id.tv_end_size, "||||||||");
		} else if (totalSignl > 70 && totalSignl < 80) {
			contentView.setTextViewText(R.id.tv_normal_size,
					"||||||||||||||||||||||||||||||");
			contentView.setTextViewText(R.id.tv_end_size, "||||||||||");
		} else if (totalSignl > 60 && totalSignl < 70) {
			contentView.setTextViewText(R.id.tv_normal_size,
					"|||||||||||||||||||||||||");
			contentView.setTextViewText(R.id.tv_end_size, "||||||||||||");
		} else {
			contentView.setTextViewText(R.id.tv_normal_size,
					"||||||||||||||||||||");
			contentView.setTextViewText(R.id.tv_end_size, "||||||||||||||");
		}

		if (special == 0) {
			contentView.setTextViewText(R.id.tv_special_size, "");
		} else if (special > 0 && special < 5) {
			contentView.setTextViewText(R.id.tv_special_size, "||||||||");
		} else if (special > 5 && special < 10) {
			contentView.setTextViewText(R.id.tv_special_size, "||||||||||");
		} else if (special > 10 && special < 15) {
			contentView.setTextViewText(R.id.tv_special_size, "||||||||||||");
		} else {// 15-20
			contentView.setTextViewText(R.id.tv_special_size, "||||||||||||||");
		}
		builder.setSmallIcon(R.drawable.logo);
		builder.setContent(contentView);
		builder.setContentIntent(pendingIntent);
		builder.build();
		Notification notification = builder.build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notificationManager.notify(R.string.app_name, notification);
	}

	// 取消通知
	private void cancelNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
	}

	/**
	 * 计算额外增强
	 * 
	 * @return
	 */
	public int getWifiSpecial(int signal) {
		if (signal > 80) {
			return (int) (Math.random() * 5) + 1;
		} else if (signal > 70 && signal < 80) {
			return (int) (Math.random() * 7) + 4;
		} else if (signal > 50 && signal < 70) {
			return new Random().nextInt(10) + 5;
		} else {// 5-20
			return new Random().nextInt(15) + 5;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");// android.intent.action.BOOT_COMPLETED
		// android.provider.Telephony.SMS_RECEIVED
		sendBroadcast(intent);
	}

}
