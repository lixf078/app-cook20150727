package com.shecook.wenyi.group;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.shared.widget.BaseSwipeHelper;
import com.letv.shared.widget.LeListView;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.shecook.wenyi.BaseFragmeng;
import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.pulltorefresh.SwitchPullToRefreshListView;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.group.adapter.GroupMemListAdapter;
import com.shecook.wenyi.group.adapter.GroupMemListAdapter.OnSwipeOperator;
import com.shecook.wenyi.model.group.GroupListItemSharedModel;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupItemDetailMemFragment extends BaseFragmeng implements OnClickListener, OnSwipeOperator{
	private static final String TAG = "GroupItemDetailMemFragment";

	private Activity mActivity = null;
	
	public static final int STATUS_OK_COLLECTION_GROUP = 1;
	public static final int STATUS_OK_COLLECTION_CREATE = 2;
	public static final int STATUS_OK_GROUP_MEM_DELETE = 3;
	private LinkedList<GroupListItemSharedModel> groupMemList;
	private SwitchPullToRefreshListView mPullRefreshListView;
	private GroupMemListAdapter mAdapter;

	private String circleid = "";
	private boolean shouldLoad = true;
	private int pindex = 0;
	private int apply = 0;

	private LinearLayout common_tip_info_layout;
	private TextView common_tip_info, common_tip_info_button;
	
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.common_fragment_switchrefreshlist_noheader,
				container, false);
		circleid = mActivity.getIntent().getStringExtra("circleid");
		groupMemList = new LinkedList<GroupListItemSharedModel>();
		initView(rootView);
		processParam(false);
		return rootView;
	}
	
	public void initView(View rootView) {
		common_tip_info_layout = (LinearLayout) rootView.findViewById(R.id.common_tip_info_layout);
		common_tip_info = (TextView) rootView.findViewById(R.id.common_tip_info);
		common_tip_info_button = (TextView) rootView.findViewById(R.id.common_tip_info_button);
		
		common_tip_info_button.setOnClickListener(this);
		
		mPullRefreshListView = (SwitchPullToRefreshListView) rootView.findViewById(R.id.pull_refresh_list);

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
						processParam(false);
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						processParam(false);
					}
				});

		mAdapter = new GroupMemListAdapter(mActivity, groupMemList);
		mAdapter.setOnSwipeOperator(this);
		
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		mPullRefreshListView.getRefreshableView().setSwipeActionLeft(BaseSwipeHelper.SWIPE_ACTION_REVEAL);
		mPullRefreshListView.getRefreshableView().setSwipeActionRight(BaseSwipeHelper.SWIPE_ACTION_DISMISS);
		mPullRefreshListView.getRefreshableView().setDismissAnimationTime(500);
		
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
			}
		});
		if(((GroupItemDetailActivity)mActivity).getStatus() <= 10001){
			View header = mActivity.getLayoutInflater().inflate(R.layout.group_detail_mem_list_header, mPullRefreshListView, false);
			num = (TextView) header.findViewById(R.id.group_detail_item_new_num);
			num.setText("10");
			header.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, GroupItemDetailMemAuditActivity.class);
					intent.putExtra("circleid", "" + circleid);
					startActivity(intent);
				}
			});
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
			header.setLayoutParams(layoutParams);
			ListView lv = mPullRefreshListView.getRefreshableView();
			lv.addHeaderView(header);
		}else{
			mPullRefreshListView.getRefreshableView().setLeListViewMode(LeListView.LE_NONE);
		}
		
	}
	TextView num = null;
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
		super.onResume();
		WenyiLog.logv(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		WenyiLog.logv(TAG, "onPause");
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
				mPullRefreshListView.onRefreshComplete();
				if(groupMemList == null || groupMemList.size() == 0){
					common_tip_info_layout.setVisibility(View.VISIBLE);
					common_tip_info.setText("该圈子目前还没有人加入");
					common_tip_info_button.setText("立刻邀请好友");
				}else{
					if(null != null){
						num.setText("" + apply);
					}
					common_tip_info_layout.setVisibility(View.GONE);
				}
				break;
			case STATUS_OK_GROUP_MEM_DELETE:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
//				processParam(true);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.common_tip_info_button:
			break;

		default:
			break;
		}
	}

	public int position = -1;
	@Override
	public void onDeleteItem(int position) {
		this.position = position;
		Log.e("lixufeng", "onDeleteItem position " + position + ", groupMemList " + groupMemList.size());
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("circleid", groupMemList.get(position).getCircleid());
			paramObject.put("uid", groupMemList.get(position).getUid());
			getGroupSharedInfo(
					HttpUrls.GROUP_MEM_KICK, paramObject, delectResultListener,
					delectErrorListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	Listener<JSONObject> delectResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			Log.d(TAG, "catalogResultListener onResponse -> " + jsonObject.toString());
			String msg = "";
			if (jsonObject != null) {
				try {
					if (!jsonObject.isNull("statuscode")
							&& 200 == jsonObject.getInt("statuscode")) {
						if (!jsonObject.isNull("data")) {
							JSONObject data = jsonObject.getJSONObject("data");
							int core_status = data.getInt("core_status");
							Log.e(TAG, "collectedResultListener core_status -> " + core_status);
							if (core_status == 200) {
								groupMemList.remove(position);
								msg = "" + "删除成员成功！";
							} else {
								msg = "" + data.getString("msg");
							}
						}
					} else {
						msg = "" + jsonObject.getString("errmsg");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Toast.makeText(mActivity, msg,
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(STATUS_OK_GROUP_MEM_DELETE);
		}
	};
	
	ErrorListener delectErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	public void processParam(boolean force){
		if(force){
			pindex = 0;
			shouldLoad = true;
		}
		if (shouldLoad) {
			JSONObject paramObject = new JSONObject();
			try {
				paramObject.put("circleid", circleid);
				paramObject.put("pindex", "" + ++pindex);
				getGroupSharedInfo(
						HttpUrls.GROUP_ITEM_MEM_LIST,
						paramObject, listResultListener,
						listErrorListener);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			Toast.makeText(mActivity, "您已翻到底儿了!",
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(HttpStatus.STATUS_OK);
		}
	
	}
	
	public void getGroupSharedInfo(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util
				.getCommonParam(mActivity);
		try {
			/**
			 * mid :5A1469CD-4819-4863-A934-8871CA1A0281
             * token:8ac7e6fada244eaa8087d2230d002a17
			 */
			// lixufeng text
//			commonsub.put("mid", "5A1469CD-4819-4863-A934-8871CA1A0281");
//			commonsub.put("token", "591f3c51eca2483b932ed1a64b896a63");
			if (paramsub != null) {
				jsonObject.put("param", paramsub);
			}
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
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<GroupListItemSharedModel> listTemp = new LinkedList<GroupListItemSharedModel>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								GroupListItemSharedModel pdi = new GroupListItemSharedModel();
								pdi.setId(jb.getString("id"));
								pdi.setCircleid(jb.getString("circleid"));
								pdi.setUid(jb.getString("uid"));
								pdi.setNickname(jb.getString("nickname"));
								pdi.setUportrait(jb.getString("uportrait"));
								pdi.setStatus(jb.getInt("status"));
								listTemp.add(pdi);
							}
							groupMemList.addAll(listTemp);
						}
						if(jsonObject.has("apply")){
							apply = jsonObject.getInt("apply");
						}else{
							apply = 0;
						}
						if(data.has("pindex")){
							pindex = data.getInt("pindex");
							int core_status = data.getInt("core_status");
							if (core_status == 200 && pindex == 0) {
								shouldLoad = false;
							}
						}
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
		handler.sendEmptyMessage(HttpStatus.STATUS_OK);
	}

	ErrorListener groupHotErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	Listener<JSONObject> groupHotResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			initData(result, 0);
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
		}
	};
}
