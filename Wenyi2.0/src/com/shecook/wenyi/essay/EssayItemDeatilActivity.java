package com.shecook.wenyi.essay;

import java.util.LinkedList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

import com.shecook.wenyi.BaseActivity;
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
import com.shecook.wenyi.essay.adapter.EssayListDetailAdapter;
import com.shecook.wenyi.model.EssayListItemDetail;
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class EssayItemDeatilActivity extends BaseActivity {

	private LinkedList<EssayListItemDetail> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private EssayListDetailAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.essay_listitem_detail);
		initView();

		getCatalogList(HttpUrls.ESSAY_WENYILIST_ITEM_DETAIL, null, detailResultListener,
				detailErrorListener);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						getCatalogList(HttpUrls.ESSAY_WENYILIST_ITEM_DETAIL, null,
								detailResultListener, detailErrorListener);
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						
					}
				});

		// ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<EssayListItemDetail>();
		mAdapter = new EssayListDetailAdapter(this, mListItems);

		mPullRefreshListView.setMode(Mode.DISABLED);
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d(TAG, "onItemClick arg2 " + arg2 + ",arg3 " + arg3);

			}
		});

	}
	private String articleid = "";
	private void initView() {
		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		ImageView settingImage = (ImageView) findViewById(R.id.right_img);
		settingImage.setVisibility(View.GONE);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		String title = getIntent().getStringExtra("essaytitle");
		articleid = getIntent().getStringExtra("articleid");
		if (TextUtils.isEmpty(title)) {
			titleView.setText(R.string.essay);
		} else {
			titleView.setText("" + title);
		}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
				Log.d("lixufeng111",
						"handleMessage mListItems " + mListItems.toString());
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};
	
	String mid = "";
	public void getCatalogList(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		WenyiUser user = Util.getUserData(EssayItemDeatilActivity.this);
		Log.d("lixufeng", "getCatalogList " + user);
		JSONObject sub = new JSONObject();
		JSONObject paramsub = new JSONObject();
		if (TextUtils.isEmpty(user.get_mID())) {
			mid = UUID.randomUUID().toString();
		}else{
			mid = user.get_mID();
		}
		try {
			paramsub.put("articleid", articleid);
			
			sub.put("mtype", "android");
			sub.put("mid", mid);
			sub.put("token", user.get_token());
			if(null == jsonObject){
				jsonObject = new JSONObject();
			}
			jsonObject.put("param", paramsub);
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
	Listener<JSONObject> detailResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initData(result, 0);

		}
	};

	ErrorListener detailErrorListener = new Response.ErrorListener() {
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
						WenyiLog.logv(TAG, "initData 44444");
						JSONObject data = jsonObject.getJSONObject("data");
						
						JSONArray list = data.getJSONArray("detail");
						LinkedList<EssayListItemDetail> listTemp = new LinkedList<EssayListItemDetail>();
						for(int i = 0,j = list.length(); i < j; i++){
							JSONObject jb = list.getJSONObject(i);
							WenyiLog.logv(TAG, "initData 5555 jb " + jb.toString());
							EssayListItemDetail elid = new EssayListItemDetail();
							elid.setId(jb.getString("id"));
							elid.setCataid(jb.getString("cataid"));
							elid.setArticleid(jb.getString("articleid"));
							elid.setRowtype(jb.getString("rowtype"));
							elid.setRowcontent(jb.getString("rowcontent"));
							listTemp.add(elid);
						}
						mListItems.addAll(listTemp);
						
						handler.sendEmptyMessage(1);
					}
				}else{
					Toast.makeText(EssayItemDeatilActivity.this, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
