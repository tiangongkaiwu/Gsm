package cn.joys.wifi.ui;

import java.util.ArrayList;
import java.util.List;
import com.baidu.mobstat.StatService;
import cn.joys.wifi.R;
import cn.joys.wifi.adapter.TrafficRankAdapter;
import cn.joys.wifi.bean.TrafficRankInfo;
import cn.joys.wifi.engine.TrafficRankEngine;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

public class TrafficRankActivity extends Activity {
	private ListView lv_traffic_rank;
	private TrafficRankAdapter adapter;
	private TrafficRankEngine rankEngine;
	private List<TrafficRankInfo> trafficRankInfos = new ArrayList<TrafficRankInfo>();
	private ImageView iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.traffic_rank);
		lv_traffic_rank = (ListView) findViewById(R.id.lv_traffic_rank);
		rankEngine = new TrafficRankEngine(TrafficRankActivity.this);
		trafficRankInfos = rankEngine.getUidTraffic();
		adapter = new TrafficRankAdapter(TrafficRankActivity.this,
				trafficRankInfos);
		lv_traffic_rank.setAdapter(adapter);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
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

}
