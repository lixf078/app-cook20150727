package com.shecook.wenyi.piazza;

import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import com.shecook.wenyi.common.AddCommentActivity;
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
import com.shecook.wenyi.essay.EssayItemDeatilActivity;
import com.shecook.wenyi.model.WenyiImage;
import com.shecook.wenyi.model.piazza.PiazzaQuestionCommentItem;
import com.shecook.wenyi.model.piazza.PiazzaQuestionItem;
import com.shecook.wenyi.piazza.adapter.PiazzaQuestionListDetialAdapter;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PizzaQuestionDeatilActivity extends BaseActivity implements
		OnClickListener {

	private LinkedList<Object> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private PiazzaQuestionListDetialAdapter mAdapter;

	EditText comment = null; // add comments edit
	EditText bottomcomment = null; // bottom add comments edit
	private ImageView shareImg;

	public String titleContent = ""; // 分享title
	public String ownerIconUrl = ""; // 分享icon url
	public String contentIconUrl = "";
	public String content = "";
	public String layer = "";

	private AlertDialog alertDialog = null;
	private Dialog commentsAlertDialog = null;

	private boolean shouldLoad = true;
	private int index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.essay_listitem_detail);
		initView();

		JSONObject paramsub = new JSONObject();
		try {
			paramsub.put("topicid", topicid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		getCatalogList(HttpUrls.PIZZA_TOPIC_LIST_ITEM_DETAIL, paramsub,
				detailResultListener, detailErrorListener);
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
						handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER);
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER);
					}
				});

		// ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<Object>();
		mAdapter = new PiazzaQuestionListDetialAdapter(this, mListItems);

		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});

	}

	private String topicid = "";

	private void initView() {
		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		findViewById(R.id.right_img).setVisibility(View.GONE);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		topicid = getIntent().getStringExtra("topicid");
		titleView.setText(R.string.question_detail);
		
		bottomcomment = (EditText) findViewById(R.id.comment_text_id);
		bottomcomment.setOnClickListener(this);
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
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;
			case HttpStatus.STATUS_LOAD_OTHER:
				if(shouldLoad){
					JSONObject paramsub = new JSONObject();
					try {
						paramsub.put("topicid", topicid);
						paramsub.put("commentid", "");
						paramsub.put("pindex", "" + ++index);
						paramsub.put("count", 10);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					getDataList(HttpUrls.PIZZA_TOPIC_LIST_ITEM_DETAIL_COMMENT_LIST, paramsub, commentsResultListener, commentsErrorListener, "");
				}
				break;
			case Util.SHOW_DIALOG:
				if (null == alertDialog) {
					alertDialog = Util
							.showLoadDataDialog(PizzaQuestionDeatilActivity.this);
				}
				if (!alertDialog.isShowing()) {
					alertDialog.show();
				}
				break;
			case Util.DISMISS_DIALOG:
				if (null == alertDialog) {
					alertDialog = Util
							.showLoadDataDialog(PizzaQuestionDeatilActivity.this);
				}
				if (alertDialog.isShowing()) {
					alertDialog.cancel();
				}
				break;
			case Util.DISMISS_DIALOG_COMMENTS:
				if (commentsAlertDialog.isShowing()) {
					commentsAlertDialog.cancel();
				}
				if (alertDialog != null && alertDialog.isShowing()) {
					alertDialog.cancel();
				}
				break;
			case Util.SHOW_DIALOG_COMMENTS:
				Intent commentIntent = new Intent(PizzaQuestionDeatilActivity.this, AddCommentActivity.class);
				commentIntent.putExtra("commentFor", "" + topicid);
				commentIntent.putExtra("flag", HttpStatus.COMMENT_FOR_TOPIC);
				startActivityForResult(commentIntent, HttpStatus.REQUEST_CODE_TOPIC);
				/*if (null == commentsAlertDialog) {
					commentsAlertDialog = Util
							.showAddCommentDialog(PizzaQuestionDeatilActivity.this);
				}

				if (!commentsAlertDialog.isShowing()) {
					commentsAlertDialog.show();
				}
				ImageView cancel = (ImageView) commentsAlertDialog
						.findViewById(R.id.add_comments_cancel);
				cancel.setOnClickListener(PizzaQuestionDeatilActivity.this);
				ImageView ok = (ImageView) commentsAlertDialog
						.findViewById(R.id.add_comments_ok);
				ok.setOnClickListener(PizzaQuestionDeatilActivity.this);
				comment = (EditText) commentsAlertDialog
						.findViewById(R.id.add_comments_content);

				handler.sendEmptyMessage(Util.DISMISS_DIALOG_INPUT);*/
				break;
			case Util.DISMISS_DIALOG_INPUT:
				InputMethodManager inputMethodManager = (InputMethodManager) comment
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInput(0,
						InputMethodManager.HIDE_NOT_ALWAYS);
				commentsAlertDialog.setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							commentsAlertDialog.dismiss();
						}
						return false;
					}
				});
				break;
			default:
				break;
			}
		};
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == RESULT_OK){
			handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER);
			shouldLoad = true;
			index = 0;
		}
	};
	
	public void getCatalogList(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject commonsub = Util.getCommonParam(PizzaQuestionDeatilActivity.this);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("common", commonsub);
			jsonObject.put("param", paramsub);
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

	/**
	 * @param jsonObject
	 * @param flag 0 topic 1 comments
	 */
	private void initData(JSONObject jsonObject, int flag) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						JSONObject jb = data.getJSONObject("detail");
						PiazzaQuestionItem pdi = new PiazzaQuestionItem();
						pdi.setId(jb.getString("id"));
						pdi.setUid(jb.getString("uid"));
						pdi.setUgid(jb.getString("ugid"));
						pdi.setNickname(jb.getString("nickname"));
						pdi.setUportrait(jb.getString("uportrait"));
						pdi.setBody(jb.getString("body"));
						pdi.setTags(jb.getString("tags"));
						pdi.setComments(jb.getString("comments"));
						pdi.setTimeline(jb.getString("timeline"));
						if(jb.has("images")){
							JSONArray imagelist = jb.getJSONArray("images");
							for (int k = 0, t = imagelist.length(); k < t; k++) {
								JSONObject imagejb = imagelist.getJSONObject(k);
								WenyiImage homeWorkImage = new WenyiImage();
								homeWorkImage.setId(imagejb.getString("id"));
								if(imagejb.has("imagejb")){
									homeWorkImage.setFollowid(imagejb
											.getString("followid"));
								}
								if(imagejb.has("imageurl")){
									homeWorkImage.setImageurl(imagejb
											.getString("imageurl"));
								}
								pdi.getImages().add(homeWorkImage);
							}
						}
						mListItems.add(pdi);
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER);
					}
				} else {
					Toast.makeText(PizzaQuestionDeatilActivity.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.add_comments_cancel:
			handler.sendEmptyMessage(Util.DISMISS_DIALOG_COMMENTS);
			break;
		case R.id.add_comments_ok:
			// sendCommentsLoadMoreData();
			break;
		case R.id.comment_text_id:
			handler.sendEmptyMessage(Util.SHOW_DIALOG_COMMENTS);
			break;
		case R.id.right_img:
			if (contentIconUrl != null) {
				ownerIconUrl = contentIconUrl;
			}

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("title", titleContent);
			map.put("content", content);
			map.put("layer", layer);
			map.put("image", ownerIconUrl);
			map.put("url", HttpUrls.PIZZA_TOPIC_LIST_ITEM_DETAIL);
			map.put("from", "book");
			openShare(map);
		default:
			break;
		}
	}
	
	public void getDataList(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener, String commentid) {
		JSONObject commonsub = Util.getCommonParam(PizzaQuestionDeatilActivity.this);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("common", commonsub);
			jsonObject.put("param", paramsub);
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

	Listener<JSONObject> commentsResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initCommentsData(result, 0);

		}
	};

	ErrorListener commentsErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};
	
	
	private void initQuextion(JSONObject jsonObject, int flag) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<PiazzaQuestionCommentItem> listTemp = new LinkedList<PiazzaQuestionCommentItem>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								PiazzaQuestionCommentItem elcid = new PiazzaQuestionCommentItem();
								elcid.setId(jb.getString("id"));
								elcid.setTopicid(jb.getString("topicid"));
								elcid.setUid(jb.getString("uid"));
								elcid.setNickname(jb.getString("nickname"));
								elcid.setUportrait(jb.getString("uportrait"));
								elcid.setComment(jb.getString("comment"));
								elcid.setFloor(jb.getString("floor"));
								elcid.setComments(jb.getString("comments"));
								elcid.setTimeline(jb.getString("timeline"));
								
								listTemp.add(elcid);
							}
							mListItems.addAll(listTemp);
							
							handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						}
					}
				} else {
					Toast.makeText(PizzaQuestionDeatilActivity.this, ""
							+ jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void initCommentsData(JSONObject jsonObject, int flag) {
		if(index == 1){
			PiazzaQuestionItem pqi = (PiazzaQuestionItem) mListItems.get(0);
			mListItems.clear();
			mListItems.add(pqi);
		}
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<PiazzaQuestionCommentItem> listTemp = new LinkedList<PiazzaQuestionCommentItem>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								PiazzaQuestionCommentItem elcid = new PiazzaQuestionCommentItem();
								elcid.setId(jb.getString("id"));
								elcid.setTopicid(jb.getString("topicid"));
								elcid.setUid(jb.getString("uid"));
								elcid.setNickname(jb.getString("nickname"));
								elcid.setUportrait(jb.getString("uportrait"));
								elcid.setComment(jb.getString("comment"));
								elcid.setFloor(jb.getString("floor"));
								elcid.setComments(jb.getString("comments"));
								elcid.setTimeline(jb.getString("timeline"));
								
								listTemp.add(elcid);
								if(data.has("comment_items")){
									JSONArray secondCommentlist = data.getJSONArray("comment_items");
									for(int k = 0, t = secondCommentlist.length(); k < t; k++){
										JSONObject commentjb = secondCommentlist.getJSONObject(k);
										PiazzaQuestionCommentItem secPqci = new PiazzaQuestionCommentItem();
										secPqci.setId(commentjb.getString("id"));
										secPqci.setTopicid(commentjb.getString("topicid"));
										secPqci.setUid(commentjb.getString("uid"));
										secPqci.setNickname(commentjb.getString("nickname"));
										secPqci.setUportrait(commentjb.getString("uportrait"));
										secPqci.setComment(commentjb.getString("comment"));
										secPqci.setFloor(commentjb.getString("floor"));
										secPqci.setComments(commentjb.getString("comments"));
										secPqci.setTimeline(commentjb.getString("timeline"));
										secPqci.getComment_items().add(secPqci);
									}
								}
								elcid.setComment(true);
							}

							mListItems.addAll(listTemp);
							index = data.getInt("pindex");
							int core_status = data.getInt("core_status");
							if(core_status == 200 && index == 0){
								Log.e(TAG, "has not some item");
								shouldLoad = false;
							}
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				} else {
					Toast.makeText(PizzaQuestionDeatilActivity.this, ""
							+ jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}
