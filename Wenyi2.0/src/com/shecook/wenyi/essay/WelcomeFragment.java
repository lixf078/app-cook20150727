package com.shecook.wenyi.essay;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shecook.wenyi.BaseFragmeng;
import com.shecook.wenyi.R;
import com.shecook.wenyi.StartActivity;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.common.volley.toolbox.NetworkTextView;
import com.shecook.wenyi.essay.adapter.ViewPagerAdapter;
import com.shecook.wenyi.essay.view.AutoScrollViewPager;
import com.shecook.wenyi.essay.view.CirclePageIndicator;
import com.shecook.wenyi.model.WenyiGallery;
import com.shecook.wenyi.model.essay.EssayCatlog;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.view.FixedSpeedScroller;
import com.shecook.wenyi.view.PageIndicator;

@SuppressLint("ShowToast")
public class WelcomeFragment extends BaseFragmeng implements OnClickListener{
	private static final String LOGTAG = "WelcomeFragment";

	private AutoScrollViewPager viewPager;
	private PageIndicator mIndicator;
	private ArrayList<WenyiGallery> mPageViews;
	private ViewPagerAdapter adapter;

	private ImageView return_img, right_img;
	private TextView middle_title;

	private List<EssayCatlog> catalogList;
	private JSONObject jsonObject;

