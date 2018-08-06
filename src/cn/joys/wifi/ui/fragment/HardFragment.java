package cn.joys.wifi.ui.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import com.baidu.mobstat.StatService;

import cn.joys.wifi.R;
import cn.joys.wifi.util.URLS;
import cn.joys.wifi.util.WebAppInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.JsResult;

;

public class HardFragment extends Fragment implements OnClickListener {
	private View view;
	private TextView tv_hard_start;// 开机硬件加速
	private LinearLayout ll_hard_start;// 加速之前
	private LinearLayout ll_hard_end;// 加速之后
	private LinearLayout ll_hard_middle;// 加速时
	private RelativeLayout rl_hard_middle_1;
	private RelativeLayout rl_hard_middle_2;
	private RelativeLayout rl_hard_middle_3;
	private RelativeLayout rl_hard_middle_4;
	private RelativeLayout rl_hard_middle_5;
	private RelativeLayout rl_hard_middle_6;
	private TextView tv_hard_middle_1;
	private TextView tv_hard_middle_2;
	private TextView tv_hard_middle_3;
	private TextView tv_hard_middle_4;
	private TextView tv_hard_middle_5;
	private SharedPreferences sp;
	private TextView tv_hard_end;// 加速完成
	private boolean isHard;// 是否加速标识
	private Animation translate;
	WebView myWebView;
	private int k = 0;
	private int k2 = 0;
	private int k3 = 0;
	private int k4 = 0;
	private int k5 = 0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				tv_hard_middle_1.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					rl_hard_middle_2.setVisibility(View.VISIBLE);
					showAnimation2(rl_hard_middle_2);
				}
				break;
			case 3:
				tv_hard_middle_2.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					rl_hard_middle_3.setVisibility(View.VISIBLE);
					showAnimation3(rl_hard_middle_3);
				}
				break;
			case 4:
				tv_hard_middle_3.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					rl_hard_middle_4.setVisibility(View.VISIBLE);
					showAnimation4(rl_hard_middle_4);
				}
				break;

			case 5:
				tv_hard_middle_4.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					rl_hard_middle_5.setVisibility(View.VISIBLE);
					showAnimation5(rl_hard_middle_5);
				}
				break;

			case 6:
				tv_hard_middle_5.setText(msg.arg1 + "%");
				if (msg.arg1 == 100) {
					rl_hard_middle_6.setVisibility(View.VISIBLE);
					ll_hard_middle.setVisibility(View.GONE);
					ll_hard_end.setVisibility(View.VISIBLE);
					/*
					 * Editor editor = sp.edit(); editor.putBoolean("isHard",
					 * true); editor.commit();
					 */
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.hardfragment, container, false);
		initView();
		return view;
	}

	private void initView() {
		k = 0;
		k2 = 0;
		k3 = 0;
		k4 = 0;
		k5 = 0;
		sp = getActivity().getSharedPreferences("wifi", Context.MODE_PRIVATE);
		tv_hard_start = (TextView) view.findViewById(R.id.tv_hard_start);
		tv_hard_start.setOnClickListener(this);
		tv_hard_end = (TextView) view.findViewById(R.id.tv_hard_end);
		tv_hard_end.setOnClickListener(this);
		ll_hard_start = (LinearLayout) view.findViewById(R.id.ll_hard_start);
		ll_hard_end = (LinearLayout) view.findViewById(R.id.ll_hard_end);
		ll_hard_middle = (LinearLayout) view.findViewById(R.id.ll_hard_middle);
		rl_hard_middle_1 = (RelativeLayout) view
				.findViewById(R.id.rl_hard_middle_1);
		rl_hard_middle_2 = (RelativeLayout) view
				.findViewById(R.id.rl_hard_middle_2);
		rl_hard_middle_3 = (RelativeLayout) view
				.findViewById(R.id.rl_hard_middle_3);
		rl_hard_middle_4 = (RelativeLayout) view
				.findViewById(R.id.rl_hard_middle_4);
		rl_hard_middle_5 = (RelativeLayout) view
				.findViewById(R.id.rl_hard_middle_5);
		rl_hard_middle_6 = (RelativeLayout) view
				.findViewById(R.id.rl_hard_middle_6);
		tv_hard_middle_1 = (TextView) view.findViewById(R.id.tv_hard_middle_1);
		tv_hard_middle_2 = (TextView) view.findViewById(R.id.tv_hard_middle_2);
		tv_hard_middle_3 = (TextView) view.findViewById(R.id.tv_hard_middle_3);
		tv_hard_middle_4 = (TextView) view.findViewById(R.id.tv_hard_middle_4);
		tv_hard_middle_5 = (TextView) view.findViewById(R.id.tv_hard_middle_5);
		isHard = sp.getBoolean("isHard", false);
		/*
		 * if (isHard == true) { ll_hard_start.setVisibility(View.GONE);
		 * ll_hard_end.setVisibility(View.VISIBLE); }
		 */
		translate = AnimationUtils
				.loadAnimation(getActivity(), R.anim.flashing);

		myWebView = (WebView) view.findViewById(R.id.wv);
		WebAppInterface.loadUrl(getActivity(), myWebView);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_hard_start:// 开始加速
			ll_hard_start.setVisibility(View.GONE);
			ll_hard_middle.setVisibility(View.VISIBLE);
			rl_hard_middle_1.setVisibility(View.VISIBLE);
			showAnimation(rl_hard_middle_1);

			break;
		case R.id.tv_hard_end:// 加速完成
			Toast.makeText(getActivity(), "已加速成功，无需重新加速", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}
	}

	public void showAnimation(final View v) {
		// translate = AnimationUtils.loadAnimation(getActivity(),
		// R.anim.flashing);
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

				new Thread(new Runnable() {

					@Override
					public void run() {

						while (k <= 100) {
							Message msg = new Message();
							msg.what = 2;
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
		v.setAnimation(translate);
		translate.start();
		// view.startAnimation(translate);
	}

	public void showAnimation2(final View v) {

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
				new Thread(new Runnable() {

					@Override
					public void run() {

						while (k2 <= 100) {
							Message msg = new Message();
							msg.what = 3;
							msg.arg1 = k2;
							handler.sendMessage(msg);
							k2++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
				}).start();

			}
		});
		v.setAnimation(translate);
		translate.start();

	}

	public void showAnimation3(final View v) {
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
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (k3 <= 100) {
							Message msg = new Message();
							msg.what = 4;
							msg.arg1 = k3;
							handler.sendMessage(msg);
							k3++;
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
		v.setAnimation(translate);
		translate.start();
	}

	public void showAnimation4(final View v) {
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
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (k4 <= 100) {
							Message msg = new Message();
							msg.what = 5;
							msg.arg1 = k4;
							handler.sendMessage(msg);
							k4++;
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
		v.setAnimation(translate);
		translate.start();
	}

	public void showAnimation5(final View v) {
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
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (k5 <= 100) {
							Message msg = new Message();
							msg.what = 6;
							msg.arg1 = k5;
							handler.sendMessage(msg);
							k5++;
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
		v.setAnimation(translate);
		translate.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onResume() {
		super.onResume();
		if (sp.getBoolean("isHard", false) == false) {
			ll_hard_start.setVisibility(View.VISIBLE);
			ll_hard_middle.setVisibility(View.GONE);
			rl_hard_middle_1.setVisibility(View.GONE);
			rl_hard_middle_2.setVisibility(View.GONE);
			rl_hard_middle_3.setVisibility(View.GONE);
			rl_hard_middle_4.setVisibility(View.GONE);
			rl_hard_middle_5.setVisibility(View.GONE);
			rl_hard_middle_6.setVisibility(View.GONE);
			initView();
		} else {
			rl_hard_middle_1.setVisibility(View.GONE);
			rl_hard_middle_2.setVisibility(View.GONE);
			rl_hard_middle_3.setVisibility(View.GONE);
			rl_hard_middle_4.setVisibility(View.GONE);
			rl_hard_middle_5.setVisibility(View.GONE);
			rl_hard_middle_6.setVisibility(View.GONE);
			ll_hard_middle.setVisibility(View.GONE);
			ll_hard_end.setVisibility(View.VISIBLE);
			initView();
		}
		StatService.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);
		k = 101;
		k2 = 101;
		k3 = 101;
		k4 = 101;
		k5 = 101;
		// tv_hard_middle_1.setText("%");
		// tv_hard_middle_2.setText("%");
		// tv_hard_middle_3.setText("%");
		// tv_hard_middle_4.setText("%");
		// tv_hard_middle_5.setText("%");

	}

}
