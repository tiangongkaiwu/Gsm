package cn.joys.wifi.ui.fragment;

import java.util.Random;

import com.baidu.mobstat.StatService;

import cn.joys.wifi.R;
import cn.joys.wifi.ui.MainActivity;
import cn.joys.wifi.ui.MyApplication;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class WifiFragment extends Fragment implements OnClickListener {
	private View view;
	private ImageView iv_wifi_point;// 指针
	private TextView tv_total;// 信号强度
	private TextView tv_normal;// 普通强度
	private TextView tv_special;// 额外强度
	private ImageView iv_wifi_signal;// 信号强度开启与否显示图标
	private TextView tv_wifi_signal;// 信号强度显示文字
	private ImageView iv_hard_speed;// 硬件加速显示图标
	private TextView tv_hard_speed;// 硬件加速显示文字
	private TextView tv_signal_start;// 开始增强
	private TextView tv_signal_time;// 运行时间
	private TextView tv_hard_start;// 硬件加速
	private SharedPreferences sp;
	private boolean isHard;// 是否开启硬件加速
	private boolean isSignal;// 是否开启硬件加速
	private HardFragment hardFragment;
	private Drawable drawable;
	private MyApplication app;
	private WifiStartFragment wifiStartFragment;
	private int totalSignl = 0;// 合计信号强度
	private int signal = 0;// 无线信号强度
	private int special = 0;// 额外增强
	private int hour = 0;// 小时
	private int minutes = 0;// 分钟
	private float from = -100;
	private ImageView iv_wifi_single;
	private LinearLayout ll_wifi_signal;
	private TelephonyManager mTelephonyManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.wififragment, container, false);
		app = (MyApplication) getActivity().getApplication(); // 获得我们的应用程序MyApplication
		mTelephonyManager = (TelephonyManager) getActivity().getSystemService(
				Context.TELEPHONY_SERVICE);
		initView();
		return view;
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		sp = getActivity().getSharedPreferences("wifi", Context.MODE_PRIVATE);
		iv_wifi_point = (ImageView) view.findViewById(R.id.iv_wifi_point);
		startAnimation(from, from);
		tv_total = (TextView) view.findViewById(R.id.tv_total);
		tv_normal = (TextView) view.findViewById(R.id.tv_normal);
		tv_special = (TextView) view.findViewById(R.id.tv_special);
		iv_wifi_signal = (ImageView) view.findViewById(R.id.iv_wifi_signal);
		iv_hard_speed = (ImageView) view.findViewById(R.id.iv_hard_speed);
		tv_wifi_signal = (TextView) view.findViewById(R.id.tv_wifi_signal);
		tv_hard_speed = (TextView) view.findViewById(R.id.tv_hard_speed);
		tv_signal_start = (TextView) view.findViewById(R.id.tv_signal_start);
		tv_signal_time = (TextView) view.findViewById(R.id.tv_signal_time);
		tv_hard_start = (TextView) view.findViewById(R.id.tv_hard_start);
		tv_signal_start.setOnClickListener(this);
		tv_hard_start.setOnClickListener(this);
		iv_wifi_single = (ImageView) view.findViewById(R.id.iv_wifi_single);
		ll_wifi_signal = (LinearLayout) view.findViewById(R.id.ll_wifi_signal);
		ll_wifi_signal.setOnClickListener(this);
		initData();
	}

	private void initData() {
		isHard = sp.getBoolean("isHard", false);
		if (isHard == true) {
			iv_hard_speed.setBackgroundResource(R.drawable.iv_wifi_enable);
			tv_hard_speed.setText("硬件加速已启动");
			tv_hard_speed.setTextColor(getResources().getColor(
					R.color.wifienable));
			tv_hard_start.setText("已加速");
			tv_hard_start.setTextColor(Color.GRAY);
			tv_hard_start.setBackgroundResource(R.drawable.iv_hard_end);
		} else {
			iv_hard_speed.setBackgroundResource(R.drawable.iv_wifi_disable);
			tv_hard_speed.setText("硬件加速未启动");
			tv_hard_speed.setTextColor(getResources().getColor(
					R.color.wifidisable));
			tv_hard_start.setText("立即加速");
			tv_hard_start.setBackgroundResource(R.drawable.iv_hard_selector);
		}

		if (getSimState() == TelephonyManager.SIM_STATE_READY) {
			isSignal = sp.getBoolean("isSignal", false);
			if (isSignal == true) {
				iv_wifi_signal.setBackgroundResource(R.drawable.iv_wifi_enable);
				tv_wifi_signal.setText("WIFI信号增强器已启动");
				tv_wifi_signal.setTextColor(getResources().getColor(
						R.color.wifienable));
				tv_signal_start.setText("已启动");
				iv_wifi_single.setVisibility(View.GONE);
			} else {
				iv_wifi_signal
						.setBackgroundResource(R.drawable.iv_wifi_disable);
				tv_wifi_signal.setText("WIFI信号增强未启动");
				tv_wifi_signal.setTextColor(getResources().getColor(
						R.color.wifidisable));
				tv_signal_start.setText("增强信号");
				iv_wifi_single.setVisibility(View.VISIBLE);
				AnimationDrawable animationDrawable = (AnimationDrawable) iv_wifi_single
						.getBackground();
				animationDrawable.start();
			}
			signal = sp.getInt("signal", 78);
			// special = sp.getInt("special", 0);
			if (isSignal == true) {
				special = getWifiSpecial(signal);
			} else {
				special = 0;
			}
			Editor editor = sp.edit();
			editor.putInt("special", special);
			editor.commit();
			hour = sp.getInt("hour", 0);
			minutes = sp.getInt("minutes", 1);
			totalSignl = signal + special;
			tv_normal.setText("普通强度" + " " + signal + "%");
			if (special == 0) {
				tv_special.setText(" 额外增强" + "   " + special + "%");
			} else if (special < 10) {
				tv_special.setText("额外增强" + "   " + special + "%");
			} else {
				tv_special.setText("额外增强" + " " + special + "%");
			}
			tv_total.setText(totalSignl + "");
			setAnimation(totalSignl);
			if (hour == 0) {
				tv_signal_time.setText("已经运行" + minutes + "分钟");
			} else {
				tv_signal_time.setText("已经运行" + hour + "小时" + minutes + "分钟");
			}

			setNotification();
		}

		else {
			tv_normal.setText("普通强度" + 0 + "%");
			tv_special.setText(" 额外增强" + 0 + "%");
			tv_total.setText(0 + "");
			hour = sp.getInt("hour", 0);
			minutes = sp.getInt("minutes", 1);
			if (hour == 0) {
				tv_signal_time.setText("已经运行" + minutes + "分钟");
			} else {
				tv_signal_time.setText("已经运行" + hour + "小时" + minutes + "分钟");
			}
			startAnimation(from, from);
			tv_signal_start.setText("增强信号");
			iv_wifi_single.setVisibility(View.VISIBLE);
			AnimationDrawable animationDrawable = (AnimationDrawable) iv_wifi_single
					.getBackground();
			animationDrawable.start();
			Editor editor = sp.edit();
			editor.putBoolean("isSignal", false);
			editor.commit();
			cancelNotification();
		}

	}

	/**
	 * 指针旋转动画
	 */
	protected void startAnimation(float from, float to) {

		RotateAnimation rotateAnimation = new RotateAnimation(from, to,
				Animation.RELATIVE_TO_SELF, 0.95f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setFillAfter(true);
		iv_wifi_point.startAnimation(rotateAnimation);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.tv_signal_start:
		//
		// if (getSimState() != TelephonyManager.SIM_STATE_READY) {
		// Toast.makeText(getActivity(), "SIM卡状态未知", Toast.LENGTH_SHORT)
		// .show();
		// } else {
		// isSignal = sp.getBoolean("isSignal", false);
		// if (isSignal == true) {
		// Editor editor = sp.edit();
		// editor.putBoolean("isSignal", false);
		// editor.commit();
		//
		// iv_wifi_signal
		// .setBackgroundResource(R.drawable.iv_wifi_disable);
		// tv_wifi_signal.setText("3G/4G信号增强未启动");
		// tv_wifi_signal.setTextColor(getResources().getColor(
		// R.color.wifidisable));
		// tv_signal_start.setText("增强信号");
		// Toast.makeText(getActivity(), "已关闭信号增强", Toast.LENGTH_SHORT)
		// .show();
		// } else {
		// Editor editor = sp.edit();
		// editor.putBoolean("isSignal", true);
		// editor.commit();
		// wifiStartFragment = new WifiStartFragment();
		// FragmentManager fragmentManager = getActivity()
		// .getSupportFragmentManager();
		// fragmentManager.beginTransaction()
		// .replace(R.id.ll_main_middle, wifiStartFragment)
		// .addToBackStack(null).commit();
		// }
		// }
		//
		// StatService.onEvent(getActivity(), "signal_start", "signal_start",
		// 1);
		// break;
		case R.id.tv_hard_start:
			if (isHard == true) {
				Toast.makeText(getActivity(), "已加速,无需重新加速", Toast.LENGTH_SHORT)
						.show();
			} else {
				hardFragment = new HardFragment();
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.ll_main_middle, hardFragment).commit();
				TextView tv_hard = (TextView) getActivity().findViewById(
						R.id.tv_hard);
				TextView tv_wifi = (TextView) getActivity().findViewById(
						R.id.tv_wifi);
				tv_hard.setTextColor(getResources().getColor(R.color.blue));
				drawable = getResources().getDrawable(R.drawable.ic_hard_s);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_hard.setCompoundDrawables(null, drawable, null, null);
				tv_wifi.setTextColor(getResources().getColor(R.color.gray));// #000000
				drawable = getResources().getDrawable(R.drawable.ic_wifi_n);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_wifi.setCompoundDrawables(null, drawable, null, null);
				app.setPosition(1);
			}
			StatService.onEvent(getActivity(), "hard_start", "hard_start", 1);
			break;
		case R.id.ll_wifi_signal:
			if (getSimState() == TelephonyManager.SIM_STATE_READY) {
				isSignal = sp.getBoolean("isSignal", false);
				if (isSignal == true) {
					Editor editor = sp.edit();
					editor.putBoolean("isSignal", false);
					editor.commit();
					tv_special.setText("额外增强" + "   " + 0 + "%");
					iv_wifi_signal
							.setBackgroundResource(R.drawable.iv_wifi_disable);
					tv_wifi_signal.setText("WIFI信号增强未启动");
					tv_wifi_signal.setTextColor(getResources().getColor(
							R.color.wifidisable));
					tv_signal_start.setText("增强信号");
					iv_wifi_single.setVisibility(View.VISIBLE);
					AnimationDrawable animationDrawable = (AnimationDrawable) iv_wifi_single
							.getBackground();
					animationDrawable.start();
					Toast.makeText(getActivity(), "已关闭信号增强", Toast.LENGTH_SHORT)
							.show();
				} else {
					Editor editor = sp.edit();
					editor.putBoolean("isSignal", true);
					editor.commit();
					wifiStartFragment = new WifiStartFragment();
					FragmentManager fragmentManager = getActivity()
							.getSupportFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.ll_main_middle, wifiStartFragment)
							.commit();
				}
			} else {
				Toast.makeText(getActivity(), "SIM卡状态未知", Toast.LENGTH_SHORT)
						.show();
			}
			StatService.onEvent(getActivity(), "signal_start", "signal_start",
					1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
		StatService.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	public void setAnimation(int signal) {
		if (signal == 0) {
			startAnimation(from, from);
		} else if (signal == 10) {
			startAnimation(from, -70);
		} else if (signal > 10 && signal < 20) {
			startAnimation(from, -50);
		} else if (signal == 20) {
			startAnimation(from, -35);
		} else if (signal > 20 && signal < 30) {
			startAnimation(from, -20);
		} else if (signal == 30) {
			startAnimation(from, 0);
		} else if (signal > 30 && signal < 40) {
			startAnimation(from, 10);
		} else if (signal == 40) {
			startAnimation(from, 30);
		} else if (signal > 40 && signal < 50) {
			startAnimation(from, 50);
		} else if (signal == 50) {
			startAnimation(from, 65);
		} else if (signal > 50 && signal < 60) {
			startAnimation(from, 80);
		} else if (signal == 60) {
			startAnimation(from, 100);
		} else if (signal > 60 && signal < 70) {
			startAnimation(from, 115);
		} else if (signal == 70) {
			startAnimation(from, 125);
		} else if (signal > 70 && signal < 80) {
			startAnimation(from, 135);
		} else if (signal == 80) {
			startAnimation(from, 153);
		} else {// >80
			startAnimation(from, 165);
		}
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
		} else if (signal == 0) {
			return 0;
		} else {// 5-20
			return new Random().nextInt(15) + 5;
		}
	}

	// 添加常驻通知
	private void setNotification() {
		Intent intent = new Intent(getActivity(), MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),
				0, intent, Notification.FLAG_NO_CLEAR);
		NotificationManager notificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				getActivity());
		RemoteViews contentView = new RemoteViews(getActivity()
				.getPackageName(), R.layout.notification_wifi);
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
		NotificationManager notificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
	}

	/**
	 * 判断网络类型
	 * 
	 * @param context
	 * @return
	 */
	public int getConnectedType() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
			return mNetworkInfo.getType();
		} else {
			return -1;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// cancelNotification();
	}

	private int getSimState() {
		return mTelephonyManager.getSimState();
	}

}
