package com.shecook.wenyi.piazza;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.shecook.wenyi.BaseFragmeng;
import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase.Mode;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshListView;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.essay.EssayItemDeatilActivity;
import com.shecook.wenyi.essay.adapter.ViewPagerAdapter;
import com.shecook.wenyi.essay.view.AutoScrollViewPager;
import com.shecook.wenyi.essay.view.CirclePageIndicator;
import com.shecook.wenyi.model.WenyiGallery;
import com.shecook.wenyi.model.WenyiImage;
import com.shecook.wenyi.model.piazza.PiazzaDiscoverItem;
import com.shecook.wenyi.piazza.adapter.PiazzaDiscoverListAdapter;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.view.FixedSpeedScroller;
import com.shecook.wenyi.view.PageIndicator;

public class PiazzaDiscoverFragment extends BaseFragmeng {
	private static final String TAG = "PiazzaFragment";

	private Activity mActivity = null;
	
	private AutoScrollViewPager viewPager;
	private PageIndicator mIndicator;
	private ArrayList<WenyiGallery> mPageViews;
	private ViewPagerAdapter adapter;
	
	private PullToRefreshListView mPullRefreshListView;
	PiazzaDiscoverListAdapter mAdapter = null;
	LinkedList<PiazzaDiscoverItem> mListItems;
	private boolean shouldLoad = true;

	@Override
	public void onAttach(Activity activity) {
		WenyiLog.logv(TAG, "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mPageViews = new ArrayList<WenyiGallery>();
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.piazza_discover_fragment,
				container, false);
		initView(rootView);
		getDiscoverList(HttpUrls.PIZZA_TOPIC_LIST, null, listResultListener,
				listErrorListener);
		return rootView;
	}
	
	public void initView(View rootView) {
		
		// view pager init start
/*		viewPager = (AutoScrollViewPager) rootView
				.findViewById(R.id.view_pager_advert);
		mIndicator = (CirclePageIndicator) rootView
				.findViewById(R.id.indicator);
		
		WenyiGallery eg = new WenyiGallery();
		eg.setId(1000);
		eg.setTitle(getResources().getString(R.string.app_name));
		eg.setImgUrl("");
		eg.setEvent_type(10);
		eg.setEvent_content("www.baidu.com");
		mPageViews.add(eg);
		
		adapter = new ViewPagerAdapter(mActivity, mPageViews);
		viewPager.setAdapter(adapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setInterval(4000);
		viewPager.setCurrentItem(0);
		viewPager.setStopScrollWhenTouch(true);
		setViewPagerScrollSpeed(viewPager);*/
		// view pager init end
		
		
		mPullRefreshListView = (PullToRefreshListView) rootView
				.findViewById(R.id.pull_refresh_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								mActivity.getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						// getCatalogList(HttpUrls.ESSAY_WENYI_LIST,null,listResultListener,listErrorListener);
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (shouldLoad) {
							getDiscoverList(HttpUrls.PIZZA_TOPIC_LIST, null,
									piazzaDiscoverResultListener,
									piazzaDiscoverErrorListener);
						} else {
							Toast.makeText(mActivity, "End of List!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		// ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<PiazzaDiscoverItem>();
		mAdapter = new PiazzaDiscoverListAdapter(mActivity, mListItems);

		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Intent intent = new Intent(mActivity,
						EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "");
				startActivity(intent);
			}
		});
		
		
		// ******************************************************************
		
		/*AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(layoutParams);
        ListView lv = mInviteRecordLv.getRefreshableView();
        View header = mActivity.getLayoutInflater().inflate(R.layout.invite_record_header, mInviteRecordLv, false);
        lv.addHeaderView(header);
		Button header = new Button(mActivity);
		header.setBackgroundColor(mActivity.getResources().getColor(R.color.blue));
        */
		View header = mActivity.getLayoutInflater().inflate(R.layout.piazza_discover_viewpager_fragment, mPullRefreshListView, false);
		
		viewPager = (AutoScrollViewPager) header
				.findViewById(R.id.view_pager_advert);
		mIndicator = (CirclePageIndicator) header
				.findViewById(R.id.indicator);
		
		WenyiGallery eg = new WenyiGallery();
		eg.setId(1000);
		eg.setTitle(getResources().getString(R.string.app_name));
		eg.setImgUrl("");
		eg.setEvent_type(10);
		eg.setEvent_content("www.baidu.com");
		mPageViews.add(eg);
		
		adapter = new ViewPagerAdapter(mActivity, mPageViews);
		viewPager.setAdapter(adapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setInterval(4000);
		viewPager.setCurrentItem(0);
		viewPager.setStopScrollWhenTouch(true);
		setViewPagerScrollSpeed(viewPager);
		
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(layoutParams);
        ListView lv = mPullRefreshListView.getRefreshableView();
        lv.addHeaderView(header);
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
	public void onPause() {
		super.onPause();
		WenyiLog.logv(TAG, "onPause");
		viewPager.stopAutoScroll();
	}

	@Override
	public void onResume() {
		super.onResume();
		WenyiLog.logv(TAG, "onResume");
		viewPager.startAutoScroll();
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

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				
				// test
				
				WenyiGallery eg = new WenyiGallery();
				eg.setId(1000);
				eg.setTitle(getResources().getString(R.string.app_name));
				eg.setImgUrl("");
				eg.setEvent_type(10);
				eg.setEvent_content("www.baidu.com");
				mPageViews.add(eg);
				mPageViews.add(eg);
				break;

			default:
				break;
			}
		};
	};

