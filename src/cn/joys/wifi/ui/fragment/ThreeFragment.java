package cn.joys.wifi.ui.fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import cn.joys.wifi.R;
import cn.joys.wifi.bean.Traffic;
import cn.joys.wifi.dao.ITraffic;
import cn.joys.wifi.dao.impl.TrafficImpl;
import cn.joys.wifi.ui.TrafficDetailActivity;
import cn.joys.wifi.ui.TrafficSetActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ThreeFragment extends Fragment implements OnClickListener {
	private RelativeLayout rl_traffic_07;// 流量设置view
	private TextView tv_month_traffic_data;// 流量套餐
	private TextView tv_until_end_data;// 距结算日
	private SharedPreferences sp;
	private View view;
	private TextView tv_remind_month_data;// 本月剩余
	private TextView tv_average_available_data;// 日均可用
	private TextView tv_used_today_data;// 今日已用
	private TextView tv_used_month_data;// 本月已用
	private RelativeLayout rl_traffic_detail;// 本月流量单
	private ImageView iv_point;// 仪表指针
	private TextView tv_percent;// 已用百分比
	private TextView tv_info;// 头部温馨提示

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.threefragment, null);
		sp = getActivity()
				.getSharedPreferences("traffic", Context.MODE_PRIVATE);
		initView();
		initData();
		return view;
	}

	private void initView() {
		rl_traffic_07 = (RelativeLayout) view.findViewById(R.id.rl_traffic_07);
		rl_traffic_detail = (RelativeLayout) view
				.findViewById(R.id.rl_traffic_detail);
		tv_month_traffic_data = (TextView) view
				.findViewById(R.id.tv_month_traffic_data);
		tv_until_end_data = (TextView) view
				.findViewById(R.id.tv_until_end_data);
		tv_remind_month_data = (TextView) view
				.findViewById(R.id.tv_remind_month_data);
		tv_average_available_data = (TextView) view
				.findViewById(R.id.tv_average_available_data);
		tv_used_today_data = (TextView) view
				.findViewById(R.id.tv_used_today_data);
		tv_used_month_data = (TextView) view
				.findViewById(R.id.tv_used_month_data);
		iv_point = (ImageView) view.findViewById(R.id.iv_point);
		tv_percent = (TextView) view.findViewById(R.id.tv_percent);
		tv_info = (TextView) view.findViewById(R.id.tv_info);
		rl_traffic_07.setOnClickListener(this);
		rl_traffic_detail.setOnClickListener(this);
		startAnimation(-125, -125);
	}

	private void initData() {
		float float1 = 0f;
		String month = sp.getString("month", "未设置");
		String monthUsed = sp.getString("monthUsed", "0.00");
		int maxDate = getCurrentMonthDay();// 当月天数
		String dayCal = sp.getString("day", "未设置");
		ITraffic dao = new TrafficImpl(getActivity());
		Traffic traffic = new Traffic();
		traffic = dao.getTraffic(getTime().split(" ")[0], 2);

		if (traffic != null) {
			tv_used_today_data.setText(traffic.getMobile());
			String nowDate = sp.getString("nowDate", getDate());
			boolean isChange = sp.getBoolean("ischange", false);
			if (isChange == true) {
				float1 = Float.parseFloat(monthUsed)
						+ Float.parseFloat(tv_used_today_data.getText()
								.toString());
				tv_used_month_data.setText(getDouble(float1));// 本月已用
				Editor editor = sp.edit();
				editor.putString("changeUsed", tv_used_month_data.getText()
						.toString());
				editor.putString("nowDate", getDate());
				editor.putBoolean("ischange", false);
				editor.commit();
			} else {

				if (!nowDate.trim().equals(getDate())) {
					String changeUsed = sp.getString("changeUsed", "0.00");
					float1 = Float.parseFloat(changeUsed)
							+ Float.parseFloat(tv_used_today_data.getText()
									.toString());
					tv_used_month_data.setText(getDouble(float1));// 本月已用
					Editor editor = sp.edit();
					editor.putString("monthUsed", tv_used_month_data.getText()
							.toString());
					editor.putString("nowDate", getDate());
					editor.commit();
				} else {
					float1 = Float.parseFloat(monthUsed)
							+ Float.parseFloat(tv_used_today_data.getText()
									.toString());
					tv_used_month_data.setText(getDouble(float1));// 本月已用
					Editor editor = sp.edit();
					editor.putString("changeUsed", tv_used_month_data.getText()
							.toString());
					editor.commit();
				}

			}

		} else {
			tv_used_today_data.setText("0.00");
			tv_used_month_data.setText(monthUsed);// 本月已用
		}

		if (month.trim().equals("未设置")) {
			tv_month_traffic_data.setText("未设置");
			tv_average_available_data.setText("未设置");
			tv_remind_month_data.setText("未设置");
			tv_percent.setText("0%");
			tv_info.setText("请先设置流量套餐");
			tv_until_end_data.setText("未设置");
		} else {
			if (dayCal.trim().equals("未设置")) {
				tv_until_end_data.setText("未设置");
			} else {
				int count = maxDate + Integer.parseInt(dayCal) - 1 - getDay();
				tv_until_end_data.setText(String.valueOf(count));
			}
			tv_month_traffic_data.setText(month);
			float remind = Float.parseFloat(month) - float1;// 剩余流量
			if (remind <= 0) {
				tv_remind_month_data.setText("0.00");
				tv_average_available_data.setText("0.00");
				tv_info.setText("本月套餐已经使用完毕");
				tv_percent.setText("100%");
				startAnimation(-125, 130);// 指针动画
			} else {
				tv_remind_month_data.setText(getDouble(remind));
				if (!tv_until_end_data.getText().toString().equals("未设置")) {
					Float average = remind
							/ Float.parseFloat(tv_until_end_data.getText()
									.toString());
					if (average <= 0) {
						tv_average_available_data.setText("0.00");
					} else {
						tv_average_available_data.setText(getDouble(average));
					}
					String percent = getDouble(float1 / Float.parseFloat(month)
							* 100);// 已经百分比
					tv_percent.setText(percent + "%");
					animation(Float.parseFloat(percent));// 指针的动画
					if (Float.parseFloat(percent) > 70) {
						tv_info.setText("流量告急，请小心使用");
						tv_percent.setTextColor(Color.RED);// 0xFF0033
					} else {
						tv_info.setText("流量充足，请放心使用");
					}
				}
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_traffic_07:
			Intent trafficSet = new Intent(getActivity(),
					TrafficSetActivity.class);
			startActivity(trafficSet);
			break;
		case R.id.rl_traffic_detail:
			Intent trafficDetail = new Intent(getActivity(),
					TrafficDetailActivity.class);
			startActivity(trafficDetail);
			break;
		default:
			break;
		}

	}

	@Override
	public void onResume() {
		initData();
		super.onResume();
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

	/**
	 * 获取当月的 天数
	 * */
	public int getCurrentMonthDay() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		int maxDate = calendar.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 获取今天多少号
	 * 
	 * @return
	 */
	private int getDay() {
		Calendar calendar = Calendar.getInstance();

		return calendar.get(Calendar.DATE);
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
	 * 指针旋转动画
	 */
	protected void startAnimation(float from, float to) {
		RotateAnimation rotateAnimation = new RotateAnimation(from, to,
				Animation.RELATIVE_TO_SELF, 0.6f, Animation.RELATIVE_TO_SELF,
				0.8f);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setFillAfter(true);
		iv_point.startAnimation(rotateAnimation);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		Date date = new Date();// new Date()为获取当前系统时间
		return df.format(new Date());
	}

	public void animation(float percent) {
		if (percent > 0 && percent < 5) {
			startAnimation(-125, -115);
		} else if (percent == 5) {
			startAnimation(-125, -110);
		} else if (percent > 5 && percent < 10) {
			startAnimation(-125, -105);
		} else if (percent == 10) {
			startAnimation(-125, -95);
		} else if (percent > 10 && percent < 15) {
			startAnimation(-125, -85);
		} else if (percent == 15) {
			startAnimation(-125, -80);
		} else if (percent == 20) {
			startAnimation(-125, -70);
		} else if (percent > 15 && percent < 20) {
			startAnimation(-125, -76);
		} else if (percent > 20 && percent < 25) {
			startAnimation(-125, -63);
		} else if (percent == 25) {
			startAnimation(-125, -60);
		} else if (percent > 25 && percent < 30) {
			startAnimation(-125, -50);
		} else if (percent == 30) {
			startAnimation(-125, -45);
		} else if (percent > 30 && percent < 35) {
			startAnimation(-125, -40);
		} else if (percent == 35) {
			startAnimation(-125, -35);
		} else if (percent > 35 && percent < 40) {
			startAnimation(-125, -30);
		} else if (percent == 40) {
			startAnimation(-125, -25);
		} else if (percent > 40 && percent < 45) {
			startAnimation(-125, -18);
		} else if (percent == 45) {
			startAnimation(-125, -12);
		} else if (percent > 45 && percent < 50) {
			startAnimation(-125, -5);
		} else if (percent == 50) {
			startAnimation(-125, 0);
		} else if (percent > 50 && percent < 55) {
			startAnimation(-125, 6);
		} else if (percent == 55) {
			startAnimation(-125, 10);
		} else if (percent > 55 && percent < 60) {
			startAnimation(-125, 15);
		} else if (percent == 60) {
			startAnimation(-125, 22);
		} else if (percent > 60 && percent < 65) {
			startAnimation(-125, 30);
		} else if (percent == 65) {
			startAnimation(-125, 35);
		} else if (percent > 65 && percent < 70) {
			startAnimation(-125, 40);
		} else if (percent == 70) {
			startAnimation(-125, 45);
		} else if (percent > 70 && percent < 75) {
			startAnimation(-125, 55);
		} else if (percent == 75) {
			startAnimation(-125, 60);
		} else if (percent > 75 && percent < 80) {
			startAnimation(-125, 70);
		} else if (percent == 80) {
			startAnimation(-125, 75);
		} else if (percent > 80 && percent < 85) {
			startAnimation(-125, 86);
		} else if (percent == 85) {
			startAnimation(-125, 90);
		} else if (percent > 85 && percent < 90) {
			startAnimation(-125, 98);
		} else if (percent == 90) {
			startAnimation(-125, 105);
		} else if (percent > 90 && percent < 95) {
			startAnimation(-125, 111);
		} else if (percent == 95) {
			startAnimation(-125, 116);
		} else if (percent > 95 && percent < 100) {
			startAnimation(-125, 125);
		} else if (percent == 100) {
			startAnimation(-125, 130);
		} else {
			startAnimation(-125, -125);
		}
	}
}
