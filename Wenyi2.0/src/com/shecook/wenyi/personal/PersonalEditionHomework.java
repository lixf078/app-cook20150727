package com.shecook.wenyi.personal;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

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
import com.shecook.wenyi.essay.EssayItemDeatilActivity;
import com.shecook.wenyi.model.personal.HomeWorkImage;
import com.shecook.wenyi.model.personal.PersonalHomeworkItem;
import com.shecook.wenyi.personal.adapter.PersonalEditionListAdapter;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalEditionHomework extends Fragment {
	private static final String TAG = "PiazzaFragment";
	
	private Activity mActivity = null;
	private PullToRefreshListView mPullRefreshListView;
	PersonalEditionListAdapter mAdapter = null;
	LinkedList<PersonalHomeworkItem> mListItems;
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

	public void initView(View rootView){
		mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.pull_refresh_list);

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

						// Do work to refresh the list here.
//						getCatalogList(HttpUrls.ESSAY_WENYI_LIST,null,listResultListener,listErrorListener);
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if(shouldLoad){
							getHomeworkList(HttpUrls.ESSAY_WENYI_LIST,null,homeworkResultListener,homeworkErrorListener);
						}else{
							Toast.makeText(mActivity, "End of List!", Toast.LENGTH_SHORT).show();
						}
					}
				});

//		ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<PersonalHomeworkItem>();
		mAdapter = new PersonalEditionListAdapter(mActivity, mListItems);

		/**
		 * Add Sound Event Listener
		 */
		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(mActivity);
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
				Intent intent = new Intent(mActivity,EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "");
				startActivity(intent);
			}
		});
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.personal_edition_homeworklist, container, false);
		initView(rootView);
		getHomeworkList(HttpUrls.PERSONAL_EDITION_HOMEWORK, null,
				homeworkResultListener, homeworkErrorListener);
		return rootView;
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
	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
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
	public void getHomeworkList(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if(null == jsonObject){
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(mActivity);
		JSONObject paramsub = new JSONObject();
		try {
			jsonObject.put("common", commonsub);
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
		WenyiLog.logv(TAG, "initData 1111");
		if(jsonObject != null){
			try {
				WenyiLog.logv(TAG, "initData 22222");
				if(!jsonObject.isNull("statuscode") && 200 == jsonObject.getInt("statuscode")){
					WenyiLog.logv(TAG, "initData 33333");
					if(!jsonObject.isNull("data")){
						JSONObject data = jsonObject.getJSONObject("data");
						
						JSONArray list = data.getJSONArray("list");
						WenyiLog.logv(TAG, "initData 44444 length " + list.length());
						LinkedList<PersonalHomeworkItem> listTemp = new LinkedList<PersonalHomeworkItem>();
						for(int i = 0,j = list.length(); i < j; i++){
							JSONObject jb = list.getJSONObject(i);
							WenyiLog.logv(TAG, "initData 5555 jb " + jb.toString());
							PersonalHomeworkItem phi = new PersonalHomeworkItem();
							phi.setId(jb.getString("id"));
							phi.setRecipeid(jb.getString("recipeid"));
							phi.setUid(jb.getString("uid"));
							phi.setNickname(jb.getString("nickname"));
							phi.setUportrait(jb.getString("uportrait"));
							phi.setDescription(jb.getString("description"));
							phi.setComments(jb.getString("comments"));
							phi.setTimeline(jb.getString("timeline"));
							JSONArray imagelist = data.getJSONArray("images");
							LinkedList<HomeWorkImage> toplistTemp = new LinkedList<HomeWorkImage>();
							for(int k = 0, t = imagelist.length(); k < t; k++){
								JSONObject imagejb = imagelist.getJSONObject(k);
								HomeWorkImage homeWorkImage = new HomeWorkImage();
								homeWorkImage.setId(imagejb.getString("id"));
								homeWorkImage.setFollowid(imagejb.getString("followid"));
								homeWorkImage.setImageurl(imagejb.getString("imageurl"));
								phi.getImages().add(homeWorkImage);
							}
							listTemp.add(phi);
						}
						mListItems.addAll(listTemp);
						
						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 0 && index == 0){
							shouldLoad = false;
						}
						handler.sendEmptyMessage(1);
					}
				}else{
					Toast.makeText(mActivity, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	ErrorListener homeworkErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	Listener<JSONObject> homeworkResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG, "catalogResultListener onResponse -> " + result.toString());
		}
	};
}
