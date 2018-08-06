package cn.joys.wifi.ui.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import com.baidu.mobstat.StatService;

import cn.joys.wifi.R;
import cn.joys.wifi.bean.TaskInfo;
import cn.joys.wifi.engine.TaskInfoEngine;
import cn.joys.wifi.util.TextFormater;
import cn.joys.wifi.util.URLS;
import cn.joys.wifi.util.WebAppInterface;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.IntentSender.OnFinished;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameFragment extends Fragment implements OnClickListener {
	private ImageView iv_game_flying;// 设置飞机显示的屁股动画
	private TextView tv_game_start;// 点击开始加速的动画按钮
	private RelativeLayout rl_game_start;// 开始加速之前的整个布局
	private RelativeLayout rl_game_flyed;// 加速开始后整个布局
	private ImageView iv_flying;// 喷火的火箭
	private RelativeLayout rl_fly_info_4;// 加速进度显示布局
	private RelativeLayout rl_fly_info_3;
	private RelativeLayout rl_fly_info_2;
	private RelativeLayout rl_fly_info_1;
	private RelativeLayout rl_fly_info_0;
	private TextView tv_fly_info_4;// 加速进度的描述
	private TextView tv_fly_info_3;
	private TextView tv_fly_info_2;
	private TextView tv_fly_info_1;
	private TextView tv_fly_info_0;
	private TextView tv_fly_percent_4;// 加速进度百分比显示
	private TextView tv_fly_percent_3;
	private TextView tv_fly_percent_2;
	private TextView tv_fly_percent_1;
	private TextView tv_fly_percent_0;
	private RelativeLayout rl_start_rocket;// 点击之后
	private Animation translate;
	private RelativeLayout rl_fly_result;// 加速成功后改变上面部分布局

	private TextView tv_fly_result;// 显示清理结果
	private View view;
	private SharedPreferences sp;
	WebView myWebView;

	private TaskInfoEngine taskInfoEngine;
	private List<RunningAppProcessInfo> runningAppInfos;
	private ActivityManager am;
	private List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
	private int k = 0;
	private int k2 = 0;
	private int k3 = 0;
	private int k4 = 0;
	private int k5 = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				tv_fly_percent_4.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					rl_fly_info_3.setVisibility(View.VISIBLE);
					tv_fly_info_3.setText("正在启动加速程序...");
					tv_fly_percent_3.setText("已完成");
					showAnimation2(rl_fly_info_4);
				}
				break;
			case 2:
				tv_fly_percent_4.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					tv_fly_info_3.setText("正在优化CPU负载...");
					tv_fly_percent_3.setText("已完成");
					rl_fly_info_2.setVisibility(View.VISIBLE);
					tv_fly_info_2.setText("正在启动加速程序...");
					tv_fly_percent_2.setText("已完成");
					showAnimation3(rl_fly_info_4);
				}
				break;
			case 3:
				tv_fly_percent_4.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					tv_fly_info_3.setText("正在释放手机内存...");
					tv_fly_percent_3.setText("已完成");
					tv_fly_info_2.setText("正在优化CPU负载...");
					tv_fly_percent_2.setText("已完成");
					rl_fly_info_1.setVisibility(View.VISIBLE);
					tv_fly_info_1.setText("正在启动加速程序...");
					tv_fly_percent_1.setText("已完成");
					showAnimation4(rl_fly_info_4);
				}
				break;

			case 4:
				tv_fly_percent_4.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					tv_fly_info_3.setText("正在释放系统进程...");
					tv_fly_percent_3.setText("已完成");
					tv_fly_info_2.setText("正在释放手机内存...");
					tv_fly_percent_2.setText("已完成");
					tv_fly_info_1.setText("正在优化CPU负载...");
					tv_fly_percent_1.setText("已完成");
					rl_fly_info_0.setVisibility(View.VISIBLE);
					tv_fly_info_0.setText("正在启动加速程序...");
					tv_fly_percent_0.setText("已完成");
					showAnimation5(rl_fly_info_4);
				}
				break;

			case 5:
				tv_fly_percent_4.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					rl_fly_info_4
							.setBackgroundResource(R.drawable.game_bg_fly_info_0);
					tv_fly_percent_4.setText("已完成");
					rl_fly_result.setVisibility(View.VISIBLE);
					killTask();
					tv_fly_result.setText("加速成功");
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.gamefragment, null);
		initView();
		return view;
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		k = 0;
		k2 = 0;
		k3 = 0;
		k4 = 0;
		k5 = 0;

		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		translate = AnimationUtils
				.loadAnimation(getActivity(), R.anim.flashing);
		iv_game_flying = (ImageView) view.findViewById(R.id.iv_game_flying);

		rl_game_start = (RelativeLayout) view.findViewById(R.id.rl_game_start);
		rl_start_rocket = (RelativeLayout) view
				.findViewById(R.id.rl_start_rocket);

		tv_game_start = (TextView) view.findViewById(R.id.tv_game_start);
		rl_game_flyed = (RelativeLayout) view.findViewById(R.id.rl_game_flyed);
		rl_fly_info_4 = (RelativeLayout) view.findViewById(R.id.rl_fly_info_4);
		rl_fly_info_3 = (RelativeLayout) view.findViewById(R.id.rl_fly_info_3);
		rl_fly_info_2 = (RelativeLayout) view.findViewById(R.id.rl_fly_info_2);
		rl_fly_info_1 = (RelativeLayout) view.findViewById(R.id.rl_fly_info_1);
		rl_fly_info_0 = (RelativeLayout) view.findViewById(R.id.rl_fly_info_0);

		tv_fly_info_4 = (TextView) view.findViewById(R.id.tv_fly_info_4);
		tv_fly_info_3 = (TextView) view.findViewById(R.id.tv_fly_info_3);
		tv_fly_info_2 = (TextView) view.findViewById(R.id.tv_fly_info_2);
		tv_fly_info_1 = (TextView) view.findViewById(R.id.tv_fly_info_1);
		tv_fly_info_0 = (TextView) view.findViewById(R.id.tv_fly_info_0);

		tv_fly_percent_4 = (TextView) view.findViewById(R.id.tv_fly_percent_4);
		tv_fly_percent_3 = (TextView) view.findViewById(R.id.tv_fly_percent_3);
		tv_fly_percent_2 = (TextView) view.findViewById(R.id.tv_fly_percent_2);
		tv_fly_percent_1 = (TextView) view.findViewById(R.id.tv_fly_percent_1);
		tv_fly_percent_0 = (TextView) view.findViewById(R.id.tv_fly_percent_0);

		rl_fly_result = (RelativeLayout) view.findViewById(R.id.rl_fly_result);
		tv_fly_result = (TextView) view.findViewById(R.id.tv_fly_result);
		iv_flying = (ImageView) view.findViewById(R.id.iv_flying);
		tv_game_start.setOnClickListener(this);

		myWebView = (WebView) view.findViewById(R.id.wv);
		WebAppInterface.loadUrl(getActivity(), myWebView);
	}

	/**
	 * 获取进程数据
	 */
	public void initDate() {
		taskInfoEngine = new TaskInfoEngine(getActivity());
		am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);
		runningAppInfos = am.getRunningAppProcesses();
		// System.out.println(runningAppInfos.size());
		taskInfos = taskInfoEngine.getAllTasks(runningAppInfos);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_game_start:
			rl_start_rocket.setVisibility(View.VISIBLE);
			AnimationDrawable animationDrawable = (AnimationDrawable) iv_game_flying
					.getBackground();
			animationDrawable.start();
			Animation translate = AnimationUtils.loadAnimation(getActivity(),
					R.anim.out_to_up);

			iv_game_flying.startAnimation(translate);

			translate.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// System.out.println("执行完毕，稍后隐藏，加载新的页面");
					rl_game_start.setVisibility(View.GONE);
					rl_game_flyed.setVisibility(View.VISIBLE);
					AnimationDrawable animationDrawable = (AnimationDrawable) iv_flying
							.getBackground();
					animationDrawable.start();
					rl_fly_info_4.setVisibility(View.VISIBLE);
					rl_fly_info_4
							.setBackgroundResource(R.drawable.game_bg_fly_info_1);
					showAnimation(rl_fly_info_4);
				}
			});

			StatService.onEvent(getActivity(), "game_start", "game_start");
			break;

		default:
			break;
		}

	}

	public void showAnimation(final View view) {

		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 正在启动加速程序...
				tv_fly_info_4.setText("正在启动加速程序...");
				new Thread(new Runnable() {

					@Override
					public void run() {

						while (k <= 100) {
							Message msg = new Message();
							msg.what = 1;
							msg.arg1 = k;
							handler.sendMessage(msg);
							k++;
							try {
								Thread.sleep(15);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
				}).start();

			}
		});

		view.startAnimation(translate);
	}

	public void showAnimation2(final View view) {

		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 正在优化cpu负载...
				tv_fly_info_4.setText("正在优化CPU负载...");
				new Thread(new Runnable() {

					@Override
					public void run() {

						while (k2 <= 100) {
							Message msg = new Message();
							msg.what = 2;
							msg.arg1 = k2;
							handler.sendMessage(msg);
							k2++;
							try {
								Thread.sleep(60);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
				}).start();

			}
		});

		view.startAnimation(translate);
	}

	public void showAnimation3(final View view) {
		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 正在优化cpu负载...
				tv_fly_info_4.setText("正在释放手机内存...");
				new Thread(new Runnable() {

					@Override
					public void run() {

						while (k3 <= 100) {
							Message msg = new Message();
							msg.what = 3;
							msg.arg1 = k3;
							handler.sendMessage(msg);
							k3++;
							try {
								Thread.sleep(30);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
				}).start();
			}
		});

		view.startAnimation(translate);
	}

	public void showAnimation4(final View view) {
		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 正在优化cpu负载...
				tv_fly_info_4.setText("正在释放系统进程...");
				new Thread(new Runnable() {

					@Override
					public void run() {

						while (k4 <= 100) {
							Message msg = new Message();
							msg.what = 4;
							msg.arg1 = k4;
							handler.sendMessage(msg);
							k4++;
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
				}).start();
			}
		});

		view.startAnimation(translate);
	}

	public void showAnimation5(final View view) {
		Animation translate = AnimationUtils.loadAnimation(getActivity(),
				R.anim.flashing);
		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 正在优化cpu负载...
				tv_fly_info_4.setText("正在清除缓存文件...");
				new Thread(new Runnable() {

					@Override
					public void run() {

						while (k5 <= 100) {
							Message msg = new Message();
							msg.what = 5;
							msg.arg1 = k5;
							handler.sendMessage(msg);
							k5++;
							try {
								Thread.sleep(20);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
				}).start();
			}
		});
		view.startAnimation(translate);
	}

	/**
	 * 一键清理 按键监听事件
	 */
	public void killTask() {
		int total = 0;
		int memorySize = 0;
		for (TaskInfo taskInfo : taskInfos) {
			if (!taskInfo.getPackName().equals("com.example.wifitestdemo")) {
				memorySize += taskInfo.getMemorySize();
				// 杀死选中进程
				am.killBackgroundProcesses(taskInfo.getPackName());
				// taskInfos.remove(taskInfo);
				total++;
			}
		}
		// 通知用户清理结果
		String size = TextFormater.getKbDateSize(memorySize);
		Toast.makeText(getActivity(),
				"清理了" + total + "个进程" + ",释放了" + size + "空间", 0).show();
		tv_fly_result.setText("清理了" + total + "个进程" + ",释放了" + size + "空间");
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);
		rl_game_start.setVisibility(View.VISIBLE);
		rl_start_rocket.setVisibility(View.GONE);
		rl_fly_info_0.setVisibility(View.GONE);
		rl_fly_info_1.setVisibility(View.GONE);
		rl_fly_info_2.setVisibility(View.GONE);
		rl_fly_info_3.setVisibility(View.GONE);
		rl_fly_info_4.setVisibility(View.GONE);
		rl_game_flyed.setVisibility(View.GONE);
		rl_fly_result.setVisibility(View.GONE);
		initView();
	}

	@Override
	public void onPause() {
		super.onPause();
		k = 101;
		k2 = 101;
		k3 = 101;
		k4 = 101;
		k5 = 101;
		StatService.onPause(this);
	}
}
