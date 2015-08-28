package com.shecook.wenyi.cookbook;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

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
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.StartActivity;
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
import com.shecook.wenyi.model.WenyiUser;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CookbookItemDeatilActivity extends BaseActivity implements
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
		setContentView(R.layout.cookbook_listitem_detail);
		initView();

		getCookbookDetail(HttpUrls.COOKBOOK_LIST_ITEM_DETAIL, null,
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
						getCookbookDetail(HttpUrls.COOKBOOK_LIST_ITEM_DETAIL,
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

	private String recipeid = "";

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
		String title = getIntent().getStringExtra("cookbooktitle");
		recipeid = getIntent().getStringExtra("recipeid");
		if (TextUtils.isEmpty(title)) {
			titleView.setText(R.string.cook_book_title);
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
			case 1:
				Log.d("lixufeng111",
						"handleMessage mListItems " + mListItems.toString());
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;
			case Util.SHOW_DIALOG:
				if (null == alertDialog) {
					alertDialog = Util
							.showLoadDataDialog(CookbookItemDeatilActivity.this);
				}
				if (!alertDialog.isShowing()) {
					alertDialog.show();
				}
				break;
			case Util.DISMISS_DIALOG:
				if (null == alertDialog) {
					alertDialog = Util
							.showLoadDataDialog(CookbookItemDeatilActivity.this);
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
							.showAddCommentDialog(CookbookItemDeatilActivity.this);
				}

				if (!commentsAlertDialog.isShowing()) {
					commentsAlertDialog.show();
				}
				ImageView cancel = (ImageView) commentsAlertDialog
						.findViewById(R.id.add_comments_cancel);
				cancel.setOnClickListener(CookbookItemDeatilActivity.this);
				ImageView ok = (ImageView) commentsAlertDialog
						.findViewById(R.id.add_comments_ok);
				ok.setOnClickListener(CookbookItemDeatilActivity.this);
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

	public void getCookbookDetail(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		Log.d("lixufeng", "getCookbookDetail " + user + ",recipeid " + recipeid);
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(CookbookItemDeatilActivity.this);
		JSONObject paramsub = new JSONObject();
		
		try {
			paramsub.put("articleid", recipeid);
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
		WenyiLog.logv(TAG, "initData 1111");
		if (jsonObject != null) {
			try {
				WenyiLog.logv(TAG, "initData 22222");
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					WenyiLog.logv(TAG, "initData 33333");
					if (!jsonObject.isNull("data")) {
						WenyiLog.logv(TAG, "initData 44444");
						JSONObject data = jsonObject.getJSONObject("data");

						JSONArray list = data.getJSONArray("detail");
						LinkedList<EssayListItemDetail> listTemp = new LinkedList<EssayListItemDetail>();
						for (int i = 0, j = list.length(); i < j; i++) {
							JSONObject jb = list.getJSONObject(i);
							WenyiLog.logv(TAG,
									"initData 5555 jb " + jb.toString());
							EssayListItemDetail elid = new EssayListItemDetail();
							elid.setId(jb.getString("id"));
							elid.setCataid(jb.getString("cataid"));
							elid.setArticleid(jb.getString("articleid"));
							elid.setRowtype(jb.getString("rowtype"));
							elid.setRowcontent(jb.getString("rowcontent"));
							listTemp.add(elid);
						}
						mListItems.addAll(listTemp);

						handler.sendEmptyMessage(1);
					}
				} else {
					Toast.makeText(CookbookItemDeatilActivity.this, ""
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
			map.put("url", HttpUrls.COOKBOOK_LIST_ITEM_DETAIL);
			map.put("from", "book");
			openShare(map);
		default:
			break;
		}
	}
}
