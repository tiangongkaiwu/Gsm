package cn.joys.wifi.bean;

import android.graphics.drawable.Drawable;

public class TrafficRankInfo {
	private String appName;
	private long traffic;
	private Drawable bitmap;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public long getTraffic() {
		return traffic;
	}

	public Drawable getBitmap() {
		return bitmap;
	}

	public void setBitmap(Drawable bitmap) {
		this.bitmap = bitmap;
	}

	public void setTraffic(long traffic) {
		this.traffic = traffic;
	}

}
