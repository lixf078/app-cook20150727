package com.shecook.wenyi.cookbook;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
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
import com.shecook.wenyi.BaseFragment;
import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.StartActivity;
import com.shecook.wenyi.StartActivity.UpdateFragmentListener;
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
import com.shecook.wenyi.cookbook.adapter.CookbookListAdapter;
import com.shecook.wenyi.model.CookbookCatalog;
import com.shecook.wenyi.model.CookbookListItem;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CookbookFragment extends BaseFragment implements
		UpdateFragmentListener , OnClickListener, TextWatcher, OnEditorActionListener, OnClearListener, OnCancelListener{

	private static final String TAG = "CookbookFragment";

	static final int MENU_SET_MODE = 0;
	public JSONObject cookbookCatalogObject;

	private LinkedList<CookbookCatalog> mCatalogItems;

	private LinkedList<CookbookListItem> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private CookbookListAdapter mAdapter;
	private boolean shouldLoad = true;
	private boolean isFirstLoad = true;
	private StartActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		WenyiLog.logv(TAG, "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		mActivity = (StartActivity) getActivity();
		mActivity.setBaseFragment(this);
		mActivity.setUpdateFragmentListener(this);
		mCatalogItems = new LinkedList<CookbookCatalog>();
		mListItems = new LinkedList<CookbookListItem>();
		if (null != cookbookCatalogObject) {
			initCatalogData(cookbookCatalogObject);
		} else {
			getCatalog(HttpUrls.COOKBOOK_LIST_CATALOG, null,
					catalogResultListener, catalogErrorListener);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WenyiLog.logv(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.copyofcookbook_list_fragment,
				container, false);
		initView(rootView);

		mPullRefreshListView = (PullToRefreshListView) rootView
				.findViewById(R.id.pull_refresh_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(mActivity,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						if (shouldLoad) {
							getCatalogList(HttpUrls.COOKBOOK_LIST, null,
									listResultListener, listErrorListener);
						}else{
							Toast.makeText(mActivity, "您已翻到底儿了!", Toast.LENGTH_SHORT).show();
							handler.sendEmptyMessage(HttpStatus.STATUS_OK);
						}
					}
				});

		// Add an end-of-list listener
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (shouldLoad) {
							getCatalogList(HttpUrls.COOKBOOK_LIST, null,
									listResultListener, listErrorListener);
						} else {
							Toast.makeText(mActivity, "您已翻到底儿了!",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		// ListView actualListView = mPullRefreshListView.getRefreshableView();

		mAdapter = new CookbookListAdapter(mActivity, mListItems);

		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long position) {
				Intent intent = new Intent(mActivity, CookbookItemDeatilActivity.class);
				intent.putExtra("cookbooktitle", "" + mListItems.get((int) position).getRecipename());
				intent.putExtra("recipeid", "" + mListItems.get((int) position).getId());
				intent.putExtra("contentIconUrl", "" + mListItems.get((int) position).getImgthumbnail());
				startActivity(intent);
			}
		});
		return rootView;
	}

	String title = "";
	TextView titleView;
	private void initView(View view) {
		ImageView returnImage = (ImageView) view.findViewById(R.id.return_img);
		returnImage.setBackgroundResource(R.drawable.setting_alt);
		returnImage.setOnClickListener(this);
		
		ImageView settingImage = (ImageView) view.findViewById(R.id.right_img);
		settingImage.setBackgroundResource(R.drawable.search);
		
		settingImage.setOnClickListener(this/*new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity, CookbookSearchActivity.class);
				startActivity(intent);
			}
		}*/);
		
		titleView = (TextView) view.findViewById(R.id.middle_title);
		if (TextUtils.isEmpty(title)) {
			titleView.setText(R.string.caipu);
		} else {
			titleView.setText("" + title);
		}
		
		wenyi_common_head_rl = (RelativeLayout) view.findViewById(R.id.wenyi_common_head_rl);
		wenyi_common_header = (LinearLayout) view.findViewById(R.id.wenyi_common_header_layout);
		
		mAlphaLayout = view.findViewById(R.id.alphaLayout);
        mAlphaLayout.setOnClickListener(this);
        mSearchResultLayout = (RelativeLayout) view.findViewById(R.id.event_search_result_container);
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        CookbookSearchFragment f = new CookbookSearchFragment(this);
        ft.add(R.id.event_search_result_container, f);
        ft.commit();
        mInputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				if (isFirstLoad) {
					isFirstLoad = false;
					// 把全局加载框隐藏
				} else {
					// 隐藏局部加载框
				}
				mAdapter.notifyDataSetChanged();
				// Call onRefreshComplete when the list has been refreshed.
				mPullRefreshListView.onRefreshComplete();
				break;
			case 2:
				if (mCatalogItems != null && mCatalogItems.size() > 0) {
					catalogid = "" + mCatalogItems.get(0).getId();
				}
				getCatalogList(HttpUrls.COOKBOOK_LIST, null,
						listResultListener, listErrorListener);
			default:
				break;
			}
		};
	};

	public void getCatalog(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}

		JSONObject sub = Util.getCommonParam(getActivity());

		try {
			String catalogid = mActivity.getIntent()
					.getStringExtra("catalogid");
			if (TextUtils.isEmpty(catalogid)) {
				catalogid = "10";
			}
			jsonObject.put("common", sub);
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

	public JSONObject getCookbookCatalogObject() {
		return cookbookCatalogObject;
	}

	public void setCookbookCatalogObject(JSONObject cookbookCatalogObject) {
		this.cookbookCatalogObject = cookbookCatalogObject;
	}

	public String getCatalogid() {
		return catalogid;
	}

	public void setCatalogid(String catalogid) {
		this.catalogid = catalogid;
	}

	private int index = 0;
	String catalogid = "";

	public void getCatalogList(String url, JSONObject jsonObject,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		if (null == jsonObject) {
			jsonObject = new JSONObject();
		}
		JSONObject commonsub = Util.getCommonParam(getActivity());
		JSONObject paramsub = new JSONObject();

		try {
			paramsub.put("catalogid", catalogid);
			paramsub.put("pindex", "" + ++index);
			paramsub.put("count", "20");

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

	Listener<JSONObject> catalogResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initCatalogData(result);
		}
	};

	ErrorListener catalogErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	Listener<JSONObject> listResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initData(result, 0);
		}
	};

	ErrorListener listErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	private void initCatalogData(JSONObject jsonObject) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {

					if (!jsonObject.isNull("data")) {

						JSONObject data = jsonObject.getJSONObject("data");
						if(data.has("catalog")){
							JSONArray list = data.getJSONArray("catalog");
							LinkedList<CookbookCatalog> listTemp = new LinkedList<CookbookCatalog>();
							
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								CookbookCatalog cbc = new CookbookCatalog();
								cbc.setId(jb.getString("id"));
								cbc.setCataname(jb.getString("cataname"));
								cbc.setParentid(jb.getString("parentid"));
								
								LinkedList<CookbookCatalog> listTemp2 = null;
								listTemp2 = new LinkedList<CookbookCatalog>();
								if (jb.has("cata_items")) {
									JSONArray sublist = jb
											.getJSONArray("cata_items");
									for (int k = 0, t = sublist.length(); k < t; k++) {
										JSONObject subjb = sublist.getJSONObject(k);
										CookbookCatalog subCbc = new CookbookCatalog();
										subCbc.setId(subjb.getString("id"));
										subCbc.setCataname(subjb
												.getString("cataname"));
										subCbc.setParentid(subjb
												.getString("parentid"));
										listTemp2.add(subCbc);
									}
								}
								cbc.setCata_items(listTemp2);
								listTemp.add(cbc);
							}
							mCatalogItems.addAll(listTemp);
						}

						handler.sendEmptyMessage(2);
					}
				} else {
					Toast.makeText(mActivity,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void initData(JSONObject jsonObject, int flag) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						if(data.has("list")){
							JSONArray list = data.getJSONArray("list");
							LinkedList<CookbookListItem> listTemp = new LinkedList<CookbookListItem>();
							for (int i = 0, j = list.length(); i < j; i++) {
								JSONObject jb = list.getJSONObject(i);
								CookbookListItem eli = new CookbookListItem();
								eli.setId(jb.getString("id"));
								eli.setRecipename(jb.getString("recipename"));
								eli.setSummary(jb.getString("summary"));
								eli.setImgoriginal(jb.getString("imgoriginal"));
								eli.setImgthumbnail(jb.getString("imgthumbnail"));
								eli.setComments(jb.getString("comments"));
								eli.setFollows(jb.getString("follows"));
								eli.setTag(jb.getString("tag"));
								eli.setTimeline(jb.getString("timeline"));
								listTemp.add(eli);
							}
							if(updateCatalog){
								mListItems.clear();
								updateCatalog = false;
							}
							mListItems.addAll(listTemp);
						}

						index = data.getInt("pindex");
						int core_status = data.getInt("core_status");
						if (core_status == 0 && index == 0) {
							shouldLoad = false;
						}
						handler.sendEmptyMessage(HttpStatus.STATUS_OK);
					}
				} else {
					Toast.makeText(mActivity,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean updateCatalog = false;
	
	@Override
	public void updateFragment(String cataId, String cataName) {
		Log.d(TAG, "updateFragment");
		index = 0;
		catalogid = cataId;
		titleView.setText("" + cataName);
		updateCatalog = true;
		getCatalogList(HttpUrls.COOKBOOK_LIST, null, listResultListener, listErrorListener);
	}
	
	// ***************************************************************
	
		/*@Override
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
	    }*/
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
			case R.id.return_img:
				mActivity.toggleMenu();
				break;
			case R.id.right_img:
	            mLcSearchView = (LcSearchView) CookbookFragment.this.getActivity().getLayoutInflater().inflate(R.layout.all_in_one_title_bar_search, null);
	            mLcSearchView.setQueryRefinementEnabled(true);
	            mLcSearchView.setOnTextChangedListener(this);
	            mLcSearchView.setOnEditorActionListener(this);
	            mLcSearchView.setOnClearListener(this);
	            mLcSearchView.setOnCancelListener(this);
	            mLcSearchView.setOnSuggestionListener(mLcSuggestionListener);
	            mLcSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
	            mLcSearchView.requestFocus();
	            mLcSearchView.setVisibility(View.VISIBLE);
	            mLcSearchView.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(View arg0, int arg1, KeyEvent keyevent) {
						Log.e("CookbookFragment", "setOnKeyListener keyevent " + keyevent.getKeyCode());
						return false;
					}
				});
	            mLcSearchView.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View arg0, boolean focus) {
						Log.e("CookbookFragment", "setOnFocusChangeListener focus " + focus);
					}
				});
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
	        	keyworkChangeListener.keyworkClear();
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
				keyworkChangeListener.keyworkClear();
	        }
			return false;
		}

		@Override
		public boolean onEditorAction(TextView arg0, int arg1, KeyEvent event) {
			
			Log.e("CookbookFragment", "onEditorAction getKeyCode " + event.getKeyCode());
			
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
			void keyworkChanged(String keyword);
			void keyworkClear();
		}
		@Override
		public boolean onBackpress() {
			Log.e("CookbookFragment", "onBackPressed");
	        if(mLcSearchView != null && mLcSearchView.isShown()) {
	        	cancleSearch();
	        	return true;
	        }
	        return false;
	    }

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			if (mSearchResultLayout.getVisibility() == View.VISIBLE) {
				Log.e(TAG, "dispatchTouchEvent");
				mInputManager.hideSoftInputFromWindow(mLcSearchView.getWindowToken(), 0);
            }
			return false;
		}
		
		// ***************************************************************************
	
}
