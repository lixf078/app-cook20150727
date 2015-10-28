package com.shecook.wenyi.personal;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.letv.shared.widget.BaseSwipeHelper;
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
import com.shecook.wenyi.model.WenyiImage;
import com.shecook.wenyi.model.personal.PersonalTopicModel;
import com.shecook.wenyi.personal.adapter.PersonalTopicListAdapter;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalEditionTopic extends Fragment {
	private static final String TAG = "PersonalEditionTopic";
	
	private Activity mActivity = null;
	private PullToRefreshListView mPullRefreshListView;
	com.shecook.wenyi.personal.adapter.PersonalTopicListAdapter mAdapter = null;
	LinkedList<PersonalTopicModel> mListItems;
	private boolean shouldLoad = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.personal_edition_topiclist, container, false);
		initView(rootView);
		if(shouldLoad){
			getMyTopicList(HttpUrls.PERSONAL_TOPIC_MY_LIST, null, listResultListener, listErrorListener);
		}else{
			Toast.makeText(mActivity, "End of List!", Toast.LENGTH_SHORT).show();
		}
		return rootView;
	}

	public void initView(View rootView){
		mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.pull_refresh_list);
		
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

						if(shouldLoad){
							getMyTopicList(HttpUrls.PERSONAL_TOPIC_MY_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(mActivity, "End of List!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if(shouldLoad){
							getMyTopicList(HttpUrls.PERSONAL_TOPIC_MY_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(mActivity, "End of List!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		mListItems = new LinkedList<PersonalTopicModel>();
		mAdapter = new PersonalTopicListAdapter(mActivity, mListItems);

		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
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
	

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};
	
	private int index = 0;
	public void getMyTopicList(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if(null == jsonObject){
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(mActivity);
		JSONObject paramsub = new JSONObject();
		try {
			paramsub.put("pindex", "" + ++index);
			paramsub.put("count", "20");
			
			jsonObject.put("param", paramsub);
			jsonObject.put("common", commonsub);
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
	
	Listener<JSONObject> listResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG, "catalogResultListener onResponse -> " + result.toString());
			initData(result, 0);
			
		}
	};
	
	ErrorListener listErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	
	private void initData(JSONObject jsonObject, int flag){
		if(jsonObject != null){
			try {
				if(!jsonObject.isNull("statuscode") && 200 == jsonObject.getInt("statuscode")){
					if(!jsonObject.isNull("data")){
						JSONObject data = jsonObject.getJSONObject("data");
						
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<PersonalTopicModel> listTemp = new LinkedList<PersonalTopicModel>();
							for(int i = 0,j = list.length(); i < j; i++){
								JSONObject jb = list.getJSONObject(i);
								PersonalTopicModel eli = new PersonalTopicModel();
								eli.setId(jb.getString("id"));
								eli.setUid(jb.getString("uid"));
								eli.setUgid(jb.getString("ugid"));
								eli.setNickname(jb.getString("nickname"));
								eli.setUportrait(jb.getString("uportrait"));
								eli.setBody(jb.getString("body"));
								eli.setTags(jb.getString("tags"));
								eli.setComments(jb.getString("comments"));
								eli.setTimeline(jb.getString("timeline"));
								if(jb.has("images")){
									JSONArray imagelist = jb.getJSONArray("images");
									LinkedList<WenyiImage> images = new LinkedList<WenyiImage>();
									for(int k = 0,t = imagelist.length();k<t;k++){
										try {
											JSONObject imageobj = imagelist.getJSONObject(k);
											WenyiImage img = new WenyiImage();
											img.setImageurl(imageobj.getString("url"));
										} catch (Exception e) {
											e.printStackTrace();
											images = null;
										}
									}
									if(images != null){
										eli.setImages(images);
									}
								}
							}
							mListItems.addAll(listTemp);
						}else{
							Toast.makeText(mActivity, "" + jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
						}
						
						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 200 && index == 0){
							Log.e(TAG, "has not some item");
							shouldLoad = false;
						}
					}
				}else{
					Toast.makeText(mActivity, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendEmptyMessage(HttpStatus.STATUS_OK);
	}
}
