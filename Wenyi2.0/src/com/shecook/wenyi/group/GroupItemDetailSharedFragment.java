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
import com.shecook.wenyi.group.adapter.GroupSharedListAdapter;
import com.shecook.wenyi.group.adapter.GroupSharedListAdapter.OnSwipeOperator;
import com.shecook.wenyi.model.WenyiImage;
import com.shecook.wenyi.model.group.GroupListItemSharedModel;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupItemDetailSharedFragment extends BaseFragmeng implements OnClickListener, OnSwipeOperator{
	private static final String TAG = "GroupItemDetailSharedFragment";

	private Activity mActivity = null;
	
	public static final int STATUS_OK_COLLECTION_GROUP = 1;
	public static final int STATUS_OK_COLLECTION_CREATE = 2;
	public static final int STATUS_OK_COLLECTION_COLLECTED = 3;
	private LinkedList<GroupListItemSharedModel> groupSharedList;
	private SwitchPullToRefreshListView mPullRefreshListView;
	private GroupSharedListAdapter mAdapter;

	private String circleid = "";
	private boolean shouldLoad = true;
	private int pindex = 0;

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
		groupSharedList = new LinkedList<GroupListItemSharedModel>();
		initView(rootView);
		processParam();
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

		mAdapter = new GroupSharedListAdapter(mActivity, groupSharedList);
		mAdapter.setOnSwipeOperator(this);
		
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		mPullRefreshListView.getRefreshableView().setSwipeActionLeft(BaseSwipeHelper.SWIPE_ACTION_REVEAL);
		mPullRefreshListView.getRefreshableView().setSwipeActionRight(BaseSwipeHelper.SWIPE_ACTION_DISMISS);
		mPullRefreshListView.getRefreshableView().setDismissAnimationTime(500);
		
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Intent intent = new Intent(mActivity, GroupShareDeatilActivity.class);
				intent.putExtra("circleid", "" + groupSharedList.get((int) position).getCircleid());
				intent.putExtra("shareid", "" + groupSharedList.get((int) position).getId());
				startActivity(intent);
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
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				if(groupSharedList == null || groupSharedList.size() == 0){
					common_tip_info_layout.setVisibility(View.VISIBLE);
					common_tip_info.setText("该圈子目前还没有人分享");
					common_tip_info_button.setText("立刻分享");
				}else{
					common_tip_info_layout.setVisibility(View.GONE);
				}
				break;
			case STATUS_OK_COLLECTION_COLLECTED:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
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

	int operPosition = -1;
	@Override
	public void onDeleteItem(int position) {
		operPosition = position;
		Log.e("lixufeng", "onDeleteItem position " + position + ", groupSharedList " + groupSharedList.size());
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("shareid", groupSharedList.get(position).getId());
			getGroupSharedInfo(
					HttpUrls.GROUP_CIRCLE_SHARE_DEL, paramObject, delectResultListener,
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
								groupSharedList.remove(operPosition);
								handler.sendEmptyMessage(STATUS_OK_COLLECTION_COLLECTED);
								msg = "" + "删除分享成功！";
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
						HttpUrls.GROUP_ITEM_SHARED_LIST,
						paramObject, listResultListener,
						listErrorListener);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Toast.makeText(mActivity, "您已翻到底儿了!",
					Toast.LENGTH_SHORT).show();
		}
	
	}
	
	public void getGroupSharedInfo(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util
				.getCommonParam(mActivity);
		try {
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
								pdi.setBody(jb.getString("body"));
								pdi.setComments(jb.getString("comments"));
								pdi.setTimeline(jb.getString("timeline"));
								pdi.setDel(jb.getInt("del"));
								if(jb.has("images")){
									JSONArray images = jb.getJSONArray("images");
									for(int k = 0, t = images.length(); k < t; k++){
										JSONObject image = images.getJSONObject(k);
										WenyiImage im = new WenyiImage();
										im.setId(image.getString("id"));
										im.setImageurl(image.getString("imageurl"));
										// 还有其他字段未使用
										pdi.getImages().add(im);
									}
								}
								listTemp.add(pdi);
							}
							groupSharedList.addAll(listTemp);
						}else{
							Toast.makeText(mActivity,
									"" + jsonObject.getString("msg"),
									Toast.LENGTH_SHORT).show();
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
		Log.e("lixufeng", "sendEmptyMessage groupItemdetailsharedfragment");
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
