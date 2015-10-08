package com.shecook.wenyi.personal;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.shared.widget.LeListView;
import com.shecook.wenyi.BaseActivity;
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
import com.shecook.wenyi.model.personal.PersonalTopicModel;
import com.shecook.wenyi.personal.adapter.PersonalTopicListAdapter;
import com.shecook.wenyi.personal.adapter.PersonalTopicListAdapter.OnSwipeOperator;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalTopicListActivity extends BaseActivity implements
		OnClickListener, OnSwipeOperator {

	static final String TAG = "PersonalTopicListActivity";
	public static final int STATUS_OK_TOPIC_GROUP = 1;
	public static final int STATUS_OK_TOPIC_CREATE = 2;
	public static final int STATUS_OK_TOPIC_COLLECTED = 3;
	private LinkedList<PersonalTopicModel> collectionList;
	private PullToRefreshListView mPullRefreshListView;
	private PersonalTopicListAdapter mAdapter;

	private String groupid = "";
	private boolean shouldLoad = true;
	private int pindex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_pull_to_refresh_listview_divider);
		initView();
		processParam();
	}

	private void initView() {

		groupid = getIntent().getStringExtra("groupid");
		collectionList = new LinkedList<PersonalTopicModel>();

		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(this);
		ImageView settingImage = (ImageView) findViewById(R.id.right_img);
		settingImage.setOnClickListener(this);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		titleView.setText("我的收藏");

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								PersonalTopicListActivity.this.getApplicationContext(),
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

		mAdapter = new PersonalTopicListAdapter(this, collectionList);
		mAdapter.setOnSwipeOperator(this);
		
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			case STATUS_OK_TOPIC_COLLECTED:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			case STATUS_OK_TOPIC_CREATE:
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
		case R.id.right_img:
			
			break;

		case R.id.return_img:
			finish();
		default:
			break;
		}
	}

	@Override
	public void onChangeGroup(int position) {
		
	}

	@Override
	public void onDeleteItem(int position) {
		
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
								handler.sendEmptyMessage(STATUS_OK_TOPIC_COLLECTED);
								msg = "" + "删除收藏成功！";
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
			Toast.makeText(PersonalTopicListActivity.this, msg,
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
				paramObject.put("groupid", groupid);
				paramObject.put("pindex", "" + ++pindex);
				getPersonalCollectionInfo(
						HttpUrls.PERSONAL_TOPIC_LIST,
						paramObject, listResultListener,
						listErrorListener);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Toast.makeText(PersonalTopicListActivity.this, "End of List!",
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(HttpStatus.STATUS_OK);
		}
	}
	
	public void getPersonalCollectionInfo(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util
				.getCommonParam(PersonalTopicListActivity.this);
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
								eli.set_id(jb.getString("_id"));
								eli.setUid(jb.getString("uid"));
								eli.setClickto(jb.getString("clickto"));
								eli.setMainid(jb.getString("mainid"));
								eli.setCommentid(jb.getString("commentid"));
								eli.setTop_nickname(jb.getString("top_nickname"));
								eli.setTop_imageurl(jb.getString("top_imageurl"));
								eli.setTop_desc(jb.getString("top_desc"));
								eli.setBottom_nickname(jb.getString("bottom_nickname"));
								eli.setBottom_imageurl(jb.getString("bottom_imageurl"));
								eli.setBottom_desc(jb.getString("bottom_desc"));
								listTemp.add(eli);
							}
							collectionList.addAll(listTemp);
						}
						
						pindex = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 200 && pindex == 0){
							shouldLoad = false;
						}
					}
				}else{
					Toast.makeText(PersonalTopicListActivity.this, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
				handler.sendEmptyMessage(HttpStatus.STATUS_OK);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
