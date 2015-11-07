package com.shecook.wenyi.personal;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.shecook.wenyi.cookbook.CookbookCollectionActivity;
import com.shecook.wenyi.model.personal.PersonalMessageModel;
import com.shecook.wenyi.personal.adapter.PersonalMessageListAdapter;
import com.shecook.wenyi.personal.adapter.PersonalMessageListAdapter.OnSwipeOperator;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalMessageListActivity extends BaseActivity implements
		OnClickListener, OnSwipeOperator {

	static final String TAG = "CookbookCollectionActivity";
	public static final int STATUS_OK_COLLECTION_GROUP = 1;
	public static final int STATUS_OK_COLLECTION_CREATE = 2;
	public static final int STATUS_OK_COLLECTION_COLLECTED = 3;
	private LinkedList<PersonalMessageModel> messageList;
	private SwitchPullToRefreshListView mPullRefreshListView;
	private PersonalMessageListAdapter mAdapter;

	private boolean shouldLoad = true;
	private int pindex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_fragment_switchrefreshlist_hasheader_oneopt);
		initView();
		processParam();
	}

	private void initView() {

		messageList = new LinkedList<PersonalMessageModel>();

		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(this);
		ImageView settingImage = (ImageView) findViewById(R.id.right_img);
		settingImage.setOnClickListener(this);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		titleView.setText("我的消息");

		mPullRefreshListView = (SwitchPullToRefreshListView) findViewById(R.id.pull_refresh_list);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<LeListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<LeListView> refreshView) {
						String label = DateUtils.formatDateTime(
								PersonalMessageListActivity.this.getApplicationContext(),
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

		mAdapter = new PersonalMessageListAdapter(this, messageList);
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
				Util.dispatchClickEvent(PersonalMessageListActivity.this, messageList.get((int) position).getClickto(), messageList.get((int) position).getMainid(), new String[]{messageList.get((int) position).getCommentid()});
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
			case STATUS_OK_COLLECTION_COLLECTED:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			case STATUS_OK_COLLECTION_CREATE:
				break;
			default:
				break;
			}
		};
	};

	EditText et_search;

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
		Intent intent = new Intent(PersonalMessageListActivity.this, CookbookCollectionActivity.class);
		intent.putExtra("objid", messageList.get(position).get_id());
		intent.putExtra("event", "change");
		startActivity(intent);
	}

	int position = -1;
	@Override
	public void onDeleteItem(int position) {
		position = position;
		Log.e("lixufeng", "onDeleteItem position " + position + ", messageList " + messageList.size());
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("objid", messageList.get(position).get_id());
			operatorItem(
					HttpUrls.PERSONAL_MESSAGE_LIST_ITEM_DEL, paramObject, delectResultListener,
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
								messageList.remove(position);
								msg = "" + "删除消息成功！";
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
			Toast.makeText(PersonalMessageListActivity.this, msg,
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(STATUS_OK_COLLECTION_COLLECTED);
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
				paramObject.put("pindex", "" + ++pindex);
				operatorItem(
						HttpUrls.PERSONAL_MESSAGE_LIST,
						paramObject, listResultListener,
						listErrorListener);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Toast.makeText(PersonalMessageListActivity.this, "End of List!",
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(HttpStatus.STATUS_OK);
		}
	
	}
	
	public void operatorItem(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util
				.getCommonParam(PersonalMessageListActivity.this);
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
							LinkedList<PersonalMessageModel> listTemp = new LinkedList<PersonalMessageModel>();
							for(int i = 0,j = list.length(); i < j; i++){
								JSONObject jb = list.getJSONObject(i);
								PersonalMessageModel eli = new PersonalMessageModel();
								eli.set_id(jb.getString("_id"));
								eli.setUid(jb.getString("uid"));
								eli.setClickto(jb.getString("clickto"));
								eli.setMainid(jb.getString("mainid"));
								eli.setCommentid(jb.getString("commentid"));
								eli.setFrom_nickname(jb.getString("from_nickname"));
								eli.setFrom_imageurl(jb.getString("from_imageurl"));
								eli.setFrom_desc(jb.getString("from_desc"));
								listTemp.add(eli);
							}
							messageList.addAll(listTemp);
						}
						
						pindex = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 200 && pindex == 0){
							shouldLoad = false;
						}
					}
				}else{
					Toast.makeText(PersonalMessageListActivity.this, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendEmptyMessage(HttpStatus.STATUS_OK);
	}

}
