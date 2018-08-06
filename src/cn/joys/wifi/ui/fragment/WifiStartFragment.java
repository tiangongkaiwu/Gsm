package cn.joys.wifi.ui.fragment;

import com.baidu.mobstat.StatService;
import cn.joys.wifi.R;
import cn.joys.wifi.view.MyProgressBar;
import cn.joys.wifi.view.WaterWave;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiStartFragment extends Fragment {
	private View view;
	private MyProgressBar pb;
	private int pre;// 随机生成百分比数
	private TextView tv_pre;
	private WaterWave waveView;
	private TextView tv_content;
	private ImageView iv_plane;
	private WifiFragment wifiFragment;
	private SharedPreferences sp;
	private FragmentManager fragmentManager;
	private AnimationDrawable animationDrawable;
	private boolean isStop = true;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				tv_pre.setText(msg.arg1 + "");
				if (msg.arg1 == -1) {
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pre = (int) (Math.random() * 40 + 40);
					setInPB(pre);
				}
				break;
			case 1:
				tv_pre.setText(msg.arg1 + "");
				break;
			case 2:
				tv_content.setText("3G/4G模块的校准...");
				setContent(tv_content.getText().toString());
				break;
			case 3:
				tv_content.setText("开始3G/4G模块...");
				setContent(tv_content.getText().toString());
				break;
			case 4:
				tv_content.setText("开始优化...");
				setContent(tv_content.getText().toString());
				break;
			case 5:
				tv_content.setText("选择最佳端口...");
				setContent(tv_content.getText().toString());
				break;
			case 6:
				tv_content.setText("检查网络连接...");
				setContent(tv_content.getText().toString());
				break;
			case 7:
				tv_content.setText("你已经获得更好的链接!");
				setContent(tv_content.getText().toString());
				break;
			case 8:
				Editor editor = sp.edit();
				editor.putBoolean("isSignal", true);
				editor.commit();
				wifiFragment = new WifiFragment();
				fragmentManager = getActivity().getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.ll_main_middle, wifiFragment).commit();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.wifi_start_fragment, container, false);
		isStop = false;
		sp = getActivity().getSharedPreferences("wifi", Context.MODE_PRIVATE);
		pb = (MyProgressBar) view.findViewById(R.id.pb);
		pre = (int) (Math.random() * 40 + 40);
		tv_pre = (TextView) view.findViewById(R.id.tv_pre);
		tv_pre.setText(pre + "");
		waveView = (WaterWave) view.findViewById(R.id.waveview1);
		waveView.startWave();
		tv_content = (TextView) view.findViewById(R.id.tv_content);
		iv_plane = (ImageView) view.findViewById(R.id.iv_plane);
		animationDrawable = (AnimationDrawable) iv_plane.getBackground();
		animationDrawable.start();
		setContent(tv_content.getText().toString());
		setPB(pre);
		return view;
	}

	public void setPB(final int progress) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int count = progress;
				while (count != -1) {

					pb.setProgress(count);
					pre--;
					count--;

					Message msg = new Message();
					msg.what = 0;
					msg.arg1 = pre;

					handler.sendMessage(msg);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

	public void setInPB(final int progress) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int count = 0;
				while (count <= progress) {// 15=20% 10=10% 65=100%
					pb.setProgress(count);
					count++;
					Message msg = new Message();
					msg.what = 1;
					msg.arg1 = count;

					handler.sendMessage(msg);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

	}

	@Override
	public void onResume() {
		super.onResume();
		isStop = false;
		// System.out.println("onResume()");
		StatService.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// System.out.println("onPause()");
		isStop = true;
		StatService.onPause(this);
	}

	@Override
	public void onDestroy() {
		// System.out.println("onDestroy()");
		isStop = true;
		super.onDestroy();
	}

	public void setContent(String content) {
		if (isStop == false) {
			if (content.equals("正在初始化应用程序...")) {
				handler.sendEmptyMessageDelayed(2, 1200);
			} else if (content.equals("3G/4G模块的校准...")) {
				handler.sendEmptyMessageDelayed(3, 1700);
			} else if (content.equals("开始3G/4G模块...")) {
				handler.sendEmptyMessageDelayed(4, 1700);
			} else if (content.equals("开始优化...")) {
				handler.sendEmptyMessageDelayed(5, 900);
			} else if (content.equals("选择最佳端口...")) {
				handler.sendEmptyMessageDelayed(6, 1000);
			} else if (content.equals("检查网络连接...")) {
				handler.sendEmptyMessageDelayed(7, 1000);
			} else if (content.equals("你已经获得更好的链接!")) {// 你已经获得更好的链接！
														// (content.equals("你已经获得更好的链接!"))
				handler.sendEmptyMessageDelayed(8, 500);
			} else {
				Editor editor = sp.edit();
				editor.putBoolean("isSignal", false);
				editor.commit();
			}
		} else {
			Editor editor = sp.edit();
			editor.putBoolean("isSignal", false);
			editor.commit();
		}
	}
}
