package com.shecook.wenyi.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.util.WenyiLog;

public class PersonalFragment extends Fragment implements OnClickListener{
	private static final String LOGTAG = "PersonalFragment";
	@Override
	public void onAttach(Activity activity) {
		WenyiLog.logv(LOGTAG, "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(LOGTAG, "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(LOGTAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.personal_fragment, container, false);
		initView(rootView);
		return rootView;
	}

	public void initView(View rootView){
		ImageView settings = (ImageView) rootView.findViewById(R.id.personal_center_settings);
		settings.setOnClickListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		WenyiLog.logv(LOGTAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		WenyiLog.logv(LOGTAG, "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		WenyiLog.logv(LOGTAG, "onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		WenyiLog.logv(LOGTAG, "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		WenyiLog.logv(LOGTAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		WenyiLog.logv(LOGTAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		WenyiLog.logv(LOGTAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		WenyiLog.logv(LOGTAG, "onDetach");
		super.onDetach();
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.personal_center_settings:
			Intent intent = new Intent(this.getActivity(),PersonalSettings.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	
}
