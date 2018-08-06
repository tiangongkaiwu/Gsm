package cn.joys.wifi.util;

import java.text.DecimalFormat;

public class TextFormater {
	/**
	 * 返回byte数据大小对应的文本
	 * 
	 * @param size
	 * @return
	 */
	public static String getDateSize(long size) {
		if (size < 0) {
			size = 0;
		}
		DecimalFormat format = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "bytes";
		} else if (size < 1024 * 1024) {
			float kbsize = size / 1024f;
			return format.format(kbsize) + "KB";

		} else if (size < 1024 * 1024 * 1024) {
			float mbsize = size / 1024f / 1024f;
			return format.format(mbsize) + "MB";
		} else if (size < 1024 * 1024 * 1024 * 1024) {
			float gbsize = size / 1024f / 1024f / 1024f;
			return format.format(gbsize) + "GB";
		} else {
			return "size:error";
		}

	}

	/**
	 * 返回kb数据大小对应的文本
	 * 
	 * @param size
	 * @return
	 */
	public static String getKbDateSize(long size) {
		if (size <= 0) {
			size = 0;
		}
		return getDateSize(size * 1024);
	}

	/**
	 * 返回数据大小，以MB为单位
	 * 
	 * @param size
	 * @return
	 */
	public static String getDate(long size) {
		if (size <= 0) {
			return "0.00";
		} else {
			DecimalFormat format = new DecimalFormat("####0.00");
			float mbsize = size / 1024f / 1024f;
			return format.format(mbsize);
		}

	}
}
