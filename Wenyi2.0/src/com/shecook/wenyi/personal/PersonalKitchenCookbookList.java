package com.shecook.wenyi.personal;

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
import com.shecook.wenyi.StartActivity.UpdateFragmentListener;
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
import com.shecook.wenyi.cookbook.CookbookItemDeatilActivity;
import com.shecook.wenyi.cookbook.adapter.CookbookListAdapter;
import com.shecook.wenyi.model.CookbookListItem;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalKitchenCookbookList extends BaseActivity implements
		UpdateFragmentListener {

	private static final String TAG = "PersonalKitchenCookbookList";

	static final int MENU_SET_MODE = 0;
	public JSONObject cookbookCatalogObject;

	private LinkedList<CookbookListItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CookbookListAdapter mAdapter;
	private boolean isFirstLoad = true;

	private String[] keywords;
	private String hotword;
	private String flag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.copyofcookbook_list_fragment);
//		keyword = getIntent().getStringExtra("keyword");

		flag = getIntent().getStringExtra("flag");
		initView();

		mListItems = new LinkedList<CookbookListItem>();
		if("food".equals(flag)){
			keywords = getIntent().getStringArrayExtra("keyword");
			getCatalogList(HttpUrls.PERSONAL_KITCHEN_FOOD_COMB, null,
					listResultListener, listErrorListener);
		}else{
			keyword = getIntent().getStringExtra("keyword");
			getCatalogList(HttpUrls.PERSONAL_KITCHEN_FOOD_KEY, null,
					listResultListener, listErrorListener);
		}

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								PersonalKitchenCookbookList.this,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
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

		mAdapter = new CookbookListAdapter(PersonalKitchenCookbookList.this,
				mListItems);

		/**
		 * Add Sound Event Listener
		 */
		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(
				PersonalKitchenCookbookList.this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		// mPullRefreshListView.setOnPullEventListener(soundListener); // 音效
		mPullRefreshListView.setMode(Mode.DISABLED);
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Log.d(TAG, "onItemClick arg2 " + arg2 + ",position " + position
						+ ",recipeid " + mListItems.get((int) position).getId()
						+ ",cookbooktitle "
						+ mListItems.get((int) position).getRecipename());
				Intent intent = new Intent(PersonalKitchenCookbookList.this,
						CookbookItemDeatilActivity.class);
				intent.putExtra("cookbooktitle",
						"" + mListItems.get((int) position).getRecipename());
				intent.putExtra("recipeid", ""
						+ mListItems.get((int) position).getId());
				startActivity(intent);
			}
		});

	}

	String title = "";

	private void initView() {
		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setVisibility(View.GONE);

		ImageView settingImage = (ImageView) findViewById(R.id.right_img);
		settingImage.setVisibility(View.GONE);

		TextView titleView = (TextView) findViewById(R.id.middle_title);
		if (TextUtils.isEmpty(title)) {
			titleView.setText(R.string.caipu);
		} else {
			titleView.setText("" + title);
		}
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

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				if (isFirstLoad) {
					isFirstLoad = false;
					// 把全局加载框隐藏
				} else {
					// 隐藏局部加载框
				}
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;
			case 2:
				getCatalogList(HttpUrls.COOKBOOK_LIST, null,
						listResultListener, listErrorListener);
			default:
				break;
			}
		};
	};

	public JSONObject getCookbookCatalogObject() {
		return cookbookCatalogObject;
	}

	public void setCookbookCatalogObject(JSONObject cookbookCatalogObject) {
		this.cookbookCatalogObject = cookbookCatalogObject;
	}

	public String getCatalogid() {
		return keyword;
	}

	public void setCatalogid(String catalogid) {
		this.keyword = catalogid;
	}

	String keyword = "";

	public void getCatalogList(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util
				.getCommonParam(PersonalKitchenCookbookList.this);
		JSONObject paramsub = new JSONObject();

		try {
			if("food".equals(flag)){
				paramsub.put("keyword", fromJosn());
			}else{
				paramsub.put("keyid", keyword);
			}
			Log.e("lixufeng", "getCatalogList " + paramsub.toString());
			jsonObject.put("param", paramsub);
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

	public JSONArray fromJosn() {
		JSONArray jsonarray = new JSONArray();// json数组，里面包含的内容为pet的所有对象
		for (int i = 0; i < keywords.length; i++) {
			try {
				JSONObject jsonObj = new JSONObject();// pet对象，json形式
				jsonObj.put("name", keywords[i]);// 向pet对象里面添加值
				jsonarray.put(jsonObj);// 向json数组里面添加pet对象
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonarray;
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
						if (data.has("list")) {
							JSONArray list = data.getJSONArray("list");
							LinkedList<CookbookListItem> listTemp = new LinkedList<CookbookListItem>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								CookbookListItem eli = new CookbookListItem();
								eli.setId(jb.getString("id"));
								eli.setRecipename(jb.getString("recipename"));
								eli.setSummary(jb.getString("summary"));
								eli.setImgoriginal(jb.getString("imgoriginal"));
								eli.setImgthumbnail(jb
										.getString("imgthumbnail"));
								eli.setComments(jb.getString("comments"));
								eli.setFollows(jb.getString("follows"));
								eli.setTag(jb.getString("tag"));
								eli.setTimeline(jb.getString("timeline"));
								listTemp.add(eli);
							}
							mListItems.addAll(listTemp);
						}

						int core_status = data.getInt("core_status");
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				} else {
					Toast.makeText(PersonalKitchenCookbookList.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void updateFragment(String cataId) {
		Log.d(TAG, "updateFragment");
		keyword = cataId;
		getCatalogList(HttpUrls.ESSAY_WENYI_LIST, null, listResultListener,
				listErrorListener);
	}
}
