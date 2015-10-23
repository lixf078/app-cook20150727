package com.shecook.wenyi.piazza;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.shecook.wenyi.BaseFragmeng;
import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.WebViewActivity;
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
import com.shecook.wenyi.cookbook.CookbookHomeworkDeatilActivity;
import com.shecook.wenyi.cookbook.CookbookItemDeatilActivity;
import com.shecook.wenyi.essay.EssayItemDeatilActivity;
import com.shecook.wenyi.model.piazza.PiazzaDiscoverItem;
import com.shecook.wenyi.piazza.adapter.PiazzaDiscoverListAdapter;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PiazzaDiscoverFragment extends BaseFragmeng {
	private static final String TAG = "PiazzaDiscoverFragment";

	private Activity mActivity = null;
	
	private PullToRefreshListView mPullRefreshListView;
	PiazzaDiscoverListAdapter mAdapter = null;
	LinkedList<PiazzaDiscoverItem> mListItems;
	private boolean shouldLoad = true;

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
		View rootView = inflater.inflate(R.layout.piazza_discover_fragment,
				container, false);
		initView(rootView);
		JSONObject paramsub = new JSONObject();
		try {
			paramsub.put("count", "20");
		}catch(Exception e){
			
		}
		getDiscoverList(HttpUrls.PIZZA_DISCOVER_LIST_NEW, paramsub, listResultListener,
				listErrorListener);
		return rootView;
	}
	
	public void initView(View rootView) {
		
		mPullRefreshListView = (PullToRefreshListView) rootView
				.findViewById(R.id.pull_refresh_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								mActivity.getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						JSONObject paramsub = new JSONObject();
						try {
							paramsub.put("count", "20");
						}catch(Exception e){
							
						}
						getDiscoverList(HttpUrls.PIZZA_DISCOVER_LIST_NEW, paramsub, listResultListener,
								listErrorListener);
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						JSONObject paramsub = new JSONObject();
						try {
							paramsub.put("count", "20");
							paramsub.put("timeline", "" + mListItems.get(mListItems.size() -1).getTimeline());
						}catch(Exception e){
							
						}
						getDiscoverList(HttpUrls.PIZZA_DISCOVER_LIST_HISTORY, paramsub, historylistResultListener,
								listErrorListener);
					}
				});
		mListItems = new LinkedList<PiazzaDiscoverItem>();
		mAdapter = new PiazzaDiscoverListAdapter(mActivity, mListItems);

		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				
				PiazzaDiscoverItem pdi = mListItems.get((int) position);
				int event_type = Integer.parseInt(pdi.getEvent_type());
				String event_content = pdi.getEvent_content();
				Intent intent = null;
				switch (event_type) {
				case 10000:

					break;
				case 10001:
					intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event_content));
					startActivity(intent);
					startActivity(intent);
					break;
				case 10002:
					intent = new Intent(mActivity, EssayItemDeatilActivity.class);
					intent.putExtra("essaytitle", "" + mListItems.get((int) position).getTitle());
					intent.putExtra("catalogtitle", "文怡随笔");
					intent.putExtra("articleid", "" + event_content);
					startActivity(intent);

					break;
				case 10003:
					intent = new Intent(mActivity, CookbookItemDeatilActivity.class);
					intent.putExtra("cookbooktitle", "" + mListItems.get((int) position).getTitle());
					intent.putExtra("recipeid", "" + event_content);
					startActivity(intent);

					break;
				case 10004:
					intent = new Intent(mActivity, WebViewActivity.class);
					intent.putExtra("weburl", "" + event_content);
					startActivity(intent);

					break;
				case 10005:
					/*intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event_content));
					startActivity(intent);*/
					break;
				case 10006:
					intent = new Intent(mActivity, PizzaQuestionDeatilActivity.class);
					intent.putExtra("topicid", "" + event_content);
					startActivity(intent);
				case 10007:
					intent = new Intent(mActivity, CookbookHomeworkDeatilActivity.class);
					intent.putExtra("followid", "" + event_content);
					startActivity(intent);
					break;
				default:
					break;
				}
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
				mPullRefreshListView.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};

	public void getDiscoverList(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util.getCommonParam(mActivity);
		try {
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

	Listener<JSONObject> listResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initData(result, 0);
		}
	};
	
	Listener<JSONObject> historylistResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initData(result, 1);
		}
	};

	ErrorListener listErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	private void initList(JSONArray entrylist, boolean istop) throws JSONException{
		Log.e(TAG, "initList " + entrylist);
		LinkedList<PiazzaDiscoverItem> listTemp = new LinkedList<PiazzaDiscoverItem>();
		for (int i = 0, j = entrylist.length(); i < j; i++) {
			JSONObject jb = entrylist.getJSONObject(i);
			PiazzaDiscoverItem pdi = new PiazzaDiscoverItem();
			if(jb.has("entry")){
				JSONObject entry = jb.getJSONObject("entry"); 
				pdi.setTitle(entry.getString("title"));
				pdi.setImageurl(entry.getString("imageurl"));
				pdi.setDesc(entry.getString("desc"));
				pdi.setImg_width(entry.getInt("img_width"));
				pdi.setImg_height(entry.getInt("img_height"));
				if(entry.has("image_items")){
					JSONArray imagelist = entry.getJSONArray("image_items");
					int length = imagelist.length();
					String[] tempimages = new String[length];
					for (int k = 0, t = length; k < t; k++) {
						JSONObject imageentry = imagelist.getJSONObject(k);
						tempimages[k]= imageentry.getString("url");
					}
					pdi.setImage_items(tempimages);
				}
			}
			
			pdi.set_id(jb.getString("_id"));
			pdi.setObjid(jb.getString("objid"));
			pdi.setTemplate(jb.getString("template"));
			pdi.setEvent_type(jb.getString("event_type"));
			pdi.setEvent_content(jb.getString("event_content"));
			pdi.setIstop(jb.getBoolean("istop"));
			pdi.setKeyword(jb.getString("keyword"));
			pdi.setTimeline(jb.getString("timeline"));
			
			if(istop){
				listTemp.add(0,pdi);
			}else{
				listTemp.add(pdi);
			}
		}
		mListItems.addAll(listTemp);
	}
	
	private void initData(JSONObject jsonObject, int flag) {
		if(flag == 0){
			mListItems.clear();
			shouldLoad = true;
		}
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");

						if(data.has("toplist")){
							JSONArray toplist = data.getJSONArray("toplist");
							initList(toplist, true);
						}
						
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							initList(list, false);
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
			handler.sendEmptyMessage(HttpStatus.STATUS_OK);
		}
	}

	ErrorListener piazzaDiscoverErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	Listener<JSONObject> piazzaDiscoverResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			initData(result, 0);
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
		}
	};
}