	private int index = 0;

	public void getDiscoverList(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(mActivity);
		JSONObject paramsub = new JSONObject();
		try {
			jsonObject.put("common", commonsub);
			paramsub.put("pindex", "" + ++index);
			paramsub.put("count", "20");

			jsonObject.put("param", paramsub);
			jsonObject.put("common", commonsub);
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

	Listener<JSONObject> listResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initData(result, 0);
		}
	};

	ErrorListener listErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	private void initData(JSONObject jsonObject, int flag) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");

						JSONArray list = data.getJSONArray("list");
						LinkedList<PiazzaDiscoverItem> listTemp = new LinkedList<PiazzaDiscoverItem>();
						for (int i = 0, j = list.length(); i < j; i++) {
							JSONObject jb = list.getJSONObject(i);
							PiazzaDiscoverItem pdi = new PiazzaDiscoverItem();
							pdi.setId(jb.getString("id"));
							pdi.setUid(jb.getString("uid"));
							pdi.setUgid(jb.getString("ugid"));
							pdi.setNickname(jb.getString("nickname"));
							pdi.setUportrait(jb.getString("uportrait"));
							pdi.setBody(jb.getString("body"));
							pdi.setTags(jb.getString("tags"));
							pdi.setComments(jb.getString("comments"));
							pdi.setTimeline(jb.getString("timeline"));
							JSONArray imagelist = jb.getJSONArray("images");
							LinkedList<WenyiImage> toplistTemp = new LinkedList<WenyiImage>();
							for (int k = 0, t = imagelist.length(); k < t; k++) {
								JSONObject imagejb = imagelist.getJSONObject(k);
								WenyiImage homeWorkImage = new WenyiImage();
								homeWorkImage.setId(imagejb.getString("id"));
								homeWorkImage.setFollowid(imagejb
										.getString("followid"));
								homeWorkImage.setImageurl(imagejb
										.getString("imageurl"));
								pdi.getImages().add(homeWorkImage);
							}
							listTemp.add(pdi);
						}
						mListItems.addAll(listTemp);

						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if (core_status == 0 && index == 0) {
							shouldLoad = false;
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				} else {
					Toast.makeText(mActivity,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	ErrorListener piazzaDiscoverErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	Listener<JSONObject> piazzaDiscoverResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			initData(result, 0);
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
		}
	};
	
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
}
