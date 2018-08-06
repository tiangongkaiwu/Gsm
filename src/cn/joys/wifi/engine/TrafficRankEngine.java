package cn.joys.wifi.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.joys.wifi.bean.TrafficRankInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;

/**
 * 计算流量排行
 * 
 * @author Administrator
 * 
 */
public class TrafficRankEngine {
	private Context context;
	private List<ResolveInfo> resolveInfos;
	private PackageManager pm;
	private List<TrafficRankInfo> trafficRankInfos;

	public TrafficRankEngine(Context context) {
		this.context = context;
		pm = context.getPackageManager();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		resolveInfos = pm.queryIntentActivities(intent,
				PackageManager.GET_UNINSTALLED_PACKAGES);// PackageManager.GET_UNINSTALLED_PACKAGES
															// PackageManager.MATCH_DEFAULT_ONLY)
		// System.out.println(resolveInfos.size());
		trafficRankInfos = new ArrayList<TrafficRankInfo>();
	}

	public List<TrafficRankInfo> getUidTraffic() {
		for (int i = 0; i < resolveInfos.size(); i++) {
			TrafficRankInfo trafficRankInfo = new TrafficRankInfo();
			ResolveInfo info = resolveInfos.get(i);
			String appName = info.loadLabel(pm).toString();
			// System.out.println(appName);
			Drawable appIcon = info.loadIcon(pm);
			// Bitmap resizeIcon = ImageUtil.getResizedBitmap(
			// (BitmapDrawable) appIcon, context);
			// 获取包名
			String packName = info.activityInfo.packageName;
			try {
				PackageInfo packageInfo = pm.getPackageInfo(packName, 0);
				// 获取应用程序UID
				int uid = packageInfo.applicationInfo.uid;
				long uidTraffic = TrafficStats.getUidTxBytes(uid)
						+ TrafficStats.getUidRxBytes(uid);
				trafficRankInfo.setAppName(appName);
				trafficRankInfo.setBitmap(appIcon);
				trafficRankInfo.setTraffic(uidTraffic);
				trafficRankInfos.add(trafficRankInfo);

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		Collections.sort(trafficRankInfos, new Comparator<TrafficRankInfo>() {

			@Override
			public int compare(TrafficRankInfo rankInfo1,
					TrafficRankInfo rankInfo2) {
				if (rankInfo2.getTraffic() > rankInfo1.getTraffic()) {
					return 1;
				} else if (rankInfo1.getTraffic() == rankInfo2.getTraffic()) {
					return 0;
				} else {
					return -1;
				}
			}
		});
		return trafficRankInfos;
	}

}
