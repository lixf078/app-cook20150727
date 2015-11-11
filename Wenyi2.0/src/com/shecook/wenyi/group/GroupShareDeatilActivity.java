package com.shecook.wenyi.group;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
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
import com.shecook.wenyi.essay.adapter.ViewPagerAdapter;
import com.shecook.wenyi.essay.view.AutoScrollViewPager;
import com.shecook.wenyi.essay.view.CirclePageIndicator;
import com.shecook.wenyi.group.adapter.GroupShareItemDetialAdapter;
import com.shecook.wenyi.model.WenyiGallery;
import com.shecook.wenyi.model.group.GroupShareCommentItem;
import com.shecook.wenyi.model.group.GroupShareModel;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.view.FixedSpeedScroller;
import com.shecook.wenyi.view.PageIndicator;

public class GroupShareDeatilActivity extends BaseActivity implements
		OnClickListener {

	private LinkedList<Object> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private GroupShareItemDetialAdapter mAdapter;

	EditText comment = null; // add comments edit
	EditText bottomcomment = null; // bottom add comments edit

	public String titleContent = ""; // 分享title
	public String ownerIconUrl = ""; // 分享icon url
	public String contentIconUrl = "";
	public String content = "";
	public String layer = "";

	private boolean shouldLoad = true;
	private int index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_share_listitem_detail);
		initView();

		JSONObject paramsub = new JSONObject();
		try {
			paramsub.put("circleid", circleid);
			paramsub.put("shareid", shareid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		getHomeworkInfo(HttpUrls.GROUP_CIRCLE_SHARE_DETAIL, paramsub,
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
		mAdapter = new GroupShareItemDetialAdapter(this, mListItems);

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

	private String circleid = "";
	private String shareid = "";

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
		circleid = getIntent().getStringExtra("circleid");
		shareid = getIntent().getStringExtra("shareid");
		titleView.setText("");
		
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
			/*if (contentIconUrl != null) {
				ownerIconUrl = contentIconUrl;
			}

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("title", titleContent);
			map.put("content", content);
			map.put("layer", layer);
			map.put("image", ownerIconUrl);
			map.put("url", HttpUrls.GROUP_CIRCLE_SHARE_DETAIL);
			map.put("from", "book");
			openShare(map);*/
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
			case HttpStatus.STATUS_LOAD_OTHER_OK:
				mAdapter.notifyDataSetChanged();
				mPullRefreshListView.onRefreshComplete();
				break;
			case HttpStatus.STATUS_LOAD_OTHER:
				if(shouldLoad){
					JSONObject paramsub = new JSONObject();
					try {
						paramsub.put("shareid", shareid);
						paramsub.put("pindex", "" + ++index);
						paramsub.put("count", 10);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					getDataList(HttpUrls.GROUP_CIRCLE_SHARE_COMMON_LIST, paramsub, commentsResultListener, commentsErrorListener, "");
				}
				break;
			case Util.SHOW_DIALOG_COMMENTS:
				Intent commentIntent = new Intent(GroupShareDeatilActivity.this, AddCommentActivity.class);
				commentIntent.putExtra("commentFor", "" + shareid);
				commentIntent.putExtra("flag", HttpStatus.COMMENT_FOR_CIRCLE);
				startActivityForResult(commentIntent, HttpStatus.REQUEST_CODE_CIRCLE);
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
	
	private AutoScrollViewPager viewPager;
	private PageIndicator mIndicator;
	private ArrayList<WenyiGallery> mPageViews;
	private ViewPagerAdapter adapter;
	
	private void inifViewPaper(){
		ListView lv = mPullRefreshListView.getRefreshableView();
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
		
        View header = GroupShareDeatilActivity.this.getLayoutInflater().inflate(R.layout.piazza_discover_viewpager_fragment, mPullRefreshListView, false);
		
		viewPager = (AutoScrollViewPager) header
				.findViewById(R.id.view_pager_advert);
		mIndicator = (CirclePageIndicator) header
				.findViewById(R.id.indicator);
		mPageViews.addAll(chm.getImages());
		
		adapter = new ViewPagerAdapter(GroupShareDeatilActivity.this, mPageViews);
		viewPager.setAdapter(adapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setInterval(4000);
		viewPager.setCurrentItem(0);
		viewPager.setStopScrollWhenTouch(true);
		setViewPagerScrollSpeed(viewPager);
		
        header.setLayoutParams(layoutParams);
        lv.addHeaderView(header);
        if(viewPager != null){
			viewPager.startAutoScroll();
		}
		
		
        View userheader = GroupShareDeatilActivity.this.getLayoutInflater().inflate(R.layout.group_share_item_detail_userinfo, mPullRefreshListView, false);
		TextView advTitle = (TextView) userheader.findViewById(R.id.group_share_user_item_title);
		TextView advTime = (TextView) userheader.findViewById(R.id.group_share_user_item_time);
		TextView group_share_item_level = (TextView) userheader.findViewById(R.id.group_share_user_item_level);
		NetworkImageView imageUrl = (NetworkImageView) userheader.findViewById(R.id.item_img);
        ImageLoader imageLoader;
		try {
			advTitle.setText("" + chm.getNickname());
			advTime.setText(Util.formatTime2Away(chm.getTimeline()));
			group_share_item_level.setText(chm.getBody());
			LruImageCache lruImageCache = LruImageCache.instance();
			imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			imageUrl.setDefaultImageResId(R.drawable.icon);
			imageUrl.setErrorImageResId(R.drawable.icon);
			imageUrl.setImageUrl(chm.getUportrait(), imageLoader);
		} catch (Exception e) {
			e.printStackTrace();
		}
        lv.addHeaderView(userheader);
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
		JSONObject commonsub = Util.getCommonParam(GroupShareDeatilActivity.this);
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

	private GroupShareModel chm = null;
	
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
						chm = new GroupShareModel();
						chm.setLvlname(data.getString("lvlname"));
						JSONObject detail = data.getJSONObject("detail");
						
						chm.setId(detail.getString("id"));
						chm.setUid(detail.getString("uid"));
						chm.setNickname(detail.getString("nickname"));
						chm.setUportrait(detail.getString("uportrait"));
						chm.setBody(detail.getString("body"));
						chm.setComments(detail.getString("comments"));
						chm.setTimeline(detail.getString("timeline"));
						
						if(detail.has("images")){
							JSONArray imagelist = detail.getJSONArray("images");
							for (int k = 0, t = imagelist.length(); k < t; k++) {
								JSONObject imagejb = imagelist.getJSONObject(k);
								WenyiGallery shareImage = new WenyiGallery();
								shareImage.setId(imagejb.getInt("id"));
								if(imagejb.has("shareid")){
									shareImage.setTitle(imagejb.getString("shareid"));
								}
								if(imagejb.has("imageurl")){
									shareImage.setImgUrl(imagejb.getString("imageurl"));
								}
								chm.getImages().add(shareImage);
							}
						}
					}
					handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER);
				} else {
					Toast.makeText(GroupShareDeatilActivity.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void getDataList(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener, String commentid) {
		JSONObject commonsub = Util.getCommonParam(GroupShareDeatilActivity.this);
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
		
		if(index == 1){
			mListItems.clear();
		}
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<GroupShareCommentItem> listTemp = new LinkedList<GroupShareCommentItem>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								GroupShareCommentItem elcid = new GroupShareCommentItem();
								elcid.setId(jb.getString("id"));
								elcid.setShareid(jb.getString("shareid"));
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
										GroupShareCommentItem secPqci = new GroupShareCommentItem();
										secPqci.setId(commentjb.getString("id"));
										secPqci.setShareid(commentjb.getString("shareid"));
										secPqci.setCommentid(commentjb.getString("commentid"));
										secPqci.setUid(commentjb.getString("uid"));
										secPqci.setNickname(commentjb.getString("nickname"));
										secPqci.setUportrait(commentjb.getString("uportrait"));
										secPqci.setComment(commentjb.getString("comment"));
										secPqci.setTimeline(commentjb.getString("timeline"));
										secPqci.setComment(true);
										listTemp.add(secPqci);
									}
								}
							}

							mListItems.addAll(listTemp);
							index = data.getInt("pindex");
							int core_status = data.getInt("core_status");
							if(core_status == 200 && index == 0){
								Log.e(TAG, "has not some item");
								shouldLoad = false;
							}
						}
					}
				} else {
					Toast.makeText(GroupShareDeatilActivity.this, ""
							+ jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(HttpStatus.STATUS_LOAD_OTHER_OK);
		}
	}
	
}
