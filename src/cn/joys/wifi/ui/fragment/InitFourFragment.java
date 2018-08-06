package cn.joys.wifi.ui.fragment;
import cn.joys.wifi.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
public class InitFourFragment extends Fragment {
	private CheckBox isdown;// 是否安装好搜
	private CheckStateListener listenr;
	private ImageView iv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		listenr = (CheckStateListener) getActivity();
	}

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.four, null);
		// 是否安装360好搜
		isdown = (CheckBox) view.findViewById(R.id.isdown);
		iv = (ImageView) view.findViewById(R.id.iv);
		isdown.setChecked(true);
		isdown.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				listenr.checkState(arg1);
			}
		});

		return view;
	}

	public interface CheckStateListener {
		void checkState(boolean b);
	}

}
