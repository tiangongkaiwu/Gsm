package cn.joys.wifi.ui.fragment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.baidu.mobstat.StatService;
import cn.joys.wifi.R;
import cn.joys.wifi.bean.Traffic;
import cn.joys.wifi.dao.ITraffic;
import cn.joys.wifi.dao.impl.TrafficImpl;
import cn.joys.wifi.ui.TrafficDetailActivity;
import cn.joys.wifi.ui.TrafficRankActivity;
import cn.joys.wifi.ui.TrafficSetActivity;
import cn.joys.wifi.view.MyProgressBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TrafficFragment extends Fragment implements OnClickListener {
	private LinearLayout ll_traffic_detail;// 本月流量单
	private LinearLayout ll_traffic_set;// 流量设置
	private View view;
	private TextView tv_remind_month_data;// 本月剩余
	private TextView tv_average_available_data;// 日均可用
	private TextView tv_used_today;// 今日已用
	private TextView tv_used_month_left;// 本月已用,左半部分
	private TextView tv_month_traffic_data;// 流量套餐
	private TextView tv_until_end_data;// 距结算日
	private TextView tv_used_month_right;// 本月已用,右半部分
	private SharedPreferences sp;
	private MyProgressBar pb;
	private LinearLayout ll_traffic_rank;
	private ITraffic dao;
	private float mobileUsed = 0.00f;
	private LinearLayout ll_traffic_1;
	private LinearLayout ll_traffic_2;
	private LinearLayout ll_traffic_3;
	private LinearLayout ll_traffic_4;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.trafficfragment, container, false);
		dao = new TrafficImpl(getActivity());
		sp = getActivity()
				.getSharedPreferences("traffic", Context.MODE_PRIVATE);
		initView();
		initData();
		return view;
	}

	/**
	 * 初始化套餐
	 */
	private void initView() {
		ll_traffic_detail = (LinearLayout) view
				.findViewById(R.id.ll_traffic_detail);
		ll_traffic_set = (LinearLayout) view.findViewById(R.id.ll_traffic_set);
		tv_month_traffic_data = (TextView) view
				.findViewById(R.id.tv_month_traffic_data);
		tv_remind_month_data = (TextView) view
				.findViewById(R.id.tv_remind_month_data);
		tv_average_available_data = (TextView) view
				.findViewById(R.id.tv_average_available_data);
		tv_used_today = (TextView) view.findViewById(R.id.tv_used_today);
		tv_used_month_left = (TextView) view
				.findViewById(R.id.tv_used_month_left);
		tv_until_end_data = (TextView) view
				.findViewById(R.id.tv_until_end_data);
		tv_used_month_right = (TextView) view
				.findViewById(R.id.tv_used_month_right);
		ll_traffic_detail.setOnClickListener(this);
		ll_traffic_set.setOnClickListener(this);
		pb = (MyProgressBar) view.findViewById(R.id.pb);
		pb.setProgress(1);
		ll_traffic_rank = (LinearLayout) view
				.findViewById(R.id.ll_traffic_rank);
		ll_traffic_rank.setOnClickListener(this);
		ll_traffic_1 = (LinearLayout) view.findViewById(R.id.ll_traffic_1);
		ll_traffic_2 = (LinearLayout) view.findViewById(R.id.ll_traffic_2);
		ll_traffic_3 = (LinearLayout) view.findViewById(R.id.ll_traffic_3);
		ll_traffic_4 = (LinearLayout) view.findViewById(R.id.ll_traffic_4);
		ll_traffic_1.setOnClickListener(this);
		ll_traffic_2.setOnClickListener(this);
		ll_traffic_3.setOnClickListener(this);
		ll_traffic_4.setOnClickListener(this);
	}

	private void initData() {
		float float1 = 0.00f;
		String month = sp.getString("month", "未设置");
		String monthUsed = sp.getString("monthUsed", "0.00");
		int day = sp.getInt("setDay", 1);
		if (getDay() == 1 && sp.getBoolean("isreset", false) == false) {
			monthUsed = 0.00 + "";
			Editor editor = sp.edit();
			editor.putBoolean("isreset", true);
			editor.putString("monthUsed", "0.00");
			editor.putInt("setDay", 1);
			editor.commit();
		} else {
			Editor editor = sp.edit();
			editor.putBoolean("isreset", false);
			editor.putInt("setDay", 1);
			editor.commit();
		}
		int maxDate = getCurrentMonthDay();// 当月天数
		String dayCal = sp.getString("day", "未设置");

		float today = getTrafficToday(getMonth(), getDay());
		getTrafficUsed(getMonth(), day);
		tv_used_today.setText("今日已用" + today + "MB");
		float1 = Float.parseFloat(monthUsed) + mobileUsed;// 本月已用

		String traffic_left = getDouble(float1).split("\\.")[0];
		String traffic_right = getDouble(float1).split("\\.")[1];
		tv_used_month_left.setText(traffic_left);
		tv_used_month_right.setText("." + traffic_right + "MB");

		if (month.trim().equals("未设置")) {
			tv_month_traffic_data.setText("未设置");
			tv_average_available_data.setText("未设置");
			tv_remind_month_data.setText("未设置");
			tv_until_end_data.setText("未设置");
		} else {
			if (dayCal.trim().equals("未设置")) {
				tv_until_end_data.setText("未设置");
			} else {
				int count = maxDate + Integer.parseInt(dayCal) - getDay();
				tv_until_end_data.setText(String.valueOf(count));
			}
			tv_month_traffic_data.setText(month);
			float remind = Float.parseFloat(month) - float1;// 剩余流量
			if (remind <= 0) {
				tv_remind_month_data.setText("0.00");
				tv_average_available_data.setText("0.00");
				setProg(100);
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
				}
				if (float1 == 0) {
					pb.setProgress(1);
				} else {
					// TODO:添加进度动画
					int percent = (int) (float1 / Float.parseFloat(month) * 100);
					setProg(percent);
				}

			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
		StatService.onResume(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_traffic_detail:// 流量详情
			Intent trafficDetail = new Intent(getActivity(),
					TrafficDetailActivity.class);
			startActivity(trafficDetail);

			StatService.onEvent(getActivity(), "traffic_detail",
					"traffic_detail", 1);
			break;
		case R.id.ll_traffic_set:// 流量设置
			Intent trafficSet = new Intent(getActivity(),
					TrafficSetActivity.class);
			startActivity(trafficSet);
			StatService.onEvent(getActivity(), "traffic_set", "traffic_set", 1);
			break;
		case R.id.ll_traffic_rank://  流量排行
			Intent trafficRank = new Intent(getActivity(),
					TrafficRankActivity.class);
			startActivity(trafficRank);
			StatService.onEvent(getActivity(), "traffic_rank", "traffic_rank",
					1);
			break;
		case R.id.ll_traffic_1:
			Intent trafficSet1 = new Intent(getActivity(),
					TrafficSetActivity.class);
			startActivity(trafficSet1);
			break;
		case R.id.ll_traffic_2:
			Intent trafficSet2 = new Intent(getActivity(),
					TrafficSetActivity.class);
			startActivity(trafficSet2);
			break;
		case R.id.ll_traffic_3:
			Intent trafficSet3 = new Intent(getActivity(),
					TrafficSetActivity.class);
			startActivity(trafficSet3);
			break;
		case R.id.ll_traffic_4:
			Intent trafficSet4 = new Intent(getActivity(),
					TrafficSetActivity.class);
			startActivity(trafficSet4);
			break;
		default:
			break;
		}
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
		return df.format(new Date());// new Date()为获取当前系统时间

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
	 * 获取当前日期
	 * 
	 * @return
	 */
	public String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		return df.format(new Date());
	}

	public void setProg(int pre) {
		int progress = 0;
		if (pre <= 5) {
			progress = 6;
		} else if (pre <= 10) {
			progress = 10;
		} else if (pre <= 15) {
			progress = 12;
		} else if (pre <= 20) {
			progress = 15;
		} else if (pre <= 25) {
			progress = 18;
		} else if (pre <= 30) {
			progress = 23;
		} else if (pre <= 40) {
			progress = 28;
		} else if (pre <= 45) {
			progress = 31;
		} else if (pre <= 50) {
			progress = 33;
		} else if (pre <= 55) {
			progress = 36;
		} else if (pre <= 60) {
			progress = 40;
		} else if (pre <= 65) {
			progress = 42;
		} else if (pre <= 70) {
			progress = 45;
		} else if (pre <= 75) {
			progress = 50;
		} else if (pre <= 80) {
			progress = 53;
		} else if (pre <= 85) {
			progress = 55;
		} else if (pre <= 90) {
			progress = 57;
		} else if (pre <= 95) {
			progress = 60;
		} else {
			progress = 60;
		}

		setPB(progress);
	}

	public void setPB(final int progress) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int count = 0;
				while (count <= progress) {// 15=20% 10=10% 65=100%

					pb.setProgress(count);
					count += 3;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	/**
	 * 本月已用(根据设置已用流量的时间来计算)
	 * 
	 * @param month
	 * @param day
	 */
	public void getTrafficUsed(int month, int day) {
		mobileUsed = 0.00f;
		List<Traffic> traffics = dao.getTrafficByDay(month, day);
		for (Traffic traffic : traffics) {
			float mobile = Float.parseFloat(traffic.getMobile());
			mobileUsed = mobileUsed + mobile;
		}
	}

	/**
	 * 今日已用
	 * 
	 * @param month
	 * @param day
	 * @return
	 */
	public float getTrafficToday(int month, int day) {
		float todayUsed = 0.00f;
		List<Traffic> traffics = dao.getTrafficToday(month, day);
		if (traffics.size() != 0) {
			for (Traffic traffic : traffics) {
				float mobile = Float.parseFloat(traffic.getMobile());
				todayUsed = todayUsed + mobile;
			}
		}
		return todayUsed;
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
}
