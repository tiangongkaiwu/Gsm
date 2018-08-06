package cn.joys.wifi.dao.impl;

import java.util.ArrayList;
import java.util.List;

import cn.joys.wifi.bean.Traffic;
import cn.joys.wifi.dao.ITraffic;
import cn.joys.wifi.dao.db.WifiDBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TrafficImpl implements ITraffic {

	private WifiDBHelper helper;
	private SQLiteDatabase db;

	public TrafficImpl(Context context) {
		helper = new WifiDBHelper(context);
	}

	@Override
	public void add(Traffic traffic) {
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("flag", traffic.getFlag());
		values.put("mobile", traffic.getMobile());
		values.put("wifi", traffic.getWifi());
		values.put("date", traffic.getDate());
		values.put("time", traffic.getTime());
		values.put("month", traffic.getMonth());
		values.put("day", traffic.getDay());
		db.insert("traffic", null, values);
		db.close();
	}

	@Override
	public void update(Traffic traffic) {
		if (getTrafficByFlag(1) != null) {
			db = helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("date", traffic.getDate());
			values.put("time", traffic.getTime());
			db.update("traffic", values, "flag=?", new String[] { 1 + "" });
			db.close();

			if (getTraffic(traffic.getDate(), 2) != null) {
				ContentValues values1 = new ContentValues();
				values1.put("flag", 3);
				db.update("traffic", values1, "date=? and flag=?",
						new String[] { traffic.getDate(), 2 + "" });
				db.close();
			}
		} else {
			add(traffic);
		}

	}

	@Override
	public Traffic getTraffic(String date, int flag) {
		db = helper.getWritableDatabase();
		String sql = "select * from traffic where date=" + " '" + date + "'"
				+ " " + "and flag=" + flag;
		// Cursor cursor = db.rawQuery(
		// "select * from traffic where date=? and flag=?", new String[] {
		// date, flag + "" });
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			Traffic traffic = new Traffic();
			traffic.setDate(date);
			traffic.setFlag(flag);
			traffic.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			traffic.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
			traffic.setWifi(cursor.getString(cursor.getColumnIndex("wifi")));
			traffic.setTime(cursor.getString(cursor.getColumnIndex("time")));
			traffic.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
			traffic.setDay(cursor.getInt(cursor.getColumnIndex("day")));
			return traffic;
		}
		cursor.close();
		db.close();
		return null;
	}

	@Override
	public void delTraffic(String date, int flag) {
		db = helper.getWritableDatabase();
		db.delete("traffic", "date=? and flag=?", new String[] { date,
				flag + "" });
		db.close();

	}

	@Override
	public void updateTraffic(Traffic traffic) {
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("flag", traffic.getFlag());
		values.put("mobile", traffic.getMobile());
		values.put("wifi", traffic.getWifi());
		values.put("date", traffic.getDate());
		values.put("time", traffic.getTime());
		values.put("month", traffic.getMonth());
		values.put("day", traffic.getDay());
		db.update("traffic", values, "date=? and flag=2",
				new String[] { traffic.getDate() });
		db.close();
	}

	@Override
	public Traffic getTrafficByFlag(int flag) {
		db = helper.getWritableDatabase();
		String sql = "select * from traffic where flag=" + flag;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			Traffic traffic = new Traffic();
			traffic.setDate(cursor.getString(cursor.getColumnIndex("date")));
			traffic.setFlag(2);
			traffic.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			traffic.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
			traffic.setWifi(cursor.getString(cursor.getColumnIndex("wifi")));
			traffic.setTime(cursor.getString(cursor.getColumnIndex("time")));
			traffic.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
			traffic.setDay(cursor.getInt(cursor.getColumnIndex("day")));
			return traffic;
		}
		cursor.close();
		db.close();
		return null;
	}

	@Override
	public void updateTraffic2(Traffic traffic) {
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("flag", traffic.getFlag());
		values.put("mobile", traffic.getMobile());
		values.put("wifi", traffic.getWifi());
		values.put("date", traffic.getDate());
		values.put("time", traffic.getTime());
		values.put("month", traffic.getMonth());
		values.put("day", traffic.getDay());
		db.update("traffic", values, "date=? and flag=3",
				new String[] { traffic.getDate() });
		db.close();

	}

	@Override
	public List<Traffic> getTrafficByDay(int month, int day) {
		List<Traffic> traffics = new ArrayList<Traffic>();
		db = helper.getReadableDatabase();
		String sql = "select * from traffic where month=" + month + " and"
				+ " day>=" + day;
		Cursor cursor = db.rawQuery(sql, null);

		while (cursor.moveToNext()) {
			Traffic traffic = new Traffic();
			traffic.setDate(cursor.getString(cursor.getColumnIndex("date")));
			traffic.setFlag(cursor.getInt(cursor.getColumnIndex("flag")));
			traffic.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			traffic.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
			traffic.setWifi(cursor.getString(cursor.getColumnIndex("wifi")));
			traffic.setTime(cursor.getString(cursor.getColumnIndex("time")));
			traffic.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
			traffic.setDay(cursor.getInt(cursor.getColumnIndex("day")));
			traffics.add(traffic);
			// System.out.println(traffics.size());
		}
		cursor.close();
		db.close();
		return traffics;
	}

	@Override
	public List<Traffic> getMaxDay(int month, int flag) {
		List<Traffic> traffics = new ArrayList<Traffic>();
		db = helper.getReadableDatabase();
		String sql = "select * from traffic where flag=" + flag + " and"
				+ " month=" + month;
		// System.out.println(sql);
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			Traffic traffic = new Traffic();
			traffic.setDate(cursor.getString(cursor.getColumnIndex("date")));
			traffic.setFlag(flag);
			traffic.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			traffic.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
			traffic.setWifi(cursor.getString(cursor.getColumnIndex("wifi")));
			traffic.setTime(cursor.getString(cursor.getColumnIndex("time")));
			traffic.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
			traffic.setDay(cursor.getInt(cursor.getColumnIndex("day")));
			traffics.add(traffic);
		}
		cursor.close();
		db.close();
		return traffics;
	}

	@Override
	public Traffic getTrafficByMD(int month, int day, int flag) {
		db = helper.getReadableDatabase();
		String sql = "select * from traffic where flag=" + flag + " and"
				+ " month=" + month + " and" + " day=" + day;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			Traffic traffic = new Traffic();
			traffic.setDate(cursor.getString(cursor.getColumnIndex("date")));
			traffic.setFlag(flag);
			traffic.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			traffic.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
			traffic.setWifi(cursor.getString(cursor.getColumnIndex("wifi")));
			traffic.setTime(cursor.getString(cursor.getColumnIndex("time")));
			traffic.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
			traffic.setDay(cursor.getInt(cursor.getColumnIndex("day")));
			return traffic;
		}
		cursor.close();
		db.close();
		return null;
	}

	@Override
	public List<Traffic> getTrafficToday(int month, int day) {
		List<Traffic> traffics = new ArrayList<Traffic>();
		db = helper.getReadableDatabase();
		String sql = "select * from traffic where month=" + month + " and"
				+ " day=" + day;
		Cursor cursor = db.rawQuery(sql, null);

		while (cursor.moveToNext()) {
			Traffic traffic = new Traffic();
			traffic.setDate(cursor.getString(cursor.getColumnIndex("date")));
			traffic.setFlag(cursor.getInt(cursor.getColumnIndex("flag")));
			traffic.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			traffic.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
			traffic.setWifi(cursor.getString(cursor.getColumnIndex("wifi")));
			traffic.setTime(cursor.getString(cursor.getColumnIndex("time")));
			traffic.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
			traffic.setDay(cursor.getInt(cursor.getColumnIndex("day")));
			traffics.add(traffic);
		}
		cursor.close();
		db.close();
		return traffics;
	}

	@Override
	public List<Traffic> getMaxDayReboot(int flag) {
		List<Traffic> traffics = new ArrayList<Traffic>();
		db = helper.getReadableDatabase();
		String sql = "select * from traffic where flag=" + flag;
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			Traffic traffic = new Traffic();
			traffic.setDate(cursor.getString(cursor.getColumnIndex("date")));
			traffic.setFlag(flag);
			traffic.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			traffic.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
			traffic.setWifi(cursor.getString(cursor.getColumnIndex("wifi")));
			traffic.setTime(cursor.getString(cursor.getColumnIndex("time")));
			traffic.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
			traffic.setDay(cursor.getInt(cursor.getColumnIndex("day")));
			traffics.add(traffic);
		}
		cursor.close();
		db.close();
		return traffics;
	}

}