	@Override
	public void onAttach(Activity activity) {
		WenyiLog.logv(LOGTAG, "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(LOGTAG, "onCreate");
		super.onCreate(savedInstanceState);
		// getCatalog_(HttpUrls.ESSAY_WENYI_LIST, null, catalogResultListener,
		// catalogErrorListener);
		mPageViews = new ArrayList<WenyiGallery>();
		catalogList = new ArrayList<EssayCatlog>();
		jsonObject = StartActivity.getWelcomeData();
		initData();
		WenyiLog.logv(LOGTAG, "onCreate size " + mPageViews.size());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(LOGTAG, "onCreateView");

		View rootView = inflater.inflate(R.layout.activity_main, container,
				false);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		right_img = (ImageView) rootView.findViewById(R.id.right_img);
		return_img = (ImageView) rootView.findViewById(R.id.return_img);
		middle_title = (TextView) rootView.findViewById(R.id.middle_title);

		right_img.setVisibility(View.GONE);
		return_img.setVisibility(View.INVISIBLE);
		middle_title.setText(R.string.essay);

		viewPager = (AutoScrollViewPager) rootView
				.findViewById(R.id.view_pager_advert);
		mIndicator = (CirclePageIndicator) rootView
				.findViewById(R.id.indicator);

		
		/*WenyiGallery eg = new WenyiGallery();
		eg.setId(1000);
		eg.setTitle(getResources().getString(R.string.app_name));
		eg.setImgUrl("");
		eg.setEvent_type(10);
		eg.setEvent_content("www.baidu.com");
		mPageViews.add(eg);
		
		adapter = new ViewPagerAdapter(this.getActivity()
				.getApplicationContext(), mPageViews);
		viewPager.setAdapter(adapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setInterval(4000);
		viewPager.setCurrentItem(0);
		viewPager.setStopScrollWhenTouch(true);
		setViewPagerScrollSpeed(viewPager);*/

		NetworkTextView textView = (NetworkTextView) rootView
				.findViewById(R.id.essay_new_action);
		NetworkTextView textView2 = (NetworkTextView) rootView
				.findViewById(R.id.essay_everyday_chart);
		NetworkTextView textView3 = (NetworkTextView) rootView
				.findViewById(R.id.essay_life_way);
		NetworkTextView textView4 = (NetworkTextView) rootView
				.findViewById(R.id.essay_good_baby);
		NetworkTextView textView5 = (NetworkTextView) rootView
				.findViewById(R.id.essay_cook_knowledge);
		NetworkTextView textView6 = (NetworkTextView) rootView
				.findViewById(R.id.essay_question_answer);
		
		LruImageCache lruImageCache = LruImageCache.instance();
		Log.d(TAG,"size " + catalogList.size());
		if (null != catalogList && catalogList.size() > 0) {
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			textView.setDefaultImageResId(R.drawable.shicai_default);
//			textView.setErrorImageResId(R.drawable.shicai_default);

			textView.setImageUrl(catalogList.get(0).getIconurl(), imageLoader);
			textView.setText(catalogList.get(0).getTitle());
			textView.setOnClickListener(this);
		}

		if (null != catalogList && catalogList.size() > 1) {
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			textView2.setDefaultImageResId(R.drawable.shicai_default);
//			textView2.setErrorImageResId(R.drawable.shicai_default);
			
			textView2.setImageUrl(catalogList.get(1).getIconurl(), imageLoader);
			textView2.setText(catalogList.get(1).getTitle());
			textView2.setOnClickListener(this);
		}
		if (null != catalogList && catalogList.size() > 2) {
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			textView3.setDefaultImageResId(R.drawable.shicai_default);
//			textView3.setErrorImageResId(R.drawable.shicai_default);

			textView3.setImageUrl(catalogList.get(2).getIconurl(), imageLoader);
			textView3.setText(catalogList.get(2).getTitle());
			Log.d("lixufeng", "initView " + catalogList.get(2).getTitle());
			textView3.setOnClickListener(this);
		}
		if (null != catalogList && catalogList.size() > 3) {
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			textView4.setDefaultImageResId(R.drawable.shicai_default);
//			textView4.setErrorImageResId(R.drawable.shicai_default);
			
			textView4.setImageUrl(catalogList.get(3).getIconurl(), imageLoader);
			Log.d("lixufeng", "initView " + catalogList.get(3).getTitle());
			textView4.setText(catalogList.get(3).getTitle());
			textView4.setOnClickListener(this);
		}
		if (null != catalogList && catalogList.size() > 4) {
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			textView5.setDefaultImageResId(R.drawable.shicai_default);
//			textView5.setErrorImageResId(R.drawable.shicai_default);

			textView5.setImageUrl(catalogList.get(4).getIconurl(), imageLoader);
			textView5.setText(catalogList.get(4).getTitle());
			textView5.setOnClickListener(this);
		}

		if (null != catalogList && catalogList.size() > 5) {
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			textView6.setDefaultImageResId(R.drawable.shicai_default);
//			textView6.setErrorImageResId(R.drawable.shicai_default);

			textView6.setImageUrl(catalogList.get(5).getIconurl(), imageLoader);
			textView6.setText(catalogList.get(5).getTitle());
			textView6.setOnClickListener(this);
		}
		
		if(mPageViews != null && mPageViews.size() > 0){
			initViewPaper();
		}
	}

	private void initViewPaper(){
		adapter = new ViewPagerAdapter(this.getActivity()
				.getApplicationContext(), mPageViews);
		viewPager.setAdapter(adapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setInterval(4000);
		viewPager.setCurrentItem(0);
		viewPager.setStopScrollWhenTouch(true);
		setViewPagerScrollSpeed(viewPager);
	}
	
	private void initData() {
		if (jsonObject != null) {
			try {
				int status_code = jsonObject.isNull("statuscode") ? -1
						: jsonObject.getInt("statuscode");
				if (200 == status_code) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						JSONArray gallery = data.getJSONArray("gallery");
						if(gallery.length() > 0){
							mPageViews.clear();
						}
						for (int i = 0, j = gallery.length(); i < j; i++) {
							JSONObject jb = gallery.getJSONObject(i);
							WenyiGallery eg = new WenyiGallery();
							eg.setId(jb.getInt("id"));
							eg.setTitle(jb.getString("title"));
							eg.setImgUrl(jb.getString("imgurl"));
							eg.setEvent_type(jb.getInt("event_type"));
							eg.setEvent_content(jb.getString("event_content"));
							eg.setTimeline(jb.getString("timeline"));
							mPageViews.add(eg);
						}

						JSONArray catalog = data.getJSONArray("catalog");
						for (int i = 0, j = catalog.length(); i < j; i++) {
							JSONObject catalogjb = catalog.getJSONObject(i);
							EssayCatlog ec = new EssayCatlog();
							ec.setId(catalogjb.getInt("id"));
							ec.setTitle(catalogjb.getString("title"));
							ec.setIconurl(catalogjb.getString("iconurl"));
							ec.setSort(catalogjb.getString("sort"));
							ec.setIspub(catalogjb.getBoolean("ispub"));
							ec.setCount(catalogjb.getInt("count"));
							ec.setEvent_type(catalogjb.getInt("event_type"));
							ec.setEvent_content(catalogjb
									.getString("event_content"));
							ec.setTimeline(catalogjb.getString("timeline"));
							catalogList.add(ec);
						}
					}
				} else {
					Toast.makeText(getActivity(),
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT);
					if (status_code == 10005) {

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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
		super.onResume();
		viewPager.startAutoScroll();
	}

	@Override
	public void onPause() {
		super.onPause();
		viewPager.stopAutoScroll();
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

	Listener<JSONObject> resultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {

		}
	};

	ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	Listener<JSONObject> catalogResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
		}
	};

	ErrorListener catalogErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	String mid = "";

	public void getCatalog_(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject sub = Util.getCommonParam(getActivity());
		try {
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest wenyiRequest = new JsonObjectRequest(Method.POST,
				url, jsonObject, resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent(WelcomeFragment.this.getActivity(),
				EssayListActivity.class);
		int id = view.getId();
		switch (id) {
		case R.id.essay_new_action:
			intent.putExtra("catalogtitle", "" + catalogList.get(0).getTitle());
			intent.putExtra("catalogid", "" + catalogList.get(0).getId());
			WelcomeFragment.this.getActivity().startActivity(
					intent);
			break;
		case R.id.essay_everyday_chart:
			intent.putExtra("catalogtitle", "" + catalogList.get(1).getTitle());
			intent.putExtra("catalogid", "" + catalogList.get(1).getId());
			WelcomeFragment.this.getActivity().startActivity(
					intent);
			break;
		case R.id.essay_life_way:
			intent.putExtra("catalogtitle", "" + catalogList.get(2).getTitle());
			intent.putExtra("catalogid", "" + catalogList.get(2).getId());
			WelcomeFragment.this.getActivity().startActivity(
					intent);
			break;
		case R.id.essay_good_baby:
			intent.putExtra("catalogtitle", "" + catalogList.get(3).getTitle());
			intent.putExtra("catalogid", "" + catalogList.get(3).getId());
			WelcomeFragment.this.getActivity().startActivity(
					intent);
			break;
		case R.id.essay_cook_knowledge:
			intent.putExtra("catalogtitle", "" + catalogList.get(4).getTitle());
			intent.putExtra("catalogid", "" + catalogList.get(4).getId());
			WelcomeFragment.this.getActivity().startActivity(
					intent);
			break;
		case R.id.essay_question_answer:
			intent.putExtra("catalogtitle", "" + catalogList.get(5).getTitle());
			intent.putExtra("catalogid", "" + catalogList.get(5).getId());
			WelcomeFragment.this.getActivity().startActivity(
					intent);
			break;
			
		default:
			break;
		}
	}
}
