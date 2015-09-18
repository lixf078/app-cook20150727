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
import com.shecook.wenyi.essay.adapter.EssayListDetailAdapter;
import com.shecook.wenyi.model.EssayListItemDetail;
import com.shecook.wenyi.model.essay.EssayListCommentsItemDetail;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PizzaDiscovertemDeatilActivity extends BaseActivity implements
		OnClickListener {

	private LinkedList<EssayListItemDetail> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private EssayListDetailAdapter mAdapter;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.essay_listitem_detail);
		initView();

		getCatalogList(HttpUrls.ESSAY_WENYILIST_ITEM_DETAIL, null,
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

						// Do work to refresh the list here.
						getCatalogList(HttpUrls.ESSAY_WENYILIST_ITEM_DETAIL,
								null, detailResultListener, detailErrorListener);
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

		mListItems = new LinkedList<EssayListItemDetail>();
		mAdapter = new EssayListDetailAdapter(this, mListItems);

		mPullRefreshListView.setMode(Mode.DISABLED);
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d(TAG, "onItemClick arg2 " + arg2 + ",arg3 " + arg3);

			}
		});

	}

	private String articleid = "";

	private void initView() {
		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		shareImg = (ImageView) findViewById(R.id.right_img);
		shareImg.setBackgroundResource(R.drawable.f55_btn);
		shareImg.setVisibility(View.VISIBLE);
		shareImg.setOnClickListener(this);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		String title = getIntent().getStringExtra("catalogtitle");
		articleid = getIntent().getStringExtra("articleid");
		if (TextUtils.isEmpty(title)) {
			titleView.setText(R.string.essay);
		} else {
			titleView.setText("" + title);
		}
		
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
				JSONObject paramsub = new JSONObject();
				try {
//					paramsub.put("articleid", articleid);
					paramsub.put("articleid", "2071");
					paramsub.put("commentid", "");
					paramsub.put("pindex", "1");
					paramsub.put("count", 3);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				getDataList(HttpUrls.ESSAY_WENYILIST_ITEM_DETAIL_COMMENT, paramsub, commentsResultListener, commentsErrorListener, "");
				break;
			case Util.SHOW_DIALOG:
				if (null == alertDialog) {
					alertDialog = Util
							.showLoadDataDialog(PizzaDiscovertemDeatilActivity.this);
				}
				if (!alertDialog.isShowing()) {
					alertDialog.show();
				}
				break;
			case Util.DISMISS_DIALOG:
				if (null == alertDialog) {
					alertDialog = Util
							.showLoadDataDialog(PizzaDiscovertemDeatilActivity.this);
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
				if (null == commentsAlertDialog) {
					commentsAlertDialog = Util
							.showAddCommentDialog(PizzaDiscovertemDeatilActivity.this);
				}

				if (!commentsAlertDialog.isShowing()) {
					commentsAlertDialog.show();
				}
				ImageView cancel = (ImageView) commentsAlertDialog
						.findViewById(R.id.add_comments_cancel);
				cancel.setOnClickListener(PizzaDiscovertemDeatilActivity.this);
				ImageView ok = (ImageView) commentsAlertDialog
						.findViewById(R.id.add_comments_ok);
				ok.setOnClickListener(PizzaDiscovertemDeatilActivity.this);
				comment = (EditText) commentsAlertDialog
						.findViewById(R.id.add_comments_content);

				handler.sendEmptyMessage(Util.DISMISS_DIALOG_INPUT);
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

	public void getCatalogList(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		Log.d("lixufeng", "getCatalogList " + user + ",articleid " + articleid);
		JSONObject commonsub = Util.getCommonParam(PizzaDiscovertemDeatilActivity.this);
		JSONObject paramsub = new JSONObject();
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		try {
			paramsub.put("articleid", articleid);
			
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

	private void initData(JSONObject jsonObject, int flag) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");

						JSONArray list = data.getJSONArray("detail");
						LinkedList<EssayListItemDetail> listTemp = new LinkedList<EssayListItemDetail>();
						for (int i = 0, j = list.length(); i < j; i++) {
							JSONObject jb = list.getJSONObject(i);
							EssayListItemDetail elid = new EssayListItemDetail();
							elid.setId(jb.getString("id"));
							elid.setCataid(jb.getString("cataid"));
							elid.setArticleid(jb.getString("articleid"));
							elid.setRowtype(jb.getString("rowtype"));
							elid.setRowcontent(jb.getString("rowcontent"));
							elid.setWidth(jb.getInt("img_width"));
							elid.setHeight(jb.getInt("img_height"));
							listTemp.add(elid);
						}
						mListItems.addAll(listTemp);
						EssayListItemDetail essayTitleElid = new EssayListItemDetail();
						essayTitleElid.setId("");
						essayTitleElid.setCataid("");
						essayTitleElid.setArticleid("");
						essayTitleElid.setRowtype("essayTitleElid");
						String title = PizzaDiscovertemDeatilActivity.this.getIntent().getStringExtra("essaytitle");
						essayTitleElid.setRowcontent(title);
						mListItems.add(0, essayTitleElid);
						
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER);
					}
				} else {
					Toast.makeText(PizzaDiscovertemDeatilActivity.this, ""
							+ jsonObject.getString("errmsg"),
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
			map.put("url", HttpUrls.ESSAY_WENYILIST_ITEM_DETAIL);
			map.put("from", "book");
			openShare(map);
		default:
			break;
		}
	}
	
	public void getDataList(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener, String commentid) {
		Log.d("lixufeng", "getDataList " + user + ",articleid " + articleid);
		JSONObject commonsub = Util.getCommonParam(PizzaDiscovertemDeatilActivity.this);
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

	private void initCommentsData(JSONObject jsonObject, int flag) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<EssayListCommentsItemDetail> listTemp = new LinkedList<EssayListCommentsItemDetail>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								EssayListCommentsItemDetail elcid = new EssayListCommentsItemDetail();
								elcid.setId(jb.getString("id"));
								elcid.setArticleid(jb.getString("articleid"));
								elcid.setUid(jb.getString("uid"));
								elcid.setNickname(jb.getString("nickname"));
								elcid.setUportrait(jb.getString("uportrait"));
								elcid.setComment(jb.getString("comment"));
								elcid.setFloor(jb.getString("floor"));
								elcid.setComments(jb.getString("comments"));
								elcid.setTimeline(jb.getString("timeline"));
								elcid.setRowtype("commentOne");
								
								listTemp.add(elcid);
								if(data.has("comment_items")){
									JSONArray secondCommentlist = data.getJSONArray("comment_items");
									for(int k = 0, t = secondCommentlist.length(); k < t; k++){
										JSONObject commentjb = secondCommentlist.getJSONObject(k);
										EssayListCommentsItemDetail secondComment = new EssayListCommentsItemDetail();
										secondComment.setId(commentjb.getString("id"));
										secondComment.setArticleid(commentjb.getString("articleid"));
										secondComment.setCommentid(commentjb.getString("commentid"));
										secondComment.setUid(commentjb.getString("uid"));
										secondComment.setNickname(commentjb.getString("nickname"));
										secondComment.setUportrait(commentjb.getString("uportrait"));
										secondComment.setComment(commentjb.getString("comment"));
										secondComment.setTimeline(commentjb.getString("timeline"));
										secondComment.setRowtype("commentTwo");
										elcid.getComment_items().add(secondComment);
									}
								}else{
									for(int k = 0, t = 2; k < t; k++){
										EssayListCommentsItemDetail secondComment = new EssayListCommentsItemDetail();
										secondComment.setId("");
										secondComment.setArticleid("");
										secondComment.setCommentid("");
										secondComment.setUid("");
										secondComment.setNickname("");
										secondComment.setUportrait("");
										secondComment.setComment("用户A 评论 用户B ：你是个大傻。。。");
										secondComment.setTimeline("");
										secondComment.setRowtype("commentTwo");
										// elcid.getComment_items().add(secondComment);
										listTemp.add(secondComment);
									}
								}
							}
							mListItems.addAll(listTemp);
							
							handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						}
					}
				} else {
					Toast.makeText(PizzaDiscovertemDeatilActivity.this, ""
							+ jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}
