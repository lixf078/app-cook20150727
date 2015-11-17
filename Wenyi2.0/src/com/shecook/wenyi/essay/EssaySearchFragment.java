package com.shecook.wenyi.essay;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

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
import com.shecook.wenyi.essay.EssayListActivity.OnKeywordChangeListener;
import com.shecook.wenyi.essay.adapter.EssayListAdapter;
import com.shecook.wenyi.model.EssayListItem;
import com.shecook.wenyi.util.Util;

public class EssaySearchFragment extends Fragment implements OnKeywordChangeListener{

	private static final String TAG = "EssaySearchFragment";
	
	private LinkedList<EssayListItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private EssayListAdapter mAdapter;
	private boolean shouldLoad = true;
	
	String action = "search";
	String keywords = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView =inflater.inflate(R.layout.le_cookbook_essay_search, null);
		initView(rootView);
		((EssayListActivity)getActivity()).setKeyworkChangeListener(this);
		return rootView;
	}
	
	private void initView(View rootView) {
		
		mListItems = new LinkedList<EssayListItem>();
		
		mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.search_cookbook_listview);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(EssaySearchFragment.this.getActivity(),
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
							Toast.makeText(EssaySearchFragment.this.getActivity(), "您已翻到底儿了!", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(EssaySearchFragment.this.getActivity(), "您已翻到底儿了!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		// ListView actualListView = mPullRefreshListView.getRefreshableView();

		mAdapter = new EssayListAdapter(EssaySearchFragment.this.getActivity(), mListItems);

		mPullRefreshListView.setMode(Mode.DISABLED);
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Intent intent = new Intent(EssaySearchFragment.this.getActivity(),EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "" + mListItems.get((int)position).getTitle());
				intent.putExtra("articleid", "" + mListItems.get((int)position).getId());
				startActivity(intent);
			}
		});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
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
			Util.commonHttpMethod(EssaySearchFragment.this.getActivity(), HttpUrls.ESSAY_WENYI_SEARCH, paramsub, resultListener, errorListener);
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
		mListItems.clear();
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
					Toast.makeText(EssaySearchFragment.this.getActivity(),
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyworkChanged(String keyword) {
		if(TextUtils.isEmpty(keyword)){
			mListItems.clear();
			handler.sendEmptyMessage(HttpStatus.STATUS_OK);
			return;
		}
		this.keywords = keyword;
		index = 0;
		shouldLoad = true;
		getKeywordsList(HttpUrls.COOKBOOK_WENYI_SEARCH, null,
				listResultListener, listErrorListener);
	}

	@Override
	public void keyworkClear() {
		mListItems.clear();
		handler.sendEmptyMessage(HttpStatus.STATUS_OK);
	}
	
}
