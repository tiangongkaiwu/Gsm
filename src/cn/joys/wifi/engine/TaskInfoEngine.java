package cn.joys.wifi.engine;

import java.util.ArrayList;
import java.util.List;

import cn.joys.wifi.R;
import cn.joys.wifi.bean.TaskInfo;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

public class TaskInfoEngine {
	private Context context;
	private PackageManager pm;
	private ActivityManager am;

	public TaskInfoEngine(Context context) {
		this.context = context;
		pm = context.getPackageManager();
		am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
	}

	public List<TaskInfo> getAllTasks(
			List<RunningAppProcessInfo> runningAppInfos) {
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo info : runningAppInfos) {
			TaskInfo taskInfo;
			try {
				taskInfo = new TaskInfo();
				int pid = info.pid;
				taskInfo.setPid(pid);
				String packName = info.processName;
				taskInfo.setPackName(packName);
				ApplicationInfo appInfo = pm.getPackageInfo(packName, 0).applicationInfo;
				// 判断是否为第三方应用程序
				if (filterApp(appInfo)) {
					taskInfo.setSystemApp(false);
				} else {
					taskInfo.setSystemApp(true);
				}
				// 获取应用程序图片
				Drawable appIcon = appInfo.loadIcon(pm);
				taskInfo.setAppIcon(appIcon);
				// 获取应用程序名字
				String appName = appInfo.loadLabel(pm).toString();
				taskInfo.setAppName(appName);
				// 获取进程内存信息,返回一个数组
				MemoryInfo[] memoryInfos = am
						.getProcessMemoryInfo(new int[] { pid });
				// 获取使用进程的内存大小，KB为单位
				int memorySize = memoryInfos[0].getTotalPrivateDirty();
				taskInfo.setMemorySize(memorySize);
				taskInfos.add(taskInfo);
				taskInfo = null;
			} catch (NameNotFoundException e) {
				// e.printStackTrace();
				taskInfo = new TaskInfo();
				int pid = info.pid;
				taskInfo.setPid(pid);
				String packName = info.processName;
				taskInfo.setPackName(packName);
				taskInfo.setAppName(packName);
				Drawable appIcon = context.getResources().getDrawable(
						R.drawable.ic_launcher);
				taskInfo.setAppIcon(appIcon);
				taskInfo.setSystemApp(true);
				// 获取进程内存信息,返回一个数组
				MemoryInfo[] memoryInfos = am
						.getProcessMemoryInfo(new int[] { pid });
				// 获取使用进程的内存大小，KB为单位
				int memorySize = memoryInfos[0].getTotalPrivateDirty();
				taskInfo.setMemorySize(memorySize);
				taskInfos.add(taskInfo);
				taskInfo = null;
			}
		}
		return taskInfos;

	}

	/**
	 * 判断某个应用程序是不是三方应用，更新的系统应用也是三方应用
	 * 
	 * @param info
	 * @return
	 */
	public boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}
