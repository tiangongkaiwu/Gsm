package cn.joys.wifi.service;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.joys.wifi.bean.Traffic;
import cn.joys.wifi.dao.ITraffic;
import cn.joys.wifi.dao.impl.TrafficImpl;
import cn.joys.wifi.receiver.ScreenReceiver;
import cn.joys.wifi.util.TextFormater;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.IBinder;
import android.util.Log;

public class TrafficService extends Service {
	private String mobileTraffic;
	private String wifiTraffic;
	private Timer timer;
	private ITraffic dao;
	private float mobile = 0.00f;
	private float wifi = 0.00f;
	private SharedPreferences sp;
	private float monthTraffic;
	private int tag;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dao = new TrafficImpl(this);
		sp = getSharedPreferences("traffic", Context.MODE_PRIVATE);
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// setTotalDataInfo();
				add();
			}
		}, 0, 25000);

	}

	/**
	 * 获取流量信息
	 */
	public void setTotalDataInfo() {
		// 获取移动数据接收流量bytes
		long mobileRx = TrafficStats.getMobileRxBytes();
		// System.out.println("mobileRx=" + mobileRx);
		// 获取移动数据发送流量bytes
		long mobileTx = TrafficStats.getMobileTxBytes();
		// System.out.println("mobileTx=" + mobileTx);
		// 计算总的移动数据流量
		long mobileTotal = mobileRx + mobileTx;
		// System.out.println("mobileTotal=" + mobileTotal);
		// 数据进行格式化
		// mobileTraffic = TextFormater.getDateSize(mobileTotal);
		mobileTraffic = TextFormater.getDate(mobileTotal);
		// System.out.println("mobileTraffic=" + mobileTraffic);
		// 获取全部数据接收流量大小
		long totalRx = TrafficStats.getTotalRxBytes();
		// System.out.println("totalRx=" + totalRx);
		// 获取全部数据发送流量大小
		long totalTx = TrafficStats.getTotalTxBytes();
		// System.out.println("totalTx=" + totalTx);
		// 计算总共流量大小
		long total = totalRx + totalTx;
		// System.out.println("total=" + total);

		// 计算wifi下流量信息 total-mobileTotal
		long wifiTotal = total - mobileTotal;
		// System.out.println("wifiTotal=" + wifiTotal);
		// wifi流量信息格式化
		// wifiTraffic = TextFormater.getDateSize(wifiTotal);
		wifiTraffic = TextFormater.getDate(wifiTotal);
		// Log.i("TAG", "采集的mobile流量long为：" + mobileTotal);
		// Log.i("TAG", "采集的wifi流量long为：" + wifiTotal);
		// Log.i("TAG", "采集的mobile流量为：" + mobileTraffic);
		// Log.i("TAG", "采集的wifi流量为：" + wifiTraffic);
		// System.out.println("采集的mobile流量为：" + mobileTraffic);
		// System.out.println("采集的wifi流量为：" + wifiTraffic);

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

	/**
	 * 获取毫秒
	 * 
	 * @param time
	 * @return
	 */
	public long getMills(String time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		try {
			return df.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void add() {
		setTotalDataInfo();
		String time = getTime();// 当前时间
		String date = time.split(" ")[0];// 日期 2014-10-25
		int month = getMonth();// 月
		int day = getDay();// 天
		int setDay = sp.getInt("setDay", 1);// 设置已用流量的时间
		String used = sp.getString("monthUsed", "0.00");
		if (getDay() == 1 && sp.getBoolean("isreset", false) == false) {
			used = 0.00 + "";
			Editor editor = sp.edit();
			editor.putBoolean("isreset", true);
			editor.putInt("setDay", 1);
			editor.commit();

		} else {
			Editor editor = sp.edit();
			editor.putBoolean("isreset", true);
			editor.putInt("setDay", 1);
			editor.commit();
		}
		List<Traffic> list = dao.getMaxDayReboot(1);
		long max = 0;
		String maxReboot;
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (max < getMills(list.get(i).getTime().toString())) {
					max = getMills(list.get(i).getTime().toString());
				}
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			maxReboot = df.format(max);
		} else {
			maxReboot = null;
		}

		if (maxReboot != null) {
			String maxDate = maxReboot.split(" ")[0];// 日期 2014-10-25
			if (date.trim().equals(maxDate.trim())
					&& dao.getTraffic(date, 2) == null) {
				Traffic traffic = new Traffic();
				traffic.setFlag(2);
				traffic.setMobile(mobileTraffic);
				traffic.setWifi(wifiTraffic);
				traffic.setTime(time);
				traffic.setDate(date);
				traffic.setMonth(month);
				traffic.setDay(day);
				dao.add(traffic);
			} else if (date.trim().equals(maxDate.trim())
					&& dao.getTraffic(date, 2) != null) {
				Traffic traffic = new Traffic();
				traffic.setFlag(2);
				traffic.setMobile(mobileTraffic);
				traffic.setWifi(wifiTraffic);
				traffic.setTime(time);
				traffic.setDate(date);
				traffic.setMonth(month);
				traffic.setDay(day);
				dao.updateTraffic(traffic);
			} else if (!date.trim().equals(maxDate.trim())
					&& dao.getTraffic(date, 2) == null) {
				// TODO:必须减去开机之后到今天之前的数据

				// Date date1 = new Date(maxReboot); sdf.parse(dstr);
				// Date date2 = new Date(date);
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date1 = sdf.parse(maxDate);
					Date date2 = sdf.parse(date);
					int maxDay = daysBetween(date1, date2);
					float beforeMobile = 0.00f;
					float beforeWifi = 0.00f;
					for (int i = 0; i < maxDay; i++) {
						beforeMobile += Float.parseFloat(dao.getTraffic(
								maxDate, 2).getMobile());
						beforeWifi += Float.parseFloat(dao.getTraffic(maxDate,
								2).getWifi());
						maxDate = getAfterDay(maxDate);
					}
					mobileTraffic = getDouble(Float.parseFloat(mobileTraffic)
							- beforeMobile);
					wifiTraffic = getDouble((Float.parseFloat(wifiTraffic) - beforeWifi));
					// Log.i("TAG", "aaaa=：" + mobileTraffic);
					// Log.i("TAG", "aaaa=：" + wifiTraffic);
					Traffic traffic = new Traffic();
					traffic.setFlag(2);
					traffic.setMobile(mobileTraffic);
					traffic.setWifi(wifiTraffic);
					traffic.setTime(time);
					traffic.setDate(date);
					traffic.setMonth(month);
					traffic.setDay(day);

					// System.out.println("aaaa=：" + mobileTraffic);
					// System.out.println("aaaa=：" + wifiTraffic);

					dao.add(traffic);
				} catch (ParseException e) {
					e.printStackTrace();
				}

			} else {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date1 = sdf.parse(maxDate);
					Date date2 = sdf.parse(date);
					int maxDay = daysBetween(date1, date2);
					float beforeMobile = 0.00f;
					float beforeWifi = 0.00f;
					for (int i = 0; i < maxDay; i++) {
						beforeMobile += Float.parseFloat(dao.getTraffic(
								maxDate, 2).getMobile());
						beforeWifi += Float.parseFloat(dao.getTraffic(maxDate,
								2).getWifi());
						maxDate = getAfterDay(maxDate);
					}
					mobileTraffic = getDouble(Float.parseFloat(mobileTraffic)
							- beforeMobile);
					wifiTraffic = getDouble((Float.parseFloat(wifiTraffic) - beforeWifi));

					// Log.i("TAG", "bbbb=：" + mobileTraffic);
					// Log.i("TAG", "bbbb=：" + wifiTraffic);

					Traffic traffic = new Traffic();
					traffic.setFlag(2);
					traffic.setMobile(mobileTraffic);
					traffic.setWifi(wifiTraffic);
					traffic.setTime(time);
					traffic.setDate(date);
					traffic.setMonth(month);
					traffic.setDay(day);

					// System.out.println("bbbb=：" + mobileTraffic);
					// System.out.println("bbbb=：" + wifiTraffic);

					dao.updateTraffic(traffic);
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		} else {// 没有重启纪录
			if (dao.getTraffic(date, 2) == null) {
				float beforeMobile = 0.00f;
				float beforeWifi = 0.00f;
				System.out.println(day + "天");
				for (int i = 1; i < day; i++) {
					if (dao.getTrafficByMD(month, i, 2) == null) {
						beforeMobile = beforeMobile + 0.00f;
						beforeWifi = beforeWifi + 0.00f;
					} else {
						beforeMobile += Float.parseFloat(dao.getTrafficByMD(
								month, i, 2).getMobile());
						beforeWifi += Float.parseFloat(dao.getTrafficByMD(
								month, i, 2).getWifi());
					}

				}
				mobileTraffic = getDouble(Float.parseFloat(mobileTraffic)
						- beforeMobile);
				wifiTraffic = getDouble((Float.parseFloat(wifiTraffic) - beforeWifi));

				// Log.i("TAG", "cccc=：" + mobileTraffic);
				// Log.i("TAG", "cccc=：" + wifiTraffic);

				Traffic traffic = new Traffic();
				traffic.setFlag(2);
				traffic.setMobile(mobileTraffic);
				traffic.setWifi(wifiTraffic);
				traffic.setTime(time);
				traffic.setDate(date);
				traffic.setMonth(month);
				traffic.setDay(day);

				// System.out.println("cccc=：" + mobileTraffic);
				// System.out.println("cccc=：" + wifiTraffic);

				dao.add(traffic);
			} else {
				float beforeMobile = 0.00f;
				float beforeWifi = 0.00f;
				for (int i = 1; i < day; i++) {
					if (dao.getTrafficByMD(month, i, 2) == null) {
						beforeMobile = beforeMobile + 0.00f;
						beforeWifi = beforeWifi + 0.00f;
					} else {
						beforeMobile += Float.parseFloat(dao.getTrafficByMD(
								month, i, 2).getMobile());
						beforeWifi += Float.parseFloat(dao.getTrafficByMD(
								month, i, 2).getWifi());
					}

				}
				mobileTraffic = getDouble(Float.parseFloat(mobileTraffic)
						- beforeMobile);
				wifiTraffic = getDouble((Float.parseFloat(wifiTraffic) - beforeWifi));

				// Log.i("TAG", "dddd=：" + mobileTraffic);
				// Log.i("TAG", "dddd=：" + wifiTraffic);

				Traffic traffic = new Traffic();
				traffic.setFlag(2);
				traffic.setMobile(mobileTraffic);
				traffic.setWifi(wifiTraffic);
				traffic.setTime(time);
				traffic.setDate(date);
				traffic.setMonth(month);
				traffic.setDay(day);
				// System.out.println("dddd=：" + mobileTraffic);
				// System.out.println("dddd=：" + wifiTraffic);

				dao.updateTraffic(traffic);
			}
		}

		monthTraffic = getTrafficUsed(month, setDay) + Float.parseFloat(used);// 获取已经使用的流量
		boolean isNet = sp.getBoolean("isNet", false);
		if (isNet == true) {// 自动断网
			disNet(monthTraffic);
		}
	}

	/**
	 * 保留两位小数
	 * 
	 * @param d
	 * @return
	 */
	public String getDouble(Float f) {
		DecimalFormat format = new DecimalFormat("####0.00");
		return format.format(f);

	}

	/**
	 * 获取前一天日期
	 * 
	 * @param nowDay
	 * @return
	 */
	public static String getBeforeDay(String nowDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(nowDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - 1);

		String beforeDay = new SimpleDateFormat("yyy-MM-dd")
				.format(c.getTime());
		return beforeDay;
	}

	public void disNet(float monthTraffic) {
		String month = sp.getString("month", "0");
		// System.out.println(month + "month");
		if (monthTraffic > Float.parseFloat(month)) {
			// TODO：自动断网
			setMobileData(TrafficService.this, false);
		}
	}

	/**
	 * 设置手机的移动数据
	 */
	public void setMobileData(Context context, boolean b) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class ownerClass = connectivityManager.getClass();
			Class[] argsClass = new Class[1];
			argsClass[0] = boolean.class;
			Method method = ownerClass.getMethod("setMobileDataEnabled",
					argsClass);
			method.invoke(connectivityManager, b);

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("移动数据设置错误: " + e.toString());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
		Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");
		sendBroadcast(intent);
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

	public int getDay() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DATE);
	}

	
	public float getTrafficUsed(int month, int day) {
		float mobileUsed = 0.00f;
		List<Traffic> traffics = dao.getTrafficByDay(month, day);
		if (traffics.size() != 0) {
			for (Traffic traffic : traffics) {
				float mobile = Float.parseFloat(traffic.getMobile());
				mobileUsed = mobileUsed + mobile;
			}
		}
		return mobileUsed;
	}

	/**
	 * 获取后一天日期
	 * 
	 * @param nowDay
	 * @return
	 */
	public static String getAfterDay(String nowDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(nowDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);

		String beforeDay = new SimpleDateFormat("yyy-MM-dd")
				.format(c.getTime());
		return beforeDay;
	}

	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}
}
