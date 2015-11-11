package com.shecook.wenyi.essay;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
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
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.essay.adapter.EssayListAdapter;
import com.shecook.wenyi.model.EssayListItem;
import com.shecook.wenyi.util.Util;

public class EssaySearchActivity extends BaseActivity implements OnClickListener{

	private static final String TAG = "CookbookSearchActivity";
	
	private TextView searchButton;
	private EditText searchEdit;
	private LinkedList<EssayListItem> mListItems;
	
	private PullToRefreshListView mPullRefreshListView;
	private EssayListAdapter mAdapter;
	private boolean shouldLoad = true;
	
	String action = "search";
	String keywords = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.cookbook_essay_search);
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		
		searchButton = (TextView) findViewById(R.id.cookbook_input_button);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.search_cookbook_listview);

		searchButton.setOnClickListener(this);
		
		searchEdit = (EditText) findViewById(R.id.search_edit);
		mListItems = new LinkedList<EssayListItem>();
		
		ImageView returnImage = (ImageView)findViewById(R.id.return_img);
		returnImage.setVisibility(View.GONE);

		ImageView settingImage = (ImageView)findViewById(R.id.right_img);
		settingImage.setVisibility(View.GONE);

		TextView titleView = (TextView)findViewById(R.id.middle_title);
		titleView.setText("文怡随笔");
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.search_cookbook_listview);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(EssaySearchActivity.this,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						if (shouldLoad) {
							getKeywordsList(HttpUrls.ESSAY_WENYI_SEARCH, null,
									listResultListener, listErrorListener);
						}else{
							Toast.makeText(EssaySearchActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
							handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						}
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (shouldLoad) {
							getKeywordsList(HttpUrls.ESSAY_WENYI_SEARCH, null,
									listResultListener, listErrorListener);
						} else {
							Toast.makeText(EssaySearchActivity.this, "End of List!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		// ListView actualListView = mPullRefreshListView.getRefreshableView();

		mAdapter = new EssayListAdapter(EssaySearchActivity.this, mListItems);

		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Intent intent = new Intent(EssaySearchActivity.this,EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "" + mListItems.get((int)position).getTitle());
				intent.putExtra("articleid", "" + mListItems.get((int)position).getId());
				startActivity(intent);
			}
		});
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.cookbook_input_button:
			keywords = searchEdit.getEditableText().toString();
			index = 0;
			shouldLoad = true;
			getKeywordsList(HttpUrls.ESSAY_WENYI_SEARCH, null,
					listResultListener, listErrorListener);
			break;

		default:
			break;
		}
	}

	private AlertDialog alertDialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;
			case HttpStatus.STATUS_OK_2:
				
			default:
				break;
			}
		};
	};
	
	public void getKeywordsList(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject paramsub = new JSONObject();
		try {
			paramsub.put("keyword", keywords);
			paramsub.put("pindex", "" + ++index);
			paramsub.put("count", "20");
			Util.commonHttpMethod(EssaySearchActivity.this, HttpUrls.ESSAY_WENYI_SEARCH, paramsub, resultListener, errorListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private int index = 0;
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
							LinkedList<EssayListItem> listTemp = new LinkedList<EssayListItem>();
							for (int i = 0, j = list.length(); i < j; i++) {
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

						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if (core_status == 0 && index == 0) {
							shouldLoad = false;
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				} else {
					Toast.makeText(EssaySearchActivity.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
