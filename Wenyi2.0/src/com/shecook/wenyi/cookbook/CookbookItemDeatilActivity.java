package com.shecook.wenyi.cookbook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
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
import com.shecook.wenyi.cookbook.adapter.CookbookListDetailAdapter;
import com.shecook.wenyi.essay.adapter.ViewPagerAdapter;
import com.shecook.wenyi.essay.view.AutoScrollViewPager;
import com.shecook.wenyi.essay.view.CirclePageIndicator;
import com.shecook.wenyi.model.WenyiGallery;
import com.shecook.wenyi.model.cookbook.CookBookModel;
import com.shecook.wenyi.model.cookbook.CookbookComment;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.view.FixedSpeedScroller;
import com.shecook.wenyi.view.PageIndicator;

public class CookbookItemDeatilActivity extends BaseActivity implements
		OnClickListener {

	private AutoScrollViewPager viewPager;
	private PageIndicator mIndicator;
	private ArrayList<WenyiGallery> mPageViews;
	private ViewPagerAdapter adapter;
	View footer;
	
	private LinkedList<CookBookModel> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CookbookListDetailAdapter mAdapter;

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

		mListItems = new LinkedList<CookBookModel>();
		mAdapter = new CookbookListDetailAdapter(this, mListItems);

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
		
		mPageViews = new ArrayList<WenyiGallery>();
		footer = getLayoutInflater().inflate(R.layout.piazza_discover_viewpager_fragment, mPullRefreshListView, false);
		
		viewPager = (AutoScrollViewPager) footer.findViewById(R.id.view_pager_advert);
		mIndicator = (CirclePageIndicator) footer.findViewById(R.id.indicator);
		
		WenyiGallery eg = new WenyiGallery();
		eg.setId(Integer.parseInt(recipeid));
		eg.setTitle(getResources().getString(R.string.app_name));
		eg.setImgUrl("");
		eg.setEvent_type(11);
		eg.setEvent_content("www.baidu.com");
		mPageViews.add(eg);
		
		adapter = new ViewPagerAdapter(CookbookItemDeatilActivity.this, mPageViews);
		viewPager.setAdapter(adapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setInterval(4000);
		viewPager.setCurrentItem(0);
		viewPager.setStopScrollWhenTouch(true);
		setViewPagerScrollSpeed(viewPager);
		
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        footer.setLayoutParams(layoutParams);
        ListView lv = mPullRefreshListView.getRefreshableView();
        footer.setVisibility(View.GONE);
        lv.addFooterView(footer);

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
		shareImg.setBackgroundResource(R.drawable.share);
		shareImg.setVisibility(View.VISIBLE);
		shareImg.setOnClickListener(this);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		String title = getIntent().getStringExtra("cookbooktitle");
		recipeid = getIntent().getStringExtra("recipeid");
		Log.d(TAG, "getCookbookDetail recipeid is " + recipeid);
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
	public void onResume() {
		super.onResume();
		WenyiLog.logv(TAG, "onResume");
		viewPager.startAutoScroll();
	}

	@Override
	public void onPause() {
		super.onPause();
		WenyiLog.logv(TAG, "onPause");
		viewPager.stopAutoScroll();
	}

	@Override
	protected void onStop() {
		System.gc();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				if(mPageViews.size() > 0){
					footer.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
				}
				break;
			case Util.SHOW_DIALOG_COMMENTS:
				Intent commentIntent = new Intent(CookbookItemDeatilActivity.this, AddCommentActivity.class);
				commentIntent.putExtra("commentFor", "" + recipeid);
				commentIntent.putExtra("flag", HttpStatus.COMMENT_FOR_COOKBOOK);
				startActivityForResult(commentIntent, HttpStatus.REQUEST_CODE_COOKBOOK);
				break;
			default:
				break;
			}
		};
	};

	public void getCookbookDetail(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(CookbookItemDeatilActivity.this);
		JSONObject paramsub = new JSONObject();
		
		try {
			Log.d(TAG, "getCookbookDetail recipeid is " + recipeid);
			paramsub.put("recipeid", recipeid);
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
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");

						JSONObject detail = data.getJSONObject("detail");
						LinkedList<CookBookModel> listTemp = new LinkedList<CookBookModel>();
						
						CookBookModel cbm = new CookBookModel();
						cbm.setId(detail.getString("id"));
						cbm.setRowType("title");
						cbm.setRowContent(detail.getString("recipename"));
						listTemp.add(cbm);
						
						CookBookModel imageCbm = new CookBookModel();
						imageCbm.setId(detail.getString("id"));
						imageCbm.setRowType("imgoriginal");
						imageCbm.setRowContent(detail.getString("imgoriginal"));
						imageCbm.setWidth(detail.getInt("oimg_width"));
						imageCbm.setHeight(detail.getInt("oimg_height"));
						listTemp.add(imageCbm);
						
						JSONArray indredients = detail.has("indredients") ? detail.getJSONArray("indredients") : null;
						if(null != indredients){
							CookBookModel indredientCbm = new CookBookModel();
							indredientCbm.setRowContent("原料：\n");
							indredientCbm.setRowType("ingredients");
							for (int i = 0, j = indredients.length(); i < j; i++) {
								JSONObject jb = indredients.getJSONObject(i);
								indredientCbm.setId(jb.getString("id"));
								indredientCbm.setRecipeid(jb.getString("recipeid"));
								indredientCbm.setRowContent(jb.getString("ingredients"));
								indredientCbm.setRowContent(jb.getString("unit"));
							}
							listTemp.add(indredientCbm);
						}
						
						JSONArray seasonings = detail.has("seasonings") ? detail.getJSONArray("seasonings") : null;
						if(null != indredients){
							CookBookModel seasoningsCbm = new CookBookModel();
							seasoningsCbm.setRowType("seasonings");
							seasoningsCbm.setRowContent("调料：\n");
							for (int i = 0, j = seasonings.length(); i < j; i++) {
								JSONObject seasoningsjb = seasonings.getJSONObject(i);
								seasoningsCbm.setId(seasoningsjb.getString("id"));
								seasoningsCbm.setRecipeid(seasoningsjb.getString("recipeid"));
								seasoningsCbm.setRowContent(seasoningsjb.getString("seasoning"));
								seasoningsCbm.setRowContent(seasoningsjb.getString("unit"));
							}
							listTemp.add(seasoningsCbm);
						}
						
						JSONArray contents = detail.has("contents") ? detail.getJSONArray("contents") : null;
						if(null != indredients){
							CookBookModel contentsCbm1 = new CookBookModel();
							contentsCbm1.setId("");
							contentsCbm1.setRecipeid("");
							contentsCbm1.setRowType("paragraph");
							contentsCbm1.setRowContent("内容：\n");
							listTemp.add(contentsCbm1);
							for (int i = 0, j = contents.length(); i < j; i++) {
								JSONObject contentsjb = contents.getJSONObject(i);
								CookBookModel contentsCbm = new CookBookModel();
								contentsCbm.setId(contentsjb.getString("id"));
								contentsCbm.setRecipeid(contentsjb.getString("recipeid"));
								contentsCbm.setRowType(contentsjb.getString("rowtype"));
								contentsCbm.setRowContent(contentsjb.getString("rowcontent"));
								listTemp.add(contentsCbm);
							}
						}
						
						JSONArray notes = detail.has("notes") ? detail.getJSONArray("notes") : null;
						if(null != notes){
							CookBookModel contentsCbm1 = new CookBookModel();
							contentsCbm1.setId("");
							contentsCbm1.setRecipeid("");
							contentsCbm1.setRowType("paragraph");
							contentsCbm1.setRowContent("超级啰嗦：\n");
							listTemp.add(contentsCbm1);
							for (int i = 0, j = notes.length(); i < j; i++) {
								JSONObject notesjb = notes.getJSONObject(i);
								CookBookModel notesCbm = new CookBookModel();
								notesCbm.setId(notesjb.getString("id"));
								notesCbm.setRecipeid(notesjb.getString("recipeid"));
								notesCbm.setRowType(notesjb.getString("rowtype"));
								notesCbm.setRowContent(notesjb.getString("rowcontent"));
								listTemp.add(notesCbm);
							}
						}
						// ******************************** add homework CookbookHomeworkListItem
						// WenyiGallery
						JSONArray recipefollows = detail.has("recipefollows") ? detail.getJSONArray("recipefollows") : null;
						Log.d(TAG, "recipecomments " + recipefollows);
						if(null != recipefollows){
							mPageViews.clear();
							for(int i = 0,j = recipefollows.length(); i < j; i++){
								
								JSONObject jb = recipefollows.getJSONObject(i);
								
								WenyiGallery eg = new WenyiGallery();
								eg.setId(Integer.parseInt(jb.getString("id")));
								eg.setTitle(jb.getString("description"));
								eg.setImgUrl(jb.getJSONArray("images").getJSONObject(0).getString("imageurl"));
								eg.setEvent_type(HttpStatus.REQUEST_CODE_COOKBOOK);
								eg.setEvent_content(recipeid);
								mPageViews.add(eg);
							}
						}
						// ********************************
						
						// ******************************** add homework comments
						JSONArray recipecomments = detail.has("recipecomments") ? detail.getJSONArray("recipecomments") : null;
						Log.d(TAG, "recipecomments " + recipecomments);
						if(null != recipecomments){
							for (int i = 0, j = recipecomments.length(); i < j; i++) {
								JSONObject recipecommentsjb = recipecomments.getJSONObject(i);
								CookbookComment recipecommentsCbm = new CookbookComment();
								recipecommentsCbm.setId(recipecommentsjb.getString("id"));
								recipecommentsCbm.setRecipeid(recipecommentsjb.getString("recipeid"));
								recipecommentsCbm.setRowType("comments");
								recipecommentsCbm.setUid(recipecommentsjb.getString("uid"));
								recipecommentsCbm.setNickname(recipecommentsjb.getString("nickname"));
								recipecommentsCbm.setUportrait(recipecommentsjb.getString("uportrait"));
								recipecommentsCbm.setComment(recipecommentsjb.getString("comment"));
								recipecommentsCbm.setFloor(recipecommentsjb.getString("floor"));
								recipecommentsCbm.setComments(recipecommentsjb.getString("comments"));
								recipecommentsCbm.setTimeline(recipecommentsjb.getString("timeline"));
								listTemp.add(recipecommentsCbm);
							}
						}
						// ********************************
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
			openShareForCookbook(map,recipeid);
		default:
			break;
		}
	}
	
	private void setViewPagerScrollSpeed(AutoScrollViewPager mViewPager) {
		try {
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(
					mViewPager.getContext());
			mScroller.set(mViewPager, scroller);
		} catch (NoSuchFieldException e) {

		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
