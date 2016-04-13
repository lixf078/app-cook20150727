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
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.cookbook.adapter.CookbookHomewrokListItemDetialAdapter;
import com.shecook.wenyi.essay.adapter.ViewPagerAdapter;
import com.shecook.wenyi.essay.view.AutoScrollViewPager;
import com.shecook.wenyi.essay.view.CirclePageIndicator;
import com.shecook.wenyi.model.WenyiGallery;
import com.shecook.wenyi.model.cookbook.CookbookHomeworkModel;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.view.FixedSpeedScroller;
import com.shecook.wenyi.view.PageIndicator;

public class CookbookHomeworkDeatilActivity extends BaseActivity implements
		OnClickListener {

	private LinkedList<Object> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CookbookHomewrokListItemDetialAdapter mAdapter;

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

	NetworkImageView head_image;
	private TextView pizza_cookbook_item_header_title, pizza_cookbook_item_header_time, pizza_cookbook_item_header_level, pizza_cookbook_list_header_content, cookbook_homework_from;
	
	private boolean shouldLoad = true;
	private int index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cookbook_homework_listitem_detail);
		initView();

		JSONObject paramsub = new JSONObject();
		try {
			paramsub.put("followid", followid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		getHomeworkInfo(HttpUrls.COOKBOOK_HOMEWORK_LIST_ITEM_DETAIL, paramsub,
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
//					    handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER); // 暂时没有添加评论接口
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
//						handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER); // 暂时没有添加评论接口
					}
				});

		// ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<Object>();
		mAdapter = new CookbookHomewrokListItemDetialAdapter(this, mListItems);

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

	private String followid = "";

	private void initView() {
		mPageViews = new ArrayList<WenyiGallery>();
		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		findViewById(R.id.right_img).setVisibility(View.GONE);
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		followid = getIntent().getStringExtra("followid");
		titleView.setText(R.string.cookbook_homework_detail);
		
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
		if(viewPager != null){
			viewPager.startAutoScroll();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(viewPager != null){
			viewPager.stopAutoScroll();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
			map.put("url", HttpUrls.COOKBOOK_HOMEWORK_LIST_ITEM_DETAIL);
			map.put("from", "book");
			openShare(map);
		default:
			break;
		}
	}
	
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				inifViewPaper();
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;
			case HttpStatus.STATUS_LOAD_OTHER:
				if(shouldLoad){
					JSONObject paramsub = new JSONObject();
					try {
						paramsub.put("followid", followid);
						paramsub.put("commentid", "");
						paramsub.put("pindex", "" + ++index);
						paramsub.put("count", 10);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					getDataList(HttpUrls.COOKBOOK_HOMEWORK_LIST_ITEM_DETAIL, paramsub, commentsResultListener, commentsErrorListener, "");
				}
				break;
			case Util.SHOW_DIALOG_COMMENTS:
				Intent commentIntent = new Intent(CookbookHomeworkDeatilActivity.this, AddCommentActivity.class);
				commentIntent.putExtra("commentFor", "" + followid);
				commentIntent.putExtra("flag", HttpStatus.COMMENT_FOR_TOPIC);
				startActivityForResult(commentIntent, HttpStatus.REQUEST_CODE_TOPIC);
				break;
			default:
				break;
			}
		};
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == RESULT_OK){
//			handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER); // 暂时没有添加评论接口
			shouldLoad = true;
			index = 0;
		}
	};
	
	private AutoScrollViewPager viewPager;
	private PageIndicator mIndicator;
	private ArrayList<WenyiGallery> mPageViews;
	private ViewPagerAdapter adapter;
	
	private void inifViewPaper(){
        
//		View header = CookbookHomeworkDeatilActivity.this.getLayoutInflater().inflate(R.layout.piazza_discover_viewpager_fragment, mPullRefreshListView, false);
//		
//		viewPager = (AutoScrollViewPager) header
//				.findViewById(R.id.view_pager_advert);
//		mIndicator = (CirclePageIndicator) header
//				.findViewById(R.id.indicator);
////		CookbookHomeworkModel chm = (CookbookHomeworkModel) mListItems.get(0);
//		mPageViews.addAll(chminfo.getImages());
//		
//		adapter = new ViewPagerAdapter(CookbookHomeworkDeatilActivity.this, mPageViews);
//		viewPager.setAdapter(adapter);
//		mIndicator.setViewPager(viewPager);
//
//		viewPager.setInterval(4000);
//		viewPager.setCurrentItem(0);
//		viewPager.setStopScrollWhenTouch(true);
//		setViewPagerScrollSpeed(viewPager);
//		
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//        header.setLayoutParams(layoutParams);
        ListView lv = mPullRefreshListView.getRefreshableView();
//        lv.addHeaderView(header);
//        if(viewPager != null){
//			viewPager.startAutoScroll();
//		}
        
        
        View infoHeader = getLayoutInflater().inflate(R.layout.piazza_cookbook_item_detail_header, mPullRefreshListView, false);
		pizza_cookbook_item_header_title = (TextView) infoHeader.findViewById(R.id.pizza_question_item_header_title);
		pizza_cookbook_item_header_time = (TextView) infoHeader.findViewById(R.id.pizza_question_item_header_time);
		pizza_cookbook_item_header_level = (TextView) infoHeader.findViewById(R.id.pizza_question_item_header_level);
		pizza_cookbook_list_header_content = (TextView) infoHeader.findViewById(R.id.pizza_question_list_header_content);
		cookbook_homework_from = (TextView) infoHeader.findViewById(R.id.cookbook_homework_header_from);
		head_image = (NetworkImageView) infoHeader.findViewById(R.id.item_img);
		
		ImageLoader imageLoader;
		try {
			LruImageCache lruImageCache = LruImageCache.instance();
			imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			head_image.setImageUrl(chminfo.getUportrait(), imageLoader);
			pizza_cookbook_item_header_title.setText(chminfo.getNickname());
			pizza_cookbook_item_header_time.setText(Util.formatTime2Away(chminfo.getTimeline()));
			pizza_cookbook_item_header_level.setText(chminfo.getUserlvl());
			if(chminfo.getDescription() == null || chminfo.getDescription().equals("")){
				pizza_cookbook_list_header_content.setVisibility(View.GONE);
			}
			if(!TextUtils.isEmpty(chminfo.getRecipeid())&& !"0".equals(chminfo.getRecipeid())){
//				cookbook_homework_from.setVisibility(View.VISIBLE);
				cookbook_homework_from.setText("来自菜谱" + chminfo.getRecipename());
//				cookbook_homework_from.setOnClickListener(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						
//					}
//				});
			}
			pizza_cookbook_list_header_content.setText("" + chminfo.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
		infoHeader.setLayoutParams(layoutParams);
//		ListView lv = mPullRefreshListView.getRefreshableView();
		lv.addHeaderView(infoHeader);
        
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
	
	public void getHomeworkInfo(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject commonsub = Util.getCommonParam(CookbookHomeworkDeatilActivity.this);
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
	CookbookHomeworkModel chminfo;
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
						JSONObject detail = data.getJSONObject("detail");
						chminfo = new CookbookHomeworkModel();
						
						chminfo.setUserlvl(detail.getString("userlvl"));
						chminfo.setRecipename(detail.getString("recipename"));
						chminfo.setId(detail.getString("id"));
						chminfo.setRecipeid(detail.getString("recipeid"));
						chminfo.setUid(detail.getString("uid"));
						chminfo.setNickname(detail.getString("nickname"));
						chminfo.setUportrait(detail.getString("uportrait"));
						chminfo.setDescription(detail.getString("description"));
						chminfo.setComments(detail.getString("comments"));
						chminfo.setTimeline(detail.getString("timeline"));
						
						if(detail.has("images")){
							JSONArray imagelist = detail.getJSONArray("images");
							for (int k = 0, t = imagelist.length(); k < t; k++) {
								JSONObject imagejb = imagelist.getJSONObject(k);
								CookbookHomeworkModel homeworkImg = new CookbookHomeworkModel();
								homeworkImg.imgUrl = imagejb.getString("imageurl");
								homeworkImg.type = 1;
								homeworkImg.width = imagejb.getInt("width");
								homeworkImg.height = imagejb.getInt("height");
								homeworkImg.setComment(false);
								
								WenyiGallery homeWorkImage = new WenyiGallery();
								homeWorkImage.setId(imagejb.getInt("id"));
								if(imagejb.has("followid")){
									// homeWorkImage.setTitle(imagejb.getString("followid"));
									 homeWorkImage.setTitle("");
								}
								if(imagejb.has("imageurl")){
									homeWorkImage.setImgUrl(imagejb.getString("imageurl"));
								}
								chminfo.getImages().add(homeWorkImage);
								
								mListItems.add(homeworkImg);
							}
						}
						
						
						if(!TextUtils.isEmpty(chminfo.getRecipeid())&& !"0".equals(chminfo.getRecipeid())){
							CookbookHomeworkModel homeworkRecipied = new CookbookHomeworkModel();
							homeworkRecipied.setRecipeid(chminfo.getRecipeid());
							homeworkRecipied.setRecipename(chminfo.getRecipename());
							homeworkRecipied.setComment(false);
							homeworkRecipied.type = 2;
							mListItems.add(homeworkRecipied);
						}
						
					}
					
//						handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER); // 暂时没有添加评论接口
				} else {
					Toast.makeText(CookbookHomeworkDeatilActivity.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendEmptyMessage(HttpStatus.STATUS_OK);
	}

	public void getDataList(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener, String commentid) {
		JSONObject commonsub = Util.getCommonParam(CookbookHomeworkDeatilActivity.this);
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
		// 暂时没有添加评论接口
		/*
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
								elcid.setfollowid(jb.getString("followid"));
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
										secPqci.setfollowid(commentjb.getString("followid"));
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
					Toast.makeText(CookbookHomeworkDeatilActivity.this, ""
							+ jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	*/}
	
}
