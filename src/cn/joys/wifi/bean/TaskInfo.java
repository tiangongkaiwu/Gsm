package cn.joys.wifi.bean;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private String appName;
	private Drawable appIcon;
	private int pid;
	private int memorySize;
	private boolean ischecked;
	private boolean systemApp;

	private String packName;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public boolean isIschecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public boolean isSystemApp() {
		return systemApp;
	}

	public void setSystemApp(boolean systemApp) {
		this.systemApp = systemApp;
	}

	public TaskInfo() {

	}

	public TaskInfo(String appName, Drawable appIcon, int pid, int memorySize,
			boolean ischecked, boolean systemApp, String packName) {
		this.appName = appName;
		this.appIcon = appIcon;
		this.pid = pid;
		this.memorySize = memorySize;
		this.ischecked = ischecked;
		this.systemApp = systemApp;
		this.packName = packName;
	}

}
