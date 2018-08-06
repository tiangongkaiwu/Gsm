package cn.joys.wifi.receiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.joys.wifi.bean.Traffic;
import cn.joys.wifi.dao.ITraffic;
import cn.joys.wifi.dao.impl.TrafficImpl;
import cn.joys.wifi.service.TrafficService;
import cn.joys.wifi.service.WifiService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {
	private ITraffic dao;

	@Override
	public void onReceive(Context context, Intent intent) {
		dao = new TrafficImpl(context);
		this.update();
		Intent trafficService = new Intent(context, TrafficService.class);
		context.startService(trafficService);
		Intent wifiService = new Intent(context, WifiService.class);
		context.startService(wifiService);
	}
	
	public void update() {
		String time = getTime();
		String date = time.split(" ")[0];
		int month = getMonth();
		int day = getDay();
		if (dao.getTraffic(date, 2) != null) {
			Traffic traffic = new Traffic();
			traffic.setFlag(1);
			traffic.setMobile(dao.getTraffic(date, 2).getMobile());
			traffic.setWifi(dao.getTraffic(date, 2).getWifi());
			traffic.setTime(time);
			traffic.setDate(date);
			traffic.setMonth(month);
			traffic.setDay(day);
			dao.updateTraffic(traffic);
		} else {
			Traffic traffic = new Traffic();
			traffic.setFlag(1);
			traffic.setMobile("0.00");
			traffic.setWifi("0.00");
			traffic.setTime(time);
			traffic.setDate(date);
			traffic.setMonth(month);
			traffic.setDay(day);
			dao.add(traffic);
		}

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
}
