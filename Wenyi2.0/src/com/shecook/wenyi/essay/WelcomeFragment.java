package com.shecook.wenyi.essay;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shecook.wenyi.BaseFragmeng;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.essay.adapter.ViewPagerAdapter;
import com.shecook.wenyi.essay.view.AutoScrollViewPager;
import com.shecook.wenyi.essay.view.CirclePageIndicator;
import com.shecook.wenyi.model.MyAdvView;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.net.NetResult;
import com.shecook.wenyi.view.FixedSpeedScroller;
import com.shecook.wenyi.view.PageIndicator;

public class WelcomeFragment extends BaseFragmeng {
	private static final String LOGTAG = "WelcomeFragment";

	private AutoScrollViewPager viewPager;
	private PageIndicator mIndicator;
	private ArrayList<MyAdvView> mPageViews;
	private ViewPagerAdapter adapter;

	private ImageView return_img, right_img;
	private TextView middle_title;

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
		View rootView = inflater.inflate(R.layout.activity_main, container,
				false);
		right_img = (ImageView) rootView.findViewById(R.id.right_img);
		return_img = (ImageView) rootView.findViewById(R.id.return_img);
		middle_title = (TextView) rootView.findViewById(R.id.middle_title);

		right_img.setVisibility(View.GONE);
		return_img.setVisibility(View.GONE);
		middle_title.setText(R.string.essay);

		mPageViews = new ArrayList<MyAdvView>();
		for (int i = 0; i < 4; i++) {
			mPageViews.add(new MyAdvView("", "测试链接", ""));
		}
		viewPager = (AutoScrollViewPager) rootView
				.findViewById(R.id.view_pager_advert);
		mIndicator = (CirclePageIndicator) rootView
				.findViewById(R.id.indicator);

		adapter = new ViewPagerAdapter(this.getActivity()
				.getApplicationContext(), mPageViews);
		viewPager.setAdapter(adapter);
		;
		mIndicator.setViewPager(viewPager);

		viewPager.setInterval(4000);
		viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2
				% mPageViews.size());
		viewPager.setStopScrollWhenTouch(true);
		setViewPagerScrollSpeed(viewPager);

		return rootView;
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
	public void onPause() {
		super.onPause();
		viewPager.stopAutoScroll();
	}

	@Override
	public void onResume() {
		super.onResume();
		viewPager.startAutoScroll();
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

	private void setViewPagerScrollSpeed(AutoScrollViewPager mViewPager) {
		try {
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(
					mViewPager.getContext());
			mScroller.set(mViewPager, scroller);
		} catch (NoSuchFieldException e) {

		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
	}
	
	Listener<NetResult> resultListener = new Listener<NetResult>() {

		@Override
		public void onResponse(NetResult result) {
			
		}
	};

	ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
}
