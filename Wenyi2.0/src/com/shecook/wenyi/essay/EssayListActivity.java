package com.shecook.wenyi.essay;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.letv.shared.widget.LcSearchView;
import com.letv.shared.widget.LcSearchView.OnCancelListener;
import com.letv.shared.widget.LcSearchView.OnClearListener;
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
import com.shecook.wenyi.essay.adapter.EssayListAdapter;
import com.shecook.wenyi.model.EssayListItem;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class EssayListActivity extends BaseActivity implements OnClickListener, TextWatcher, OnEditorActionListener, OnClearListener, OnCancelListener{

	static final String TAG = "EssayListActivity";

	private LinkedList<EssayListItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private EssayListAdapter mAdapter;
	
	private boolean shouldLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_essay_list);
		initView();
		getCatalogList(HttpUrls.ESSAY_WENYI_LIST,null,listResultListener,listErrorListener);
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
						if(shouldLoad){
							getCatalogList(HttpUrls.ESSAY_WENYI_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(EssayListActivity.this, "您已翻到底儿了!",
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
						Log.e(TAG, "onLastItemVisible shouldLoad " + shouldLoad);
						if(shouldLoad){
							getCatalogList(HttpUrls.ESSAY_WENYI_LIST, null, listResultListener, listErrorListener);
						}else{
							Toast.makeText(EssayListActivity.this, "您已翻到底儿了!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

//		ListView actualListView = mPullRefreshListView.getRefreshableView();

		mListItems = new LinkedList<EssayListItem>();
		Log.d("lixufeng111", "onCreate mListItems " + mListItems.toString());
		mAdapter = new EssayListAdapter(this, mListItems);

		/**
		 * Add Sound Event Listener
		 */
		/*SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(
				this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		mPullRefreshListView.setOnPullEventListener(soundListener);*/
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		// You can also just use setListAdapter(mAdapter) or
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Intent intent = new Intent(EssayListActivity.this,EssayItemDeatilActivity.class);
				intent.putExtra("essaytitle", "" + mListItems.get((int)position).getTitle());
				intent.putExtra("catalogtitle", "" + EssayListActivity.this.getIntent().getStringExtra("catalogtitle"));
				intent.putExtra("articleid", "" + mListItems.get((int)position).getId());
				startActivity(intent);
			}
		});
	}

	private void initView(){
		wenyi_common_head_rl = (RelativeLayout) findViewById(R.id.wenyi_common_head_rl);
		wenyi_common_header = (LinearLayout) findViewById(R.id.wenyi_common_header_layout);
		ImageView returnImage = (ImageView) findViewById(R.id.return_img);
		returnImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		ImageView right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setBackgroundResource(R.drawable.search);
		right_img.setOnClickListener(this);
		/*right_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EssayListActivity.this, EssaySearchActivity.class);
				startActivity(intent);
			}
		});*/
		
		TextView titleView = (TextView) findViewById(R.id.middle_title);
		String title = getIntent().getStringExtra("catalogtitle");
		if(TextUtils.isEmpty(title)){
			titleView.setText(R.string.essay);
		}else{
			titleView.setText("" + title);
		}
		
		mAlphaLayout = findViewById(R.id.alphaLayout);
        mAlphaLayout.setOnClickListener(this);
        mSearchResultLayout = (RelativeLayout) findViewById(R.id.event_search_result_container);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new EssaySearchFragment();
        ft.add(R.id.event_search_result_container, f);
        ft.commit();
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};
	
	// ***************************************************************
	public boolean onTouchEvent(android.view.MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			if(mLcSearchView != null){
				mLcSearchView.clearFocus();
			}
		}
		return super.onTouchEvent(event);
	};
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
			if(mLcSearchView != null){
				mLcSearchView.clearFocus();
			}
		}
		return super.dispatchTouchEvent(ev);
	}
    @Override
    public void onBackPressed() {
        if(mLcSearchView != null && mLcSearchView.isShown()) {
        	cancleSearch();
        	return;
        }
        super.onBackPressed();
    }
    private LcSearchView.OnSuggestionListener mLcSuggestionListener = new LcSearchView.OnSuggestionListener() {

        @Override
        public boolean onSuggestionClick(int position) {
            return false;
        }
    };
	public LcSearchView mLcSearchView;
	private InputMethodManager mInputManager;
	private View mAlphaLayout;
    private RelativeLayout mSearchResultLayout;
    RelativeLayout wenyi_common_head_rl;
    LinearLayout wenyi_common_header;
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.right_img:
            mLcSearchView = (LcSearchView) getLayoutInflater().inflate(R.layout.all_in_one_title_bar_search, null);
            mLcSearchView.setQueryRefinementEnabled(true);
            mLcSearchView.setOnTextChangedListener(this);
            mLcSearchView.setOnEditorActionListener(this);
            mLcSearchView.setOnClearListener(this);
            mLcSearchView.setOnCancelListener(this);
            mLcSearchView.setOnSuggestionListener(mLcSuggestionListener);
            mLcSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            mLcSearchView.requestFocus();
            mLcSearchView.setVisibility(View.VISIBLE);
            mInputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            mAlphaLayout.setVisibility(View.VISIBLE);

            wenyi_common_head_rl.setVisibility(View.GONE);
            wenyi_common_header.addView(mLcSearchView);
			break;
		case R.id.alphaLayout:
			if(mLcSearchView != null && mLcSearchView.isShown()){
				cancleSearch();
			}
            break;
		default:
			break;
		}
	}
	private void cancleSearch() {
        mLcSearchView.clearFocus();
        mLcSearchView.setVisibility(View.GONE);
        mSearchResultLayout.setVisibility(View.GONE);
        mAlphaLayout.setVisibility(View.GONE);
        wenyi_common_head_rl.setVisibility(View.VISIBLE);
        mPullRefreshListView.setVisibility(View.VISIBLE);
        if(keyworkChangeListener != null){
        	keyworkChangeListener.keyworkChanged("");
        }
    }
	
	@Override
	public boolean onCancel() {
		cancleSearch();
		return false;
	}

	@Override
	public boolean onClear() {
		if(keyworkChangeListener != null){
        	keyworkChangeListener.keyworkChanged("");
        }
		return false;
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		return false;
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}
    private void setResultLayoutStates (boolean isVisible) {
        if (isVisible) {
            mSearchResultLayout.setVisibility(View.GONE);
            mPullRefreshListView.setVisibility(View.VISIBLE);
            mAlphaLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchResultLayout.setVisibility(View.VISIBLE);
            mPullRefreshListView.setVisibility(View.GONE);
            mAlphaLayout.setVisibility(View.GONE);
        }
    }
	
    
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.e(TAG, "onTextChanged keyword " + s.toString() + ", keyworkChangeListener " + keyworkChangeListener);
        if(s != null && !"".equals(s.toString().trim())) {
            if (mSearchResultLayout.getVisibility() == View.GONE) {
            	setResultLayoutStates(false);
            }
            mLcSearchView.setThreshold(Integer.MAX_VALUE);
            if(keyworkChangeListener != null){
            	keyworkChangeListener.keyworkChanged(s.toString().trim());
            }
        } else {
            if (mSearchResultLayout.getVisibility() == View.VISIBLE) {
            	setResultLayoutStates(true);
            }
            mLcSearchView.setThreshold(Integer.MAX_VALUE);
        }
    }
	
	OnKeywordChangeListener keyworkChangeListener;

	public void setKeyworkChangeListener(
			OnKeywordChangeListener keyworkChangeListener) {
		Log.e(TAG, "setKeyworkChangeListener");
		this.keyworkChangeListener = keyworkChangeListener;
	}
	public interface OnKeywordChangeListener{
		void keyworkChanged(String keywork);
		void keyworkClear();
	}

	// ***************************************************************************
	private int index = 0;
	public void getCatalogList(String url, JSONObject jsonObject, Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if(null == jsonObject){
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(EssayListActivity.this);
		JSONObject paramsub = new JSONObject();
		try {
			jsonObject.put("common", commonsub);
			String catalogid = getIntent().getStringExtra("catalogid");
			if(TextUtils.isEmpty(catalogid)){
				catalogid = "10";
			}
			paramsub.put("catalogid", catalogid);
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
		if(jsonObject != null){
			try {
				if(!jsonObject.isNull("statuscode") && 200 == jsonObject.getInt("statuscode")){
					if(!jsonObject.isNull("data")){
						JSONObject data = jsonObject.getJSONObject("data");
						
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<EssayListItem> listTemp = new LinkedList<EssayListItem>();
							for(int i = 0,j = list.length(); i < j; i++){
								JSONObject jb = list.getJSONObject(i);
								EssayListItem eli = new EssayListItem();
								eli.setId(jb.getString("id"));
								eli.setCataid(jb.getString("cataid"));
								eli.setTitle(jb.getString("title"));
								eli.setSumm(jb.getString("summ"));
								eli.setIconurl(jb.getString("iconurl"));
								eli.setOntop(jb.getBoolean("ontop"));
								eli.setEvent_type(jb.getString("event_type"));
								eli.setEvent_content(jb.getString("event_content"));
								eli.setQkey(jb.getString("qkey"));
								eli.setTimeline(jb.getString("timeline"));
								listTemp.add(eli);
							}
							mListItems.addAll(listTemp);
						}
						
						if(data.has("toplist")){
							JSONArray toplist = data.getJSONArray("toplist");
							LinkedList<EssayListItem> toplistTemp = new LinkedList<EssayListItem>();
							for(int i = 0,j = toplist.length(); i < j; i++){
								JSONObject topjb = toplist.getJSONObject(i);
								WenyiLog.logv(TAG, "initData toplist topjb " + topjb.toString());
								EssayListItem topeli = new EssayListItem();
								topeli.setId(topjb.getString("id"));
								topeli.setCataid(topjb.getString("cataid"));
								topeli.setTitle(topjb.getString("title"));
								topeli.setSumm(topjb.getString("summ"));
								topeli.setIconurl(topjb.getString("iconurl"));
								topeli.setOntop(topjb.getBoolean("ontop"));
								topeli.setEvent_type(topjb.getString("event_type"));
								topeli.setEvent_content(topjb.getString("event_content"));
								topeli.setQkey(topjb.getString("qkey"));
								topeli.setTimeline(topjb.getString("timeline"));
								toplistTemp.add(topeli);
							}
							mListItems.addAll(0, toplistTemp);
						}
						
						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if(core_status == 200 && index == 0){
							Log.e(TAG, "has not some item");
							shouldLoad = false;
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				}else{
					Toast.makeText(EssayListActivity.this, "" + jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
