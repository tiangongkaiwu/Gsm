package cn.joys.wifi.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mobstat.StatService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cn.joys.wifi.R;
import cn.joys.wifi.bean.UpdateInfo;
import cn.joys.wifi.dao.ITraffic;
import cn.joys.wifi.dao.impl.TrafficImpl;
import cn.joys.wifi.service.TrafficService;
import cn.joys.wifi.service.WifiService;
import cn.joys.wifi.ui.fragment.GameFragment;
import cn.joys.wifi.ui.fragment.HardFragment;
import cn.joys.wifi.ui.fragment.WifiFragment;
import cn.joys.wifi.ui.fragment.TrafficFragment;
import cn.joys.wifi.util.CharacterUtil;
import cn.joys.wifi.view.CustomProgressDialog;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private List<Fragment> fragments;
	private RelativeLayout ll_main_middle;
	private TextView tv_game;
	private TextView tv_traffic;
	private FragmentManager fragmentManager;
	private SharedPreferences sp;
	private int lastPosition = 0;
	private TextView tv_wifi;
	private TextView tv_hard;
	private Drawable drawable;
	private RelativeLayout rl_title;// 顶部布局
	private TextView tv_top_title;// 顶部标题
	private MyApplication app;
	private long exitTime = 0;// 返回键最后退出时间
	private String UPDATE_URL = "http://222.186.15.109:81/Api/Wifi/updateVersion";
	private String APK_URL = "http://222.186.15.109:81/soft/GSM.apk";
	private float version;
	private CustomProgressDialog progressDialog = null;
	private PowerManager powerManager = null;
	private WakeLock wakeLock = null;
	private ITraffic dao;
	private boolean isHard;

	// private boolean isFirst;// 是否第一次运行

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		dao = new TrafficImpl(this);
		powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
		wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"My Lock");

		app = (MyApplication) getApplication(); // 获得我们的应用程序MyApplication
		app.setPosition(0);
		sp = getSharedPreferences("wifi", Context.MODE_PRIVATE);
		version = getVersion();
		getUpdate();
		initFragment();
		initView();
		fragmentManager = getSupportFragmentManager();
		initDate();
		Intent wifiService = new Intent(MainActivity.this, WifiService.class);
		Intent trafficService = new Intent(MainActivity.this,
				TrafficService.class);
		startService(wifiService);
		startService(trafficService);
	}

	private void initView() {
		ll_main_middle = (RelativeLayout) findViewById(R.id.ll_main_middle);
		tv_game = (TextView) findViewById(R.id.tv_game);
		tv_traffic = (TextView) findViewById(R.id.tv_traffic);
		tv_wifi = (TextView) findViewById(R.id.tv_wifi);
		tv_hard = (TextView) findViewById(R.id.tv_hard);
		tv_game.setOnClickListener(this);
		tv_traffic.setOnClickListener(this);
		tv_wifi.setOnClickListener(this);
		tv_hard.setOnClickListener(this);
		rl_title = (RelativeLayout) findViewById(R.id.rl_title);
		tv_top_title = (TextView) findViewById(R.id.tv_top_title);
	}

	/**
	 * 初始化Fragment
	 */
	public void initFragment() {
		fragments = new ArrayList<Fragment>();
		Fragment wifiFragment = new WifiFragment();
		Fragment hardFragment = new HardFragment();
		Fragment trafficFragment = new TrafficFragment();
		Fragment gameFragment = new GameFragment();
		fragments.add(wifiFragment);
		fragments.add(hardFragment);
		fragments.add(trafficFragment);
		fragments.add(gameFragment);
	}

	private void initDate() {
		if (app.getPosition() == 0) {
			tv_wifi.setTextColor(getResources().getColor(R.color.blue));// #0371CE
			drawable = getResources().getDrawable(R.drawable.ic_wifi_s);
			// 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tv_wifi.setCompoundDrawables(null, drawable, null, null);
			fragmentManager.beginTransaction()
					.replace(R.id.ll_main_middle, fragments.get(0)).commit();
			tv_top_title.setText("信号增强");
			rl_title.setBackgroundColor(getResources()
					.getColor(R.color.traffic));
		} else {
			tv_wifi.setTextColor(getResources().getColor(R.color.blue));// #0371CE
			drawable = getResources().getDrawable(R.drawable.ic_wifi_s);
			// 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tv_wifi.setCompoundDrawables(null, drawable, null, null);
			fragmentManager.beginTransaction()
					.replace(R.id.ll_main_middle, fragments.get(0)).commit();
			tv_top_title.setText("信号增强");
			rl_title.setBackgroundColor(getResources()
					.getColor(R.color.traffic));
			app.setPosition(0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_game:
			if (app.getPosition() == 3) {
				tv_game.setTextColor(getResources().getColor(R.color.blue));// #0371CE
				drawable = getResources().getDrawable(R.drawable.ic_game_s);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_game.setCompoundDrawables(null, drawable, null, null);
			} else {

				tv_game.setTextColor(getResources().getColor(R.color.blue));// #0371CE
				drawable = getResources().getDrawable(R.drawable.ic_game_s);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_game.setCompoundDrawables(null, drawable, null, null);
				fragmentManager.beginTransaction()
						.replace(R.id.ll_main_middle, fragments.get(3))
						.commit();
				tv_top_title.setText("游戏加速");
				rl_title.setBackgroundColor(getResources().getColor(
						R.color.game));// #211B23
				changeBottom(app.getPosition());
				app.setPosition(3);
			}
			StatService.onEvent(this, "game", "game", 1);
			break;
		case R.id.tv_traffic:
			if (app.getPosition() == 2) {
				tv_traffic.setTextColor(getResources().getColor(R.color.blue));// #0371CE
				drawable = getResources().getDrawable(R.drawable.ic_traffic_s);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_traffic.setCompoundDrawables(null, drawable, null, null);
			} else {

				tv_traffic.setTextColor(getResources().getColor(R.color.blue));// #0371CE
				drawable = getResources().getDrawable(R.drawable.ic_traffic_s);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_traffic.setCompoundDrawables(null, drawable, null, null);
				fragmentManager.beginTransaction()
						.replace(R.id.ll_main_middle, fragments.get(2))
						.commit();

				tv_top_title.setText("流量检测");
				rl_title.setBackgroundColor(getResources().getColor(
						R.color.traffic));
				changeBottom(app.getPosition());
				app.setPosition(2);
			}
			StatService.onEvent(this, "traffic", "traffic", 1);
			break;

		case R.id.tv_hard:
			if (app.getPosition() == 1) {
				tv_hard.setTextColor(getResources().getColor(R.color.blue));
				drawable = getResources().getDrawable(R.drawable.ic_hard_s);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_hard.setCompoundDrawables(null, drawable, null, null);
			} else {
				tv_hard.setTextColor(getResources().getColor(R.color.blue));// #0371CE
				drawable = getResources().getDrawable(R.drawable.ic_hard_s);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_hard.setCompoundDrawables(null, drawable, null, null);
				isHard = sp.getBoolean("isHard", false);

				fragmentManager.beginTransaction()
						.replace(R.id.ll_main_middle, fragments.get(1))
						.addToBackStack(null)// fragments.get(1)
						.commit();

				tv_top_title.setText("硬件加速");
				rl_title.setBackgroundColor(getResources().getColor(
						R.color.traffic));
				changeBottom(app.getPosition());
				app.setPosition(1);
			}
			StatService.onEvent(this, "hard", "hard", 1);
			break;

		case R.id.tv_wifi:
			if (app.getPosition() == 0) {
				tv_wifi.setTextColor(getResources().getColor(R.color.blue));// #0371CE
				drawable = getResources().getDrawable(R.drawable.ic_wifi_s);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_wifi.setCompoundDrawables(null, drawable, null, null);
			} else {
				tv_wifi.setTextColor(getResources().getColor(R.color.blue));// #0371CE
				drawable = getResources().getDrawable(R.drawable.ic_wifi_s);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				tv_wifi.setCompoundDrawables(null, drawable, null, null);
				fragmentManager.beginTransaction()
						.replace(R.id.ll_main_middle, fragments.get(0))
						.commit();
				tv_top_title.setText("信号增强");
				rl_title.setBackgroundColor(getResources().getColor(
						R.color.traffic));
				changeBottom(app.getPosition());
				app.setPosition(0);
			}
			StatService.onEvent(this, "wifi", "wifi", 1);
			break;
		default:
			break;
		}

	}

	public void changeBottom(int lastPosition) {
		switch (app.getPosition()) {
		case 0:
			tv_wifi.setTextColor(getResources().getColor(R.color.gray));// #000000
			drawable = getResources().getDrawable(R.drawable.ic_wifi_n);
			// 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tv_wifi.setCompoundDrawables(null, drawable, null, null);
			break;
		case 1:
			tv_hard.setTextColor(getResources().getColor(R.color.gray));// #000000
			drawable = getResources().getDrawable(R.drawable.ic_hard_n);
			// 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tv_hard.setCompoundDrawables(null, drawable, null, null);
			break;
		case 2:
			tv_traffic.setTextColor(getResources().getColor(R.color.gray));// #000000
			drawable = getResources().getDrawable(R.drawable.ic_traffic_n);
			// 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tv_traffic.setCompoundDrawables(null, drawable, null, null);
			break;
		case 3:
			tv_game.setTextColor(getResources().getColor(R.color.gray));// #000000
			drawable = getResources().getDrawable(R.drawable.ic_game_n);
			// 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tv_game.setCompoundDrawables(null, drawable, null, null);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 页面统计,可见
		wakeLock.acquire();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 不可见
		wakeLock.release();
		StatService.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(MainActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 获取应用程序本地版本号
	 * 
	 * @return
	 */
	public int getVersion() {
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * 判断网络是否连接
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		} else {
			return false;
		}
	}

	public void getUpdate() {

		if (isNetworkConnected()) {
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(2000);
			RequestParams params = new RequestParams();// 设置参数
			// 手机信号增强器 传参数from=2检查更新
			params.put("from", 2); // 手机IMEI
			client.post(UPDATE_URL, params, new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String responseString) {
					try {
						final UpdateInfo updateInfo = new UpdateInfo();
						JSONObject jsonObject = new JSONObject(responseString);
						updateInfo.setDescription(CharacterUtil
								.toGBK(jsonObject
										.getString("version_description")));
						float v = Float.parseFloat(jsonObject
								.getString("version"));
						updateInfo.setVersion(v);

						if (version < updateInfo.getVersion()) {
							// hideProgressDialog();
							AlertDialog.Builder builder = new Builder(
									MainActivity.this);
							builder.setTitle("提示");
							builder.setIcon(R.drawable.logo);
							builder.setMessage(updateInfo.getDescription());
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											showProgressDialog("下载中...");
											getAPK(APK_URL);

										}
									});

							builder.setNegativeButton("取消", null);
							builder.create().show();
						}

					} catch (JSONException e) {
						e.printStackTrace();
						// hideProgressDialog();

					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {

				}
			});

		}
	}

	/**
	 * 下载apk
	 * 
	 * @param url
	 */
	public void getAPK(String url) {
		if (isNetworkConnected()) {
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(15);
			String[] allowedTypes = new String[] { ".*" };
			client.get(url, new BinaryHttpResponseHandler(allowedTypes) {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] binaryData) {
					hideProgressDialog();
					try {
						File file = MainActivity.this.getFilesDir();
						String path = file.getAbsolutePath() + "/GSM.apk";
						final File file2 = new File(path);
						FileOutputStream fos = new FileOutputStream(file2);
						fos.write(binaryData);
						fos.flush();
						fos.close();
						AlertDialog.Builder builder = new Builder(
								MainActivity.this);
						builder.setTitle("提示");
						builder.setIcon(R.drawable.logo);
						builder.setMessage("下载成功，是否立即安装！");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO:安装
										install(file2);
									}
								});
						builder.setNegativeButton("取消", null);
						builder.create().show();

					} catch (Exception e) {
						e.printStackTrace();
						hideProgressDialog();
					}

				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] binaryData, Throwable error) {
					hideProgressDialog();
					Toast.makeText(MainActivity.this, "请求失败，稍后请重试...",
							Toast.LENGTH_SHORT).show();

				}
			});
		}
	}

	/**
	 * 安装apk
	 * 
	 * @param file
	 */
	public void install(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
		finish();
	}

	private void showProgressDialog(String msg) {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setMessage(msg);
		}
		// progressDialog.setCancelable(false);
		progressDialog.show();
	}

	private void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * 当月
	 * 
	 * @return
	 */
	public int getMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 当天
	 * 
	 * @return
	 */
	public int getDay() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DATE);
	}

	/**
	 * 获取当前系统时间
	 * 
	 * @return
	 */
	public String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		Date date = new Date();// new Date()为获取当前系统时间
		return df.format(new Date());

	}

}
