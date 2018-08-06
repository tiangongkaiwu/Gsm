package cn.joys.wifi.ui.fragment;
import java.util.Timer;
import java.util.TimerTask;

import cn.joys.wifi.R;
import cn.joys.wifi.ui.InitActivity;
import cn.joys.wifi.ui.MainActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InitOneFragment extends Fragment{
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.one,null);
	
		return view;
	}
}
