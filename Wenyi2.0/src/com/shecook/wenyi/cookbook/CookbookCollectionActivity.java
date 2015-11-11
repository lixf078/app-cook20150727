package com.shecook.wenyi.cookbook;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase.Mode;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshListView;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.cookbook.adapter.CookbookCollectionGroupAdatper;
import com.shecook.wenyi.model.cookbook.CookbookCollectionGroup;
import com.shecook.wenyi.personal.PersonalCollectionListActivity;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CookbookCollectionActivity extends BaseActivity implements OnClickListener{

	static final String TAG = "CookbookCollectionActivity";
	public static final int STATUS_OK_COLLECTION_GROUP = 1;
	public static final int STATUS_OK_COLLECTION_CREATE = 2;
	public static final int STATUS_OK_COLLECTION_COLLECTED = 3;
	private LinkedList<CookbookCollectionGroup> groupList;
	private PullToRefreshListView mPullRefreshListView;
	private CookbookCollectionGroupAdatper mAdapter;

	private String recipeid = "";
	private String groupid = "";
	private String event = "add";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cookbook_collection_list);

		initView();
	}

	private void initView() {

		recipeid = getIntent().getStringExtra("recipeid");
		event = getIntent().getStringExtra("event");
		groupid = getIntent().getStringExtra("groupid");
		
		groupList = new LinkedList<CookbookCollectionGroup>();

		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(this);
		ImageView settingImage = (ImageView) findViewById(R.id.right_img);
		settingImage.setOnClickListener(this);
		settingImage.setBackgroundResource(R.drawable.add);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		titleView.setText("我的收藏");

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		mAdapter = new CookbookCollectionGroupAdatper(this, groupList);

		mPullRefreshListView.setMode(Mode.DISABLED);
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				if(TextUtils.isEmpty(recipeid)){
					Intent intent = new Intent(CookbookCollectionActivity.this,
							 PersonalCollectionListActivity.class);
					intent.putExtra("groupid", groupList.get((int) position).getId());
					startActivity(intent);
				}else{
					String selectedGroupid = groupList.get((int) position).getId();
					if(groupid.equals(selectedGroupid)){
						Toast.makeText(CookbookCollectionActivity.this, "您就在这个分组里", Toast.LENGTH_SHORT).show();
						return;
					}
					JSONObject paramObject = new JSONObject();
					try {
						paramObject.put("recipeid", recipeid);
						paramObject.put("groupid", selectedGroupid);
						if("change".equals(event)){
							getCookbookCollectionInfo(
									HttpUrls.PERSONAL_COLLECTION_CHANGE_COOKBOOK_GROUP,
									paramObject, collectedResultListener,
									collectedErrorListener);
						}else{
							getCookbookCollectionInfo(
									HttpUrls.COOKBOOK_COLLECTION_COLLECTED,
									paramObject, collectedResultListener,
									collectedErrorListener);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
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
		getCookbookCollectionInfo(HttpUrls.COOKBOOK_COLLECTION_GROUP_LIST, null,
				grouplistResultListener, grouplistErrorListener);
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
			case STATUS_OK_COLLECTION_GROUP:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			case STATUS_OK_COLLECTION_COLLECTED:
				finish();
				break;
			case STATUS_OK_COLLECTION_CREATE:
				getCookbookCollectionInfo(HttpUrls.COOKBOOK_COLLECTION_GROUP_LIST,
						null, grouplistResultListener, grouplistErrorListener);
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
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请输入分组名称");
			
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.add_collection_name_dialog, null);
			builder.setView(layout);
			et_search = (EditText)layout.findViewById(R.id.add_collection_name_content);
			builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String name = et_search.getEditableText().toString();
					if(TextUtils.isEmpty(name)){
						Toast.makeText(CookbookCollectionActivity.this, "", Toast.LENGTH_SHORT).show();
						return;
					}
					JSONObject paramObject = new JSONObject();
					try {
						paramObject.put("groupname", name);
						getCookbookCollectionInfo(
								HttpUrls.COOKBOOK_COLLECTION_GROUP_CREATE, paramObject, createCollectionResultListener,
								createCollectionErrorListener);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			builder.setNegativeButton("取消", null);
			builder.show();
			break;

		case R.id.return_img:
			finish();
		default:
			break;
		}
	}
	
	public void getCookbookCollectionInfo(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util
				.getCommonParam(CookbookCollectionActivity.this);
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

	Listener<JSONObject> grouplistResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.e(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initGroupData(result);
		}
	};

	ErrorListener grouplistErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	private void initGroupData(JSONObject jsonObject) {
		if (groupList != null) {
			groupList.clear();
		}
		String msg = "";
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						if (data.getInt("core_status") == 200) {
							if (data.has("group")) {
								JSONArray list = data.getJSONArray("group");
								LinkedList<CookbookCollectionGroup> listTemp = new LinkedList<CookbookCollectionGroup>();
								for (int i = 0, j = list.length(); i < j; i++) {
									JSONObject jb = list.getJSONObject(i);
									CookbookCollectionGroup ccg = new CookbookCollectionGroup();
									ccg.setId(jb.getString("id"));
									ccg.setUid(jb.getString("uid"));
									ccg.setGroupname(jb.getString("groupname"));
									ccg.setTimeline(jb.getString("timeline"));
									listTemp.add(ccg);
								}
								groupList.addAll(listTemp);
							}
							handler.sendEmptyMessage(STATUS_OK_COLLECTION_GROUP);
						}
					}
				} else {
					msg = jsonObject.getString("errmsg");
					Toast.makeText(CookbookCollectionActivity.this, "" + msg,
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	Listener<JSONObject> collectedResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			Log.e(TAG,
					"collectedResultListener onResponse -> "
							+ jsonObject.toString());
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
								handler.sendEmptyMessage(STATUS_OK_COLLECTION_COLLECTED);
								msg = "" + "收藏成功！";
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
			Toast.makeText(CookbookCollectionActivity.this, msg,
					Toast.LENGTH_SHORT).show();
		}

	};

	ErrorListener collectedErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	Listener<JSONObject> createCollectionResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject jsonObject) {
			Log.e(TAG,
					"collectedResultListener onResponse -> "
							+ jsonObject.toString());
			String msg = "";
			if (jsonObject != null) {
				try {
					if (!jsonObject.isNull("statuscode")
							&& 200 == jsonObject.getInt("statuscode")) {
						if (!jsonObject.isNull("data")) {
							JSONObject data = jsonObject.getJSONObject("data");
							int core_status = data.getInt("core_status");
							Log.e(TAG, "collectedResultListener core_status -> " + core_status);
							msg = "" + data.getString("msg");
							handler.sendEmptyMessage(STATUS_OK_COLLECTION_CREATE);
						}
					} else {
						msg = "" + jsonObject.getString("errmsg");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Toast.makeText(CookbookCollectionActivity.this, msg,
					Toast.LENGTH_SHORT).show();
		}

	};

	ErrorListener createCollectionErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

}
