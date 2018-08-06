package cn.joys.wifi.dao;

import java.util.List;

import cn.joys.wifi.bean.Traffic;

public interface ITraffic {
	/**
	 * 增加新的流量记录到数据库
	 */
	public void add(Traffic traffic);

	/**
	 * 更新数据库记录
	 */
	public void update(Traffic traffic);

	/**
	 * 根据日期查询流量信息
	 * 
	 * @param date
	 * @return 返回traffic对象信息
	 */
	public Traffic getTraffic(String date, int flag);

	/**
	 * 删除流量信息
	 * 
	 * @param date
	 * @param flag
	 */
	public void delTraffic(String date, int flag);

	public void updateTraffic(Traffic traffic);

	public Traffic getTrafficByFlag(int flag);

	public void updateTraffic2(Traffic traffic);

	public List<Traffic> getTrafficByDay(int month, int day);

	public List<Traffic> getMaxDay(int month, int flag);// 重启时间

	public List<Traffic> getMaxDayReboot(int flag);// 重启时间

	public Traffic getTrafficByMD(int month, int day, int flag);

	public List<Traffic> getTrafficToday(int month, int day);

	// public List<Traffic> getTrafficByDate(String date, int flag);
}
