package cn.joys.wifi.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import cn.joys.wifi.R;
import cn.joys.wifi.ui.fragment.InitFourFragment;
import cn.joys.wifi.ui.fragment.InitFourFragment.CheckStateListener;
import cn.joys.wifi.ui.fragment.InitOneFragment;
import cn.joys.wifi.ui.fragment.InitThreeFragment;
import cn.joys.wifi.ui.fragment.InitTwoFragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
/**
 * 初始化动画
 * 
 * @author Administrator
 * 
 */
@SuppressLint("NewApi")
public class InitActivity extends FragmentActivity {

	private ViewPager init_animation_viewpager;
	private List<Fragment> listfragments;// 碎片集合
	private InitAdapter adapter;
	private int lastX;// 最后一次点击的X坐标
	private SharedPreferences sp;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Intent intent = new Intent(InitActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.splash_in, R.anim.splash_out);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		if (sp.getBoolean("isFirstStart", true)) {
			sp.edit().putBoolean("isFirstStart", false).commit();
			setContentView(R.layout.initmain);
			listfragments = new ArrayList<Fragment>();
			// 初始化数据
			InitDate();
			init_animation_viewpager = (ViewPager) findViewById(R.id.init_animation_viewpager);
			adapter = new InitAdapter(getSupportFragmentManager());
			init_animation_viewpager.setOffscreenPageLimit(2);
			init_animation_viewpager.setAdapter(adapter);

		} else {
			setContentView(R.layout.initmain);
			handler.sendEmptyMessageDelayed(1, 2000);
		}
		
	}

	private void InitDate() {
		listfragments.add(new InitOneFragment());
		listfragments.add(new InitTwoFragment());
		listfragments.add(new InitThreeFragment());

	}

	/**
	 * 自定义设配器
	 */
	class InitAdapter extends FragmentPagerAdapter {

		public InitAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return listfragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listfragments.size();
		}

	}

}
