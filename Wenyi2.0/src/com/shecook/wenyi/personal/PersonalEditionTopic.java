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
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.letv.shared.widget.BaseSwipeHelper;
import com.letv.shared.widget.LeListView;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.pulltorefresh.CopyOfRefreshAndMoreListView;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.essay.EssayItemDeatilActivity;
import com.shecook.wenyi.model.EssayListItem;
import com.shecook.wenyi.personal.adapter.EssayListAdapter;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalEditionTopic extends Fragment {
	private static final String TAG = "PersonalEditionTopic";
	
	private Activity mActivity = null;
	private CopyOfRefreshAndMoreListView mPullRefreshListView;
	com.shecook.wenyi.personal.adapter.EssayListAdapter mAdapter = null;
	LinkedList<EssayListItem> mListItems;
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
	}

	public void initView(View rootView){
		mPullRefreshListView = (CopyOfRefreshAndMoreListView) rootView.findViewById(R.id.pull_refresh_list);
		
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<LeListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<LeListView> refreshView) {
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
//						getCatalogList(HttpUrls.ESSAY_WENYI_LIST,null,listResultListener,listErrorListener);
						if(shouldLoad){
							getCatalogList(HttpUrls.ESSAY_WENYI_LIST, null, listResultListener, listErrorListener);
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
							getCatalogList(HttpUrls.ESSAY_WENYI_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(mActivity, "End of List!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

//		ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<EssayListItem>();
		mAdapter = new EssayListAdapter(mActivity, mListItems);

		/**
		 * Add Sound Event Listener
		 */
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
//		mPullRefreshListView.getRefreshableView().setSwipeMode(BaseSwipeHelper.SWIPE_MODE_LEFT);
		
//		mPullRefreshListView.getRefreshableView().setSwipeMode(BaseSwipeHelper.SWIPE_MODE_LEFT);
//		mPullRefreshListView.getRefreshableView().setSwipeActionLeft(BaseSwipeHelper.SWIPE_ACTION_REVEAL);
//		mPullRefreshListView.getRefreshableView().setOffsetLeft(1000);
		
//		mPullRefreshListView.setSwipeMode(BaseSwipeHelper.SWIPE_MODE_LEFT);
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.getRefreshableView().setSwipeEnabled(true);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Intent intent = new Intent(mActivity,EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "");
				startActivity(intent);
			}
		});
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.personal_edition_topiclist, container, false);
		initView(rootView);
		if(shouldLoad){
			getCatalogList(HttpUrls.ESSAY_WENYI_LIST, null, listResultListener, listErrorListener);
		}else{
			Toast.makeText(mActivity, "End of List!", Toast.LENGTH_SHORT).show();
		}
		return rootView;
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
	public void getCatalogList(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if(null == jsonObject){
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(mActivity);
		JSONObject paramsub = new JSONObject();
		try {
			jsonObject.put("common", commonsub);
			String catalogid = mActivity.getIntent().getStringExtra("catalogid");
			if(TextUtils.isEmpty(catalogid)){
				catalogid = "14";
			}
			paramsub.put("catalogid", catalogid);
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
		WenyiLog.logv(TAG, "initData 1111");
		if(jsonObject != null){
			try {
				WenyiLog.logv(TAG, "initData 22222");
				if(!jsonObject.isNull("statuscode") && 200 == jsonObject.getInt("statuscode")){
					WenyiLog.logv(TAG, "initData 33333");
					if(!jsonObject.isNull("data")){
						JSONObject data = jsonObject.getJSONObject("data");
						
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							WenyiLog.logv(TAG, "initData 44444 length " + list.length());
							LinkedList<EssayListItem> listTemp = new LinkedList<EssayListItem>();
							for(int i = 0,j = list.length(); i < j; i++){
								JSONObject jb = list.getJSONObject(i);
								WenyiLog.logv(TAG, "initData 5555 jb " + jb.toString());
								EssayListItem eli = new EssayListItem();
								eli.setId(jb.getString("id"));
								eli.setCataid(jb.getString("cataid"));
								eli.setTitle(jb.getString("title"));
								eli.setSumm(jb.getString("summ"));
								eli.setIconurl(jb.getString("iconurl"));
								eli.setOntop(jb.getBoolean("ontop"));
								eli.setEvent_type(jb.getString("event_type"));
								eli.setEvent_content(jb.getString("event_content"));
								eli.setQkey(jb.getString("qkey"));
								eli.setTimeline(jb.getString("timeline"));
								listTemp.add(eli);
							}
							mListItems.addAll(listTemp);
						}
						
						if(data.has("toplist")){
							JSONArray toplist = data.getJSONArray("toplist");
							LinkedList<EssayListItem> toplistTemp = new LinkedList<EssayListItem>();
							for(int i = 0,j = toplist.length(); i < j; i++){
								JSONObject topjb = toplist.getJSONObject(i);
								WenyiLog.logv(TAG, "initData toplist topjb " + topjb.toString());
								EssayListItem topeli = new EssayListItem();
								topeli.setId(topjb.getString("id"));
								topeli.setCataid(topjb.getString("cataid"));
								topeli.setTitle(topjb.getString("title"));
								topeli.setSumm(topjb.getString("summ"));
								topeli.setIconurl(topjb.getString("iconurl"));
								topeli.setOntop(topjb.getBoolean("ontop"));
								topeli.setEvent_type(topjb.getString("event_type"));
								topeli.setEvent_content(topjb.getString("event_content"));
								topeli.setQkey(topjb.getString("qkey"));
								topeli.setTimeline(topjb.getString("timeline"));
								toplistTemp.add(topeli);
							}
							mListItems.addAll(0, toplistTemp);
						}
						
						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 200 && index == 0){
							Log.e(TAG, "has not some item");
							shouldLoad = false;
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				}else{
					Toast.makeText(mActivity, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
