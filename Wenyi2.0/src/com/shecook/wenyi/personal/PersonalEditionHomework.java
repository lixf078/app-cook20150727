package com.shecook.wenyi.personal;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.cookbook.CookbookHomeworkDeatilActivity;
import com.shecook.wenyi.cookbook.adapter.CookbookHomeworkListAdapter;
import com.shecook.wenyi.model.CookbookHomeworkListItem;
import com.shecook.wenyi.model.WenyiImage;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;

public class PersonalEditionHomework extends Fragment {
	private static final String TAG = "PiazzaFragment";
	public static final int STATUS_OK_COLLECTION_COLLECTED = 3;
	private Activity mActivity = null;
	NetworkImageView networkImageView = null;
	
	private LinkedList<CookbookHomeworkListItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CookbookHomeworkListAdapter mAdapter;
	
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
		View rootView = inflater.inflate(R.layout.cookbook_homework_list, container, false);
		initView(rootView);
		getHomeworkList(HttpUrls.PERSONAL_EDITION_HOMEWORK, null,
				homeworkResultListener, homeworkErrorListener);
		return rootView;
	}
	
	public void initView(View rootView){
		
		rootView.findViewById(R.id.wenyi_common_header_id).setVisibility(View.GONE);
		
		mPullRefreshListView = (PullToRefreshListView) rootView.findViewById(R.id.pull_refresh_list);

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
						if(shouldLoad){
							getHomeworkList(HttpUrls.PERSONAL_EDITION_HOMEWORK, null, homeworkResultListener, homeworkErrorListener);
						}else{
							Toast.makeText(mActivity, "您已翻到底儿了!",
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
						if(shouldLoad){
							getHomeworkList(HttpUrls.PERSONAL_EDITION_HOMEWORK, null, homeworkResultListener, homeworkErrorListener);
						}else{
							Toast.makeText(mActivity, "您已翻到底儿了!", Toast.LENGTH_SHORT).show();
							handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						}
					}
				});

//		ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<CookbookHomeworkListItem>();
		mAdapter = new CookbookHomeworkListAdapter(mActivity, mListItems);

		/**
		 * Add Sound Event Listener
		 */
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				getBottomDialog((int)position);
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
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			case STATUS_OK_COLLECTION_COLLECTED:
				mListItems.remove(operPosition);
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			default:
				break;
			}
		};
	};
	
	Dialog dialog = null;
	private void getBottomDialog(final long position) {
		dialog = Util.getBottomDialog(mActivity,
				R.layout.a_common_bottom_dialog_layout);
		ImageView img3 = null;
		Button button3 = null;
		ImageView img4 = null;
		Button button4 = null;
		ImageView img5 = null;
		Button button5 = null;

		img3 = (ImageView) dialog.findViewById(R.id.wenyi_bottomsheet_img_3);
		button3 = (Button) dialog.findViewById(R.id.wenyi_bottomsheet_btn_3);
		img3.setVisibility(View.VISIBLE);
		button3.setVisibility(View.VISIBLE);
		button3.setText("删除");
		img4 = (ImageView) dialog.findViewById(R.id.wenyi_bottomsheet_img_4);
		button4 = (Button) dialog.findViewById(R.id.wenyi_bottomsheet_btn_4);
		img4.setVisibility(View.VISIBLE);
		button4.setVisibility(View.VISIBLE);
		button4.setText("查看");
		img5 = (ImageView) dialog.findViewById(R.id.wenyi_bottomsheet_img_5);
		button5 = (Button) dialog.findViewById(R.id.wenyi_bottomsheet_btn_5);
		img5.setVisibility(View.VISIBLE);
		button5.setVisibility(View.VISIBLE);
		button5.setText("取消");

		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onDeleteItem((int)position);
				dialog.dismiss();
			}
		});

		button4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity, CookbookHomeworkDeatilActivity.class);
				intent.putExtra("topicid", "" + mListItems.get((int) position).getId());
				startActivity(intent);
				dialog.dismiss();
			}
		});

		button5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private int index = 0;
	public void getHomeworkList(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		try {
			JSONObject paramsub = new JSONObject();
			paramsub.put("pindex", "" + ++index);
			paramsub.put("count", "20");
			Util.commonHttpMethod(mActivity, url, paramsub, resultListener, errorListener);
		} catch (JSONException e) {
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
							for(int i = 0,j = list.length(); i < j; i++){
								JSONObject jb = list.getJSONObject(i);
								CookbookHomeworkListItem phi = new CookbookHomeworkListItem();
								phi.setId(jb.getString("id"));
								phi.setRecipeid(jb.getString("recipeid"));
								phi.setUid(jb.getString("uid"));
								phi.setNickname(jb.getString("nickname"));
								phi.setUportrait(jb.getString("uportrait"));
								phi.setDescription(jb.getString("description"));
								phi.setComments(jb.getString("comments"));
								phi.setTimeline(jb.getString("timeline"));
								if(jb.has("images")){
									JSONArray imagelist = jb.getJSONArray("images");
									for(int k = 0, t = imagelist.length(); k < t; k++){
										JSONObject imagejb = imagelist.getJSONObject(k);
										WenyiImage homeWorkImage = new WenyiImage();
										homeWorkImage.setId(imagejb.getString("id"));
										homeWorkImage.setFollowid(imagejb.getString("followid"));
										homeWorkImage.setImageurl(imagejb.getString("imageurl"));
										phi.getImageList().add(homeWorkImage);
									}
								}
								listTemp.add(phi);
							}
							mListItems.addAll(listTemp);
						}else{
							Toast.makeText(mActivity, "" + data.getString("msg"), Toast.LENGTH_SHORT).show();
						}
						
						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 0 && index == 0){
							shouldLoad = false;
						}
					}
				}else{
					Toast.makeText(mActivity, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendEmptyMessage(HttpStatus.STATUS_OK);
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
			initData(result, 0);
		}
	};
	
	int operPosition = -1;
	public void onDeleteItem(int position) {
		operPosition = position;
		Log.e("lixufeng", "onDeleteItem position " + position + ", groupSharedList " + mListItems.size());
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("recipeid", mListItems.get(position).getId());
			Util.commonHttpMethod(mActivity, 
					HttpUrls.PERSONAL_EDITION_HOMEWORK_DEL, paramObject, delectResultListener,
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
								handler.sendEmptyMessage(STATUS_OK_COLLECTION_COLLECTED);
								msg = "" + "删除作业成功！";
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
			Toast.makeText(mActivity, msg,
					Toast.LENGTH_SHORT).show();
		}
	};
	
	ErrorListener delectErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
}
