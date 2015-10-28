package com.shecook.wenyi.group;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.shared.widget.BaseSwipeHelper;
import com.letv.shared.widget.LeListView;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.shecook.wenyi.BaseActivity;
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
import com.shecook.wenyi.group.adapter.GroupMemAuditListAdapter;
import com.shecook.wenyi.group.adapter.GroupMemAuditListAdapter.OnSwipeOperator;
import com.shecook.wenyi.model.group.GroupListItemSharedModel;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupItemDetailMemAuditActivity extends BaseActivity implements OnClickListener, OnSwipeOperator{
	private static final String TAG = "GroupItemDetailSharedFragment";

	public static final int STATUS_OK_COLLECTION_GROUP = 1;
	public static final int STATUS_OK_COLLECTION_CREATE = 2;
	public static final int STATUS_OK_AUDITING_NOT = 3;
	private LinkedList<GroupListItemSharedModel> groupMemList;
	private SwitchPullToRefreshListView mPullRefreshListView;
	private GroupMemAuditListAdapter mAdapter;

	private String circleid = "";
	private boolean shouldLoad = true;
	private int pindex = 0;

	private LinearLayout common_tip_info_layout;
	private TextView common_tip_info, common_tip_info_button;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		circleid = getIntent().getStringExtra("circleid");
		groupMemList = new LinkedList<GroupListItemSharedModel>();
		setContentView(R.layout.common_fragment_switchrefreshlist_hasheader);
		initView();
		processParam();
	}
	
	public void initView() {
		common_tip_info_layout = (LinearLayout) findViewById(R.id.common_tip_info_layout);
		common_tip_info = (TextView) findViewById(R.id.common_tip_info);
		common_tip_info_button = (TextView) findViewById(R.id.common_tip_info_button);
		
		common_tip_info_button.setOnClickListener(this);
		
		mPullRefreshListView = (SwitchPullToRefreshListView) findViewById(R.id.pull_refresh_list);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<LeListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<LeListView> refreshView) {
						String label = DateUtils.formatDateTime(
								GroupItemDetailMemAuditActivity.this.getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						processParam();
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						processParam();
					}
				});

		mAdapter = new GroupMemAuditListAdapter(GroupItemDetailMemAuditActivity.this, groupMemList);
		mAdapter.setOnSwipeOperator(this);
		
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		mPullRefreshListView.getRefreshableView().setSwipeActionLeft(BaseSwipeHelper.SWIPE_ACTION_REVEAL);
		mPullRefreshListView.getRefreshableView().setSwipeActionRight(BaseSwipeHelper.SWIPE_ACTION_DISMISS);
		mPullRefreshListView.getRefreshableView().setDismissAnimationTime(500);
		mPullRefreshListView.setAdapter(mAdapter);
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
	public void onDestroy() {
		WenyiLog.logv(TAG, "onDestroy");
		super.onDestroy();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				if(groupMemList == null || groupMemList.size() == 0){
					common_tip_info_layout.setVisibility(View.VISIBLE);
					common_tip_info.setText("该圈子目前还没有人加入");
					common_tip_info_button.setText("立刻邀请好友");
				}else{
					common_tip_info_layout.setVisibility(View.GONE);
				}
				break;
			case STATUS_OK_AUDITING_NOT:
				processParam2();
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

	int position = -1;
	@Override
	public void onDeleteItem(int position) {
		Log.e("lixufeng", "onDeleteItem position " + position + ", groupMemList " + groupMemList.size());
		this.position = position;
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("uid", groupMemList.get(position).getUid());
			paramObject.put("circleid", groupMemList.get(position).getCircleid());
			paramObject.put("atype", 0);
			getGroupSharedInfo(
					HttpUrls.GROUP_ITEM_MEM_AUDITING, paramObject, delectResultListener,
					delectErrorListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onJoinGroup(int position) {
		Log.e("lixufeng", "onJoinGroup position " + position + ", groupMemList " + groupMemList.size());
		this.position = position;
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("uid", groupMemList.get(position).getUid());
			paramObject.put("circleid", groupMemList.get(position).getCircleid());
			paramObject.put("atype", 1);
			getGroupSharedInfo(
					HttpUrls.GROUP_ITEM_MEM_AUDITING, paramObject, allowResultListener,
					delectErrorListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	Listener<JSONObject> allowResultListener = new Listener<JSONObject>() {

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
								msg = "" + "添加成员成功！";
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
			Toast.makeText(GroupItemDetailMemAuditActivity.this, msg,
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(STATUS_OK_AUDITING_NOT);
		}
	};
	
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
								msg = "" + "删除申请成功！";
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
			Toast.makeText(GroupItemDetailMemAuditActivity.this, msg,
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(STATUS_OK_AUDITING_NOT);
		}
	};
	
	ErrorListener delectErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	public void processParam(){
		if (shouldLoad) {
			JSONObject paramObject = new JSONObject();
			try {
				paramObject.put("circleid", circleid);
				paramObject.put("pindex", "" + ++pindex);
				getGroupSharedInfo(
						HttpUrls.GROUP_ITEM_MEM_AUDIT_list,
						paramObject, listResultListener,
						listErrorListener);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Toast.makeText(GroupItemDetailMemAuditActivity.this, "End of List!",
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(HttpStatus.STATUS_OK);
		}
	
	}
	public void processParam2(){
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("circleid", circleid);
			paramObject.put("pindex", "1");
			getGroupSharedInfo(
					HttpUrls.GROUP_ITEM_MEM_AUDIT_list,
					paramObject, listResultListener,
					listErrorListener);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getGroupSharedInfo(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util
				.getCommonParam(GroupItemDetailMemAuditActivity.this);
		try {
			/**
			 * mid :5A1469CD-4819-4863-A934-8871CA1A0281
             * token:8ac7e6fada244eaa8087d2230d002a17
			 */
			// lixufeng text
			commonsub.put("mid", "5A1469CD-4819-4863-A934-8871CA1A0281");
			commonsub.put("token", "591f3c51eca2483b932ed1a64b896a63");
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
						if(data.has("pindex")){
							pindex = data.getInt("pindex");
							int core_status = data.getInt("core_status");
							if (core_status == 200 && pindex == 0) {
								shouldLoad = false;
							}
						}
					}
				} else {
					Toast.makeText(GroupItemDetailMemAuditActivity.this,
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
