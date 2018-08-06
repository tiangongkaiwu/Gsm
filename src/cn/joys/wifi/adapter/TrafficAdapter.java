package cn.joys.wifi.adapter;
import java.util.List;

import cn.joys.wifi.R;
import cn.joys.wifi.bean.TrafficDetailInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TrafficAdapter extends BaseAdapter {

	private List<TrafficDetailInfo> trafficDetailInfos;
	private Context context;

	public TrafficAdapter(Context context,
			List<TrafficDetailInfo> trafficDetailInfos) {
		this.trafficDetailInfos = trafficDetailInfos;
		this.context = context;
	}

	@Override
	public int getCount() {

		return trafficDetailInfos.size();
	}

	@Override
	public Object getItem(int position) {

		return trafficDetailInfos.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.traffic_detail_item,
					null);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.tv_gprs = (TextView) convertView.findViewById(R.id.tv_gprs);
			holder.tv_wifi = (TextView) convertView.findViewById(R.id.tv_wifi);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_date.setText(trafficDetailInfos.get(position).getDate());
		holder.tv_gprs.setText(trafficDetailInfos.get(position).getMobile());
		holder.tv_wifi.setText(trafficDetailInfos.get(position).getWifi());
		return convertView;
	}

	class ViewHolder {
		TextView tv_date;
		TextView tv_gprs;
		TextView tv_wifi;
	}
}
