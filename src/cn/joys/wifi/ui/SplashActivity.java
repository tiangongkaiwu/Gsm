package cn.joys.wifi.ui;
import cn.joys.wifi.R;
import com.baidu.mobstat.StatService;
import com.trinea.connect.AdManager;
import com.trinea.connect.st.SplashView;
import com.trinea.connect.st.SpotDialogListener;
import com.trinea.connect.st.SpotManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {
	private SharedPreferences sp;
	private static final int GO_HOME = 1;
	private static final int GO_GUIDE = 2;
	SplashView splashView;
	Context context;
	View splash;
	RelativeLayout splashLayout;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				// 将第一次启动的标识设置为false
				Editor editor = sp.edit();
				editor.putBoolean("FIRST_START", false);
				// 提交设置
				editor.commit();
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;
		// 初始化接口，应用启动的时候调用
		
		AdManager.getInstance(context).init("caa36855abeb6e44", "f1324adda581cbf6");

		// 第二个参数传入目标activity，或者传入null，改为setIntent传入跳转的intent
		splashView = new SplashView(context, null);
		// 设置是否显示倒数
		splashView.setShowReciprocal(true);
		// 隐藏关闭按钮
		splashView.hideCloseBtn(true);
		

		Intent intent = new Intent(context, MainActivity.class);
		splashView.setIntent(intent);
		splashView.setIsJumpTargetWhenFail(true);
		splash = splashView.getSplashView();
			
		setContentView(R.layout.splash_activity);
		splashLayout = ((RelativeLayout) findViewById(R.id.splashview));
		splashLayout.setVisibility(View.GONE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
		params.addRule(RelativeLayout.ABOVE, R.id.cutline);
	    splashLayout.addView(splash, params);
		
		sp = getSharedPreferences("tv_three", MODE_PRIVATE);
		boolean isFirst = sp.getBoolean("FIRST_START", true);
		if (isFirst) {
			// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
		}
	}

	/**
	 * 主页
	 */
	private void goHome() {
			SpotManager.getInstance(context).showSplashSpotAds(context, splashView,
					new SpotDialogListener() {

						@Override
						public void onShowSuccess() {
							splashLayout.setVisibility(View.VISIBLE);
							splashLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pic_enter_anim_alpha));
							Log.d("youmisdk", "展示成功");
						}

						@Override
						public void onShowFailed() {
							Log.d("youmisdk", "展示失败");
						}

						@Override
						public void onSpotClosed() {
							Log.d("youmisdk", "展示关闭");
						}

						@Override
						public void onSpotClick(boolean isWebPath) {
							Log.i("YoumiAdDemo", "插屏点击");
						}
					});
	/*	Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();*/

	}
	
	/**
	 * 新手引导
	 */
	private void goGuide() {
		Intent intent = new Intent(SplashActivity.this, InitActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.splash_in, R.anim.splash_out);
	}

	@Override
	protected void onResume() {                                                                                                                                                              
		/**
		 * 设置为竖屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
		// 页面统计
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}
	
	@Override
	public void onBackPressed() {
		// 取消后退键
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// land
		} else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// port
		}
	}
	
	
}
