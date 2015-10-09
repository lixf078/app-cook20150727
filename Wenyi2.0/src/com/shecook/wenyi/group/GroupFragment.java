package com.shecook.wenyi.group;

import java.util.ArrayList;
import java.util.List;

import com.shecook.wenyi.R;
import com.shecook.wenyi.cookbook.PiazzaCookbookHomeworkList;
import com.shecook.wenyi.mainpackage.FragmentTabAdapter;
import com.shecook.wenyi.piazza.PiazzaDiscoverFragment;
import com.shecook.wenyi.piazza.PiazzaQuestionFragment;
import com.shecook.wenyi.util.WenyiLog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GroupFragment extends Fragment {
	private static final String TAG = "PiazzaFragment";

	private FragmentActivity mActivity;

	private ImageView return_img, right_img;
	private TextView middle_title;

	private RadioGroup rgs;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	PiazzaDiscoverFragment discoverFragment;
	PiazzaQuestionFragment questionFragment;
	PiazzaCookbookHomeworkList foodFragment;
	PiazzaDiscoverFragment friendFragment;

	@Override
	public void onAttach(Activity activity) {
		WenyiLog.logv(TAG, "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		discoverFragment = new PiazzaDiscoverFragment();
		questionFragment = new PiazzaQuestionFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = getActivity();
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.group_fragment, container,
				false);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		right_img = (ImageView) rootView.findViewById(R.id.right_img);
		return_img = (ImageView) rootView.findViewById(R.id.return_img);
		middle_title = (TextView) rootView.findViewById(R.id.middle_title);

		right_img.setBackgroundResource(R.drawable.edit);
		return_img.setVisibility(View.INVISIBLE);
		middle_title.setText(R.string.piazza);

		fragments.add(discoverFragment);
		fragments.add(questionFragment);

		rgs = (RadioGroup) rootView.findViewById(R.id.tabs_rg);

		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(mActivity,
				fragments, R.id.personal_edition_content, rgs);
		tabAdapter
				.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
					@Override
					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
							int checkedId, int index) {
						Log.d(TAG, "OnRgsExtraCheckedChanged -> " + index);
					}
				});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		WenyiLog.logv(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		WenyiLog.logv(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		WenyiLog.logv(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		WenyiLog.logv(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		WenyiLog.logv(TAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		WenyiLog.logv(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		WenyiLog.logv(TAG, "onDetach");
		super.onDetach();
	}
}
