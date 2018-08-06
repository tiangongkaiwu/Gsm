package cn.joys.wifi.ui;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.baidu.mobstat.StatService;
import cn.joys.wifi.R;
import cn.joys.wifi.adapter.TrafficAdapter;
import cn.joys.wifi.bean.Traffic;
import cn.joys.wifi.bean.TrafficDetailInfo;
import cn.joys.wifi.dao.ITraffic;
import cn.joys.wifi.dao.impl.TrafficImpl;
import cn.joys.wifi.view.ChartView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrafficDetailActivity extends Activity implements OnClickListener {
	private ImageView iv_traffic_detail_back;// 返回
	private ListView lv_traffic_detail;// ListView
	private TrafficAdapter adapter;
	private ITraffic dao;
	// private List<Traffic> traffics;
	private RelativeLayout rl_line;
	private TextView tv_traffic_detail_top;
	private TextView tv_traffic_detail_x1;
	private TextView tv_traffic_detail_x2;
	private TextView tv_traffic_detail_x3;
	private TextView tv_traffic_detail_x4;
	private TextView tv_traffic_detail_x5;
	private TextView tv_traffic_detail_x6;
	private TextView tv_traffic_detail_x7;
	private TextView tv_traffic_detail_x8;
	private TextView tv_traffic_detail_x9;
	private List<Float> mobileList;// 2G/3g流量集合
	private float maxFloat = 0.00f;// 最大值
	private TextView tv_traffic_detail_y0;
	private TextView tv_traffic_detail_y1;
	private TextView tv_traffic_detail_y2;
	private TextView tv_traffic_detail_y3;
	private LinearLayout ll_traffic_detail;
	private RelativeLayout rl_traffic_detail_2g3g;
	private int height;// view高度
	private int width;// view宽度
	private int y;
	private int x;
	private ChartView chareView;
	private int y0;
	private int y1;
	private int y2;
	private int y3;
	private int y4;
	private int y5;
	private int y6;
	private int y7;
	private int y8;
	private int y9;
	private int y10;
	private int y11;
	private int y12;
	private List<TrafficDetailInfo> trafficDetailInfos;

	// private int lastY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.traffic_detail);
		dao = new TrafficImpl(this);
		// traffics = new ArrayList<Traffic>();
		trafficDetailInfos = new ArrayList<TrafficDetailInfo>();
		mobileList = new ArrayList<Float>();
		initView();

		adapter = new TrafficAdapter(this, trafficDetailInfos);
		lv_traffic_detail.setAdapter(adapter);

		initdata();

	}

	private void initView() {
		iv_traffic_detail_back = (ImageView) findViewById(R.id.iv_traffic_detail_back);
		lv_traffic_detail = (ListView) findViewById(R.id.lv_traffic_detail);
		rl_line = (RelativeLayout) findViewById(R.id.rl_line);
		tv_traffic_detail_top = (TextView) findViewById(R.id.tv_traffic_detail_top);
		tv_traffic_detail_x1 = (TextView) findViewById(R.id.tv_traffic_detail_x1);
		tv_traffic_detail_x2 = (TextView) findViewById(R.id.tv_traffic_detail_x2);
		tv_traffic_detail_x3 = (TextView) findViewById(R.id.tv_traffic_detail_x3);
		tv_traffic_detail_x4 = (TextView) findViewById(R.id.tv_traffic_detail_x4);
		tv_traffic_detail_x5 = (TextView) findViewById(R.id.tv_traffic_detail_x5);
		tv_traffic_detail_x6 = (TextView) findViewById(R.id.tv_traffic_detail_x6);
		tv_traffic_detail_x7 = (TextView) findViewById(R.id.tv_traffic_detail_x7);
		tv_traffic_detail_x8 = (TextView) findViewById(R.id.tv_traffic_detail_x8);
		tv_traffic_detail_x9 = (TextView) findViewById(R.id.tv_traffic_detail_x9);
		tv_traffic_detail_y0 = (TextView) findViewById(R.id.tv_traffic_detail_y0);
		tv_traffic_detail_y1 = (TextView) findViewById(R.id.tv_traffic_detail_y1);
		tv_traffic_detail_y2 = (TextView) findViewById(R.id.tv_traffic_detail_y2);
		tv_traffic_detail_y3 = (TextView) findViewById(R.id.tv_traffic_detail_y3);
		rl_traffic_detail_2g3g = (RelativeLayout) findViewById(R.id.rl_traffic_detail_2g3g);
		ll_traffic_detail = (LinearLayout) findViewById(R.id.ll_traffic_detail);
		iv_traffic_detail_back.setOnClickListener(this);

		int days = getDay();
		String date = getTime();
		int month = getMonth();
		for (int i = days; i > 0; i--) {
			String mobile = getDouble(getTodayMobile(month, i));
			String wifi = getDouble(getTodayWifi(month, i));
			String d = month + "月" + i + "日";
			TrafficDetailInfo trafficDetailInfo = new TrafficDetailInfo();
			trafficDetailInfo.setDate(d);
			trafficDetailInfo.setMobile(mobile);
			trafficDetailInfo.setWifi(wifi);
			trafficDetailInfos.add(trafficDetailInfo);
			// Traffic traffic = new Traffic();
			// if (dao.getTraffic(date, 2) != null) {
			// traffic = dao.getTraffic(date, 2);
			// traffics.add(traffic);
			// } else {
			// traffic.setDate(date);
			// traffic.setFlag(2);
			// traffic.setWifi("0.00");
			// traffic.setMobile("0.00");
			// traffics.add(traffic);
			// }
			// // System.out.println(date);
			// date = getBeforeDay(date);
		}

		maxFloat = getMax();// 获取最大值移动流量

	}

	/**
	 * 获取最大值
	 */
	public float getMax() {
		float max = 0.00f;
		for (TrafficDetailInfo trafficDetailInfo : trafficDetailInfos) {
			mobileList.add(Float.parseFloat(trafficDetailInfo.getMobile()));
			// System.out.println(traffic.getMobile());
			float temp = Float.parseFloat(trafficDetailInfo.getMobile());
			if (temp > max) {
				max = temp;
			}
		}
		return max;
	}

	private void initdata() {
		if (maxFloat < 1) {
			tv_traffic_detail_y1.setText("0.30");
			tv_traffic_detail_y2.setText("0.60");
			tv_traffic_detail_y3.setText("0.90");
		} else if (maxFloat <= 6) {
			tv_traffic_detail_y1.setText("2.00");
			tv_traffic_detail_y2.setText("4.00");
			tv_traffic_detail_y3.setText("6.00");
		} else {
			tv_traffic_detail_y1.setText("5.00");
			tv_traffic_detail_y2.setText("10.00");
			tv_traffic_detail_y3.setText("15.00");
		}

		if (trafficDetailInfos.size() < 10) {
			tv_traffic_detail_x2.setText("02");
			tv_traffic_detail_x3.setText("03");
			tv_traffic_detail_x4.setText("04");
			tv_traffic_detail_x5.setText("05");
			tv_traffic_detail_x6.setText("06");
			tv_traffic_detail_x7.setText("07");
			tv_traffic_detail_x8.setText("08");
			tv_traffic_detail_x9.setText("09");
		} else if (trafficDetailInfos.size() < 18) {
			tv_traffic_detail_x2.setText("03");
			tv_traffic_detail_x3.setText("05");
			tv_traffic_detail_x4.setText("07");
			tv_traffic_detail_x5.setText("09");
			tv_traffic_detail_x6.setText("11");
			tv_traffic_detail_x7.setText("13");
			tv_traffic_detail_x8.setText("15");
			tv_traffic_detail_x9.setText("17");
		} else if (trafficDetailInfos.size() < 26) {
			tv_traffic_detail_x2.setText("04");
			tv_traffic_detail_x3.setText("07");
			tv_traffic_detail_x4.setText("10");
			tv_traffic_detail_x5.setText("13");
			tv_traffic_detail_x6.setText("16");
			tv_traffic_detail_x7.setText("19");
			tv_traffic_detail_x8.setText("22");
			tv_traffic_detail_x9.setText("25");
		} else {
			tv_traffic_detail_x2.setText("05");
			tv_traffic_detail_x3.setText("09");
			tv_traffic_detail_x4.setText("13");
			tv_traffic_detail_x5.setText("17");
			tv_traffic_detail_x6.setText("21");
			tv_traffic_detail_x7.setText("25");
			tv_traffic_detail_x8.setText("29");
			tv_traffic_detail_x9.setText("31");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_traffic_detail_back:
			finish();
			break;

		default:
			break;
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

		String beforeDay = new SimpleDateFormat("yyyy-MM-dd").format(c
				.getTime());
		return beforeDay;
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		Date date = new Date();// new Date()为获取当前系统时间
		return df.format(new Date());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		height = rl_line.getHeight();// 高度
		width = rl_line.getWidth();// 宽度

		y = height / 4;// 64
		x = width / 9;
		initXY();
		if (trafficDetailInfos.size() < 10) {
			int distance = x;
			int lastX = x;
			int lastY = initLastY(Float.parseFloat(trafficDetailInfos.get(
					trafficDetailInfos.size() - 1).getMobile()));
			for (int i = trafficDetailInfos.size() - 1; i >= 0; i--) {
				y = initLastY(Float.parseFloat(trafficDetailInfos.get(i)
						.getMobile()));
				drawLine(x, y, lastX, lastY);
				lastX = x;
				lastY = y;
				x = x + distance + 1;
			}
		} else if (trafficDetailInfos.size() < 18) {

			System.out.println(trafficDetailInfos.size() + "大小");
			int distance = x / 2;
			int lastX = x;
			int lastY = initLastY(Float.parseFloat(trafficDetailInfos.get(
					trafficDetailInfos.size() - 1).getMobile()));
			for (int i = trafficDetailInfos.size() - 1; i >= 0; i--) {
				y = initLastY(Float.parseFloat(trafficDetailInfos.get(i)
						.getMobile()));
				drawLine(x, y, lastX, lastY);
				lastX = x;
				lastY = y;
				x = x + distance + 1;
			}
		} else if (trafficDetailInfos.size() < 26) {
			int distance = x / 3;
			int lastX = x;
			int lastY = initLastY(Float.parseFloat(trafficDetailInfos.get(
					trafficDetailInfos.size() - 1).getMobile()));
			for (int i = trafficDetailInfos.size() - 1; i >= 0; i--) {
				y = initLastY(Float.parseFloat(trafficDetailInfos.get(i)
						.getMobile()));
				drawLine(x, y, lastX, lastY);
				lastX = x;
				lastY = y;
				x = x + distance + 1;
			}
		} else {
			int distance = x / 4;
			int lastX = x;
			int lastY = initLastY(Float.parseFloat(trafficDetailInfos.get(
					trafficDetailInfos.size() - 1).getMobile()));
			for (int i = trafficDetailInfos.size() - 1; i >= 0; i--) {
				y = initLastY(Float.parseFloat(trafficDetailInfos.get(i)
						.getMobile()));
				drawLine(x, y, lastX, lastY);
				lastX = x;
				lastY = y;
				x = x + distance + 1;
			}
			/**
			 * 取消下面注释，曲线显示会有bug
			 */
			// if (traffics.size() <= 31) {
			// int distance = x / 4;
			// int lastX = x;
			// int lastY = initLastY(Float.parseFloat(traffics.get(
			// traffics.size() - 1).getMobile()));
			// for (int i = traffics.size() - 1; i >= 0; i--) {
			// y = initLastY(Float.parseFloat(traffics.get(i).getMobile()));
			// drawLine(x, y, lastX, lastY);
			// lastX = x;
			// lastY = y;
			// x = x + distance + 1;
			//
			// }
		}

	}

	/**
	 * 根据坐标进行画线
	 * 
	 */
	public void drawLine(int x, int y, int lastX, int lastY) {

		chareView = new ChartView(TrafficDetailActivity.this, x, y, lastX,
				lastY);
		rl_line.addView(chareView);

	}

	/**
	 * 初始化XY坐标的值
	 */
	private void initXY() {
		y0 = y * 3;
		y1 = y * 2 + y / 2 + y / 2 / 2;
		y2 = y * 2 + y / 2;
		y3 = y * 2 + y / 4;
		y4 = y * 2;
		y5 = y + y / 2 + y / 2 / 2;
		y6 = y + y / 2;
		y7 = y + y / 4;
		y8 = y;
		y9 = y / 2 + y / 2 / 2;
		y10 = y / 2;
		y11 = y / 4;
		y12 = 0;
	}

	/**
	 * 初始化lastY
	 * 
	 * @param m
	 */
	private int initLastY(float m) {

		if (maxFloat < 1) {
			if (m == 0.00) {
				return y0;
			} else if (m < 0.30f / 2) {
				return y1;
			} else if (m == 0.30f / 2) {
				return y2;
			} else if (m < 0.30f) {
				return y3;
			} else if (m == 0.30f) {
				return y4;
			} else if (m < 0.45f) {
				return y5;
			} else if (m == 0.45f) {
				return y6;
			} else if (m < 0.60f) {
				return y7;
			} else if (m == 0.60f) {
				return y8;
			} else if (m < 0.75f) {
				return y9;
			} else if (m == 0.75f) {
				return y10;
			} else if (m < 0.90f) {
				return y11;
			} else {
				return y12;
			}
		} else if (maxFloat <= 6) {
			if (m == 0.00) {
				return y0;
			} else if (m < 2.00f / 2) {
				return y1;
			} else if (m == 2.00f / 2) {
				return y2;
			} else if (m < 2.00f) {
				return y3;
			} else if (m == 2.00f) {
				return y4;
			} else if (m < 3.00f) {
				return y5;
			} else if (m == 3.00f) {
				return y6;
			} else if (m < 4.00f) {
				return y7;
			} else if (m == 4.00f) {
				return y8;

			} else if (m < 5.00f) {
				return y9;
			} else if (m == 5.00f) {
				return y10;
			} else if (m < 6.00f) {
				return y11;
			} else {
				return y12;
			}

		} else {
			if (m == 0.00) {
				return y0;
			} else if (m < 2.50f) {
				return y1;
			} else if (m == 2.50f) {
				return y2;
			} else if (m < 5.00f) {
				return y3;
			} else if (m == 5.00f) {
				return y4;
			} else if (m < 7.50f) {
				return y5;
			} else if (m == 7.50f) {
				return y6;
			} else if (m < 10.00f) {
				return y7;
			} else if (m == 10.00f) {
				return y8;
			} else if (m < 12.50f) {
				return y9;
			} else if (m == 12.50f) {
				return y10;
			} else if (m < 15.00f) {
				return y11;
			} else {
				return y12;
			}
		}

	}

	/**
	 * 今日已用2g/3g
	 * 
	 * @param month
	 * @param day
	 * @return
	 */
	public float getTodayMobile(int month, int day) {
		float todayMobile = 0.00f;
		List<Traffic> traffics = dao.getTrafficToday(month, day);
		if (traffics.size() != 0) {
			for (Traffic traffic : traffics) {
				float mobile = Float.parseFloat(traffic.getMobile());
				todayMobile = todayMobile + mobile;
			}
		}
		return todayMobile;
	}

	/**
	 * 今日已用wifi
	 * 
	 * @param month
	 * @param day
	 * @return
	 */
	public float getTodayWifi(int month, int day) {
		float todayWifi = 0.00f;
		List<Traffic> traffics = dao.getTrafficToday(month, day);

		if (traffics.size() != 0) {
			for (Traffic traffic : traffics) {
				float mobile = Float.parseFloat(traffic.getWifi());
				todayWifi = todayWifi + mobile;
			}
		}
		return todayWifi;
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

	@Override
	protected void onPause() {
		super.onPause();
		// 不可见
		StatService.onPause(this);
	}
}
