package cn.joys.wifi.ui.fragment;

import java.util.Timer;
import java.util.TimerTask;

import cn.joys.wifi.R;
import cn.joys.wifi.ui.MainActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class InitThreeFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.three, null);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(), MainActivity.class);
				getActivity().startActivity(in);
				getActivity().finish();
			}
		});
	/*	Timer timer = new Timer();
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
		
			}
		};
		timer.schedule(tt, 3000);*/


		return view;
	}
}
