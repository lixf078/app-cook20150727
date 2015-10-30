package com.shecook.wenyi.piazza;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.CreatePersonalInfoActivity;
import com.shecook.wenyi.cookbook.PiazzaCookbookHomeworkList;
import com.shecook.wenyi.mainpackage.FragmentTabAdapter;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;

public class PiazzaFragment extends Fragment implements OnClickListener{
	private static final String TAG = "PiazzaFragment";

	private FragmentActivity mActivity;

	private ImageView return_img, right_img;
	private TextView middle_title;

	private RadioGroup rgs;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	PiazzaDiscoverFragment discoverFragment;
	PiazzaQuestionFragment questionFragment;
	PiazzaCookbookHomeworkList foodFragment;
//	PiazzaDiscoverFragment friendFragment;

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
		foodFragment = new PiazzaCookbookHomeworkList();
//		friendFragment = new PiazzaDiscoverFragment();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = getActivity();
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.piazza_fragment, container,
				false);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		right_img = (ImageView) rootView.findViewById(R.id.right_img);
		return_img = (ImageView) rootView.findViewById(R.id.return_img);
		middle_title = (TextView) rootView.findViewById(R.id.middle_title);

		right_img.setBackgroundResource(R.drawable.edit);
		right_img.setOnClickListener(this);
		
		return_img.setVisibility(View.INVISIBLE);
		middle_title.setText(R.string.piazza);

		fragments.add(discoverFragment);
		fragments.add(questionFragment);
		fragments.add(foodFragment);
//		fragments.add(friendFragment);

		rgs = (RadioGroup) rootView.findViewById(R.id.piazza_tabs_rg);

		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(mActivity,
				fragments, R.id.piazza_personal_edition_content, rgs);
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
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.right_img:
			getBottomDialog();
			break;

		default:
			break;
		}
	}
	
	Dialog dialog = null;
	private void getBottomDialog() {
		dialog = Util.getBottomDialog(mActivity,
				R.layout.a_common_bottom_dialog_layout);
		ImageView img3 = null;
		Button button3 = null;
		ImageView img4 = null;
		Button button4 = null;
		ImageView img5 = null;
		Button button5 = null;

		img3 = (ImageView) dialog.findViewById(R.id.wenyi_bottomsheet_img_3);
		button3 = (Button) dialog.findViewById(R.id.wenyi_bottomsheet_btn_3);
		img3.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);
		button3.setText("发提问");
		img4 = (ImageView) dialog.findViewById(R.id.wenyi_bottomsheet_img_4);
		button4 = (Button) dialog.findViewById(R.id.wenyi_bottomsheet_btn_4);
		img4.setVisibility(View.VISIBLE);
		button4.setVisibility(View.VISIBLE);
		button4.setText("晒美食");
		img5 = (ImageView) dialog.findViewById(R.id.wenyi_bottomsheet_img_5);
		button5 = (Button) dialog.findViewById(R.id.wenyi_bottomsheet_btn_5);
		img5.setVisibility(View.VISIBLE);
		button5.setVisibility(View.VISIBLE);
		button5.setText("取消");

		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity, CreatePersonalInfoActivity.class);
				intent.putExtra("ententId", "");
				intent.putExtra("flag", HttpStatus.PUBLIC_FOR_TOPIC);
				startActivity(intent);
				dialog.dismiss();
			}
		});

		button4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity, CreatePersonalInfoActivity.class);
				intent.putExtra("ententId", "");
				intent.putExtra("flag", HttpStatus.PUBLIC_FOR_COOKBOOK);
				startActivity(intent);
				dialog.dismiss();
			}
		});

		button5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.show();
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
