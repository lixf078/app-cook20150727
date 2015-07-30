package com.shecook.wenyi.essay;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.model.essay.EssayCatlog;
import com.shecook.wenyi.model.essay.EssayGallery;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.view.FixedSpeedScroller;
import com.shecook.wenyi.view.PageIndicator;

@SuppressLint("ShowToast")
public class WelcomeFragment extends BaseFragmeng {
	private static final String LOGTAG = "WelcomeFragment";

	private AutoScrollViewPager viewPager;
	private PageIndicator mIndicator;
	private ArrayList<EssayGallery> mPageViews;
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
		// getCatalog_(HttpUrls.ESSAY_WENYI_LIST, null, catalogResultListener, catalogErrorListener);
		mPageViews = new ArrayList<EssayGallery>();
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

	private void initView(View rootView){
		right_img = (ImageView) rootView.findViewById(R.id.right_img);
		return_img = (ImageView) rootView.findViewById(R.id.return_img);
		middle_title = (TextView) rootView.findViewById(R.id.middle_title);

		right_img.setVisibility(View.GONE);
		return_img.setVisibility(View.GONE);
		middle_title.setText(R.string.essay);
		
		viewPager = (AutoScrollViewPager) rootView
				.findViewById(R.id.view_pager_advert);
		mIndicator = (CirclePageIndicator) rootView
				.findViewById(R.id.indicator);

		adapter = new ViewPagerAdapter(this.getActivity()
				.getApplicationContext(), mPageViews);
		viewPager.setAdapter(adapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setInterval(4000);
		viewPager.setCurrentItem(0);
		viewPager.setStopScrollWhenTouch(true);
		setViewPagerScrollSpeed(viewPager);

		NetworkTextView textView = (NetworkTextView) rootView.findViewById(R.id.essay_question_answer);
		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    textView.setDefaultImageResId(R.drawable.icon_dialog);
	    textView.setErrorImageResId(R.drawable.icon_dialog);
	    
	    textView.setImageUrl("http://static.wenyijcc.com/notes/cata/d6766f3f5777451dbb4ae703911c8030.jpg", imageLoader);
	    textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				WelcomeFragment.this.getActivity().startActivity(new Intent(WelcomeFragment.this.getActivity(),EssayListActivity.class));
			}
		});
	    
	}
	
	private void initData(){
		WenyiLog.logv(LOGTAG, "initData 1111");
		if(jsonObject != null){
			try {
				WenyiLog.logv(LOGTAG, "initData 22222");
				int status_code = jsonObject.isNull("statuscode") ? -1 : jsonObject.getInt("statuscode");
				if(200 == status_code){
					WenyiLog.logv(LOGTAG, "initData 33333");
					if(!jsonObject.isNull("data")){
						WenyiLog.logv(LOGTAG, "initData 44444");
						JSONObject data = jsonObject.getJSONObject("data");
						JSONArray gallery = data.getJSONArray("gallery");
						for(int i = 0,j = gallery.length(); i < j; i++){
							JSONObject jb = gallery.getJSONObject(i);
							WenyiLog.logv(LOGTAG, "initData 5555 jb " + jb.toString());
							EssayGallery eg = new EssayGallery();
							eg.setId(jb.getInt("id"));
							eg.setTitle(jb.getString("title"));
							eg.setImgUrl(jb.getString("imgurl"));
							eg.setEvent_type(jb.getInt("event_type"));
							eg.setEvent_content(jb.getString("event_content"));
							eg.setTimeline(jb.getString("timeline"));
							mPageViews.add(eg);
						}
						
						JSONArray catalog = data.getJSONArray("catalog");
						for(int i = 0,j = gallery.length(); i < j; i++){
							JSONObject catalogjb = catalog.getJSONObject(i);
							WenyiLog.logv(LOGTAG, "initData 5555aaaaa jb " + catalogjb.toString());
							EssayCatlog ec = new EssayCatlog();
							ec.setId(catalogjb.getInt("id"));
							ec.setTitle(catalogjb.getString("title"));
							ec.setIconurl(catalogjb.getString("iconurl"));
							ec.setSort(catalogjb.getString("sort"));
							ec.setIspub(catalogjb.getBoolean("ispub"));
							ec.setCount(catalogjb.getInt("count"));
							ec.setEvent_type(catalogjb.getInt("event_type"));
							ec.setEvent_content(catalogjb.getString("event_content"));
							ec.setTimeline(catalogjb.getString("timeline"));
							catalogList.add(ec);
						}
					}
				}else{
					Toast.makeText(getActivity(), "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT);
					if(status_code == 10005){
						
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
			Log.d(TAG, "catalogResultListener onResponse -> " + result.toString());
		}
	};
	
	ErrorListener catalogErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	String mid = "";
	public void getCatalog_(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		WenyiUser user = Util.getUserData(getActivity());
		JSONObject sub = new JSONObject();
		if (TextUtils.isEmpty(user.get_mID())) {
			mid = UUID.randomUUID().toString();
		}else{
			mid = user.get_mID();
		}
		try {
			sub.put("mtype", "android");
			sub.put("mid", mid);
			sub.put("token", user.get_token());
			if(null == jsonObject){
				jsonObject = new JSONObject();
			}
			jsonObject.put("common", sub);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JsonObjectRequest wenyiRequest = new JsonObjectRequest(
				Method.POST, url, jsonObject, resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}
}
