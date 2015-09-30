package com.shecook.wenyi.cookbook;

import java.util.LinkedList;

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
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.cookbook.adapter.CookbookHomeworkListAdapter;
import com.shecook.wenyi.model.CookbookHomeworkListItem;
import com.shecook.wenyi.model.WenyiImage;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CookbookHomeworkList extends BaseActivity implements OnClickListener{

	private static final String TAG = "CookbookHomeworkList";
	
	NetworkImageView networkImageView = null;
	
	private LinkedList<CookbookHomeworkListItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CookbookHomeworkListAdapter mAdapter;
	private boolean shouldLoad = true;
	
	private ImageView return_img, right_img;
	private TextView middle_title;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cookbook_homework_list);
		initView();
		getHomeworkList(HttpUrls.COOKBOOK_HOMEWORK_LIST,null,listResultListener,listErrorListener);
	}

	public void initView(){
		
		right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setOnClickListener(this);
		
		networkImageView = (NetworkImageView) findViewById(R.id.main_layout_fillparent);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								CookbookHomeworkList.this.getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						if(shouldLoad){
							getHomeworkList(HttpUrls.COOKBOOK_HOMEWORK_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(CookbookHomeworkList.this, "End of List!",
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
						// Do work to refresh the list here.
						if(shouldLoad){
							getHomeworkList(HttpUrls.COOKBOOK_HOMEWORK_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(CookbookHomeworkList.this, "End of List!",
									Toast.LENGTH_SHORT).show();
							handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						}
					}
				});

//		ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<CookbookHomeworkListItem>();
		Log.d("lixufeng111", "onCreate mListItems " + mListItems.toString());
		mAdapter = new CookbookHomeworkListAdapter(CookbookHomeworkList.this, mListItems);

		/**
		 * Add Sound Event Listener
		 */
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
	public void onDestroy() {
		WenyiLog.logv(TAG, "onDestroy");
		super.onDestroy();
	}

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
//				testData();
				if(mListItems.size() == 0){
					networkImageView.setVisibility(View.VISIBLE);
				}else{
					networkImageView.setVisibility(View.GONE);
				}
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

		default:
			break;
		}
	}

	private int index = 0;
	public void getHomeworkList(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if(null == jsonObject){
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(CookbookHomeworkList.this);
		JSONObject paramsub = new JSONObject();
		try {
			jsonObject.put("common", commonsub);
			String recipeid = CookbookHomeworkList.this.getIntent().getStringExtra("recipeid");
			if(TextUtils.isEmpty(recipeid)){
				recipeid = "3133";
			}
			paramsub.put("recipeid", recipeid);
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
							LinkedList<CookbookHomeworkListItem> listTemp = new LinkedList<CookbookHomeworkListItem>();
							Log.d(TAG, "initData list length -> " + list.length());
							for(int i = 0,j = list.length(); i < j; i++){
								JSONObject jb = list.getJSONObject(i);
								CookbookHomeworkListItem chli = new CookbookHomeworkListItem();
								chli.setId(jb.getString("id"));
								chli.setRecipeid(jb.getString("recipeid"));
								chli.setUid(jb.getString("uid"));
								chli.setNickname(jb.getString("nickname"));
								chli.setUportrait(jb.getString("uportrait"));
								chli.setDescription(jb.getString("description"));
								chli.setComments(jb.getString("comments"));
								chli.setTimeline(jb.getString("timeline"));
								
								if(jb.has("images")){
									JSONArray imagchlist = jb.getJSONArray("images");
									for(int k = 0, t = imagchlist.length(); k < t; k++){
										JSONObject imagejb = imagchlist.getJSONObject(k);
										WenyiImage homeWorkImage = new WenyiImage();
										homeWorkImage.setId(imagejb.getString("id"));
										homeWorkImage.setFollowid(imagejb.getString("followid"));
										homeWorkImage.setImageurl(imagejb.getString("imageurl"));
										chli.getImageList().add(homeWorkImage);
									}
								}
								listTemp.add(chli);
							}
							mListItems.addAll(listTemp);
						}
						/*
						if(data.has("toplist")){
							JSONArray toplist = data.getJSONArray("toplist");
							LinkedList<CookbookHomeworkListItem> toplistTemp = new LinkedList<CookbookHomeworkListItem>();
							for(int i = 0,j = toplist.length(); i < j; i++){
								JSONObject topjb = toplist.getJSONObject(i);
								WenyiLog.logv(TAG, "initData toplist topjb " + topjb.toString());
								CookbookHomeworkListItem topchli = new CookbookHomeworkListItem();
								topchli.setId(topjb.getString("id"));
								topchli.setCataid(topjb.getString("cataid"));
								topchli.setTitle(topjb.getString("title"));
								topchli.setSumm(topjb.getString("summ"));
								topchli.setIconurl(topjb.getString("iconurl"));
								topchli.setOntop(topjb.getBoolean("ontop"));
								topchli.setEvent_type(topjb.getString("event_type"));
								topchli.setEvent_content(topjb.getString("event_content"));
								topchli.setQkey(topjb.getString("qkey"));
								topchli.setTimchline(topjb.getString("timchline"));
								toplistTemp.add(topchli);
							}
							mListItems.addAll(0, toplistTemp);
						}*/
						
						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 200 && index == 0){
							Log.e(TAG, "has not some item");
							shouldLoad = false;
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				}else{
					Toast.makeText(CookbookHomeworkList.this, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void testData(){
		if(mListItems.size()>0){
			return;
		}
		LinkedList<CookbookHomeworkListItem> listTemp = new LinkedList<CookbookHomeworkListItem>();
		for(int i = 0,j = 10; i < j; i++){
			CookbookHomeworkListItem chli = new CookbookHomeworkListItem();
			chli.setId("27650");
			chli.setRecipeid("3133");
			chli.setUid("0");
			chli.setNickname("玫瑰色人生");
			chli.setUportrait("http://img2.shecook.com/members/558631d1cf6044699e6d92ba562ff8e0/normal/0.jpg");
			chli.setDescription("作业描述");
			chli.setComments("5");
			chli.setTimeline("1分钟前");
			if(i == 1){
				for(int k = 0, t =2; k < t; k++){
					WenyiImage homeWorkImage = new WenyiImage();
					homeWorkImage.setId("27649");
					homeWorkImage.setFollowid("27650");
					homeWorkImage.setImageurl("http://static.wenyijcc.com/submit/201501/5f6aef0fb78245169d6583173e37e35b.jpg");
					chli.getImageList().add(homeWorkImage);
				}
			}else if (i == 2){
				chli.setNickname("丛中笑9166");
				chli.setUportrait("http://img2.shecook.com/members/29c4da0f62954d8291fa340d6e804e1b/normal/0.jpg");
				for(int k = 0, t =2; k < t; k++){
					WenyiImage homeWorkImage = new WenyiImage();
					homeWorkImage.setId("27649");
					homeWorkImage.setFollowid("27650");
					homeWorkImage.setImageurl("http://static.wenyijcc.com/submit/201501/5f6aef0fb78245169d6583173e37e35b.jpg");
					chli.getImageList().add(homeWorkImage);
				}
			}else{
				chli.setNickname("五彩水晶冻儿成功");
				chli.setUportrait("http://img2.shecook.com/members/f24785b18936469db6433dc8c44e9440/normal/0.jpg");
				for(int k = 0, t =2; k < t; k++){
					WenyiImage homeWorkImage = new WenyiImage();
					homeWorkImage.setId("27649");
					homeWorkImage.setFollowid("27650");
					homeWorkImage.setImageurl("http://static.wenyijcc.com/submit/201412/6bd86dbef6b8464cbabb84e3aa4fea50.jpg");
					chli.getImageList().add(homeWorkImage);
				}
			}
			listTemp.add(chli);
		}
//		mListItems.addAll(listTemp);
	}
}
