package com.shecook.wenyi.essay;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
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
import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase.Mode;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshBase.State;
import com.shecook.wenyi.common.pulltorefresh.PullToRefreshListView;
import com.shecook.wenyi.common.pulltorefresh.internal.SoundPullEventListener;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.essay.adapter.EssayListAdapter;
import com.shecook.wenyi.model.EssayListItem;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class EssayListActivity extends BaseActivity {

	static final String TAG = "EssayListActivity";

	private LinkedList<EssayListItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private EssayListAdapter mAdapter;
	
	private boolean shouldLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_essay_list);
		initView();
		getCatalogList(HttpUrls.ESSAY_WENYI_LIST,null,listResultListener,listErrorListener);
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
						if(shouldLoad){
							getCatalogList(HttpUrls.ESSAY_WENYI_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(EssayListActivity.this, "End of List!",
									Toast.LENGTH_SHORT).show();
							handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						}
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						Log.e(TAG, "onLastItemVisible shouldLoad " + shouldLoad);
						if(shouldLoad){
							getCatalogList(HttpUrls.ESSAY_WENYI_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(EssayListActivity.this, "End of List!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

//		ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<EssayListItem>();
		Log.d("lixufeng111", "onCreate mListItems " + mListItems.toString());
		mAdapter = new EssayListAdapter(this, mListItems);

		/**
		 * Add Sound Event Listener
		 */
		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(
				this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		mPullRefreshListView.setOnPullEventListener(soundListener);
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Intent intent = new Intent(EssayListActivity.this,EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "" + mListItems.get((int)position).getTitle());
				intent.putExtra("catalogtitle", "" + EssayListActivity.this.getIntent().getStringExtra("catalogtitle"));
				intent.putExtra("articleid", "" + mListItems.get((int)position).getId());
				startActivity(intent);
			}
		});
	}

	private void initView(){
		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		ImageView right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setBackgroundResource(R.drawable.search);
		right_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EssayListActivity.this, EssaySearchActivity.class);
				startActivity(intent);
			}
		});
		
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		String title = getIntent().getStringExtra("catalogtitle");
		if(TextUtils.isEmpty(title)){
			titleView.setText(R.string.essay);
		}else{
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
		JSONObject commonsub = Util.getCommonParam(EssayListActivity.this);
		JSONObject paramsub = new JSONObject();
		try {
			jsonObject.put("common", commonsub);
			String catalogid = getIntent().getStringExtra("catalogid");
			if(TextUtils.isEmpty(catalogid)){
				catalogid = "10";
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
		if(jsonObject != null){
			try {
				if(!jsonObject.isNull("statuscode") && 200 == jsonObject.getInt("statuscode")){
					if(!jsonObject.isNull("data")){
						JSONObject data = jsonObject.getJSONObject("data");
						
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<EssayListItem> listTemp = new LinkedList<EssayListItem>();
							for(int i = 0,j = list.length(); i < j; i++){
								JSONObject jb = list.getJSONObject(i);
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
					Toast.makeText(EssayListActivity.this, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
