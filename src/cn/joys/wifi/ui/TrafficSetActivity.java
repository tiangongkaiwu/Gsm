package cn.joys.wifi.ui;

import java.text.DecimalFormat;
import java.util.Calendar;
import com.baidu.mobstat.StatService;
import cn.joys.wifi.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class TrafficSetActivity extends Activity implements OnClickListener {
	private ImageView iv_back;// 返回
	private RelativeLayout rl_month_traffic;// 设置流量套餐布局
	private AlertDialog dialog;
	private EditText ed_traffic;
	private SharedPreferences sp;
	private TextView tv_month_traffic_data;// 流量套餐
	private RelativeLayout rl_month_used;// 本月已用
	private Window window;
	private TextView tv_month_used_data;// 本月已用
	private RelativeLayout rl_settlement_day;// 结算日布局
	private TextView tv_settlement_day_data;// 结算日
	private ImageView iv_b_change;// 超出流量断网
	// private ImageView iv_traffic_monitoring;// 开启流量监控
	private RelativeLayout rl_traffic_warning;// 流量预警布局
	private TextView tv_traffic_warning_data;// 流量预警
	private int endProg = 0;
	private RelativeLayout rl_disnet;// 自动断网

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.traffic_setting);
		sp = getSharedPreferences("traffic", Context.MODE_PRIVATE);
		initView();

	}

	private void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		rl_month_traffic = (RelativeLayout) findViewById(R.id.rl_month_traffic);
		tv_month_traffic_data = (TextView) findViewById(R.id.tv_month_traffic_data);
		tv_month_traffic_data.setText(sp.getString("month", "未设置"));
		tv_month_used_data = (TextView) findViewById(R.id.tv_month_used_data);
		tv_month_used_data.setText(sp.getString("monthUsed", "0.00"));
		rl_settlement_day = (RelativeLayout) findViewById(R.id.rl_settlement_day);
		tv_settlement_day_data = (TextView) findViewById(R.id.tv_settlement_day_data);
		tv_settlement_day_data.setText(sp.getString("day", "未设置"));
		rl_month_used = (RelativeLayout) findViewById(R.id.rl_month_used);
		iv_back.setOnClickListener(this);
		rl_traffic_warning = (RelativeLayout) findViewById(R.id.rl_traffic_warning);
		rl_traffic_warning.setOnClickListener(this);
		tv_traffic_warning_data = (TextView) findViewById(R.id.tv_traffic_warning_data);
		String warn = sp.getString("warning", "0");
		if (warn.trim().equals("0")) {
			tv_traffic_warning_data.setText("未设置");
		} else {
			tv_traffic_warning_data.setText(warn + "%");
		}
		iv_b_change = (ImageView) findViewById(R.id.iv_b_change);
		// iv_traffic_monitoring = (ImageView)
		// findViewById(R.id.iv_traffic_monitoring);
		rl_month_traffic.setOnClickListener(this);
		rl_month_used.setOnClickListener(this);
		rl_settlement_day.setOnClickListener(this);
		iv_b_change.setOnClickListener(this);
		// iv_traffic_monitoring.setOnClickListener(this);
		rl_disnet = (RelativeLayout) findViewById(R.id.rl_disnet);
		rl_disnet.setOnClickListener(this);
		boolean isNet = sp.getBoolean("isNet", false);
		if (isNet == false) {
			iv_b_change.setBackgroundResource(R.drawable.ic_change_no);
		} else {
			iv_b_change.setBackgroundResource(R.drawable.ic_change_yes);
		}

		// boolean isTraffic = sp.getBoolean("isTraffic", false);
		// if (isTraffic == false) {
		// iv_traffic_monitoring
		// .setBackgroundResource(R.drawable.ic_change_no);
		// Intent wifiIntent = new Intent(TrafficSetActivity.this,
		// TrafficService.class);
		// stopService(wifiIntent);
		// } else {
		// iv_traffic_monitoring
		// .setBackgroundResource(R.drawable.ic_change_yes);
		// // if (sp.getBoolean("isNet", false) == true) {
		// // iv_b_change.setBackgroundResource(R.drawable.ic_change_no);
		// //
		// // }
		// Intent intent = new Intent(TrafficSetActivity.this,
		// TrafficService.class);
		// startService(intent);
		//
		// }

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_month_traffic:// 设置流量套餐

			dialog = new AlertDialog.Builder(TrafficSetActivity.this).create();
			dialog.show();
			window = dialog.getWindow();
			window.setContentView(R.layout.dailog_settraffic);
			window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);// 获取焦点，可以打开软键盘
			ed_traffic = (EditText) window.findViewById(R.id.ed_traffic);
			String monthDate = sp.getString("month", "未设置");
			ed_traffic.setHint(monthDate);
			Button bt_confirm = (Button) window.findViewById(R.id.bt_confirm);
			Button bt_cancle = (Button) window.findViewById(R.id.bt_cancle);
			bt_confirm.setOnClickListener(this);
			bt_cancle.setOnClickListener(this);
			break;

		case R.id.bt_confirm://
			if (ed_traffic.getText().toString().trim().equals("")) {
				Toast.makeText(TrafficSetActivity.this, "输入不能为空，请重新输入！",
						Toast.LENGTH_SHORT).show();
			} else {
				if (ed_traffic.getText().toString().trim().equals("0")) {
					Editor editor = sp.edit();
					editor.putString("month", "未设置");
					editor.commit();
					tv_month_traffic_data.setText(ed_traffic.getText()
							.toString());
					dialog.dismiss();
				} else {
					Editor editor = sp.edit();
					editor.putString("month", ed_traffic.getText().toString());
					editor.commit();
					tv_month_traffic_data.setText(ed_traffic.getText()
							.toString());
					dialog.dismiss();

					// TODO:初始化流量预警
					tv_traffic_warning_data.setText("85%");
					Editor editor2 = sp.edit();
					editor2.putString("warning", "85");
					editor2.commit();
					endProg = 85;
				}

			}

			break;

		case R.id.bt_cancle:
			dialog.cancel();
			break;

		case R.id.rl_month_used:// 已用流量
			dialog = new AlertDialog.Builder(TrafficSetActivity.this).create();
			dialog.show();
			window = dialog.getWindow();
			window.setContentView(R.layout.dailog_monthused);
			window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);// 获取焦点，可以打开软键盘
			ed_traffic = (EditText) window.findViewById(R.id.ed_traffic);
			String monthUsed = sp.getString("monthUsed", 0.00 + "");
			ed_traffic.setHint(monthUsed);
			Button bt_month_confirm = (Button) window
					.findViewById(R.id.bt_month_confirm);
			Button bt_month_cancle = (Button) window
					.findViewById(R.id.bt_month_cancle);
			bt_month_confirm.setOnClickListener(this);
			bt_month_cancle.setOnClickListener(this);
			break;

		case R.id.bt_month_confirm:// 已用流量设置确认
			if (ed_traffic.getText().toString().trim().equals("")) {
				Toast.makeText(TrafficSetActivity.this, "输入不能为空，请重新输入！",
						Toast.LENGTH_SHORT).show();
			} else {

				Editor editor = sp.edit();
				editor.putString("monthUsed", ed_traffic.getText().toString());
				editor.putInt("setDay", getDay());
				editor.commit();
				tv_month_used_data.setText(ed_traffic.getText().toString());
				dialog.dismiss();
			}

			break;

		case R.id.bt_month_cancle:
			dialog.cancel();
			break;
		case R.id.rl_settlement_day:// 设置结算日

			dialog = new AlertDialog.Builder(TrafficSetActivity.this).create();
			dialog.show();
			window = dialog.getWindow();
			window.setContentView(R.layout.dailog_settlement);
			window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);// 获取焦点，可以打开软键盘
			ed_traffic = (EditText) window.findViewById(R.id.ed_traffic);
			String settlement = sp.getString("day", "未设置");
			ed_traffic.setHint(settlement);
			Button bt_day_confirm = (Button) window
					.findViewById(R.id.bt_day_confirm);
			Button bt_day_cancle = (Button) window
					.findViewById(R.id.bt_day_cancle);
			bt_day_confirm.setOnClickListener(this);
			bt_day_cancle.setOnClickListener(this);
			break;

		case R.id.bt_day_cancle:
			dialog.cancel();
			break;

		case R.id.bt_day_confirm:
			int num = getCurrentMonthDay();
			String day = ed_traffic.getText().toString().trim();
			if (day.equals("")) {
				Toast.makeText(TrafficSetActivity.this, "输入不能为空，请重新输入！",
						Toast.LENGTH_SHORT).show();
			} else if (Integer.parseInt(day) > num) {
				Toast.makeText(TrafficSetActivity.this, "请输入正确的结算时间",
						Toast.LENGTH_SHORT).show();
			} else {
				if (Integer.parseInt(day) == 0) {
					Editor editor = sp.edit();
					editor.putString("day", "未设置");
					editor.commit();
					tv_settlement_day_data.setText("未设置");
				} else {
					Editor editor = sp.edit();
					editor.putString("day", ed_traffic.getText().toString());
					editor.commit();
					tv_settlement_day_data.setText(ed_traffic.getText()
							.toString());

				}
				dialog.dismiss();
			}
			break;

		case R.id.rl_disnet:// 自动断网开关 R.id.iv_b_change
			// boolean isTraffic2 = sp.getBoolean("isTraffic", false);
			// if (isTraffic2 == false) {
			// Toast.makeText(TrafficSetActivity.this, "请先开启流量监控功能",
			// Toast.LENGTH_SHORT).show();
			// } else {
			boolean isNet = sp.getBoolean("isNet", false);
			if (isNet == false) {
				iv_b_change.setBackgroundResource(R.drawable.ic_change_yes);
				Editor editor = sp.edit();
				editor.putBoolean("isNet", true);
				editor.commit();
			} else {
				iv_b_change.setBackgroundResource(R.drawable.ic_change_no);
				Editor editor = sp.edit();
				editor.putBoolean("isNet", false);
				editor.commit();
			}

			// }
			break;

		// case R.id.iv_traffic_monitoring:// 开启流量监控
		// boolean isTraffic = sp.getBoolean("isTraffic", false);
		// if (isTraffic == false) {
		// iv_traffic_monitoring
		// .setBackgroundResource(R.drawable.ic_change_yes);
		//
		// Intent trafficService = new Intent(TrafficSetActivity.this,
		// TrafficService.class);
		// startService(trafficService);
		// Editor editor = sp.edit();
		// editor.putBoolean("isTraffic", true);
		// editor.commit();
		//
		// } else {
		// iv_traffic_monitoring
		// .setBackgroundResource(R.drawable.ic_change_no);
		// Editor editor = sp.edit();
		// editor.putBoolean("isTraffic", false);
		// editor.commit();
		// if (sp.getBoolean("isNet", false) == true) {
		// iv_b_change.setBackgroundResource(R.drawable.ic_change_no);
		// Editor editor2 = sp.edit();
		// editor.putBoolean("isNet", false);
		// editor.commit();
		// }
		// Intent intent = new Intent(TrafficSetActivity.this,
		// TrafficService.class);
		// stopService(intent);
		//
		// }
		// break;
		case R.id.rl_traffic_warning:// 流量预警

			if (tv_month_traffic_data.getText().toString().trim().equals("未设置")) {
				Toast.makeText(TrafficSetActivity.this, "请先设置流量套餐",
						Toast.LENGTH_SHORT).show();
			} else {

				dialog = new AlertDialog.Builder(TrafficSetActivity.this)
						.create();
				dialog.show();
				window = dialog.getWindow();
				window.setContentView(R.layout.dailog_warning);
				final TextView tv_traffic_warning = (TextView) window
						.findViewById(R.id.tv_traffic_warning);
				window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				SeekBar seekBar_traffic_warning = (SeekBar) window
						.findViewById(R.id.sekBar_traffic_warning);
				String progress = sp.getString("warning", "0");
				seekBar_traffic_warning.setMax(50);
				if (Integer.parseInt(progress) == 0) {
					seekBar_traffic_warning.setProgress(0);
				} else {
					seekBar_traffic_warning.setProgress(Integer
							.parseInt(progress) - 50);
				}

				// 流量套餐
				String month = tv_month_traffic_data.getText().toString()
						.trim();
				int prog = Integer.parseInt(progress);
				double pre = prog * (Integer.parseInt(month)) / 100;
				tv_traffic_warning.setText(prog + "%" + "(" + pre + "MB)");
				seekBar_traffic_warning
						.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
								endProg = seekBar.getProgress() + 50;

							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								// 流量套餐
								String month = tv_month_traffic_data.getText()
										.toString().trim();
								progress = progress + 50;

								double pre = progress
										* (Integer.parseInt(month)) / 100;
								tv_traffic_warning.setText(progress + "%" + "("
										+ pre + "MB)");

							}
						});

				Button bt_warn_confirm = (Button) window
						.findViewById(R.id.bt_warn_confirm);
				Button bt_warn_cancle = (Button) window
						.findViewById(R.id.bt_warn_cancle);
				bt_warn_confirm.setOnClickListener(this);
				bt_warn_cancle.setOnClickListener(this);
			}

			break;

		case R.id.bt_warn_cancle:
			dialog.cancel();
			break;

		case R.id.bt_warn_confirm:// 流量预警设置确认
			Editor editor = sp.edit();
			editor.putString("warning", endProg + "");
			editor.commit();
			dialog.dismiss();
			tv_traffic_warning_data.setText(endProg + "%");
			break;

		default:
			break;
		}
	}

	/**
	 * 获取当月的 天数
	 * */
	public static int getCurrentMonthDay() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		int maxDate = calendar.get(Calendar.DATE);
		return maxDate;
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

	@Override
	protected void onResume() {
		super.onResume();
		// 页面统计,可见
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 不可见
		StatService.onPause(this);
	}

	/**
	 * 当月号数
	 * 
	 * @return
	 */
	public int getDay() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DATE);
	}
}
