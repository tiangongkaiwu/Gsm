package cn.joys.wifi.adapter;

import java.util.List;
import cn.joys.wifi.R;
import cn.joys.wifi.bean.TrafficRankInfo;
import cn.joys.wifi.util.ImageUtil;
import cn.joys.wifi.util.TextFormater;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrafficRankAdapter extends BaseAdapter {
	private Context context;
	private List<TrafficRankInfo> trafficRankInfos;

	public TrafficRankAdapter(Context context,
			List<TrafficRankInfo> trafficRankInfos) {
		this.context = context;
		this.trafficRankInfos = trafficRankInfos;
	}

	@Override
	public int getCount() {
		return trafficRankInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return trafficRankInfos.get(position);
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
			convertView = View.inflate(context, R.layout.traffic_rank_item,
					null);
			holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_traffic = (TextView) convertView
					.findViewById(R.id.tv_traffic);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Bitmap resizeIcon = ImageUtil.getResizedBitmap(
				(BitmapDrawable) trafficRankInfos.get(position).getBitmap(),
				context);
		holder.iv_icon.setImageBitmap(resizeIcon);
		holder.tv_name.setText(trafficRankInfos.get(position).getAppName());

		holder.tv_traffic.setText(TextFormater.getDate(trafficRankInfos.get(
				position).getTraffic()));
		return convertView;
	}

	class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_traffic;
	}
}
