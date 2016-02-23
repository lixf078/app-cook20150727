package com.shecook.wenyi.personal;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.shared.widget.LeBottomSheet;
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
import com.shecook.wenyi.model.personal.PersonalCommentModel;
import com.shecook.wenyi.personal.adapter.PersonalCommentListAdapter;
import com.shecook.wenyi.personal.adapter.PersonalCommentListAdapter.OnSwipeOperator;
import com.shecook.wenyi.piazza.PizzaQuestionDeatilActivity;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalCommentsListActivity extends BaseActivity implements
		OnClickListener, OnSwipeOperator {

	static final String TAG = "PersonalTopicListActivity";
	public static final int STATUS_OK_COMMENT_GROUP = 1;
	public static final int STATUS_OK_COMMENT_CREATE = 2;
	public static final int STATUS_OK_COMMENT_COLLECTED = 3;
	private LinkedList<PersonalCommentModel> commentList;
	private PullToRefreshListView mPullRefreshListView;
	private PersonalCommentListAdapter mAdapter;

	private boolean shouldLoad = true;
	private int pindex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments_pull_to_refresh_listview_divider);
		initView();
		processParam();
	}

	private void initView() {

		commentList = new LinkedList<PersonalCommentModel>();

		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(this);
		ImageView settingImage = (ImageView) findViewById(R.id.right_img);
		settingImage.setVisibility(View.GONE);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		titleView.setText("我的评论");

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								PersonalCommentsListActivity.this.getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						processParam();
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						processParam();
					}
				});

		mAdapter = new PersonalCommentListAdapter(this, commentList);
		mAdapter.setOnSwipeOperator(this);
		
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				getBottomDialog((int) position);
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
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			case STATUS_OK_COMMENT_COLLECTED:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			case STATUS_OK_COMMENT_CREATE:
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

		case R.id.return_img:
			finish();
		default:
			break;
		}
	}

	// ********************************
	
	Dialog dialog = null;
	private void getBottomDialog(final int position) {
		dialog = Util.getBottomDialog(PersonalCommentsListActivity.this,
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
				onDeleteItem(position);
				dialog.dismiss();
			}
		});

		button4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Util.dispatchClickEvent(PersonalCommentsListActivity.this, commentList.get(position).getClickto(), commentList.get(position).getMainid(), new String[]{commentList.get(position).getCommentid()});
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
	// ********************************
	
	
	LeBottomSheet mBottomSheet;
	public void showBottomSheet(final int position){
		mBottomSheet = new LeBottomSheet(PersonalCommentsListActivity.this,R.style.wenyi_bottom_dialog);
		
		mBottomSheet.setWenyiStyle(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onDeleteItem(position);
			}
		}, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mBottomSheet.isShowing()){
					mBottomSheet.dismiss();
				}
			}
		}, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.dispatchClickEvent(PersonalCommentsListActivity.this, commentList.get(position).getClickto(), commentList.get(position).getMainid(), new String[]{commentList.get(position).getCommentid()});
			}
		}, new String[]{"删除", "取消", "查看"}, null, "请选择", R.color.blue);
		mBottomSheet.setContentAtCenter(true);
		mBottomSheet.show();
	}
	
	
	
	@Override
	public void onChangeGroup(int position) {
		
	}

	@Override
	public void onDeleteItem(int position) {
		Log.e("lixufeng", "onDeleteItem position " + position + ", commentList " + commentList.size());
		/*JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("topicid", commentList.get(position).get_id());
			getPersonalCollectionInfo(
					HttpUrls.PERSONAL_COLLECTION_DELECT_COOKBOOK, paramObject, delectResultListener,
					delectErrorListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
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
								handler.sendEmptyMessage(STATUS_OK_COMMENT_COLLECTED);
								msg = "" + "删除评论成功！";
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
			Toast.makeText(PersonalCommentsListActivity.this, msg,
					Toast.LENGTH_SHORT).show();
		}
	};
	
	ErrorListener delectErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	public void processParam(){
		if (shouldLoad) {
			JSONObject paramObject = new JSONObject();
			try {
				paramObject.put("pindex", "" + ++pindex);
				getPersonalCollectionInfo(
						HttpUrls.PERSONAL_MY_COMMENTS_LIST,
						paramObject, listResultListener,
						listErrorListener);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Toast.makeText(PersonalCommentsListActivity.this, "您已翻到底儿了!",
					Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(HttpStatus.STATUS_OK);
		}
	}
	
	public void getPersonalCollectionInfo(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util
				.getCommonParam(PersonalCommentsListActivity.this);
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
							LinkedList<PersonalCommentModel> listTemp = new LinkedList<PersonalCommentModel>();
							for(int i = 0,j = list.length(); i < j; i++){
								JSONObject jb = list.getJSONObject(i);
								PersonalCommentModel eli = new PersonalCommentModel();
								eli.set_id(jb.getString("_id"));
								eli.setUid(jb.getString("uid"));
								eli.setClickto(jb.getString("clickto"));
								eli.setMainid(jb.getString("mainid"));
								eli.setCommentid(jb.getString("commentid"));
								eli.setTop_nickname(jb.getString("top_nickname"));
								eli.setTop_imageurl(jb.getString("top_imageurl"));
								eli.setTop_desc(jb.getString("top_desc"));
								eli.setBottom_nickname(jb.getString("bottom_nickname"));
								eli.setBottom_imageurl(jb.getString("bottom_imageurl"));
								eli.setBottom_desc(jb.getString("bottom_desc"));
								listTemp.add(eli);
							}
							commentList.addAll(listTemp);
						}
						
						pindex = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 200 && pindex == 0){
							shouldLoad = false;
						}
					}
				}else{
					Toast.makeText(PersonalCommentsListActivity.this, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
				handler.sendEmptyMessage(HttpStatus.STATUS_OK);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
